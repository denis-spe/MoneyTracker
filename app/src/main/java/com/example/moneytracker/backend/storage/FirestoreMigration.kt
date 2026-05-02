package com.example.moneytracker.backend.storage

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreMigration {
    private const val TAG = "FirestoreMigration"
    private const val OLD_COLLECTION = "datasets"

    suspend fun migrateUserDatasets(db: FirebaseFirestore, userId: String) {
        Log.d(TAG, "Starting migration for user: $userId")

        val oldRef = db.collection("database")
            .document(userId)
            .collection(OLD_COLLECTION)

        try {
            val snapshot = oldRef.get().await()
            if (snapshot.isEmpty) {
                Log.d(TAG, "No datasets to migrate for user: $userId")
                return
            }

            for (doc in snapshot.documents) {
                val data = doc.data ?: continue
                val id = doc.id

                // Determine target collection
                val financeType = data["financeType"] as? String
                val dataTypeStr = data["dataType"] as? String

                val targetCollection = when {
                    financeType == "TRANSACTION" || (dataTypeStr in listOf(
                        "EARNINGS",
                        "EXPENSE",
                        "SAVINGS"
                    )) -> "Transaction"

                    financeType == "GOAL" || dataTypeStr == "GOAL" -> "Goal"
                    financeType == "LIABILITY" || (dataTypeStr in listOf(
                        "DEBT",
                        "LENT"
                    )) -> "Liability"

                    else -> "Transaction"
                }

                Log.d(TAG, "Migrating document $id to $targetCollection")

                val targetRef = db.collection("database")
                    .document(userId)
                    .collection(targetCollection)
                    .document(id)

                // 1. Copy document
                targetRef.set(data).await()

                // 2. Copy statusHistory subcollection if exists
                val historySnapshot = doc.reference.collection("statusHistory").get().await()
                for (historyDoc in historySnapshot.documents) {
                    targetRef.collection("statusHistory")
                        .document(historyDoc.id)
                        .set(historyDoc.data!!)
                        .await()
                }

                // 3. Delete old document and its subcollection
                // Note: Firestore doesn't delete subcollections automatically when deleting parent doc
                for (historyDoc in historySnapshot.documents) {
                    historyDoc.reference.delete().await()
                }
                doc.reference.delete().await()
            }
            Log.d(TAG, "Migration completed for user: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Migration failed for user: $userId", e)
        }
    }
}
