package com.example.gymtracker.views

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.gymtracker.Categories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddCustomExerciseViewModel(contextArg: Context):ViewModel() {
    val context = contextArg
    private val exerciseName = MutableStateFlow("")
    private val description = MutableStateFlow("")
    val nameState = exerciseName.asStateFlow()
    val descriptionState = description.asStateFlow()

    private val exerciseCattegory = MutableStateFlow(Categories.OTHER)

    fun updateName(name: String){
        exerciseName.value = name
    }
    fun updateDescription(desc :String){
        description.value = desc
    }
    fun checkForAdd() : Boolean{
        if(exerciseName.value == "") return false

        return true
    }
}