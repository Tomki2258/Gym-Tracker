package com.example.gymtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.time.Instant

@Entity
data class Measurement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val reps:Int,
    val weight: Float,
    val exerciseName: String,
    val date: java.util.Date = Date.from(Instant.now())
) {
    val weekOfTheYear: Int
        get() {
            val calendar = java.util.Calendar.getInstance()
            calendar.time = date
            return calendar.get(java.util.Calendar.WEEK_OF_YEAR)
        }
}