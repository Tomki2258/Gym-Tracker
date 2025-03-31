// app/src/main/java/com/example/gymtracker/roomdb/TrainingplanDatabase.kt
package com.example.gymtracker.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gymtracker.data.TrainingPlan

@Database(entities = [TrainingPlan::class], version = 1)
abstract class TrainingplanDatabase : RoomDatabase() {
    abstract fun trainingPlanDao(): TrainingPlanDao
}