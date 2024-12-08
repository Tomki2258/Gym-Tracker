package com.example.gymtracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.ui.theme.GymTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize().
                padding(4.dp,16.dp,4.dp,0.dp)) { innerPadding ->
                    MainView(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
fun LoadExercises() : List<ExerciseClass> {
    val exerciseList = listOf(
        ExerciseClass("Shoulder press","Shoulders"),
        ExerciseClass("Squats","Legs"),
        ExerciseClass("Bench press","Chest")
    )
    return exerciseList
}
@Composable
fun MainView(modifier: Modifier = Modifier) {
    val exerciseList = LoadExercises()
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {}) {
                Text(text = "Account")
            }
            Button(onClick = {}) {
                Text(text = "Settings")
            }
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            ExerciseList(exerciseList)
        }
    }
}

@Composable
fun ExerciseCard(exercise: ExerciseClass) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)
            .fillMaxWidth()) {
            Text(text = exercise.name)
            Text(text = "Category: ${exercise.category}")
            Button(
                onClick = {
                    LaunchExerciseIntent(exercise)
                }
            ) {
                Text(text = "Choose")
            }
        }
    }
}

@Composable
fun ExerciseList(exercises: List<ExerciseClass>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(exercises) { exercise ->
            ExerciseCard(exercise = exercise)
        }
    }
}

fun LaunchExerciseIntent(exercise: ExerciseClass){
    Log.d(exercise.category,exercise.name)
}