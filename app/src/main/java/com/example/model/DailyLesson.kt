package com.example.model

/**
 * Immutable data model for a daily devotional lesson.
 * The content field contains ONLY verbatim source text from the authoritative PDF.
 */
data class DailyLesson(
    val id: Int,
    val dayNumber: Int,
    val title: String,
    val sourceSection: String?,
    val content: String,
    val estimatedMinutes: Int
)
