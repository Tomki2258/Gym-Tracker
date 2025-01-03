// UserActivity.kt
package com.example.gymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gymtracker.ui.theme.GymTrackerTheme
import kotlinx.coroutines.launch

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserManager.initialize(this)
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = {
            //LaunchUserIntent(context)
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
        Button(onClick = { showDialog.value = true} ) {
            Text(text = "Change Nickname")
        }
        Button(onClick = { TODO() }) {
            Text(text = "Training plan")
        }
        Button(onClick = { TODO() }) {
            Text(text = "Supplements")
        }
    }
    if (showDialog.value) {
        ChangeNickDialog { showDialog.value = false }
    }
}

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
                Text(text = "Change Nickname", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
                TextField(
                    value = newNick.value,
                    onValueChange = { newNick.value = it },
                    label = { Text("New Nickname") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        UserManager.changeUserNick(context, newNick.value)
                        onDismissRequest()
                    }) {
                        Text(text = "Save")
                    }
                    Button(onClick = { onDismissRequest() }) {
                        Text(text = "Cancel")
                    }
                }
            }
        }
    }
}