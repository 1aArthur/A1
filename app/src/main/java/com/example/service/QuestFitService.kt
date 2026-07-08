package com.example.service

import com.example.data.DailyQuest
import com.example.data.UserStats
import com.example.data.WorkoutDao
import kotlinx.coroutines.flow.Flow
import kotlin.math.roundToInt

/**
 * Representa o resultado detalhado de uma atualização ou conclusão de missão.
 */
sealed class MissionUpdateResult {
    data class ProgressUpdated(val quest: DailyQuest, val progressAdded: Int) : MissionUpdateResult()
    data class Completed(
        val quest: DailyQuest,
        val xpRewarded: Int,
        val goldRewarded: Int,
        val attributeRewarded: String?,
        val leveledUp: Boolean,
        val newLevel: Int
    ) : MissionUpdateResult()
    data class AlreadyCompleted(val quest: DailyQuest) : MissionUpdateResult()
    data class Error(val message: String) : MissionUpdateResult()
}

/**
 * Classe de Serviço oficial do 'QuestFit' para gerenciar, rastrear e atualizar o status diário de missões,
 * garantindo que a experiência (XP) seja calculada de forma precisa e as recompensas sejam creditadas ao concluir.
 */
class QuestFitService(private val dao: WorkoutDao) {

    /**
     * Retorna o fluxo reativo de missões diárias.
     */
    val dailyQuests: Flow<List<DailyQuest>> = dao.getDailyQuests()

    /**
     * Retorna o fluxo reativo do status e atributos do usuário.
     */
    val userStats: Flow<UserStats?> = dao.getUserStats()

    /**
     * Registra o progresso de uma missão diária e calcula recompensas se concluída.
     *
     * @param questId O identificador único da missão.
     * @param progressAmount Quantidade de repetições ou metros a adicionar ao progresso atual.
     * @return O resultado da operação encapsulado em [MissionUpdateResult].
     */
    suspend fun trackAndUpdateMission(questId: Int, progressAmount: Int): MissionUpdateResult {
        val currentQuests = dao.getDailyQuestsSync()
        val quest = currentQuests.find { it.id == questId }
            ?: return MissionUpdateResult.Error("Missão com ID $questId não encontrada.")

        if (quest.isCompleted) {
            return MissionUpdateResult.AlreadyCompleted(quest)
        }

        val updatedProgress = (quest.currentValue + progressAmount).coerceAtMost(quest.targetValue)
        val isNowCompleted = updatedProgress >= quest.targetValue
        val updatedQuest = quest.copy(currentValue = updatedProgress, isCompleted = isNowCompleted)

        // Persiste a alteração no banco de dados local via DAO
        dao.updateDailyQuest(updatedQuest)

        return if (isNowCompleted) {
            // Calcula o bônus de XP, Gold e Atributos ao completar a missão
            val currentStats = dao.getUserStatsSync()
                ?: return MissionUpdateResult.Error("Status do usuário indisponíveis para premiação.")

            var newXp = currentStats.xp + quest.xpReward
            var newLevel = currentStats.level
            var newXpNeeded = currentStats.xpNeeded
            var extraStatPoints = 0
            var didLevelUp = false

            // Algoritmo de level up progressivo (multi-level up suportado)
            while (newXp >= newXpNeeded) {
                newXp -= newXpNeeded
                newLevel++
                newXpNeeded = (newXpNeeded * 1.5).roundToInt()
                extraStatPoints += 5
                didLevelUp = true
            }

            // Atualização automática de ranque baseada em níveis
            val newRank = when {
                newLevel >= 25 -> "S-Rank"
                newLevel >= 18 -> "A-Rank"
                newLevel >= 12 -> "B-Rank"
                newLevel >= 7 -> "C-Rank"
                newLevel >= 4 -> "D-Rank"
                else -> "E-Rank"
            }

            // Distribuição automática de ponto no atributo correspondente da quest
            var newStr = currentStats.str
            var newEnd = currentStats.end
            var newDex = currentStats.dex
            var newAgi = currentStats.agi
            var newSta = currentStats.sta

            when (quest.attributeReward) {
                "STR" -> newStr++
                "END" -> newEnd++
                "DEX" -> newDex++
                "AGI" -> newAgi++
                "STA" -> newSta++
            }

            val updatedStats = currentStats.copy(
                level = newLevel,
                xp = newXp,
                xpNeeded = newXpNeeded,
                rank = newRank,
                gold = currentStats.gold + quest.goldReward,
                availablePoints = currentStats.availablePoints + extraStatPoints,
                str = newStr,
                end = newEnd,
                dex = newDex,
                agi = newAgi,
                sta = newSta,
                lastActiveTimestamp = System.currentTimeMillis()
            )

            // Persiste os novos atributos atualizados do jogador no banco de dados
            dao.insertUserStats(updatedStats)

            MissionUpdateResult.Completed(
                quest = updatedQuest,
                xpRewarded = quest.xpReward,
                goldRewarded = quest.goldReward,
                attributeRewarded = quest.attributeReward,
                leveledUp = didLevelUp,
                newLevel = newLevel
            )
        } else {
            MissionUpdateResult.ProgressUpdated(updatedQuest, progressAmount)
        }
    }
}

/**
 * Provedor de Serviços (Provider Pattern) responsável por expor de maneira segura,
 * thread-safe e em padrão Singleton o [QuestFitService] para toda a aplicação.
 */
object QuestFitServiceProvider {
    @Volatile
    private var instance: QuestFitService? = null

    /**
     * Fornece a instância Singleton do [QuestFitService].
     *
     * @param dao O [WorkoutDao] para acesso direto às entidades do Room.
     * @return A instância persistente de [QuestFitService].
     */
    fun provideQuestFitService(dao: WorkoutDao): QuestFitService {
        return instance ?: synchronized(this) {
            instance ?: QuestFitService(dao).also { instance = it }
        }
    }
}
