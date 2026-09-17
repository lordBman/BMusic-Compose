package com.bsoft.compose.bmusic.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionsTest {

    @Test
    fun `toTimeFormat should format duration correctly`() {
        assertEquals("00:00", 0L.toTimeFormat())
        assertEquals("00:01", 1000L.toTimeFormat())
        assertEquals("01:00", 60000L.toTimeFormat())
        assertEquals("10:05", 605000L.toTimeFormat())
    }
}
