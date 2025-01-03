package com.example.gymtracker

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object UserManager {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_USER_NICK = "user_name"
    var userData by mutableStateOf(UserData(userNick = ""))

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userName = prefs.getString(KEY_USER_NICK, context.getString(R.string.user_name)) ?: context.getString(R.string.user_name)
        userData = UserData(userNick = userName)
    }

    fun changeUserNick(context: Context, newNick: String) {
        val cleanedNick = newNick.replace("\\s".toRegex(), "")

        //validate nickname
        if (cleanedNick.isEmpty()) {
            ToastManager(context, "Nickname cannot be empty")
            return
        }

        userData = userData.copy(userNick = cleanedNick)
        saveUserNick(context, cleanedNick)

    }

    private fun saveUserNick(context: Context, newNick: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(KEY_USER_NICK, newNick)
            apply()
        }
        ToastManager(context, "Nickname changed to ${newNick}")

    }
}