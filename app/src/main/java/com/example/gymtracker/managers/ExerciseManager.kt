// ExerciseManager.kt
package com.example.gymtracker.managers

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.room.Room
import com.example.gymtracker.data.Categories
import com.example.gymtracker.data.ExerciseClass
import com.example.gymtracker.data.ExericseEntity
import com.example.gymtracker.data.Measurement
import com.example.gymtracker.factories.ExercisesFactory
import com.example.gymtracker.managers.ExerciseManager.exerciseFactory
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
    var categories by mutableStateOf(listOf<String>())
    private lateinit var exerciseDatabase: ExercisesDatabase;
    private lateinit var exerciseFactory: ExercisesFactory;
    fun initialize(context: Context) {
        exerciseDatabase = getDatabase(context)
        exerciseFactory = ExercisesFactory(context);
    }

    fun getDatabase(context: Context): ExercisesDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ExercisesDatabase::class.java,
            "exercises_database"
        ).fallbackToDestructiveMigration()
            .build()
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
            exerciseDatabase.exerciseDao().deleteExercise(exericseEntity)

            val value = exercises.indexOf(exercises.find { it.name.equals(exericseEntity.name) })
            if (value != -1) {
                exercises = exercises.toMutableList().apply {
                    removeAt(value)
                }
            }
        }
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