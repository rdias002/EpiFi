package com.example.epifi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.epifi.ui.theme.EpiFiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EpiFiTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val ctx = LocalContext.current
                        Button(onClick = {
                            ctx.startActivity(Intent(ctx,ComposeActivity::class.java))
                        }) {
                            Text("Compose Activity")
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(onClick = {
                            ctx.startActivity(Intent(ctx,XmlActivity::class.java))
                        }) {
                            Text("XML Activity")
                        }
                    }
                }
            }
        }
    }
}
