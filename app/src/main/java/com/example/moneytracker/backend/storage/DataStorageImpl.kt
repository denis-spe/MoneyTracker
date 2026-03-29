// Glory be the name of LORD our GOD
package com.example.moneytracker.backend.storage

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.moneytracker.helper.adjustmentToMap
import com.example.moneytracker.helper.castToMutableMap
import com.example.moneytracker.helper.casting
import com.example.moneytracker.helper.statusHistoryToMap
import com.example.moneytracker.helper.statusToMap
import com.example.moneytracker.helper.toAdjustment
import com.example.moneytracker.helper.toAmount
import com.example.moneytracker.helper.toDataset
import com.example.moneytracker.helper.toMap
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

class DataStorageImpl(
    override val db: FirebaseFirestore
) : DataStorage {

    override suspend fun getWholeDatasets(
        userId: String,
        onSuccess: (isSuccess: Boolean) -> Unit,
        onFailure: (error: Throwable?) -> Unit
    ): Flow<List<Dataset>> = callbackFlow {
        val documentRef = db.collection(COLLECTION_NAME)
            .document(userId)

        val listenerRegistration = documentRef.addSnapshotListener { snapshot, error ->
            if (error != null && snapshot == null) {
                Log.e("DataStorageImpl", "getWholeDatasets listener error", error)
                onFailure(error)
                close(error) // Close the flow with an exception
                return@addSnapshotListener
            }

            try {
                val raw = snapshot?.get("datasets")
                Log.d("DataStorageImpl", "raw datasets field: $raw")
                if (raw == null) {
                    // No datasets field yet - emit empty list
                    Log.d("DataStorageImpl", "No datasets field found, emitting empty list")
                    trySend(emptyList())
                    onSuccess(true)
                    return@addSnapshotListener
                }

                val list = when (raw) {
                    is List<*> -> raw
                    else -> listOf(raw)
                }

                val data = list.mapNotNull {
                    try {
                        when (it) {
                            is Map<*, *> -> it.toDataset()
                            else -> null
                        }
                    } catch (e: Exception) {
                        Log.e("DataStorageImpl", "Failed to parse dataset item", e)
                        null
                    }
                }

                Log.d("DataStorageImpl", "parsed datasets count: ${data.size}")
                trySend(data)
                onSuccess(true)
            } catch (e: Exception) {
                Log.e("DataStorageImpl", "Unhandled error while reading datasets", e)
                // Don't close the flow on parse errors; send an empty list instead to keep collecting
                trySend(emptyList())
                onFailure(e)
            }
        }
        awaitClose { listenerRegistration.remove() } // Unregister the listener when the flow is cancelled
    }

    override suspend fun getInfo(userId: String): Flow<Info> = callbackFlow {
        val documentRef = db.collection(COLLECTION_NAME)
            .document(userId)

        val listenerRegistration = documentRef.addSnapshotListener { snapshot, error ->
            if (error != null && snapshot == null) {
                Log.e("DataStorageImpl", "getInfo listener error", error)
                close(error) // Close the flow with an exception
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
            "datasets" to listOf<Dataset>(),
            "info" to Info(color = color.toArgb())
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
            // This will show you if there was a permission error or network issue
            Log.e("Firestore", "Failed to write user data for $id", e)
        }
    }


    /**
     * Add a dataset to the storage
     */
    override fun addData(userId: String, dataset: Dataset): String {
        Log.d(
            "DataStorageImpl",
            "addData user=$userId dataset.id=${dataset.id} label=${dataset.label}"
        )
        db.collection(COLLECTION_NAME)
            .document(userId)
            .update("datasets", FieldValue.arrayUnion(dataset.toMap()))
            .addOnSuccessListener {
                Log.d("Firestore", "Successfully wrote data for user $userId")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to write user data for $userId", it)
            }

        return dataset.id
    }

    override suspend fun updateDataset(
        userId: String,
        oldDataset: Dataset,
        newDataset: Dataset
    ) {
        Log.d(
            "DataStorageImpl",
            "Update user=$userId dataset.id=${oldDataset.id} label=${oldDataset.label}"
        )
        val docRef = db.collection(COLLECTION_NAME).document(userId)

        // read (maybe served from cache if offline)
        val snapshot = docRef.get().await()
        val datasets = casting(snapshot.get("datasets")) ?: emptyList()

        val mutableDatasets = datasets.toMutableList()
        val isRemoved = mutableDatasets.removeAll { (it["id"] as? String) == oldDataset.id }

        if (isRemoved) {
            Log.d("Dataset update", "Removed for update")
        } else {
            Log.d("Dataset update", "Failed to remove")
        }

        // Add the new dataset
        val modifyNewDataset = newDataset.copy(
            adjustment = newDataset.adjustment.map { adjustment ->
                adjustment.copy(
                    tagIcon = newDataset.tagIcon,
                    label = adjustment.label,
                )

            }
        )

        val wasAdded = mutableDatasets.add(modifyNewDataset.toMap())

        if (wasAdded) {
            Log.d("Dataset update", "Added new dataset for update")
        } else {
            Log.d("Dataset update", "Failed to add dataset")
        }

        // write whole datasets array back (queued when offline)
        docRef.update("datasets", mutableDatasets).await()
    }

    override suspend fun addAdjustmentDataset(
        userId: String,
        datasetId: String,
        adjustment: Adjustment
    ) {
        Log.d(
            "DataStorageImpl",
            "addAdjustmentDataset called: $adjustment for datasetId=$datasetId userId=$userId"
        )
        val docRef = db.collection(COLLECTION_NAME).document(userId)

        // read (maybe served from cache if offline)
        val snapshot = docRef.get().await()
        val datasets = casting(snapshot.get("datasets")) ?: emptyList()

        val mutableDatasets = datasets.toMutableList()
        val idx = mutableDatasets.indexOfFirst { (it["id"] as? String) == datasetId }

        if (idx == -1) {
            throw IllegalArgumentException("Dataset $datasetId not found for user $userId")
        }

        // mutate target dataset's items
        val datasetMap = mutableDatasets[idx].toMutableMap()
        val items =
            (casting(datasetMap["adjustment"]) ?: emptyList()).toMutableList()
        items.add(
            adjustment.copy(
                adjustmentId = UUID.randomUUID().toString()
            ).adjustmentToMap
        )                         // your map representation
        datasetMap["adjustment"] = items
        mutableDatasets[idx] = datasetMap

        // write whole datasets array back (queued when offline)
        docRef.update("datasets", mutableDatasets).await()
    }


    override suspend fun stopRoutine(
        userId: String,
        datasetId: String,
    ) {
        Log.d(
            "DataStorageImpl",
            "addAdjustmentDataset called for datasetId=$datasetId userId=$userId"
        )
        val docRef = db.collection(COLLECTION_NAME).document(userId)

        // read (maybe served from cache if offline)
        val snapshot = docRef.get().await()
        val datasets = casting(snapshot.get("datasets")) ?: emptyList()

        val mutableDatasets = datasets.toMutableList()
        val idx = mutableDatasets.indexOfFirst { (it["id"] as? String) == datasetId }

        if (idx == -1) {
            throw IllegalArgumentException("Dataset $datasetId not found for user $userId")
        }

        // mutate target dataset's items
        val datasetMap = mutableDatasets[idx].toMutableMap()
        val items = castToMutableMap(datasetMap["routine"])

        items["stopRoutine"] = true
        // your map representation
        datasetMap["routine"] = items
        mutableDatasets[idx] = datasetMap

        // write whole datasets array back (queued when offline)
        docRef.update("datasets", mutableDatasets).await()
    }

    /**
     * Get a dataset from the storage
     * @param userId the user id
     * @param datasetId the dataset id
     */
    // Add dependency if needed: implementation "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:<version>"

    // requires: import kotlinx.coroutines.suspendCancellableCoroutine
    override suspend fun getDataset(userId: String, datasetId: String): Dataset? =
        suspendCancellableCoroutine { cont ->
            val docRef = db.collection(COLLECTION_NAME).document(userId)
            val task = docRef.get()
            task.addOnSuccessListener { snapshot ->
                try {
                    val raw = snapshot.get("datasets") ?: run {
                        if (!cont.isCompleted) cont.resume(null) { cause, _, _ -> }
                        return@addOnSuccessListener
                    }

                    val list = when (raw) {
                        is List<*> -> raw
                        else -> listOf(raw)
                    }

                    val dataset = list.mapNotNull {
                        (it as? Map<*, *>)?.toDataset()
                    }.firstOrNull { it.id == datasetId }

                    if (!cont.isCompleted) cont.resume(dataset) { cause, _, _ -> }
                } catch (e: Exception) {
                    if (!cont.isCompleted) cont.resumeWithException(e)
                }
            }
            task.addOnFailureListener { exc ->
                if (!cont.isCompleted) cont.resumeWithException(exc)
            }
        }

    /**
     * At the end of a routine, add a status into a list in
     * dataset and clear the adjustment list (identified by its id) for the given user.
     * @param userId the user id
     * @param datasetId the dataset id
     */
    override suspend fun completeRoutine(
        userId: String,
        datasetId: String,
        newDateTime: Timestamp,
        nextDeadline: Timestamp
    ) {
        Log.d("DataStorageImpl", "completeRoutine (Offline-Safe) for $datasetId")
        val docRef = db.collection(COLLECTION_NAME).document(userId)

        try {
            // 1. Single Read (will use cache if offline)
            val snapshot = docRef.get().await()
            val datasets = casting(snapshot.get("datasets")) ?: emptyList()
            val mutableDatasets = datasets.toMutableList()

            val idx = mutableDatasets.indexOfFirst { (it["id"] as? String) == datasetId }
            if (idx == -1) return

            val datasetMap = mutableDatasets[idx].toMutableMap()

            // 2. Logic to update Status
            val amount = datasetMap.toAmount()
            val adjustments = datasetMap.toAdjustment()
            val adjustmentAmount = adjustments.sumOf { it.amount }
            val remainingAmount = amount - adjustmentAmount
            val status = if (remainingAmount == 0.0) Status.SUCCESS else Status.OVERDUE

            val history = (casting(datasetMap["statusHistory"]) ?: emptyList()).toMutableList()

            val statusHistory = StatusHistory(
                status = status.name,
                adjustmentAmount = adjustmentAmount,
                dateTime = newDateTime,
                deadlineTime = nextDeadline
            )

            history.add(statusHistory.statusHistoryToMap)

            datasetMap["statusHistory"] = history
            datasetMap["dateTime"] = newDateTime
            datasetMap["deadlineDateTime"] = nextDeadline

            // 3. Logic to Clear Adjustments
            datasetMap["adjustment"] = emptyList<Map<String, Any?>>()

            // 4. Single Write (will queue locally if offline)
            mutableDatasets[idx] = datasetMap

            docRef.update("datasets", mutableDatasets).await()

            Log.d("DataStorageImpl", "completeRoutine: Local update successful")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Error in completeRoutine", e)
            throw e
        }
    }


    override suspend fun addStatus(
        userId: String,
        datasetId: String,
        newDateTime: Timestamp,
        newDeadlineDateTime: Timestamp
    ) {
        Log.d(
            "DataStorageImpl",
            "addStatus called for datasetId=$datasetId userId=$userId"
        )
        val docRef = db.collection(COLLECTION_NAME).document(userId)

        // read (maybe served from cache if offline)
        val snapshot = docRef.get().await()
        val datasets = casting(snapshot.get("datasets")) ?: emptyList()

        val mutableDatasets = datasets.toMutableList()
        val idx = mutableDatasets.indexOfFirst { (it["id"] as? String) == datasetId }

        if (idx == -1) {
            throw IllegalArgumentException("Dataset $datasetId not found for user $userId")
        }

        // mutate target dataset's items
        val datasetMap = mutableDatasets[idx].toMutableMap()

        val amount = datasetMap.toAmount()
        val adjustmentAmount = datasetMap.toAdjustment().sumOf { it.amount }
        val remainingAmount = amount - adjustmentAmount

        val status = if (remainingAmount == 0.0) Status.SUCCESS else Status.OVERDUE

        val items =
            (casting(datasetMap["statusHistory"]) ?: emptyList()).toMutableList()

        items.add(
            status.statusToMap
        )

        datasetMap["statusHistory"] = items
        datasetMap["dateTime"] = newDateTime
        datasetMap["deadlineDateTime"] = newDeadlineDateTime
        mutableDatasets[idx] = datasetMap

        Log.d("addStatus", "New updated dataset $datasetMap")

        // write whole datasets array back (queued when offline)
        docRef.update("datasets", mutableDatasets).await()
    }

    override suspend fun clearAdjustmentList(userId: String, datasetId: String) {
        Log.d(
            "clearAdjustmentList",
            "addAdjustmentDataset called: clear adjustment for datasetId=$datasetId userId=$userId"
        )
        val docRef = db.collection(COLLECTION_NAME).document(userId)

        // read (maybe served from cache if offline)
        val snapshot = docRef.get().await()
        val datasets = casting(snapshot.get("datasets")) ?: emptyList()

        val mutableDatasets = datasets.toMutableList()
        val idx = mutableDatasets.indexOfFirst { (it["id"] as? String) == datasetId }

        if (idx == -1) {
            throw IllegalArgumentException("Dataset $datasetId not found for user $userId")
        }

        // mutate target dataset's items
        val datasetMap = mutableDatasets[idx].toMutableMap()
        datasetMap["adjustment"] = emptyList<Map<String, Any?>>()
        mutableDatasets[idx] = datasetMap

        // write whole datasets array back (queued when offline)
        docRef.update("datasets", mutableDatasets).await()
    }

    override suspend fun ensureDatasetIds(userId: String) {
        val docRef = db.collection(COLLECTION_NAME).document(userId)
        try {
            db.runTransaction { tx ->
                val snap = tx.get(docRef)
                if (!snap.exists()) return@runTransaction null

                val list: MutableList<Any?> = when (val rawDatasets = snap.get("datasets")) {
                    is List<*> -> rawDatasets.map { it }.toMutableList()
                    else -> mutableListOf()
                }

                var changed = false
                var added = 0
                val newList = list.map { item ->
                    if (item is Map<*, *>) {
                        val id = item["id"] as? String
                        if (id == null) {
                            changed = true
                            added++
                            // copy entries and add id
                            val mutable = item.entries.associate { (k, v) -> k.toString() to v }
                                .toMutableMap()
                            mutable["id"] = UUID.randomUUID().toString()
                            mutable as Map<String, Any?>
                        } else item
                    } else item
                }

                if (changed) {
                    Log.d(
                        "DataStorageImpl",
                        "ensureDatasetIds: assigning $added ids for user=$userId"
                    )
                    tx.update(docRef, "datasets", newList)
                } else {
                    Log.d("DataStorageImpl", "ensureDatasetIds: no ids needed for user=$userId")
                }
                null
            }.await()
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to ensure dataset ids", e)
            throw e
        }
    }

    override suspend fun removeDataset(userId: String, dataset: Dataset) {
        val id = dataset.id

        Log.d(
            "DataStorageImpl",
            "addAdjustmentDataset called: clear adjustment for datasetId=$id userId=$userId"
        )
        val docRef = db.collection(COLLECTION_NAME).document(userId)

        // read (maybe served from cache if offline)
        val snapshot = docRef.get().await()
        val datasets = casting(snapshot.get("datasets")) ?: emptyList()

        val mutableDatasets = datasets.toMutableList()
        val isRemoved = mutableDatasets.removeAll { (it["id"] as? String) == id }

        if (!isRemoved) {
            Log.d("DataStorageImpl", "Dataset $id not found for user $userId")
        }

        // write whole datasets array back (queued when offline)
        docRef.update("datasets", mutableDatasets).await()
    }

    override suspend fun removeAdjustmentDataset(
        userId: String,
        datasetId: String,
        adjustment: Adjustment
    ) {
        Log.d(
            "DataStorageImpl",
            "removeAdjustmentDataset called: $adjustment for datasetId=$datasetId userId=$userId"
        )
        val docRef = db.collection(COLLECTION_NAME).document(userId)

        // read (may be served from cache if offline)
        val snapshot = docRef.get().await()
        val datasets = casting(snapshot.get("datasets")) ?: emptyList()

        val mutableDatasets = datasets.toMutableList()
        val idx = mutableDatasets.indexOfFirst { (it["id"] as? String) == datasetId }

        if (idx == -1) {
            throw IllegalArgumentException("Dataset $datasetId not found for user $userId")
        }

        // mutate target dataset's items
        val datasetMap = mutableDatasets[idx].toMutableMap()
        val items =
            (casting(datasetMap["adjustment"]) ?: emptyList()).toMutableList()
        items.removeAll { (it["adjustmentId"] as? String) == adjustment.adjustmentId }
        datasetMap["adjustment"] = items
        mutableDatasets[idx] = datasetMap

        // write whole datasets array back (queued when offline)
        docRef.update("datasets", mutableDatasets).await()
    }

    override suspend fun updateAdjustmentDataset(
        userId: String,
        datasetId: String,
        oldAdjustment: Adjustment,
        newAdjustment: Adjustment
    ) {
        Log.d(
            "DataStorageImpl",
            "removeAdjustmentDataset called: $oldAdjustment for datasetId=$datasetId userId=$userId"
        )
        val docRef = db.collection(COLLECTION_NAME).document(userId)

        // read (maybe served from cache if offline)
        val snapshot = docRef.get().await()
        val datasets = casting(snapshot.get("datasets")) ?: emptyList()

        val mutableDatasets = datasets.toMutableList()
        val idx = mutableDatasets.indexOfFirst { (it["id"] as? String) == datasetId }

        if (idx == -1) {
            throw IllegalArgumentException("Dataset $datasetId not found for user $userId")
        }

        // mutate target dataset's items
        val datasetMap = mutableDatasets[idx].toMutableMap()
        val items =
            (casting(datasetMap["adjustment"]) ?: emptyList()).toMutableList()
        items.removeAll { (it["adjustmentId"] as? String) == oldAdjustment.adjustmentId }
        val wasUpdated = items.add(newAdjustment.adjustmentToMap)
        if (wasUpdated) {
            Log.d(
                "Adjustment update",
                "${oldAdjustment.dataset?.label} = ${oldAdjustment.adjustmentId}"
            )
        } else {
            Log.d("Adjustment update", "Failed to update")
        }

        datasetMap["adjustment"] = items
        mutableDatasets[idx] = datasetMap

        // write whole datasets array back (queued when offline)
        docRef.update("datasets", mutableDatasets).await()
    }

    companion object {
        const val COLLECTION_NAME = "database"
    }
}