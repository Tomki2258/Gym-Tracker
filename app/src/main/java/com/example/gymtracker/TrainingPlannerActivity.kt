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
var currentDayIndex = 0;

@Composable
fun MainView(name: String, modifier: Modifier = Modifier) {
    val daysOfWeek =
        listOf(
            DayTrainingPlan("Monday"),
            DayTrainingPlan("Tuesday"),
            DayTrainingPlan("Wednesday"),
            DayTrainingPlan("Thursday"),
            DayTrainingPlan("Friday"),
            DayTrainingPlan("Saturday"),
            DayTrainingPlan("Sunday")
        )
    val currentDay = remember { mutableStateOf(daysOfWeek[currentDayIndex]) }
    daysOfWeek[0].exercises.add(ExerciseManager.exercises[0])
    showAddDialog = remember { mutableStateOf(false) }
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
                    currentDayIndex--
                    if (currentDayIndex < 0) {
                        currentDayIndex = daysOfWeek.size - 1
                    }
                    currentDay.value = daysOfWeek[currentDayIndex]
                    PrintTrainingsFromDay(currentDay.value)
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
                    currentDayIndex++
                    if (currentDayIndex > daysOfWeek.size - 1) {
                        currentDayIndex = 0
                    }
                    currentDay.value = daysOfWeek[currentDayIndex]
                    PrintTrainingsFromDay(currentDay.value)
                }
            ) {
                Text(text = "->")
            }
        }

        Text(
            text = currentDay.value.day,
            modifier = Modifier.padding(8.dp)
        )

        //DayCard()
        LazyColumn {
            items(daysOfWeek[currentDayIndex].exercises) { exercise ->
                ExericeCard(exercise)
            }
        }
        AddExercisePanel()
        if (showAddDialog.value) {
            AddExericeToDay(onDismissRequest = { showAddDialog.value = false })
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

@Composable
fun DayCard() {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column {
            Text(text = "Exercise")
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
}

@Composable
fun AddExericeToDay(onDismissRequest: () -> Unit) {
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
                                Log.d(
                                    "AddExericeToDay",
                                    "Add ${exercise.name} to ${showAddDialog.value}"
                                )

                                showAddDialog.value = false
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
fun PrintTrainingsFromDay(day: DayTrainingPlan) {
    for (exercise in day.exercises) {
        Log.d("Exercise", exercise.name)
    }
}