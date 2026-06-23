package com.bsoft.compose.bmusic.data.repositories

import android.content.ContentUris
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.bsoft.compose.bmusic.data.Album
import com.bsoft.compose.bmusic.data.Artist
import com.bsoft.compose.bmusic.data.Song
import androidx.core.net.toUri

class SongRepository(private val context: Context) {
    val songs by lazy { return@lazy fetchSongs() }
    val albums by lazy { return@lazy fetchAlbums() }
    val artists by lazy { return@lazy fetchArtists() }

    private fun fetchAlbums(): List<Album> {
        val projection = arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.NUMBER_OF_SONGS)

        val selection = "${MediaStore.Audio.Albums.ARTIST} IS NOT NULL OR ${MediaStore.Audio.Albums.ARTIST} != ?"
        val selectionArgs = arrayOf("")
        val sortOrder = "${MediaStore.Audio.Albums.ALBUM} ASC"
        val cursor = context.contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder)

        return cursor?.use {
            val idCol = it.getColumnIndex(MediaStore.Audio.Albums._ID)
            val nameCol = it.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
            val artistCol = it.getColumnIndex(MediaStore.Audio.Albums.ARTIST)
            val countCol = it.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
            buildList {
                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val name = it.getString(nameCol) ?: ""
                    val artist = it.getString(artistCol) ?: ""
                    val songCount = it.getInt(countCol)
                    // Reconstruct the explicit Content Uri for the album art
                    val artworkUri = ContentUris.withAppendedId(
                        "content://media/external/audio/albumart".toUri(),
                        id
                    )
                    add(Album(id, name, artist, songCount))
                }
            }
        } ?: emptyList()
    }

    private fun fetchArtists(): List<Artist> {
        val projection = arrayOf(MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS, MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
        val sortOrder = "${MediaStore.Audio.Artists.ARTIST} ASC"
        val cursor = context.contentResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder)

        return cursor?.use {
            val idCol = it.getColumnIndex(MediaStore.Audio.Artists._ID)
            val nameCol = it.getColumnIndex(MediaStore.Audio.Artists.ARTIST)
            val albumCountCol = it.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
            val songCountCol = it.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
            buildList {
                while (it.moveToNext()) {
                    add(Artist(id = it.getLong(idCol), name = it.getString(nameCol) ?: "", albumCount = it.getInt(albumCountCol), songCount = it.getInt(songCountCol)))
                }
            }
        } ?: emptyList()
    }

    private fun fetchSongs(): List<Song> {
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

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != ? AND ${MediaStore.Audio.Media.DURATION} >= ?"
        val selectionArgs = arrayOf("0", "60000")
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        return context.contentResolver.query(collection, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            buildList {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn) ?: "Unknown Name"
                    val title = cursor.getString(titleColumn) ?: "Unknown Title"
                    val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                    val album = cursor.getString(albumColumn) ?: "Unknown Album"
                    val duration = cursor.getLong(durationColumn)
                    val contentURI = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    add(Song(id = id, displayName = displayName, title = title, artist = artist, album = album, contentUri = contentURI, duration = duration))
                }
            }
        } ?: emptyList()
    }

    fun findSongById(songId: Long): Song? {
        val result = songs.find { it.id == songId }
        if(result == null){
            Log.e("Song Repository", "No song found with ID: $songId")
        }
        return result
    }

    fun findSongsByAlbumId(albumId: Long): List<Song>{
        val album = albums.find { it.id == albumId }

        return songs.filter { it.album == album?.name }
    }

    fun findSongsByArtistId(artistId: Long): List<Song>{
        val artist = artists.find { it.id == artistId }

        return songs.filter { it.artist == artist?.name }
    }

    fun search(query: String): List<Song>{
        val lowerQuery = query.lowercase()

        return songs.filter {
            it.title.lowercase().contains(lowerQuery) ||
                    it.displayName.lowercase().contains(lowerQuery) ||
                    it.artist.lowercase().contains(lowerQuery) ||
                    it.album.lowercase().contains(lowerQuery)
        }
    }
}