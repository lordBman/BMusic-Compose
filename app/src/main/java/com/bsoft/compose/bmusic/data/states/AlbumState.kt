package com.bsoft.compose.bmusic.data.states

import android.graphics.Bitmap
import com.bsoft.compose.bmusic.data.models.Album
import com.bsoft.compose.bmusic.data.models.Artist
import com.bsoft.compose.bmusic.data.models.Song

data class AlbumState(
    val album: Album? = null,
    val songs: List<Song> = emptyList(),
    val bitmap: Bitmap? = null,
    val message: String = "Loading Album data.., please wait",
    val loaded: Boolean = false,
    val error: String? = null)
