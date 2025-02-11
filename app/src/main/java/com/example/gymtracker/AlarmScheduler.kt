package com.example.gymtracker

interface AlarmScheduler {
    fun scheduleAlarm(item : AlarmItem)
    fun cancelAlarm(item : AlarmItem)
}