// Glory to the LORD our GOD
package com.example.moneytracker.backend.storage

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Extension functions for DataStorageImpl to handle loading data from subcollections
 * These methods are optional helpers for advanced use cases
 */

/**
 * Load status history for a specific dataset from the statusHistory subcollection
 * @param db FirebaseFirestore instance
 * @param userId the user id
 * @param datasetId the dataset id
 * @return List of StatusHistory ordered by dateTime (newest first)
 */
suspend fun loadStatusHistoryForDataset(
    db: FirebaseFirestore,
    userId: String,
    datasetId: String
): List<StatusHistory> {
    return try {
        val snapshot = db.collection("database")
            .document(userId)
            .collection("datasets")
            .document(datasetId)
            .collection("statusHistory")
            .orderBy("startDateTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()

        snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data ?: return@mapNotNull null
                StatusHistory(
                    status = (data["status"] as? String) ?: "",
                    totalAdjustmentAmount = (data["totalAdjustmentAmount"] as? Number)?.toDouble()
                        ?: 0.0,
                    startDateTime = (data["startDateTime"] as? Timestamp) ?: Timestamp.now(),
                    deadlineDateTime = (data["deadlineDateTime"] as? Timestamp) ?: Timestamp.now()
                )
            } catch (e: Exception) {
                Log.e("StatusHistoryLoad", "Failed to parse status history entry", e)
                null
            }
        }
    } catch (e: Exception) {
        Log.e("StatusHistoryLoad", "Failed to load status history", e)
        emptyList()
    }
}

/**
 * Listen to real-time status history updates for a specific dataset
 * @param db FirebaseFirestore instance
 * @param userId the user id
 * @param datasetId the dataset id
 * @return Flow of status history lists (automatically updated when changes occur)
 */
fun listenToStatusHistory(
    db: FirebaseFirestore,
    userId: String,
    datasetId: String
): Flow<List<StatusHistory>> = callbackFlow {
    val registration = db.collection("database")
        .document(userId)
        .collection("datasets")
        .document(datasetId)
        .collection("statusHistory")
        .orderBy("startDateTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("StatusHistoryListener", "Status history listener error", error)
                close(error)
                return@addSnapshotListener
            }

            try {
                val statusList = snapshot?.documents?.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    try {
                        StatusHistory(
                            status = (data["status"] as? String) ?: "",
                            totalAdjustmentAmount = (data["totalAdjustmentAmount"] as? Number)?.toDouble()
                                ?: 0.0,
                            startDateTime = (data["startDateTime"] as? Timestamp)
                                ?: Timestamp.now(),
                            deadlineDateTime = (data["deadlineDateTime"] as? Timestamp)
                                ?: Timestamp.now()
                        )
                    } catch (e: Exception) {
                        Log.e("StatusHistoryListener", "Failed to parse status entry", e)
                        null
                    }
                } ?: emptyList()

                trySend(statusList)
            } catch (e: Exception) {
                Log.e("StatusHistoryListener", "Error processing status history", e)
            }
        }

    awaitClose { registration.remove() }
}

/**
 * Get the latest status for a dataset
 * @param db FirebaseFirestore instance
 * @param userId the user id
 * @param datasetId the dataset id
 * @return The most recent StatusHistory or null if none exist
 */
suspend fun getLatestStatusForDataset(
    db: FirebaseFirestore,
    userId: String,
    datasetId: String
): StatusHistory? {
    return try {
        val snapshot = db.collection("database")
            .document(userId)
            .collection("datasets")
            .document(datasetId)
            .collection("statusHistory")
            .orderBy("startDateTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()

        if (snapshot.documents.isEmpty()) {
            return null
        }

        val doc = snapshot.documents.first()
        val data = doc.data ?: return null

        StatusHistory(
            status = (data["status"] as? String) ?: "",
            totalAdjustmentAmount = (data["totalAdjustmentAmount"] as? Number)?.toDouble() ?: 0.0,
            startDateTime = (data["startDateTime"] as? Timestamp) ?: Timestamp.now(),
            deadlineDateTime = (data["deadlineDateTime"] as? Timestamp) ?: Timestamp.now()
        )
    } catch (e: Exception) {
        Log.e("LatestStatusLoad", "Failed to load latest status", e)
        null
    }
}

/**
 * Count the number of status history entries for a dataset
 * Useful for analytics or pagination
 */
suspend fun countStatusHistoryForDataset(
    db: FirebaseFirestore,
    userId: String,
    datasetId: String
): Long {
    return try {
        val snapshot = db.collection("database")
            .document(userId)
            .collection("datasets")
            .document(datasetId)
            .collection("statusHistory")
            .count()
            .get(com.google.firebase.firestore.AggregateSource.SERVER)
            .await()

        snapshot.count
    } catch (e: Exception) {
        Log.e("StatusHistoryCount", "Failed to count status history", e)
        0L
    }
}

/**
 * Delete old status history entries (older than specified timestamp)
 * Useful for cleanup and reducing Firestore storage
 */
suspend fun deleteOldStatusHistory(
    db: FirebaseFirestore,
    userId: String,
    datasetId: String,
    beforeTimestamp: Timestamp
) {
    try {
        val snapshot = db.collection("database")
            .document(userId)
            .collection("datasets")
            .document(datasetId)
            .collection("statusHistory")
            .whereLessThan("startDateTime", beforeTimestamp)
            .get()
            .await()

        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }

        Log.d("StatusHistoryCleanup", "Deleted ${snapshot.documents.size} old status entries")
    } catch (e: Exception) {
        Log.e("StatusHistoryCleanup", "Failed to delete old status history", e)
    }
}

