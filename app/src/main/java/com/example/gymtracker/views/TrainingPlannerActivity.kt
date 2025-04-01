// app/src/main/java/com/example/gymtracker/TrainingPlannerActivity.kt
package com.example.gymtracker.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gymtracker.managers.ApiManager
import com.example.gymtracker.data.Categories
import com.example.gymtracker.data.ExerciseClass
import com.example.gymtracker.managers.ExerciseManager
import com.example.gymtracker.R
import com.example.gymtracker.managers.TrainingManager
import com.example.gymtracker.ui.theme.GymTrackerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class TrainingPlannerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainView(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

var showAddDialog = mutableStateOf(false)
var showRemoveDialog = mutableStateOf(false)
var selectedToRemove = mutableStateOf(ExerciseClass("", Categories.CHEST, exericseEntity = null))
var showWarmUpDialog = mutableStateOf(false)

var warmUpList = mutableStateOf(mutableListOf<String>())


fun loadWarmUp(context: Context) {
    CoroutineScope(Dispatchers.Main).launch {
        warmUpList.value.clear()
        for (day in TrainingManager.daysOfWeek) {
            val exercises = loadExercisesForDay(context, day.day)
            val uniqueCategories = exercises.map { it.categoryString }.toSet()
            val categories = uniqueCategories.joinToString("-").lowercase(Locale.getDefault())
            val warmUp = ApiManager.getWarpUp(categories)
            warmUpList.value.add(warmUp)
        }
    }
}

fun loadExercisesForDay(context: Context, day: String): MutableList<ExerciseClass> {
    val exercises = mutableListOf<ExerciseClass>()
    val planTrainings = TrainingManager.getTrainingPlan(context, day)
    planTrainings.forEach { training ->
        val exercise = ExerciseManager.exercises.find { it.name == training.exercise }
        if (exercise != null) {
            exercises.add(exercise)
        }
    }
    return exercises
}

var exercisesView = mutableStateOf(mutableListOf<ExerciseClass>())
var currentDayIndex = mutableStateOf(0)

fun getStringDay(dayIndex: Int): String {
    return when (dayIndex) {
        0 -> "Monday"
        1 -> "Tuesday"
        2 -> "Wednesday"
        3 -> "Thursday"
        4 -> "Friday"
        5 -> "Saturday"
        6 -> "Sunday"
        else -> "Monday"
    }
}

fun IncreaseDayIndex() {
    currentDayIndex.value++
    if (currentDayIndex.value >= TrainingManager.daysOfWeek.size) {
        currentDayIndex.value = 0
    }
}

fun DecreateDayIndex() {
    currentDayIndex.value--
    if (currentDayIndex.value < 0) {
        currentDayIndex.value = TrainingManager.daysOfWeek.size - 1
    }
}

@Composable
fun MainView(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val calendar: Calendar = Calendar.getInstance()
    var totalDrag by remember { mutableStateOf(0f) }
    currentDayIndex = remember {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val adjustedDayIndex = (dayOfWeek - 1) % 7
        mutableStateOf(adjustedDayIndex)
    }

    loadWarmUp(context)
    showAddDialog = remember { mutableStateOf(false) }
    exercisesView.value = loadExercisesForDay(
        context,
        TrainingManager.daysOfWeek[currentDayIndex.value].day
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 24.dp, 0.dp, 0.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        totalDrag += dragAmount
                        //Log.d("PRZESUNIECIE: ", "amount: " + dragAmount + " total: " + totalDrag)
                    },
                    onDragEnd = {
                        if (totalDrag < -25) {
                            IncreaseDayIndex()
                            exercisesView.value = loadExercisesForDay(
                                context,
                                TrainingManager.daysOfWeek[currentDayIndex.value].day
                            )
                        } else if (totalDrag > 25) {
                            DecreateDayIndex()
                            exercisesView.value = loadExercisesForDay(
                                context,
                                TrainingManager.daysOfWeek[currentDayIndex.value].day
                            )
                        }
//                        if (totalDrag < -15) {
//                            if (viewModel.currentPage.value < screenCount - 1)
//                                viewModel.updateCurrentPage(currentPage + 1)
//                            else
//                                viewModel.updateCurrentPage(0)
//                        } else if (totalDrag > 15) {
//                            if (viewModel.currentPage.value > 0)
//                                viewModel.updateCurrentPage(currentPage - 1)
//                            else
//                                viewModel.updateCurrentPage(screenCount - 1)
//                        }
                        totalDrag = 0f
                    }
                )
            }, contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp, 24.dp, 0.dp, 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = TrainingManager.daysOfWeek[currentDayIndex.value].day,
                modifier = Modifier.padding(8.dp)
            )
            if (!exercisesView.value.isEmpty()) {
                //WarmUpButton()
            } else {
                EmptyCard()
            }
            LazyColumn {
                items(exercisesView.value) { exercise ->
                    ExericeCard(exercise, currentDayIndex.value)
                }
            }

            FloatingActionButton(
                onClick = { showAddDialog.value = true },
                modifier = Modifier
                    .padding(16.dp)
                    .requiredSize(56.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add measurement",
                    modifier = Modifier.fillMaxSize(0.6f)
                )
            }

            if (showAddDialog.value) {
                AddExericeToDay(
                    onDismissRequest = { showAddDialog.value = false },
                    onAddExercise = { exercise ->
                        val currentDay = TrainingManager.daysOfWeek[currentDayIndex.value]
                        currentDay.exercises.add(exercise)
                        exercisesView.value = currentDay.exercises.toMutableList()
                        TrainingManager.saveTrainingPlan(
                            context,
                            currentDay.day,
                            exercise.name
                        )
                    }
                )
            }

            if (showRemoveDialog.value) {
                RemoveExerciseCard(selectedToRemove.value)
            }
            if (showWarmUpDialog.value) {
                WarmUpCard(
                    onDismissRequest = { showWarmUpDialog.value = false },
                    warmUpString = warmUpList.value[currentDayIndex.value]
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 36.dp,
                    start = 8.dp,
                    top = 8.dp,
                    end = 8.dp
                )
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                modifier = Modifier
                    .size(25.dp)
                    .scale(scaleX = -1f, scaleY = 1f),
                onClick = {
                    DecreateDayIndex()

                    exercisesView.value = loadExercisesForDay(
                        context,
                        TrainingManager.daysOfWeek[currentDayIndex.value].day
                    )
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.right_arrow),
                    contentDescription = "Previous day"
                )
            }

            IconButton(
                modifier = Modifier
                    .size(25.dp)
                    .scale(scaleX = 1f, scaleY = 1f),
                onClick = {
                    IncreaseDayIndex()

                    exercisesView.value = loadExercisesForDay(
                        context,
                        TrainingManager.daysOfWeek[currentDayIndex.value].day
                    )
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.right_arrow),
                    contentDescription = "Next day"
                )
            }
        }
    }
}

@Composable
fun AddExericeToDay(
    onDismissRequest: () -> Unit,
    onAddExercise: (ExerciseClass) -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            LazyColumn {
                items(ExerciseManager.exercises) { exercise ->
                    Row(
                        modifier = Modifier
                            .clickable {
                                if (exercisesView.value.contains(exercise)) {
                                    onDismissRequest()
                                    return@clickable
                                }
                                onAddExercise(exercise)
                                onDismissRequest()
                            }
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = exercise.name, fontWeight = FontWeight.Bold
                            )
                            Text(text = exercise.categoryString)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        if(exercise.isCustom) {
                            Image(
                                bitmap = exercise.getImage(),
                                contentDescription = "${exercise.name} image",
                                //colorFilter = ColorFilter.tint(colorResource(id = R.color.images)),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(50.dp)
                                    //.size(100.dp)
                                    .clip(RoundedCornerShape(percent = 10))
                            )
                        }
                        else{
                            Icon(
                                painter = painterResource(id = exercise.getPhotoResourceId(LocalContext.current)),
                                contentDescription = "Exercise photo",
                                tint = colorResource(id = R.color.images),
                                modifier = Modifier.padding(8.dp)
                                    .size(50.dp)
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
fun ExericeCard(
    exercise: ExerciseClass,
    dayIndex: Int,
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                val exerciseIndex = ExerciseManager.exercises.indexOf(exercise)
                LaunchExerciseIntent(exerciseIndex, context)
            },
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (exercise.isCustom) {
                Image(
                    bitmap = exercise.getImage(),
                    contentDescription = "${exercise.name} image",
                    //colorFilter = ColorFilter.tint(colorResource(id = R.color.images)),
                    modifier = Modifier
                        .padding(8.dp)
                        //.size(100.dp)
                        .clip(RoundedCornerShape(percent = 10))
                )
            } else {
                Icon(
                    painter = painterResource(id = exercise.getPhotoResourceId(context)),
                    contentDescription = "Exercise photo",
                    tint = colorResource(id = R.color.images),
                    modifier = Modifier.padding(8.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = exercise.name,
                    fontSize = 20.sp
                )
                Text(text = exercise.categoryString)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    showRemoveDialog.value = true
                    selectedToRemove.value = exercise
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

@Composable
fun RemoveExerciseCard(exercise: ExerciseClass) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = {
            showRemoveDialog.value = false
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = buildAnnotatedString {
                    append("Do you want to remove ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(exercise.name)
                    }
                    append(" from the training plan?")
                })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            showRemoveDialog.value = false
                            TrainingManager.removeExerciseFromPlan(
                                context,
                                getStringDay(currentDayIndex.value),
                                exercise.name
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text(text = "Yes")
                    }
                    Button(
                        onClick = {
                            showRemoveDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(text = "No")
                    }
                }
            }
        }
    }
    fun LaunchExerciseIntent(exerciseIndex: Int, context: Context) {
        val intent = Intent(context, ExerciseView::class.java).apply {
            putExtra("EXERCISE_INDEX", exerciseIndex)
        }
        context.startActivity(intent)
    }
}

@Composable
fun WarmUpButton() {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                showWarmUpDialog.value = true
            },
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Warm Up !",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun WarmUpCard(
    onDismissRequest: () -> Unit, warmUpString: String
) {
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = warmUpString
            )
        }
    }
}

@Composable
fun EmptyCard() {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(50.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Click + to add exercise",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
