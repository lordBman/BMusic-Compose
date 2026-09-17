package com.bsoft.compose.bmusic.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import com.bsoft.compose.bmusic.MainDispatcherRule
import com.bsoft.compose.bmusic.data.datastores.appSettingsDataStore
import com.bsoft.compose.bmusic.data.serializables.AppSettingsData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import app.cash.turbine.test

@ExperimentalCoroutinesApi
class AppSettingsPreferencesTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk()
    private val dataStore: DataStore<AppSettingsData> = mockk(relaxed = true)
    private lateinit var preferences: AppSettingsPreferences

    @Before
    fun setUp() {
        mockkStatic("com.bsoft.compose.bmusic.data.datastores.AppSettingsDataStoreKt")
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns mainDispatcherRule.testDispatcher
        
        every { context.appSettingsDataStore } returns dataStore
        preferences = AppSettingsPreferences(context)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `data should return flow from datastore`() = runTest {
        val appSettingsData = AppSettingsData(accentColor = 123)
        every { dataStore.data } returns flowOf(appSettingsData)

        preferences.data.test {
            assertEquals(appSettingsData, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setAccentColor should update datastore`() = runTest {
        preferences.setAccentColor(123)
        advanceUntilIdle()
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setLanguage should update datastore`() = runTest {
        preferences.setLanguage("en")
        advanceUntilIdle()
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setRememberLastTab should update datastore`() = runTest {
        preferences.setRememberLastTab(true)
        advanceUntilIdle()
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setHideShortSongs should update datastore`() = runTest {
        preferences.setHideShortSongs(true)
        advanceUntilIdle()
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setShakeToChange should update datastore`() = runTest {
        preferences.setShakeToChange(true)
        advanceUntilIdle()
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setSoundFade should update datastore`() = runTest {
        preferences.setSoundFade(true)
        advanceUntilIdle()
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setLastPlayedMediaId should update datastore`() = runTest {
        preferences.setLastPlayedMediaId("1")
        advanceUntilIdle()
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setLastRepeatMode should update datastore`() = runTest {
        preferences.setLastRepeatMode(1)
        advanceUntilIdle()
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setLastShuffleMode should update datastore`() = runTest {
        preferences.setLastShuffleMode(true)
        advanceUntilIdle()
        coVerify { dataStore.updateData(any()) }
    }
}
