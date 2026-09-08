package com.bsoft.compose.bmusic

import android.app.Application
import com.bsoft.compose.bmusic.data.models.ArtistDetails
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.data.repositories.SongRepository
import com.bsoft.compose.bmusic.data.states.SongsState
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject
import kotlinx.coroutines.*

private suspend fun <T1, T2, T3, T4> awaitAll(
    d1: Deferred<T1>,
    d2: Deferred<T2>,
    d3: Deferred<T3>,
    d4: Deferred<T4>
): Pair<Triple<T1, T2, T3>, T4> = Pair(Triple(d1.await(), d2.await(), d3.await()), d4.await())

@HiltAndroidApp
class BMusicApp: Application() {
    @Inject
    lateinit var songRepository: SongRepository

    suspend fun loadSongsState(): SongsState = coroutineScope {
        val songsDeferred =  async { songRepository.songs }
        val albumsDeferred = async { songRepository.albums }
        val artistsDeferred = async { songRepository.artists }
        val lastDeferred = async { songRepository.last }

        val (init, last) = awaitAll(songsDeferred, albumsDeferred, artistsDeferred, lastDeferred)

        val (songs, albums, artists) = init
        val state = SongsState(songs = songs, albums = albums, artists = artists, last = last, loaded = true)

        return@coroutineScope state
    }

    suspend fun loadSongsByAlbum(albumId: Long): List<Song> = coroutineScope {
        val songs = songRepository.findSongsByAlbumId(albumId)

        return@coroutineScope songs
    }

    suspend fun loadArtistDetails(artistId: Long): ArtistDetails = coroutineScope {
        val details = songRepository.findArtistDetailsByArtistId(artistId)

        return@coroutineScope details
    }
}