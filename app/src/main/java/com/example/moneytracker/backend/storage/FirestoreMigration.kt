package com.example.moneytracker.backend.storage

import android.util.Log
import com.example.moneytracker.helper.asSettlement
import com.example.moneytracker.helper.settlementToMap
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

                // 1. Copy document (without the settlement list field)
                val mutableData = data.toMutableMap()
                val settlementsRaw = mutableData.remove("settlement") as? List<*>
                targetRef.set(mutableData).await()

                // 2. Migrate settlements from list to subcollection
                settlementsRaw?.forEach { adjMap ->
                    if (adjMap is Map<*, *>) {
                        val adj = adjMap.asSettlement()
                        val finalAdj = adj.copy(userId = userId, datasetId = id)
                        targetRef.collection("settlement")
                            .document(finalAdj.settlementId)
                            .set(finalAdj.settlementToMap)
                            .await()
                    }
                }

                // 3. Copy achievement subcollection if exists
                val historySnapshot = doc.reference.collection("achievement").get().await()
                for (historyDoc in historySnapshot.documents) {
                    targetRef.collection("achievement")
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

    suspend fun migrateSettlementsOnly(db: FirebaseFirestore, userId: String) {
        val collections = listOf("Goal", "Liability")
        for (collection in collections) {
            val ref = db.collection("database").document(userId).collection(collection)
            try {
                val snapshot = ref.get().await()
                for (doc in snapshot.documents) {
                    val data = doc.data ?: continue
                    val settlementsRaw = data["settlement"] as? List<*> ?: continue

                    Log.d(TAG, "Migrating settlements for document ${doc.id} in $collection")

                    // Migrate to subcollection
                    settlementsRaw.forEach { adjObj ->
                        if (adjObj is Map<*, *>) {
                            val adj = adjObj.asSettlement()
                            val finalAdj = adj.copy(userId = userId, datasetId = doc.id)
                            doc.reference.collection("settlement")
                                .document(finalAdj.settlementId)
                                .set(finalAdj.settlementToMap)
                                .await()
                        }
                    }

                    // Remove list field
                    doc.reference.update(
                        "settlement",
                        com.google.firebase.firestore.FieldValue.delete()
                    ).await()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to migrate settlements only for $collection", e)
            }
        }
    }
}
