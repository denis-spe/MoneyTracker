package com.example.moneytracker.backend.storage

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DataStorageImpl(
    override val db: FirebaseFirestore
) : DataStorage {

    suspend fun getWholeDatasets(useId: String) {
        db.collection(COLLECTION_NAME)
            .document(useId)
            .get()
            .await()
            .data
            ?.map { it.value as Dataset }
    }

    /**
     * Create a new user with a given id
     */
    override suspend fun createUserWithId(id: String) {
        val data = hashMapOf(
            "datasets" to listOf<Dataset>()
        )

        db.collection(COLLECTION_NAME)
            .document(id)
            .set(data)
            .await()
    }

    /**
     * Add a dataset to the storage
     */
    override fun addData(userId: String, dataset: Dataset) {
        db.collection(COLLECTION_NAME)
            .document(userId)
            .set(dataset)
    }

    companion object {
        const val COLLECTION_NAME = "database"
    }
}