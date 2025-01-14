// MeasurementDao.kt
package com.example.gymtracker.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.gymtracker.Measurement
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Insert
    suspend fun insertMeasurement(measurement: Measurement)

    @Delete
    suspend fun deleteMeasurement(measurement: Measurement)

    @Query("DELETE FROM Measurement")
    suspend fun clearAllTables()

    @Query("SELECT * FROM Measurement")
    fun getAllMeasurements(): Flow<List<Measurement>>

    @Query ("SELECT * FROM Measurement WHERE exerciseName = :exerciseName ORDER BY date DESC")
    fun getMeasurementsByExercise(exerciseName: String): Flow<List<Measurement>>
}