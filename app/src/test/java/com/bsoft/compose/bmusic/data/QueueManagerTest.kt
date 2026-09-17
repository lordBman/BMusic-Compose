package com.bsoft.compose.bmusic.data

import androidx.media3.common.MediaItem
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class QueueManagerTest {

    private lateinit var queueManager: QueueManager
    private val mediaItems = listOf(
        MediaItem.Builder().setMediaId("1").build(),
        MediaItem.Builder().setMediaId("2").build(),
        MediaItem.Builder().setMediaId("3").build()
    )

    @Before
    fun setUp() {
        queueManager = QueueManager()
    }

    @Test
    fun `setQueue initializes originalQueue and state`() = runTest {
        queueManager.setQueue(mediaItems, 0)
        assertEquals(mediaItems, queueManager.originalQueue)
        assertEquals(mediaItems, queueManager.currentQueue)
        assertEquals(0, queueManager.state.value.currentIndex)
    }

    @Test
    fun `setQueue with shuffle true rotates and shuffles`() = runTest {
        val startIndex = 1
        queueManager.toggleShuffle() // becomes true
        queueManager.setQueue(mediaItems, startIndex)
        
        assertEquals(0, queueManager.state.value.currentIndex)
        assertEquals(mediaItems[startIndex], queueManager.currentQueue[0])
        assertEquals(mediaItems.size, queueManager.currentQueue.size)
    }

    @Test
    fun `disableShuffle restores original order and correct index`() = runTest {
        queueManager.setQueue(mediaItems, 0)
        queueManager.enableShuffle()
        
        val restoredIndex = queueManager.disableShuffle()
        
        assertEquals(mediaItems, queueManager.currentQueue)
        assertEquals(0, restoredIndex)
    }

    @Test
    fun `getCurrentContextMediaId generates correct format`() = runTest {
        // Need to set a song so queueManager.state.value.current is not null
        // But current is calculated from queue and currentIndex
        queueManager.setQueue(mediaItems, 1, "album")
        val mediaId = queueManager.getCurrentContextMediaId()
        // Format: "${currentContextPrefix}#${currentOriginalIndex}|$currentSongId"
        // mediaItems[1] has id "2" (Wait, Song.fromMediaItem uses mediaId)
        assertTrue(mediaId.startsWith("album#1|"))
    }
}
