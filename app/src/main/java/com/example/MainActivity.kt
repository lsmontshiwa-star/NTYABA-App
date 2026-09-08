package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DevotionalViewModel
import com.example.ui.NavTab
import com.example.ui.components.TrippyFluidBackground
import com.example.ui.screens.LessonsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.ReadingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.BlueGrey
import com.example.ui.theme.DeepSlate
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PureWhite
import com.example.ui.theme.VividOrange
import com.example.ui.theme.WarmBeige

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DevotionalApp()
            }
        }
    }
}

@Composable
fun DevotionalApp(
    viewModel: DevotionalViewModel = viewModel()
) {
    val appState by viewModel.appState.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val readingLesson by viewModel.readingLesson.collectAsState()

    // Handle Back button during Reading mode
    BackHandler(enabled = readingLesson != null) {
        viewModel.closeReading()
    }

    if (!appState.onboardingCompleted) {
        OnboardingScreen(
            reduceMotion = appState.reduceMotion,
            onBeginJourney = { enableReminder, hour, minute ->
                viewModel.completeOnboarding()
                if (enableReminder) {
                    viewModel.updateReminder(true, hour, minute)
                }
                viewModel.openTodayLesson()
            }
        )
    } else if (readingLesson != null) {
        val currentLesson = readingLesson!!
        ReadingScreen(
            lesson = currentLesson,
            appState = appState,
            initialScrollOffset = viewModel.getScroll(currentLesson.dayNumber),
            onSaveScroll = { dayNumber, offset ->
                viewModel.saveScroll(dayNumber, offset)
            },
            onMarkComplete = {
                viewModel.markCurrentLessonComplete()
            },
            onPrevious = {
                viewModel.goToPreviousLesson()
            },
            onNext = {
                viewModel.goToNextLesson()
            },
            onClose = {
                viewModel.closeReading()
            },
            onToggleTextSize = { size ->
                viewModel.setTextSize(size)
            }
        )
    } else {
        TrippyFluidBackground(reduceMotion = appState.reduceMotion) {
            Scaffold(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                bottomBar = {
                    NavigationBar(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .testTag("bottom_nav_bar"),
                        containerColor = PureWhite.copy(alpha = 0.94f),
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = activeTab == NavTab.TODAY,
                            onClick = { viewModel.selectTab(NavTab.TODAY) },
                            icon = { Icon(Icons.Default.Today, contentDescription = "Today") },
                            label = {
                                Text(
                                    "Today",
                                    fontSize = 11.sp,
                                    fontWeight = if (activeTab == NavTab.TODAY) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = VividOrange,
                                selectedTextColor = VividOrange,
                                indicatorColor = VividOrange.copy(alpha = 0.15f),
                                unselectedIconColor = DeepSlate.copy(alpha = 0.7f),
                                unselectedTextColor = DeepSlate.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.testTag("nav_tab_today")
                        )

                        NavigationBarItem(
                            selected = activeTab == NavTab.LESSONS,
                            onClick = { viewModel.selectTab(NavTab.LESSONS) },
                            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Lessons") },
                            label = {
                                Text(
                                    "Lessons",
                                    fontSize = 11.sp,
                                    fontWeight = if (activeTab == NavTab.LESSONS) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = VividOrange,
                                selectedTextColor = VividOrange,
                                indicatorColor = VividOrange.copy(alpha = 0.15f),
                                unselectedIconColor = DeepSlate.copy(alpha = 0.7f),
                                unselectedTextColor = DeepSlate.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.testTag("nav_tab_lessons")
                        )

                        NavigationBarItem(
                            selected = activeTab == NavTab.PROGRESS,
                            onClick = { viewModel.selectTab(NavTab.PROGRESS) },
                            icon = { Icon(Icons.Default.Timeline, contentDescription = "Progress") },
                            label = {
                                Text(
                                    "Progress",
                                    fontSize = 11.sp,
                                    fontWeight = if (activeTab == NavTab.PROGRESS) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = VividOrange,
                                selectedTextColor = VividOrange,
                                indicatorColor = VividOrange.copy(alpha = 0.15f),
                                unselectedIconColor = DeepSlate.copy(alpha = 0.7f),
                                unselectedTextColor = DeepSlate.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.testTag("nav_tab_progress")
                        )

                        NavigationBarItem(
                            selected = activeTab == NavTab.SETTINGS,
                            onClick = { viewModel.selectTab(NavTab.SETTINGS) },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                            label = {
                                Text(
                                    "Settings",
                                    fontSize = 11.sp,
                                    fontWeight = if (activeTab == NavTab.SETTINGS) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = VividOrange,
                                selectedTextColor = VividOrange,
                                indicatorColor = VividOrange.copy(alpha = 0.15f),
                                unselectedIconColor = DeepSlate.copy(alpha = 0.7f),
                                unselectedTextColor = DeepSlate.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.testTag("nav_tab_settings")
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                ) {
                    when (activeTab) {
                        NavTab.TODAY -> TodayScreen(
                            appState = appState,
                            onOpenLesson = { lesson ->
                                viewModel.openLesson(lesson)
                            }
                        )

                        NavTab.LESSONS -> LessonsScreen(
                            appState = appState,
                            onSelectLesson = { lesson ->
                                viewModel.openLesson(lesson)
                            }
                        )

                        NavTab.PROGRESS -> ProgressScreen(
                            appState = appState,
                            onSelectLesson = { lesson ->
                                viewModel.openLesson(lesson)
                            }
                        )

                        NavTab.SETTINGS -> SettingsScreen(
                            appState = appState,
                            integrityReport = viewModel.integrityReport,
                            onUpdateReminder = { enabled, hour, min ->
                                viewModel.updateReminder(enabled, hour, min)
                            },
                            onUpdateTextSize = { size ->
                                viewModel.setTextSize(size)
                            },
                            onUpdateReduceMotion = { reduce ->
                                viewModel.setReduceMotion(reduce)
                            }
                        )
                    }
                }
            }
        }
    }
}
