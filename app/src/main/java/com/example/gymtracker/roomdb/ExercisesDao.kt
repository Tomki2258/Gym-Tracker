package com.example.gymtracker.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.gymtracker.ExerciseClass
import com.example.gymtracker.ExericseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExercisesDao {
    @Insert
    suspend fun insertExercise(exericseEntity: ExericseEntity)

    @Delete
    suspend fun deleteExercise(exericseEntity: ExericseEntity)

    @Query("SELECT * FROM exercises")
    fun getAllExercises(exericseEntity: ExericseEntity) :Flow<List<ExericseEntity>>
}