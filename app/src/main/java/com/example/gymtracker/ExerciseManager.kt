// ExerciseManager.kt
package com.example.gymtracker

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.gymtracker.roomdb.MeasurementDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object ExerciseManager {
    private const val PREFS_NAME = "exercise_prefs"
    private const val KEY_EXERCISES = "exercises"
    var exercises by mutableStateOf(listOf<ExerciseClass>())

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val exercisesJson = prefs.getString(KEY_EXERCISES, "[]")
        exercises = LoadExercises()
        // Load measurements from the database
        val db = MeasurementDatabase.getInstance(context)
        loadMeasurementsFromDatabase(db)
        Thread{
            LoadDescriptions()
        }.start()
        Thread {
            exercises.forEach { exercise ->
                exercise.SetBestMeasurement()
            }
        }.start()
    }

    private fun loadMeasurementsFromDatabase(db: MeasurementDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            val measurements = db.measurementDao().getAllMeasurements().first()
            exercises.forEach { exercise ->
                exercise.measurementsList = measurements.filter { measurement ->
                    measurement.exerciseName == exercise.name
                }.toMutableList()
            }
        }
    }
    fun LoadDescriptions(){
        exercises.forEach { exercise ->
            Thread {
                val url = exercise.name.replace(" ", "").lowercase()
                val description = ApiManager.getExercise(url)
                exercise.exerciseDecs = description
            }.start()
        }
    }
    fun LoadExercises(): List<ExerciseClass> {
        val exerciseList = listOf(
            ExerciseClass("Chest fly", Categories.CHEST),
            ExerciseClass("Leg curl", Categories.LEGS),
            ExerciseClass("Leg press", Categories.LEGS),
            ExerciseClass("Dumbbell biceps", Categories.BICEPS),
            ExerciseClass("Bench press", Categories.CHEST),
            ExerciseClass("Seated barbell press", Categories.SHOULDERS),
            ExerciseClass("Dumbbell press", Categories.CHEST),
            ExerciseClass("Barbell Curl", Categories.BACK),
            ExerciseClass("Tricep Pushdown", Categories.TRICEPS),
            ExerciseClass("Seated Row", Categories.BACK),
            ExerciseClass("Lat Pulldown", Categories.BACK),
            ExerciseClass("Cock sucking", Categories.ABS),
        )
        return exerciseList
    }

    fun GetExerciseMeasurements(exercise: ExerciseClass): List<Measurement> {
        val index = exercises.indexOf(exercise)
        return exercises[index].measurementsList
    }
}