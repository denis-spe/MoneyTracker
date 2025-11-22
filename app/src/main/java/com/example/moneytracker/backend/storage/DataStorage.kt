package com.example.moneytracker.backend.storage

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

interface DataStorage {

    /**
     * Firebase store instance
     */
    val db: FirebaseFirestore

    /**
     * Get all datasets
     */
//    val getWholeDatasets: State<List<Dataset>>

    /**
     * Create a new user with a given id
     */
    suspend fun createUserWithId(id: String)

    /**
     * Add a dataset to the storage
     * @param userId the user id
     * @param dataset the dataset to add
     */
    fun addData(userId: String, dataset: Dataset)

    /**
     * Get all datasets
     * @param userId the user id
     */
    suspend fun getWholeDatasets(userId: String): Flow<List<Dataset>>
}