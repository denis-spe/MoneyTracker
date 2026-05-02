// Glory be to the LORD of hosts
package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.isAmountEqualToAdjustAmount
import javax.inject.Inject

class GetAdjustFinanceUseCase @Inject constructor() {
    operator fun invoke(
        financeEntityList: List<FinanceEntity>,
    ): List<FinanceEntity> {
        return financeEntityList
            .filterNot { it.isAmountEqualToAdjustAmount() }
    }
}