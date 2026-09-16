package com.bsoft.compose.bmusic.data

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.bsoft.compose.bmusic.data.states.QueueState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class QueueManager {
    private var currentContextPrefix: String = "songs"

    private var _originalQueue: List<MediaItem> =  emptyList()
    val originalQueue: List<MediaItem>
        get() = _originalQueue

    private val _state = MutableStateFlow(QueueState())
    val state: StateFlow<QueueState> = _state.asStateFlow()
    val currentQueue: List<MediaItem>
        get() = _state.value.queue

    val currentOriginalIndex: Int
        get() {
            val idx = _state.value.currentIndex ?: return 0
            if (currentQueue.isEmpty() || idx !in currentQueue.indices) return 0
            return _originalQueue.indexOf(currentQueue[idx]).coerceAtLeast(0)
        }

    private fun rotateAndShuffle(list: List<MediaItem>, targetIndex: Int): List<MediaItem> {
        if (list.isEmpty()) return list
        require(targetIndex in list.indices) { "Index out of bounds" }

        val result = list.toMutableList()
        val targetItem = result.removeAt(targetIndex)

        result.shuffle()
        result.add(0, targetItem)

        return result
    }

    fun setQueue(items: List<MediaItem>, startIndex: Int, contextPrefix: String = "songs"): Int {
        this.currentContextPrefix = contextPrefix
        _originalQueue = items
        if(_state.value.shuffle && startIndex >= 0 ){
            val finalList = rotateAndShuffle(list = items, targetIndex = startIndex)

            _state.update { it.copy(queue = finalList, currentIndex = 0) }
            return 0
        }else{
            _state.update { it.copy(queue = items, currentIndex = startIndex) }
            return startIndex
        }
    }

    fun getCurrentContextMediaId(): String {
        return "${currentContextPrefix}_${currentOriginalIndex}"
    }

    fun updateCurrentIndex(index: Int) {
        _state.update { it.copy(currentIndex = index) }
    }

    fun toggleShuffle(): Int{
        val shuffle = !_state.value.shuffle
        return if(shuffle){
            enableShuffle()
        }else{
            disableShuffle()
        }
    }

    fun enableShuffle(): Int{
        val currentIdx = currentOriginalIndex
        _state.update { it.copy(shuffle = true) }
        _state.update {
            it.copy(
                queue = rotateAndShuffle(list = originalQueue, targetIndex = currentIdx),
                currentIndex = 0)
        }

        return 0
    }

    fun disableShuffle(): Int{
        val currentIdx = currentOriginalIndex
        _state.update { it.copy(shuffle = false) }
        _state.update { it.copy(queue = originalQueue, currentIndex = currentIdx) }

        return currentIdx
    }

    fun setRepeatMode(mode: @Player.RepeatMode Int){
        _state.update { it.copy(repeatMode = mode) }
    }
}