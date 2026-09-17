package com.bsoft.compose.bmusic.viewmodels

import com.bsoft.compose.bmusic.MainDispatcherRule
import com.bsoft.compose.bmusic.data.EqualizerManager
import com.bsoft.compose.bmusic.data.models.EqualizerBand
import com.bsoft.compose.bmusic.data.preferences.EqualizerPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import app.cash.turbine.test

@ExperimentalCoroutinesApi
class EqualizerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val manager: EqualizerManager = mockk(relaxed = true)
    private val preferences: EqualizerPreferences = mockk(relaxed = true)
    private lateinit var viewModel: EqualizerViewModel

    @Before
    fun setUp() {
        every { preferences.isEqualizerEnabled } returns flowOf(true)
        every { preferences.values } returns flowOf(listOf(0, 0, 0))
        every { preferences.selected } returns flowOf(1)
        
        viewModel = EqualizerViewModel(manager, preferences)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `toggleEnable should toggle state and update preferences`() = runTest {
        viewModel.toggleEnable()
        verify { manager.enabled = false }
        verify { preferences.enableEqualizer(false) }
    }

    @Test
    fun `choosePreset should update manager and preferences if preset is positive`() {
        viewModel.choosePreset(2)
        verify { manager.choosePreset(2) }
        verify { preferences.setSelected(2) }
    }

    @Test
    fun `choosePreset should apply manual bands if preset is negative`() {
        every { preferences.values } returns flowOf(listOf(100.toShort(), 200.toShort()))
        // Re-init to pick up new values flow because stateIn eagerly collects
        viewModel = EqualizerViewModel(manager, preferences)
        
        viewModel.choosePreset(-1)
        verify { manager.modifyBand(0, 100) }
        verify { manager.modifyBand(1, 200) }
    }

    @Test
    fun `modifyBand should update manager and preferences`() {
        val band = EqualizerBand(band = 0, frequency = 100, level = 500, min = -1500, max = 1500)
        every { manager.bands } returns listOf(band)
        
        viewModel.modifyBand(0, 500)
        
        verify { manager.modifyBand(0, 500) }
        verify { preferences.setSelected(-1) }
        verify { preferences.setValues(listOf(500)) }
    }
}
