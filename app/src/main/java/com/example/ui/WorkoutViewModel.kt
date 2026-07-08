package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
        }
    }

    val userStats: StateFlow<UserStats?> = repository.userStats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val dailyQuests: StateFlow<List<DailyQuest>> = repository.dailyQuests
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val completedWorkouts: StateFlow<List<CompletedWorkout>> = repository.completedWorkouts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val cosmetics: StateFlow<List<CosmeticItem>> = repository.cosmetics
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun incrementQuest(questId: Int, value: Int) {
        viewModelScope.launch {
            repository.incrementQuestProgress(questId, value)
        }
    }

    fun allocatePoint(attribute: String) {
        viewModelScope.launch {
            repository.allocateAttributePoint(attribute)
        }
    }

    fun buyCosmetic(itemId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.buyCosmeticItem(itemId)
            onResult(success)
        }
    }

    fun equipCosmetic(itemId: String) {
        viewModelScope.launch {
            repository.equipCosmeticItem(itemId)
        }
    }

    fun finishWorkout(title: String, category: String, baseXP: Int, baseGold: Int) {
        viewModelScope.launch {
            repository.completeWorkout(title, category, baseXP, baseGold)
        }
    }

    fun resetDailyQuests() {
        viewModelScope.launch {
            repository.resetDailyQuests()
        }
    }
}

class WorkoutViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
