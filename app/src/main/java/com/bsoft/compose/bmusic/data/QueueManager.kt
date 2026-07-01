package com.bsoft.compose.bmusic.data

import androidx.media3.common.MediaItem
import com.bsoft.compose.bmusic.data.models.QueueEntry
import com.bsoft.compose.bmusic.data.models.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.emptyList

class QueueManager {
    private val _queue = MutableStateFlow<List<QueueEntry>>(emptyList())
    val queue: StateFlow<List<QueueEntry>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    fun setQueue(songs: List<Song>, startIndex: Int) {
        _queue.value = songs.map {
            QueueEntry(song = it, mediaItem = it.toMediaItem())
        }
        _currentIndex.value = startIndex
    }

    fun updateCurrentIndex(index: Int) {
        _currentIndex.value = index
    }

    fun currentQueue(): List<MediaItem> = _queue.value.map { it.mediaItem }
}