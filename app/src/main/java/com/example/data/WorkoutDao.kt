package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM user_stats WHERE id = :id LIMIT 1")
    fun getUserStats(id: Int = 1): Flow<UserStats?>

    @Query("SELECT * FROM user_stats WHERE id = :id LIMIT 1")
    suspend fun getUserStatsSync(id: Int = 1): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStats)

    @Query("SELECT * FROM daily_quests")
    fun getDailyQuests(): Flow<List<DailyQuest>>

    @Query("SELECT * FROM daily_quests")
    suspend fun getDailyQuestsSync(): List<DailyQuest>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyQuests(quests: List<DailyQuest>)

    @Update
    suspend fun updateDailyQuest(quest: DailyQuest)

    @Query("SELECT * FROM completed_workouts ORDER BY timestamp DESC")
    fun getCompletedWorkouts(): Flow<List<CompletedWorkout>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedWorkout(workout: CompletedWorkout)

    @Query("SELECT * FROM cosmetic_items")
    fun getCosmetics(): Flow<List<CosmeticItem>>

    @Query("SELECT * FROM cosmetic_items")
    suspend fun getCosmeticsSync(): List<CosmeticItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCosmeticItems(items: List<CosmeticItem>)

    @Update
    suspend fun updateCosmeticItem(item: CosmeticItem)
}
