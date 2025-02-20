package com.example.gymtracker.views

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

class AddCustomExerciseView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val exerciseViewModel: AddCustomExerciseViewModel = AddCustomExerciseViewModel(this)
        setContent {
            GymTrackerTheme {
                MainView(
                    exerciseViewModel
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(exerciseViewModel: AddCustomExerciseViewModel) {
    val name by exerciseViewModel.nameState.collectAsState()
    val description by exerciseViewModel.descriptionState.collectAsState()

    var selectedCategory by remember { mutableStateOf(Categories.OTHER) }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { exerciseViewModel.updateName(it) },
            label = { Text("Exercise Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
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
                Categories.values().forEach { category ->
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
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                if(exerciseViewModel.checkForAdd()){
                    Toast.makeText(exerciseViewModel.context,"SIGMA",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(exerciseViewModel.context,"NIE SIGMA",Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Confirm")
        }
    }
}