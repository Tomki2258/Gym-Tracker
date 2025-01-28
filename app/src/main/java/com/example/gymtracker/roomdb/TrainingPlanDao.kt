// app/src/main/java/com/example/gymtracker/roomdb/TrainingPlanDao.kt
package com.example.gymtracker.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TrainingPlanDao {
    @Insert
    suspend fun insert(trainingPlan: TrainingPlan)

    @Update
    suspend fun update(trainingPlan: TrainingPlan)

    @Query("SELECT * FROM training_plan WHERE day = :day")
    suspend fun getTrainingPlanByDay(day: String): MutableList<TrainingPlan>

    @Query("DELETE FROM training_plan WHERE day = :day AND exercise = :exercise")
    suspend fun deleteTrainingPlanByDay(day: String, exercise: String)
}