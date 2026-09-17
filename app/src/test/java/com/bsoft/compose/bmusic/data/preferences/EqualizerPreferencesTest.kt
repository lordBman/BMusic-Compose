package com.bsoft.compose.bmusic.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import com.bsoft.compose.bmusic.MainDispatcherRule
import com.bsoft.compose.bmusic.data.datastores.equalizerPreferencesDataStore
import com.bsoft.compose.bmusic.data.serializables.EqualizerPreferencesData
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
class EqualizerPreferencesTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk()
    private val dataStore: DataStore<EqualizerPreferencesData> = mockk(relaxed = true)
    private lateinit var preferences: EqualizerPreferences

    @Before
    fun setUp() {
        mockkStatic("com.bsoft.compose.bmusic.data.datastores.EqualizerPreferencesDataStoreKt")
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns mainDispatcherRule.testDispatcher

        every { context.equalizerPreferencesDataStore } returns dataStore
        preferences = EqualizerPreferences(context)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `isEqualizerEnabled should return flow from datastore`() = runTest {
        val data = EqualizerPreferencesData(isEqualizerEnabled = true)
        every { dataStore.data } returns flowOf(data)

        preferences.isEqualizerEnabled.test {
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `values should return flow from datastore`() = runTest {
        val values = listOf<Short>(1, 2, 3)
        val data = EqualizerPreferencesData(values = values)
        every { dataStore.data } returns flowOf(data)

        preferences.values.test {
            assertEquals(values, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selected should return flow from datastore`() = runTest {
        val data = EqualizerPreferencesData(selected = 2)
        every { dataStore.data } returns flowOf(data)

        preferences.selected.test {
            assertEquals(2.toShort(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `enableEqualizer should update datastore`() = runTest {
        preferences.enableEqualizer(true)
        advanceUntilIdle()
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setValues should update datastore`() = runTest {
        preferences.setValues(listOf(1, 2, 3))
        advanceUntilIdle()
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setSelected should update datastore`() = runTest {
        preferences.setSelected(1)
        advanceUntilIdle()
        coVerify { dataStore.updateData(any()) }
    }
}
