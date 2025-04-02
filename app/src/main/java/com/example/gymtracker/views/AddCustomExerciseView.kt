package com.example.gymtracker.views

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.Categories
import com.example.gymtracker.views.ui.theme.GymTrackerTheme
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberImagePainter
import com.example.gymtracker.R

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
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                exerciseViewModel.updatePhoto(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (Uri.EMPTY.equals(photoUri)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(100.dp)
                    .padding(bottom = 16.dp)
                    .clickable(){

                    }
                    .clickable() {
                        pickMedia.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier.size(15.dp),
                            painter = painterResource(R.drawable.camera),
                            contentDescription = "Camera"
                        )
                        Text(text = "Click to add photo")
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier
                    //.fillMaxWidth(0.75f)
                    .padding(bottom = 16.dp)
                    .size(200.dp)
                    .clip(RoundedCornerShape(percent = 10))
                    .clickable {
                        pickMedia.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
            {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberImagePainter(photoUri),
                    contentDescription = "Exercise Image",
                    contentScale = ContentScale.Crop
                )
            }
        }
//        Button(
//            onClick = {
//                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//            }
//        ) {
//            Text("Add Photo")
//        }
        TextField(
            value = name,
            onValueChange = { exerciseViewModel.updateName(it) },
            label = { Text("Exercise Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)

        )
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
                    Toast.makeText(exerciseViewModel.context, "Exercise added", Toast.LENGTH_SHORT).show()
                    activity.finish()
                } else {
                    Toast.makeText(exerciseViewModel.context, "ERROR", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        ) {
            Text("Add exercise")
        }
    }
}