package com.example.gymtracker.roomdb

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface ExercisesDao {
    @Insert
    suspend fun insertExercise()
}