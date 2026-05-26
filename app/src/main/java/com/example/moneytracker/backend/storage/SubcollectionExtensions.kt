// Glory to the LORD our GOD
package com.example.moneytracker.backend.storage

import android.util.Log
import com.example.moneytracker.helper.asSettlement
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

private fun getCollectionNameFromType(type: String): String {
    return when (type.uppercase()) {
        "TRANSACTION" -> "Transaction"
        "GOAL" -> "Goal"
        "LIABILITY" -> "Liability"
        else -> "Transaction"
    }
}

/**
 * Load status history for a specific dataset from the achievement subcollection
 * @param db FirebaseFirestore instance
 * @param userId the user id
 * @param datasetId the dataset id
 * @param financeType the record type
 * @return List of Achievement ordered by dateTime (newest first)
 */
suspend fun loadAchievementForDataset(
    db: FirebaseFirestore,
    userId: String,
    datasetId: String,
    financeType: String,
    source: com.google.firebase.firestore.Source = com.google.firebase.firestore.Source.DEFAULT
): List<Achievement> {
    return try {
        val snapshot = db.collection("database")
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)
            .collection("achievement")
            .orderBy("startDateTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get(source)
            .await()

        snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data ?: return@mapNotNull null
                Achievement(
                    status = (data["status"] as? String) ?: "",
                    totalSettlementAmount = (data["totalSettlementAmount"] as? Number)?.toDouble()
                        ?: 0.0,
                    startDateTime = (data["startDateTime"] as? Timestamp) ?: Timestamp.now(),
                    deadlineDateTime = (data["deadlineDateTime"] as? Timestamp) ?: Timestamp.now()
                )
            } catch (e: Exception) {
                Log.e("AchievementLoad", "Failed to parse status history entry", e)
                null
            }
        }
    } catch (e: Exception) {
        Log.e("AchievementLoad", "Failed to load status history", e)
        emptyList()
    }
}

/**
 * Listen to real-time status history updates for a specific dataset
 * @param db FirebaseFirestore instance
 * @param userId the user id
 * @param datasetId the dataset id
 * @param financeType the record type
 * @return Flow of status history lists (automatically updated when changes occur)
 */
fun listenToAchievement(
    db: FirebaseFirestore,
    userId: String,
    datasetId: String,
    financeType: String
): Flow<List<Achievement>> = callbackFlow {
    val registration = db.collection("database")
        .document(userId)
        .collection(getCollectionNameFromType(financeType))
        .document(datasetId)
        .collection("achievement")
        .orderBy("startDateTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("AchievementListener", "Status history listener error", error)
                close(error)
                return@addSnapshotListener
            }

            try {
                val statusList = snapshot?.documents?.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    try {
                        Achievement(
                            status = (data["status"] as? String) ?: "",
                            totalSettlementAmount = (data["totalSettlementAmount"] as? Number)?.toDouble()
                                ?: 0.0,
                            startDateTime = (data["startDateTime"] as? Timestamp)
                                ?: Timestamp.now(),
                            deadlineDateTime = (data["deadlineDateTime"] as? Timestamp)
                                ?: Timestamp.now()
                        )
                    } catch (e: Exception) {
                        Log.e("AchievementListener", "Failed to parse status entry", e)
                        null
                    }
                } ?: emptyList()

                trySend(statusList)
            } catch (e: Exception) {
                Log.e("AchievementListener", "Error processing status history", e)
            }
        }

    awaitClose { registration.remove() }
}

/**
 * Get the latest status for a dataset
 * @param db FirebaseFirestore instance
 * @param userId the user id
 * @param datasetId the dataset id
 * @param financeType the record type
 * @return The most recent Achievement or null if none exist
 */
suspend fun getLatestAchievementForDataset(
    db: FirebaseFirestore,
    userId: String,
    datasetId: String,
    financeType: String
): Achievement? {
    return try {
        val snapshot = db.collection("database")
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)
            .collection("achievement")
            .orderBy("startDateTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()

        if (snapshot.documents.isEmpty()) {
            return null
        }

        val doc = snapshot.documents.first()
        val data = doc.data ?: return null

        Achievement(
            status = (data["status"] as? String) ?: "",
            totalSettlementAmount = (data["totalSettlementAmount"] as? Number)?.toDouble() ?: 0.0,
            startDateTime = (data["startDateTime"] as? Timestamp) ?: Timestamp.now(),
            deadlineDateTime = (data["deadlineDateTime"] as? Timestamp) ?: Timestamp.now()
        )
    } catch (e: Exception) {
        Log.e("LatestStatusLoad", "Failed to load latest status", e)
        null
    }
}

/**
 * Load settlements for a specific dataset
 */
suspend fun loadSettlementForDataset(
    db: FirebaseFirestore,
    userId: String,
    datasetId: String,
    financeType: String,
    source: com.google.firebase.firestore.Source = com.google.firebase.firestore.Source.DEFAULT
): List<Settlement> {
    return try {
        val snapshot = db.collection("database")
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)
            .collection("settlement")
            .get(source)
            .await()

        snapshot.documents.mapNotNull { doc ->
            try {
                (doc.data ?: return@mapNotNull null).asSettlement()
            } catch (e: Exception) {
                Log.e("SettlementLoad", "Failed to parse settlement entry", e)
                null
            }
        }
    } catch (e: Exception) {
        Log.e("SettlementLoad", "Failed to load settlements", e)
        emptyList()
    }
}

/**
 * Count the number of status history entries for a dataset
 * Useful for analytics or pagination
 */
suspend fun countAchievementForDataset(
    db: FirebaseFirestore,
    userId: String,
    datasetId: String,
    financeType: String
): Long {
    return try {
        val snapshot = db.collection("database")
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)
            .collection("achievement")
            .count()
            .get(com.google.firebase.firestore.AggregateSource.SERVER)
            .await()

        snapshot.count
    } catch (e: Exception) {
        Log.e("AchievementCount", "Failed to count status history", e)
        0L
    }
}

/**
 * Delete old status history entries (older than specified timestamp)
 * Useful for cleanup and reducing Firestore storage
 */
suspend fun deleteOldAchievement(
    db: FirebaseFirestore,
    userId: String,
    datasetId: String,
    financeType: String,
    beforeTimestamp: Timestamp
) {
    try {
        val snapshot = db.collection("database")
            .document(userId)
            .collection(getCollectionNameFromType(financeType))
            .document(datasetId)
            .collection("achievement")
            .whereLessThan("startDateTime", beforeTimestamp)
            .get()
            .await()

        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }

        Log.d("AchievementCleanup", "Deleted ${snapshot.documents.size} old status entries")
    } catch (e: Exception) {
        Log.e("AchievementCleanup", "Failed to delete old status history", e)
    }
}

