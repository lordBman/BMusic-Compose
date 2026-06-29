package com.bsoft.compose.bmusic.data.models

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

data class Song(val id: Long, val displayName: String, val title: String, val artist: String, val album: String, val duration: Long){
    val artworkUri: Uri
        get() = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    fun toMediaItem(): MediaItem {
        return MediaItem.Builder().setMediaId(id.toString()).setUri(artworkUri)
            .setMediaMetadata(
                MediaMetadata.Builder().setDisplayTitle(displayName).setTitle(title)
                    .setArtist(artist).setAlbumTitle(album).setDurationMs(duration)
                    .setIsPlayable(true)
                    .build()
            ).build()
    }

    companion object{
        fun fromMediaItem(mediaItem: MediaItem): Song{
            val id = mediaItem.mediaId.toLong()
            val displayName = mediaItem.mediaMetadata.displayTitle?.toString() ?: "Unknown Name"
            val title = mediaItem.mediaMetadata.title?.toString() ?: "Unknown Title"
            val artist = mediaItem.mediaMetadata.artist?.toString() ?: "Unknown Artist"
            val album = mediaItem.mediaMetadata.albumTitle?.toString() ?: "Unknown Album"
            val duration = mediaItem.mediaMetadata.durationMs ?: 0

            return Song(id, displayName, title, artist, album, duration)
        }
    }
}