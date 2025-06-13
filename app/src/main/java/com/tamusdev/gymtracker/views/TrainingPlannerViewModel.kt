package com.tamusdev.gymtracker.views

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.tamusdev.gymtracker.data.Categories
import com.tamusdev.gymtracker.data.ExerciseClass
import com.tamusdev.gymtracker.managers.ExerciseManager
import com.tamusdev.gymtracker.managers.TrainingManager

class TrainingPlannerViewModel(context: Context): ViewModel() {
    internal val context: Context = context
    var showAddDialog = mutableStateOf(false)
    var showRemoveDialog = mutableStateOf(false)
    var currentDayIndex = mutableStateOf(0)
    var showWarmUpDialog = mutableStateOf(false)
    var exercisesView = mutableStateOf(mutableListOf<ExerciseClass>())
    var selectedToRemove = mutableStateOf(ExerciseClass("", Categories.CHEST, exericseEntity = null))

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
    fun getStringDay(dayIndex: Int): String {
        return when (dayIndex) {
            0 -> "Monday"
            1 -> "Tuesday"
            2 -> "Wednesday"
            3 -> "Thursday"
            4 -> "Friday"
            5 -> "Saturday"
            6 -> "Sunday"
            else -> "Monday"
        }
    }

    fun IncreaseDayIndex() {
        currentDayIndex.value++
        if (currentDayIndex.value >= TrainingManager.daysOfWeek.size) {
            currentDayIndex.value = 0
        }
    }

    fun DecreateDayIndex() {
        currentDayIndex.value--
        if (currentDayIndex.value < 0) {
            currentDayIndex.value = TrainingManager.daysOfWeek.size - 1
        }
    }
}