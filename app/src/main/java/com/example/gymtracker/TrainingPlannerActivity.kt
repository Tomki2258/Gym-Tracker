package com.example.gymtracker

import DayTrainingPlan
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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

    val calendar: Calendar = Calendar.getInstance()
    val currentDayIndex = remember {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val adjustedDayIndex = (dayOfWeek + 5) % 7
        mutableStateOf(adjustedDayIndex)
    }

    showAddDialog = remember { mutableStateOf(false) }
    val exercises = remember { mutableStateOf(TrainingManager.daysOfWeek[currentDayIndex.value].exercises.toMutableList()) }

    LaunchedEffect(currentDayIndex.value) {
        exercises.value = TrainingManager.daysOfWeek[currentDayIndex.value].exercises.toMutableList()
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
                ExericeCard(exercise)
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
            LazyColumn(
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
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
fun AddExercisePanel(){
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
fun ExericeCard(exercise: ExerciseClass) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Row(
        )
        {
            Icon(
                painter = painterResource(id = exercise.getPhotoResourceId(LocalContext.current)),
                contentDescription = "Exercise photo",
                modifier = Modifier.padding(8.dp)
            )
            Column {
                Text(text = exercise.name)
                Text(text = exercise.category)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    MainView("Android")
}

fun PrintTrainingsFromDay(day: DayTrainingPlan) {
    for (exercise in day.exercises) {
        Log.d("Exercise", exercise.name)
    }
}