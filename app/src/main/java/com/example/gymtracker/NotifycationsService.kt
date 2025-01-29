package com.example.gymtracker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class NotifycationsService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification() {
        //You can also create INTENT to call it on notification click
        val notifycation = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Gym Tracker")
            .setContentText("Don't forget your supplements !")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notifycation)
    }
    companion object{
        const val CHANNEL_ID = "notifyGymTracker"
    }
}