# Ensure Routine Rescheduling and Fix Missing Logs

The "Rescheduled next routine" log in `RoutineWorker` is not reached because `DataStorageImpl.completeRoutine` (which runs in `Dispatchers.IO`) checks for cancellation right after its internal `NonCancellable` block finishes. If the worker was cancelled during that time, it throws `JobCancellationException` back to the worker's catch block, skipping the rescheduling and notification logic.

## Proposed Changes

### Backend Workers

#### [RoutineWorker.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/backend/workers/RoutineWorker.kt)

- Wrap the critical sequence (completing the routine, rescheduling the next one, and showing the notification) in a `withContext(NonCancellable)` block.
- This ensures that once the worker starts updating the database to finish a cycle (clearing settlements), it will definitely finish the rest of the routine logic, even if the system tries to stop the worker.

```kotlin
            // Ensure critical completion and rescheduling sequence is not interrupted by cancellation
            withContext(NonCancellable) {
                dataStorage.completeRoutine(
                    userId = userId,
                    datasetId = datasetId,
                    financeType = financeType,
                    newDateTime = normalizedNow,
                    nextDeadline = nextDeadline
                )

                if (dataset.routine.routine != Routine.Nothing) {
                    workers.startRoutineWorker(
                        WorkersTask(
                            userId = userId,
                            datasetId = datasetId,
                            financeType = financeType,
                            deadlineDateTime = nextDeadline,
                            routineData = dataset.routine
                        )
                    )

                    // ... notification logic ...

                    Log.d(TAG, "Rescheduled next routine for ${dataset.label} at $nextDeadline")
                }
            }
```

## Verification Plan

### Manual Verification
- **Logcat Monitoring**:
  - Verify that "Rescheduled next routine for..." now appears consistently in the logs after `completeRoutine` succeeds.
  - Verify that even if a `JobCancellationException` was occurring before, it no longer prevents the routine from continuing.
- **Continuous Routine Test**:
  - Set a 1-minute routine and verify it runs for several cycles (e.g., 5-10 minutes) without stopping unexpectedly.
