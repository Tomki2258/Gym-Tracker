// app/src/main/java/com/example/gymtracker/TrainingManager.kt
package com.tamusdev.gymtracker.managers

import DayTrainingPlan
import android.content.Context
import android.util.Log
import androidx.room.Room
import com.tamusdev.gymtracker.roomdb.TrainingplanDatabase
import com.tamusdev.gymtracker.data.TrainingPlan
import kotlinx.coroutines.runBlocking

object TrainingManager {
    var db: TrainingplanDatabase? = null

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
        Log.d("TrainingManager", "Removing $exercise from $day")
        trainingPlans.forEach {
            if (it.exercise == exercise) {
                Log.d("TrainingManager", "Deleting $exercise from $day")
                runBlocking {
                    db.trainingPlanDao().deleteTrainingPlanByDay(day, exercise)
                }
            }
        }
    }
    fun deleteDatabase(context: Context) {
        context.deleteDatabase("training_plan_database")
    }
}