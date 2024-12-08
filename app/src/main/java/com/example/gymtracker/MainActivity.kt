package com.example.gymtracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
fun LoadExercises() : List<ExerciseClass> {
    val exerciseList = listOf(
        ExerciseClass("Shoulder press"),
        ExerciseClass("Squats")
    )
    return exerciseList
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val exerciseList = LoadExercises()
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
    ExerciseList(exerciseList)
}

@Composable
fun ExerciseCard(exercise: ExerciseClass) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "exercise")
            //Text(text = "Category: ${exercise.category}")
        }
    }
}

@Composable
fun ExerciseList(exercises: List<ExerciseClass>) {
    LazyColumn {
        items(exercises) { exercise ->
            ExerciseCard(exercise = exercise)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GymTrackerTheme {
        Greeting("Android")
    }
}