package com.tamusdev.gymtracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.tamusdev.gymtracker.services.NotifycationsService

class NotifycationsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotifycationsService.CHANNEL_ID,
                "Gym Tracker",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Gym Tracker Notification"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}