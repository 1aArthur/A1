package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CosmeticItem
import com.example.ui.WorkoutViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.NeonCard
import com.example.ui.theme.*

@Composable
fun QuestShopScreen(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cosmetics by viewModel.cosmetics.collectAsStateWithLifecycle()
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val completedWorkouts by viewModel.completedWorkouts.collectAsStateWithLifecycle()

    if (userStats == null || cosmetics.isEmpty()) {
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
                    color = NeonPurple,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "ACESSANDO MERCADO DE COSMA...",
                    color = NeonPurple,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Carregando títulos e conquistas",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
        return
    }

    var activeSubTab by remember { mutableStateOf("LOJA") } // LOJA, BOSS, CONQUISTAS
    val subTabs = listOf("LOJA", "CONCURSOS (BOSS)", "CONQUISTAS")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBackground)
            .padding(top = 16.dp)
    ) {
        // 1. Gold Banner & Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SISTEMA DE PROGRESSÃO",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Desbloqueie títulos, derrote chefes e marque suas conquistas.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Sub-tab selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            subTabs.forEach { tab ->
                val isSelected = activeSubTab == tab
                Button(
                    onClick = { activeSubTab = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) NeonPurple else Color(0xFF141926)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = tab,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Sub-tab Contents
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            when (activeSubTab) {
                "LOJA" -> ShopSubTab(
                    cosmetics = cosmetics,
                    goldAvailable = userStats?.gold ?: 0,
                    onBuy = { item ->
                        viewModel.buyCosmetic(item.id) { success ->
                            if (success) {
                                Toast.makeText(context, "Cosmético ${item.title} adquirido!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Ouro insuficiente para comprar!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onEquip = { viewModel.equipCosmetic(it.id) }
                )
                "CONCURSOS (BOSS)" -> BossSubTab(
                    onCompleteBoss = { xp, gold ->
                        viewModel.finishWorkout("Chefe Semanal: Titan Monarca", "DESAFIO", xp, gold)
                        Toast.makeText(context, "Chefe Derrotado! Recompensas creditadas!", Toast.LENGTH_LONG).show()
                    }
                )
                "CONQUISTAS" -> AchievementsSubTab(
                    completedCount = completedWorkouts.size,
                    statPointsAllocated = (userStats?.str ?: 10) + (userStats?.end ?: 10) + (userStats?.dex ?: 10) + (userStats?.agi ?: 10) + (userStats?.sta ?: 10) - 50,
                    streak = userStats?.streak ?: 1,
                    cosmeticsCount = cosmetics.filter { it.isPurchased }.size
                )
            }
        }
    }
}

@Composable
fun ShopSubTab(
    cosmetics: List<CosmeticItem>,
    goldAvailable: Int,
    onBuy: (CosmeticItem) -> Unit,
    onEquip: (CosmeticItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            // Gold Wallet indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF151D30))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Mercado",
                        tint = GoldenSun
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SUA CARTEIRA:",
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "🪙 ${goldAvailable}g Ouro",
                    color = GoldenSun,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
        }

        items(cosmetics) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberCard)
                    .border(1.dp, if (item.isEquipped) NeonCyan else Color(0xFF1D263B), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Type visual badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (item.type == "TITLE") NeonPurple.copy(alpha = 0.15f) else NeonCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (item.type == "TITLE") "🏷️" else "👤",
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.title,
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (item.type == "TITLE") "Título de RPG" else "Avatar Cosmético",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Buy / Equip button
                when {
                    item.isEquipped -> {
                        Button(
                            onClick = {},
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(disabledContainerColor = NeonCyan.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("EQUIPADO", color = NeonCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                    item.isPurchased -> {
                        Button(
                            onClick = { onEquip(item) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("EQUIPAR", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                    else -> {
                        Button(
                            onClick = { onBuy(item) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldenSun),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("🪙 ${item.cost}g", color = VoidBackground, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BossSubTab(
    onCompleteBoss: (Int, Int) -> Unit
) {
    var bossDefeated by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NeonCard(borderColor = ErrorRed, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "BOSS SEMANAL ATIVO",
                color = ErrorRed,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Boss Visual Art Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF2C0B0B), Color(0xFF100505))
                        )
                    )
                    .border(1.dp, ErrorRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "😈", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "TITAN DAS PARALELAS LENDÁRIAS",
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "HP: 12.000 / 12.000",
                        color = ErrorRed,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "CONDIÇÃO DE VITÓRIA:",
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = "Para derrotar este chefe e reivindicar o loot lendário, você deve completar qualquer treino do nível 'Avançado' com sucesso hoje.",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Divider(color = Color(0xFF2C0B0B), thickness = 1.dp)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "RECOMPENSAS DO ABATE (LOOT):",
                color = GoldenSun,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "💎 +600 XP", color = NeonCyan, fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = "🪙 +400g Ouro", color = GoldenSun, fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (!bossDefeated) {
            Button(
                onClick = {
                    bossDefeated = true
                    onCompleteBoss(600, 400)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Shield, contentDescription = "Batalhar", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("REIVINDICAR RELATÓRIO DO COMBATE", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF141926))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✅ CHEFE ABATIDO ESTA SEMANA",
                    color = SuccessGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun AchievementsSubTab(
    completedCount: Int,
    statPointsAllocated: Int,
    streak: Int,
    cosmeticsCount: Int
) {
    val achievementsList = listOf(
        AchievementItem(
            "Despertar Inicial",
            "Complete seu primeiro treino no aplicativo.",
            "🏆",
            completedCount >= 1
        ),
        AchievementItem(
            "Vontade de Aço",
            "Mantenha um streak diário de 3 ou mais dias de treino.",
            "🔥",
            streak >= 3
        ),
        AchievementItem(
            "Distribuidor Lendário",
            "Aloque um total de 5 pontos de atributo em seu avatar.",
            "⚡",
            statPointsAllocated >= 5
        ),
        AchievementItem(
            "Colecionador de Títulos",
            "Adquira pelo menos 2 cosméticos diferentes no mercado.",
            "💎",
            cosmeticsCount >= 2
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        items(achievementsList) { ach ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberCard)
                    .border(
                        width = 1.dp,
                        color = if (ach.isUnlocked) GoldenSun.copy(alpha = 0.5f) else Color(0xFF1D263B),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Trophy/Badge Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (ach.isUnlocked) GoldenSun.copy(alpha = 0.15f) else Color(0xFF161C2C)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = ach.icon, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ach.title,
                        color = if (ach.isUnlocked) GoldenSun else TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = ach.description,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (ach.isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Unblocked",
                        tint = GoldenSun,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MilitaryTech,
                        contentDescription = "Locked",
                        tint = TextSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

data class AchievementItem(
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean
)
