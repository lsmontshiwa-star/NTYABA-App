package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LessonRepository
import com.example.data.UserAppState
import com.example.model.DailyLesson
import com.example.ui.theme.BlueGrey
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.DeepSlate
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.VividOrange
import com.example.ui.theme.WarmBeige

@Composable
fun ProgressScreen(
    appState: UserAppState,
    onSelectLesson: (DailyLesson) -> Unit
) {
    val completedCount = appState.completedDays.size
    val progressFraction = completedCount / 30f
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        label = "journey_progress"
    )

    val allLessons = LessonRepository.getAllLessons()

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(span = { GridItemSpan(5) }) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "YOUR JOURNEY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = VividOrange,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
                Text(
                    text = "$completedCount / 30 Lessons Complete",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepSlate
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite.copy(alpha = 0.90f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Overall Progress",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepSlate
                                )
                            )
                            Text(
                                text = "${(progressFraction * 100).toInt()}% Complete",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = VividOrange
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = VividOrange,
                            trackColor = WarmBeige.copy(alpha = 0.4f),
                            strokeCap = StrokeCap.Round
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3 Stats in a Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Current Streak",
                        value = "${appState.currentStreak} d",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Longest Streak",
                        value = "${appState.longestStreak} d",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Days Engaged",
                        value = "${appState.totalDaysEngaged} d",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "30-Day Path",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepSlate
                    )
                )
                Text(
                    text = "Tap any day to read or revisit that lesson anytime.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // 30 Days Grid
        items(allLessons) { lesson ->
            val isCompleted = appState.completedDays.contains(lesson.dayNumber)
            val isCurrent = lesson.dayNumber == appState.currentDay

            val bgColor = when {
                isCompleted -> VividOrange
                isCurrent -> PureWhite
                else -> WarmBeige.copy(alpha = 0.45f)
            }

            val contentColor = when {
                isCompleted -> PureWhite
                isCurrent -> VividOrange
                else -> DeepSlate.copy(alpha = 0.6f)
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor)
                    .clickable { onSelectLesson(lesson) }
                    .testTag("journey_day_${lesson.dayNumber}"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = PureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    } else if (isCurrent) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Current",
                            tint = VividOrange,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "${lesson.dayNumber}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Medium,
                            color = contentColor
                        )
                    )
                }
            }
        }

        item(span = { GridItemSpan(5) }) {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite.copy(alpha = 0.88f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DeepSlate,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MutedText,
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
        }
    }
}
