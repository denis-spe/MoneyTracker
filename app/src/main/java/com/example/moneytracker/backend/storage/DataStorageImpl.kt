// Glory be the name of LORD our GOD
package com.example.moneytracker.backend.storage

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.moneytracker.helper.adjustmentToMap
import com.example.moneytracker.helper.statusHistoryToMap
import com.example.moneytracker.helper.toFinance
import com.example.moneytracker.helper.toMap
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
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

                val finances = snapshot.documents.mapNotNull { doc ->
                    runCatching { doc.data?.toFinance() }
                        .onFailure {
                            Log.e(
                                "DataStorageImpl",
                                "Failed to parse item during filtering in $collection",
                                it
                            )
                        }
                        .getOrNull()
                }
                results.addAll(finances)
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

        fun emitCombined() {
            val combined = latestData.values.flatten()
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
                        adjustment = newFinanceEntity.adjustment.map { adjustment ->
                            adjustment.copy(
                                tagIcon = newFinanceEntity.tagIcon,
                                label = adjustment.label,
                            )
                        }
                    )
                }

                is FinanceEntity.Liability -> {
                    newFinanceEntity.copy(
                        adjustment = newFinanceEntity.adjustment.map { adjustment ->
                            adjustment.copy(
                                tagIcon = newFinanceEntity.tagIcon,
                                label = adjustment.label,
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

    override suspend fun addAdjustmentDataset(
        userId: String,
        datasetId: String,
        financeType: String,
        adjustment: Adjustment
    ) {
        Log.d(
            "DataStorageImpl",
            "addAdjustmentDataset called: $adjustment for datasetId=$datasetId userId=$userId type=$financeType"
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
            val finance = snapshot.data?.toFinance() ?: return

            // Add new adjustment to list
            val updatedAdjustments = when (finance) {
                is FinanceEntity.Goal -> finance.adjustment.toMutableList()
                is FinanceEntity.Liability -> finance.adjustment.toMutableList()
                is FinanceEntity.Transaction -> mutableListOf()
            }
            updatedAdjustments.add(
                adjustment.copy(
                    adjustmentId = adjustment.adjustmentId.ifEmpty { UUID.randomUUID().toString() }
                )
            )

            // Update only the adjustment array
            docRef.update("adjustment", updatedAdjustments.map { it.adjustmentToMap })
            Log.d("DataStorageImpl", "Added adjustment to financeEntity record $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to add adjustment", e)
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
            docRef.get().await().data?.toFinance()
        } catch (e: Exception) {
            try {
                docRef.get(Source.CACHE).await().data?.toFinance()
            } catch (cacheException: Exception) {
                if (e is FirebaseFirestoreException) throw e
                null
            }
        }
    }

    override suspend fun completeRoutine(
        userId: String,
        datasetId: String,
        financeType: String,
        newDateTime: Timestamp,
        nextDeadline: Timestamp
    ) {
        Log.d("DataStorageImpl", "completeRoutine (Offline-Safe) for $datasetId type=$financeType")

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
            val finance = snapshot.data?.toFinance() ?: return


            // Calculate status and total adjustment amount
            val totalAdjustmentAmount = when (finance) {
                is FinanceEntity.Goal -> finance.adjustment.sumOf { it.amount }
                is FinanceEntity.Liability -> finance.adjustment.sumOf { it.amount }
                is FinanceEntity.Transaction -> 0.0
            }
            val isAchieved = totalAdjustmentAmount >= finance.amount
            val status = if (isAchieved) Status.SUCCESS else Status.OVERDUE

            // Create status history entry with total adjustment amount and timestamps
            val statusHistory = StatusHistory(
                status = status.name,
                totalAdjustmentAmount = totalAdjustmentAmount,
                startDateTime = newDateTime,
                deadlineDateTime = nextDeadline
            )

            // Add to statusHistory subcollection
            val statusId = UUID.randomUUID().toString()
            docRef.collection("statusHistory")
                .document(statusId)
                .set(statusHistory.statusHistoryToMap)

            // Update financeEntity fields and reset adjustments
            docRef.update(
                mapOf(
                    "routineData.startDateTime" to newDateTime,
                    "routineData.deadlineDateTime" to nextDeadline,
                    "adjustment" to emptyList<Map<String, Any?>>()
                )
            )

            Log.d("DataStorageImpl", "completeRoutine: Update successful for $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Error in completeRoutine for $datasetId", e)
            throw e
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
            val finance = snapshot.data?.toFinance() ?: return

            val totalAdjustmentAmount = when (finance) {
                is FinanceEntity.Goal -> finance.adjustment.sumOf { it.amount }
                is FinanceEntity.Liability -> finance.adjustment.sumOf { it.amount }
                is FinanceEntity.Transaction -> 0.0
            }
            val isAchieved = totalAdjustmentAmount >= finance.amount
            val status = if (isAchieved) Status.SUCCESS else Status.OVERDUE

            // Create status history entry with total adjustment amount and timestamps
            val statusHistory = StatusHistory(
                status = status.name,
                totalAdjustmentAmount = totalAdjustmentAmount,
                startDateTime = newDateTime,
                deadlineDateTime = newDeadlineDateTime
            )

            // Add to statusHistory subcollection
            val statusId = UUID.randomUUID().toString()
            datasetDocRef.collection("statusHistory")
                .document(statusId)
                .set(statusHistory.statusHistoryToMap)

            // Update dataset timestamps
            datasetDocRef.update(
                mapOf(
                    "createdAt" to newDateTime,
                    "routineData.deadlineDateTime" to newDeadlineDateTime
                )
            )

            Log.d("addStatus", "Status added and record updated for $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to add status", e)
        }
    }

    override suspend fun clearAdjustmentList(
        userId: String,
        datasetId: String,
        financeType: String
    ) {
        Log.d(
            "clearAdjustmentList",
            "clearAdjustmentList called: clear adjustment for datasetId=$datasetId userId=$userId type=$financeType"
        )

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)

        try {
            docRef.update("adjustment", emptyList<Map<String, Any?>>())
            Log.d("clearAdjustmentList", "Adjustment list cleared for dataset $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to clear adjustment list", e)
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
            val statusHistorySnapshot = try {
                docRef.collection("statusHistory").get().await()
            } catch (e: Exception) {
                docRef.collection("statusHistory").get(Source.CACHE).await()
            }

            // 2. Create a Write Batch
            val batch = db.batch()

            // 3. Add all subcollection deletes to the batch (No await here!)
            for (doc in statusHistorySnapshot.documents) {
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

    override suspend fun removeAdjustmentDataset(
        userId: String,
        datasetId: String,
        financeType: String,
        adjustment: Adjustment
    ) {
        Log.d(
            "DataStorageImpl",
            "removeAdjustmentDataset called: $adjustment for datasetId=$datasetId userId=$userId type=$financeType"
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
            val finance = snapshot.data?.toFinance() ?: return

            // Remove adjustment from list
            val currentAdjustments = when (finance) {
                is FinanceEntity.Goal -> finance.adjustment
                is FinanceEntity.Liability -> finance.adjustment
                is FinanceEntity.Transaction -> emptyList()
            }
            val updatedAdjustments = currentAdjustments.filter {
                it.adjustmentId != adjustment.adjustmentId
            }

            docRef.update("adjustment", updatedAdjustments.map { it.adjustmentToMap })
            Log.d("DataStorageImpl", "Removed adjustment from financeEntity record $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to remove adjustment", e)
            throw e
        }
    }

    override suspend fun updateAdjustmentDataset(
        userId: String,
        datasetId: String,
        financeType: String,
        oldAdjustment: Adjustment,
        newAdjustment: Adjustment
    ) {
        Log.d(
            "DataStorageImpl",
            "updateAdjustmentDataset called: $oldAdjustment for datasetId=$datasetId userId=$userId type=$financeType"
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
            val finance = snapshot.data?.toFinance() ?: return

            // Remove old adjustment and add new one
            val currentAdjustments = when (finance) {
                is FinanceEntity.Goal -> finance.adjustment
                is FinanceEntity.Liability -> finance.adjustment
                is FinanceEntity.Transaction -> emptyList()
            }
            val updatedAdjustments = currentAdjustments.filter {
                it.adjustmentId != oldAdjustment.adjustmentId
            }.toMutableList()

            updatedAdjustments.add(newAdjustment)

            docRef.update("adjustment", updatedAdjustments.map { it.adjustmentToMap })
            Log.d("Adjustment update", "Updated adjustment ${oldAdjustment.adjustmentId}")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to update adjustment", e)
            throw e
        }
    }

    companion object {
        const val COLLECTION_NAME = "database"
        const val TRANSACTION_COLLECTION = "Transaction"
        const val GOAL_COLLECTION = "Goal"
        const val LIABILITY_COLLECTION = "Liability"
    }
}