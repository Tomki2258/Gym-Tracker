package com.example.gymtracker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class NotifycationsService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification() {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.meds)
            .setContentTitle("Gym Tracker")
            .setContentText("Don't forget your supplements!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }

    companion object {
        const val CHANNEL_ID = "notifyGymTracker"
    }
}