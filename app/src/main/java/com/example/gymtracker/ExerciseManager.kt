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
    fun SetCategory(category: Categories) : String{
        return category.toString()
    }
    fun LoadExercises(): List<ExerciseClass> {
        val exerciseList = listOf(
            ExerciseClass("Chest fly", Categories.CHEST, "leg_press.txt"),
            ExerciseClass("Leg curl", Categories.LEGS, "leg_press.txt"),
            ExerciseClass("Leg press", Categories.LEGS,"leg_press.txt"),
            ExerciseClass("Dumbbell biceps", Categories.BICEPS,"bench_press.txt"),
            ExerciseClass("Bench press", Categories.CHEST, "bench_press.txt"),
            ExerciseClass("Seated barbell press", Categories.SHOULDERS, "leg_press.txt"),
            ExerciseClass("Dumbbell press", Categories.CHEST, "leg_press.txt"),
            ExerciseClass("Barbell Curl", Categories.BACK, "leg_press.txt"),
            ExerciseClass("Tricep Pushdown", Categories.TRICEPS, "leg_press.txt"),
            ExerciseClass("Seated Row", Categories.BACK, "leg_press.txt"),
            ExerciseClass("Lat Pulldown", Categories.BACK, "leg_press.txt"),
        )
        return exerciseList
    }

    fun GetExerciseMeasurements(exercise: ExerciseClass): List<Measurement> {
        val index = exercises.indexOf(exercise)
        return exercises[index].measurementsList
    }
}