// Bless be the name of LORD our GOD
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.FinanceEntity

data class ItemData(
    val itemFinanceEntity: FinanceEntity? = null,
    val itemAdjustment: Adjustment? = null
)
