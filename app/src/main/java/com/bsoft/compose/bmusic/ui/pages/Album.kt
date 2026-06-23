package com.bsoft.compose.bmusic.ui.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bsoft.compose.bmusic.data.Album
import com.bsoft.compose.bmusic.data.Song
import com.bsoft.compose.bmusic.ui.components.AlbumView
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun AlbumPage(modifier: Modifier = Modifier, albums: List<Album>, chosen: (album: Album)-> Unit) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(count = albums.size) { index ->
            AlbumView(album = albums[index]){
                chosen(albums[index])
            }
        }
    }
}

@Preview
@Composable
fun AlbumPagePreview(){
    val albums = (0..20).map {
        Album(id = 0, name = "Album Name", artist = "Artist name", songCount = 5)
    }
    BMusicTheme {
        Surface {
            AlbumPage(albums = albums){}
        }
    }
}