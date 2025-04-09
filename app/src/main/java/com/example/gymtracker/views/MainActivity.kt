// MainActivity.kt
package com.example.gymtracker.views


import com.example.gymtracker.services.AndroidAlarmScheduler
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.room.Room
import com.example.gymtracker.AlarmItem
import com.example.gymtracker.data.ExerciseClass
import com.example.gymtracker.managers.ExerciseManager
import com.example.gymtracker.services.NotifycationsService
import com.example.gymtracker.R
import com.example.gymtracker.managers.UserManager
import com.example.gymtracker.roomdb.MeasurementDatabase
import com.example.gymtracker.roomdb.MeasurementViewModel
import com.example.gymtracker.ui.theme.GymTrackerTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.time.LocalDateTime
import java.util.Calendar
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale


private lateinit var service: NotifycationsService
private lateinit var alarmScheduler: AndroidAlarmScheduler
private lateinit var mainActivityViewModel: MainActivityViewModel

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            MeasurementDatabase::class.java,
            "measurement_database"
        ).build()
    }

    private val dao by lazy { db.measurementDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExerciseManager.initialize(this)
        UserManager.initialize(this)
        service = NotifycationsService(this)

        //TrainingManager.init(this)
        enableEdgeToEdge()
        setContent {
            GymTrackerTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                    //.padding(4.dp, 16.dp, 4.dp, 0.dp)
                ) { innerPadding ->
                    MainView(
                        modifier = Modifier.padding(innerPadding),
                        MeasurementViewModel(dao)
                    )
                }
            }
        }
        //val event = MeasurementEvent.InsertMeasurement(measurement)
        //measurementViewModel.onEvent(event)
    }
}

//fun LoadExercises(): List<ExerciseClass> {
//    val exerciseList = listOf(
//        ExerciseClass("Chest fly", "Chest"),
//        ExerciseClass("Leg curl", "Legs"),
//        ExerciseClass("Leg press", "Legs"),
//        ExerciseClass("Dumbbell biceps", "Arms"),
//        ExerciseClass("Bench press", "Chest"),
//        ExerciseClass("Seated barbell press", "Shoulders"),
//    )
//    return exerciseList
//}

fun LoadCategories(exercises: List<ExerciseClass>): List<String> {
    val categories = mutableListOf<String>()
    categories.add("All")
    for (exercise in exercises) {
        if (!categories.contains(exercise.category.toString())) {
            categories.add(exercise.category.toString())
        }
    }

    return categories
}

@Composable
fun MainView(modifier: Modifier = Modifier, measurementViewModel: MeasurementViewModel) {
    //HideStatusBar()
    mainActivityViewModel = MainActivityViewModel()
    mainActivityViewModel.context = LocalContext.current

    for (ex in ExerciseManager.exercises){
        ex.loadImage(mainActivityViewModel.context)
    }
    val categories = LoadCategories(ExerciseManager.exercises)
    var currentCategory by remember { mutableStateOf(categories.first()) }
    val showNickameDialog = remember { mutableStateOf(false) }
    val showHourDialog = remember { mutableStateOf(false) }
    var totalDrag by remember { mutableStateOf(0f) }
    alarmScheduler = AndroidAlarmScheduler(
        mainActivityViewModel.context
    )

    mainActivityViewModel.RequestNotificationPermission()
    //Log.d("Color", MaterialTheme.colorScheme.background.toString())
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(0.dp, 16.dp, 0.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Spacer(modifier = Modifier.height(32.dp))
//                IconButton(onClick = {
//                    LaunchUserIntent(context)
//                }) {
//                    Icon(
//                        painter = painterResource(R.drawable.icons8_male_user_100),
//                        contentDescription = "User Icon"
//                    )
//                }
//                Text(
//                    modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
//                    text = UserManager.wellcomeMessage
//                )
//                IconButton(onClick = {
//                    measurementViewModel.clearAllTables()
//                    TrainingManager.deleteDatabase(context)
//                }) {
//                    Icon(
//                        painter = painterResource(R.drawable.icons8_settings_500),
//                        contentDescription = "User Icon"
//                    )
//                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    //.background(Color.LightGray)
                    .padding(0.dp, 0.dp, 0.dp, 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    var catIndex = categories.indexOf(currentCategory)
                    catIndex -= 1
                    if (catIndex < 0) {
                        catIndex = categories.size - 1
                    }
                    currentCategory = categories[catIndex]
                    //Log.d(currentCategory, currentCategory)
                }) {
                    Icon(
                        modifier = Modifier
                            .size(25.dp)
                            .scale(scaleX = -1f, scaleY = 1f),
                        painter = painterResource(R.drawable.right_arrow),
                        contentDescription = "Filter Icon"
                    )
                }
                var catIndex = categories.indexOf(currentCategory)
                Text(
                    text = "${currentCategory.lowercase().replaceFirstChar { it.uppercase() }}\n${catIndex + 1} / ${categories.size}",
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = {
                    catIndex += 1
                    if (catIndex > categories.size - 1) {
                        catIndex = 0
                    }
                    currentCategory = categories[catIndex]
                    Log.d(currentCategory, currentCategory)
                }) {
                    Icon(
                        modifier = Modifier.size(25.dp),
                        painter = painterResource(R.drawable.right_arrow),
                        contentDescription = "Filter Icon"
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
                    .padding(0.dp, 0.dp, 0.dp, 64.dp)
                    .pointerInput(Unit)
                    {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                totalDrag += dragAmount
                                Log.d("PRZESUNIECIE: ", "amount: $dragAmount total: $totalDrag")
                            },
                            onDragEnd = {
                                if (totalDrag < -25) {
                                    var catIndex = categories.indexOf(currentCategory)
                                    catIndex += 1
                                    if (catIndex > categories.size - 1) {
                                        catIndex = 0
                                    }
                                    currentCategory = categories[catIndex]
                                } else if (totalDrag > 25) {
                                    var catIndex = categories.indexOf(currentCategory)
                                    catIndex -= 1
                                    if (catIndex < 0) {
                                        catIndex = categories.size - 1
                                    }
                                    currentCategory = categories[catIndex]
                                }
                                totalDrag = 0f
                            }
                        )
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExerciseList(ExerciseManager.exercises, currentCategory)
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)

                //.background(Color.LightGray)
                .padding(0.dp, 2.dp, 0.dp, 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = { showNickameDialog.value = true },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons8_male_user_100),
                    contentDescription = "User Icon"
                )
            }
            IconButton(
                onClick = { LaunchTrainingPlanIntent(
                    mainActivityViewModel.context
                ) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar),
                    contentDescription = "User Icon"
                )
            }
            IconButton(
                onClick = { showHourDialog.value = true },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.meds),
                    contentDescription = "User Icon"
                )
            }
        }

        if (showNickameDialog.value) {
            ChangeNickDialog { showNickameDialog.value = false }
        }
        if (showHourDialog.value) {
            HourPicker(
                onConfirm = { showHourDialog.value = false },
                onDismiss = { showHourDialog.value = false }
            )
        }
    }
}

@Composable
fun WelcomeCard(userName: String) {
    val randomText = UserManager.wellcomeMessage
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(350.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),

            ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "$randomText $userName!",
                    fontSize = 24.sp
                )
                Text(
                    text = "Let's get started!",
                    fontSize = 16.sp
                )
            }
        }
    }
}
@Composable
fun AddCustomExercisePanel(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                val intent =
                    Intent(mainActivityViewModel.context, AddCustomExerciseView::class.java)
                mainActivityViewModel.context.startActivity(intent)
            },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(350.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
            ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Click to add exercise",
                    fontSize = 24.sp
                )
            }
        }
    }
}
@Composable
fun ExerciseList(exercises: List<ExerciseClass>, currentCategory: String) {
    val userName = UserManager.getUserName()
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                WelcomeCard(userName)
            }
            if(currentCategory == "All") {
                item {
                    AddCustomExercisePanel()
                }
            }
            items(exercises) { exercise ->
                if (currentCategory == "All" || exercise.category.toString() == currentCategory) {
                    ExerciseCard(exercise, exercises.indexOf(exercise))
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: ExerciseClass, index: Int) {
    val context = LocalContext.current
    val photoId = exercise.getPhotoResourceId(context)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(350.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        LaunchExerciseIntent(index, context)
                    }
            ) {
                Row {
                    Column(
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text(
                            text = exercise.name, fontSize = 24.sp
                        )
                        Text(
                            text = "Category: ${exercise.categoryString}", fontSize = 16.sp
                        )
                    }
                    if(exercise.isCustom) {
                        Image(
                            bitmap = exercise.getImage(),
                            contentDescription = "${exercise.name} image",
                            contentScale = ContentScale.Crop,
                                    //colorFilter = ColorFilter.tint(colorResource(id = R.color.images)),
                            modifier = Modifier
                                .padding(8.dp)
                                .size(100.dp)
                                .clip(RoundedCornerShape(percent = 10))
                        )
                    }
                    else{
                        Image(
                            painter = painterResource(id = photoId),
                            contentDescription = "${exercise.name} image",
                            colorFilter = ColorFilter.tint(colorResource(id = R.color.images)),
                            modifier = Modifier
                                .padding(8.dp)
                                .size(100.dp)
                        )
                    }
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

fun LaunchUserIntent(context: Context) {
    val intent = Intent(context, UserActivity::class.java)
    context.startActivity(intent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HourPicker(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )
    Dialog(onDismissRequest = { onDismiss() }) {
        Card {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Set supplements notification",
                    modifier = Modifier.padding(4.dp)
                )
                TimeInput(
                    state = timePickerState,
                )
                Button(onClick = {
                    val previousAlarmItem = AlarmItem(
                        LocalDateTime.of(
                            currentTime.get(Calendar.YEAR),
                            currentTime.get(Calendar.MONTH) + 1,
                            currentTime.get(Calendar.DAY_OF_MONTH),
                            timePickerState.hour,
                            timePickerState.minute
                        ),
                        "Time to take your supplements!"
                    )
                    alarmScheduler.cancelAlarm(previousAlarmItem)

                    // Schedule the new alarm
                    val newAlarmItem = AlarmItem(
                        LocalDateTime.of(
                            currentTime.get(Calendar.YEAR),
                            currentTime.get(Calendar.MONTH) + 1,
                            currentTime.get(Calendar.DAY_OF_MONTH),
                            timePickerState.hour,
                            timePickerState.minute
                        ),
                        "Time to take your supplements!"
                    )
                    alarmScheduler.scheduleAlarm(newAlarmItem)
                    onConfirm()
                }
                , colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) {
                    Text("Confirm"
                    , fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun LaunchTrainingPlanIntent(context: Context) {
    val intent = Intent(context, TrainingPlannerActivity::class.java)
    context.startActivity(intent)
}

@Composable
fun HideStatusBar() {
    val systemUiController = rememberSystemUiController()

    systemUiController.isStatusBarVisible = false
    systemUiController.isNavigationBarVisible = false
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeNickDialog(onDismissRequest: () -> Unit) {
    val newNick = remember { mutableStateOf("") }
    val context = LocalContext.current

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Change Nickname",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = newNick.value,
                    onValueChange = { newNick.value = it },
                    label = { Text("New Nickname") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true
                    , colors = TextFieldDefaults.textFieldColors(
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
                        disabledPlaceholderColor =MaterialTheme.colorScheme.inversePrimary
                    )
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            UserManager.changeUserNick(context, newNick.value)
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    ) {
                        Text(text = "Save",
                            fontWeight = FontWeight.Bold)
                    }
                    Button(onClick = { onDismissRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(text = "Cancel",
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

