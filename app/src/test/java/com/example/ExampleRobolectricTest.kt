package com.example

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.test.core.app.ApplicationProvider
import com.example.data.UserPreferencesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Born Again", appName)
    }

    @Test
    fun testUserPreferencesRepository_lifecycle() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = UserPreferencesRepository(context)

        // Initial state
        val initialState = repo.appState.value
        assertEquals(1, initialState.currentDay)
        assertTrue(initialState.completedDays.isEmpty())

        // Mark Day 1 complete
        repo.markLessonComplete(1)
        val updatedState = repo.appState.value
        assertTrue(updatedState.completedDays.contains(1))
        assertEquals(2, updatedState.currentDay)
        assertEquals(1, updatedState.currentStreak)
    }

    @Test
    fun testPaletteColors() {
        assertEquals(0xFFD9CCBE.toInt(), com.example.ui.theme.WarmBeige.toArgb())
        assertEquals(0xFFFB5624.toInt(), com.example.ui.theme.VividOrange.toArgb())
        assertEquals(0xFF98B2BF.toInt(), com.example.ui.theme.BlueGrey.toArgb())
        assertEquals(0xFFFFFFFF.toInt(), com.example.ui.theme.PureWhite.toArgb())
    }

    @Test
    fun testNotificationScheduler_scheduleAndCancel() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val scheduler = com.example.reminder.NotificationScheduler(context)

        // Schedule
        scheduler.scheduleDailyReminder(8, 0)
        assertTrue(scheduler.isReminderScheduled())

        // Cancel
        scheduler.cancelReminder()
        assertFalse(scheduler.isReminderScheduled())
    }
}
