package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val name: String = "Recruit",
    val level: Int = 1,
    val xp: Int = 0,
    val xpNeeded: Int = 100,
    val rank: String = "E-Rank",
    val streak: Int = 1,
    val stamina: Int = 100,
    val maxStamina: Int = 100,
    val str: Int = 10,
    val end: Int = 10,
    val dex: Int = 10,
    val agi: Int = 10,
    val sta: Int = 10,
    val availablePoints: Int = 5,
    val gold: Int = 150,
    val title: String = "The Reawakened One",
    val avatarResName: String = "avatar_default",
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_quests")
data class DailyQuest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // PUSHUP, SITUP, SQUAT, RUNNING
    val targetValue: Int,
    val currentValue: Int,
    val xpReward: Int,
    val goldReward: Int,
    val attributeReward: String, // STR, END, DEX, AGI, STA
    val isCompleted: Boolean = false
)

@Entity(tableName = "completed_workouts")
data class CompletedWorkout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // PEITO, COSTAS, OMBROS, PERNAS, CORE, CARDIO
    val xpGained: Int,
    val goldGained: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cosmetic_items")
data class CosmeticItem(
    @PrimaryKey val id: String,
    val title: String,
    val type: String, // TITLE, AVATAR
    val value: String, // Value of title string or avatar illustration descriptor
    val cost: Int,
    val isPurchased: Boolean = false,
    val isEquipped: Boolean = false
)
