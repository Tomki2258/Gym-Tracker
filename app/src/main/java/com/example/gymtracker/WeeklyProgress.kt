package com.example.gymtracker

import android.util.Log

class WeeklyProgress(private val measurements: List<Measurement>, val weekNumber: Int) {
    val year: Int
    val avgWeight: Float = setAvgWeight()

    init {
        this.year = measurements[0].date.year + 1900
        Log.d("Week number: $weekNumber", measurements.size.toString())
    }

    fun getMeasurements(): List<Measurement> {
        return measurements
    }
    fun setAvgWeight(): Float {
        var totalWeight = 0.0f
        var totalReps = 0
        for (measurement in measurements) {
            totalWeight += measurement.weight * measurement.reps
            totalReps += measurement.reps
        }
        return totalWeight / totalReps
    }
}