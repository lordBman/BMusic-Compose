package com.bsoft.compose.bmusic.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun SettingsScreen(modifier: Modifier = Modifier){
    Scaffold(
        topBar = {

        }
    ) {
        Surface(modifier = modifier.padding(it)) {
            Text("Settings Screen")
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview(){
    BMusicTheme {
        SettingsScreen()
    }
}