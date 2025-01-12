package com.example.gymtracker.roomdb

import com.example.gymtracker.Measurement

sealed interface MeasurementEvent {
    data class InsertMeasurement(val measurement: Measurement) : MeasurementEvent
}