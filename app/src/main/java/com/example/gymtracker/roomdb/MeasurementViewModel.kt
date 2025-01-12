package com.example.gymtracker.roomdb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MeasurementViewModel(
    private val dao: MeasurementDao
) : ViewModel(){
    fun onEvent(event: MeasurementEvent){
        when(event){
            is MeasurementEvent.InsertMeasurement -> {
                viewModelScope.launch {
                    dao.insertMeasurement(event.measurement)
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