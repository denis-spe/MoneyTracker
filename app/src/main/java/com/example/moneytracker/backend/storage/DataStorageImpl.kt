// Glory be the name of LORD our GOD
package com.example.moneytracker.backend.storage

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.moneytracker.helper.adjustmentToMap
import com.example.moneytracker.helper.castToMutableMap
import com.example.moneytracker.helper.statusHistoryToMap
import com.example.moneytracker.helper.toDataset
import com.example.moneytracker.helper.toMap
import com.google.firebase.Timestamp
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
        val datasetsRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection("datasets")

        val listenerRegistration = datasetsRef.addSnapshotListener { snapshot, error ->
            if (error != null && snapshot == null) {
                Log.e("DataStorageImpl", "getWholeDatasets listener error", error)
                onFailure(error)
                close(error)
                return@addSnapshotListener
            }

            try {
                val data = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.data?.toDataset()
                    } catch (e: Exception) {
                        Log.e("DataStorageImpl", "Failed to parse dataset item", e)
                        null
                    }
                } ?: emptyList()

                Log.d("DataStorageImpl", "parsed datasets count: ${data.size}")
                trySend(data)
                onSuccess(true)
            } catch (e: Exception) {
                Log.e("DataStorageImpl", "Unhandled error while reading datasets", e)
                trySend(emptyList())
                onFailure(e)
            }
        }
        awaitClose { listenerRegistration.remove() }
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
     * Add a dataset to the storage
     */
    override fun addData(userId: String, dataset: Dataset): String {
        Log.d(
            "DataStorageImpl",
            "addData user=$userId dataset.id=${dataset.id} label=${dataset.label}"
        )

        val datasetId = dataset.id.ifEmpty { UUID.randomUUID().toString() }
        val updatedDataset = if (dataset.id.isEmpty()) dataset.copy(id = datasetId) else dataset

        db.collection(COLLECTION_NAME)
            .document(userId)
            .collection("datasets")
            .document(datasetId)
            .set(updatedDataset.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "Successfully wrote dataset $datasetId for user $userId")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to write dataset for $userId", it)
            }

        return datasetId
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

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection("datasets")
            .document(oldDataset.id)

        try {
            // Modify new dataset
            val modifyNewDataset = newDataset.copy(
                adjustment = newDataset.adjustment.map { adjustment ->
                    adjustment.copy(
                        tagIcon = newDataset.tagIcon,
                        label = adjustment.label,
                    )
                }
            )

            // Simply update the document
            docRef.set(modifyNewDataset.toMap()).await()
            Log.d("Dataset update", "Updated dataset ${oldDataset.id}")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to update dataset", e)
        }
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

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection("datasets")
            .document(datasetId)

        try {
            val snapshot = docRef.get().await()
            val dataset = snapshot.data?.toDataset() ?: return

            // Add new adjustment to list
            val updatedAdjustments = dataset.adjustment.toMutableList()
            updatedAdjustments.add(
                adjustment.copy(
                    adjustmentId = adjustment.adjustmentId.ifEmpty { UUID.randomUUID().toString() }
                )
            )

            // Update only the adjustment array
            docRef.update("adjustment", updatedAdjustments.map { it.adjustmentToMap }).await()
            Log.d("DataStorageImpl", "Added adjustment to dataset $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to add adjustment", e)
            throw e
        }
    }


    override suspend fun stopRoutine(
        userId: String,
        datasetId: String,
    ) {
        Log.d(
            "DataStorageImpl",
            "stopRoutine called for datasetId=$datasetId userId=$userId"
        )

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection("datasets")
            .document(datasetId)

        try {
            val snapshot = docRef.get().await()
            val dataset = snapshot.data?.toDataset() ?: return

            // Update routine field
            val routineMap = castToMutableMap(mapOf("routine" to dataset.routine))
            routineMap["stopRoutine"] = true

            docRef.update("routine", routineMap).await()
            Log.d("DataStorageImpl", "Stopped routine for dataset $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to stop routine", e)
        }
    }

    /**
     * Get a dataset from the storage
     * @param userId the user id
     * @param datasetId the dataset id
     */
    override suspend fun getDataset(userId: String, datasetId: String): Dataset? =
        suspendCancellableCoroutine { cont ->
            val docRef = db.collection(COLLECTION_NAME)
                .document(userId)
                .collection("datasets")
                .document(datasetId)

            val task = docRef.get()
            task.addOnSuccessListener { snapshot ->
                try {
                    val dataset = snapshot.data?.toDataset()
                    if (!cont.isCompleted) cont.resume(dataset) { cause, _, _ -> }
                } catch (e: Exception) {
                    if (!cont.isCompleted) cont.resumeWithException(e)
                }
            }
            task.addOnFailureListener { exc ->
                if (!cont.isCompleted) cont.resumeWithException(exc)
            }
        }

    override suspend fun completeRoutine(
        userId: String,
        datasetId: String,
        newDateTime: Timestamp,
        nextDeadline: Timestamp
    ) {
        Log.d("DataStorageImpl", "completeRoutine (Offline-Safe) for $datasetId")

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection("datasets")
            .document(datasetId)

        try {
            val snapshot = docRef.get().await()
            val dataset = snapshot.data?.toDataset() ?: return

            // Calculate status and total adjustment amount
            val totalAdjustmentAmount = dataset.adjustment.sumOf { it.amount }
            val remainingAmount = dataset.amount - totalAdjustmentAmount
            val status = if (remainingAmount == 0.0) Status.SUCCESS else Status.OVERDUE

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
                .await()

            // Update dataset fields
            val routineMap = castToMutableMap(mapOf("routine" to dataset.routine))
            routineMap["triggerMillis"] =
                nextDeadline.seconds * 1000 + nextDeadline.nanoseconds / 1_000_000

            docRef.update(
                mapOf(
                    "dateTime" to newDateTime,
                    "deadlineDateTime" to nextDeadline,
                    "routine" to routineMap,
                    "adjustment" to emptyList<Map<String, Any?>>()
                )
            ).await()

            Log.d("DataStorageImpl", "completeRoutine: Update successful for $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Error in completeRoutine for $datasetId", e)
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

        val datasetDocRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection("datasets")
            .document(datasetId)

        try {
            val snapshot = datasetDocRef.get().await()
            val dataset = snapshot.data?.toDataset() ?: return

            val totalAdjustmentAmount = dataset.adjustment.sumOf { it.amount }
            val remainingAmount = dataset.amount - totalAdjustmentAmount
            val status = if (remainingAmount == 0.0) Status.SUCCESS else Status.OVERDUE

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
                .await()

            // Update dataset timestamps
            datasetDocRef.update(
                mapOf(
                    "dateTime" to newDateTime,
                    "deadlineDateTime" to newDeadlineDateTime
                )
            ).await()

            Log.d("addStatus", "Status added and dataset updated for $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to add status", e)
        }
    }

    override suspend fun clearAdjustmentList(userId: String, datasetId: String) {
        Log.d(
            "clearAdjustmentList",
            "clearAdjustmentList called: clear adjustment for datasetId=$datasetId userId=$userId"
        )

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection("datasets")
            .document(datasetId)

        try {
            docRef.update("adjustment", emptyList<Map<String, Any?>>()).await()
            Log.d("clearAdjustmentList", "Adjustment list cleared for dataset $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to clear adjustment list", e)
        }
    }

    override suspend fun ensureDatasetIds(userId: String) {
        Log.d("DataStorageImpl", "ensureDatasetIds called for user=$userId")

        try {
            val datasetsRef = db.collection(COLLECTION_NAME)
                .document(userId)
                .collection("datasets")

            val snapshot = datasetsRef.get().await()
            var updated = 0

            for (doc in snapshot.documents) {
                val id = doc.get("id") as? String
                if (id == null || id.isEmpty()) {
                    val newId = UUID.randomUUID().toString()
                    doc.reference.update("id", newId).await()
                    updated++
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

    override suspend fun removeDataset(userId: String, dataset: Dataset) {
        val id = dataset.id

        Log.d("DataStorageImpl", "removeDataset called for datasetId=$id userId=$userId")

        try {
            val docRef = db.collection(COLLECTION_NAME)
                .document(userId)
                .collection("datasets")
                .document(id)

            // First delete all status history documents in the subcollection
            val statusHistorySnapshot = docRef.collection("statusHistory").get().await()
            for (doc in statusHistorySnapshot.documents) {
                doc.reference.delete().await()
            }
            Log.d(
                "DataStorageImpl",
                "Deleted ${statusHistorySnapshot.documents.size} status history entries"
            )

            // Then delete the dataset document itself
            docRef.delete().await()
            Log.d("DataStorageImpl", "Dataset $id deleted successfully")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to remove dataset", e)
            throw e
        }
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

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection("datasets")
            .document(datasetId)

        try {
            val snapshot = docRef.get().await()
            val dataset = snapshot.data?.toDataset() ?: return

            // Remove adjustment from list
            val updatedAdjustments = dataset.adjustment.filter {
                it.adjustmentId != adjustment.adjustmentId
            }

            docRef.update("adjustment", updatedAdjustments.map { it.adjustmentToMap }).await()
            Log.d("DataStorageImpl", "Removed adjustment from dataset $datasetId")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to remove adjustment", e)
            throw e
        }
    }

    override suspend fun updateAdjustmentDataset(
        userId: String,
        datasetId: String,
        oldAdjustment: Adjustment,
        newAdjustment: Adjustment
    ) {
        Log.d(
            "DataStorageImpl",
            "updateAdjustmentDataset called: $oldAdjustment for datasetId=$datasetId userId=$userId"
        )

        val docRef = db.collection(COLLECTION_NAME)
            .document(userId)
            .collection("datasets")
            .document(datasetId)

        try {
            val snapshot = docRef.get().await()
            val dataset = snapshot.data?.toDataset() ?: return

            // Remove old adjustment and add new one
            val updatedAdjustments = dataset.adjustment.filter {
                it.adjustmentId != oldAdjustment.adjustmentId
            }.toMutableList()

            updatedAdjustments.add(newAdjustment)

            docRef.update("adjustment", updatedAdjustments.map { it.adjustmentToMap }).await()
            Log.d("Adjustment update", "Updated adjustment ${oldAdjustment.adjustmentId}")
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to update adjustment", e)
            throw e
        }
    }

    companion object {
        const val COLLECTION_NAME = "database"
    }
}