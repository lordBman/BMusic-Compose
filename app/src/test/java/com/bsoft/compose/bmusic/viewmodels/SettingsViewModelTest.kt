package com.bsoft.compose.bmusic.viewmodels

import com.bsoft.compose.bmusic.MainDispatcherRule
import com.bsoft.compose.bmusic.data.preferences.AppSettingsPreferences
import com.bsoft.compose.bmusic.data.serializables.AppSettingsData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import app.cash.turbine.test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val preferences: AppSettingsPreferences = mockk(relaxed = true)
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        every { preferences.data } returns flowOf(AppSettingsData(accentColor = 1))
        viewModel = SettingsViewModel(preferences)
    }

    @Test
    fun `settings should expose data from preferences`() = runTest {
        viewModel.settings.test {
            assertEquals(1, awaitItem()?.accentColor)
        }
    }

    @Test
    fun `setAccentColor should delegate to preferences`() {
        viewModel.setAccentColor(123)
        verify { preferences.setAccentColor(123) }
    }

    @Test
    fun `setLanguage should delegate to preferences`() {
        viewModel.setLanguage("en")
        verify { preferences.setLanguage("en") }
    }

    @Test
    fun `setRememberLastTab should delegate to preferences`() {
        viewModel.setRememberLastTab(true)
        verify { preferences.setRememberLastTab(true) }
    }

    @Test
    fun `setHideShortSongs should delegate to preferences`() {
        viewModel.setHideShortSongs(true)
        verify { preferences.setHideShortSongs(true) }
    }

    @Test
    fun `setShakeToChange should delegate to preferences`() {
        viewModel.setShakeToChange(true)
        verify { preferences.setShakeToChange(true) }
    }

    @Test
    fun `setSoundFade should delegate to preferences`() {
        viewModel.setSoundFade(true)
        verify { preferences.setSoundFade(true) }
    }
}
