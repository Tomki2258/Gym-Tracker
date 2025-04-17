package com.example.gymtracker.views

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.gymtracker.data.ExerciseClass
import com.example.gymtracker.managers.ApiManager
import com.example.gymtracker.managers.ExerciseManager
import com.example.gymtracker.managers.TrainingManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class TrainingPlannerViewModel(context: Context): ViewModel() {
    private val context: Context = context
    fun loadWarmUp() {
        CoroutineScope(Dispatchers.Main).launch {
            warmUpList.value.clear()
            for (day in TrainingManager.daysOfWeek) {
                val exercises = loadExercisesForDay(day.day)
                val uniqueCategories = exercises.map { it.categoryString }.toSet()
                val categories = uniqueCategories.joinToString("-").lowercase(Locale.getDefault())
                val warmUp = ApiManager.getWarpUp(categories)
                warmUpList.value.add(warmUp)
            }
        }
    }

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