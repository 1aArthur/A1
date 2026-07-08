package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CompletedWorkout
import com.example.ui.WorkoutViewModel
import com.example.ui.components.NeonCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    val completedWorkouts by viewModel.completedWorkouts.collectAsState()
    val userStats by viewModel.userStats.collectAsState()

    var activeSubTab by remember { mutableStateOf("HISTORICO") } // HISTORICO, ANALYTICS

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBackground)
            .padding(top = 16.dp)
    ) {
        // 1. Header Title
        Text(
            text = "HISTÓRICO & PERFORMANCE",
            color = TextPrimary,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = "Monitore sua constância, rendimento e estatísticas de combate físico.",
            color = TextSecondary,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Sub-tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { activeSubTab = "HISTORICO" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubTab == "HISTORICO") NeonCyan else Color(0xFF141926)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Registro",
                    tint = if (activeSubTab == "HISTORICO") VoidBackground else TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "REGISTROS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeSubTab == "HISTORICO") VoidBackground else TextSecondary
                )
            }

            Button(
                onClick = { activeSubTab = "ANALYTICS" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubTab == "ANALYTICS") NeonPurple else Color(0xFF141926)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = "Gráficos",
                    tint = if (activeSubTab == "ANALYTICS") Color.White else TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MÉTRICAS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeSubTab == "ANALYTICS") Color.White else TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Tab Contents
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            if (activeSubTab == "HISTORICO") {
                HistoryListTab(completedWorkouts = completedWorkouts)
            } else {
                AnalyticsTab(
                    completedWorkouts = completedWorkouts,
                    totalSTR = userStats?.str ?: 10,
                    totalEND = userStats?.end ?: 10,
                    totalSTA = userStats?.sta ?: 10,
                    totalAGI = userStats?.agi ?: 10
                )
            }
        }
    }
}

@Composable
fun HistoryListTab(completedWorkouts: List<CompletedWorkout>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        if (completedWorkouts.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📭", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Histórico Vazio",
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Complete seu primeiro calabouço de treino para registrar dados no sistema.",
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp).padding(top = 4.dp)
                    )
                }
            }
        } else {
            items(completedWorkouts) { log ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberCard)
                        .border(1.dp, Color(0xFF1D263B), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Badge Category
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF151D30)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (log.category) {
                                    "PEITO" -> "胸"
                                    "COSTAS" -> "背"
                                    "PERNAS" -> "脚"
                                    "DESAFIO" -> "👑"
                                    else -> "体"
                                },
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = log.title,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            // Date formatting
                            val dateString = remember(log.timestamp) {
                                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                sdf.format(Date(log.timestamp))
                            }
                            Text(
                                text = dateString,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "+${log.xpGained} XP",
                            color = NeonCyan,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "+${log.goldGained}g Ouro",
                            color = GoldenSun,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsTab(
    completedWorkouts: List<CompletedWorkout>,
    totalSTR: Int,
    totalEND: Int,
    totalSTA: Int,
    totalAGI: Int
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // 1. Chart Section
        item {
            NeonCard(borderColor = NeonPurple, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "GRÁFICO DE RENDIMENTO SEMANAL",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Demonstrativo de XP obtido por treinos concluídos.",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                WeeklyPerformanceChart(completedWorkouts = completedWorkouts)
            }
        }

        // 2. Physical Balance Info
        item {
            NeonCard(borderColor = NeonCyan, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "MÉTRICAS DA COMPOSIÇÃO FÍSICA",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Custom bar stats distribution
                StatProgressIndicator("Poder de Empurrar (STR)", totalSTR, 30, NeonCyan)
                Spacer(modifier = Modifier.height(8.dp))
                StatProgressIndicator("Capacidade de Defesa (DEF)", totalEND, 30, NeonPurple)
                Spacer(modifier = Modifier.height(8.dp))
                StatProgressIndicator("Estamina Geral (STA)", totalSTA, 30, GoldenSun)
                Spacer(modifier = Modifier.height(8.dp))
                StatProgressIndicator("Velocidade de Arranque (AGI)", totalAGI, 30, Color(0xFF00E676))
            }
        }
    }
}

@Composable
fun WeeklyPerformanceChart(completedWorkouts: List<CompletedWorkout>) {
    // We will accumulate training counts or XP gained in the last 7 days
    val currentCalendar = Calendar.getInstance()
    val weeklyXpArray = FloatArray(7)

    // Calculate XP per day of week (0 = 6 days ago, 6 = Today)
    for (i in 0..6) {
        val testCal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -i)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = testCal.timeInMillis
        testCal.add(Calendar.DAY_OF_YEAR, 1)
        val endTime = testCal.timeInMillis

        val xpToday = completedWorkouts.filter { log ->
            log.timestamp in startTime until endTime
        }.sumOf { it.xpGained }

        weeklyXpArray[6 - i] = xpToday.toFloat()
    }

    val maxVal = (weeklyXpArray.maxOrNull() ?: 100f).coerceAtLeast(100f)

    // Drawing the custom canvas bar chart
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = 32.dp.toPx()
        val spacing = (width - (barWidth * 7)) / 8

        for (i in 0..6) {
            val xpVal = weeklyXpArray[i]
            val pct = xpVal / maxVal
            val barHeight = (height - 30.dp.toPx()) * pct
            
            val xOffset = spacing + i * (barWidth + spacing)
            val yOffset = height - 20.dp.toPx() - barHeight

            // Draw background gray bar
            drawRoundRect(
                color = Color(0xFF141926),
                topLeft = Offset(xOffset, 0f),
                size = Size(barWidth, height - 20.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Draw active neon gradient bar if XP exists
            if (xpVal > 0) {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(NeonCyan, NeonPurple)
                    ),
                    topLeft = Offset(xOffset, yOffset),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
    }

    // Days labels below
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val calendar = Calendar.getInstance()
        val daysLabels = mutableListOf<String>()
        val daysNames = listOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB")
        
        for (i in 0..6) {
            val testCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val dayOfWeek = testCal.get(Calendar.DAY_OF_WEEK) - 1
            daysLabels.add(daysNames[dayOfWeek])
        }
        daysLabels.reverse()

        daysLabels.forEach { label ->
            Text(
                text = label,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(36.dp)
            )
        }
    }
}

@Composable
fun StatProgressIndicator(
    label: String,
    value: Int,
    capValue: Int,
    color: Color
) {
    val progress = (value.toFloat() / capValue.toFloat()).coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = TextPrimary, fontSize = 11.sp)
            Text(text = "$value / $capValue", color = color, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF141926))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}
