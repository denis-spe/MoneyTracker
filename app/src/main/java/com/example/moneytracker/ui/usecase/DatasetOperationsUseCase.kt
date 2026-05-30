package com.example.moneytracker.ui.usecase

import android.util.Log
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.FirestoreMigration
import com.example.moneytracker.backend.storage.Settlement
import com.example.moneytracker.backend.storage.Withdrawal
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import javax.inject.Inject

class FinanceOperationsUseCase @Inject constructor(
    private val dataStorage: DataStorage
) {

    fun addData(userId: String, financeEntity: FinanceEntity): String {
        return dataStorage.addData(userId, financeEntity)
    }

    suspend fun filterFinances(
        userId: String,
        filter: Filter,
        orderBy: String? = null,
        orderDirection: Query.Direction? = null
    ): List<FinanceEntity> {
        return dataStorage.filterDatasets(
            userId = userId,
            filter = filter,
            orderBy = orderBy,
            orderDirection = orderDirection
        )
    }

    suspend fun updateData(
        userId: String,
        oldFinanceEntity: FinanceEntity,
        newFinanceEntity: FinanceEntity
    ) {
        dataStorage.updateDataset(
            userId = userId,
            oldFinanceEntity = oldFinanceEntity,
            newFinanceEntity = newFinanceEntity
        )
    }

    suspend fun removeData(userId: String, financeEntity: FinanceEntity) {
        dataStorage.removeDataset(userId, financeEntity)
    }

    suspend fun addSettlement(
        userId: String,
        financeId: String,
        financeType: String,
        settlement: Settlement
    ) {
        dataStorage.addSettlementDataset(
            userId = userId,
            datasetId = financeId,
            financeType = financeType,
            settlement = settlement
        )
    }

    suspend fun addWithdrawal(
        userId: String,
        financeId: String,
        financeType: String,
        withdrawal: Withdrawal
    ) {
        dataStorage.addWithdrawal(
            userId = userId,
            datasetId = financeId,
            financeType = financeType,
            withdrawal = withdrawal
        )
    }

    suspend fun updateSettlement(
        userId: String,
        financeId: String,
        financeType: String,
        oldSettlement: Settlement,
        newSettlement: Settlement
    ) {
        dataStorage.updateSettlementDataset(
            userId = userId,
            datasetId = financeId,
            financeType = financeType,
            oldSettlement = oldSettlement,
            newSettlement = newSettlement
        )
    }

    suspend fun updateWithdrawal(
        userId: String,
        financeId: String,
        financeType: String,
        oldWithdrawal: Withdrawal,
        newWithdrawal: Withdrawal
    ) {
        dataStorage.updateWithdrawalDataset(
            userId = userId,
            datasetId = financeId,
            financeType = financeType,
            oldWithdrawal = oldWithdrawal,
            newWithdrawal = newWithdrawal
        )
    }

    suspend fun removeSettlement(
        userId: String,
        financeId: String,
        financeType: String,
        settlement: Settlement
    ) {
        dataStorage.removeSettlementDataset(
            userId = userId,
            datasetId = financeId,
            financeType = financeType,
            settlement = settlement
        )
    }

    suspend fun removeWithdrawal(
        userId: String,
        financeId: String,
        financeType: String,
        withdrawal: Withdrawal
    ) {
        dataStorage.removeWithdrawalDataset(
            userId = userId,
            datasetId = financeId,
            financeType = financeType,
            withdrawal = withdrawal
        )
    }

    suspend fun triggerMigration(userId: String) {
        try {
            FirestoreMigration.migrateUserDatasets(dataStorage.db, userId)
            FirestoreMigration.migrateSettlementsOnly(dataStorage.db, userId)
            dataStorage.ensureDatasetIds(userId)
        } catch (e: Exception) {
            Log.e("FinanceOperationsUseCase", "Migration failed", e)
        }
    }
}