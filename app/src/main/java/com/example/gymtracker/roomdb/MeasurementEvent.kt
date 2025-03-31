package com.example.gymtracker.roomdb

import com.example.gymtracker.data.Measurement

sealed interface MeasurementEvent {
    data class InsertMeasurement(val measurement: Measurement) : MeasurementEvent
    data class DeleteMeasurement(val measurement: Measurement) : MeasurementEvent
}