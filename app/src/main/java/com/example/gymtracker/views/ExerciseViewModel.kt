package com.example.gymtracker.views

import WeeklyProgress
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.gymtracker.Measurement
import com.example.gymtracker.R
import com.example.gymtracker.ToastManager
import com.example.gymtracker.roomdb.MeasurementEvent
import com.example.gymtracker.roomdb.MeasurementViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseViewModel(
    measurementViewModel: MeasurementViewModel):ViewModel() {
    private val measurementViewModel = measurementViewModel
    //Cards UI sizes
    val weekSize = 1.5f
    val yearSize = 0.5f
    val avgWeightSize = 0.65f
    val weightDiffSize = 1f

    fun formatStringDate(date: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(date))
    }
    fun GetTint(value: Float): Color {
        if (value == 0.0f) return Color.Gray

        if (value > 0) return Color.Green
        return Color.Red
    }

    fun GetRDrawable(value: Float): Int {
        if (value == 0.0f) return R.drawable.none

        if (value > 0) return R.drawable.up
        return R.drawable.down
    }
    
    fun addMeasurementDatabase(
        reps: Int,
        weight: Float,
        exerciseName: String,
        measurementsList: MutableState<MutableList<Measurement>>
    ) {
        val measurement = Measurement(
            reps = reps,
            weight = weight,
            exerciseName = exerciseName
        )
        if (reps == 0 || weight.toDouble() == 0.0) {
            //ToastManager(context, "Reps or weight cannot be 0")
            return
        }
        exercise.measurementsList.add(measurement)
        val event = MeasurementEvent.InsertMeasurement(measurement)
        measurementViewModel.onEvent(event)
        measurementsList.value = exercise.measurementsList.toMutableList()
        exercise.setBestMeasurement()
    }

    fun deleteMeasurementDatabase(
        measurement: Measurement,
        measurementsList: MutableState<MutableList<Measurement>>
    ) {
        val event = MeasurementEvent.DeleteMeasurement(measurement)
        measurementViewModel.onEvent(event)
        exercise.measurementsList.remove(measurement)
        exercise.setBestMeasurement()
        measurementsList.value = exercise.measurementsList.toMutableList()
    }

    fun createWeeklyProgressList(measurements: List<Measurement>): MutableList<WeeklyProgress> {
        val weeklyProgressMap = measurements.groupBy { it.weekOfTheYear }
        val sortedMap = weeklyProgressMap.entries.sortedBy { it.value.first().date }

        val weeklyProgressList = mutableListOf<WeeklyProgress>()

        var lastWeek: WeeklyProgress? = null
        for ((weekNumber, weekMeasurements) in sortedMap) {
            val weeklyProgress = WeeklyProgress(weekMeasurements, weekNumber, lastWeek)
            weeklyProgressList.add(weeklyProgress)
            lastWeek = weeklyProgress
        }
        //weeklyProgressList.sortBy { it.firstDate }
//        for (weeklyProgress in weeklyProgressList) {
//            Log.d("Weekly Progress", "Week ${weeklyProgress.weekNumber} with value ${weeklyProgress.avgWeight}")
//        }
        return weeklyProgressList
    }
    fun roundTheNumber(numInDouble: Float): String {
        return "%.1f".format(numInDouble)
    }
}