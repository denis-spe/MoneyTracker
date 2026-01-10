// Bless be the name of LORD our GOD
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.Dataset

data class ItemData(
    val itemDataset: Dataset? = null,
    val itemAdjustment: Adjustment? = null
)
