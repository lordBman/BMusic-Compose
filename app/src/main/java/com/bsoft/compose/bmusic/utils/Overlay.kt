package com.bsoft.compose.bmusic.utils

data class ScreenOverlay(val item: Item = Item.None, val id: Long = -1, val name: String = ""){
    enum class Item{
        None, PlaylistForm, PlaylistMenu, PlaylistDeleteDialog
    }
}