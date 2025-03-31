package com.example.gymtracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.gymtracker.services.NotifycationsService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "notification")
        val notifycationsService = NotifycationsService(context)
        notifycationsService.showNotification()
    }
}