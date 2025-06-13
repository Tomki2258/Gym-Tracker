// app/src/main/java/com/example/gymtracker/roomdb/TrainingplanDatabase.kt
package com.tamusdev.gymtracker.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tamusdev.gymtracker.data.TrainingPlan

@Database(entities = [TrainingPlan::class], version = 1)
abstract class TrainingplanDatabase : RoomDatabase() {
    abstract fun trainingPlanDao(): TrainingPlanDao
}