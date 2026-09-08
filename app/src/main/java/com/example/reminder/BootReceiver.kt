package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.UserPreferencesRepository

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefsRepo = UserPreferencesRepository(context)
            val state = prefsRepo.appState.value
            if (state.reminderEnabled) {
                ReminderManager.scheduleDailyReminder(
                    context,
                    state.reminderHour,
                    state.reminderMinute
                )
            }
        }
    }
}
