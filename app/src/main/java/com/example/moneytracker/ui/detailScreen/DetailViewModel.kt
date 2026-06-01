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
import com.example.moneytracker.backend.storage.listenToAchievement
import com.example.moneytracker.backend.storage.listenToSettlementForDataset
import com.example.moneytracker.ui.homeScreen.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val storage: DataStorage,
    private val account: AccountServices,
) : ViewModel() {

    private val _detailState = MutableStateFlow(DetailStates())
    val detailState: StateFlow<DetailStates> = _detailState.asStateFlow()

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
