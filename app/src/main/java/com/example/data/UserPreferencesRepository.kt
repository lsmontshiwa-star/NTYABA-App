package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TextSizePreference(val label: String, val bodySp: Float, val lineSp: Float) {
    SMALL("Small", 16f, 26f),
    MEDIUM("Medium", 18f, 30f),
    LARGE("Large", 22f, 36f)
}

data class UserAppState(
    val onboardingCompleted: Boolean = false,
    val currentDay: Int = 1,
    val completedDays: Set<Int> = emptySet(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalDaysEngaged: Int = 0,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 7,
    val reminderMinute: Int = 0,
    val textSize: TextSizePreference = TextSizePreference.MEDIUM,
    val reduceMotion: Boolean = false
)

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("born_again_prefs", Context.MODE_PRIVATE)

    private val _appState = MutableStateFlow(loadState())
    val appState: StateFlow<UserAppState> = _appState.asStateFlow()

    private fun loadState(): UserAppState {
        val completedDaysList = prefs.getStringSet(KEY_COMPLETED_DAYS, emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()

        val onboardingDone = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        val currentDay = prefs.getInt(KEY_CURRENT_DAY, 1).coerceIn(1, 30)
        val currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 0)
        val longestStreak = prefs.getInt(KEY_LONGEST_STREAK, 0)
        val totalDaysEngaged = prefs.getInt(KEY_TOTAL_DAYS_ENGAGED, 0)
        val reminderEnabled = prefs.getBoolean(KEY_REMINDER_ENABLED, false)
        val reminderHour = prefs.getInt(KEY_REMINDER_HOUR, 7)
        val reminderMinute = prefs.getInt(KEY_REMINDER_MINUTE, 0)
        val textSizeStr = prefs.getString(KEY_TEXT_SIZE, TextSizePreference.MEDIUM.name)
        val textSize = try {
            TextSizePreference.valueOf(textSizeStr ?: TextSizePreference.MEDIUM.name)
        } catch (e: Exception) {
            TextSizePreference.MEDIUM
        }
        val reduceMotion = prefs.getBoolean(KEY_REDUCE_MOTION, false)

        return UserAppState(
            onboardingCompleted = onboardingDone,
            currentDay = currentDay,
            completedDays = completedDaysList,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalDaysEngaged = totalDaysEngaged,
            reminderEnabled = reminderEnabled,
            reminderHour = reminderHour,
            reminderMinute = reminderMinute,
            textSize = textSize,
            reduceMotion = reduceMotion
        )
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
        _appState.value = _appState.value.copy(onboardingCompleted = true)
    }

    fun markLessonComplete(dayNumber: Int) {
        val currentCompleted = _appState.value.completedDays.toMutableSet()
        val isFirstTime = !currentCompleted.contains(dayNumber)
        currentCompleted.add(dayNumber)

        // Calculate next current day
        val nextIncomplete = (1..30).firstOrNull { !currentCompleted.contains(it) } ?: 30

        var newStreak = _appState.value.currentStreak
        var newLongest = _appState.value.longestStreak
        var newDaysEngaged = _appState.value.totalDaysEngaged

        if (isFirstTime) {
            val todayStr = getTodayDateString()
            val lastActiveDay = prefs.getString(KEY_LAST_COMPLETION_DATE, "")
            val datesSet = prefs.getStringSet(KEY_ENGAGED_DATES, emptySet())?.toMutableSet() ?: mutableSetOf()
            val isNewEngagementDay = !datesSet.contains(todayStr)
            if (isNewEngagementDay) {
                datesSet.add(todayStr)
                newDaysEngaged = datesSet.size
                prefs.edit().putStringSet(KEY_ENGAGED_DATES, datesSet).apply()

                val yesterdayStr = getYesterdayDateString()
                newStreak = when {
                    lastActiveDay == yesterdayStr -> newStreak + 1
                    lastActiveDay == todayStr -> newStreak
                    else -> 1
                }
                if (newStreak > newLongest) {
                    newLongest = newStreak
                }
                prefs.edit().putString(KEY_LAST_COMPLETION_DATE, todayStr).apply()
            }
        }

        prefs.edit()
            .putStringSet(KEY_COMPLETED_DAYS, currentCompleted.map { it.toString() }.toSet())
            .putInt(KEY_CURRENT_DAY, nextIncomplete)
            .putInt(KEY_CURRENT_STREAK, newStreak)
            .putInt(KEY_LONGEST_STREAK, newLongest)
            .putInt(KEY_TOTAL_DAYS_ENGAGED, newDaysEngaged)
            .apply()

        _appState.value = _appState.value.copy(
            completedDays = currentCompleted,
            currentDay = nextIncomplete,
            currentStreak = newStreak,
            longestStreak = newLongest,
            totalDaysEngaged = newDaysEngaged
        )
    }

    fun setReminder(enabled: Boolean, hour: Int = _appState.value.reminderHour, minute: Int = _appState.value.reminderMinute) {
        prefs.edit()
            .putBoolean(KEY_REMINDER_ENABLED, enabled)
            .putInt(KEY_REMINDER_HOUR, hour)
            .putInt(KEY_REMINDER_MINUTE, minute)
            .apply()

        _appState.value = _appState.value.copy(
            reminderEnabled = enabled,
            reminderHour = hour,
            reminderMinute = minute
        )
    }

    fun setTextSize(size: TextSizePreference) {
        prefs.edit().putString(KEY_TEXT_SIZE, size.name).apply()
        _appState.value = _appState.value.copy(textSize = size)
    }

    fun setReduceMotion(reduce: Boolean) {
        prefs.edit().putBoolean(KEY_REDUCE_MOTION, reduce).apply()
        _appState.value = _appState.value.copy(reduceMotion = reduce)
    }

    fun saveScrollPosition(dayNumber: Int, scrollY: Int) {
        prefs.edit().putInt(KEY_SCROLL_PREFIX + dayNumber, scrollY).apply()
    }

    fun getScrollPosition(dayNumber: Int): Int {
        return prefs.getInt(KEY_SCROLL_PREFIX + dayNumber, 0)
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(cal.time)
    }

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_CURRENT_DAY = "current_day"
        private const val KEY_COMPLETED_DAYS = "completed_days"
        private const val KEY_CURRENT_STREAK = "current_streak"
        private const val KEY_LONGEST_STREAK = "longest_streak"
        private const val KEY_TOTAL_DAYS_ENGAGED = "total_days_engaged"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_MINUTE = "reminder_minute"
        private const val KEY_TEXT_SIZE = "text_size"
        private const val KEY_REDUCE_MOTION = "reduce_motion"
        private const val KEY_LAST_COMPLETION_DATE = "last_completion_date"
        private const val KEY_ENGAGED_DATES = "engaged_dates"
        private const val KEY_SCROLL_PREFIX = "scroll_day_"
    }
}
