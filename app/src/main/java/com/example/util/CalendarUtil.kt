package com.example.util

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import com.example.model.DailyLesson
import java.util.Calendar

object CalendarUtil {

    /**
     * Launches the user's native Android calendar app with a pre-populated event
     * via ACTION_INSERT without requiring Google APIs or OAuth.
     */
    fun addDailyReadingToCalendar(context: Context, lesson: DailyLesson) {
        try {
            val startCal = Calendar.getInstance().apply {
                // Schedule reminder for today (or next hour)
                add(Calendar.HOUR_OF_DAY, 1)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            val endCal = (startCal.clone() as Calendar).apply {
                add(Calendar.MINUTE, lesson.estimatedMinutes.coerceAtLeast(15))
            }

            val calendarIntent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, "Daily Reading — Now That You're Born Again")
                putExtra(
                    CalendarContract.Events.DESCRIPTION,
                    "Day ${lesson.dayNumber}: ${lesson.title}\nSource: ${lesson.sourceSection ?: "Devotional"}"
                )
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startCal.timeInMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCal.timeInMillis)
                putExtra(CalendarContract.Events.ALL_DAY, false)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(calendarIntent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Calendar integration isn't available on this device.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
