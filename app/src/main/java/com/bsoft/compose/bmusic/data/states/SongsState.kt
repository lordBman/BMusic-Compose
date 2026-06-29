package com.bsoft.compose.bmusic.data.states

import com.bsoft.compose.bmusic.data.models.Album
import com.bsoft.compose.bmusic.data.models.Artist
import com.bsoft.compose.bmusic.data.models.Song

data class SongsState(
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val message: String = "",
    val loaded: Boolean = false,
    val error: String? = null)