// Glory be to the LORD of hosts and to his CHRIST JESUS,
// who is the KING of kings and LORD of lords, who alone has immortality,
// who dwells in unapproachable light, whom no one has ever seen or can see.
// To him be honor and eternal dominion. Amen.
package com.example.moneytracker.ui.detailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Settlement
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.backend.storage.TagIcon
import com.example.moneytracker.backend.storage.Withdrawal
import com.example.moneytracker.backend.storage.listenToAchievement
import com.example.moneytracker.backend.storage.listenToSettlementForDataset
import com.example.moneytracker.backend.storage.listenToWithdrawalForDataset
import com.example.moneytracker.backend.storage.types.SettlementType
import com.example.moneytracker.ui.homeScreen.DataState
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val storage: DataStorage,
    private val account: AccountServices,
) : ViewModel() {

    private val _detailState = MutableStateFlow(DetailStates())
    val detailState: StateFlow<DetailStates> = _detailState.asStateFlow()

    fun loadTransaction(transactionId: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(financeEntity = DataState.Loading) }

            val userId = account.currentUserId
            val financeType = "TRANSACTION"

            combine(
                storage.listenToDataset(userId, transactionId, financeType),
                listenToWithdrawalForDataset(storage.db, userId, transactionId, financeType)
            ) { entity, withdrawals ->
                when (entity) {
                    is FinanceEntity.Transaction -> entity.copy(
                        withdrawal = withdrawals
                    )

                    else -> entity
                }
            }.collectLatest { updatedEntity ->
                _detailState.update { it.copy(financeEntity = DataState.Success(updatedEntity)) }
            }
        }
    }

    fun loadLiability(liabilityId: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(financeEntity = DataState.Loading) }

            val userId = account.currentUserId
            val financeType = "LIABILITY"

            combine(
                storage.listenToDataset(userId, liabilityId, financeType),
                listenToSettlementForDataset(storage.db, userId, liabilityId, financeType)
            ) { entity, settlements ->
                when (entity) {
                    is FinanceEntity.Liability -> entity.copy(
                        settlement = settlements
                    )

                    else -> entity
                }
            }.collectLatest { updatedEntity ->
                _detailState.update { it.copy(financeEntity = DataState.Success(updatedEntity)) }
            }
        }
    }

    fun loadGoal(goalId: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(financeEntity = DataState.Loading) }

            val userId = account.currentUserId
            val financeType = "GOAL"

            // Combine Goal, Settlements, and Achievements into a single live stream
            combine(
                storage.listenToDataset(userId, goalId, financeType),
                listenToSettlementForDataset(storage.db, userId, goalId, financeType),
                listenToAchievement(storage.db, userId, goalId, financeType)
            ) { entity, settlements, achievements ->
                when (entity) {
                    is FinanceEntity.Goal -> entity.copy(
                        settlement = settlements,
                        achievement = achievements
                    )

                    else -> entity
                }
            }.collectLatest { updatedEntity ->
                _detailState.update { it.copy(financeEntity = DataState.Success(updatedEntity)) }
            }
        }
    }

    fun addGoalAttainment(
        goalId: String,
        amount: Double,
        label: String,
        description: String,
        paymentMethod: PaymentMethod,
        dateTime: Timestamp,
        tagIcon: TagIcon
    ) {
        viewModelScope.launch {
            val settlement = Settlement(
                settlementId = UUID.randomUUID().toString(),
                amount = amount,
                label = label,
                description = description,
                dateTime = dateTime,
                tagIcon = tagIcon,
                settlementType = SettlementType.GOAL_ATTAIN,
                paymentMethod = paymentMethod,
                userId = account.currentUserId,
                datasetId = goalId
            )
            storage.addSettlementDataset(
                userId = account.currentUserId,
                datasetId = goalId,
                financeType = "GOAL",
                settlement = settlement
            )
        }
    }

    fun updateGoalInfo(
        goalId: String,
        label: String,
        description: String,
        tagIcon: TagIcon
    ) {
        viewModelScope.launch {
            val currentState = _detailState.value.financeEntity
            if (currentState is DataState.Success && currentState.data is FinanceEntity.Goal) {
                val oldGoal = currentState.data
                val newGoal = oldGoal.copy(
                    label = label,
                    description = description,
                    tagIcon = tagIcon
                )
                storage.updateDataset(
                    userId = account.currentUserId,
                    oldFinanceEntity = oldGoal,
                    newFinanceEntity = newGoal
                )
            }
        }
    }

    fun updateTransactionInfo(
        transactionId: String,
        label: String,
        description: String,
        tagIcon: TagIcon
    ) {
        viewModelScope.launch {
            val currentState = _detailState.value.financeEntity
            if (currentState is DataState.Success && currentState.data is FinanceEntity.Transaction) {
                val oldTransaction = currentState.data
                val newTransaction = oldTransaction.copy(
                    label = label,
                    description = description,
                    tagIcon = tagIcon
                )
                storage.updateDataset(
                    userId = account.currentUserId,
                    oldFinanceEntity = oldTransaction,
                    newFinanceEntity = newTransaction
                )
            }
        }
    }

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            val currentState = _detailState.value.financeEntity
            if (currentState is DataState.Success && currentState.data is FinanceEntity.Transaction) {
                storage.removeDataset(
                    userId = account.currentUserId,
                    financeEntity = currentState.data
                )
            }
        }
    }

    fun updateLiabilityInfo(
        liabilityId: String,
        label: String,
        description: String,
        tagIcon: TagIcon,
        isAmountReceived: Boolean
    ) {
        viewModelScope.launch {
            val currentState = _detailState.value.financeEntity
            if (currentState is DataState.Success && currentState.data is FinanceEntity.Liability) {
                val oldLiability = currentState.data
                val newLiability = oldLiability.copy(
                    label = label,
                    description = description,
                    tagIcon = tagIcon,
                    isAmountReceived = isAmountReceived
                )
                storage.updateDataset(
                    userId = account.currentUserId,
                    oldFinanceEntity = oldLiability,
                    newFinanceEntity = newLiability
                )
            }
        }
    }

    fun deleteLiability(liabilityId: String) {
        viewModelScope.launch {
            val currentState = _detailState.value.financeEntity
            if (currentState is DataState.Success && currentState.data is FinanceEntity.Liability) {
                storage.removeDataset(
                    userId = account.currentUserId,
                    financeEntity = currentState.data
                )
            }
        }
    }

    fun updateAchievementAmount(
        achievement: com.example.moneytracker.backend.storage.Achievement,
        newAmount: Double
    ) {
        viewModelScope.launch {
            // Get target amount from current goal state
            val currentGoal =
                (_detailState.value.financeEntity as? DataState.Success)?.data as? FinanceEntity.Goal
            val targetAmount = currentGoal?.amount ?: 0.0

            // Determine status based on whether new amount reaches target
            val newStatus = if (newAmount >= targetAmount) {
                Status.COMPLETED.name
            } else {
                Status.OVERDUE.name
            }

            val updatedAchievement = achievement.copy(
                totalSettlementAmount = newAmount,
                status = newStatus
            )

            storage.updateAchievementDataset(
                userId = achievement.userId,
                datasetId = achievement.datasetId,
                financeType = "GOAL",
                oldAchievement = achievement,
                newAchievement = updatedAchievement
            )
        }
    }

    fun deleteAchievement(
        achievement: com.example.moneytracker.backend.storage.Achievement
    ) {
        viewModelScope.launch {
            storage.removeAchievementDataset(
                userId = achievement.userId,
                datasetId = achievement.datasetId,
                financeType = "GOAL",
                achievement = achievement
            )
        }
    }

    fun addLiabilitySettlement(
        liabilityId: String,
        amount: Double,
        label: String,
        description: String,
        paymentMethod: PaymentMethod,
        dateTime: Timestamp,
        tagIcon: TagIcon,
        settlementType: SettlementType
    ) {
        viewModelScope.launch {
            val settlement = Settlement(
                settlementId = UUID.randomUUID().toString(),
                amount = amount,
                label = label,
                description = description,
                dateTime = dateTime,
                tagIcon = tagIcon,
                settlementType = settlementType,
                paymentMethod = paymentMethod,
                userId = account.currentUserId,
                datasetId = liabilityId
            )
            storage.addSettlementDataset(
                userId = account.currentUserId,
                datasetId = liabilityId,
                financeType = "LIABILITY",
                settlement = settlement
            )
        }
    }

    fun addWithdrawal(
        datasetId: String,
        amount: Double,
        label: String,
        description: String,
        toPaymentMethod: PaymentMethod,
        fromPaymentMethod: PaymentMethod,
        dateTime: Timestamp
    ) {
        viewModelScope.launch {
            val withdrawal = Withdrawal(
                withdrawalId = UUID.randomUUID().toString(),
                datasetId = datasetId,
                userId = account.currentUserId,
                amount = amount,
                label = label,
                description = description,
                createdAt = dateTime,
                toPaymentMethod = toPaymentMethod,
                fromPaymentMethod = fromPaymentMethod
            )
            storage.addWithdrawal(
                userId = account.currentUserId,
                datasetId = datasetId,
                financeType = "TRANSACTION",
                withdrawal = withdrawal
            )
        }
    }

    fun loadAchievementCounts(goalId: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(countAchievement = DataState.Loading) }

            storage.listenToCountAchievement(
                userId = account.currentUserId,
                datasetId = goalId,
                financeType = "GOAL"
            ).collectLatest { countAchievement ->
                android.util.Log.d("DetailViewModel", "Real-time counts: $countAchievement")
                _detailState.update { it.copy(countAchievement = DataState.Success(countAchievement)) }
            }
        }
    }
}
