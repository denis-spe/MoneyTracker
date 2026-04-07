package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType
import javax.inject.Inject

class SortTodayDataAdjustUseCase @Inject constructor() {

    operator fun invoke(
        datasets: List<Dataset>,
        timeSorting: SortType,
        categorySorting: String?,
        paymentSorting: PaymentMethod?,
        alphabeticalOrder: SortType,
        amountSorting: SortType,
        take: Int? = null
    ): List<DataAdjust> {
        var coupledData = coupleDatasetsWithAdjustments(datasets).filter {
            when (it) {
                is DataAdjust.Data -> it.dataset.isForToday
                is DataAdjust.Adjust -> it.adjustment.isForToday
            }
        }

        coupledData = when (timeSorting) {
            SortType.Ascending -> coupledData.sortedBy {
                when (it) {
                    is DataAdjust.Data -> it.dataset.dateTime
                    is DataAdjust.Adjust -> it.adjustment.dateTime
                }
            }

            SortType.Descending -> coupledData.sortedByDescending {
                when (it) {
                    is DataAdjust.Data -> it.dataset.dateTime
                    is DataAdjust.Adjust -> it.adjustment.dateTime
                }
            }

            SortType.Initial -> coupledData
        }

        coupledData =
            if (categorySorting.isNullOrBlank() || categorySorting == "Initial") {
                coupledData
            } else {
                coupledData.filter {
                    when (it) {
                        is DataAdjust.Data -> it.dataset.dataType.text == categorySorting
                        is DataAdjust.Adjust -> it.adjustment.adjustmentType.text == categorySorting
                    }
                }
            }

        coupledData =
            if (paymentSorting == null) {
                coupledData
            } else {
                coupledData.filter {
                    when (it) {
                        is DataAdjust.Data -> it.dataset.paymentMethod == paymentSorting
                        is DataAdjust.Adjust -> it.adjustment.paymentMethod == paymentSorting
                    }
                }
            }

        coupledData = when (alphabeticalOrder) {
            SortType.Ascending -> coupledData.sortedBy {
                when (it) {
                    is DataAdjust.Data -> it.dataset.label
                    is DataAdjust.Adjust -> it.adjustment.label
                }
            }

            SortType.Descending -> coupledData.sortedByDescending {
                when (it) {
                    is DataAdjust.Data -> it.dataset.label
                    is DataAdjust.Adjust -> it.adjustment.label
                }
            }

            SortType.Initial -> coupledData
        }

        coupledData = when (amountSorting) {
            SortType.Ascending -> coupledData.sortedBy {
                when (it) {
                    is DataAdjust.Data -> it.dataset.amount
                    is DataAdjust.Adjust -> it.adjustment.amount
                }
            }

            SortType.Descending -> coupledData.sortedByDescending {
                when (it) {
                    is DataAdjust.Data -> it.dataset.amount
                    is DataAdjust.Adjust -> it.adjustment.amount
                }
            }

            SortType.Initial -> coupledData
        }

        return if (take != null) coupledData.take(take) else coupledData
    }
}