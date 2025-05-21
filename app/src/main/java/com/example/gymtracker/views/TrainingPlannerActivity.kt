// app/src/main/java/com/example/gymtracker/TrainingPlannerActivity.kt
package com.example.gymtracker.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
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
import com.example.gymtracker.data.ExerciseClass
import com.example.gymtracker.managers.ExerciseManager
import com.example.gymtracker.R
import com.example.gymtracker.managers.TrainingManager
import com.example.gymtracker.services.TrainingManagerService
import com.example.gymtracker.ui.theme.Black
import com.example.gymtracker.ui.theme.BlackDark
import com.example.gymtracker.ui.theme.GymTrackerTheme
import java.util.Calendar

private lateinit var viewModel: TrainingPlannerViewModel

class TrainingPlannerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTrackerTheme {
                viewModel = TrainingPlannerViewModel(LocalContext.current)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainView(
                        modifier = Modifier.padding(innerPadding),
                        trainingManagerService = viewModel.trainingManagerService
                    )
                }
            }
        }
    }
}

@Composable
fun MainView(modifier: Modifier = Modifier, trainingManagerService: TrainingManagerService) {
    val calendar: Calendar = Calendar.getInstance()
    var totalDrag by remember { mutableStateOf(0f) }
    viewModel.currentDayIndex = remember {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val adjustedDayIndex = (dayOfWeek - 1) % 7
        mutableStateOf(adjustedDayIndex)
    }

    //trainingPlannerViewModel.loadWarmUp()
    viewModel.showAddDialog = remember { mutableStateOf(false) }
    viewModel.exercisesView.value = viewModel.trainingManagerService.loadExercisesForDay(
        TrainingManager.daysOfWeek[viewModel.currentDayIndex.value].day
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
                            viewModel.increaseDayIndex()
                            viewModel.exercisesView.value = trainingManagerService.loadExercisesForDay(
                                TrainingManager.daysOfWeek[viewModel.currentDayIndex.value].day
                            )
                        } else if (totalDrag > 25) {
                            viewModel.decreateDayIndex()
                            viewModel.exercisesView.value = trainingManagerService.loadExercisesForDay(
                                TrainingManager.daysOfWeek[viewModel.currentDayIndex.value].day
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
                .padding(0.dp, 5.dp, 0.dp, 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row() {
                for (day in TrainingManager.daysOfWeek) {
                    val firstChar = day.day.subSequence(0, 3).toString()
                    var currentColor: Color = Black

                    if (TrainingManager.daysOfWeek.indexOf(day) == viewModel.currentDayIndex.value) {
                        currentColor = BlackDark
                    }
                    Text(
                        modifier = Modifier
                            .padding(12.dp)
                            .drawBehind {
                                drawCircle(
                                    color = currentColor,
                                    radius = 60f
                                )
                            }
                            .clickable() {
                                viewModel.currentDayIndex.value =
                                    TrainingManager.daysOfWeek.indexOf(day)
                            },
                        text = firstChar
                    )
                }
            }
            if (!viewModel.exercisesView.value.isEmpty()) {
                //WarmUpButton()
            } else {
                EmptyCard()
            }
            LazyColumn {
                items(viewModel.exercisesView.value) { exercise ->
                    ExericeCard(exercise)
                }
                item {
                    AddExercisePanelPanel()
                }
            }


            if (viewModel.showAddDialog.value) {
                AddExericeToDay(
                    onDismissRequest = { viewModel.showAddDialog.value = false },
                    onAddExercise = { exercise ->
                        val currentDay = TrainingManager.daysOfWeek[viewModel.currentDayIndex.value]
                        currentDay.exercises.add(exercise)
                        viewModel.exercisesView.value = currentDay.exercises.toMutableList()
                        TrainingManager.saveTrainingPlan(
                            viewModel.context,
                            currentDay.day,
                            exercise.name
                        )
                    }
                )
            }

            if (viewModel.showRemoveDialog.value) {
                RemoveExerciseCard(viewModel.selectedToRemove.value)
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                modifier = Modifier
                    .size(25.dp)
                    .scale(scaleX = -1f, scaleY = 1f),
                onClick = {
                    viewModel.decreateDayIndex()

                    viewModel.exercisesView.value = trainingManagerService.loadExercisesForDay(
                        TrainingManager.daysOfWeek[viewModel.currentDayIndex.value].day
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
                    viewModel.increaseDayIndex()
                    viewModel.exercisesView.value = trainingManagerService.loadExercisesForDay(
                        TrainingManager.daysOfWeek[viewModel.currentDayIndex.value].day
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
                                if (viewModel.exercisesView.value.contains(exercise)) {
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
                        if (exercise.isCustom) {
                            Image(
                                bitmap = exercise.getImage(),
                                contentDescription = "${exercise.name} image",
                                contentScale = ContentScale.Crop,
                                //colorFilter = ColorFilter.tint(colorResource(id = R.color.images)),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(50.dp)
                                    //.size(100.dp)
                                    .clip(RoundedCornerShape(percent = 10))
                            )
                        } else {
                            Icon(
                                painter = painterResource(
                                    id = exercise.getPhotoResourceId(
                                        LocalContext.current
                                    )
                                ),
                                contentDescription = "Exercise photo",
                                tint = colorResource(id = R.color.images),
                                modifier = Modifier
                                    .padding(8.dp)
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
fun AddExercisePanelPanel() {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(50.dp)
            .clickable()
            {
                viewModel.showAddDialog.value = true
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
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
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                val exerciseIndex = ExerciseManager.exercises.indexOf(exercise)
                LaunchExerciseIntent(exerciseIndex, viewModel.context)
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
                    contentScale = ContentScale.Crop,
                    //colorFilter = ColorFilter.tint(colorResource(id = R.color.images)),
                    modifier = Modifier
                        .padding(8.dp)
                        //.size(50.dp)
                        .size(100.dp)
                        .clip(RoundedCornerShape(percent = 10))
                )
            } else {
                Icon(
                    painter = painterResource(id = exercise.getPhotoResourceId(viewModel.context)),
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
                    viewModel.showRemoveDialog.value = true
                    viewModel.selectedToRemove.value = exercise
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
            viewModel.showRemoveDialog.value = false
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
                            viewModel.showRemoveDialog.value = false
                            TrainingManager.removeExerciseFromPlan(
                                context,
                                viewModel.getStringDay(viewModel.currentDayIndex.value),
                                exercise.name
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text(text = "Yes")
                    }
                    Button(
                        onClick = {
                            viewModel.showRemoveDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(text = "No")
                    }
                }
            }
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
