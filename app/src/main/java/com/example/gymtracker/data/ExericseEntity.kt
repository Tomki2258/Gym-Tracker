package com.example.gymtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExericseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: Categories,
    val description: String,
){
    fun check(): Boolean {
        if(name.isEmpty() || name.equals("") || category == null){
            return false;
        }
        return true;
    }
}
