package com.bsoft.compose.bmusic

enum class HomeDestination(val route: String, val label: String, val icon: Int, val selectedIcon: Int) {
    SONGS("songs", "Songs", R.drawable.glyphs__music_list_outline, R.drawable.glyphs__music_list_bold),
    ALBUM("album", "Album", R.drawable.glyphs__album_outline, R.drawable.glyphs__album_bold),
    Artists("artists", "Artist", R.drawable.glyphs__users_outline, R.drawable.glyphs__users_bold),
    PLAYLISTS("playlist", "Playlist", R.drawable.glyphs__grid_add_outline, R.drawable.glyphs__grid_add_bold)
}