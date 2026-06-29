package com.bsoft.compose.bmusic.ui.screens

import android.graphics.Bitmap
import android.util.Size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.models.Album
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.data.states.SongsState

import com.bsoft.compose.bmusic.ui.components.ImageBackgroundTopAppBar
import com.bsoft.compose.bmusic.ui.components.SongView
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import com.bsoft.compose.bmusic.utils.Util

@Composable
fun AlbumScreen(modifier: Modifier = Modifier, id: Long, state: SongsState, play: (Album, Int)-> Unit, back: ()-> Unit){
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

    var songs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var album by remember { mutableStateOf<Album?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(id) {
        album = state.albums.find { it.id == id }
        album?.let {
            songs = state.songs.filter { song -> song.album == it.name }
            bitmap = Util.loadArtwork(context, it.artworkUri, Size(300, 300))
        }
    }

    Scaffold(modifier = modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ImageBackgroundTopAppBar(
                title = album?.name ?: "Unknown",
                image = R.drawable.album_bg,
                bitmap = bitmap,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { back() }) {
                        Icon(imageVector = ImageVector.vectorResource(R.drawable.material_symbols__arrow_back_ios_new_rounded),
                            contentDescription = "")
                    }
                }
            )
        }
    ) {
        Surface(modifier = modifier.padding(it)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(songs.size) { index ->
                    val song = songs[index]
                    SongView(song = song){
                        album?.let { album ->
                            play(album, index)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AlbumScreenPreview(){
    val songs = (0..20).map {
        Song(id = 0, displayName = "Display Name", title = "Song Title", artist = "Artist name", album = "Album Name", duration = 5000)
    }

    val albums = listOf(Album(id = 0, name = "Album Name", artist = "Artist name", songCount = 20))
    BMusicTheme {
        AlbumScreen(id = 0, state = SongsState(songs = songs, albums = albums), play = { _, _ -> }){

        }
    }
}