// Love the LORD your GOD with all your soul and with all mind
// and with all your strength and love your neighbor
// as yourself
package com.example.moneytracker.ui.showAll

import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.ui.homeScreen.DataState

data class ShowAllStates(
    val transaction: DataState<List<FinanceEntity.Transaction>> = DataState.Loading,
    val liability: DataState<List<FinanceEntity.Liability>> = DataState.Loading,
    val goal: DataState<List<FinanceEntity.Goal>> = DataState.Loading
)
