package com.tamusdev.gymtracker.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tamusdev.gymtracker.data.ExericseEntity

@Database(entities = [ExericseEntity::class], version = 1)
abstract class ExercisesDatabase: RoomDatabase(){
    abstract fun exerciseDao(): ExercisesDao
}