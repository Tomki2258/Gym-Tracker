package com.example.gymtracker.views

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.Categories
import com.example.gymtracker.views.ui.theme.GymTrackerTheme
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import coil.compose.rememberImagePainter
import com.example.gymtracker.R
import java.net.URI

class AddCustomExerciseView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val exerciseViewModel: AddCustomExerciseViewModel = AddCustomExerciseViewModel(this)
        setContent {
            GymTrackerTheme {
                MainView(
                    exerciseViewModel,
                    this
                )
            }
        }
    }
}


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(exerciseViewModel: AddCustomExerciseViewModel, activity: ComponentActivity) {
    val name by exerciseViewModel.nameState.collectAsState()
    val description by exerciseViewModel.descriptionState.collectAsState()
    val photoUri by exerciseViewModel.photoUriState.collectAsState()

    var selectedCategory by remember { mutableStateOf(Categories.OTHER) }
    var expanded by remember { mutableStateOf(false) }
    //Log.d("Photo URI",exerciseViewModel.photoUri.value.toString())
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            exerciseViewModel.updatePhoto(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { exerciseViewModel.updateName(it) },
            label = { Text("Exercise Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        if(Uri.EMPTY.equals(photoUri)){
            Card(
                modifier = Modifier.size(200.dp),
                border = BorderStroke(1.dp, Color.White),
            ) {

            }
        }else{
            Image(
                painter = rememberImagePainter(photoUri),
                contentDescription = "Exercise Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .size(200.dp)
            )
        }
        Button(
            onClick = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        ) {
            Text("Add Photo")
        }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            TextField(
                readOnly = true,
                value = selectedCategory.name,
                onValueChange = {},
                label = { Text("Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Categories.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategory = category
                            exerciseViewModel.updateCattegory(category)
                            expanded = false
                        }
                    )
                }
            }
        }
        TextField(
            value = description,
            onValueChange = { exerciseViewModel.updateDescription(it) },
            label = { Text("Exercise Description (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                if (exerciseViewModel.checkForAdd()) {
                    Toast.makeText(exerciseViewModel.context, "SIGMA", Toast.LENGTH_SHORT).show()
                    activity.finish()
                } else {
                    Toast.makeText(exerciseViewModel.context, "NIE SIGMA", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        ) {
            Text("Confirm")
        }
    }
}