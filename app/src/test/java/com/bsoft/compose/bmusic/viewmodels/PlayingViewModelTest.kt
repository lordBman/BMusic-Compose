package com.bsoft.compose.bmusic.viewmodels

import android.os.Looper
import com.bsoft.compose.bmusic.data.QueueManager
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.data.preferences.AppSettingsPreferences
import com.bsoft.compose.bmusic.data.repositories.FavouriteRepository
import com.bsoft.compose.bmusic.data.repositories.PlayerCounterRepository
import com.bsoft.compose.bmusic.data.states.QueueState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var queueManager: QueueManager
    private lateinit var playerCounterRepository: PlayerCounterRepository
    private lateinit var favouriteRepository: FavouriteRepository
    private lateinit var appSettingsPreferences: AppSettingsPreferences
    private lateinit var viewModel: PlayingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns mockk(relaxed = true)
        
        queueManager = mockk(relaxed = true)
        playerCounterRepository = mockk(relaxed = true)
        favouriteRepository = mockk(relaxed = true)
        appSettingsPreferences = mockk(relaxed = true)
        
        every { queueManager.state } returns MutableStateFlow(QueueState())
        every { favouriteRepository.getAllFavorites() } returns flowOf(emptyList())
        every { playerCounterRepository.getLastPlayed() } returns flowOf(emptyList())
        every { playerCounterRepository.getMostPlayed() } returns flowOf(emptyList())
        every { appSettingsPreferences.data } returns flowOf(mockk(relaxed = true))

        viewModel = PlayingViewModel(
            queueManager,
            playerCounterRepository,
            favouriteRepository,
            appSettingsPreferences
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Looper::class)
    }

    @Test
    fun `toggleFavorite calls repository`() = runTest {
        val song = Song(1L, "D", "T", "A", "Al", 1000L)
        every { queueManager.state.value.current } returns song
        
        viewModel.toggleFavorite()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { favouriteRepository.toggleFavourite(song) }
    }

    @Test
    fun `initial state is correct`() {
        assertNotNull(viewModel.state.value)
    }
}
