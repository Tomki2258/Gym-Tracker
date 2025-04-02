// ExerciseView.kt
package com.example.gymtracker.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.gymtracker.data.Categories
import com.example.gymtracker.data.ExerciseClass
import com.example.gymtracker.managers.ExerciseManager
import com.example.gymtracker.data.Measurement
import com.example.gymtracker.R
import com.example.gymtracker.other.ToastManager
import com.example.gymtracker.roomdb.MeasurementDatabase
import com.example.gymtracker.roomdb.MeasurementViewModel
import com.example.gymtracker.ui.theme.GymTrackerTheme
import kotlinx.coroutines.launch

var exercise = ExerciseClass("Default Name", Categories.CALVES, exericseEntity = null)

class ExerciseView : ComponentActivity() {
    private lateinit var measurementViewModel: MeasurementViewModel
    private lateinit var exerciseViewModel: ExerciseViewModel
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
        exerciseViewModel = ExerciseViewModel(measurementViewModel)
        setContent {
            GymTrackerTheme {
                val index = intent.getIntExtra("EXERCISE_INDEX", 0)
                val exerciseClass = ExerciseManager.exercises[index]
                exercise = exerciseClass
                ExerciseIntent(
                    exerciseClass = exerciseClass,
                    exerciseView = this,
                    exerciseViewModel
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ExerciseIntent(
        exerciseClass: ExerciseClass = ExerciseClass(
            "Default Name",
            Categories.CALVES,
            "no_desc.txt",
            exericseEntity = null
        ),
        exerciseView: ExerciseView,
        exerciseViewModel: ExerciseViewModel
    ) {
        exerciseViewModel.context = LocalContext.current
        val showDialog = remember { mutableStateOf(false) }
        val showDescDialog = remember { mutableStateOf(false) }
        val showDeleteDialog = remember { mutableStateOf(false) }
        val clickedMeasurement = remember { mutableStateOf(Measurement(0, 0, 0.0f, "")) }
        val measurementsList =
            remember { mutableStateOf(exercise.measurementsList.toMutableList()) }
        val sortedMeasurementsList = remember(measurementsList.value) {
            measurementsList.value.sortedByDescending { it.date.time }
        }

        val weeklyProgressList = remember(measurementsList.value) {
            exerciseViewModel.createWeeklyProgressList(measurementsList.value).sortedByDescending { it.firstDate }
        }
        //Log.d("Best Measurement", exercise.bestMeasurement?.weight.toString())
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp, 12.dp, 0.dp, 0.dp)
        ) {
            Column {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if(exercise.isCustom) {
                            Image(
                                bitmap = exercise.getImage(),
                                contentScale = ContentScale.Crop,
                                //colorFilter = ColorFilter.tint(colorResource(id = R.color.images)),
                                contentDescription = "Exercise Image",
                                modifier = Modifier
                                    .size(exerciseViewModel.imageUIsize)
                                    .clip(RoundedCornerShape(percent = 10))
                            )
                        }
                        else{
                            Image(
                                painter = painterResource(id = exercise.getPhotoResourceId(LocalContext.current)),
                                colorFilter = ColorFilter.tint(colorResource(id = R.color.images)),
                                contentDescription = "Exercise Image",
                                modifier = Modifier.size(exerciseViewModel.imageUIsize),
                            )
                        }
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
                        .height(250.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),

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
                                        .padding(8.dp, 0.dp, 8.dp, 8.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
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
                                        text = exerciseViewModel.formatStringDate(measurement.date.time),
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

                Card(
                    modifier = Modifier
                        .padding(8.dp, 0.dp, 8.dp, 8.dp)
                        .fillMaxWidth()
                        .height(200.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),

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
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Text(
                                        text = "Week",
                                        modifier = Modifier.weight(exerciseViewModel.weekSize),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Year",
                                        modifier = Modifier.weight(exerciseViewModel.yearSize),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "AVG",
                                        modifier = Modifier.weight(exerciseViewModel.avgWeightSize),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Weight Diff",
                                        modifier = Modifier.weight(exerciseViewModel.weightDiffSize),
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
                                        modifier = Modifier.weight(exerciseViewModel.weekSize)
                                    )
                                    Text(
                                        text = "${weeklyProgress.year}",
                                        modifier = Modifier.weight(exerciseViewModel.yearSize)
                                    )
                                    val avgDifference = weeklyProgress.avgWeightDifference
                                    Text(
                                        text = "${exerciseViewModel.roundTheNumber(weeklyProgress.avgWeight)}",
                                        modifier = Modifier.weight(exerciseViewModel.avgWeightSize)
                                    )
                                    val tint = exerciseViewModel.GetTint(avgDifference)
                                    Row(
                                        modifier = Modifier.weight(exerciseViewModel.weightDiffSize)
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                id = exerciseViewModel.GetRDrawable(
                                                    avgDifference
                                                )
                                            ),
                                            contentDescription = "Weight Difference",
                                            modifier = Modifier.size(22.dp),
                                            tint = tint
                                        )

                                        Text(
                                            text = " ${exerciseViewModel.roundTheNumber(avgDifference)}",
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
            ShowExerciseInfo(onDismissRequest = { showDescDialog.value = false },exerciseViewModel)
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


    @OptIn(ExperimentalMaterial3Api::class)
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
            Card(
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = reps.value.toString(),
                            onValueChange = { reps.value = it },
                            label = { Text("Reps") },
                            modifier = Modifier.width(75.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                disabledTextColor = MaterialTheme.colorScheme.inversePrimary,
                                cursorColor = MaterialTheme.colorScheme.inversePrimary,
                                errorCursorColor = MaterialTheme.colorScheme.inversePrimary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.inversePrimary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.inversePrimary,
                                disabledIndicatorColor = MaterialTheme.colorScheme.inversePrimary,
                                errorIndicatorColor = MaterialTheme.colorScheme.inversePrimary,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.inversePrimary,
                                errorLeadingIconColor = MaterialTheme.colorScheme.inversePrimary,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.inversePrimary,
                                errorTrailingIconColor = MaterialTheme.colorScheme.inversePrimary,
                                focusedLabelColor = MaterialTheme.colorScheme.inversePrimary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.inversePrimary,
                                disabledLabelColor = MaterialTheme.colorScheme.inversePrimary,
                                errorLabelColor = MaterialTheme.colorScheme.inversePrimary,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.inversePrimary
                            )
                        )
                        Text(
                            text = " x ",
                            fontSize = 20.sp
                        )
                        TextField(
                            value = weight.value,
                            onValueChange = { weight.value = it },
                            label = { Text("Weight") },
                            colors = TextFieldDefaults.textFieldColors(
                                disabledTextColor = MaterialTheme.colorScheme.inversePrimary,
                                cursorColor = MaterialTheme.colorScheme.inversePrimary,
                                errorCursorColor = MaterialTheme.colorScheme.inversePrimary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.inversePrimary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.inversePrimary,
                                disabledIndicatorColor = MaterialTheme.colorScheme.inversePrimary,
                                errorIndicatorColor = MaterialTheme.colorScheme.inversePrimary,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.inversePrimary,
                                errorLeadingIconColor = MaterialTheme.colorScheme.inversePrimary,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.inversePrimary,
                                errorTrailingIconColor = MaterialTheme.colorScheme.inversePrimary,
                                focusedLabelColor = MaterialTheme.colorScheme.inversePrimary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.inversePrimary,
                                disabledLabelColor = MaterialTheme.colorScheme.inversePrimary,
                                errorLabelColor = MaterialTheme.colorScheme.inversePrimary,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.inversePrimary
                            )
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "weight on both sides"
                        )
                        RadioButton(
                            selected = isDoubleWeight.value,
                            onClick = { isDoubleWeight.value = !isDoubleWeight.value },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.inversePrimary,
                                unselectedColor = MaterialTheme.colorScheme.inversePrimary
                            )
                        )
                    }
                    Button(
                        onClick = {
                            val repsInt = reps.value.toIntOrNull()
                            val weightFloat = weight.value.toFloatOrNull()

                            if (repsInt == null || weightFloat == null) {
                                ToastManager(
                                    context,
                                    "Reps must be an integer and weight must be a number"
                                )
                                return@Button
                            }
                            if (repsInt <= 0 || weightFloat <= 0.0f) {
                                ToastManager(
                                    context,
                                    "Reps or weight cannot be less than or equal to 0"
                                )
                                return@Button
                            }

                            scope.launch {
                                exerciseView.exerciseViewModel.addMeasurementDatabase(
                                    reps.value.toInt(),
                                    weight.value.toFloat() * if (isDoubleWeight.value) 2 else 1,
                                    exercise.name,
                                    measurementsList
                                )
                                onDismissRequest()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text(text = "Add measurement")
                    }
                }
            }
        }
    }

    @Composable
    fun ShowExerciseInfo(onDismissRequest: () -> Unit,exerciseViewModel: ExerciseViewModel) {
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(
                modifier = Modifier
                    .height(200.dp)
                    .width(400.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = exercise.exerciseDecs
                    )
                    if (exercise.isCustom) {
                        Button(
                            onClick = {
                                finish()
                                exercise.removeImage(exerciseViewModel.context)
                                exercise.exericseEntity?.let {
                                    ExerciseManager.DeleteExercise(it)
                                }
                                onDismissRequest()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        ) {
                            Text(text = "Delete Exercise")
                        }
                    }
                }
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
                    .height(200.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),

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
                                exerciseView.exerciseViewModel.deleteMeasurementDatabase(
                                    measurement,
                                    measurementsList
                                )
                            }
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    }
}