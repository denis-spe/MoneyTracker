package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

interface DataStorage {

    /**
     * Firebase store instance
     */
    val db: FirebaseFirestore

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
    suspend fun getWholeDatasets(
        userId: String,
        onSuccess: (isSuccess: Boolean) -> Unit,
        onFailure: (error: Throwable?) -> Unit
    ): Flow<List<Dataset>>


    suspend fun getInfo(userId: String): Flow<Info>

    /**
     * Add a repay entry into a dataset (identified by its id) for the given user.
     * Implementations should perform this atomically (transaction) to avoid lost updates.
     */
    suspend fun addAdjustmentDataset(userId: String, datasetId: String, adjustment: Adjustment)

    /**
     * Ensure every dataset stored for the user has a non-null id; assign UUIDs to missing ones.
     * This is a migration helper to avoid dataset.id being null for older documents.
     */
    suspend fun ensureDatasetIds(userId: String)

    /**
     * Remove a dataset from the storage
     * @param userId the user id
     * @param dataset the dataset to remove
     */
    suspend fun removeDataset(userId: String, dataset: Dataset)

    /**
     * Remove adjusted dataset from the storage
     * @param userId the user id
     * @param datasetId the dataset id
     * @param adjustment the adjustment
     */
    suspend fun removeAdjustmentDataset(userId: String, datasetId: String, adjustment: Adjustment)

    /**
     * Update a dataset to the storage
     * @param userId the user id
     * @param oldDataset the dataset to update
     * @param newDataset the dataset to update the old one
     */
    suspend fun updateDataset(
        userId: String,
        oldDataset: Dataset,
        newDataset: Dataset
    )

    /**
     * Update a dataset to the storage
     * @param userId the user id
     * @param datasetId of the dataset
     * @param oldAdjustment to update with the new one
     * @param newAdjustment to update the old one
     */
    suspend fun updateAdjustmentDataset(
        userId: String,
        datasetId: String,
        oldAdjustment: Adjustment,
        newAdjustment: Adjustment
    )

    /**
     * Add a status into a list in dataset (identified by its id) for the given user.
     * Implementations should perform this atomically (transaction) to avoid lost updates.
     * @param userId the user id
     * @param datasetId the dataset id
     */
    suspend fun addStatus(
        userId: String,
        datasetId: String,
        newDateTime: Timestamp,
        newDeadlineDateTime: Timestamp
    )

    /**
     * Clear a list in dataset (identified by its id) for the given user.
     * Implementations should perform this atomically (transaction) to avoid lost updates.
     * @param userId the user id
     * @param datasetId the dataset id
     */
    suspend fun clearAdjustmentList(userId: String, datasetId: String)

    suspend fun stopRoutine(
        userId: String,
        datasetId: String,
    )
}