package com.example.gymtracker

import ExerciseClass
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gymtracker.ui.theme.GymTrackerTheme

val userData = UserData()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTrackerTheme {
                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp, 16.dp, 4.dp, 0.dp)) { innerPadding ->
                    MainView(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

fun LoadExercises() : List<ExerciseClass> {
    val exerciseList = listOf(
        ExerciseClass("Chest fly",CategoriesEnum.CHEST),
        ExerciseClass("Leg curl",CategoriesEnum.NONE),
        ExerciseClass("Leg press",CategoriesEnum.NONE),
        ExerciseClass("Dumbbell biceps",CategoriesEnum.NONE),
        ExerciseClass("Bench press",CategoriesEnum.CHEST),
        ExerciseClass("Seated barbell press",CategoriesEnum.SHOULDERS)
    )
    return exerciseList
}
fun LoadCategories(exercises: List<ExerciseClass>) : List<String> {
    val categories = mutableListOf<String>()
    /*
    for (exercise in exercises) {
        if(!categories.contains(exercise.category)) {
            categories.add(exercise.category.toString())
        }
    }
    */
    return categories
}
@Composable
fun MainView(modifier: Modifier = Modifier) {
    val exerciseList = LoadExercises()
    val categories = LoadCategories(exerciseList)
    val context  = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {}) {
                Text(text = "Account")
            }
            Text(text = context.getString(userData.userNick))
            Button(onClick = {}) {
                Text(text = "Settings")
            }
        }
         //DropdownMenu() { }
        /*
            Drop down menu z możliwością przefiltrowania kategorii ćwiczeń w aplikacji
            tak aby wszystkie na raz nie były pokazane na ekranie itp itd
        */
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .horizontalScroll(rememberScrollState())) {
            ExerciseList(exerciseList)
        }
    }
}


@Composable
fun ExerciseCard(exercise: ExerciseClass) {
    val context = LocalContext.current
    val photoId = exercise.getPhotoResourceId(context)
    Card(
        modifier = Modifier.padding(8.dp)
            .fillMaxWidth())
    {
        Column(modifier = Modifier
            .padding(16.dp)
            .width(300.dp)) {
            Row() {
                Column {
                    Text(text = exercise.name)
                    Text(text = "Category: ${exercise.category}")
                    Button(onClick = {
                        LaunchExerciseIntent(exercise,context)
                    }) {
                        Text(text = "Choose")
                    }
                }
                if (photoId != 0) {
                    Image(
                        painter = painterResource(id = photoId),
                        contentDescription = "${exercise.name} image",
                        //contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = "Image not found")
                }
            }
        }
    }
}

@Composable
fun ExerciseList(exercises: List<ExerciseClass>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(exercises) { exercise ->
            ExerciseCard(exercise = exercise)
        }
    }
}


fun LaunchExerciseIntent(exercise: ExerciseClass, context: Context) {
    val intent = Intent(context, ExerciseView::class.java).apply {
        putExtra("EXERCISE", exercise)
    }
    context.startActivity(intent)
}