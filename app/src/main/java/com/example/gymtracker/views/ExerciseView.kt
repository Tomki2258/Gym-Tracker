// ExerciseView.kt
package com.example.gymtracker.views

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.gymtracker.ui.theme.BlackDark
import com.example.gymtracker.ui.theme.GymTrackerTheme
import com.example.gymtracker.ui.theme.Purple40
import kotlinx.coroutines.launch


class ExerciseView : ComponentActivity() {
    private lateinit var exerciseViewModel: ExerciseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            MeasurementDatabase::class.java,
            "measurement_database"
        ).build()
        val dao = db.measurementDao()

        val measurementViewModel = ViewModelProvider(
            this,
            MeasurementViewModel.Factory(dao)
        ).get(MeasurementViewModel::class.java)
        setContent {
            GymTrackerTheme {
                val index = intent.getIntExtra("EXERCISE_INDEX", 0)
                val exerciseClass = ExerciseManager.exercises[index]
                exerciseViewModel = ExerciseViewModel(measurementViewModel,exerciseClass)

                ExerciseIntent(
                    exerciseClass,
                    exerciseView = this,
                    exerciseViewModel
                )
            }
        }
    }

    @SuppressLint("StateFlowValueCalledInComposition")
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
        //TODO move ExerciseView values to viewModel
        exerciseViewModel.context = LocalContext.current
        val showDialog = exerciseViewModel.showDialogState.collectAsState()
        val showDescDialog = exerciseViewModel.showDescDialogState.collectAsState()
        val showDeleteDialog = exerciseViewModel.showDeleteDialogState.collectAsState()

        val clickedMeasurement = exerciseViewModel.clicledMeasurementState.collectAsState()

        val measurementsList =
            remember { mutableStateOf(exerciseViewModel.getMeasurementsList()) }

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
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()

        ) {
            Column {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                    ,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if(exerciseViewModel.getExercise().isCustom) {
                            Image(
                                bitmap = exerciseViewModel.getExercise().getImage(),
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
                                painter = painterResource(id = exerciseViewModel.getExercise().getPhotoResourceId(LocalContext.current)),
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
                        .height(250.dp)
                    ,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),

                    ) {
                    if (measurementsList.value.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                            , contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No measurements yet")
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp,8.dp,8.dp,0.dp)
                                .background(Color(0xFF49454f))
                        ) {
                            Text(
                                text = "  Date",
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
                        LazyColumn(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            items(sortedMeasurementsList) { measurement ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            onClick = {
                                                exerciseViewModel.updateDeleteDialog(true)
                                                exerciseViewModel.updateClickedMeasurement(measurement)
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color(0xFF49454f))
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
                onClick = { exerciseViewModel.updateShowDescDialog(true) },
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
                onClick = { exerciseViewModel.updateShowDialog(true) },
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
                onDismissRequest = { exerciseViewModel.updateShowDialog(false) },
                measurementsList,
                exerciseView
            )
        }
        if (showDescDialog.value) {
            ShowExerciseInfo(onDismissRequest = { exerciseViewModel.updateShowDescDialog(false) },exerciseViewModel)
        }
        if (showDeleteDialog.value) {
            ShowDeleteMeasurement(
                clickedMeasurement.value,
                onDismissRequest = { exerciseViewModel.updateDeleteDialog(false) },
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
                            singleLine = true,
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
                            singleLine = true,
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
                                    exerciseViewModel.getExercise().name,
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
                        text = exerciseViewModel.getExercise().exerciseDecs
                    )
                    if (exerciseViewModel.getExercise().isCustom) {
                        Button(
                            onClick = {
                                finish()
                                exerciseViewModel.getExercise().removeImage(exerciseViewModel.context)
                                exerciseViewModel.getExercise().exericseEntity?.let {
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