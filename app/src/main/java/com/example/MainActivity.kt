package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Workout
import com.example.ui.WorkoutViewModel
import com.example.ui.WorkoutViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    private val viewModel: WorkoutViewModel by viewModels {
        WorkoutViewModelFactory((application as QuestFitApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuestFitTheme {
                var activeWorkoutForSession by remember { mutableStateOf<Workout?>(null) }
                var activeTab by remember { mutableIntStateOf(0) }

                val workoutToRun = activeWorkoutForSession

                if (workoutToRun != null) {
                    // Fullscreen immersive Workout session without Bottom Navigation interference
                    ActiveWorkoutScreen(
                        workout = workoutToRun,
                        viewModel = viewModel,
                        onBackToHome = { activeWorkoutForSession = null }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar(
                                containerColor = CyberCard,
                                contentColor = TextSecondary,
                                tonalElevation = 8.dp,
                                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                            ) {
                                NavigationBarItem(
                                    selected = activeTab == 0,
                                    onClick = { activeTab = 0 },
                                    icon = { Icon(Icons.Default.Shield, contentDescription = "HUD") },
                                    label = { Text("HUD", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NeonCyan,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = Color(0xFF1B243B),
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    )
                                )
                                NavigationBarItem(
                                    selected = activeTab == 1,
                                    onClick = { activeTab = 1 },
                                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Dungeons") },
                                    label = { Text("TREINOS", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NeonCyan,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = Color(0xFF1B243B),
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    )
                                )
                                NavigationBarItem(
                                    selected = activeTab == 2,
                                    onClick = { activeTab = 2 },
                                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Library") },
                                    label = { Text("BIBLIOTECA", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NeonCyan,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = Color(0xFF1B243B),
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    )
                                )
                                NavigationBarItem(
                                    selected = activeTab == 3,
                                    onClick = { activeTab = 3 },
                                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Store") },
                                    label = { Text("PROGRESSO", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NeonCyan,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = Color(0xFF1B243B),
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    )
                                )
                                NavigationBarItem(
                                    selected = activeTab == 4,
                                    onClick = { activeTab = 4 },
                                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Logs") },
                                    label = { Text("MÉTRICAS", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NeonCyan,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = Color(0xFF1B243B),
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    )
                                )
                            }
                        }
                    ) { innerPadding ->
                        AnimatedContent(
                            targetState = activeTab,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "tab_transition",
                            modifier = Modifier
                                .fillMaxSize()
                                .background(VoidBackground)
                                .padding(innerPadding)
                        ) { tab ->
                            when (tab) {
                                0 -> DashboardScreen(viewModel = viewModel)
                                1 -> WorkoutsScreen(onStartWorkout = { activeWorkoutForSession = it })
                                2 -> ExercisesScreen()
                                3 -> QuestShopScreen(viewModel = viewModel)
                                4 -> HistoryScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
