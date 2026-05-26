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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val storage: DataStorage,
    private val account: AccountServices,
) : ViewModel() {

    private val _goal = MutableStateFlow<FinanceEntity.Goal?>(null)
    val goal: StateFlow<FinanceEntity.Goal?> = _goal.asStateFlow()

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadGoal(goalId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = storage.getDataset(
                    userId = account.currentUserId,
                    datasetId = goalId,
                    financeType = "GOAL"
                )
                (result as? FinanceEntity.Goal)?.let {
                    _goal.value = it
                }
            } catch (_: Exception) {
                // Handle error if needed
            } finally {
                _isLoading.value = false
            }
        }
    }
}
