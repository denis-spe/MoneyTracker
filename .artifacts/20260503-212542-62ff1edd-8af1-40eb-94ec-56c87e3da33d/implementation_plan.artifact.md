# Fix Today Flow Refund Calculation Bug

Ensure that "Refund" items are correctly included in the "Flow" calculation (Incoming - Outgoing) in the Today screen's donut chart.

## Proposed Changes

### UI Components - Stat Area

#### [StatArea.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/ui/homeScreen/todayScreen/statArea/StatArea.kt)

- Update the `DonutChartPager` flow calculation to include "Refund" as an incoming item.
- While `SettlementType.LENT_REPAY` uses "Loan Refund", some UI parts (like in `YesterdayStatArea`) use the word "Refund". Adding this ensures all variations are captured.

```kotlin
                val (flowIn, flowOut) = donutChartDataCollection.items.fold(0f to 0f) { (incoming, outgoing), item ->
                    when (item.title) {
                        "Earnings",
                        "Debt",
                        "Loan Refund",
                        "Refund" -> (incoming + item.amount) to outgoing // Added "Refund"

                        "Expense",
                        "Lent",
                        "Savings",
                        "Debt Payback" -> incoming to (outgoing + item.amount)

                        else -> incoming to outgoing
                    }
                }
```

## Verification Plan

### Manual Verification
- **Test Transaction**: Add a settlement of type "Loan Refund" (Lent Repayment) for today.
- **Verify Flow**: Check the "Flow" value in the center of the donut chart. It should increase by the amount of the refund.
- **Verification of consistency**: Check if "Debt" and "Debt Payback" also correctly affect the flow.
