package com.bsoft.compose.bmusic.data.states

import android.graphics.Bitmap
import com.bsoft.compose.bmusic.data.models.Album
import com.bsoft.compose.bmusic.data.models.Artist
import com.bsoft.compose.bmusic.data.models.Song

data class ArtistState(
    val artist: Artist? = null,
    val bitmap: Bitmap? = null,
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val message: String = "Loading Artist data.., please wait",
    val loaded: Boolean = false,
    val error: String? = null)
