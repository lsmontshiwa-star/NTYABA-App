package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.IntegrityReport
import com.example.data.LessonRepository
import com.example.data.TextSizePreference
import com.example.data.UserAppState
import com.example.data.UserPreferencesRepository
import com.example.model.DailyLesson
import com.example.reminder.ReminderManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class NavTab {
    TODAY,
    LESSONS,
    PROGRESS,
    SETTINGS
}

class DevotionalViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = UserPreferencesRepository(application)
    val appState: StateFlow<UserAppState> = prefsRepo.appState

    val allLessons: List<DailyLesson> = LessonRepository.getAllLessons()

    private val _activeTab = MutableStateFlow(NavTab.TODAY)
    val activeTab: StateFlow<NavTab> = _activeTab.asStateFlow()

    private val _readingLesson = MutableStateFlow<DailyLesson?>(null)
    val readingLesson: StateFlow<DailyLesson?> = _readingLesson.asStateFlow()

    val integrityReport: IntegrityReport by lazy {
        LessonRepository.validateIntegrity()
    }

    fun selectTab(tab: NavTab) {
        _activeTab.value = tab
    }

    fun openLesson(lesson: DailyLesson) {
        _readingLesson.value = lesson
    }

    fun openTodayLesson() {
        val currentDay = appState.value.currentDay
        val lesson = LessonRepository.getLesson(currentDay) ?: allLessons.first()
        _readingLesson.value = lesson
    }

    fun closeReading() {
        _readingLesson.value = null
    }

    fun markCurrentLessonComplete() {
        val current = _readingLesson.value ?: return
        prefsRepo.markLessonComplete(current.dayNumber)
    }

    fun goToNextLesson() {
        val current = _readingLesson.value ?: return
        if (current.dayNumber < 30) {
            val next = LessonRepository.getLesson(current.dayNumber + 1)
            if (next != null) {
                _readingLesson.value = next
            }
        }
    }

    fun goToPreviousLesson() {
        val current = _readingLesson.value ?: return
        if (current.dayNumber > 1) {
            val prev = LessonRepository.getLesson(current.dayNumber - 1)
            if (prev != null) {
                _readingLesson.value = prev
            }
        }
    }

    fun completeOnboarding() {
        prefsRepo.completeOnboarding()
    }

    fun updateReminder(enabled: Boolean, hour: Int, minute: Int) {
        prefsRepo.setReminder(enabled, hour, minute)
        if (enabled) {
            ReminderManager.scheduleDailyReminder(getApplication(), hour, minute)
        } else {
            ReminderManager.cancelReminder(getApplication())
        }
    }

    fun setTextSize(size: TextSizePreference) {
        prefsRepo.setTextSize(size)
    }

    fun setReduceMotion(reduce: Boolean) {
        prefsRepo.setReduceMotion(reduce)
    }

    fun saveScroll(dayNumber: Int, offset: Int) {
        prefsRepo.saveScrollPosition(dayNumber, offset)
    }

    fun getScroll(dayNumber: Int): Int {
        return prefsRepo.getScrollPosition(dayNumber)
    }
}
