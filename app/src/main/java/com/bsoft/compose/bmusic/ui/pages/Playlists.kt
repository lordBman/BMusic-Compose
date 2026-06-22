package com.bsoft.compose.bmusic.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun PlaylistPage(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Playlist Screen")
    }
}

@Preview
@Composable
fun PlaylistPagePreview(){
    BMusicTheme {
        Surface {
            PlaylistPage()
        }
    }
}