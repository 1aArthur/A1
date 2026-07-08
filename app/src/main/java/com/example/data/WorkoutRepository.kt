package com.example.data

import com.example.service.QuestFitServiceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.roundToInt

class WorkoutRepository(val dao: WorkoutDao) {

    private val questFitService = QuestFitServiceProvider.provideQuestFitService(dao)

    val userStats: Flow<UserStats?> = dao.getUserStats()
    val dailyQuests: Flow<List<DailyQuest>> = dao.getDailyQuests()
    val completedWorkouts: Flow<List<CompletedWorkout>> = dao.getCompletedWorkouts()
    val cosmetics: Flow<List<CosmeticItem>> = dao.getCosmetics()

    suspend fun initializeDatabaseIfEmpty() {
        val stats = dao.getUserStatsSync()
        if (stats == null) {
            // 1. Insert initial UserStats
            val defaultStats = UserStats()
            dao.insertUserStats(defaultStats)

            // 2. Insert default Daily Quests
            val defaultQuests = listOf(
                DailyQuest(
                    title = "Flexões Diárias (Push-ups)",
                    type = "PUSHUP",
                    targetValue = 15, // Starts small, scales with level
                    currentValue = 0,
                    xpReward = 50,
                    goldReward = 30,
                    attributeReward = "STR"
                ),
                DailyQuest(
                    title = "Abdominais Diários (Sit-ups)",
                    type = "SITUP",
                    targetValue = 20,
                    currentValue = 0,
                    xpReward = 50,
                    goldReward = 30,
                    attributeReward = "END"
                ),
                DailyQuest(
                    title = "Agachamentos Diários (Squats)",
                    type = "SQUAT",
                    targetValue = 20,
                    currentValue = 0,
                    xpReward = 50,
                    goldReward = 30,
                    attributeReward = "STA"
                ),
                DailyQuest(
                    title = "Corrida de Resistência (Running)",
                    type = "RUNNING",
                    targetValue = 2, // 2 km
                    currentValue = 0,
                    xpReward = 80,
                    goldReward = 50,
                    attributeReward = "AGI"
                )
            )
            dao.insertDailyQuests(defaultQuests)

            // 3. Insert default cosmetic rewards/items
            val defaultCosmetics = listOf(
                CosmeticItem("title_reawakened", "The Reawakened One", "TITLE", "The Reawakened One", 0, isPurchased = true, isEquipped = true),
                CosmeticItem("title_shadow", "Monarca das Sombras", "TITLE", "Monarca das Sombras", 150, isPurchased = false, isEquipped = false),
                CosmeticItem("title_iron", "Vontade de Ferro", "TITLE", "Vontade de Ferro", 100, isPurchased = false, isEquipped = false),
                CosmeticItem("title_god", "Deus da Calistenia", "TITLE", "Deus da Calistenia", 300, isPurchased = false, isEquipped = false),
                
                CosmeticItem("avatar_default", "Recruta Inicial", "AVATAR", "avatar_default", 0, isPurchased = true, isEquipped = true),
                CosmeticItem("avatar_blue_energy", "Energia Ciano", "AVATAR", "avatar_blue_energy", 120, isPurchased = false, isEquipped = false),
                CosmeticItem("avatar_shadow_horde", "Guerreiro Sombrio", "AVATAR", "avatar_shadow_horde", 200, isPurchased = false, isEquipped = false),
                CosmeticItem("avatar_divine_light", "Luz Divina", "AVATAR", "avatar_divine_light", 350, isPurchased = false, isEquipped = false)
            )
            dao.insertCosmeticItems(defaultCosmetics)
        }
    }

    suspend fun incrementQuestProgress(questId: Int, value: Int) {
        questFitService.trackAndUpdateMission(questId, value)
    }

    suspend fun completeWorkout(title: String, category: String, baseXP: Int, baseGold: Int) {
        // Record completed workout
        val completed = CompletedWorkout(
            title = title,
            category = category,
            xpGained = baseXP,
            goldGained = baseGold
        )
        dao.insertCompletedWorkout(completed)

        // Award rewards
        awardRewards(baseXP, baseGold, null)
    }

    private suspend fun awardRewards(xpReward: Int, goldReward: Int, attributeToReward: String?) {
        val stats = dao.getUserStatsSync() ?: return
        var newXp = stats.xp + xpReward
        var newLevel = stats.level
        var newXpNeeded = stats.xpNeeded
        var earnedPoints = 0

        while (newXp >= newXpNeeded) {
            newXp -= newXpNeeded
            newLevel++
            newXpNeeded = (newXpNeeded * 1.5).roundToInt()
            earnedPoints += 5 // 5 stat points per level up!
        }

        // Rank updates based on levels
        val newRank = when {
            newLevel >= 25 -> "S-Rank"
            newLevel >= 18 -> "A-Rank"
            newLevel >= 12 -> "B-Rank"
            newLevel >= 7 -> "C-Rank"
            newLevel >= 4 -> "D-Rank"
            else -> "E-Rank"
        }

        // Increment specific attribute if rewarded from a quest
        var newStr = stats.str
        var newEnd = stats.end
        var newDex = stats.dex
        var newAgi = stats.agi
        var newSta = stats.sta

        if (attributeToReward != null) {
            when (attributeToReward) {
                "STR" -> newStr++
                "END" -> newEnd++
                "DEX" -> newDex++
                "AGI" -> newAgi++
                "STA" -> newSta++
            }
        }

        val updatedStats = stats.copy(
            level = newLevel,
            xp = newXp,
            xpNeeded = newXpNeeded,
            rank = newRank,
            gold = stats.gold + goldReward,
            availablePoints = stats.availablePoints + earnedPoints,
            str = newStr,
            end = newEnd,
            dex = newDex,
            agi = newAgi,
            sta = newSta,
            lastActiveTimestamp = System.currentTimeMillis()
        )
        dao.insertUserStats(updatedStats)
    }

    suspend fun allocateAttributePoint(attribute: String) {
        val stats = dao.getUserStatsSync() ?: return
        if (stats.availablePoints <= 0) return

        val updatedStats = when (attribute) {
            "STR" -> stats.copy(str = stats.str + 1, availablePoints = stats.availablePoints - 1)
            "END" -> stats.copy(end = stats.end + 1, availablePoints = stats.availablePoints - 1)
            "DEX" -> stats.copy(dex = stats.dex + 1, availablePoints = stats.availablePoints - 1)
            "AGI" -> stats.copy(agi = stats.agi + 1, availablePoints = stats.availablePoints - 1)
            "STA" -> stats.copy(sta = stats.sta + 1, availablePoints = stats.availablePoints - 1)
            else -> stats
        }
        dao.insertUserStats(updatedStats)
    }

    suspend fun buyCosmeticItem(itemId: String): Boolean {
        val stats = dao.getUserStatsSync() ?: return false
        val cosmeticsList = dao.getCosmeticsSync()
        val item = cosmeticsList.find { it.id == itemId } ?: return false

        if (item.isPurchased) return true
        if (stats.gold < item.cost) return false

        // Spend gold
        val updatedStats = stats.copy(gold = stats.gold - item.cost)
        dao.insertUserStats(updatedStats)

        // Mark purchased
        val updatedItem = item.copy(isPurchased = true)
        dao.updateCosmeticItem(updatedItem)
        return true
    }

    suspend fun equipCosmeticItem(itemId: String) {
        val stats = dao.getUserStatsSync() ?: return
        val cosmeticsList = dao.getCosmeticsSync()
        val itemToEquip = cosmeticsList.find { it.id == itemId } ?: return

        if (!itemToEquip.isPurchased) return

        // Unequip all of the same type, equip selected
        for (item in cosmeticsList) {
            if (item.type == itemToEquip.type && item.isEquipped) {
                dao.updateCosmeticItem(item.copy(isEquipped = false))
            }
        }

        dao.updateCosmeticItem(itemToEquip.copy(isEquipped = true))

        // Update stats
        val updatedStats = if (itemToEquip.type == "TITLE") {
            stats.copy(title = itemToEquip.value)
        } else {
            stats.copy(avatarResName = itemToEquip.value)
        }
        dao.insertUserStats(updatedStats)
    }

    suspend fun resetDailyQuests() {
        val stats = dao.getUserStatsSync() ?: return
        val currentQuests = dao.getDailyQuestsSync()

        // Recalculate target values dynamically based on current user level!
        // This answers the user's specific requirement: "ajuste automático com base no progresso"
        val level = stats.level
        val updatedQuests = currentQuests.map { quest ->
            val scaledTarget = when (quest.type) {
                "PUSHUP" -> 15 + (level * 3)
                "SITUP" -> 20 + (level * 4)
                "SQUAT" -> 20 + (level * 4)
                "RUNNING" -> 2 + (level * 0.5).roundToInt()
                else -> quest.targetValue
            }
            quest.copy(
                currentValue = 0,
                targetValue = scaledTarget,
                isCompleted = false
            )
        }
        dao.insertDailyQuests(updatedQuests)

        // Reset streak if we didn't train, or increment streak.
        // Let's increment streak by 1 for fun or keep it active.
        dao.insertUserStats(stats.copy(streak = stats.streak + 1, stamina = 100))
    }
}
