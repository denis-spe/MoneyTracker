package com.example.moneytracker.backend.storage

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
     * Add a finance record to the storage
     * @param userId the user id
     * @param finance the finance record to add
     * @return the id of the added record
     */
    fun addData(userId: String, finance: Finance): String

    /**
     * Get all finance records
     * @param userId the user id
     */
    suspend fun getWholeDatasets(
        userId: String,
        onSuccess: (isSuccess: Boolean) -> Unit,
        onFailure: (error: Throwable?) -> Unit
    ): Flow<List<Finance>>


    suspend fun getInfo(userId: String): Flow<Info>

    /**
     * Add a repay entry into a finance record (identified by its id) for the given user.
     * Implementations should perform this atomically (transaction) to avoid lost updates.
     */
    suspend fun addAdjustmentDataset(userId: String, datasetId: String, adjustment: Adjustment)

    /**
     * Ensure every record stored for the user has a non-null id; assign UUIDs to missing ones.
     * This is a migration helper to avoid finance.id being null for older documents.
     */
    suspend fun ensureDatasetIds(userId: String)

    /**
     * Remove a finance record from the storage
     * @param userId the user id
     * @param finance the finance record to remove
     */
    suspend fun removeDataset(userId: String, finance: Finance)

    /**
     * Remove adjusted finance record from the storage
     * @param userId the user id
     * @param datasetId the record id
     * @param adjustment the adjustment
     */
    suspend fun removeAdjustmentDataset(userId: String, datasetId: String, adjustment: Adjustment)

    /**
     * Update a finance record to the storage
     * @param userId the user id
     * @param oldFinance the record to update
     * @param newFinance the record to update the old one
     */
    suspend fun updateDataset(
        userId: String,
        oldFinance: Finance,
        newFinance: Finance
    )

    /**
     * Update a finance record to the storage
     * @param userId the user id
     * @param datasetId of the record
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
     * Add a status into a list in record (identified by its id) for the given user.
     * Implementations should perform this atomically (transaction) to avoid lost updates.
     * @param userId the user id
     * @param datasetId the record id
     */
    suspend fun addStatus(
        userId: String,
        datasetId: String,
        newDateTime: Timestamp,
        newDeadlineDateTime: Timestamp
    )

    /**
     * Clear a list in record (identified by its id) for the given user.
     * Implementations should perform this atomically (transaction) to avoid lost updates.
     * @param userId the user id
     * @param datasetId the record id
     */
    suspend fun clearAdjustmentList(userId: String, datasetId: String)

    /**
     * Get a record from the storage
     * @param userId the user id
     * @param datasetId the record id
     */
    suspend fun stopRoutine(
        userId: String,
        datasetId: String,
    )

    /**
     * Get a record from the storage
     * @param userId the user id
     * @param datasetId the record id
     */
    suspend fun getDataset(userId: String, datasetId: String): Finance?

    /**
     * At the end of a routine, add a status into a list in
     * record and clear the adjustment list (identified by its id) for the given user.
     * @param userId the user id
     * @param datasetId the record id
     */
    suspend fun completeRoutine(
        userId: String,
        datasetId: String,
        newDateTime: Timestamp,
        nextDeadline: Timestamp
    )

    /**
     * Filter records based on the provided filter criteria.
     * @param userId the user id
     * @param filter the filter criteria to apply
     * @param orderBy the field to order the results by (optional)
     * @param orderDirection the direction to order the results (optional)
     * @return a list of records that match the filter criteria
     */
    suspend fun filterDatasets(
        userId: String,
        filter: Filter,
        orderBy: String? = null,
        orderDirection: Query.Direction? = null
    ): List<Finance>
}