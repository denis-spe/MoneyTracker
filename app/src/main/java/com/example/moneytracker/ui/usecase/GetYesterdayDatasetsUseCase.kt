package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.isCreatedAtEqualTo
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import network.chaintech.kmp_date_time_picker.utils.now
import javax.inject.Inject

class GetYesterdayFinanceUseCase @Inject constructor() {
    operator fun invoke(financeEntityList: List<FinanceEntity>): List<FinanceEntity> {
        val yesterday = LocalDateTime.now()
            .date
            .minus(1, DateTimeUnit.DAY)

        return financeEntityList.filter { entity ->
            entity.isCreatedAtEqualTo(yesterday) || when (entity) {
                is FinanceEntity.Goal -> entity.settlement.any { it.isCreatedAtEqualTo(yesterday) }
                is FinanceEntity.Liability -> entity.settlement.any {
                    it.isCreatedAtEqualTo(
                        yesterday
                    )
                }

                else -> false
            }
        }
    }
}
