// MeasurementDatabase.kt
package com.example.gymtracker.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gymtracker.Measurement

@Database(entities = [Measurement::class], version = 1)
abstract class MeasurementDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDao
}