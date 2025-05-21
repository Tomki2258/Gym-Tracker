// MainActivity.kt
package com.example.gymtracker.views


import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymtracker.ui.theme.Black
import com.example.gymtracker.ui.theme.BlackDark
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.log


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

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
                ) {
                    MainViewPreview()
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

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    MainView()
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MainView() {
    //HideStatusBar()
    mainActivityViewModel = MainActivityViewModel(LocalContext.current)

    val userDialog by mainActivityViewModel.showNicknameDialogState.collectAsState()

    for (ex in ExerciseManager.exercises) {
        ex.loadImage(mainActivityViewModel.context)
    }

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
                TopBar()
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
                            },
                            onDragEnd = {
                                var catIndex =
                                    mainActivityViewModel.categories.indexOf(mainActivityViewModel.currentCategoryState.value)
                                if (totalDrag < -25) {
                                    catIndex += 1
                                    if (catIndex > mainActivityViewModel.categories.size - 1) {
                                        catIndex = 0
                                    }
                                } else if (totalDrag > 25) {
                                    catIndex -= 1
                                    if (catIndex < 0) {
                                        catIndex = mainActivityViewModel.categories.size - 1
                                    }
                                }
                                mainActivityViewModel.updateCat(mainActivityViewModel.categories[catIndex])
                                totalDrag = 0f
                            }
                        )
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExerciseList(ExerciseManager.exercises)
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)

                .padding(0.dp, 2.dp, 0.dp, 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = { mainActivityViewModel.updateShowNickameDialog(true) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons8_male_user_100),
                    contentDescription = "User Icon"
                )
            }
            IconButton(
                onClick = {
                    mainActivityViewModel.LaunchTrainingPlanIntent()
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar),
                    contentDescription = "User Icon"
                )
            }
        }

        if (userDialog) {
            ChangeNickDialog { mainActivityViewModel.updateShowNickameDialog(false) }

        }
        if (mainActivityViewModel.showHourDialogState.value) {
            HourPicker(
                onConfirm = { mainActivityViewModel.updateShowHourDialog(false) },
                onDismiss = { mainActivityViewModel.updateShowHourDialog(false) }
            )
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TopBar() {
    LazyRow(
        modifier = Modifier.padding(25.dp, 0.dp, 25.dp, 0.dp)
    ) {
        item {
            Text(
                modifier = Modifier
                    .padding(5.dp)
                    .clickable() {
                        mainActivityViewModel.toggleSearch()
                    },
                text = "Search".uppercase()
            )
        }
        item {
            Text(
                modifier = Modifier
                    .padding(5.dp)
                    .clickable() {
                        mainActivityViewModel.launchCustomExericseIntent()
                    },
                text = "Add custom".uppercase()
            )
        }
        items(mainActivityViewModel.getCat()) { category ->
            Text(
                modifier = Modifier
                    .padding(5.dp)
                    .clickable {
                        mainActivityViewModel.updateCat(category)
                    }
                    .drawBehind {
                        val circleColor = Black
                        drawCircle(
                            color = circleColor,
                            radius = 65f
                        )
                    },
                text = category
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


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ExerciseList(exercises: List<ExerciseClass>) {
    val search by mainActivityViewModel.searchNameState.collectAsState()

    val currentCategory by mainActivityViewModel.currentCategoryState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                if (mainActivityViewModel.searchEnabled.value == true) {
                    SearchCard()
                } else {
                    WelcomeCard(UserManager.getUserName())
                }
                Log.d("Search", mainActivityViewModel.searchEnabled.value.toString())
            }
//            item {
//                SearchCard()
//            }
//            if (currentCategory == "All") {
//                item {
//                    AddCustomExercisePanel()
//                }
//            }
            val filtered = exercises.filter {
                it.name.contains(search, ignoreCase = true)
            }
            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .padding(25.dp)
                    ) {
                        Text(text = "Nothing is here :(")
                    }
                }
            } else {
                items(filtered) { exercise ->
                    if (currentCategory == "All" || exercise.category.toString() == currentCategory) {
                        ExerciseCard(exercise, exercises.indexOf(exercise))
                    }
                }
            }
        }
    }
}

@Composable
fun SearchCard() {
    val search by mainActivityViewModel.searchNameState.collectAsState()

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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = search,
                    onValueChange = { mainActivityViewModel.updateSearch(it) },
                    label = { Text("Search for exercise") }
                )
                IconButton(onClick = {
                    mainActivityViewModel.searchEnabled.value = false
                }) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = "User Icon",
                        Modifier.size(20.dp)
                    )
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
                    if (exercise.isCustom) {
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
                    } else {
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
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) {
                    Text(
                        "Confirm", fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
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
                    singleLine = true, colors = TextFieldDefaults.textFieldColors(
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
                        Text(
                            text = "Save",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = { onDismissRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(
                            text = "Cancel",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

