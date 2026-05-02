# Firestore Restructure: 'datasets' replaced with Typed Collections

I have successfully refactored the Firestore data structure to use separate subcollections for each finance type: `Transaction`, `Goal`, and `Liability`. This provides better organization and potentially more efficient indexing for type-specific queries.

## Key Changes

### Firestore Schema Update
-   **Old Structure**: `database/{userId}/datasets/{id}`
-   **New Structure**:
    -   `database/{userId}/Transaction/{id}`
    -   `database/{userId}/Goal/{id}`
    -   `database/{userId}/Liability/{id}`

### Code Refactoring
-   **[DataStorageImpl.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/backend/storage/DataStorageImpl.kt)**: Updated all database operations to use the correct collection based on the `Finance` type. `getWholeDatasets` and `filterDatasets` now merge results from all three collections.
-   **[DataStorage.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/backend/storage/DataStorage.kt)**: Updated several method signatures to include `financeType: String`. This allows the implementation to target the correct collection without doing redundant searches.
-   **[ObserveUserDataUseCase.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/ui/usecase/ObserveUserDataUseCase.kt)**: Now triggers the migration process automatically when a user's data is first observed.
-   **Workers and Receivers**: Updated `RoutineWorker`, `RoutineBootReceiver`, and `MoneyTrackerApplication` to pass and handle the `financeType`, ensuring background tasks correctly target the new collections.

### Automatic Migration
-   **[FirestoreMigration.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/backend/storage/FirestoreMigration.kt)**: A new utility that moves documents from the legacy `datasets` collection to the new typed collections. It also handles the `statusHistory` subcollection and deletes the old data after a successful copy.

## Verification Results

### Build Status
The project was successfully compiled with the new changes:
```bash
./gradlew :app:compileDebugKotlin
# Build finished successfully.
```

### Manual Verification Steps (For User)
1.  **Run the App**: On first run, check the Logcat for "FirestoreMigration" tags. You should see "Migration completed for user: {uid}".
2.  **Verify Firestore Console**: Navigate to your Firestore instance and confirm that the `datasets` collection is being emptied and its documents are appearing in `Transaction`, `Goal`, or `Liability` subcollections.
3.  **UI Check**: Ensure that all your data (earnings, expenses, goals, etc.) still appears correctly on the Home screen and in the stats areas.
