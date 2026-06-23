package com.bsoft.compose.bmusic.data

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

data class Song(val id: Long, val displayName: String, val title: String, val artist: String, val album: String, val duration: Long, val contentUri: Uri){
    fun toMediaItem(): MediaItem {
        return MediaItem.Builder().setMediaId(id.toString()).setUri(contentUri)
            .setMediaMetadata(
                MediaMetadata.Builder().setDisplayTitle(displayName).setTitle(title).setArtist(artist).setAlbumTitle(album).setDurationMs(duration).setIsPlayable(true).build()
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
            val contentURI = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

            return Song(id, displayName, title, artist, album, duration, contentURI)
        }
    }
}

data class Album(val id: Long, val name: String, val artist: String, val songCount: Int){
    fun toMediaItem(): MediaItem {
        return MediaItem.Builder().setMediaId("album_${id}")
            .setMediaMetadata(
                MediaMetadata.Builder().setTitle(name).setIsBrowsable(true).build()
            ).build()
    }
}

data class Artist(val id: Long, val name: String, val albumCount: Int, val songCount: Int){
    fun toMediaItem(): MediaItem {
        return MediaItem.Builder().setMediaId("artist_${id}")
            .setMediaMetadata(
                MediaMetadata.Builder().setTitle(name).setIsBrowsable(true).build()
            ).build()
    }
}

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
    val playing: Boolean = false,
    val position: Long = 0,
    val playlist: List<Song> = emptyList(),
    val favourites: List<Long> = emptyList(),
    val repeatMode: RepeatMode = RepeatMode.All,
    val shuffle: Boolean = false
)