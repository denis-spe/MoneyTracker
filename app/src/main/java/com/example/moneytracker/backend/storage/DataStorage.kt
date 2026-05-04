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
     * Add a financeEntity record to the storage
     * @param userId the user id
     * @param financeEntity the financeEntity record to add
     * @return the id of the added record
     */
    fun addData(userId: String, financeEntity: FinanceEntity): String

    /**
     * Get all financeEntity records
     * @param userId the user id
     */
    suspend fun getWholeDatasets(
        userId: String,
        onSuccess: (isSuccess: Boolean) -> Unit,
        onFailure: (error: Throwable?) -> Unit
    ): Flow<List<FinanceEntity>>


    suspend fun getInfo(userId: String): Flow<Info>

    /**
     * Add a repay entry into a financeEntity record (identified by its id) for the given user.
     * Implementations should perform this atomically (transaction) to avoid lost updates.
     */
    suspend fun addSettlementDataset(
        userId: String,
        datasetId: String,
        financeType: String,
        settlement: Settlement
    )

    /**
     * Ensure every record stored for the user has a non-null id; assign UUIDs to missing ones.
     * This is a migration helper to avoid financeEntity.id being null for older documents.
     */
    suspend fun ensureDatasetIds(userId: String)

    /**
     * Remove a financeEntity record from the storage
     * @param userId the user id
     * @param financeEntity the financeEntity record to remove
     */
    suspend fun removeDataset(userId: String, financeEntity: FinanceEntity)

    /**
     * Remove adjusted financeEntity record from the storage
     * @param userId the user id
     * @param datasetId the record id
     * @param financeType the record type
     * @param settlement the settlement
     */
    suspend fun removeSettlementDataset(
        userId: String,
        datasetId: String,
        financeType: String,
        settlement: Settlement
    )

    /**
     * Update a financeEntity record to the storage
     * @param userId the user id
     * @param oldFinanceEntity the record to update
     * @param newFinanceEntity the record to update the old one
     */
    suspend fun updateDataset(
        userId: String,
        oldFinanceEntity: FinanceEntity,
        newFinanceEntity: FinanceEntity
    )

    /**
     * Update a financeEntity record to the storage
     * @param userId the user id
     * @param datasetId of the record
     * @param financeType the record type
     * @param oldSettlement to update with the new one
     * @param newSettlement to update the old one
     */
    suspend fun updateSettlementDataset(
        userId: String,
        datasetId: String,
        financeType: String,
        oldSettlement: Settlement,
        newSettlement: Settlement
    )

    /**
     * Add a status into a list in record (identified by its id) for the given user.
     * Implementations should perform this atomically (transaction) to avoid lost updates.
     * @param userId the user id
     * @param datasetId the record id
     * @param financeType the record type
     */
    suspend fun addStatus(
        userId: String,
        datasetId: String,
        financeType: String,
        newDateTime: Timestamp,
        newDeadlineDateTime: Timestamp
    )

    /**
     * Clear a list in record (identified by its id) for the given user.
     * Implementations should perform this atomically (transaction) to avoid lost updates.
     * @param userId the user id
     * @param datasetId the record id
     * @param financeType the record type
     */
    suspend fun clearSettlementList(userId: String, datasetId: String, financeType: String)

    /**
     * Get a record from the storage
     * @param userId the user id
     * @param datasetId the record id
     * @param financeType the record type
     */
    suspend fun stopRoutine(
        userId: String,
        datasetId: String,
        financeType: String
    )

    /**
     * Get a record from the storage
     * @param userId the user id
     * @param datasetId the record id
     * @param financeType the record type
     */
    suspend fun getDataset(userId: String, datasetId: String, financeType: String): FinanceEntity?

    /**
     * At the end of a routine, add a status into a list in
     * record and clear the settlement list (identified by its id) for the given user.
     * @param userId the user id
     * @param datasetId the record id
     * @param financeType the record type
     */
    suspend fun completeRoutine(
        userId: String,
        datasetId: String,
        financeType: String,
        newDateTime: Timestamp,
        nextDeadline: Timestamp,
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
    ): List<FinanceEntity>
}