package com.example.gymtracker.views

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.gymtracker.data.ExerciseClass
import com.example.gymtracker.managers.ExerciseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel : ViewModel() {
    lateinit var context: Context

    private val searchName = MutableStateFlow("")
    val searchNameState = searchName.asStateFlow()
    val searchEnabled = mutableStateOf(false)
    val categories = LoadCategories(ExerciseManager.exercises)
    private val currentCategory = MutableStateFlow(categories.first())
    val currentCategoryState = currentCategory.asStateFlow()

    @Composable
    fun RequestNotificationPermission() {
        var hasNotificationPermission by remember {
            mutableStateOf(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }
            )
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                hasNotificationPermission = isGranted
            }
        )

        LaunchedEffect(Unit) {
            if (!hasNotificationPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    fun updateSearch(search:String){
        searchName.value = search
        //Log.d("Search value ",searchName.value.toString())
    }
    fun LoadCategories(exercises: List<ExerciseClass>): List<String> {
        val categories = mutableListOf<String>()
        categories.add("All")
        for (exercise in exercises) {
            if (!categories.contains(exercise.category.toString())) {
                categories.add(exercise.category.toString())
            }
        }

        return categories
    }
    fun getCat(): List<String>{
        return categories
    }
}