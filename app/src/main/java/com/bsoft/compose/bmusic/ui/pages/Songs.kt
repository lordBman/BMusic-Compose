package com.bsoft.compose.bmusic.ui.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.ui.components.AlphabeticList
import com.bsoft.compose.bmusic.ui.components.SongView
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun SongsPage(modifier: Modifier = Modifier, songs: List<Song> = emptyList(), chosen: (index: Int)-> Unit) {
    AlphabeticList(items = songs, header = { it.title.first() }) { index, item ->
        SongView(song = item) {
            chosen(index)
        }
    }
}

@Preview
@Composable
fun SongsPagePreview(){
    val songs = (0..20).map {
        Song(id = 0, displayName = "Display Name", title = "Song Title", artist = "Artist name", album = "Album Name", duration = 5000)
    }

    BMusicTheme {
        Surface {
            SongsPage(songs = songs){}
        }
    }
}