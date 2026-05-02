// Bless be the name of LORD our GOD
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.Settlement

data class ItemData(
    val itemFinanceEntity: FinanceEntity? = null,
    val itemSettlement: Settlement? = null
)
