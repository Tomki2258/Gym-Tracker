// app/src/main/java/com/example/gymtracker/ExerciseClass.kt
package com.example.gymtracker

import android.content.Context
import android.util.Log
import java.io.Serializable
import java.nio.file.Path

class ExerciseClass(
    val name: String,
    val category: String,
    val descFilePath: String,
    val photoString: String = name.replace(" ", "_").lowercase(),
    val measurementsList: MutableList<MeasurementClass> = mutableListOf<MeasurementClass>(),
    var bestMeasurement: MeasurementClass? = null
) : Serializable {
    fun getPhotoResourceId(context: Context): Int {
        return context.resources.getIdentifier(photoString, "drawable", context.packageName)
    }
    fun SetBestMeasurement() {
        if (measurementsList.isNotEmpty()) {
            bestMeasurement = measurementsList.maxByOrNull { it.weight }
        }
    }
    fun printList() {
        for (measurement in measurementsList) {
            Log.d("Reps: ${measurement.reps}", "Date: ${measurement.date}")
        }
    }

}