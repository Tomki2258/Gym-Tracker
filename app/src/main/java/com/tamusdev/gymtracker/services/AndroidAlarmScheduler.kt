package com.tamusdev.gymtracker.services

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.tamusdev.gymtracker.AlarmItem
import com.tamusdev.gymtracker.AlarmReceiver
import com.tamusdev.gymtracker.AlarmScheduler
import java.time.ZoneId

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("ShortAlarm")
    override fun scheduleAlarm(item: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("message", item.message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerTime = item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            1000, // AlarmManager.INTERVAL_DAY dla testu można ustawić co 1000 aby mieć co minute
            pendingIntent
        )
    }

    override fun cancelAlarm(item: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, item.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}