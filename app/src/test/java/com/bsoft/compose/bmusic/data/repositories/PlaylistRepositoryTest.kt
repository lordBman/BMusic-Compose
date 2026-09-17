package com.bsoft.compose.bmusic.data.repositories

import com.bsoft.compose.bmusic.MainDispatcherRule
import com.bsoft.compose.bmusic.data.doas.PlaylistDao
import com.bsoft.compose.bmusic.data.entities.PlaylistEntity
import com.bsoft.compose.bmusic.data.entities.PlaylistSongEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import app.cash.turbine.test

@ExperimentalCoroutinesApi
class PlaylistRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val playlistDao: PlaylistDao = mockk(relaxed = true)
    private lateinit var repository: PlaylistRepository

    @Before
    fun setUp() {
        repository = PlaylistRepository(playlistDao)
    }

    @Test
    fun `createPlayList should call dao createPlaylists`() = runTest {
        val playlist = PlaylistEntity(name = "Test Playlist")
        repository.createPlayList(playlist)
        coVerify { playlistDao.createPlaylists(playlist) }
    }

    @Test
    fun `getPlayLists should return flow from dao`() = runTest {
        val playlists = listOf(PlaylistEntity(id = 1, name = "Test"))
        every { playlistDao.getPlaylists() } returns flowOf(playlists)

        repository.getPlayLists().test {
            assertEquals(playlists, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `deletePlayList should call dao delete methods`() = runTest {
        val playlistId = 1L
        repository.deletePlayList(playlistId)
        coVerify { playlistDao.deletePlaylist(playlistId) }
        coVerify { playlistDao.deleteAllSongFromPlaylist(playlistId) }
    }

    @Test
    fun `insertSongsIntoPlayList should insert only new songs and update details`() = runTest {
        val song1 = PlaylistSongEntity(id = 1, title = "Song 1", displayName = "Song 1", artist = "Artist 1", album = "Album 1", duration = 1000, playlist = 10)
        val song2 = PlaylistSongEntity(id = 2, title = "Song 2", displayName = "Song 2", artist = "Artist 2", album = "Album 2", duration = 2000, playlist = 10)
        val songEntities = listOf(song1, song2)

        coEvery { playlistDao.doesSongExistInPlaylist(1, 10) } returns false
        coEvery { playlistDao.doesSongExistInPlaylist(2, 10) } returns true
        coEvery { playlistDao.getTotalSongsDurationForPlaylist(10) } returns 3000
        coEvery { playlistDao.getPlaylistSongCount(10) } returns 2

        repository.insertSongsIntoPlayList(songEntities)

        coVerify { playlistDao.insertPlaylistSong(song1) }
        coVerify(exactly = 0) { playlistDao.insertPlaylistSong(song2) }
        coVerify { playlistDao.updateSongDetails(10, 2, 3000) }
    }

    @Test
    fun `getPlayList should return playlist from dao`() = runTest {
        val playlist = PlaylistEntity(id = 1, name = "Test")
        coEvery { playlistDao.getPlaylist(1) } returns playlist
        val result = repository.getPlayList(1)
        assertEquals(playlist, result)
    }

    @Test
    fun `getSongsFromPlayList should return songs from dao`() = runTest {
        val songs = listOf(PlaylistSongEntity(id = 1, title = "Song", displayName = "Song", artist = "Artist", album = "Album", duration = 1000, playlist = 1))
        coEvery { playlistDao.getPlaylistSongs(1) } returns songs
        val result = repository.getSongsFromPlayList(1)
        assertEquals(songs, result)
    }

    @Test
    fun `deleteFromPlaylist should delete song and update details`() = runTest {
        val song = PlaylistSongEntity(id = 1, title = "Song", displayName = "Song", artist = "Artist", album = "Album", duration = 1000, playlist = 10)
        coEvery { playlistDao.getTotalSongsDurationForPlaylist(10) } returns 0
        coEvery { playlistDao.getPlaylistSongCount(10) } returns 0

        repository.deleteFromPlaylist(song)

        coVerify { playlistDao.deleteSongFromPlaylist(song) }
        coVerify { playlistDao.updateSongDetails(10, 0, 0) }
    }
}
