package com.example.moneytracker.backend.storage

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class DataStorageImpl(
    override val db: FirebaseFirestore
) : DataStorage {

    override suspend fun getWholeDatasets(userId: String): Flow<List<Dataset>> = callbackFlow {
        val documentRef = db.collection(COLLECTION_NAME)
            .document(userId)

        val listenerRegistration = documentRef.addSnapshotListener { snapshot, error ->
            if (error != null && snapshot == null) {
                Log.e("DataStorageImpl", "getWholeDatasets listener error", error)
                close(error) // Close the flow with an exception
                return@addSnapshotListener
            }

            try {
                val raw = snapshot?.get("datasets")
                Log.d("DataStorageImpl", "raw datasets field: $raw")
                if (raw == null) {
                    // No datasets field yet - emit empty list
                    trySend(emptyList())
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
            } catch (e: Exception) {
                Log.e("DataStorageImpl", "Unhandled error while reading datasets", e)
                // Don't close the flow on parse errors; send an empty list instead to keep collecting
                trySend(emptyList())
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

    companion object {
        const val COLLECTION_NAME = "database"
    }
}