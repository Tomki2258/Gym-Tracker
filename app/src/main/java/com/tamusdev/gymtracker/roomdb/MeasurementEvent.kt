package com.tamusdev.gymtracker.roomdb

import com.tamusdev.gymtracker.data.Measurement

sealed interface MeasurementEvent {
    data class InsertMeasurement(val measurement: Measurement) : MeasurementEvent
    data class DeleteMeasurement(val measurement: Measurement) : MeasurementEvent
}