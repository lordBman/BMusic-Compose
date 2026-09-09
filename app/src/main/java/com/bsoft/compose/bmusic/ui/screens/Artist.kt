package com.bsoft.compose.bmusic.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.models.Album
import com.bsoft.compose.bmusic.data.models.Artist
import com.bsoft.compose.bmusic.ui.components.AlbumView
import com.bsoft.compose.bmusic.ui.components.BitmapImage
import com.bsoft.compose.bmusic.ui.components.QueueSongView
import com.bsoft.compose.bmusic.ui.components.TransparentStatusBarHandler
import com.bsoft.compose.bmusic.ui.components.TransparentTopBar
import com.bsoft.compose.bmusic.utils.toTimeFormat
import com.bsoft.compose.bmusic.viewmodels.SongsViewModel

@Composable
fun ArtistScreen(modifier: Modifier = Modifier, id: Long, viewModel: SongsViewModel, play: (Artist, Int)-> Unit, playAll: (MediaItem, Boolean)-> Unit, toAlbum: (Album)-> Unit, back: ()-> Unit){
    TransparentStatusBarHandler()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val artistState by viewModel.artistState.collectAsStateWithLifecycle()
    LaunchedEffect(id) {
        viewModel.queryArtist(id)
    }

    val duration by remember { derivedStateOf { artistState.songs.sumOf { it.duration }.toTimeFormat() } }

    if(artistState.bitmap == null){
        Image(modifier = Modifier.fillMaxSize().blur(radius = 16.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle), painter = painterResource(id = R.drawable.artist_bg), contentScale = ContentScale.Crop, contentDescription = null)
    }else{
        BitmapImage(modifier = Modifier.fillMaxSize().blur(radius = 16.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle), bitmap = artistState.bitmap as Bitmap, contentScale = ContentScale.Crop)
    }

    Scaffold(modifier = modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Black.copy(alpha = 0.2f),
        topBar = {
            TransparentTopBar(
                title = artistState.artist?.name ?: "Unknown",
                details = listOf("${artistState.songs.size} songs - $duration"),
                scrollBehavior = scrollBehavior,
                playAll = {
                    artistState.artist?.let { artist ->
                        playAll(artist.toMediaItem(0), false)
                    }
                },
                shuffle = {
                    artistState.artist?.let { artist ->
                        playAll(artist.toMediaItem(0), true)
                    }
                },
                bitmap = artistState.bitmap, bg = R.drawable.artist_bg , back = back)
        }
    ) { padding ->
        Surface(modifier = modifier.padding(padding),  color = Color.Transparent) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(artistState.songs.size + 1) { index ->
                    if(index == 0){
                        LazyRow(contentPadding = PaddingValues(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)){
                            items(artistState.albums){ album ->
                                AlbumView(modifier = Modifier.width(200.dp), album = album){
                                    toAlbum(album)
                                }
                            }
                        }
                    }else{
                        val song = artistState.songs[index - 1]
                        QueueSongView(song = song) {
                            artistState.artist?.let { artist ->
                                play(artist, index)
                            }
                        }
                    }
                }
            }
        }
    }
}