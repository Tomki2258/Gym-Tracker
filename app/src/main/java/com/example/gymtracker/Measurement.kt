package com.example.gymtracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Locale

@Entity
data class Measurement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val reps:Int,
    val weight: Float,
    val exerciseName: String
) {
}