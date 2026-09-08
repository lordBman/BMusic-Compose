package com.bsoft.compose.bmusic.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsoft.compose.bmusic.data.models.Album
import com.bsoft.compose.bmusic.ui.components.AlbumView
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun AlbumPage(modifier: Modifier = Modifier, albums: List<Album>, chosen: (album: Album)-> Unit) {
    LazyVerticalGrid(modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        columns = GridCells.Fixed(count = 2),
        contentPadding = PaddingValues(10.dp)) {
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