package com.bsoft.compose.bmusic.data

import android.media.audiofx.Equalizer
import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import io.mockk.Runs
import io.mockk.just
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EqualizerManagerTest {

    private lateinit var manager: EqualizerManager

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        
        mockkConstructor(Equalizer::class)
        every { anyConstructed<Equalizer>().numberOfBands } returns 5
        every { anyConstructed<Equalizer>().bandLevelRange } returns shortArrayOf(-1500, 1500)
        every { anyConstructed<Equalizer>().getBandLevel(any<Short>()) } returns 0
        every { anyConstructed<Equalizer>().getCenterFreq(any<Short>()) } returns 100000
        every { anyConstructed<Equalizer>().numberOfPresets } returns 2
        every { anyConstructed<Equalizer>().getPresetName(any<Short>()) } answers { "Preset ${args[0]}" }
        every { anyConstructed<Equalizer>().enabled } returns false
        every { anyConstructed<Equalizer>().setEnabled(any<Boolean>()) } returns 0
        every { anyConstructed<Equalizer>().usePreset(any<Short>()) } just Runs
        every { anyConstructed<Equalizer>().setBandLevel(any<Short>(), any<Short>()) } just Runs
        every { anyConstructed<Equalizer>().setControlStatusListener(any()) } just Runs
        every { anyConstructed<Equalizer>().setParameterListener(any<Equalizer.OnParameterChangeListener>()) } just Runs
        every { anyConstructed<Equalizer>().release() } just Runs
        
        manager = EqualizerManager()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `attach should initialize equalizer and call listeners`() {
        var listenerCalled = false
        var updateListenerCalled = false
        manager.setListeners({ listenerCalled = true }, { updateListenerCalled = true })

        manager.attach(1)

        assertTrue(listenerCalled)
        assertTrue(updateListenerCalled)
        assertNotNull(manager.equalizer)
    }

    @Test
    fun `enabled should get and set equalizer enabled state`() {
        manager.attach(1)
        
        // Mocking the behavior after attach since it creates a new instance
        val equalizer = manager.equalizer!!
        every { equalizer.enabled } returns true
        
        assertTrue(manager.enabled)
        
        manager.enabled = false
        verify { equalizer.enabled = false }
    }

    @Test
    fun `bandCount should return number of bands`() {
        manager.attach(1)
        assertEquals(5, manager.bandCount)
    }

    @Test
    fun `bands should return list of EqualizerBand`() {
        manager.attach(1)
        val bands = manager.bands
        assertEquals(5, bands.size)
        assertEquals(0.toShort(), bands[0].band)
        assertEquals(100, bands[0].frequency)
    }

    @Test
    fun `presets should return list of EqualizerPresent`() {
        manager.attach(1)
        val presets = manager.presets
        assertEquals(2, presets.size)
        assertEquals("Preset 0", presets[0].name)
    }

    @Test
    fun `choosePreset should call equalizer usePreset and update listener`() {
        var updateListenerCalled = false
        manager.setListeners({}, { updateListenerCalled = true })
        manager.attach(1)
        updateListenerCalled = false // reset after attach

        manager.choosePreset(1)

        verify { manager.equalizer?.usePreset(1) }
        assertTrue(updateListenerCalled)
    }

    @Test
    fun `modifyBand should call equalizer setBandLevel and update listener`() {
        var updateListenerCalled = false
        manager.setListeners({}, { updateListenerCalled = true })
        manager.attach(1)
        updateListenerCalled = false // reset after attach

        manager.modifyBand(0, 500)

        verify { manager.equalizer?.setBandLevel(0, 500) }
        assertTrue(updateListenerCalled)
    }

    @Test
    fun `release should nullify equalizer`() {
        manager.attach(1)
        manager.release()
        assertEquals(null, manager.equalizer)
    }
}
