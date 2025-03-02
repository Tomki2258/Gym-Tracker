package com.example.gymtracker.views

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.Categories
import com.example.gymtracker.ExerciseManager
import com.example.gymtracker.ExericseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import coil.compose.rememberImagePainter

// In your ViewModel
class AddCustomExerciseViewModel(contextArg: Context) : ViewModel() {
    val context = contextArg

    private val exerciseName = MutableStateFlow("")
    private val description = MutableStateFlow("")

    val nameState = exerciseName.asStateFlow()
    val descriptionState = description.asStateFlow()

    private val exerciseCattegory = MutableStateFlow(Categories.OTHER)
    private var photoUri: MutableStateFlow<Uri?> = MutableStateFlow(null)

    fun updateName(name: String) {
        exerciseName.value = name
    }

    fun updateDescription(desc: String) {
        description.value = desc
    }

    fun updateCattegory(cat: Categories) {
        exerciseCattegory.value = cat
    }

    fun updatePhoto(uri: Uri?) {
        photoUri.value = uri
    }

    val photoUriState = photoUri.asStateFlow()

    fun checkForAdd(): Boolean {
        if (exerciseName.value == "") return false
        try {
            viewModelScope.launch {
                ExerciseManager.getDatabase(context).exerciseDao().insertExercise(
                    ExericseEntity(
                        0,
                        exerciseName.value,
                        exerciseCattegory.value,
                        description.value
                    )
                )
                ExerciseManager.initialize(context)
            }
        } catch (ex: Exception) {
            ex.message?.let { Log.e("ADDING EXERCISE FAILED", it) }
            return false
        }
        return true
    }
}