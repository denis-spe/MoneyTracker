// Bless be the name of LORD our GOD
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.Finance

data class ItemData(
    val itemFinance: Finance? = null,
    val itemAdjustment: Adjustment? = null
)
