// ExerciseManager.kt
package com.example.gymtracker

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.room.Room
import com.example.gymtracker.roomdb.ExercisesDatabase
import com.example.gymtracker.roomdb.MeasurementDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object ExerciseManager {
    private const val PREFS_NAME = "exercise_prefs"
    private const val KEY_EXERCISES = "exercises"
    var exercises by mutableStateOf(listOf<ExerciseClass>())
    private var exerciseDatabase: ExercisesDatabase? = null

    fun initialize(context: Context) {
        exerciseDatabase = getDatabase(context)
        CoroutineScope(Dispatchers.IO).launch {
            val customExercises = loadCustomExercises()
            val defaultExercises = LoadExercises()
            exercises = customExercises + defaultExercises
            //exercises = defaultExercises

            val db = MeasurementDatabase.getInstance(context)
            loadMeasurementsFromDatabase(db)
            Thread {
                LoadDescriptions()
            }.start()
        }
    }

    fun getDatabase(context: Context): ExercisesDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ExercisesDatabase::class.java,
            "exercises_database"
        ).fallbackToDestructiveMigration()
            .build()
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
    suspend fun loadCustomExercises(): List<ExerciseClass> {
        val loadedExercises = mutableListOf<ExerciseClass>()
        val customExercises = exerciseDatabase?.exerciseDao()?.getAllExercises()?.first()
        customExercises?.forEach { ex ->
            loadedExercises.add(
                ExerciseClass(ex.name, ex.category, ex.description, isCustom = true, exericseEntity = ex)
            )
            Log.d(ex.name, ex.description)
        }
        return loadedExercises
    }
    fun LoadDescriptions() {
        exercises.forEach { exercise ->
            Thread {
                val url = exercise.name.replace(" ", "").lowercase()
                val description = ApiManager.getExercise(url)
                exercise.exerciseDecs = description
            }.start()
        }
    }
    fun DeleteExercise(exericseEntity: ExericseEntity){
        CoroutineScope(Dispatchers.IO).launch {
            exerciseDatabase?.exerciseDao()?.deleteExercise(exericseEntity)

            val value = exercises.indexOf(exercises.find { it.name.equals(exericseEntity.name) })
            if (value != -1) {
                exercises = exercises.toMutableList().apply {
                    removeAt(value)
                }
            }
        }
    }
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
        return ExerciseClass("Not found", Categories.CHEST, exericseEntity = null)
    }
}