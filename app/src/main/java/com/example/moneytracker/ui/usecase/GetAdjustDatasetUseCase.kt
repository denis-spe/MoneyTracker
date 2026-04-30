// Glory be to the LORD of hosts
package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Finance
import com.example.moneytracker.helper.isAmountEqualToAdjustAmount
import javax.inject.Inject

class GetAdjustFinanceUseCase @Inject constructor() {
    operator fun invoke(
        financeList: List<Finance>,
    ): List<Finance> {
        return financeList
            .filterNot { it.isAmountEqualToAdjustAmount() }
    }
}