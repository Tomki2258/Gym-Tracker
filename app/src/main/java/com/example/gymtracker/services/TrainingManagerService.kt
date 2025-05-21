package com.example.gymtracker.services

import android.content.Context
import com.example.gymtracker.data.ExerciseClass
import com.example.gymtracker.managers.ExerciseManager
import com.example.gymtracker.managers.TrainingManager

class TrainingManagerService(private val context: Context) {
    fun loadExercisesForDay(day: String): MutableList<ExerciseClass> {
        val exercises = mutableListOf<ExerciseClass>()
        val planTrainings = TrainingManager.getTrainingPlan(context, day)
        planTrainings.forEach { training ->
            val exercise = ExerciseManager.exercises.find { it.name == training.exercise }
            if (exercise != null) {
                exercises.add(exercise)
            }
        }
        return exercises
    }
}