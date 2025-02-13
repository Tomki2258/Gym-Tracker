package com.example.gymtracker

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object ApiManager {
    const val BASE_URL = "http://192.168.1.75:8080/";
    init {
//        Thread {
//            val url = "chestfly"
//            val exercise = getExercise(url)
//            Log.d("ApiManager", exercise)
//        }.start()
    }
    fun getExercise(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(BASE_URL+url)
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("error: ${response.code()}")
                }
                response.body()?.string().orEmpty()
            }
        } catch (exc: Exception) {
            "Exception: ${exc.message}"
        }
    }

    suspend fun getWarpUp(url: String): String {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val buildURL = BASE_URL +"warmup/"+ url
            val request = Request.Builder()
                .url(buildURL)
                .build()
            Log.d("ApiManager", "Request URL: ${buildURL}")
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Error: ${response.code()}")
                    }
                    val responseBody = response.body()?.string().orEmpty()
                    Log.d("ApiManager", "Response Body: $responseBody")
                    responseBody
                }
            } catch (exc: Exception) {
                Log.e("ApiManager", "Exception: ${exc.message}", exc)
                "Exception: ${exc.message}"
            }
        }
    }
}