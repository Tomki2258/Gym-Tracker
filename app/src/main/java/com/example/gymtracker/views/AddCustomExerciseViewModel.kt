package com.example.gymtracker.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.Categories
import com.example.gymtracker.managers.ExerciseManager
import com.example.gymtracker.data.ExericseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// In your ViewModel
class AddCustomExerciseViewModel(contextArg: Context) : ViewModel() {
    val context = contextArg

    private val exerciseName = MutableStateFlow("")
    private val description = MutableStateFlow("")

    val nameState = exerciseName.asStateFlow()
    val descriptionState = description.asStateFlow()

    private val exerciseCattegory = MutableStateFlow(Categories.OTHER)

    var photoUri: MutableStateFlow<Uri> = MutableStateFlow(Uri.EMPTY)
    val photoState = photoUri.asStateFlow()

    fun updateName(name: String) {
        exerciseName.value = name
    }

    fun updateDescription(desc: String) {
        description.value = desc
    }

    fun updateCattegory(cat: Categories) {
        exerciseCattegory.value = cat
    }

    fun updatePhoto(uri: Uri) {
        photoUri.value = uri
    }

    val photoUriState = photoUri.asStateFlow()

    fun checkForAdd(): Boolean {
        val startTime = System.currentTimeMillis()

        if (exerciseName.value == "") return false
        try {
            saveImage()
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
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        Log.d("Gyat","Gyat")
        Log.d("Time elapsed:",duration.toString())
        return true
    }
    private fun saveImage() {
        val fileName = "${exerciseName.value}.JPEG"
        val uri = photoUri.value
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        }
    }
}