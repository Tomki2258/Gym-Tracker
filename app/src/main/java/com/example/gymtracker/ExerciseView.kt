package com.example.gymtracker

import ExerciseClass
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymtracker.ui.theme.GymTrackerTheme


class ExerciseView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTrackerTheme {
                val exercise = intent.getSerializableExtra("EXERCISE") as ExerciseClass
                ExerciseIntent(modifier = Modifier.fillMaxSize(), exerciseClass = exercise)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ExerciseIntent(modifier: Modifier = Modifier,
                   exerciseClass: ExerciseClass = ExerciseClass("Default Name", "Default Category")) {
    Column(modifier = modifier.fillMaxSize()) {
        Image(painter = painterResource(id = exerciseClass.getPhotoResourceId(LocalContext.current)),
            contentDescription = "Exercise Image")
        Text(text = exerciseClass.name)
        Text(text = exerciseClass.category)
        Button(
            onClick = {  },
            modifier = Modifier.fillMaxSize()
        ) { }
    }
}