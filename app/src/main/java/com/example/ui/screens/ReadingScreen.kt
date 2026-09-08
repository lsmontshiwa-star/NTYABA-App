package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TextSizePreference
import com.example.data.UserAppState
import com.example.model.DailyLesson
import com.example.ui.components.TrippyFluidBackground
import com.example.ui.theme.BlueGrey
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.DeepSlate
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.VividOrange
import com.example.ui.theme.WarmBeige
import com.example.util.CalendarUtil

@Composable
fun ReadingScreen(
    lesson: DailyLesson,
    appState: UserAppState,
    initialScrollOffset: Int,
    onSaveScroll: (dayNumber: Int, offset: Int) -> Unit,
    onMarkComplete: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    onToggleTextSize: (TextSizePreference) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isFocusMode by remember { mutableStateOf(false) }
    var showTextSizeMenu by remember { mutableStateOf(false) }

    val isCompleted = appState.completedDays.contains(lesson.dayNumber)

    // Restore scroll position when screen launches
    LaunchedEffect(lesson.dayNumber) {
        if (initialScrollOffset > 0) {
            scrollState.scrollTo(initialScrollOffset)
        }
    }

    // Save scroll position when navigating away or disposing
    DisposableEffect(lesson.dayNumber) {
        onDispose {
            onSaveScroll(lesson.dayNumber, scrollState.value)
        }
    }

    TrippyFluidBackground(reduceMotion = appState.reduceMotion) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top App Bar
            AnimatedVisibility(
                visible = !isFocusMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            onSaveScroll(lesson.dayNumber, scrollState.value)
                            onClose()
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(PureWhite.copy(alpha = 0.85f))
                            .testTag("close_reader_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close reader",
                            tint = DeepSlate
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "DAY ${lesson.dayNumber} OF 30",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = VividOrange,
                                letterSpacing = 1.5.sp
                            )
                        )
                        Text(
                            text = lesson.sourceSection ?: "Chapter",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MutedText
                            )
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { showTextSizeMenu = !showTextSizeMenu },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PureWhite.copy(alpha = 0.85f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "Adjust text size",
                                tint = DeepSlate
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { isFocusMode = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PureWhite.copy(alpha = 0.85f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Focus reading mode",
                                tint = DeepSlate
                            )
                        }
                    }
                }
            }

            // Quick text size picker dialog / bar
            AnimatedVisibility(visible = showTextSizeMenu && !isFocusMode) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite.copy(alpha = 0.95f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Text Size:",
                            style = MaterialTheme.typography.labelMedium.copy(color = DeepSlate)
                        )
                        TextSizePreference.values().forEach { sizePref ->
                            val isSelected = appState.textSize == sizePref
                            Button(
                                onClick = {
                                    onToggleTextSize(sizePref)
                                    showTextSizeMenu = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) VividOrange else WarmBeige.copy(alpha = 0.4f),
                                    contentColor = if (isSelected) PureWhite else DeepSlate
                                )
                            ) {
                                Text(sizePref.label)
                            }
                        }
                    }
                }
            }

            // In Focus Mode, show floating exit button
            if (isFocusMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(
                        onClick = { isFocusMode = false },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(PureWhite.copy(alpha = 0.90f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.FullscreenExit,
                            contentDescription = "Exit focus mode",
                            tint = DeepSlate
                        )
                    }
                }
            }

            // Reading Content Canvas: high-contrast warm white surface with soft shadow
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = if (isFocusMode) 8.dp else 14.dp, vertical = 4.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PureWhite.copy(alpha = 0.96f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .testTag("reading_text_container")
                ) {
                    Text(
                        text = "Day ${lesson.dayNumber}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = VividOrange,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepSlate,
                            fontSize = 26.sp,
                            lineHeight = 32.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(
                        color = WarmBeige.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Verbatim Source Text
                    Text(
                        text = lesson.content,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontSize = appState.textSize.bodySp.sp,
                            lineHeight = appState.textSize.lineSp.sp,
                            color = CharcoalText,
                            letterSpacing = 0.2.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    HorizontalDivider(
                        color = WarmBeige.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Completion & Actions section
                    Button(
                        onClick = onMarkComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("mark_complete_button"),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCompleted) BlueGrey else VividOrange,
                            contentColor = PureWhite
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCompleted) "Completed ✓ Tap to Update" else "Mark Lesson Complete",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            CalendarUtil.addDailyReadingToCalendar(context, lesson)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(23.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DeepSlate
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = VividOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add to Calendar",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = DeepSlate
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Navigation footer: Previous / Next
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                onSaveScroll(lesson.dayNumber, scrollState.value)
                                onPrevious()
                            },
                            enabled = lesson.dayNumber > 1,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WarmBeige.copy(alpha = 0.6f),
                                contentColor = DeepSlate,
                                disabledContainerColor = WarmBeige.copy(alpha = 0.2f),
                                disabledContentColor = MutedText.copy(alpha = 0.4f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous lesson",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Previous")
                        }

                        Button(
                            onClick = {
                                onSaveScroll(lesson.dayNumber, scrollState.value)
                                onNext()
                            },
                            enabled = lesson.dayNumber < 30,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WarmBeige.copy(alpha = 0.6f),
                                contentColor = DeepSlate,
                                disabledContainerColor = WarmBeige.copy(alpha = 0.2f),
                                disabledContentColor = MutedText.copy(alpha = 0.4f)
                            )
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next lesson",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp).navigationBarsPadding())
        }
    }
}
