package com.tamusdev.gymtracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class Notifycations :Application(){
    override fun onCreate(){
        super.onCreate()

        val channel = NotificationChannel(
            "channel1",
            "Channel 1",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notifycationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifycationManager.createNotificationChannel(channel)
    }
}