package com.example.gymtracker

import com.example.gymtracker.data.Categories
import com.example.gymtracker.data.ExericseEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class AddingExercisesTests {
    @Test
    fun addExercise(){
        val entity = ExericseEntity(0,"exercise", Categories.SHOULDERS,"")

        assert(entity.check())
    }
}