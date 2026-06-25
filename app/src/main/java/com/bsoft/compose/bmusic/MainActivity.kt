package com.bsoft.compose.bmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bsoft.compose.bmusic.ui.Main
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            BMusicTheme {
                Main()
            }
        }
    }
}