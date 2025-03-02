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
    var bestMeasurement: Measurement? = null,
    val exerciseDecsArg: String = "",
    val exericseEntity: ExericseEntity?,
    val isCustom: Boolean = false
) : Serializable {
    var categoryString = ""
    var exerciseDecs = ""

    init {
        categoryString = category.toString().lowercase()
        categoryString = categoryString.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        setBestMeasurement()

        if(isCustom){
            exerciseDecs = "here is exercise description from CUSTOM exercise"
        }
        else{
            exerciseDecs = "here is exercise description from NON custom exercise"
        }

        Log.d("READING MEASUREMENTS","")
        for(measurement in measurementsList){
            Log.d(measurement.exerciseName,measurement.reps.toString())
        }
    }

    fun getPhotoResourceId(context: Context): Int {
        val resourceId = context.resources.getIdentifier(photoString, "drawable", context.packageName)
        return if (resourceId != 0) {
            resourceId
        } else {
            context.resources.getIdentifier("information", "drawable", context.packageName)
        }
    }

    var weightDiff: Float = 0f

    fun setBestMeasurement() {
        bestMeasurement = measurementsList.maxByOrNull { it.reps * it.weight }
    }

    fun getExerciseLogInfo(){
        Log.d("Exercise Log Info", "Name: ${name}\nCattegory: ${category}\nDecs(optional): ${exerciseDecs}")
    }

    fun doCustomExercise(){
        //saving custom exercises made by users
    }

}