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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.gymtracker.ui.theme.GymTrackerTheme

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

@Composable
fun MainView(name: String, modifier: Modifier = Modifier) {
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val currentDay = remember { mutableStateOf(daysOfWeek[0]) }
    val showAddDialog = remember { mutableStateOf(false) }
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
                    var index = daysOfWeek.indexOf(currentDay.value)
                    index -= 1
                    if (index < 0) {
                        index = daysOfWeek.size - 1
                    }
                    currentDay.value = daysOfWeek[index]
                    Log.d("MainView", "currentDay: ${currentDay.value}")
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
                    var index = daysOfWeek.indexOf(currentDay.value)
                    index += 1
                    if (index >= daysOfWeek.size) {
                        index = 0
                    }
                    currentDay.value = daysOfWeek[index]
                }
            ) {
                Text(text = "->")
            }
        }

        Text(
            text = currentDay.value,
            modifier = Modifier.padding(8.dp)
        )

        LazyColumn {
            items(daysOfWeek) { day ->
                DayCard()
            }
        }
        if(showAddDialog.value){
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
                onClick = { /*TODO*/ }
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
fun AddExericeToDay(onDismissRequest: () -> Unit){
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = "This is a minimal dialog",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
            )
        }
    }
}