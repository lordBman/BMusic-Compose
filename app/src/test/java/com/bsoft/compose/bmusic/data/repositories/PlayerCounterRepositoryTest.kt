package com.bsoft.compose.bmusic.data.repositories

import com.bsoft.compose.bmusic.data.doas.PlayerCounterDao
import com.bsoft.compose.bmusic.data.entities.PlayCounterEntity
import com.bsoft.compose.bmusic.data.models.Song
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PlayerCounterRepositoryTest {

    private lateinit var playerCounterDao: PlayerCounterDao
    private lateinit var repository: PlayerCounterRepository
    private val song = Song(1L, "Display", "Title", "Artist", "Album", 1000L)

    @Before
    fun setUp() {
        playerCounterDao = mockk(relaxed = true)
        repository = PlayerCounterRepository(playerCounterDao)
    }

    @Test
    fun `incrementCount inserts new entity if not exists`() = runTest {
        coEvery { playerCounterDao.get(1L) } returns null
        repository.incrementCount(song)
        coVerify { playerCounterDao.insertAll(any()) }
    }

    @Test
    fun `incrementCount updates existing entity`() = runTest {
        val existing = PlayCounterEntity(song = 1L, title = "Title", played = 5, lastPlayed = 0L)
        coEvery { playerCounterDao.get(1L) } returns existing
        repository.incrementCount(song)
        coVerify { playerCounterDao.update(match { it.played == 6 }) }
    }
}
