// app/src/main/java/com/example/gymtracker/TrainingPlannerActivity.kt
package com.example.gymtracker

import DayTrainingPlan
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape // Add this import
import java.util.Calendar

class TrainingPlannerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MainView(
                    name = "Android",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

var showAddDialog = mutableStateOf(false)

@Composable
fun MainView(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val calendar: Calendar = Calendar.getInstance()
    val currentDayIndex = remember {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val adjustedDayIndex = (dayOfWeek + 5) % 7
        mutableStateOf(adjustedDayIndex)
    }

    showAddDialog = remember { mutableStateOf(false) }
    val exercises = remember { mutableStateOf(mutableListOf<ExerciseClass>()) }

    LaunchedEffect(currentDayIndex.value) {
        val trainingPlans = TrainingManager.getTrainingPlan(context, TrainingManager.daysOfWeek[currentDayIndex.value].day)
        exercises.value = trainingPlans.flatMap { it.exercises.split(",") }.mapNotNull { exerciseName ->
            ExerciseManager.exercises.find { it.name == exerciseName }
        }.toMutableList()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    currentDayIndex.value--
                    if (currentDayIndex.value < 0) {
                        currentDayIndex.value = TrainingManager.daysOfWeek.size - 1
                    }
                }
            ) {
                Text(text = "<-")
            }

            Text(
                text = "Training Planner",
                modifier = Modifier.padding(8.dp)
            )

            Button(
                onClick = {
                    currentDayIndex.value++
                    if (currentDayIndex.value >= TrainingManager.daysOfWeek.size) {
                        currentDayIndex.value = 0
                    }
                }
            ) {
                Text(text = "->")
            }
        }

        Text(
            text = TrainingManager.daysOfWeek[currentDayIndex.value].day,
            modifier = Modifier.padding(8.dp)
        )

        LazyColumn {
            items(exercises.value) { exercise ->
                ExericeCard(exercise, currentDayIndex.value, exercises)
            }
        }
        AddExercisePanel()
        if (showAddDialog.value) {
            AddExericeToDay(
                onDismissRequest = { showAddDialog.value = false },
                onAddExercise = { exercise ->
                    val currentDay = TrainingManager.daysOfWeek[currentDayIndex.value]
                    if (!currentDay.exercises.contains(exercise)) {
                        currentDay.exercises.add(exercise)
                        exercises.value = currentDay.exercises.toMutableList()
                        TrainingManager.saveTrainingPlan(context, currentDay.day, currentDay.exercises.joinToString(",") { it.name })
                    }
                }
            )
        }
    }
}

@Composable
fun AddExericeToDay(onDismissRequest: () -> Unit, onAddExercise: (ExerciseClass) -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            LazyColumn {
                items(ExerciseManager.exercises) { exercise ->
                    Row {
                        Text(text = exercise.name)
                        IconButton(
                            onClick = {
                                onAddExercise(exercise)
                                onDismissRequest()
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.add),
                                contentDescription = "Add exercise"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddExercisePanel() {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        IconButton(
            onClick = { showAddDialog.value = true }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add exercise"
            )
        }
    }
}

@Composable
fun ExericeCard(exercise: ExerciseClass, dayIndex: Int, exercises: MutableState<MutableList<ExerciseClass>>) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Row {
            Icon(
                painter = painterResource(id = exercise.getPhotoResourceId(context)),
                contentDescription = "Exercise photo",
                modifier = Modifier.padding(8.dp)
            )
            Column {
                Text(text = exercise.name)
                Text(text = exercise.categoryString)
                IconButton(
                    onClick = {
                        val currentDay = TrainingManager.daysOfWeek[dayIndex]
                        currentDay.exercises.remove(exercise)
                        exercises.value = currentDay.exercises.toMutableList()
                        TrainingManager.removeExerciseFromPlan(context, currentDay.day, exercise.name)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.none),
                        contentDescription = "Remove exercise"
                    )
                }
            }
        }
    }
}

@Composable
fun MainViewPreview() {
    MainView("Android")
}

fun PrintTrainingsFromDay(day: DayTrainingPlan) {
    for (exercise in day.exercises) {
        Log.d("Exercise", exercise.name)
    }
}