package com.bsoft.compose.bmusic.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun SettingsScreen(modifier: Modifier = Modifier){
    Box(modifier = modifier, contentAlignment = Alignment.Center){
        Text("Settings Screen")
    }
}

@Preview
@Composable
private fun SettingsScreenPreview(){
    BMusicTheme {
        SettingsScreen()
    }
}