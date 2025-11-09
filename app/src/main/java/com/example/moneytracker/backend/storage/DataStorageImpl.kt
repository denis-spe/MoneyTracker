package com.example.moneytracker.backend.storage

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DataStorageImpl(
    override val db: FirebaseFirestore
) : DataStorage {

    suspend fun getWholeDatasets(useId: String) {
        val data = db.collection(COLLECTION_NAME)
            .document(useId)
            .get()
            .await()
            .get("datasets")
            .let { it as List<*> }
            .map { it as Dataset }
    }

    /**
     * Create a new user with a given id
     */
    override suspend fun createUserWithId(id: String) {
        val data = hashMapOf(
            "datasets" to listOf<Dataset>()
        )

        try {
            db.collection(COLLECTION_NAME)
                .document(id)
                .set(data)
                .await()
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