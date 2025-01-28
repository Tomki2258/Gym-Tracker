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
        //val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        //val exercisesJson = prefs.getString(KEY_EXERCISES, "[]")
        exercises = LoadExercises()
        // Load measurements from the database
        val db = MeasurementDatabase.getInstance(context)
        loadMeasurementsFromDatabase(db)
        Thread{
            LoadDescriptions()
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
            ExerciseClass("Bench press", Categories.CHEST),
            ExerciseClass("Dumbbell Chest Press", Categories.CHEST),
            ExerciseClass("Pec Deck", Categories.CHEST),
            ExerciseClass("Reverse Machine Fly", Categories.SHOULDERS),
            ExerciseClass("Shoulder Press", Categories.SHOULDERS),
            ExerciseClass("Barbell Curl", Categories.BICEPS),
            ExerciseClass("Tricep Pushdown", Categories.TRICEPS),
            ExerciseClass("Cable Grip", Categories.BACK),
            ExerciseClass("Lat Pulldown", Categories.BACK),
            ExerciseClass("Machine Crunch", Categories.ABS),
            ExerciseClass("Leg Raise" , Categories.ABS),
        )
        return exerciseList
    }

    fun GetExerciseMeasurements(exercise: ExerciseClass): List<Measurement> {
        val index = exercises.indexOf(exercise)
        return exercises[index].measurementsList
    }

    fun getExerciseByName(exercise: String): ExerciseClass {
        exercises.forEach { exerciseIt ->
            if (exerciseIt.name == exercise) {
                return exerciseIt
            }
        }
        return ExerciseClass("Not found", Categories.CHEST)
    }
}