package com.example.gymtracker.factories

import android.content.Context
import android.util.Log
import com.example.gymtracker.data.Categories
import com.example.gymtracker.data.ExerciseClass
import com.example.gymtracker.managers.ExerciseManager
import com.example.gymtracker.managers.ExerciseManager.getDatabase
import com.example.gymtracker.roomdb.MeasurementDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ExercisesFactory(private val context: Context) {
    init {
        CoroutineScope(Dispatchers.IO).launch {
            val customExercises = loadCustomExercises()
            val defaultExercises = LoadExercises()
            ExerciseManager.exercises = customExercises + defaultExercises

            val db = MeasurementDatabase.getInstance(context)
            loadMeasurementsFromDatabase(db)

            ExerciseManager.categories = LoadCategories(ExerciseManager.exercises)
        }
    }

    private fun loadMeasurementsFromDatabase(db: MeasurementDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            val measurements = db.measurementDao().getAllMeasurements().first()
            ExerciseManager.exercises.forEach { exercise ->
                exercise.measurementsList = measurements.filter { measurement ->
                    measurement.exerciseName == exercise.name
                }.toMutableList()
            }
        }
    }

    //hard coded exercises
    fun LoadExercises(): List<ExerciseClass> {
        return listOf(
            ExerciseClass("Chest fly", Categories.CHEST, exericseEntity = null),
            ExerciseClass("Bench press", Categories.CHEST, exericseEntity = null),
            ExerciseClass("Dumbbell Chest Press", Categories.CHEST, exericseEntity = null),
            ExerciseClass("Pec Deck", Categories.CHEST, exericseEntity = null),
            ExerciseClass("Reverse Machine Fly", Categories.SHOULDERS, exericseEntity = null),
            ExerciseClass("Shoulder Press", Categories.SHOULDERS, exericseEntity = null),
            ExerciseClass("Barbell Curl", Categories.BICEPS, exericseEntity = null),
            ExerciseClass("Tricep Pushdown", Categories.TRICEPS, exericseEntity = null),
            ExerciseClass("Cable Grip", Categories.BACK, exericseEntity = null),
            ExerciseClass("Lat Pulldown", Categories.BACK, exericseEntity = null),
            ExerciseClass("Machine Crunch", Categories.ABS, exericseEntity = null),
            ExerciseClass("Leg Press", Categories.LEGS, exericseEntity = null),
            ExerciseClass("Leg Curl", Categories.LEGS, exericseEntity = null)
        )
    }

    suspend fun loadCustomExercises(): List<ExerciseClass> {
        val loadedExercises = mutableListOf<ExerciseClass>()
        val customExercises = getDatabase(context).exerciseDao().getAllExercises().first()
        customExercises.forEach { ex ->
            loadedExercises.add(
                ExerciseClass(
                    ex.name,
                    ex.category,
                    ex.description,
                    isCustom = true,
                    exericseEntity = ex
                )
            )
            Log.d(ex.name, ex.description)
        }
        return loadedExercises
    }

    fun LoadCategories(exercises: List<ExerciseClass>): List<String> {
        val categories = mutableListOf<String>()
        categories.add("All")
        for (exercise in exercises) {
            if (!categories.contains(exercise.category.toString())) {
                categories.add(exercise.category.toString())
            }
        }
        return categories
    }
}