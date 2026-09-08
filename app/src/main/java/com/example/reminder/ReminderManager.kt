package com.example.reminder

import android.content.Context

/**
 * Backward-compatible facade for [NotificationScheduler].
 */
object ReminderManager {

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        NotificationScheduler.scheduleDailyReminder(context, hour, minute)
    }

    fun cancelReminder(context: Context) {
        NotificationScheduler.cancelReminder(context)
    }

    fun isReminderScheduled(context: Context): Boolean {
        return NotificationScheduler.isReminderScheduled(context)
    }
}
