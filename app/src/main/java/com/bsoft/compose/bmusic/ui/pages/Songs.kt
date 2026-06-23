package com.bsoft.compose.bmusic.ui.pages

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bsoft.compose.bmusic.data.Song
import com.bsoft.compose.bmusic.ui.components.SongView
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun SongsPage(modifier: Modifier = Modifier, songs: List<Song> = emptyList(), chosen: (index: Int, song: Song)-> Unit) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(songs.size) { index ->
            val song = songs[index]
            SongView(song = song){
                chosen(index,song)
            }
        }
    }
}

@Preview
@Composable
fun SongsPagePreview(){
    val songs = (0..20).map {
        Song(id = 0, displayName = "Display Name", title = "Song Title", artist = "Artist name", album = "Album Name", duration = 5000, contentUri = Uri.EMPTY)
    }

    BMusicTheme {
        Surface {
            SongsPage(songs = songs){ index, song ->  }
        }
    }
}