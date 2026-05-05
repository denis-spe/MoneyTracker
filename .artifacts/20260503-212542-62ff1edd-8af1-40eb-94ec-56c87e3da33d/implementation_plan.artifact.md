# Consolidate Routine Completion and Update Fulfillment Loading

This plan covers consolidating the routine completion logic and updating the fulfillment data loading to include Goal, Debt, and Loan.

## Consolidate Routine Completion Logic

The goal is to move the rescheduling and notification logic from `RoutineWorker` into `DataStorageImpl.completeRoutine`. This ensures that the entire cycle completion is treated as a single unit of work and is protected by a `NonCancellable` block within the storage implementation.

### Backend Storage

#### [DataStorage.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/backend/storage/DataStorage.kt)

- Update `completeRoutine` signature to accept `WorkerInf` and `Notifier`.

```kotlin
    suspend fun completeRoutine(
        userId: String,
        datasetId: String,
        financeType: String,
        newDateTime: Timestamp,
        nextDeadline: Timestamp,
        workers: WorkerInf,
        notifier: Notifier
    )
```

#### [DataStorageImpl.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/backend/storage/DataStorageImpl.kt)

- Implement the updated `completeRoutine`.
- Move rescheduling (`workers.startRoutineWorker`) and notification (`notifier.showNotification`) logic inside the `NonCancellable` block.

### Backend Workers

#### [RoutineWorker.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/backend/workers/RoutineWorker.kt)

- Simplify `doWork` to call the updated `completeRoutine`, passing `workers` and `notifier`.

---

## Update Fulfillment Data Loading

Update `loadGoalData` to `loadFulfillmentData` and include `Goal`, `Debt`, and `Loan` types.

### UI Layer

#### [HomeViewModel.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/ui/homeScreen/HomeViewModel.kt)

- Rename `loadGoalData` to `loadFulfillmentData`.
- Update the filter to include `GOAL`, `DEBT`, and `LOAN` (mapped as `LIABILITY` in backend).
- Update `fulfillmentFinanceEntity` flow to call the renamed function and filter for `Goal` and `Liability`.

```kotlin
    val fulfillmentFinanceEntity = datasetsFlow
        .map { it.filter { entity -> entity is FinanceEntity.Goal || entity is FinanceEntity.Liability } }
        .onStart { loadFulfillmentData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    // ...

    fun loadFulfillmentData() = viewModelScope.launch {
        val uid = accountService.userState.value?.uid ?: return@launch
        _uiState.update { it.copy(isGoalDataLoading = true) }
        financeOperationsUseCase.filterFinances(
            userId = uid,
            filter = Filter.or(
                Filter.equalTo("financeType", "GOAL"),
                Filter.equalTo("financeType", "LIABILITY")
            )
        )
        _uiState.update { it.copy(isGoalDataLoading = false) }
    }
```

## Verification Plan

### Manual Verification
- **Logcat Monitoring**:
  - Verify that `loadFulfillmentData` is called and executes without errors.
- **UI Verification**:
  - Ensure that the "Fulfillment" screen correctly displays Goals, Debts, and Lent items.
