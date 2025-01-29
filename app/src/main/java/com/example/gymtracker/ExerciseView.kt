// ExerciseView.kt
package com.example.gymtracker

import WeeklyProgress
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

var exercise = ExerciseClass("Default Name", Categories.CALVES)

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


    fun addMeasurementDatabase(
        reps: Int,
        weight: Float,
        exerciseName: String,
        measurementsList: MutableState<MutableList<Measurement>>
    ) {
        val measurement = Measurement(
            reps = reps,
            weight = weight,
            exerciseName = exerciseName
        )
        if (reps == 0 || weight.toDouble() == 0.0) {
            ToastManager(this, "Reps or weight cannot be 0")
            return
        }
        exercise.measurementsList.add(measurement)
        val event = MeasurementEvent.InsertMeasurement(measurement)
        measurementViewModel.onEvent(event)
        measurementsList.value = exercise.measurementsList.toMutableList()
        exercise.setBestMeasurement()
    }

    fun deleteMeasurementDatabase(
        measurement: Measurement,
        measurementsList: MutableState<MutableList<Measurement>>
    ) {
        val event = MeasurementEvent.DeleteMeasurement(measurement)
        measurementViewModel.onEvent(event)
        exercise.measurementsList.remove(measurement)
        exercise.setBestMeasurement()
        measurementsList.value = exercise.measurementsList.toMutableList()
    }

    fun formatStringDate(date: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(date))
    }

    fun createWeeklyProgressList(measurements: List<Measurement>): MutableList<WeeklyProgress> {
        val weeklyProgressMap = measurements.groupBy { it.weekOfTheYear }
        val sortedMap = weeklyProgressMap.entries.sortedBy { it.value.first().date }

        val weeklyProgressList = mutableListOf<WeeklyProgress>()

        var lastWeek: WeeklyProgress? = null
        for ((weekNumber, weekMeasurements) in sortedMap) {
            val weeklyProgress = WeeklyProgress(weekMeasurements, weekNumber, lastWeek)
            weeklyProgressList.add(weeklyProgress)
            lastWeek = weeklyProgress
        }
        //weeklyProgressList.sortBy { it.firstDate }
//        for (weeklyProgress in weeklyProgressList) {
//            Log.d("Weekly Progress", "Week ${weeklyProgress.weekNumber} with value ${weeklyProgress.avgWeight}")
//        }
        return weeklyProgressList
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ExerciseIntent(
        exerciseClass: ExerciseClass = ExerciseClass(
            "Default Name",
            Categories.CALVES,
            "no_desc.txt"
        ),
        exerciseView: ExerciseView
    ) {
        val showDialog = remember { mutableStateOf(false) }
        val showDescDialog = remember { mutableStateOf(false) }
        val showDeleteDialog = remember { mutableStateOf(false) }
        val clickedMeasurement = remember { mutableStateOf(Measurement(0, 0, 0.0f, "")) }
        val measurementsList = remember { mutableStateOf(exercise.measurementsList.toMutableList()) }
        val sortedMeasurementsList = remember(measurementsList.value) {
            measurementsList.value.sortedByDescending { it.date.time }
        }
        val weeklyProgressList = remember(measurementsList.value) {
            createWeeklyProgressList(measurementsList.value).sortedByDescending { it.firstDate }
        }

        Log.d("Best Measurement", exercise.bestMeasurement?.weight.toString())
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
                            painter = painterResource(
                                id = exerciseClass.getPhotoResourceId(
                                    LocalContext.current
                                )
                            ),
                            contentDescription = "Exercise Image"
                        )
                        Text(
                            text = "${exerciseClass.name} - ${exerciseClass.categoryString}",
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
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp), contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No measurements yet")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            stickyHeader {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .background(MaterialTheme.colorScheme.surface)
                                ) {
                                    Text(
                                        text = "Date",
                                        modifier = Modifier.weight(1f),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Reps",
                                        modifier = Modifier.weight(1f),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Weight (kg)",
                                        modifier = Modifier.weight(1f),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            items(sortedMeasurementsList) { measurement ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            onClick = {
                                                showDeleteDialog.value = true
                                                clickedMeasurement.value = measurement
                                            }
                                        )
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = formatStringDate(measurement.date.time),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${measurement.reps}",
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${measurement.weight}",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
                val weekSize = 1.5f
                val yearSize = 0.5f
                val avgWeightSize = 1f
                val weightDiffSize = 1f

                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    LazyColumn {
                        if (weeklyProgressList.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp), contentAlignment = Alignment.Center
                                ) {
                                    Text(text = ":(")
                                }
                            }
                        } else {
                            stickyHeader {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .background(MaterialTheme.colorScheme.surface)
                                ) {
                                    Text(
                                        text = "Week",
                                        modifier = Modifier.weight(weekSize),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Year",
                                        modifier = Modifier.weight(yearSize),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "AVG Weight",
                                        modifier = Modifier.weight(avgWeightSize),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Weight Diff",
                                        modifier = Modifier.weight(weightDiffSize),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            items(weeklyProgressList) { weeklyProgress ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "${weeklyProgress.weekNumber} (${weeklyProgress.weekRange})",
                                        modifier = Modifier.weight(weekSize)
                                    )
                                    Text(
                                        text = "${weeklyProgress.year}",
                                        modifier = Modifier.weight(yearSize)
                                    )
                                    val avgDifference = weeklyProgress.avgWeightDifference
                                    Text(
                                        text = "${roundTheNumber(weeklyProgress.avgWeight)}",
                                        modifier = Modifier.weight(avgWeightSize)
                                    )
                                    val tint = GetTint(avgDifference)
                                    Row(
                                        modifier = Modifier.weight(weightDiffSize)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = GetRDrawable(avgDifference)),
                                            contentDescription = "Weight Difference",
                                            modifier = Modifier.size(22.dp),
                                            tint = tint
                                        )

                                        Text(
                                            text = " ${roundTheNumber(avgDifference)}",
                                        )
                                    }
                                }
                            }
                        }
                    }
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

    fun roundTheNumber(numInDouble: Float): String {
        return "%.1f".format(numInDouble)
    }

    @Composable
    fun AddMeasurementDialog(
        onDismissRequest: () -> Unit,
        measurementsList: MutableState<MutableList<Measurement>>,
        exerciseView: ExerciseView
    ) {
        val reps = remember { mutableStateOf<String>("") }
        val weight = remember { mutableStateOf<String>("") }
        val isDoubleWeight = remember { mutableStateOf<Boolean>(false) }
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
                            onValueChange = { reps.value = it },
                            label = { Text("Reps") }
                        )
                        Text(
                            text = " x ",
                            fontSize = 20.sp
                        )
                        TextField(
                            value = weight.value,
                            onValueChange = { weight.value = it },
                            label = { Text("Weight") }
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text="weight on both sides"
                        )
                        RadioButton(
                            selected = isDoubleWeight.value,
                            onClick = { isDoubleWeight.value = !isDoubleWeight.value }
                        )
                    }
                    Button(onClick = {
                        val repsInt = reps.value.toIntOrNull()
                        val weightFloat = weight.value.toFloatOrNull()

                        if (repsInt == null || weightFloat == null) {
                            ToastManager(context, "Reps must be an integer and weight must be a number")
                            return@Button
                        }
                        if (repsInt <= 0 || weightFloat <= 0.0f) {
                            ToastManager(context, "Reps or weight cannot be less than or equal to 0")
                            return@Button
                        }

                        scope.launch {
                            exerciseView.addMeasurementDatabase(
                                reps.value.toInt(),
                                weight.value.toFloat() * if(isDoubleWeight.value) 2 else 1,
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
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(
                modifier = Modifier.height(200.dp)
                    .width(400.dp)
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = exercise.exerciseDecs
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
                modifier = Modifier
                    .width(300.dp)
                    .height(180.dp)
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
                                exerciseView.deleteMeasurementDatabase(
                                    measurement,
                                    measurementsList
                                )
                            }
                            onDismissRequest()
                        }
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    }}
fun GetTint(value: Float): Color{
    if(value == 0.0f) return Color.Gray

    if(value > 0) return Color.Green
    return Color.Red
}
fun GetRDrawable(value: Float): Int{
    if(value == 0.0f) return R.drawable.none

    if(value > 0) return R.drawable.up
    return R.drawable.down
}