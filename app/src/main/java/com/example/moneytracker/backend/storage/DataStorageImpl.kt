// Glory be the name of LORD our GOD
package com.example.moneytracker.backend.storage

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.moneytracker.helper.achievementToMap
import com.example.moneytracker.helper.asAchievement
import com.example.moneytracker.helper.asSettlement
import com.example.moneytracker.helper.asWithdrawal
import com.example.moneytracker.helper.settlementToMap
import com.example.moneytracker.helper.toEpochMilli
import com.example.moneytracker.helper.toFinance
import com.example.moneytracker.helper.toMap
import com.example.moneytracker.helper.withdrawalToMap
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

class DataStorageImpl(
    override val db: FirebaseFirestore
) : DataStorage {

    private fun getCollectionName(financeEntity: FinanceEntity): String {
        return when (financeEntity) {
            is FinanceEntity.Transaction -> TRANSACTION_COLLECTION
            is FinanceEntity.Goal -> GOAL_COLLECTION
            is FinanceEntity.Liability -> LIABILITY_COLLECTION
        }
    }

    private fun getCollectionNameFromType(type: String): String {
        return when (type.uppercase()) {
            "TRANSACTION" -> TRANSACTION_COLLECTION
            "GOAL" -> GOAL_COLLECTION
            "LIABILITY" -> LIABILITY_COLLECTION
            else -> TRANSACTION_COLLECTION
        }
    }

    override suspend fun filterDatasets(
        userId: String,
        filter: Filter,
        orderBy: String?,
        orderDirection: Query.Direction?
    ): List<FinanceEntity> {
        return try {
            val collections = listOf(TRANSACTION_COLLECTION, GOAL_COLLECTION, LIABILITY_COLLECTION)
            val results = mutableListOf<FinanceEntity>()

            for (collection in collections) {
                var query: Query = db.collection(COLLECTION_NAME)
                    .document(userId)
                    .collection(collection)
                    .where(filter)

                if (orderBy != null && orderDirection != null) {
                    query = query.orderBy(orderBy, orderDirection)
                }

                val snapshot = query.get().await()

                for (doc in snapshot.documents) {
                    try {
                        val entity = doc.data?.toFinance() ?: continue
                        val settlements = doc.reference.collection("settlement").get().await()
                            .documents.mapNotNull { it.data?.asSettlement() }
                        val withdrawals = doc.reference.collection("withdrawal").get().await()
                            .documents.mapNotNull { it.data?.asWithdrawal() }
                        val achievements = doc.reference.collection("achievement").get().await()
                            .documents.mapNotNull { it.data?.asAchievement() }

                        val updatedEntity = when (entity) {
                            is FinanceEntity.Goal -> entity.copy(
                                settlement = settlements,
                                achievement = achievements
                            )

                            is FinanceEntity.Liability -> entity.copy(settlement = settlements)
                            is FinanceEntity.Transaction -> entity.copy(withdrawal = withdrawals)
                        }
                        results.add(updatedEntity)
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        Log.e(
                            "DataStorageImpl",
                            "Failed to parse item during filtering in $collection",
                            e
                        )
                    }
                }
            }
            results
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Error filtering datasets", e)
            emptyList()
        }
    }

    override suspend fun getWholeDatasets(
        userId: String,
        onSuccess: (isSuccess: Boolean) -> Unit,
        onFailure: (error: Throwable?) -> Unit
    ): Flow<List<FinanceEntity>> = callbackFlow {
        val collections = listOf(TRANSACTION_COLLECTION, GOAL_COLLECTION, LIABILITY_COLLECTION)
        val listeners = mutableListOf<com.google.firebase.firestore.ListenerRegistration>()
        val latestData = mutableMapOf<String, List<FinanceEntity>>()
        var latestSettlements = emptyList<Settlement>()
        var latestWithdrawal = emptyList<Withdrawal>()
        var latestAchievements = emptyList<Achievement>()

        // Initialize with empty lists to ensure something is emitted early if requested
        collections.forEach { latestData[it] = emptyList() }

        fun emitCombined() {
            val combined = latestData.values.flatten().map { entity ->
                when (entity) {
                    is FinanceEntity.Goal -> entity.copy(
                        settlement = latestSettlements.filter { it.datasetId == entity.id },
                        achievement = latestAchievements.filter { it.datasetId == entity.id }
                    )

                    is FinanceEntity.Liability -> entity.copy(settlement = latestSettlements.filter { it.datasetId == entity.id })
                    is FinanceEntity.Transaction -> entity.copy(withdrawal = latestWithdrawal.filter { it.datasetId == entity.id })
                }
            }
            Log.d("DataStorageImpl", "parsed total count: ${combined.size}")
            trySend(combined)
            onSuccess(true)
        }

        collections.forEach { collectionName ->
            val ref = db.collection(COLLECTION_NAME)
                .document(userId)
                .collection(collectionName)

            val listener = ref.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(
                        "DataStorageImpl",
                        "getWholeDatasets listener error in $collectionName",
                        error
                    )
                    if (error.code != FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                        onFailure(error)
                    }
                    return@addSnapshotListener
                }

                val data = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.data?.toFinance()
                    } catch (e: Exception) {
                        Log.e("DataStorageImpl", "Failed to parse item in $collectionName", e)
                        null
                    }
                } ?: emptyList()

                latestData[collectionName] = data
                emitCombined()
            }
            listeners.add(listener)
        }

        // Listen to settlements via collection group
        val settlementListener = db.collectionGroup("settlement")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("DataStorageImpl", "settlement collectionGroup listener error", error)
                    return@addSnapshotListener
                }

                val settlements = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.data?.asSettlement()
                    } catch (e: Exception) {
                        Log.e("DataStorageImpl", "Failed to parse settlement", e)
                        null
                    }
                } ?: emptyList()

                latestSettlements = settlements
                emitCombined()
            }
        listeners.add(settlementListener)

        // Listen to settlements via collection group
        val withdrawalListener = db.collectionGroup("withdrawal")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("DataStorageImpl", "withdrawal collectionGroup listener error", error)
                    return@addSnapshotListener
                }

                val withdrawal = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.data?.asWithdrawal()
                    } catch (e: Exception) {
                        Log.e("DataStorageImpl", "Failed to parse settlement", e)
                        null
                    }
                } ?: emptyList()

                latestWithdrawal = withdrawal
                emitCombined()
            }
        listeners.add(withdrawalListener)

        // Listen to achievements via collection group
        val achievementListener = db.collectionGroup("achievement")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("DataStorageImpl", "achievement collectionGroup listener error", error)
                    return@addSnapshotListener
                }

                val achievements = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.data?.asAchievement()
                    } catch (e: Exception) {
                        Log.e("DataStorageImpl", "Failed to parse achievement", e)
                        null
                    }
                } ?: emptyList()

                latestAchievements = achievements
                emitCombined()
            }
        listeners.add(achievementListener)

        awaitClose {
            listeners.forEach { it.remove() }
        }
    }

    override suspend fun getInfo(userId: String): Flow<Info> = callbackFlow {
        val documentRef = db.collection(COLLECTION_NAME)
            .document(userId)

        val listenerRegistration = documentRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("DataStorageImpl", "getInfo listener error", error)
                if (error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    // During logout, this error is expected. Close gracefully.
                    close()
                } else {
                    close(error)
                }
                return@addSnapshotListener
            }

            try {
                val infoField = snapshot?.get("info")
                Log.d("DataStorageImpl", "raw info field: $infoField")
                val color = when (infoField) {
                    is Map<*, *> -> {
                        // Firestore may store numbers as Long - use safe cast
                        (infoField["color"] as? Number)?.toInt() ?: 0
                    }

                    is Number -> infoField.toInt()
                    else -> 0
                }

                val info = Info(color = color)
                Log.d("DataStorageImpl", "emitting Info(color=${info.color})")
                trySend(info)
            } catch (e: Exception) {
                Log.e("DataStorageImpl", "Failed to parse info", e)
                // Emit default Info instead of closing
                trySend(Info())
            }
        }
        awaitClose { listenerRegistration.remove() } // Unregister the listener when the flow is cancelled
    }

    fun generateRandomOpaqueColor(): Color {
        // Generate random values for Red, Green, and Blue components (0 to 255)
        val red = Random.nextInt(256)
        val green = Random.nextInt(256)
        val blue = Random.nextInt(256)

        // Create a Compose Color object with full opacity (alpha = 255)
        return Color(red, green, blue, alpha = 255)
    }


    /**
     * Create a new user with a given id
     */
    override suspend fun createUserWithId(id: String) {
        val color = generateRandomOpaqueColor()
        Log.d("Color", color.toString())

        val data = hashMapOf(
            "info" to mapOf("color" to color.toArgb())
        )

        try {
            val docRef = db.collection(COLLECTION_NAME)
                .document(id)

            db.runTransaction { tx ->
                val snap = tx.get(docRef)
                if (!snap.exists()) {
                    tx.set(docRef, data)
                }
                null
            }.await()

            Log.d("Firestore", "Successfully wrote data for user $id")
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to write user data for $id", e)
        }
    }


    /**
     * Add a financeEntity record to the storage
     */
    override fun addData(userId: String, financeEntity: FinanceEntity): String {
        Log.d(
            "DataStorageImpl",
            "addData user=$userId financeEntity.id=${financeEntity.id} label=${financeEntity.label}"
        )

        val financeId = financeEntity.id.ifEmpty { UUID.randomUUID().toString() }
        val updatedFinanceEntity = when (financeEntity) {
            is FinanceEntity.Transaction -> if (financeEntity.id.isEmpty()) financeEntity.copy(id = financeId) else financeEntity
            is FinanceEntity.Goal -> if (financeEntity.id.isEmpty()) financeEntity.copy(id = financeId) else financeEntity
            is FinanceEntity.Liability -> if (financeEntity.id.isEmpty()) financeEntity.copy(id = financeId) else financeEntity
        }

        db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionName(updatedFinanceEntity))
            .document(financeId)
            .set(updatedFinanceEntity.toMap())
            .addOnSuccessListener {
                Log.d(
                    "Firestore",
                    "Successfully wrote financeEntity record $financeId for user $userId"
                )
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to write financeEntity record for $userId", it)
            }

        return financeId
    }

    override suspend fun updateDataset(
        userId: String,
        oldFinanceEntity: FinanceEntity,
        newFinanceEntity: FinanceEntity
    ) {
        Log.d(
            "DataStorageImpl",
            "Update user=$userId financeEntity.id=${oldFinanceEntity.id} label=${oldFinanceEntity.label}"
        )

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionName(oldFinanceEntity))
            .document(oldFinanceEntity.id)

        try {
            // Modify new financeEntity record
            val modifiedFinanceEntity = when (newFinanceEntity) {
                is FinanceEntity.Goal -> {
                    newFinanceEntity.copy(
                        settlement = newFinanceEntity.settlement.map { settlement ->
                            settlement.copy(
                                tagIcon = newFinanceEntity.tagIcon,
                                label = settlement.label,
                            )
                        }
                    )
                }

                is FinanceEntity.Liability -> {
                    newFinanceEntity.copy(
                        settlement = newFinanceEntity.settlement.map { settlement ->
                            settlement.copy(
                                tagIcon = newFinanceEntity.tagIcon,
                                label = settlement.label,
                            )
                        }
                    )
                }

                is FinanceEntity.Transaction -> newFinanceEntity
            }

            // Simply update the document
            docRef.set(modifiedFinanceEntity.toMap())
            Log.d("FinanceEntity update", "Updated financeEntity record ${oldFinanceEntity.id}")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to update financeEntity record", e)
        }
    }

    override suspend fun addSettlementDataset(
        userId: String,
        datasetId: String,
        financeType: String,
        settlement: Settlement
    ) {
        Log.d(
            "DataStorageImpl",
            "addSettlementDataset called: $settlement for datasetId=$datasetId userId=$userId type=$financeType"
        )

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)

        try {
            val settlementId = settlement.settlementId.ifEmpty { UUID.randomUUID().toString() }
            val finalSettlement = settlement.copy(
                settlementId = settlementId,
                userId = userId,
                datasetId = datasetId
            )

            docRef.collection("settlement")
                .document(settlementId)
                .set(finalSettlement.settlementToMap)
                .await()

            Log.d(
                "DataStorageImpl",
                "Added settlement to financeEntity record $datasetId subcollection"
            )
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to add settlement", e)
            throw e
        }
    }


    override suspend fun stopRoutine(
        userId: String,
        datasetId: String,
        financeType: String
    ) {
        Log.d(
            "DataStorageImpl",
            "stopRoutine called for datasetId=$datasetId userId=$userId type=$financeType"
        )

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)

        try {
            val snapshot = try {
                docRef.get().await()
            } catch (e: Exception) {
                docRef.get(Source.CACHE).await()
            }
            snapshot.data?.toFinance() ?: return

            // Update stopRoutine field in routineData
            docRef.update("routineData.stopRoutine", true)
            Log.d("DataStorageImpl", "Stopped routine for record $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to stop routine", e)
        }
    }

    override suspend fun getCountOfAchievement(
        userId: String,
        datasetId: String,
        financeType: String
    ): CountAchievement? {
        return try {
            countAchievementForDataset(
                db,
                userId = userId,
                datasetId = datasetId,
                financeType = financeType
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get a financeEntity record from the storage
     * @param userId the user id
     * @param datasetId the record id
     */
    override suspend fun getDataset(
        userId: String,
        datasetId: String,
        financeType: String
    ): FinanceEntity? {
        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)

        return try {
            val snapshot = docRef.get().await()
            val entity = snapshot.data?.toFinance() ?: return null
            val settlements = loadSettlementForDataset(db, userId, datasetId, financeType)
            val achievements = loadAchievementForDataset(db, userId, datasetId, financeType)
            val withdrawals = loadWithdrawalForDataset(db, userId, datasetId, financeType)

            when (entity) {
                is FinanceEntity.Goal -> entity.copy(
                    settlement = settlements,
                    achievement = achievements
                )
                is FinanceEntity.Liability -> entity.copy(settlement = settlements)
                is FinanceEntity.Transaction -> entity.copy(withdrawal = withdrawals)
            }
        } catch (e: Exception) {
            try {
                val snapshot = docRef.get(Source.CACHE).await()
                val entity = snapshot.data?.toFinance() ?: return null
                val settlements =
                    loadSettlementForDataset(db, userId, datasetId, financeType, Source.CACHE)
                val achievements =
                    loadAchievementForDataset(db, userId, datasetId, financeType, Source.CACHE)
                val withdrawals =
                    loadWithdrawalForDataset(db, userId, datasetId, financeType, Source.CACHE)

                when (entity) {
                    is FinanceEntity.Goal -> entity.copy(
                        settlement = settlements,
                        achievement = achievements
                    )
                    is FinanceEntity.Liability -> entity.copy(settlement = settlements)
                    is FinanceEntity.Transaction -> entity.copy(withdrawal = withdrawals)
                }
            } catch (cacheException: Exception) {
                if (e is FirebaseFirestoreException) throw e
                null
            }
        }
    }

    override fun listenToDataset(
        userId: String,
        datasetId: String,
        financeType: String
    ): Flow<FinanceEntity?> = callbackFlow {
        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)

        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("DataStorageImpl", "Error listening to dataset $datasetId", error)
                close(error)
                return@addSnapshotListener
            }

            val entity = snapshot?.data?.toFinance()
            if (entity == null) {
                trySend(null)
                return@addSnapshotListener
            }

            // We need to combine this with settlements and achievements
            // For simplicity in this implementation, we will use the existing flows if possible
            // or just trigger a refresh. But a better way is to combine them.

            launch {
                // Collect one-time for now or create a nested listener
                // A better approach is to use combine() in the ViewModel
                trySend(entity)
            }
        }

        awaitClose { registration.remove() }
    }

    override fun listenToCountAchievement(
        userId: String,
        datasetId: String,
        financeType: String
    ): Flow<CountAchievement> = callbackFlow {
        val achievementCollection = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)
            .collection("achievement")

        val registration = achievementCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("DataStorageImpl", "Error listening to counts for $datasetId", error)
                return@addSnapshotListener
            }

            launch {
                // When subcollection changes, recalculate counts
                val counts = countAchievementForDataset(db, userId, datasetId, financeType)
                trySend(counts)
            }
        }

        awaitClose { registration.remove() }
    }


    override suspend fun completeRoutine(
        userId: String,
        datasetId: String,
        financeType: String,
        newDateTime: Timestamp,
        nextDeadline: Timestamp,
        previousDeadline: Timestamp
    ) = withContext(Dispatchers.IO) {
        Log.d("DataStorageImpl", "=== completeRoutine START ===")
        Log.d("DataStorageImpl", "Inputs: userId=$userId datasetId=$datasetId type=$financeType")

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)

        try {
            Log.d("DataStorageImpl", "Fetching document...")

            val snapshot = try {
                docRef.get().await()
            } catch (e: Exception) {
                Log.e("DataStorageImpl", "Remote fetch failed, trying CACHE", e)
                docRef.get(Source.CACHE).await()
            }

            if (!snapshot.exists()) {
                Log.e("DataStorageImpl", "Document does not exist for $datasetId")
                return@withContext
            }

            val baseFinance = snapshot.data?.toFinance()
            if (baseFinance == null) {
                Log.e("DataStorageImpl", "toFinance() returned null for $datasetId")
                return@withContext
            }

            Log.d("DataStorageImpl", "Parsed finance: $baseFinance")

            Log.d("DataStorageImpl", "Fetching settlements...")
            val settlementsSnapshot = try {
                docRef.collection("settlement").get().await()
            } catch (e: Exception) {
                Log.e("DataStorageImpl", "Settlement remote fetch failed, trying CACHE", e)
                docRef.collection("settlement").get(Source.CACHE).await()
            }

            val settlements = settlementsSnapshot.documents.mapNotNull {
                it.data?.asSettlement()
            }

            Log.d("DataStorageImpl", "Settlements count: ${settlements.size}")

            val finance = when (baseFinance) {
                is FinanceEntity.Goal -> baseFinance.copy(settlement = settlements)
                is FinanceEntity.Liability -> baseFinance.copy(settlement = settlements)
                else -> baseFinance
            }

            val totalSettlementAmount = when (finance) {
                is FinanceEntity.Goal -> finance.settlement.sumOf { it.amount }
                is FinanceEntity.Liability -> finance.settlement.sumOf { it.amount }
                is FinanceEntity.Transaction -> 0.0
            }

            val status = if (totalSettlementAmount >= finance.amount) {
                Status.COMPLETED
            } else {
                Status.OVERDUE
            }

            Log.d("DataStorageImpl", "Total settlement: $totalSettlementAmount")
            Log.d("DataStorageImpl", "Target amount: ${finance.amount}")
            Log.d("DataStorageImpl", "Computed status: $status")

            val batch = db.batch()

            val achievement = Achievement(
                status = status.name,
                totalSettlementAmount = totalSettlementAmount,
                startDateTime = previousDeadline,
                deadlineDateTime = newDateTime
            )

            val achievementId = UUID.randomUUID().toString()
            val achievementRef = docRef.collection("achievement").document(achievementId)

            batch.set(achievementRef, achievement.achievementToMap)
            Log.d("DataStorageImpl", "Achievement added: $achievementId")

            batch.update(
                docRef,
                mapOf(
                    "routineData.startDateTime" to newDateTime,
                    "routineData.deadlineDateTime" to nextDeadline,
                    "routineData.triggerMillis" to nextDeadline.toEpochMilli()
                )
            )
            Log.d("DataStorageImpl", "Parent document update prepared")

            for (adjDoc in settlementsSnapshot.documents) {
                Log.d("DataStorageImpl", "Deleting settlement: ${adjDoc.id}")
                batch.delete(adjDoc.reference)
            }

            Log.d("DataStorageImpl", "Committing batch...")

            batch.commit()
                .addOnSuccessListener {
                    Log.d("DataStorageImpl", "✅ Batch success")
                }
                .addOnFailureListener {
                    Log.e("DataStorageImpl", "❌ Batch failed", it)
                }

            Log.d("DataStorageImpl", "✅ completeRoutine SUCCESS for $datasetId")
        } catch (e: CancellationException) {
            Log.w("DataStorageImpl", "completeRoutine CANCELLED for $datasetId", e)
            throw e
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "❌ completeRoutine FAILED for $datasetId", e)
            throw e
        } finally {
            Log.d("DataStorageImpl", "=== completeRoutine END ===")
        }
    }

    override suspend fun addWithdrawal(
        userId: String,
        datasetId: String,
        financeType: String,
        withdrawal: Withdrawal
    ) = withContext(Dispatchers.IO) {
        Log.d("DataStorageImpl", "=== completeRoutine START ===")
        Log.d("DataStorageImpl", "Inputs: userId=$userId datasetId=$datasetId type=$financeType")

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)

        try {
            val batch = db.batch()

            val withdrawalId = withdrawal.withdrawalId.ifEmpty { UUID.randomUUID().toString() }
            val finalWithdrawal = withdrawal.copy(
                withdrawalId = withdrawalId,
                userId = userId,
                datasetId = datasetId
            )

            val withdrawalRef = docRef.collection("withdrawal")
                .document(withdrawalId)

            batch.set(withdrawalRef, finalWithdrawal.withdrawalToMap)

            Log.d("DataStorageImpl", "Committing batch...")

            batch.commit()
                .addOnSuccessListener {
                    Log.d("DataStorageImpl", "✅ Batch success")
                }
                .addOnFailureListener {
                    Log.e("DataStorageImpl", "❌ Batch failed", it)
                }

            Log.d("DataStorageImpl", "✅ completeRoutine SUCCESS for $datasetId")
        } catch (e: CancellationException) {
            Log.w("DataStorageImpl", "completeRoutine CANCELLED for $datasetId", e)
            throw e
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "❌ completeRoutine FAILED for $datasetId", e)
            throw e
        } finally {
            Log.d("DataStorageImpl", "=== completeRoutine END ===")
        }
    }


    override suspend fun addStatus(
        userId: String,
        datasetId: String,
        financeType: String,
        newDateTime: Timestamp,
        newDeadlineDateTime: Timestamp
    ) {
        Log.d(
            "DataStorageImpl",
            "addStatus called for datasetId=$datasetId userId=$userId type=$financeType"
        )

        val datasetDocRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)

        try {
            val snapshot = try {
                datasetDocRef.get().await()
            } catch (e: Exception) {
                datasetDocRef.get(Source.CACHE).await()
            }
            val baseFinance = snapshot.data?.toFinance() ?: return

            val settlementsSnapshot = try {
                datasetDocRef.collection("settlement").get().await()
            } catch (e: Exception) {
                datasetDocRef.collection("settlement").get(Source.CACHE).await()
            }
            val settlements = settlementsSnapshot.documents.mapNotNull { it.data?.asSettlement() }

            val finance = when (baseFinance) {
                is FinanceEntity.Goal -> baseFinance.copy(settlement = settlements)
                is FinanceEntity.Liability -> baseFinance.copy(settlement = settlements)
                else -> baseFinance
            }

            val totalSettlementAmount = when (finance) {
                is FinanceEntity.Goal -> finance.settlement.sumOf { it.amount }
                is FinanceEntity.Liability -> finance.settlement.sumOf { it.amount }
                is FinanceEntity.Transaction -> 0.0
            }
            val isAchieved = totalSettlementAmount >= finance.amount
            val status = if (isAchieved) Status.COMPLETED else Status.OVERDUE

            // Create status history entry with total settlement amount and timestamps
            val achievement = Achievement(
                status = status.name,
                totalSettlementAmount = totalSettlementAmount,
                startDateTime = newDateTime,
                deadlineDateTime = newDeadlineDateTime
            )

            // Add to achievement subcollection
            val statusId = UUID.randomUUID().toString()
            datasetDocRef.collection("achievement")
                .document(statusId)
                .set(achievement.achievementToMap)

            // Update dataset timestamps
            datasetDocRef.update(
                mapOf(
                    "createdAt" to newDateTime,
                    "routineData.deadlineDateTime" to newDeadlineDateTime,
                    "routineData.triggerMillis" to newDeadlineDateTime.toEpochMilli()
                )
            )

            Log.d("addStatus", "Status added and record updated for $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to add status", e)
        }
    }

    override suspend fun clearSettlementList(
        userId: String,
        datasetId: String,
        financeType: String
    ) {
        Log.d(
            "clearSettlementList",
            "clearSettlementList called: clear settlement for datasetId=$datasetId userId=$userId type=$financeType"
        )

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)

        try {
            val settlementsSnapshot = docRef.collection("settlement").get().await()
            if (!settlementsSnapshot.isEmpty) {
                val batch = db.batch()
                for (adjDoc in settlementsSnapshot.documents) {
                    batch.delete(adjDoc.reference)
                }
                batch.commit().await()
            }
            Log.d("clearSettlementList", "Settlement subcollection cleared for dataset $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to clear settlement list", e)
        }
    }

    override suspend fun ensureDatasetIds(userId: String) {
        Log.d("DataStorageImpl", "ensureDatasetIds called for user=$userId")

        try {
            val collections = listOf(TRANSACTION_COLLECTION, GOAL_COLLECTION, LIABILITY_COLLECTION)
            var updated = 0

            for (collection in collections) {
                val ref = db.collection(COLLECTION_NAME)
                    .document(userId)
                    .collection(collection)

                val snapshot = ref.get().await()

                for (doc in snapshot.documents) {
                    val id = doc.get("id") as? String
                    if (id == null || id.isEmpty()) {
                        val newId = UUID.randomUUID().toString()
                        doc.reference.update("id", newId).await()
                        updated++
                    }
                }
            }

            if (updated > 0) {
                Log.d("DataStorageImpl", "ensureDatasetIds: assigned $updated ids for user=$userId")
            } else {
                Log.d("DataStorageImpl", "ensureDatasetIds: no ids needed for user=$userId")
            }
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to ensure dataset ids", e)
            throw e
        }
    }

    override suspend fun removeDataset(userId: String, financeEntity: FinanceEntity) {
        val id = financeEntity.id
        val docRef = db.collection(COLLECTION_NAME).document(userId)
            .collection(getCollectionName(financeEntity)).document(id)

        try {
            // 1. Get the snapshots (Try to use cache if offline)
            val achievementSnapshot = try {
                docRef.collection("achievement").get().await()
            } catch (e: Exception) {
                docRef.collection("achievement").get(Source.CACHE).await()
            }

            // 2. Create a Write Batch
            val batch = db.batch()

            // 3. Add all subcollection deletes to the batch (No await here!)
            for (doc in achievementSnapshot.documents) {
                batch.delete(doc.reference)
            }

            // 4. Add the parent document delete to the batch
            batch.delete(docRef)

            // 5. Commit the batch
            // Remove .await() if you want it to finish instantly while offline
            batch.commit()

            Log.d("DataStorageImpl", "FinanceEntity record and history deleted via batch")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to remove financeEntity record", e)
            throw e
        }
    }

    override suspend fun removeSettlementDataset(
        userId: String,
        datasetId: String,
        financeType: String,
        settlement: Settlement
    ) {
        Log.d(
            "DataStorageImpl",
            "removeSettlementDataset called: $settlement for datasetId=$datasetId userId=$userId type=$financeType"
        )

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)

        try {
            docRef.collection("settlement")
                .document(settlement.settlementId)
                .delete()
                .await()
            Log.d(
                "DataStorageImpl",
                "Removed settlement from financeEntity record $datasetId subcollection"
            )
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to remove settlement", e)
            throw e
        }
    }

    override suspend fun removeWithdrawalDataset(
        userId: String,
        datasetId: String,
        financeType: String,
        withdrawal: Withdrawal
    ) {
        Log.d(
            "DataStorageImpl",
            "removeSettlementDataset called: $withdrawal for datasetId=$datasetId userId=$userId type=$financeType"
        )

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)

        try {
            docRef.collection("withdrawal")
                .document(withdrawal.withdrawalId)
                .delete()
                .await()
            Log.d(
                "DataStorageImpl",
                "Removed withdrawal from financeEntity record $datasetId subcollection"
            )
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to remove withdrawal", e)
            throw e
        }
    }

    override suspend fun updateSettlementDataset(
        userId: String,
        datasetId: String,
        financeType: String,
        oldSettlement: Settlement,
        newSettlement: Settlement
    ) {
        Log.d(
            "DataStorageImpl",
            "updateSettlementDataset called: $oldSettlement for datasetId=$datasetId userId=$userId type=$financeType"
        )

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)

        try {
            val finalSettlement = newSettlement.copy(
                userId = userId,
                datasetId = datasetId
            )
            docRef.collection("settlement")
                .document(oldSettlement.settlementId)
                .set(finalSettlement.settlementToMap)
                .await()
            Log.d(
                "Settlement update",
                "Updated settlement ${oldSettlement.settlementId} in subcollection"
            )
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to update settlement", e)
            throw e
        }
    }

    override suspend fun updateWithdrawalDataset(
        userId: String,
        datasetId: String,
        financeType: String,
        oldWithdrawal: Withdrawal,
        newWithdrawal: Withdrawal
    ) {
        Log.d(
            "DataStorageImpl",
            "updateSettlementDataset called: $oldWithdrawal for datasetId=$datasetId userId=$userId type=$financeType"
        )

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)

        try {
            val finalWithdrawal = newWithdrawal.copy(
                datasetId = datasetId
            )
            docRef.collection("withdrawal")
                .document(oldWithdrawal.withdrawalId)
                .set(finalWithdrawal.withdrawalToMap)
                .await()
            Log.d(
                "Withdrawal update",
                "Updated withdrawal ${oldWithdrawal.withdrawalId} in subcollection"
            )
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to update withdrawal", e)
            throw e
        }
    }

    override suspend fun updateAchievementDataset(
        userId: String,
        datasetId: String,
        financeType: String,
        oldAchievement: Achievement,
        newAchievement: Achievement
    ) {
        updateAchievementDataset(
            db,
            userId = userId,
            datasetId = datasetId,
            financeType = financeType,
            oldAchievement = oldAchievement,
            newAchievement = newAchievement
        )
    }

    override suspend fun removeAchievementDataset(
        userId: String,
        datasetId: String,
        financeType: String,
        achievement: Achievement
    ) {
        removeAchievementDataset(
            db,
            userId = userId,
            datasetId = datasetId,
            financeType = financeType,
            achievement = achievement
        )
    }

    companion object {
        const val COLLECTION_NAME = "database"
        const val TRANSACTION_COLLECTION = "Transaction"
        const val GOAL_COLLECTION = "Goal"
        const val LIABILITY_COLLECTION = "Liability"
    }
}