package com.example

import com.example.data.LessonRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testSourceIntegrityReport_verifiesAll30LessonsValid() {
        val report = LessonRepository.validateIntegrity()
        assertTrue("Integrity check details: ${report.details}", report.isValid)
        assertEquals(30, report.lessonCount)
        assertTrue(report.isSequential)
        assertTrue(report.hasNoEmptyContent)
        assertTrue(report.hasNoDuplicates)
        assertTrue(report.contentChecksum.isNotEmpty())
    }

    @Test
    fun testLessonDayCoverage_matchesBookStructure() {
        val allLessons = LessonRepository.getAllLessons()
        assertEquals(30, allLessons.size)

        val day1 = LessonRepository.getLesson(1)
        assertNotNull(day1)
        assertEquals("Dearly Beloved", day1?.title)
        assertTrue(day1?.content?.contains("Pastor Chris Oyakhilome") == true)

        val day2 = LessonRepository.getLesson(2)
        assertNotNull(day2)
        assertEquals("The Real You", day2?.title)
        assertTrue(day2?.content?.contains("inner man") == true)

        val day4 = LessonRepository.getLesson(4)
        assertNotNull(day4)
        assertEquals("You Are a New Creation", day4?.title)

        val day30 = LessonRepository.getLesson(30)
        assertNotNull(day30)
        assertEquals("Growing Up", day30?.title)
        assertTrue(day30?.content?.contains("CHRIST EMBASSY") == true)
    }

    @Test
    fun testEveryLessonHasSequentialDayNumberAndId() {
        val allLessons = LessonRepository.getAllLessons()
        for (i in 1..30) {
            val lesson = allLessons[i - 1]
            assertEquals(i, lesson.id)
            assertEquals(i, lesson.dayNumber)
            assertTrue("Lesson $i title is blank", lesson.title.isNotBlank())
            assertTrue("Lesson $i content is blank", lesson.content.isNotBlank())
            assertTrue("Lesson $i estimated minutes <= 0", lesson.estimatedMinutes > 0)
        }
    }

    @Test
    fun testNotificationScheduler_calculateNextTriggerMillis() {
        val now = System.currentTimeMillis()
        val trigger = com.example.reminder.NotificationScheduler.calculateNextTriggerMillis(7, 30)
        assertTrue("Trigger time ($trigger) must be in the future compared to now ($now)", trigger > now)
        // Must be within 25 hours from now
        assertTrue(trigger - now <= 25 * 60 * 60 * 1000L)
    }
}
