// MeasurementDao.kt
package com.example.gymtracker.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gymtracker.Measurement
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Insert
    suspend fun insertMeasurement(measurement: Measurement)
    @Query("DELETE FROM Measurement")
    suspend fun clearAllTables()
}