// ExerciseView.kt
package com.example.gymtracker

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.gymtracker.roomdb.MeasurementDatabase
import com.example.gymtracker.roomdb.MeasurementEvent
import com.example.gymtracker.roomdb.MeasurementViewModel
import kotlinx.coroutines.launch
import java.io.BufferedReader

var exercise = ExerciseClass("Default Name", Categories.CALVES, "no_desc.txt")

class ExerciseView : ComponentActivity() {
    private lateinit var measurementViewModel: MeasurementViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            MeasurementDatabase::class.java,
            "measurement_database"
        ).build()
        val dao = db.measurementDao()

        measurementViewModel = ViewModelProvider(
            this,
            MeasurementViewModel.Factory(dao)
        ).get(MeasurementViewModel::class.java)
        setContent {
            val index = intent.getIntExtra("EXERCISE_INDEX", 0)
            val exerciseClass = ExerciseManager.exercises[index]
            exercise = exerciseClass
            ExerciseIntent(
                exerciseClass = exerciseClass,
                exerciseView = this
            )
        }
    }

    fun addMeasurementDatabase(reps: Int, weight: Float, exerciseName: String, measurementsList: MutableState<MutableList<Measurement>>) {
        val measurement = Measurement(
            reps = reps,
            weight = weight,
            exerciseName = exerciseName
        )
        if (reps == 0 || weight.toDouble() == 0.0) {
            //ToastManager(context, "Reps or weight cannot be 0")
            return
        }
        exercise.measurementsList.add(measurement)
        exercise.SetBestMeasurement()
        val event = MeasurementEvent.InsertMeasurement(measurement)
        measurementViewModel.onEvent(event)
        measurementsList.value = exercise.measurementsList.toMutableList()
    }

    fun deleteMeasurementDatabase(measurement: Measurement, measurementsList: MutableState<MutableList<Measurement>>) {
        val event = MeasurementEvent.DeleteMeasurement(measurement)
        measurementViewModel.onEvent(event)
        exercise.measurementsList.remove(measurement)
        measurementsList.value = exercise.measurementsList.toMutableList()
    }
}

@Composable
fun ExerciseIntent(
    exerciseClass: ExerciseClass = ExerciseClass("Default Name", Categories.CALVES, "no_desc.txt"),
    exerciseView: ExerciseView
) {
    val showDialog = remember { mutableStateOf(false) }
    val showDescDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val clickedMeasurement = remember { mutableStateOf(Measurement(0, 0, 0.0f, "")) }
    val measurementsList = remember { mutableStateOf(exercise.measurementsList.toMutableList()) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 12.dp, 0.dp, 0.dp)
    ) {
        Column {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = exerciseClass.getPhotoResourceId(LocalContext.current)),
                        contentDescription = "Exercise Image"
                    )
                    Text(
                        text = "${exerciseClass.name} - ${exerciseClass.category}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
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
                    LazyColumn(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        items(measurementsList.value) { measurement ->
                            Row(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        showDeleteDialog.value = true
                                        clickedMeasurement.value = measurement
                                    }
                                )
                            ) {
                                Text(text = "Reps: ${measurement.reps}")
                                Text(text = "Weight: ${measurement.weight}")
                            }
                        }
                    }
                }
            }
            Text(text = "Progress")
            Card {
                //PrintProgress()
            }
        }
        FloatingActionButton(
            onClick = { showDescDialog.value = true },
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
        AddMeasurementDialog(
            onDismissRequest = { showDialog.value = false },
            measurementsList,
            exerciseView
        )
    }
    if (showDescDialog.value) {
        ShowExerciseInfo(onDismissRequest = { showDescDialog.value = false })
    }
    if (showDeleteDialog.value) {
        ShowDeleteMeasurement(
            clickedMeasurement.value,
            onDismissRequest = { showDeleteDialog.value = false },
            exerciseView,
            measurementsList
        )
    }
}


fun AddMesurment(
    context: Context,
    reps: MutableIntState,
    weigh: MutableDoubleState,
    measurementsList: MutableState<MutableList<Measurement>>
) {
    if (reps.value == 0 || weigh.value == 0.0) {
        ToastManager(context, "Reps or weight cannot be 0")
        return
    }

    val newMeasurement = Measurement(
        0,
        reps.value,
        weigh.value.toFloat(),
        exercise.name
    )
    exercise.measurementsList.add(newMeasurement)
    measurementsList.value = exercise.measurementsList.toMutableList()
    exercise.SetBestMeasurement()
    ToastManager(context, "Measurement added")
}

@Composable
fun AddMeasurementDialog(
    onDismissRequest: () -> Unit,
    measurementsList: MutableState<MutableList<Measurement>>,
    exerciseView: ExerciseView
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
                        exerciseView.addMeasurementDatabase(
                            reps.value,
                            weight.doubleValue.toFloat(),
                            exercise.name,
                            measurementsList
                        )
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
fun ShowExerciseInfo(onDismissRequest: () -> Unit) {
    val exerciseDesc = LocalContext.current.assets.open(exercise.descFilePath).bufferedReader()
        .use(BufferedReader::readText)
    Log.d("Exercise Desc", exercise.descFilePath)
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.size(300.dp)
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = exerciseDesc
            )
        }
    }
}
@Composable
fun ShowDeleteMeasurement(
    measurement: Measurement,
    onDismissRequest: () -> Unit,
    exerciseView: ExerciseView,
    measurementsList: MutableState<MutableList<Measurement>>
) {
    val scope = rememberCoroutineScope()
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.size(300.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Are you sure you want to delete this measurement?"
                )
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Reps: ${measurement.reps} x Weight: ${measurement.weight}"
                )
                Button(
                    onClick = {
                        scope.launch {
                            exerciseView.deleteMeasurementDatabase(measurement, measurementsList)
                        }
                        onDismissRequest()
                    }
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }
}