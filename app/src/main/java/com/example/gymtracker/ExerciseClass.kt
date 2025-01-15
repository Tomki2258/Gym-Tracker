// app/src/main/java/com/example/gymtracker/ExerciseClass.kt
package com.example.gymtracker

import android.content.Context
import android.util.Log
import java.io.Serializable

class ExerciseClass(
    val name: String,
    val category: Categories,
    val photoString: String = name.replace(" ", "_").lowercase(),
    var measurementsList: MutableList<Measurement> = mutableListOf<Measurement>(),
    var bestMeasurement: Measurement? = null
) : Serializable {
    var categoryString = ""
    var exerciseDecs = ""
    init {
        categoryString = category.toString().lowercase()
        categoryString = categoryString.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
    fun getPhotoResourceId(context: Context): Int {
        val resourceId = context.resources.getIdentifier(photoString, "drawable", context.packageName)
        return if (resourceId != 0) {
            resourceId
        } else {
            context.resources.getIdentifier("information", "drawable", context.packageName)
        }
    }

    fun SetBestMeasurement() {
        if (measurementsList.isNotEmpty()) {
            bestMeasurement = measurementsList.maxByOrNull { it.weight }
        }
    }


}