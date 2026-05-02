# Refactor Finance Category Types

The goal is to unify `TransactionType`, `LiabilityType`, and `GoalType` under a shared `FinanceCategory` interface. This will allow for better type safety and polymorphic behavior in the UI and data layers.

## Proposed Changes

### [Finance.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/backend/storage/Finance.kt)

- Define `sealed interface FinanceCategory`.
- Update `TransactionType` and `LiabilityType` to implement `FinanceCategory` and override its properties.
- Define `data object GoalType` implementing `FinanceCategory`.
- Simplify `categoryText` and `colorRes` in `Finance` class by leveraging the properties of `FinanceCategory`.

### [DataAddition.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/ui/homeScreen/dataAddition/DataAddition.kt)

- Replace `GoalSelection` object with `GoalType` from `Finance.kt`.
- Update `DataAdditionModelDrawerContent` and other composables to use `FinanceCategory` instead of `Any` where applicable.
- Clean up `when` expressions that check for `TransactionType`, `LiabilityType`, or `GoalSelection` to check for `FinanceCategory` properties instead.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to ensure no compilation errors.

### Manual Verification
- Deploy the app and navigate to the Data Addition screen.
- Verify that "Earnings", "Savings", "Expense", "Debt", "Lent", and "Goal" all show up correctly with their respective icons, colors, and descriptions.
- Verify that adding a new transaction/liability/goal still works as expected.
