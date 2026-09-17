package com.bsoft.compose.bmusic.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Size
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class UtilTest {

    @Before
    fun setUp() {
        mockkStatic(BitmapFactory::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `loadArtwork should return null if uri is null`() = runTest {
        val context: Context = mockk()
        val size: Size = mockk()
        val result = Util.loadArtwork(context, null, size)
        assertNull(result)
    }

    @Test
    fun `loadArtwork should return bitmap if contentResolver succeeds`() = runTest {
        val context: Context = mockk()
        val contentResolver: ContentResolver = mockk()
        val uri: Uri = mockk()
        val bitmap: Bitmap = mockk()
        val size: Size = mockk()

        every { context.contentResolver } returns contentResolver
        // Mock both paths to avoid SDK version issues in test
        every { contentResolver.loadThumbnail(any(), any(), any()) } returns bitmap
        every { contentResolver.openInputStream(any()) } returns mockk(relaxed = true)
        every { BitmapFactory.decodeStream(any()) } returns bitmap

        val result = Util.loadArtwork(context, uri, size)
        assertEquals(bitmap, result)
    }

    @Test
    fun `loadArtwork should return null on IOException`() = runTest {
        val context: Context = mockk()
        val contentResolver: ContentResolver = mockk()
        val uri: Uri = mockk()
        val size: Size = mockk()

        every { context.contentResolver } returns contentResolver
        // Mock both paths to throw or return null
        every { contentResolver.loadThumbnail(any(), any(), any()) } throws IOException()
        every { contentResolver.openInputStream(any()) } throws IOException()

        val result = Util.loadArtwork(context, uri, size)
        assertNull(result)
    }
}
