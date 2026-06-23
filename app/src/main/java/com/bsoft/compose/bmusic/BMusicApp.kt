package com.bsoft.compose.bmusic

import android.app.Application
import android.content.ContentUris
import android.os.Build
import android.provider.MediaStore
import com.bsoft.compose.bmusic.data.Album
import com.bsoft.compose.bmusic.data.Artist
import com.bsoft.compose.bmusic.data.Song
import com.bsoft.compose.bmusic.data.SongsState
import com.bsoft.compose.bmusic.data.repositories.SongRepository
import kotlinx.coroutines.*

/*fun loadMediaStoreArt(context: Context, albumId: Long) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

        //context.contentResolver.loadThumbnail(currentAlbumUri, Size(200, 200), null)
    } else {
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val artworkUri: Uri = "content://media/external/audio/albumart".toUri()
        val currentAlbumUri: Uri = ContentUris.withAppendedId(artworkUri, albumId)
    }
}*/

private suspend fun <T1, T2, T3> awaitAll(
    d1: Deferred<T1>,
    d2: Deferred<T2>,
    d3: Deferred<T3>
): Triple<T1, T2, T3> = Triple(d1.await(), d2.await(), d3.await())

class BMusicApp: Application() {
    val songRepository = SongRepository(this)

    suspend fun loadSongsState(): SongsState = coroutineScope {
        val songsDeferred =  async { songRepository.songs }
        val albumsDeferred = async { songRepository.albums }
        val artistsDeferred = async { songRepository.artists }

        val (songs, albums, artists) = awaitAll(songsDeferred, albumsDeferred, artistsDeferred)
        val state = SongsState(songs = songs, albums = albums, artists = artists, loaded = true)

        return@coroutineScope state
    }
}