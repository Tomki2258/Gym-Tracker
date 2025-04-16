// UserActivity.kt
package com.example.gymtracker.views

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.R
import com.example.gymtracker.managers.UserManager

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //UserManager.initialize(this)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                DefaultPreview(modifier = Modifier.padding(paddingValues))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview(modifier: Modifier = Modifier) {
    Greeting(name = UserManager.userData.userNick, modifier = modifier)
}

fun SetSumplementsHour(hour: Int, minute: Int) {
    //TODO
    Log.d("Hour", hour.toString())
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HourPicker(
//    onConfirm: () -> Unit,
//    onDismiss: () -> Unit,
//) {
//    val currentTime = Calendar.getInstance()
//
//    val timePickerState = rememberTimePickerState(
//        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
//        initialMinute = currentTime.get(Calendar.MINUTE),
//        is24Hour = true,
//    )
//    Dialog(onDismissRequest = { onDismiss() }) {
//        Card(
//        ) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.padding(8.dp)
//            ) {
//                TimeInput(
//                    state = timePickerState,
//                )
//                Button(onClick = {
//                    onConfirm
//                    SetSumplementsHour(timePickerState.hour, timePickerState.minute)
//                }) {
//                    Text("Confirm")
//                }
//            }
//        }
//    }
//
//}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val showNickameDialog = remember { mutableStateOf(false) }
    val showHourDialog = remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = {
        }, modifier = Modifier.size(100.dp)) {
            Icon(
                painter = painterResource(R.drawable.icons8_male_user_100),
                contentDescription = "User Icon",
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Column(
            modifier = Modifier.fillMaxWidth(0.5f)
                .padding(0.dp, 24.dp, 0.dp, 0.dp),
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showNickameDialog.value = true }) {
                Text(text = "Change Nickname")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {  }) {
                Text(text = "Training plan")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showHourDialog.value = true }) {
                Text(text = "Supplements")
            }
        }
    }

    if (showNickameDialog.value) {
        //ChangeNickDialog { showNickameDialog.value = false }
    }
    if (showHourDialog.value) {
        HourPicker(
            onConfirm = { showHourDialog.value = false },
            onDismiss = { showHourDialog.value = false }
        )
    }
}

//@Composable
//fun ChangeNickDialog(onDismissRequest: () -> Unit) {
//    val newNick = remember { mutableStateOf("") }
//    val context = LocalContext.current
//
//    Dialog(onDismissRequest = { onDismissRequest() }) {
//        Card {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.padding(16.dp)
//            ) {
//                Text(
//                    text = "Change Nickname",
//                    fontSize = 20.sp,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                )
//                TextField(
//                    value = newNick.value,
//                    onValueChange = { newNick.value = it },
//                    label = { Text("New Nickname") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 8.dp)
//                )
//                Row(
//                    horizontalArrangement = Arrangement.SpaceEvenly,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Button(onClick = {
//                        UserManager.changeUserNick(context, newNick.value)
//                        onDismissRequest()
//                    }) {
//                        Text(text = "Save")
//                    }
//                    Button(onClick = { onDismissRequest() }) {
//                        Text(text = "Cancel")
//                    }
//                }
//            }
//        }
//    }
//}
//fun LaunchTrainingPlanIntent(context: Context) {
//    val intent = Intent(context, TrainingPlannerActivity::class.java)
//    context.startActivity(intent)
//}