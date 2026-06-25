package com.bsoft.compose.bmusic.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import com.bsoft.compose.bmusic.viewmodels.SongsViewModel

@Composable
fun PlaylistScreen(modifier: Modifier = Modifier, viewModel: SongsViewModel = viewModel(),){
    Box(modifier = modifier, contentAlignment = Alignment.Center){
        Text("Playlist Screen")
    }
}

@Preview
@Composable
private fun PlaylistScreenPreview(){
    BMusicTheme {
        PlaylistScreen()
    }
}