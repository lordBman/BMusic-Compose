package com.bsoft.compose.bmusic

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

enum class HomeDestination(val route: String, val label: String, val icon: Int, val selectedIcon: Int) {
    SONGS("songs", "Songs", R.drawable.glyphs__music_list_duo, R.drawable.glyphs__music_list_bold),
    ALBUM("album", "Albums", R.drawable.glyphs__album_duo, R.drawable.glyphs__album_bold),
    Artists("artists", "Artists", R.drawable.glyphs__users_duo, R.drawable.glyphs__users_bold),
    PLAYLISTS("playlists", "Playlists", R.drawable.glyphs__grid_add_duo, R.drawable.glyphs__grid_add_bold)
}

@Serializable
sealed interface Route: NavKey {
    @Serializable
    data object Home : Route

    @Serializable
    data object Settings: Route

    @Serializable
    data object Search: Route

    @Serializable
    data object Playlist: Route

    @Serializable
    data object Album: Route

    @Serializable
    data object Artist: Route
}