// app/src/main/java/com/example/gymtracker/ExerciseManager.kt
package com.example.gymtracker

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object ExerciseManager {
    private const val PREFS_NAME = "exercise_prefs"
    private const val KEY_EXERCISES = "exercises"
    var exercises by mutableStateOf(listOf<ExerciseClass>())

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val exercisesJson = prefs.getString(KEY_EXERCISES, "[]")
        exercises = LoadExercises()
    }
    fun LoadExercises(): List<ExerciseClass> {
        val exerciseList = listOf(
            ExerciseClass("Chest fly", "Chest"),
            ExerciseClass("Leg curl", "Legs"),
            ExerciseClass("Leg press", "Legs"),
            ExerciseClass("Dumbbell biceps", "Arms"),
            ExerciseClass("Bench press", "Chest"),
            ExerciseClass("Seated barbell press", "Shoulders"),
        )
        return exerciseList
    }
    fun GetExerciseMeasurements(exercise: ExerciseClass): List<MeasurementClass> {
        val index = exercises.indexOf(exercise)
        return exercises[index].measurementsList
    }
}