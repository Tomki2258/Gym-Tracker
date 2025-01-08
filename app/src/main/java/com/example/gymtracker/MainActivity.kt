// MainActivity.kt
package com.example.gymtracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import com.example.gymtracker.ui.theme.GymTrackerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExerciseManager.initialize(this)
        UserManager.initialize(this)
        enableEdgeToEdge()
        setContent {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    //.padding(4.dp, 16.dp, 4.dp, 0.dp)
            ) { innerPadding ->
                MainView(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
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
        if (!categories.contains(exercise.category)) {
            categories.add(exercise.category.toString())
        }
    }

    return categories
}

@Preview(showBackground = true)
@Composable
fun MainView(modifier: Modifier = Modifier) {
    //val exerciseList = LoadExercises()
    val categories = LoadCategories(ExerciseManager.exercises)
    var currentCategory by remember { mutableStateOf(categories.first()) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(Color.LightGray)
                .padding(0.dp, 16.dp, 0.dp, 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = {
                LaunchUserIntent(context)
            }) {
                Icon(
                    painter = painterResource(R.drawable.icons8_male_user_100),
                    contentDescription = "User Icon"
                )
            }
            Text(
                modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
                text = UserManager.userData.userNick)
            IconButton(onClick = { /* Handle click */ }) {
                Icon(
                    painter = painterResource(R.drawable.icons8_settings_500),
                    contentDescription = "User Icon"
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(Color.LightGray)
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
                Log.d(currentCategory, currentCategory)
            }) {
                Icon(
                    modifier = Modifier.size(25.dp)
                        .scale(scaleX = -1f, scaleY = 1f),
                    painter = painterResource(R.drawable.right_arrow),
                    contentDescription = "Filter Icon"
                )
                //Text(text = "<-")
            }
            var catIndex = categories.indexOf(currentCategory)
            Text(text = "${currentCategory}\n${catIndex + 1} / ${categories.size}",
                textAlign = TextAlign.Center)
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
                //Text(text = "->")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState())
                .padding(0.dp, 0.dp, 0.dp, 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExerciseList(ExerciseManager.exercises, currentCategory)
        }
    }
}

@Composable
fun ExerciseList(exercises: List<ExerciseClass>, currentCategory: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(exercises) { exercise ->
                if (currentCategory == "All" || exercise.category == currentCategory) {
                    ExerciseCard(exercise, exercises.indexOf(exercise))
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: ExerciseClass,index: Int) {
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
                .width(350.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row {
                    Column {
                        Text(text = exercise.name)
                        Text(text = "Category: ${exercise.category}")
                        Button(onClick = {
                            LaunchExerciseIntent(index, context)
                        }) {
                            Text(text = "Choose")
                        }
                    }
                    if (photoId != 0) {
                        Image(
                            painter = painterResource(id = photoId),
                            contentDescription = "${exercise.name} image"
                        )
                    } else {
                        Text(text = "Image not found")
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