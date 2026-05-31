// Glory be to the LORD of hosts and to his CHRIST JESUS,
// who is the KING of kings and LORD of lords, who alone has immortality,
// who dwells in unapproachable light, whom no one has ever seen or can see.
// To him be honor and eternal dominion. Amen.
package com.example.moneytracker.ui.detailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.ui.homeScreen.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            // 1. Instantly switch the state to Loading
            _detailState.update { currentState ->
                currentState.copy(financeEntity = DataState.Loading)
            }

            try {
                // 2. Fetch your data asynchronously
                val entity = storage.getDataset(
                    userId = account.currentUserId,
                    datasetId = goalId,
                    financeType = "GOAL"
                )

                // 3. Update state with Success and pass the fresh data
                _detailState.update { currentState ->
                    currentState.copy(financeEntity = DataState.Success(entity))
                }
            } catch (e: Exception) {
                // 4. Catch unexpected errors so your screen doesn't freeze in a loading loop
                _detailState.update { currentState ->
                    currentState.copy(financeEntity = DataState.Error(e))
                }
            }
        }
    }

    fun loadAchievementCounts(goalId: String) {
        viewModelScope.launch {
            // 1. Instantly switch the state to Loading
            _detailState.update { currentState ->
                currentState.copy(countAchievement = DataState.Loading)
            }

            try {
                // 2. Fetch your data asynchronously
                val countAchievement = storage.getCountOfAchievement(
                    userId = account.currentUserId,
                    datasetId = goalId,
                    financeType = "GOAL"
                )

                // 3. Update state with Success and pass the fresh data
                _detailState.update { currentState ->
                    currentState.copy(countAchievement = DataState.Success(countAchievement))
                }
            } catch (e: Exception) {
                // 4. Catch unexpected errors so your screen doesn't freeze in a loading loop
                _detailState.update { currentState ->
                    currentState.copy(countAchievement = DataState.Error(e))
                }
            }
        }
    }
}
