package com.bsoft.compose.bmusic.viewmodels

import android.app.Application
import android.content.ContentResolver
import android.os.Looper
import com.bsoft.compose.bmusic.BMusicApp
import com.bsoft.compose.bmusic.MainDispatcherRule
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.data.repositories.FavouriteRepository
import com.bsoft.compose.bmusic.data.repositories.PlaylistRepository
import com.bsoft.compose.bmusic.data.repositories.SongRepository
import com.bsoft.compose.bmusic.data.states.SongsState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import app.cash.turbine.test
import com.bsoft.compose.bmusic.data.models.Album
import com.bsoft.compose.bmusic.data.models.Artist
import com.bsoft.compose.bmusic.data.models.ArtistDetails

@ExperimentalCoroutinesApi
class SongsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application: Application = mockk()
    private val app: BMusicApp = mockk(relaxed = true)
    private val favouriteRepository: FavouriteRepository = mockk(relaxed = true)
    private val playlistRepository: PlaylistRepository = mockk(relaxed = true)
    private val songRepository: SongRepository = mockk(relaxed = true)
    private val contentResolver: ContentResolver = mockk(relaxed = true)

    private lateinit var viewModel: SongsViewModel

    @Before
    fun setUp() {
        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns mockk(relaxed = true)
        
        every { application.applicationContext } returns app
        every { app.contentResolver } returns contentResolver
        every { app.songRepository } returns songRepository
        
        every { favouriteRepository.getAllFavorites() } returns flowOf(emptyList())
        every { playlistRepository.getPlayLists() } returns flowOf(emptyList())

        viewModel = SongsViewModel(application, favouriteRepository, playlistRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `query should update state with loaded songs`() = runTest {
        val songsState = SongsState(songs = listOf(mockk()), loaded = true)
        coEvery { app.loadSongsState() } returns songsState

        viewModel.query()

        viewModel.state.test {
            var item = awaitItem()
            while (!item.loaded) {
                item = awaitItem()
            }
            assertEquals(songsState.songs, item.songs)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `queryAlbum should update albumState`() = runTest {
        val album = Album(id = 1, name = "Album", artist = "Artist", songCount = 1)
        val songs = listOf<Song>(mockk(relaxed = true))
        coEvery { songRepository.findAlbumById(1) } returns album
        coEvery { app.loadSongsByAlbum(1) } returns songs

        viewModel.queryAlbum(1)

        viewModel.albumState.test {
            var item = awaitItem()
            while (!item.loaded) {
                item = awaitItem()
            }
            assertEquals(album, item.album)
            assertEquals(songs, item.songs)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `queryArtist should update artistState`() = runTest {
        val artist = Artist(id = 1, name = "Artist", albumCount = 1, songCount = 1)
        val details = ArtistDetails(id = 1, songs = listOf(mockk(relaxed = true)), albums = listOf(mockk(relaxed = true)))
        coEvery { songRepository.findArtistById(1) } returns artist
        coEvery { app.loadArtistDetails(1) } returns details

        viewModel.queryArtist(1)

        viewModel.artistState.test {
            var item = awaitItem()
            while (!item.loaded) {
                item = awaitItem()
            }
            assertEquals(artist, item.artist)
            assertEquals(details.songs, item.songs)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `createPlaylist should call repository`() = runTest {
        viewModel.createPlaylist("New")
        coVerify(timeout = 2000) { playlistRepository.createPlayList(any()) }
    }

    @Test
    fun `deletePlaylist should call repository`() = runTest {
        viewModel.deletePlaylist(1L)
        coVerify(timeout = 2000) { playlistRepository.deletePlayList(1L) }
    }

    @Test
    fun `addToPlaylist should call repository with mapped entities`() = runTest {
        val song = Song(id = 1, displayName = "Song", title = "Song", artist = "Artist", album = "Album", duration = 1000)
        viewModel.addToPlaylist(1L, listOf(song))
        coVerify(timeout = 2000) { playlistRepository.insertSongsIntoPlayList(any()) }
    }
}
