package com.example.gymtracker.views

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.gymtracker.data.Categories
import com.example.gymtracker.data.ExerciseClass
import com.example.gymtracker.managers.TrainingManager
import com.example.gymtracker.services.TrainingManagerService

class TrainingPlannerViewModel(context: Context): ViewModel() {
    val context: Context = context
    var showAddDialog = mutableStateOf(false)
    var showRemoveDialog = mutableStateOf(false)
    var currentDayIndex = mutableStateOf(0)
    var showWarmUpDialog = mutableStateOf(false)
    var exercisesView = mutableStateOf(mutableListOf<ExerciseClass>())
    var selectedToRemove = mutableStateOf(ExerciseClass("", Categories.CHEST, exericseEntity = null))
    var trainingManagerService = TrainingManagerService(context)

    fun getStringDay(dayIndex: Int): String {
        return when (dayIndex) {
            0 -> "Monday"
            1 -> "Tuesday"
            2 -> "Wednesday"
            3 -> "Thursday"
            4 -> "Friday"
            5 -> "Saturday"
            6 -> "Sunday"
            else -> "UNDEFINED"
        }
    }

    fun increaseDayIndex() {
        currentDayIndex.value++
        if (currentDayIndex.value >= TrainingManager.daysOfWeek.size) {
            currentDayIndex.value = 0
        }
    }

    fun decreaseDayIndex() {
        currentDayIndex.value--
        if (currentDayIndex.value < 0) {
            currentDayIndex.value = TrainingManager.daysOfWeek.size - 1
        }
    }
}