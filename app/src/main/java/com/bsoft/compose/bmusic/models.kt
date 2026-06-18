package com.bsoft.compose.bmusic

import android.net.Uri

data class Song(
    val id: Long,
    val displayName: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val contentUri: Uri
)

data class SongsState(val songs: List<Song> = emptyList())