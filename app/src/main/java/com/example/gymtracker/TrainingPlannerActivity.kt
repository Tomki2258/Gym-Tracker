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
var currentDay : MutableState<DayTrainingPlan> = mutableStateOf(DayTrainingPlan("Monday"))
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
    currentDay = remember { mutableStateOf(daysOfWeek[0]) }
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
                    val index = daysOfWeek.indexOf(currentDay.value)
                    if (index == 0) {
                        currentDay.value = daysOfWeek[daysOfWeek.size - 1]
                    } else {
                        currentDay.value = daysOfWeek[index - 1]
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
                    val indexedValue = daysOfWeek.indexOf(currentDay.value)
                    if (indexedValue == daysOfWeek.size - 1) {
                        currentDay.value = daysOfWeek[0]
                    } else {
                        currentDay.value = daysOfWeek[indexedValue + 1]
                    }

                }
            ) {
                Text(text = "->")
            }
        }

        Text(
            text = currentDay.value.day,
            modifier = Modifier.padding(8.dp)
        )

        DayCard()

        if (showAddDialog.value) {
            AddExericeToDay(onDismissRequest = { showAddDialog.value = false })
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