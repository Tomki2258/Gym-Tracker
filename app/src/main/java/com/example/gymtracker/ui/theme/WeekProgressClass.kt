package com.example.gymtracker.ui.theme

import java.io.Serializable

class WeekProgressClass: Serializable {
    val weekNumber: Int
    val progress: Int

    constructor(weekNumber: Int, progress: Int) {
        this.weekNumber = weekNumber
        this.progress = progress
    }
}