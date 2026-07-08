package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DailyQuest
import com.example.data.UserStats
import com.example.ui.WorkoutViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val dailyQuests by viewModel.dailyQuests.collectAsStateWithLifecycle()

    if (userStats == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(VoidBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = NeonCyan,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "SINCRO-SISTEMA INICIANDO...",
                    color = NeonCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Carregando atributos e dados do jogador",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
        return
    }
    
    var showInfoDialog by remember { mutableStateOf(false) }
    var questToIncrement by remember { mutableStateOf<DailyQuest?>(null) }
    var incrementValueString by remember { mutableStateOf("10") }

    // State for animations
    var lastKnownXp by remember { mutableStateOf<Int?>(null) }
    var lastKnownLevel by remember { mutableStateOf<Int?>(null) }
    var xpGainAnimAmount by remember { mutableStateOf<Int?>(null) }
    var showLevelUpAnim by remember { mutableStateOf<Int?>(null) }

    // Quest filter state: 0 = TODAS, 1 = ATIVAS, 2 = CONCLUÍDAS
    var selectedQuestFilter by remember { mutableIntStateOf(0) }

    LaunchedEffect(userStats) {
        val stats = userStats
        if (stats != null) {
            val oldXp = lastKnownXp
            val oldLevel = lastKnownLevel
            if (oldXp != null && oldLevel != null) {
                if (stats.level > oldLevel) {
                    showLevelUpAnim = stats.level
                    // Total XP gained calculation
                    val gained = (stats.xpNeeded - oldXp) + stats.xp
                    if (gained > 0) {
                        xpGainAnimAmount = gained
                    }
                } else if (stats.xp > oldXp) {
                    val gained = stats.xp - oldXp
                    xpGainAnimAmount = gained
                }
            }
            lastKnownXp = stats.xp
            lastKnownLevel = stats.level
        }
    }

    LaunchedEffect(xpGainAnimAmount) {
        if (xpGainAnimAmount != null) {
            kotlinx.coroutines.delay(2500)
            xpGainAnimAmount = null
        }
    }

    LaunchedEffect(showLevelUpAnim) {
        if (showLevelUpAnim != null) {
            kotlinx.coroutines.delay(4000)
            showLevelUpAnim = null
        }
    }

    Box(modifier = modifier.fillMaxSize().background(VoidBackground)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            // 1. Profile HUD Header
            item {
                userStats?.let { stats ->
                    ProfileHudHeader(
                        stats = stats,
                        onInfoClick = { showInfoDialog = true }
                    )
                }
            }

            // 2. Attributes Allocation Panel
            item {
                userStats?.let { stats ->
                    AttributesPanel(
                        stats = stats,
                        onAllocate = { viewModel.allocatePoint(it) }
                    )
                }
            }

            // Quest Board Panel Overview Card (Gamified UI)
            if (dailyQuests.isNotEmpty()) {
                item {
                    val totalQuests = dailyQuests.size
                    val completedQuests = dailyQuests.count { it.isCompleted }
                    val progressPercentage = if (totalQuests > 0) (completedQuests.toFloat() / totalQuests.toFloat() * 100).toInt() else 0

                    NeonCard(borderColor = NeonPurple) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "PROGRESSO DIÁRIO",
                                    color = NeonCyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$completedQuests de $totalQuests Missões Ativas Concluídas",
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                RpgProgressBar(
                                    label = "CONCLUSÃO DE QUESTS ($progressPercentage%)",
                                    currentValue = completedQuests.toFloat(),
                                    maxValue = totalQuests.toFloat(),
                                    barColor = NeonPurple,
                                    unit = ""
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            // Game-like circular percentage ring
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF13111C))
                                    .border(2.dp, if (progressPercentage == 100) SuccessGreen else NeonPurple, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$progressPercentage%",
                                    color = if (progressPercentage == 100) SuccessGreen else Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // 3. Daily Quests Title & Actions & Filter Buttons
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIÁRIO DE MISSÕES (QUESTS)",
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        
                        IconButton(
                            onClick = { viewModel.resetDailyQuests() },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = NeonCyan)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset/Scale Quests"
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Filter Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filters = listOf("TODAS", "ATIVAS", "CONCLUÍDAS")
                        filters.forEachIndexed { index, label ->
                            val isSelected = selectedQuestFilter == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonCyan.copy(alpha = 0.15f) else Color(0xFF131722))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) NeonCyan else Color(0xFF1D2230),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedQuestFilter = index }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) NeonCyan else TextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // 4. Filtered Daily Quests List
            val filteredQuests = when (selectedQuestFilter) {
                1 -> dailyQuests.filter { !it.isCompleted }
                2 -> dailyQuests.filter { it.isCompleted }
                else -> dailyQuests
            }

            if (filteredQuests.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (dailyQuests.isEmpty()) "Carregando missões..." else "Nenhuma missão encontrada nesta categoria.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(filteredQuests) { quest ->
                    DailyQuestCard(
                        quest = quest,
                        onLogProgress = { questToIncrement = quest }
                    )
                }
            }
        }

    // Dialogue: Log Quest Progress
    questToIncrement?.let { quest ->
        Dialog(onDismissRequest = { questToIncrement = null }) {
            NeonCard(borderColor = NeonPurple) {
                Text(
                    text = "Registrar Progresso",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Insira a quantidade completada de '${quest.title}':",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Increment Choices Shortcuts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val increments = if (quest.type == "RUNNING") listOf("1", "2", "5") else listOf("5", "10", "25")
                    increments.forEach { amount ->
                        Button(
                            onClick = { incrementValueString = amount },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (incrementValueString == amount) NeonPurple else Color(0xFF1D263B)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "+$amount" + (if (quest.type == "RUNNING") " km" else ""),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { questToIncrement = null },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("CANCELAR", fontFamily = FontFamily.Monospace)
                    }

                    Button(
                        onClick = {
                            val value = incrementValueString.toIntOrNull() ?: 0
                            if (value > 0) {
                                viewModel.incrementQuest(quest.id, value)
                            }
                            questToIncrement = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("REGISTRAR", color = VoidBackground, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Dialogue: Info RPG
    if (showInfoDialog) {
        Dialog(onDismissRequest = { showInfoDialog = false }) {
            NeonCard(borderColor = GoldenSun) {
                Text(
                    text = "O SISTEMA DE STATUS",
                    color = GoldenSun,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Assim como em um sistema de jogo de RPG, você é o protagonista da sua própria jornada!\n\n" +
                            "💪 STR (Força): Melhora a performance em exercícios explosivos e empurradas.\n\n" +
                            "🛡️ DEF (Defesa): Reduz a fadiga e estende séries estáticas.\n\n" +
                            "⚡ DEX (Destreza): Melhora a velocidade articular e transição.\n\n" +
                            "🏃‍♂️ AGI (Agilidade): Otimiza a coordenação cardíaca e saltos.\n\n" +
                            "🔋 STA (Estamina): Melhora a capacidade respiratória geral.\n\n" +
                            "Suba de nível ao acumular XP nas missões diárias e nos treinos completos! Distribua seus pontos para especializar seu físico.",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(20.dp))
                CyberButton(
                    text = "Entendido",
                    onClick = { showInfoDialog = false },
                    containerColor = GoldenSun
                )
            }
        }
    }

    // Floating XP Gain Badge
    AnimatedVisibility(
        visible = xpGainAnimAmount != null,
        enter = fadeIn() + slideInVertically(initialOffsetY = { 50 }) + scaleIn(initialScale = 0.8f),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -100 }) + scaleOut(targetScale = 1.2f),
        modifier = Modifier.align(Alignment.Center).offset(y = (-100).dp)
    ) {
        xpGainAnimAmount?.let { amount ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, NeonCyan, RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚡", fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = "+$amount XP ADQUIRIDO!",
                        color = NeonCyan,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    // Level Up Celebration overlay
    AnimatedVisibility(
        visible = showLevelUpAnim != null,
        enter = fadeIn() + scaleIn(initialScale = 0.5f),
        exit = fadeOut() + scaleOut(targetScale = 1.5f)
    ) {
        showLevelUpAnim?.let { newLevel ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "🏆 LEVEL UP! 🏆",
                        color = GoldenSun,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "VOCÊ ATINGIU O NÍVEL",
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$newLevel",
                        color = NeonCyan,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 72.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Seus limites físicos foram transcendidos!\n+5 pontos de atributo concedidos.",
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showLevelUpAnim = null },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                    ) {
                        Text("CONTINUAR JORNADA", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
}

@Composable
fun ProfileHudHeader(
    stats: UserStats,
    onInfoClick: () -> Unit
) {
    NeonCard(borderColor = NeonCyan) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            colors = listOf(NeonCyan, NeonPurple, NeonCyan)
                        )
                    )
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(VoidBackground),
                contentAlignment = Alignment.Center
            ) {
                // Return descriptive letter or symbol representing current avatar
                val avatarEmoji = when (stats.avatarResName) {
                    "avatar_blue_energy" -> "⚡"
                    "avatar_shadow_horde" -> "🥷"
                    "avatar_divine_light" -> "✨"
                    else -> "👤"
                }
                Text(
                    text = avatarEmoji,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stats.name.uppercase(),
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "RPG System Info",
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onInfoClick() }
                    )
                }
                
                Text(
                    text = stats.title,
                    color = NeonPurple,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(4.dp))
                
                // Level Indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "LV.",
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = " ${stats.level}",
                        color = GoldenSun,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }

            // Hex Rank Indicator
            HexRankIcon(rank = stats.rank)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // XP Progress Bar
        RpgProgressBar(
            label = "EXPERIÊNCIA (XP)",
            currentValue = stats.xp.toFloat(),
            maxValue = stats.xpNeeded.toFloat(),
            barColor = NeonCyan,
            unit = " XP"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Energy Bar
        RpgProgressBar(
            label = "ESTAMINA DIÁRIA",
            currentValue = stats.stamina.toFloat(),
            maxValue = stats.maxStamina.toFloat(),
            barColor = ElectricBlue,
            unit = "%"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StreakBadge(streakDays = stats.streak)
            
            // Gold Wallet
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1D263B))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = "🪙", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${stats.gold}g",
                    color = GoldenSun,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun AttributesPanel(
    stats: UserStats,
    onAllocate: (String) -> Unit
) {
    NeonCard(borderColor = NeonPurple) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ATRIBUTOS DO JOGADOR",
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            if (stats.availablePoints > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonPurple)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${stats.availablePoints} PTS",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StatControlRow(
                statName = "STR (Força)",
                statValue = stats.str,
                statDescription = "Aumenta a força muscular para flexões/barras",
                pointsAvailable = stats.availablePoints,
                onAllocate = { onAllocate("STR") }
            )
            StatControlRow(
                statName = "DEF (Defesa)",
                statValue = stats.end,
                statDescription = "Reduz a fadiga e estende séries estáticas",
                pointsAvailable = stats.availablePoints,
                onAllocate = { onAllocate("END") }
            )
            StatControlRow(
                statName = "DEX (Destreza)",
                statValue = stats.dex,
                statDescription = "Melhora a velocidade articular e transição",
                pointsAvailable = stats.availablePoints,
                onAllocate = { onAllocate("DEX") }
            )
            StatControlRow(
                statName = "AGI (Agilidade)",
                statValue = stats.agi,
                statDescription = "Otimiza a coordenação cardíaca e saltos",
                pointsAvailable = stats.availablePoints,
                onAllocate = { onAllocate("AGI") }
            )
            StatControlRow(
                statName = "STA (Estamina)",
                statValue = stats.sta,
                statDescription = "Melhora a capacidade respiratória geral",
                pointsAvailable = stats.availablePoints,
                onAllocate = { onAllocate("STA") }
            )
        }
    }
}

@Composable
fun DailyQuestCard(
    quest: DailyQuest,
    onLogProgress: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberCard)
            .border(
                width = 1.dp,
                color = if (quest.isCompleted) SuccessGreen.copy(alpha = 0.5f) else Color(0xFF1D263B),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = CyberCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checked/Completed Checkbox Indicator
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (quest.isCompleted) SuccessGreen.copy(alpha = 0.15f) else Color(0xFF161C2C))
                    .border(
                        width = 1.5.dp,
                        color = if (quest.isCompleted) SuccessGreen else TextSecondary.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (quest.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Quest Completed",
                        tint = SuccessGreen,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    val questIcon = when (quest.type) {
                        "PUSHUP" -> "💪"
                        "SITUP" -> "🧘"
                        "SQUAT" -> "🏋️"
                        "RUNNING" -> "🏃‍♂️"
                        else -> "🎯"
                    }
                    Text(
                        text = questIcon,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.title,
                    color = if (quest.isCompleted) SuccessGreen else TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF2C1616))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${quest.attributeReward} +1",
                            color = Color(0xFFFF5252),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+${quest.xpReward} XP  •  +${quest.goldReward}g",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${quest.currentValue}/${quest.targetValue}",
                    color = if (quest.isCompleted) SuccessGreen else NeonCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                if (!quest.isCompleted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    IconButton(
                        onClick = onLogProgress,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = NeonPurple, contentColor = Color.White),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Log Progress",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
