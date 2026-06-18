package com.bsoft.compose.bmusic

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.core.net.toUri


fun loadMediaStoreArt(context: Context, albumId: Long) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

        //context.contentResolver.loadThumbnail(currentAlbumUri, Size(200, 200), null)
    } else {
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val artworkUri: Uri = "content://media/external/audio/albumart".toUri()
        val currentAlbumUri: Uri = ContentUris.withAppendedId(artworkUri, albumId)
    }
}

fun fetchSongs(context: Context): List<Song> {
    val songList = mutableListOf<Song>()
    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION
    )

    // Only query files marked as music
    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

    context.contentResolver.query(collection, projection, selection, null, sortOrder)?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val displayName = cursor.getString(displayNameColumn) ?: "Unknown Name"
            val title = cursor.getString(titleColumn) ?: "Unknown Title"
            val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
            val album = cursor.getString(albumColumn) ?: "Unknown Album"
            val duration = cursor.getLong(durationColumn)
            val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

            songList.add(Song(id = id, displayName = displayName, title = title, artist = artist, album = album, duration = duration, contentUri = contentUri))
        }
    }

    return songList
}

class BMusicApp: Application() {
    val songs: List<Song> by lazy {
        val init = fetchSongs(this)

        return@lazy init;
    }
}