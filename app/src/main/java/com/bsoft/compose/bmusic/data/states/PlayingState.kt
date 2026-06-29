package com.bsoft.compose.bmusic.data.states

import com.bsoft.compose.bmusic.data.models.Song

data class PlayingState(
    val current: Song? = null,
    val playing: Boolean = false,
    val position: Long = 0,
    val playlist: List<Song> = emptyList(),
    val favourites: List<Long> = emptyList(),
    val repeatMode: RepeatMode = RepeatMode.All,
    val shuffle: Boolean = false
){
    enum class RepeatMode{
        Disabled, All, Single
    }
}