package com.tamusdev.gymtracker.roomdb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MeasurementViewModel(
    val dao: MeasurementDao
) : ViewModel(){
    fun onEvent(event: MeasurementEvent){
        when(event){
            is MeasurementEvent.InsertMeasurement -> {
                viewModelScope.launch {
                    dao.insertMeasurement(event.measurement)
                }
            }

            is MeasurementEvent.DeleteMeasurement -> {
                viewModelScope.launch {
                    dao.deleteMeasurement(event.measurement)
                }
            }
        }
    }
    fun clearAllTables() {
        viewModelScope.launch {
            dao.clearAllTables()
        }
    }
    fun getMeasurementsByExercise(exerciseName: String) {
        viewModelScope.launch {
            dao.getMeasurementsByExercise(exerciseName).collect { measurements ->
                measurements.forEach {
                    println("Measurement: ${it.reps} reps, ${it.weight} kg")
                }
            }
        }
    }
    class Factory(private val dao: MeasurementDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MeasurementViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MeasurementViewModel(dao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}