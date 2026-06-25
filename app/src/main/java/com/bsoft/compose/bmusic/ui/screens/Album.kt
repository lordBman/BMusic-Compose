package com.bsoft.compose.bmusic.ui.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.Song
import com.bsoft.compose.bmusic.ui.components.ImageBackgroundTopAppBar
import com.bsoft.compose.bmusic.ui.components.SongView
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun AlbumScreen(modifier: Modifier = Modifier, back: ()-> Unit){
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val songs = (0..20).map {
        Song(id = 0, displayName = "Display Name", title = "Song Title", artist = "Artist name", album = "Album Name", duration = 5000)
    }

    Scaffold(modifier = modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ImageBackgroundTopAppBar(
                title = "Album Name",
                image = R.drawable.album_bg,
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

                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AlbumScreenPreview(){
    BMusicTheme {
        AlbumScreen{

        }
    }
}