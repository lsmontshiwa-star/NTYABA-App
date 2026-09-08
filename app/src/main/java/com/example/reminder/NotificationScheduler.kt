package com.example.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

/**
 * [NotificationScheduler] schedules and manages recurring daily reminder notifications
 * using Android's [AlarmManager].
 *
 * It ensures timely delivery even when the application is closed, killed by the OS,
 * or when the device enters low-power Doze mode:
 * - Uses [AlarmManager.RTC_WAKEUP] to wake the device.
 * - Prioritizes [AlarmManager.setExactAndAllowWhileIdle] when exact alarms are permitted.
 * - Gracefully falls back to [AlarmManager.setAndAllowWhileIdle] on Android 12+ (API 31+)
 *   or under battery optimization constraints, preventing crashes.
 * - Integrates with [DailyReminderReceiver] to re-arm tomorrow's alarm upon delivery
 *   and [BootReceiver] to restore alarms after device restart.
 */
class NotificationScheduler(private val context: Context) {

    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    /**
     * Schedules a daily reminder alarm for the specified [hour] (0-23) and [minute] (0-59).
     * If the time has already passed today, the reminder will be scheduled for tomorrow.
     */
    fun scheduleDailyReminder(hour: Int, minute: Int) {
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager service is unavailable on this device")
            return
        }

        val targetMillis = calculateNextTriggerMillis(hour, minute)
        val pendingIntent = createAlarmPendingIntent(context)

        try {
            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }

            if (canScheduleExact && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    targetMillis,
                    pendingIntent
                )
                Log.d(TAG, "Scheduled exact daily reminder at $hour:$minute (timestamp: $targetMillis)")
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    targetMillis,
                    pendingIntent
                )
                Log.d(TAG, "Scheduled allow-while-idle daily reminder at $hour:$minute (timestamp: $targetMillis)")
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    targetMillis,
                    pendingIntent
                )
                Log.d(TAG, "Scheduled legacy daily reminder at $hour:$minute (timestamp: $targetMillis)")
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "Exact alarm permission restricted, falling back to setAndAllowWhileIdle: ${e.message}")
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        targetMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        targetMillis,
                        pendingIntent
                    )
                }
            } catch (fallbackEx: Exception) {
                Log.e(TAG, "Failed to schedule fallback reminder: ${fallbackEx.message}", fallbackEx)
            }
        }
    }

    /**
     * Cancels any scheduled daily reminder.
     */
    fun cancelReminder() {
        if (alarmManager == null) return
        val pendingIntent = createAlarmPendingIntent(context)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Log.d(TAG, "Cancelled daily reminder alarm")
    }

    /**
     * Checks if a daily reminder alarm is currently active in the system.
     */
    fun isReminderScheduled(): Boolean {
        val intent = Intent(context, DailyReminderReceiver::class.java).apply {
            action = ACTION_DAILY_REMINDER
        }
        val flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, flags) != null
    }

    companion object {
        const val TAG = "NotificationScheduler"
        const val ALARM_REQUEST_CODE = 2002
        const val ACTION_DAILY_REMINDER = "com.example.action.DAILY_REMINDER"

        /**
         * Calculates the next execution timestamp in epoch milliseconds for the given hour and minute.
         */
        fun calculateNextTriggerMillis(hour: Int, minute: Int): Long {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // If time is now or in the past today, advance to the next day
            if (target.timeInMillis <= now.timeInMillis) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }

            return target.timeInMillis
        }

        /**
         * Creates an immutable [PendingIntent] directed at [DailyReminderReceiver].
         */
        fun createAlarmPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, DailyReminderReceiver::class.java).apply {
                action = ACTION_DAILY_REMINDER
            }
            return PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        // Static convenience helpers
        fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
            NotificationScheduler(context).scheduleDailyReminder(hour, minute)
        }

        fun cancelReminder(context: Context) {
            NotificationScheduler(context).cancelReminder()
        }

        fun isReminderScheduled(context: Context): Boolean {
            return NotificationScheduler(context).isReminderScheduled()
        }
    }
}
