// app/src/main/java/com/example/gymtracker/ExerciseClass.kt
package com.example.gymtracker.data

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.Serializable

class ExerciseClass(
    val name: String,
    val category: Categories,
    val photoString: String = name.replace(" ", "_").lowercase(),
    var measurementsList: MutableList<Measurement> = mutableListOf<Measurement>(),
    var bestMeasurement: Measurement? = null,
    val exericseEntity: ExericseEntity?,
    val isCustom: Boolean = false
) : Serializable {
    var categoryString = ""
    var exerciseDecs = ""
    private lateinit var exerciseBitMap:ImageBitmap
    init {
        categoryString = category.toString().lowercase()
        categoryString = categoryString.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        setBestMeasurement()

        if(isCustom){
            val loadedDescription = exericseEntity?.description
            if (loadedDescription != null) {
                if(!loadedDescription.equals("")){
                    exerciseDecs = loadedDescription
                } else{
                    exerciseDecs = "Description is empty"
                }
            }
        }
        else{
            exerciseDecs = "here is exercise description from NON custom exercise"
        }

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

    fun loadImage(context: Context) {
        if(!isCustom) return
        context.openFileInput("${name}.JPEG").use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            exerciseBitMap = bitmap.asImageBitmap()
        }
    }
    fun removeImage(context: Context){
        context.deleteFile("${name}.JPEG")
    }
    fun getImage(): ImageBitmap {
        return exerciseBitMap
    }
}