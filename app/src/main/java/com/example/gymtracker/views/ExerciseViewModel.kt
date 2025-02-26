package com.example.gymtracker.views

import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseViewModel:ViewModel() {
    fun formatStringDate(date: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(date))
    }
}