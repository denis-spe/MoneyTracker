// Glory be to the LORD of hosts
package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.isAmountEqualToAdjustAmount
import javax.inject.Inject

class GetAdjustDatasetUseCase @Inject constructor() {
    operator fun invoke(
        datasets: List<Dataset>,
    ): List<Dataset> {
        return datasets
            .filterNot { it.isAmountEqualToAdjustAmount() }
    }
}