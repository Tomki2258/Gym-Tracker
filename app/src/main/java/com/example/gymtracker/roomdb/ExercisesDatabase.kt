package com.example.gymtracker.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gymtracker.ExerciseClass
import com.example.gymtracker.ExericseEntity

@Database(entities = [ExericseEntity::class], version = 1)
abstract class ExercisesDatabase: RoomDatabase(){
    abstract fun exerciseDao(): ExercisesDao
}