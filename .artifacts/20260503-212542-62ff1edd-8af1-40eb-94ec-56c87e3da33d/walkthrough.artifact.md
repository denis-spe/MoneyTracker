# Walkthrough - Fix Today Flow Refund Calculation

I have fixed a bug where "Refund" transactions were not being correctly included in the "Flow" calculation on the Today screen.

## Problem
The `DonutChartPager` was specifically looking for the exact label "Loan Refund" to categorize money as "incoming" (adding to the Flow). However, because different parts of the app use the label "Refund" or "Loan Refund" interchangeably for Lent repayments, some refunds were being ignored by the logic, leading to an incorrect Flow total.

## Solution
I updated the Flow calculation logic in [StatArea.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/ui/homeScreen/todayScreen/statArea/StatArea.kt) to include both "Loan Refund" and "Refund" as incoming items.

### Key Changes
- Updated `DonutChartPager` in `StatArea.kt` to treat the "Refund" label as an incoming financial flow.
- This ensures that the central "Flow" value in the donut chart accurately represents the net change (Incoming - Outgoing) for the day.

## Verification Results
- **Build**: `:app:assembleDebug` finished successfully.
- **Accuracy**: Verified that "Refund" items now contribute positively to the "Flow" calculation, providing a correct daily summary.
