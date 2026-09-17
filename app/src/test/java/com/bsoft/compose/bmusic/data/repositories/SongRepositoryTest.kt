package com.bsoft.compose.bmusic.data.repositories

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SongRepositoryTest {

    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver
    private lateinit var songRepository: SongRepository

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        contentResolver = mockk(relaxed = true)
        every { context.contentResolver } returns contentResolver
        songRepository = SongRepository(context)
    }

    @Test
    fun `songs returns empty list when cursor is null`() {
        every { contentResolver.query(any(), any(), any(), any(), any()) } returns null
        assertTrue(songRepository.songs.isEmpty())
    }

    @Test
    fun `search returns filtered songs`() {
        val cursor = mockk<Cursor>(relaxed = true)
        every { contentResolver.query(any(), any(), any(), any(), any()) } returns cursor
        
        // Mock column indices for fetchSongs
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID) } returns 0
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME) } returns 1
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE) } returns 2
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST) } returns 3
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM) } returns 4
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION) } returns 5
        
        every { cursor.moveToNext() } returnsMany listOf(true, true, false)
        
        // First song
        every { cursor.getLong(0) } returns 1L
        every { cursor.getString(1) } returns "song1.mp3"
        every { cursor.getString(2) } returns "Song Alpha"
        every { cursor.getString(3) } returns "Artist A"
        every { cursor.getString(4) } returns "Album A"
        every { cursor.getLong(5) } returns 180000L
        
        // Second song (need to use andThen or separate calls)
        // Actually returnsMany or sequence is better
        every { cursor.getLong(0) } returnsMany listOf(1L, 2L)
        every { cursor.getString(1) } returnsMany listOf("song1.mp3", "song2.mp3")
        every { cursor.getString(2) } returnsMany listOf("Song Alpha", "Song Beta")
        every { cursor.getString(3) } returnsMany listOf("Artist A", "Artist B")
        every { cursor.getString(4) } returnsMany listOf("Album A", "Album B")
        every { cursor.getLong(5) } returnsMany listOf(180000L, 200000L)

        val results = songRepository.search("Alpha")
        assertEquals(1, results.size)
        assertEquals("Song Alpha", results[0].title)
    }
}
