package com.bsoft.compose.bmusic.data.repositories

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.bsoft.compose.bmusic.data.models.Album
import com.bsoft.compose.bmusic.data.models.Artist
import com.bsoft.compose.bmusic.data.models.ArtistDetails
import com.bsoft.compose.bmusic.data.models.Song

private val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
} else {
    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
}

private const val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
//private const val sortOrder = "${MediaStore.Audio.Media.TRACK} ASC"
private val projection = arrayOf(
    MediaStore.Audio.Media._ID,
    MediaStore.Audio.Media.DISPLAY_NAME,
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.ALBUM,
    MediaStore.Audio.Media.DURATION
)

private val albumProjection = arrayOf(
    MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM,
    MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.NUMBER_OF_SONGS)

private const val albumSortOrder = "${MediaStore.Audio.Albums.ALBUM} ASC"
private val albumCollection = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

private fun fetchAlbums(context: Context): Map<Long, Album> {
    val selectionArgs = arrayOf("")
    val selection = "${MediaStore.Audio.Albums.ARTIST} IS NOT NULL OR ${MediaStore.Audio.Albums.ARTIST} != ?"

    return context.contentResolver.query(albumCollection, albumProjection, selection, selectionArgs, albumSortOrder)?.use {
        val idCol = it.getColumnIndex(MediaStore.Audio.Albums._ID)
        val nameCol = it.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
        val artistCol = it.getColumnIndex(MediaStore.Audio.Albums.ARTIST)
        val countCol = it.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
        buildMap {
            while (it.moveToNext()) {
                val id = it.getLong(idCol)
                val name = it.getString(nameCol) ?: ""
                val artist = it.getString(artistCol) ?: ""
                val songCount = it.getInt(countCol)
                put(id, Album(id, name, artist, songCount))
            }
        }
    } ?: emptyMap()
}

private fun fetchArtists(context: Context): Map<Long, Artist> {
    val projection = arrayOf(MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS, MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
    val sortOrder = "${MediaStore.Audio.Artists.ARTIST} ASC"
    val cursor = context.contentResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder)

    return cursor?.use {
        val idCol = it.getColumnIndex(MediaStore.Audio.Artists._ID)
        val nameCol = it.getColumnIndex(MediaStore.Audio.Artists.ARTIST)
        val albumCountCol = it.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
        val songCountCol = it.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
        buildMap {
            while (it.moveToNext()) {
                val id = it.getLong(idCol)
                put(id, Artist(id = id, name = it.getString(nameCol) ?: "", albumCount = it.getInt(albumCountCol), songCount = it.getInt(songCountCol)))
            }
        }
    } ?: emptyMap()
}

private fun fetchSongs(context: Context): Map<Long, Song> {
    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != ? AND ${MediaStore.Audio.Media.DURATION} >= ?"
    val selectionArgs = arrayOf("0", "60000")


    return context.contentResolver.query(collection, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

        buildMap {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn) ?: "Unknown Name"
                val title = cursor.getString(titleColumn) ?: "Unknown Title"
                val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                val album = cursor.getString(albumColumn) ?: "Unknown Album"
                val duration = cursor.getLong(durationColumn)
                //val contentURI = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                put(id, Song(id = id, displayName = displayName, title = title, artist = artist, album = album, duration = duration))
            }
        }
    } ?: emptyMap()
}

private fun fetchSongsByAlbum(context: Context, albumId: Long): List<Song> {
    val selection = "${MediaStore.Audio.Media.ALBUM_ID} = ? AND ${MediaStore.Audio.Media.IS_MUSIC} != 0"
    val selectionArgs = arrayOf(albumId.toString())

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
                //val contentURI = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                add(Song(id = id, displayName = displayName, title = title, artist = artist, album = album, duration = duration))
            }
        }
    } ?: emptyList()
}

private fun fetchSongsAndAlbumByArtist(context: Context, artistId: Long): ArtistDetails {
    val selection = "${MediaStore.Audio.Media.ARTIST_ID} = ?"
    val selectionArgs = arrayOf(artistId.toString())

    val songs = context.contentResolver.query(collection, projection, selection, selectionArgs, null)?.use { cursor ->
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
                //val contentURI = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                add(Song(id = id, displayName = displayName, title = title, artist = artist, album = album, duration = duration))
            }
        }
    } ?: emptyList()

    val albumSelection = "${MediaStore.Audio.Albums.ARTIST} == ?"
    val albums = context.contentResolver.query(albumCollection, albumProjection, albumSelection, selectionArgs, albumSortOrder)?.use {
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
                add(Album(id, name, artist, songCount))
            }
        }
    } ?: emptyList()

    return ArtistDetails(id = artistId, albums = albums, songs = songs)
}

private fun fetchLastAdded(context: Context): Map<Long, Song>{
    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

    val queryArgs = Bundle().apply {
        putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
        putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, null)
        putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(MediaStore.Audio.Media.DATE_ADDED))
        putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING)
        putInt(ContentResolver.QUERY_ARG_LIMIT, 10)
    }

    return context.contentResolver.query(collection, projection, queryArgs, null)?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

        buildMap {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn) ?: "Unknown Name"
                val title = cursor.getString(titleColumn) ?: "Unknown Title"
                val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                val album = cursor.getString(albumColumn) ?: "Unknown Album"
                val duration = cursor.getLong(durationColumn)
                //val contentURI = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                put(id, Song(id = id, displayName = displayName, title = title, artist = artist, album = album, duration = duration))
            }
        }
    } ?: emptyMap()
}


class SongRepository(private val context: Context) {
    private val _songs by lazy { fetchSongs(context) }
    val songs: List<Song>
        get() = _songs.values.toList()

    private val _albums by lazy { fetchAlbums(context) }
    val albums: List<Album>
        get() = _albums.values.toList()

    private val _artists by lazy { fetchArtists(context) }
    val artists: List<Artist>
        get() = _artists.values.toList()

    private val _last by lazy { fetchLastAdded(context) }
    val last: List<Song>
        get() = _last.values.toList()

    private val albumStore: MutableMap<Long, List<Song>> = mutableMapOf()
    private val artistStore: MutableMap<Long, ArtistDetails> = mutableMapOf()

    fun findSongById(songId: Long): Song? {
        val result = _songs[songId]
        if(result == null){
            Log.e("Song Repository", "No song found with ID: $songId")
        }
        return result
    }

    fun findAlbumById(albumId: Long): Album? {
        val result = _albums[albumId]
        if(result == null){
            Log.e("Song Repository", "No Album found with ID: $albumId")
        }
        return result
    }

    fun findSongsByAlbumId(albumId: Long): List<Song>{
        if(albumStore.containsKey(albumId)){
           return albumStore[albumId]!!
        }

        val init = fetchSongsByAlbum(context, albumId)
        albumStore[albumId] = init

        return init
    }

    fun findArtistById(artistId: Long): Artist? {
        val result = _artists[artistId]
        if(result == null){
            Log.e("Song Repository", "No Artist found with ID: $artistId")
        }
        return result
    }

    fun findArtistDetailsByArtistId(artistId: Long): ArtistDetails{
        if (artistStore.containsKey(artistId)){
            return artistStore[artistId]!!
        }

        val init = fetchSongsAndAlbumByArtist(context, artistId)
        artistStore[artistId] = init

        return init
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