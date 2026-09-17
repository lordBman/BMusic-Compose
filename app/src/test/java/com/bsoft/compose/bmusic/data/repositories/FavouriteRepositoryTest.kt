package com.bsoft.compose.bmusic.data.repositories

import com.bsoft.compose.bmusic.MainDispatcherRule
import com.bsoft.compose.bmusic.data.doas.FavouritesDao
import com.bsoft.compose.bmusic.data.entities.FavouriteEntity
import com.bsoft.compose.bmusic.data.models.Song
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import app.cash.turbine.test

@ExperimentalCoroutinesApi
class FavouriteRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val favouritesDao: FavouritesDao = mockk(relaxed = true)
    private lateinit var repository: FavouriteRepository

    @Before
    fun setUp() {
        repository = FavouriteRepository(favouritesDao)
    }

    @Test
    fun `toggleFavourite should unfavourite if already favorite`() = runTest {
        val song = Song(id = 1, displayName = "Song", title = "Song", artist = "Artist", album = "Album", duration = 1000)
        every { favouritesDao.doesSongExist(1) } returns true

        repository.toggleFavourite(song)

        coVerify { favouritesDao.delete(song = 1) }
    }

    @Test
    fun `toggleFavourite should favourite if not already favorite`() = runTest {
        val song = Song(id = 1, displayName = "Song", title = "Song", artist = "Artist", album = "Album", duration = 1000)
        every { favouritesDao.doesSongExist(1) } returns false

        repository.toggleFavourite(song)

        coVerify { favouritesDao.insertAll(any()) }
    }

    @Test
    fun `getAllFavorites should return flow from dao`() = runTest {
        val favorites = listOf(FavouriteEntity(song = 1, title = "Song", favourite = true))
        every { favouritesDao.getAll() } returns flowOf(favorites)

        repository.getAllFavorites().test {
            assertEquals(favorites, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `isFavorite should call dao doesSongExist`() {
        every { favouritesDao.doesSongExist(1) } returns true
        val result = repository.isFavorite(1)
        assertTrue(result)
        coVerify { favouritesDao.doesSongExist(1) }
    }

    @Test
    fun `unFavourite should call dao delete`() = runTest {
        repository.unFavourite(1)
        coVerify { favouritesDao.delete(song = 1) }
    }

    @Test
    fun `updateFavourite should call dao updateFavorites`() = runTest {
        repository.updateFavourite(1, true)
        coVerify { favouritesDao.updateFavorites(song = 1, true) }
    }
}
