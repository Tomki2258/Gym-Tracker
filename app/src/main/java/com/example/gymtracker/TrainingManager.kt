// app/src/main/java/com/example/gymtracker/TrainingManager.kt
package com.example.gymtracker

import DayTrainingPlan
import android.content.Context
import androidx.room.Room
import com.example.gymtracker.roomdb.TrainingplanDatabase
import com.example.gymtracker.roomdb.TrainingPlan
import kotlinx.coroutines.runBlocking

object TrainingManager {
    private var db: TrainingplanDatabase? = null

    val daysOfWeek = listOf(
        DayTrainingPlan("Monday"),
        DayTrainingPlan("Tuesday"),
        DayTrainingPlan("Wednesday"),
        DayTrainingPlan("Thursday"),
        DayTrainingPlan("Friday"),
        DayTrainingPlan("Saturday"),
        DayTrainingPlan("Sunday")
    )

    private fun getDatabase(context: Context): TrainingplanDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                context.applicationContext,
                TrainingplanDatabase::class.java,
                "training_plan_database"
            ).fallbackToDestructiveMigration()
                .build()
        }
        return db!!
    }

    fun saveTrainingPlan(context: Context, day: String, exercise: String) {
        val db = getDatabase(context)
        val trainingPlan = TrainingPlan(day = day, exercise = exercise)
        runBlocking {
            db.trainingPlanDao().insert(trainingPlan)
        }
    }

    fun getTrainingPlan(context: Context, day: String): MutableList<TrainingPlan> {
        val db = getDatabase(context)
        return runBlocking {
            db.trainingPlanDao().getTrainingPlanByDay(day)
        }
    }

    fun removeExerciseFromPlan(context: Context, day: String, exercise: String) {
        val db = getDatabase(context)
        val trainingPlans = getTrainingPlan(context, day)
        val updatedPlans = trainingPlans.map { plan ->
            val updatedExercises = plan.exercise.split(",").filter { it != exercise }.joinToString(",")
            plan.copy(exercise = updatedExercises)
        }
        runBlocking {
            updatedPlans.forEach { db.trainingPlanDao().update(it) }
        }
    }
    fun deleteDatabase(context: Context) {
        context.deleteDatabase("training_plan_database")
    }
}