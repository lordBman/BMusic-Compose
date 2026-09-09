package com.bsoft.compose.bmusic.data.states

data class PlayingState(
    val position: Long = 0,
    val playing: Boolean = false,
    val favourites: List<Long> = emptyList(),
)