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

data class Album(val id: Long, val name: String, val artist: String, val songCount: Int)
data class Artist(val name: String, val albumCount: Int, val songCount: Int)

data class SongsState(
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val message: String = "",
    val loaded: Boolean = false,
    val error: String? = null)

enum class RepeatMode{
    Disabled, All, Single
}

data class PlayingState(
    val current: Song? = null,
    val position: Long = 0,
    val playlist: List<Song> = emptyList(),
    val favourites: List<Long> = emptyList(),
    val repeatMode: RepeatMode = RepeatMode.All,
    val shuffle: Boolean = false
)