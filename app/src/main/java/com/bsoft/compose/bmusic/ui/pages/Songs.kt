package com.bsoft.compose.bmusic.ui.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun SongsPage(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(50) { index ->
            Text(text = "contentName Item $index", modifier = Modifier.padding(16.dp))
        }
    }
}

@Preview
@Composable
fun SongsPagePreview(){
    BMusicTheme {
        Surface {
            SongsPage()
        }
    }
}