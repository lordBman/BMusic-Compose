package com.bsoft.compose.bmusic.utils

import androidx.navigation3.runtime.NavKey
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.ui.pages.PlaylistsOptions
import kotlinx.serialization.Serializable

enum class HomeDestination(val route: String, val label: String, val icon: Int, val selectedIcon: Int) {
    PLAYLISTS("playlists", "Playlists", R.drawable.glyphs__grid, R.drawable.glyphs__grid_bold),
    SONGS("songs", "Songs", R.drawable.glyphs__music, R.drawable.glyphs__music_bold),
    ALBUM("album", "Albums", R.drawable.glyphs__album, R.drawable.glyphs__album_bold),
    Artists("artists", "Artists", R.drawable.glyphs__users, R.drawable.glyphs__users_bold),
}

@Serializable
sealed interface Route: NavKey {
    @Serializable
    data object Home : Route

    @Serializable
    data object Settings: Route

    @Serializable
    data class Playlist(val playlistsOptions: PlaylistsOptions?, val playlist: Long?): Route

    @Serializable
    data class AddSongs(val playlist: Long, val title: String): Route

    @Serializable
    data class Album(val id: Long): Route

    @Serializable
    data class Artist(val id: Long): Route

    @Serializable
    data object Playing: Route
}