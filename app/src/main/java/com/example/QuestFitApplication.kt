package com.example

import android.app.Application
import com.example.data.WorkoutDatabase
import com.example.data.WorkoutRepository

class QuestFitApplication : Application() {
    val database by lazy { WorkoutDatabase.getDatabase(this) }
    val repository by lazy { WorkoutRepository(database.workoutDao()) }

    override fun onCreate() {
        super.onCreate()
        // Initialize static DB check if empty
    }
}
