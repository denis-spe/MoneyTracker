package com.example.moneytracker.backend.storage

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DataStorageImpl(
    override val db: FirebaseFirestore
) : DataStorage {

    override suspend fun getWholeDatasets(userId: String): Flow<List<Dataset>> = callbackFlow {
        val documentRef = db.collection(COLLECTION_NAME)
            .document(userId)

        val listenerRegistration = documentRef.addSnapshotListener { snapshot, error ->
            if (error != null && snapshot == null) {
                close(error) // Close the flow with an exception
                return@addSnapshotListener
            }
            // Use toObject() for automatic data class conversion
            try {
                var data = snapshot?.get("datasets") as List<*>
                data = data.map { it as Map<*, *> }.map { it.toDataset() }
                trySend(data) // Emit the new data
            } catch (e: Exception) {
                close(e)
            }
        }
        awaitClose { listenerRegistration.remove() } // Unregister the listener when the flow is cancelled
    }


    /**
     * Create a new user with a given id
     */
    override suspend fun createUserWithId(id: String) {
        val data = hashMapOf(
            "datasets" to listOf<Dataset>()
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