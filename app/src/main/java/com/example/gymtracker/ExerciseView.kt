// ExerciseView.kt
package com.example.gymtracker

import android.os.Bundle
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gymtracker.ui.theme.GymTrackerTheme
import kotlinx.coroutines.launch

var exercise = ExerciseClass("Default Name", "Default")

class ExerciseView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //GymTrackerTheme {
            //exercise = intent.getSerializableExtra("EXERCISE") as ExerciseClass
            val index = intent.getIntExtra("EXERCISE_INDEX", 0)
            ExerciseIntent(modifier = Modifier.fillMaxSize(), exerciseClass = ExerciseManager.exercises[index])
            //exercise.printList()
            //}
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ExerciseIntent(
    modifier: Modifier = Modifier,
    exerciseClass: ExerciseClass = ExerciseClass("Default Name", "Default")
) {
    val showDialog = remember { mutableStateOf(false) }
    val measurementsList = remember { mutableStateOf(exercise.measurementsList.toMutableList()) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Column {
                    Image(
                        painter = painterResource(id = exerciseClass.getPhotoResourceId(LocalContext.current)),
                        contentDescription = "Exercise Image"
                    )
                    Text(text = exerciseClass.name)
                    Text(text = exerciseClass.category)
                }
            }
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                if (measurementsList.value.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No measurements yet")
                    }
                } else {
                    LazyColumn (
                        modifier = Modifier.padding(8.dp)
                    ){
                        items(measurementsList.value) { measurement ->
                            Row {
                                Text(text = "Reps: ${measurement.reps}")
                                Text(text = "Weight: ${measurement.weight}")
                            }
                        }
                    }
                }
            }
            Text(text = "Progress")
            Card(){
                PrintProgress()
            }
        }
        FloatingActionButton(
            onClick = { showDialog.value = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .requiredSize(56.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.information),
                contentDescription = "Exercise Info",
                modifier = Modifier.fillMaxSize(0.6f)
            )
        }
        FloatingActionButton(
            onClick = { showDialog.value = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .requiredSize(56.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add measurement",
                modifier = Modifier.fillMaxSize(0.6f)
            )
        }
    }
    if (showDialog.value) {
        AddMeasurementDialog(onDismissRequest = { showDialog.value = false }, measurementsList)
    }
}

fun AddMesurment(
    context: Context,
    reps: MutableIntState,
    weigh: MutableDoubleState,
    measurementsList: MutableState<MutableList<MeasurementClass>>
) {
    if (reps.value == 0 || weigh.value == 0.0) {
        ToastManager(context, "Reps or weight cannot be 0")
        return
    }

    val newMeasurement = MeasurementClass(reps.value, weigh.value, exercise.category)
    exercise.measurementsList.add(newMeasurement)
    measurementsList.value = exercise.measurementsList.toMutableList()
    exercise.SetBestMeasurement()
    ToastManager(context, "Measurement added")
}

@Composable
fun AddMeasurementDialog(
    onDismissRequest: () -> Unit,
    measurementsList: MutableState<MutableList<MeasurementClass>>
) {
    val reps = remember { mutableIntStateOf(0) }
    val weight = remember { mutableDoubleStateOf(0.0) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        modifier = Modifier.width(75.dp),
                        value = reps.value.toString(),
                        onValueChange = { reps.value = it.toInt() },
                        label = { Text("Reps") }
                    )
                    Text(
                        text = " x ",
                        fontSize = 20.sp
                    )
                    TextField(
                        value = weight.doubleValue.toString(),
                        onValueChange = { weight.doubleValue = it.toDouble() },
                        label = { Text("Weight") }
                    )
                }
                Button(onClick = {
                    scope.launch {
                        AddMesurment(context, reps, weight, measurementsList)
                        onDismissRequest()
                    }
                }) {
                    Text(text = "Add measurement")
                }
            }
        }
    }
}
@Composable
fun WeekChart(){

}
@Composable
fun PrintProgress() {
    val progressWeightMap = mutableMapOf<Int, Double>()

    for (measurement in exercise.measurementsList) {
        val weekOfYear = measurement.getWeekOfYear()
        progressWeightMap[weekOfYear] = progressWeightMap.getOrDefault(weekOfYear, 0.0) + measurement.weight
    }

    val progressWeightList = progressWeightMap.toList()

    for (pair in progressWeightList) {
        //println("Week: ${pair.first}, Total Weight: ${pair.second}")
    }
    //Log.d("Progress", exercise.bestMeasurement?.weight.toString())
}
@Composable
fun ShowExerciseInfo(){

}