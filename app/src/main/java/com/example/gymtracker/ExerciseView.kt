package com.example.gymtracker


import ExerciseClass
import ToastManager
import android.os.Bundle
import android.util.Log
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gymtracker.ui.theme.GymTrackerTheme


var exercise = ExerciseClass("Default Name", "Default Category")

class ExerciseView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTrackerTheme {
                exercise = intent.getSerializableExtra("EXERCISE") as ExerciseClass
                ExerciseIntent(modifier = Modifier.fillMaxSize(), exerciseClass = exercise)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ExerciseIntent(
    modifier: Modifier = Modifier,
    exerciseClass: ExerciseClass = ExerciseClass("Default Name", "Default Category")
) {
    val showDialog = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column() {
            Image(
                painter = painterResource(id = exerciseClass.getPhotoResourceId(LocalContext.current)),
                contentDescription = "Exercise Image"
            )
            Text(text = exerciseClass.name)
            Text(text = exerciseClass.category)
            Button(onClick = { showDialog.value = true }) {
                Text(text = "Add measurement")
            }
        }
        // lista pomiarów
    }

    if (showDialog.value) {
        AddMeasurementDialog(onDismissRequest = { showDialog.value = false })
    }
}
fun AddMesurment(context: Context, reps: MutableIntState, weigh: MutableDoubleState): Int {
    exercise.measurementsList.add(MeasurementClass(reps.value, weigh.value))
    //Log.d("Measurement", "Reps: ${exercise.measurementsList[0].reps}, Weight: ${weigh.value}")
    ToastManager(context, "Measurement added")
    return 1
}

@Composable
fun AddMeasurementDialog(onDismissRequest: () -> Unit) {
    val reps = remember { mutableIntStateOf(0) }
    val weight = remember { mutableDoubleStateOf(0.0) }
    val context = LocalContext.current
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
                        onValueChange = { reps.value = it.toInt() },
                        label = { Text("Reps") }
                    )
                    Text(
                        text = " x ",
                        fontSize = 20.sp
                    )
                    TextField(
                        value = weight.value.toString(),
                        onValueChange = { weight.value = it.toDouble() },
                        label = { Text("Weight") }
                    )
                }
                Button(onClick = {
                    if(AddMesurment(context, reps, weight) == 1) {
                        onDismissRequest()
                    }
                }) {
                    Text(text = "Add measurement")
                }
            }
        }
    }
}