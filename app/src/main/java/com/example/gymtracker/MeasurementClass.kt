package com.example.gymtracker

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Locale

class MeasurementClass(resp : Int , weight : Double,from:String) {
    val reps = resp
    val weight = weight
    val date = System.currentTimeMillis()
    val from = "category"

    fun getWeekOfYear(): Int {
        val localDate = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
        return localDate.get(WeekFields.of(Locale.getDefault()).weekOfYear())
    }
}