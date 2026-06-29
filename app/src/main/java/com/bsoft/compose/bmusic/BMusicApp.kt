package com.bsoft.compose.bmusic

import android.app.Application
import com.bsoft.compose.bmusic.data.repositories.SongRepository
import com.bsoft.compose.bmusic.data.repositories.UserDataRepository
import com.bsoft.compose.bmusic.data.states.SongsState
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*

private suspend fun <T1, T2, T3> awaitAll(
    d1: Deferred<T1>,
    d2: Deferred<T2>,
    d3: Deferred<T3>
): Triple<T1, T2, T3> = Triple(d1.await(), d2.await(), d3.await())

@HiltAndroidApp
class BMusicApp: Application() {
    val songRepository = SongRepository(this)
    val userDataRepository = UserDataRepository(this)

    suspend fun loadSongsState(): SongsState = coroutineScope {
        val songsDeferred =  async { songRepository.songs }
        val albumsDeferred = async { songRepository.albums }
        val artistsDeferred = async { songRepository.artists }

        val (songs, albums, artists) = awaitAll(songsDeferred, albumsDeferred, artistsDeferred)
        val state = SongsState(songs = songs, albums = albums, artists = artists, loaded = true)

        return@coroutineScope state
    }
}