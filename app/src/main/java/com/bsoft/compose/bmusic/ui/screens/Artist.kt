package com.bsoft.compose.bmusic.ui.screens

import android.graphics.Bitmap
import android.util.Size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.Route
import com.bsoft.compose.bmusic.data.models.Album
import com.bsoft.compose.bmusic.data.models.Artist
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.data.states.SongsState
import com.bsoft.compose.bmusic.ui.components.AlbumLargeView
import com.bsoft.compose.bmusic.ui.components.ImageBackgroundTopAppBar
import com.bsoft.compose.bmusic.ui.components.SongView
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import com.bsoft.compose.bmusic.utils.Util

@Composable
fun ArtistScreen(modifier: Modifier = Modifier, id: Long, state: SongsState, play: (Artist, Int)-> Unit, toAlbum: (Album)-> Unit, back: ()-> Unit){
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val context = LocalContext.current

    var songs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var albums by remember { mutableStateOf<List<Album>>(emptyList()) }
    var artist by remember { mutableStateOf<Artist?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(id) {
        artist = state.artists.find { it.id == id }
        artist?.let {
            songs = state.songs.filter { song -> song.artist == it.name }
            albums = state.albums.filter { album -> album.artist == it.name }
            bitmap = Util.loadArtwork(context, it.artworkUri, Size(300, 300))
        }
    }

    Scaffold(modifier = modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ImageBackgroundTopAppBar(
                title = artist?.name ?: "Unknown",
                image = R.drawable.artist_bg,
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
    ) { padding ->
        Surface(modifier = modifier.padding(padding)) {

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(songs.size + 1) { index ->
                    if(index == 0){
                        LazyRow(contentPadding = PaddingValues(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)){
                            items(albums.size){ albumIndex ->
                                AlbumLargeView(album = albums[albumIndex]){
                                    toAlbum(it)
                                }
                            }
                        }
                    }else{
                        val song = songs[index - 1]
                        SongView(song = song){
                            artist?.let { 
                                play(it, index)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ArtistScreenPreview(){
    val songs = (0..20).map {
        Song(id = it.toLong(), displayName = "Display Name", title = "Song Title", artist = "Artist name", album = "Album Name", duration = 5000)
    }

    val albums = (0..4).map {
        Album(id = it.toLong(), name = "Album Name", artist = "Artist name", songCount = songs.size)
    }

    val artist = (0..4).map {
        Artist(id = it.toLong(), name = "Artist Name", albumCount = albums.size, songCount = 20)
    }
    BMusicTheme {
        ArtistScreen(id = 0, state = SongsState(songs = songs, artists = artist, albums = albums), play = { _, _ -> }, toAlbum = {}){

        }
    }
}