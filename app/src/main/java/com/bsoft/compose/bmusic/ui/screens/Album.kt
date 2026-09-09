package com.bsoft.compose.bmusic.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.models.Album
import com.bsoft.compose.bmusic.ui.components.AlphabeticList
import com.bsoft.compose.bmusic.ui.components.BitmapImage
import com.bsoft.compose.bmusic.ui.components.ImageBackgroundTopAppBar
import com.bsoft.compose.bmusic.ui.components.QueueSongView
import com.bsoft.compose.bmusic.ui.components.SongView
import com.bsoft.compose.bmusic.ui.components.TransparentStatusBarHandler
import com.bsoft.compose.bmusic.ui.components.TransparentTopBar
import com.bsoft.compose.bmusic.utils.toTimeFormat
import com.bsoft.compose.bmusic.viewmodels.SongsViewModel

@Composable
fun AlbumScreen(
    modifier: Modifier = Modifier, id: Long, viewModel: SongsViewModel,
    play: (Album, Int)-> Unit, playAll: (MediaItem, Boolean)-> Unit, back: ()-> Unit
){
    TransparentStatusBarHandler()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val albumState by viewModel.albumState.collectAsStateWithLifecycle()
    LaunchedEffect(id) {
        viewModel.queryAlbum(id)
    }

    val duration by remember { derivedStateOf { albumState.songs.sumOf { it.duration }.toTimeFormat() } }

    if(albumState.bitmap == null){
        Image(modifier = Modifier.fillMaxSize().blur(radius = 16.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle), painter = painterResource(id = R.drawable.album_bg), contentScale = ContentScale.Crop, contentDescription = null)
    }else{
        BitmapImage(modifier = Modifier.fillMaxSize().blur(radius = 16.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle), bitmap = albumState.bitmap as Bitmap, contentScale = ContentScale.Crop)
    }
    Scaffold(modifier = modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Black.copy(alpha = 0.2f),
        topBar = {
            TransparentTopBar(
                title = albumState.album?.name ?: "Unknown",
                details = listOf(albumState.album?.artist ?: "unknown", "${albumState.songs.size} songs - $duration"),
                scrollBehavior = scrollBehavior,
                playAll = {
                    albumState.album?.let { album ->
                        playAll(album.toMediaItem(0), false)
                    }
                },
                shuffle = {
                    albumState.album?.let { album ->
                        playAll(album.toMediaItem(0), true)
                    }
                },
                bitmap = albumState.bitmap, bg = R.drawable.album_bg , back = back)
        }
    ) {
        Surface(modifier = modifier.padding(it), color = Color.Transparent) {
            AlphabeticList(items = albumState.songs, header = { song -> song.title.first() }) { index, item ->
                QueueSongView(song = item) {
                    albumState.album?.let { album ->
                        play(album, index)
                    }
                }
            }
        }
    }
}