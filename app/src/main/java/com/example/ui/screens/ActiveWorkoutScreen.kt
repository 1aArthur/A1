package com.example.ui.screens

import android.os.CountDownTimer
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Workout
import com.example.ui.WorkoutViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.NeonCard
import com.example.ui.components.RpgProgressBar
import com.example.ui.theme.*
import kotlinx.coroutines.delay

enum class SessionState {
    EXERCISING,
    RESTING,
    SUMMARY
}

@Composable
fun ActiveWorkoutScreen(
    workout: Workout,
    viewModel: WorkoutViewModel,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentState by remember { mutableStateOf(SessionState.EXERCISING) }
    var currentExerciseIndex by remember { mutableStateOf(0) }
    
    // Timer states
    var secondsElapsed by remember { mutableStateOf(0) }
    var restSecondsLeft by remember { mutableStateOf(30) }

    var showExitDialog by remember { mutableStateOf(false) }

    // Intercept hardware/system back button
    BackHandler {
        if (currentState == SessionState.SUMMARY) {
            onBackToHome()
        } else {
            showExitDialog = true
        }
    }

    val currentExercise = workout.exercises.getOrNull(currentExerciseIndex)

    // Running general training duration timer in the background
    LaunchedEffect(currentState) {
        if (currentState != SessionState.SUMMARY) {
            while (true) {
                delay(1000)
                secondsElapsed++
            }
        }
    }

    // Cooldown/Rest countdown
    LaunchedEffect(currentState) {
        if (currentState == SessionState.RESTING) {
            restSecondsLeft = 30
            while (restSecondsLeft > 0) {
                delay(1000)
                restSecondsLeft--
            }
            // Auto skip resting on countdown done
            if (currentState == SessionState.RESTING) {
                if (currentExerciseIndex + 1 < workout.exercises.size) {
                    currentExerciseIndex++
                    currentState = SessionState.EXERCISING
                } else {
                    currentState = SessionState.SUMMARY
                }
            }
        }
    }

    // Motivational rest quotes
    val restQuote = remember(currentExerciseIndex) {
        val quotes = listOf(
            "A fadiga é temporária. Sua glória no sistema é eterna!",
            "Respire fundo. Regenerando barra de energia celular...",
            "Seu físico está quebrando barreiras ocultas neste segundo.",
            "Descanse o corpo, mantenha o foco do monarca inabalável.",
            "Atributos de defesa e estamina sendo lapidados!"
        )
        quotes[currentExerciseIndex % quotes.size]
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App header inside training
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = workout.title.uppercase(),
                    color = NeonCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "STATUS DO CALABOUÇO: ATIVO",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1D263B))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = String.format("%02d:%02d", secondsElapsed / 60, secondsElapsed % 60),
                        color = GoldenSun,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Exit training session button
                IconButton(
                    onClick = {
                        if (currentState == SessionState.SUMMARY) {
                            onBackToHome()
                        } else {
                            showExitDialog = true
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Abandonar Treino"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        AnimatedContent(
            targetState = currentState,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "state_transition",
            modifier = Modifier.weight(1f)
        ) { state ->
            when (state) {
                SessionState.EXERCISING -> {
                    if (currentExercise != null) {
                        ExerciseHUD(
                            exerciseName = currentExercise.name,
                            category = currentExercise.category,
                            difficulty = currentExercise.difficulty,
                            equipment = currentExercise.equipmentNeeded,
                            reps = currentExercise.defaultReps,
                            description = currentExercise.description,
                            proTip = currentExercise.proTips,
                            currentIndex = currentExerciseIndex,
                            totalExercises = workout.exercises.size,
                            onComplete = {
                                if (currentExerciseIndex + 1 < workout.exercises.size) {
                                    currentState = SessionState.RESTING
                                } else {
                                    currentState = SessionState.SUMMARY
                                }
                            }
                        )
                    }
                }
                SessionState.RESTING -> {
                    RestHUD(
                        secondsLeft = restSecondsLeft,
                        nextExerciseName = workout.exercises.getOrNull(currentExerciseIndex + 1)?.name ?: "Fim",
                        quote = restQuote,
                        onSkip = {
                            if (currentExerciseIndex + 1 < workout.exercises.size) {
                                currentExerciseIndex++
                                currentState = SessionState.EXERCISING
                            } else {
                                currentState = SessionState.SUMMARY
                            }
                        }
                    )
                }
                SessionState.SUMMARY -> {
                    SummaryHUD(
                        workoutTitle = workout.title,
                        durationSeconds = secondsElapsed,
                        xpReward = workout.xpReward,
                        goldReward = workout.goldReward,
                        onClaimRewards = {
                            viewModel.finishWorkout(
                                title = workout.title,
                                category = workout.category,
                                baseXP = workout.xpReward,
                                baseGold = workout.goldReward
                            )
                            onBackToHome()
                        }
                    )
                }
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = "ABANDONAR CALABOUÇO?",
                    color = Color.Red,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Se você abandonar o treino agora, todo o progresso atual e as recompensas acumuladas de XP e Ouro serão perdidas. Deseja mesmo sair?",
                    color = TextPrimary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onBackToHome()
                    }
                ) {
                    Text("SIM, SAIR", color = Color.Red, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    Text("CONTINUAR TREINANDO", color = NeonCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF161A26),
            textContentColor = TextSecondary
        )
    }
}

@Composable
fun ExerciseHUD(
    exerciseName: String,
    category: String,
    difficulty: String,
    equipment: String,
    reps: String,
    description: String,
    proTip: String,
    currentIndex: Int,
    totalExercises: Int,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Exercise Info Card
        NeonCard(borderColor = NeonCyan, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EXERCÍCIO ${currentIndex + 1} DE $totalExercises",
                    color = NeonCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(NeonPurple.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$category • $equipment",
                        color = NeonPurple,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = exerciseName,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Large Reps callout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF151D30))
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = reps.uppercase(),
                    color = GoldenSun,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "COMO EXECUTAR:",
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "💡 DICA DE MONARCA:",
                color = GoldenSun,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Text(
                text = proTip,
                color = TextSecondary.copy(alpha = 0.9f),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Action Button
        CyberButton(
            text = "Concluir Exercício",
            onClick = onComplete,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}

@Composable
fun RestHUD(
    secondsLeft: Int,
    nextExerciseName: String,
    quote: String,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NeonCard(borderColor = NeonPurple, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "FASE DE DESCANSO (REST)",
                color = NeonPurple,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Big Timer Graphic
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF151B29))
                    .border(2.dp, NeonPurple, CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$secondsLeft",
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    )
                    Text(
                        text = "SEGUNDOS",
                        color = TextSecondary,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Rest quote
            Text(
                text = "\"$quote\"",
                color = TextPrimary,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = Color(0xFF1D263B), thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "PRÓXIMO DESAFIO:",
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = nextExerciseName,
                color = NeonCyan,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Skip button
        Button(
            onClick = onSkip,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D263B)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Pular Descanso",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PULAR DESCANSO",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SummaryHUD(
    workoutTitle: String,
    durationSeconds: Int,
    xpReward: Int,
    goldReward: Int,
    onClaimRewards: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Celebration,
                contentDescription = "Congratulations",
                tint = GoldenSun,
                modifier = Modifier
                    .size(72.dp)
                    .padding(bottom = 12.dp)
            )

            Text(
                text = "LIMITES SUPERADOS!",
                color = GoldenSun,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Você concluiu o calvário com perfeição.",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            NeonCard(borderColor = GoldenSun, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "RELATÓRIO DO CALABOUÇO",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Treino Concluído:", color = TextSecondary, fontSize = 12.sp)
                    Text(text = workoutTitle, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Tempo Despendido:", color = TextSecondary, fontSize = 12.sp)
                    Text(
                        text = String.format("%02d:%02d", durationSeconds / 60, durationSeconds % 60),
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color(0xFF1D263B), thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "RECOMPENSAS REGISTRADAS:",
                    color = NeonCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "💎", fontSize = 24.sp)
                        Text(
                            text = "+$xpReward XP",
                            color = NeonCyan,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(text = "XP do Sistema", color = TextSecondary, fontSize = 10.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🪙", fontSize = 24.sp)
                        Text(
                            text = "+$goldReward Ouro",
                            color = GoldenSun,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(text = "Moeda de RPG", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }

        CyberButton(
            text = "Receber Recompensas",
            onClick = onClaimRewards,
            containerColor = GoldenSun,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}
