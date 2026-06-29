package com.bsoft.compose.bmusic.data.models

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

data class Album(val id: Long, val name: String, val artist: String, val songCount: Int){
    val artworkUri: Uri
        get() = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, id)
    fun toMediaItem(): MediaItem {
        return MediaItem.Builder().setMediaId("album_${id}")
            .setMediaMetadata(
                MediaMetadata.Builder().setTitle(name).setIsBrowsable(true).build()
            ).build()
    }
}