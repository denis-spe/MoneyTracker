package com.example.moneytracker.backend.storage

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.random.Random

class DataStorageImpl(
    override val db: FirebaseFirestore
) : DataStorage {

    @RequiresApi(Build.VERSION_CODES.O)
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
                val color = when (val infoMap = infoField) {
                    is Map<*, *> -> {
                        // Firestore may store numbers as Long - use safe cast
                        (infoMap["color"] as? Number)?.toInt() ?: 0
                    }

                    is Number -> infoMap.toInt()
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
    override fun addData(userId: String, dataset: Dataset) {
        Log.d(
            "DataStorageImpl",
            "addData user=$userId dataset.id=${dataset.id} label=${dataset.label}"
        )
        db.collection(COLLECTION_NAME)
            .document(userId)
            .update("datasets", FieldValue.arrayUnion(dataset))
            .addOnSuccessListener {
                Log.d("Firestore", "Successfully wrote data for user $userId")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to write user data for $userId", it)
            }
    }

    private fun repayToMap(repay: Repay): Map<String, Any?> = mapOf(
        "amount" to repay.amount,
        "label" to repay.label,
        "description" to repay.description,
        "dateTime" to repay.dateTime,
        "repayIcon" to repay.repayIcon
    )

    override suspend fun addRepayToDataset(
        userId: String,
        datasetId: String,
        repay: Repay
    ) {
        Log.d(
            "DataStorageImpl",
            "addRepayToDataset called: $repay for datasetId=$datasetId userId=$userId"
        )
        val docRef = db.collection(COLLECTION_NAME).document(userId)

        try {
            db.runTransaction { tx ->
                val snap = tx.get(docRef)
                if (!snap.exists()) {
                    Log.w("DataStorageImpl", "User document does not exist: $userId")
                    return@runTransaction null
                }

                val list: MutableList<Any?> = when (val rawDatasets = snap.get("datasets")) {
                    is List<*> -> rawDatasets.map { it as Any? }.toMutableList()
                    null -> mutableListOf()
                    else -> mutableListOf(rawDatasets as Any?)
                }

                // Find the dataset map by matching its stored 'id' field to datasetId
                val idx = list.indexOfFirst { item ->
                    (item as? Map<*, *>)?.get("id")?.toString() == datasetId
                }

                if (idx == -1) {
                    Log.w(
                        "DataStorageImpl",
                        "Could not find dataset with id $datasetId in user's datasets"
                    )
                    return@runTransaction null
                }

                // Safely create a mutable map with String keys for the dataset
                val datasetMap: MutableMap<String, Any?> = when (val existing = list[idx]) {
                    is Map<*, *> -> existing.entries.associate { (k, v) -> k.toString() to v }
                        .toMutableMap()

                    else -> mutableMapOf()
                }

                // Ensure repay list exists and is a mutable list we can append to
                val currentRepay =
                    (datasetMap["repay"] as? List<*>)?.map { it as Any? }?.toMutableList()
                        ?: mutableListOf()
                currentRepay.add(repayToMap(repay))

                // Put updated repay back into the dataset map
                datasetMap["repay"] = currentRepay

                // Replace the dataset in the list with updated map
                list[idx] = datasetMap

                // Write the updated datasets array back
                tx.update(docRef, "datasets", list)
                null
            }.await()

            Log.d(
                "DataStorageImpl",
                "addRepayToDataset transaction committed for datasetId=$datasetId"
            )
        } catch (e: Exception) {
            Log.e("DataStorageImpl", "Failed to add repay to dataset", e)
            throw e
        }
    }

    override suspend fun ensureDatasetIds(userId: String) {
        val docRef = db.collection(COLLECTION_NAME).document(userId)
        try {
            db.runTransaction { tx ->
                val snap = tx.get(docRef)
                if (!snap.exists()) return@runTransaction null

                val list: MutableList<Any?> = when (val rawDatasets = snap.get("datasets")) {
                    is List<*> -> rawDatasets.map { it as Any? }.toMutableList()
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

    companion object {
        const val COLLECTION_NAME = "database"
    }
}