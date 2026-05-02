# Replace 'datasets' with 'Transaction', 'Goal', or 'Liability' in Firestore

The goal is to change the Firestore collection structure from using a generic `datasets` subcollection under each user to using specific subcollections based on the type of finance: `Transaction`, `Goal`, or `Liability`.

## User Review Required

- **Data Migration**: This change will break existing data in Firestore unless a migration script is run. I will provide a migration function in the implementation, but the user will need to decide when to run it.
- **Multiple Subcollections vs. Single Collection**: The request is specifically to use `Transaction`, `Goal`, or `Liability`. This means a single `Finance` object will now live in one of these three subcollections. This makes querying "all finances" slightly more complex (requires 3 queries or a Collection Group query).

## Proposed Changes

### [Backend Storage]

Update `DataStorage` and `DataStorageImpl` to handle multiple subcollections.

#### [DataStorage.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/backend/storage/DataStorage.kt)

- No changes needed to the interface itself, as it mostly takes `Finance` objects which contain the type information.

#### [DataStorageImpl.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/backend/storage/DataStorageImpl.kt)

- Add a helper function `getCollectionName(finance: Finance): String`.
- Update `addData`, `updateDataset`, `addAdjustmentDataset`, `stopRoutine`, `getDataset`, `completeRoutine`, `addStatus`, `clearAdjustmentList`, `removeDataset`, `removeAdjustmentDataset`, `updateAdjustmentDataset` to use the new subcollections.
- Update `getWholeDatasets` to merge results from all three subcollections using `combine` (Flow).
- Update `filterDatasets` to either use Collection Group queries or perform 3 separate queries and merge them. Given the user-centric structure, 3 queries merged locally is likely safer and avoids complex index requirements.

#### [SubcollectionExtensions.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/backend/storage/SubcollectionExtensions.kt)

- Update these helper functions to take a `financeType` or `collectionName` parameter.

### [Data Migration]

#### [NEW] [FirestoreMigration.kt](file:///mnt/b44e42b9-497a-47b6-9d82-210124fd3ab8/Programming/android/kotlin/MoneyTracker/app/src/main/java/com/example/moneytracker/backend/storage/FirestoreMigration.kt)

- Create a utility to move existing documents from `datasets` to the appropriate `Transaction`, `Goal`, or `Liability` subcollection.

---

## Verification Plan

### Automated Tests
- I will run existing unit tests if they exist (need to check for `DataStorageImplTest`).
- I will add a new test case to verify that `addData` puts the document in the correct subcollection based on its type.

### Manual Verification
- Deploy the app and add a Transaction, a Goal, and a Liability.
- Verify in the Firebase Console that they appear in the new subcollections.
- Verify that the app still correctly displays all items in the Home screen.
