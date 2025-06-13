package com.tamusdev.gymtracker

interface AlarmScheduler {
    fun scheduleAlarm(item : AlarmItem)
    fun cancelAlarm(item : AlarmItem)
}