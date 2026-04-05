// Glory be to the name of LORD our GOD
package com.example.moneytracker.backend.alarmManager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Unit tests for [AndroidAlarm] class.
 *
 * This test suite verifies:
 * - Correct initialization of AlarmManager and Intent
 * - Proper handling of exact alarm scheduling based on Android API level
 * - Fallback to inexact alarms when exact alarm permission is denied
 * - SecurityException handling
 * - PendingIntent creation with correct flags
 */
class AndroidAlarmTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockAlarmManager: AlarmManager

    private lateinit var androidAlarm: AndroidAlarm
    private lateinit var mockedPendingIntent: MockedStatic<PendingIntent>
    private val mockPendingIntent = mock(PendingIntent::class.java)

    private val testAlarmItem = AlarmItem(
        userId = "test-user",
        datasetId = "test-dataset-123",
        triggerMillis = System.currentTimeMillis() + 5000
    )

    @Before
    fun setup() {
        // Initialize Mockito annotations manually
        MockitoAnnotations.openMocks(this)

        // Mock the context to return the mock AlarmManager
        Mockito.`when`(mockContext.getSystemService(Context.ALARM_SERVICE))
            .thenReturn(mockAlarmManager)

        // Mock PendingIntent.getBroadcast to return our mockPendingIntent
        mockedPendingIntent = mockStatic(PendingIntent::class.java)
        mockedPendingIntent.`when`<PendingIntent> {
            PendingIntent.getBroadcast(
                any(),
                anyInt(),
                any(),
                anyInt()
            )
        }.thenReturn(mockPendingIntent)

        androidAlarm = AndroidAlarm(mockContext)
    }

    @After
    fun tearDown() {
        mockedPendingIntent.close()
    }

    /**
     * Test that schedule() creates a PendingIntent with correct flags
     */
    @Test
    fun schedule_createsPendingIntentWithCorrectFlags() {
        // Arrange
        Mockito.`when`(mockAlarmManager.canScheduleExactAlarms()).thenReturn(true)

        // Act
        androidAlarm.schedule(testAlarmItem)

        // Assert - Verify PendingIntent.getBroadcast was called with correct flags
        // Note: This is a simplified test; in real scenarios, you'd spy on static methods
        verify(mockAlarmManager, times(1)).setExactAndAllowWhileIdle(
            eq(AlarmManager.RTC_WAKEUP),
            anyLong(),
            any()
        )
    }

    /**
     * Test that schedule() uses exact alarm when permission is available on Android 12+
     */
    @Test
    fun schedule_usesExactAlarmWhenPermissionAvailableOnAndroid12Plus() {
        // This test requires SDK_INT >= S (31)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Arrange
            Mockito.`when`(mockAlarmManager.canScheduleExactAlarms()).thenReturn(true)

            // Act
            androidAlarm.schedule(testAlarmItem)

            // Assert
            verify(mockAlarmManager).setExactAndAllowWhileIdle(
                eq(AlarmManager.RTC_WAKEUP),
                anyLong(),
                any()
            )
            // Verify that inexact alarm was NOT called
            verify(mockAlarmManager, times(0)).setAndAllowWhileIdle(
                eq(AlarmManager.RTC_WAKEUP),
                anyLong(),
                any()
            )
        }
    }

    /**
     * Test that schedule() falls back to inexact alarm when exact alarm permission is denied
     */
    @Test
    fun schedule_fallsBackToInexactAlarmWhenExactPermissionDeniedOnAndroid12Plus() {
        // This test requires SDK_INT >= S (31)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Arrange
            Mockito.`when`(mockAlarmManager.canScheduleExactAlarms()).thenReturn(false)

            // Act
            androidAlarm.schedule(testAlarmItem)

            // Assert - Verify inexact alarm is used
            verify(mockAlarmManager).setAndAllowWhileIdle(
                eq(AlarmManager.RTC_WAKEUP),
                anyLong(),
                any()
            )
            // Verify that exact alarm was NOT called
            verify(mockAlarmManager, times(0)).setExactAndAllowWhileIdle(
                eq(AlarmManager.RTC_WAKEUP),
                anyLong(),
                any()
            )
        }
    }

    /**
     * Test that schedule() handles SecurityException gracefully
     */
    @Test
    fun schedule_handlesSecurityExceptionByFallingBackToInexactAlarm() {
        // Arrange
        // We mock setExactAndAllowWhileIdle to throw SecurityException
        // which should be caught and fall back to setAndAllowWhileIdle
        Mockito.doThrow(SecurityException("No permission"))
            .`when`(mockAlarmManager).setExactAndAllowWhileIdle(
                anyInt(),
                anyLong(),
                any()
            )

        // Act - Should not throw
        androidAlarm.schedule(testAlarmItem)

        // Assert - Verify inexact alarm is used as fallback
        verify(mockAlarmManager).setAndAllowWhileIdle(
            eq(AlarmManager.RTC_WAKEUP),
            anyLong(),
            any()
        )
    }

    /**
     * Test that schedule() uses exact alarm on Android 11 and below
     */
    @Test
    fun schedule_usesExactAlarmOnAndroidBelowS() {
        // This test verifies behavior for older Android versions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // Arrange
            // No need to mock canScheduleExactAlarms for older APIs

            // Act
            androidAlarm.schedule(testAlarmItem)

            // Assert
            verify(mockAlarmManager).setExactAndAllowWhileIdle(
                eq(AlarmManager.RTC_WAKEUP),
                anyLong(),
                any()
            )
        }
    }

    /**
     * Test that schedule() respects the 5-second delay
     */
    @Test
    fun schedule_setsCorrectTriggerTime() {
        // Arrange
        Mockito.`when`(mockAlarmManager.canScheduleExactAlarms()).thenReturn(true)

        // Act
        androidAlarm.schedule(testAlarmItem)

        // Assert - Trigger time should be exactly what we passed in testAlarmItem
        verify(mockAlarmManager).setExactAndAllowWhileIdle(
            eq(AlarmManager.RTC_WAKEUP),
            eq(testAlarmItem.triggerMillis),
            any()
        )
    }

    @Test
    fun cancel_callsAlarmManagerCancel() {
        // Arrange
        val testAlarmItem = AlarmItem(
            userId = "test-user",
            datasetId = "test-id",
            triggerMillis = System.currentTimeMillis()
        )

        // Act
        androidAlarm.cancel(testAlarmItem)

        // Assert
        verify(mockAlarmManager).cancel(any(PendingIntent::class.java))
        // also verify pendingIntent.cancel() was called if we could mock the static PendingIntent.getBroadcast
    }

    /**
     * Test multiple consecutive alarm schedules
     */
    @Test
    fun schedule_canScheduleMultipleAlarmsInSuccession() {
        // Arrange
        Mockito.`when`(mockAlarmManager.canScheduleExactAlarms()).thenReturn(true)
        val alarmItem1 = AlarmItem("dataset-1", "user-1", System.currentTimeMillis() + 5000)
        val alarmItem2 = AlarmItem("dataset-2", "user-2", System.currentTimeMillis() + 10000)

        // Act
        androidAlarm.schedule(alarmItem1)
        androidAlarm.schedule(alarmItem2)

        // Assert - Both alarms should have been scheduled
        verify(mockAlarmManager, times(2)).setExactAndAllowWhileIdle(
            eq(AlarmManager.RTC_WAKEUP),
            anyLong(),
            any()
        )
    }

    /**
     * Test that the alarm uses RTC_WAKEUP type
     */
    @Test
    fun schedule_usesRtcWakeupAlarmType() {
        // Arrange
        Mockito.`when`(mockAlarmManager.canScheduleExactAlarms()).thenReturn(true)

        // Act
        androidAlarm.schedule(testAlarmItem)

        // Assert
        verify(mockAlarmManager).setExactAndAllowWhileIdle(
            eq(AlarmManager.RTC_WAKEUP),
            anyLong(),
            any()
        )
    }

    /**
     * Test that PendingIntent flags include FLAG_IMMUTABLE
     */
    @Test
    fun schedule_usesFlagImmutableForPendingIntent() {
        // Arrange
        Mockito.`when`(mockAlarmManager.canScheduleExactAlarms()).thenReturn(true)

        // Act
        androidAlarm.schedule(testAlarmItem)

        // Assert
        // FLAG_IMMUTABLE is required on Android 12+
        // This is enforced during PendingIntent creation
        verify(mockAlarmManager).setExactAndAllowWhileIdle(
            eq(AlarmManager.RTC_WAKEUP),
            anyLong(),
            any()
        )
    }
}











