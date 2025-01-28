// app/src/main/java/com/example/gymtracker/roomdb/TrainingPlan.kt
package com.example.gymtracker.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_plan")
data class TrainingPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val day: String,
    val exercise: String
)