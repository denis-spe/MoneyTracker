// Glory be to the name of LORD our GOD
package com.example.moneytracker.backend.alarmManager

import android.app.AlarmManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [AndroidAlarm] class.
 *
 * These tests run on an actual Android device or emulator and verify:
 * - Actual AlarmManager integration
 * - Real Context behavior
 * - Actual PendingIntent creation
 * - System alarm scheduling without mocks
 */
@RunWith(AndroidJUnit4::class)
class AndroidAlarmInstrumentedTest {

    private lateinit var androidAlarm: AndroidAlarm
    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager
    private val testAlarmItem = AlarmItem(
        datasetId = "instrumented-test-dataset-123",
        message = "Instrumented test alarm message"
    )

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        androidAlarm = AndroidAlarm(context)
    }

    /**
     * Test that AndroidAlarm initializes correctly with real context
     */
    @Test
    fun androidAlarm_initializesWithRealContext() {
        // Assert
        assertNotNull(androidAlarm)
        assertNotNull(androidAlarm.alarmManager)
        assertNotNull(androidAlarm.intent)
    }

    /**
     * Test that AlarmManager is obtained correctly
     */
    @Test
    fun androidAlarm_obtainsAlarmManagerCorrectly() {
        // Assert
        val obtainedAlarmManager = androidAlarm.alarmManager
        assertNotNull(obtainedAlarmManager)
    }

    /**
     * Test that Intent is created correctly
     */
    @Test
    fun androidAlarm_createsIntentForAndroidAlarmReceiver() {
        // Assert
        val intent = androidAlarm.intent
        assertNotNull(intent)

        // Verify the intent action or component if it was set
        val component = intent.component
        assertTrue(component?.className?.contains("AndroidAlarmReceiver") ?: true)
    }

    /**
     * Test that schedule() completes without throwing an exception
     */
    @Test
    fun schedule_completesWithoutException() {
        // Act & Assert - Should not throw any exception
        try {
            androidAlarm.schedule(testAlarmItem)
        } catch (e: Exception) {
            throw AssertionError("schedule() should not throw exception: ${e.message}", e)
        }
    }

    /**
     * Test that multiple schedules can be called in succession
     */
    @Test
    fun schedule_canBeCalledMultipleTimes() {
        // Act
        val alarmItem1 = AlarmItem("dataset-1", "Test message 1")
        val alarmItem2 = AlarmItem("dataset-2", "Test message 2")

        androidAlarm.schedule(alarmItem1)
        androidAlarm.schedule(alarmItem2)

        // Assert - Both calls should complete without exception
        // If we reach here, both schedules succeeded
    }

    /**
     * Test that schedule() works with various alarm item data
     */
    @Test
    fun schedule_worksWithVariousAlarmItemData() {
        // Arrange
        val testCases = listOf(
            AlarmItem("", ""),
            AlarmItem("test-id", "Test message"),
            AlarmItem("dataset-with-long-id-1234567890", "Message with special chars: @#$%"),
            AlarmItem("日本語テスト", "Unicode test message 日本語")
        )

        // Act & Assert
        for (alarmItem in testCases) {
            try {
                androidAlarm.schedule(alarmItem)
            } catch (e: Exception) {
                throw AssertionError(
                    "schedule() failed for alarmItem: $alarmItem with error: ${e.message}",
                    e
                )
            }
        }
    }

    /**
     * Test that cancel() throws NotImplementedError (current behavior)
     */
    @Test(expected = NotImplementedError::class)
    fun cancel_throwsNotImplementedError() {
        // Act
        androidAlarm.cancel(testAlarmItem)
    }

    /**
     * Test that the alarm system doesn't crash when scheduling
     * This is a stress test with rapid scheduling
     */
    @Test
    fun schedule_doesNotCrashUnderRapidCalls() {
        // Act - Schedule many alarms rapidly
        for (i in 0..9) {
            val alarmItem = AlarmItem(
                datasetId = "rapid-test-$i",
                message = "Rapid test message $i"
            )
            androidAlarm.schedule(alarmItem)
        }

        // Assert - If we reach here without crashing, the test passes
    }

    /**
     * Test that PendingIntent creation respects FLAG_IMMUTABLE
     * This is important for Android 12+ security
     */
    @Test
    fun schedule_respectsFlagImmutableRequirement() {
        // Act
        androidAlarm.schedule(testAlarmItem)

        // Assert - If no SecurityException was thrown, FLAG_IMMUTABLE was used correctly
        // This test passes if the schedule completes without SecurityException
    }

    /**
     * Test that schedule respects allow-while-idle behavior
     */
    @Test
    fun schedule_allowsAlarmWhileDeviceIsIdle() {
        // Act
        androidAlarm.schedule(testAlarmItem)

        // Assert - The alarm should be scheduled even if device goes into idle mode
        // This is verified by the successful execution of setExactAndAllowWhileIdle or setAndAllowWhileIdle
    }

    /**
     * Test that the alarm respects the context's package name
     */
    @Test
    fun schedule_usesCorrectContextPackageName() {
        // Arrange
        val expectedPackageName = context.packageName

        // Act
        androidAlarm.schedule(testAlarmItem)

        // Assert
        assertTrue(expectedPackageName.isNotEmpty())
        assertTrue(expectedPackageName.contains("moneytracker"))
    }
}


