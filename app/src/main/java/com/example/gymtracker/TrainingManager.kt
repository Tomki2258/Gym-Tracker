package com.example.gymtracker

import DayTrainingPlan
import android.content.Context

object TrainingManager {
    val daysOfWeek =
        mutableListOf(
            DayTrainingPlan("Monday"),
            DayTrainingPlan("Tuesday"),
            DayTrainingPlan("Wednesday"),
            DayTrainingPlan("Thursday"),
            DayTrainingPlan("Friday"),
            DayTrainingPlan("Saturday"),
            DayTrainingPlan("Sunday")
        )
    fun init(context: Context){
        daysOfWeek[0].exercises.add(ExerciseManager.exercises[1])
    }
}