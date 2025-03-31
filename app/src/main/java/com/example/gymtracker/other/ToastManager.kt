package com.example.gymtracker.other

import android.content.Context
import android.widget.Toast

class ToastManager(private val context: Context,message :String) {
    init {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}