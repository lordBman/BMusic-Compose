package com.bsoft.compose.bmusic.data.states

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.bsoft.compose.bmusic.data.models.Song

data class QueueState(
    val queue: List<MediaItem> = emptyList(),
    val currentIndex: Int? = null,
    val shuffle: Boolean = false,
    val repeatMode: @Player.RepeatMode Int = Player.REPEAT_MODE_ALL
){
    val current: Song?
        get(){
            if(currentIndex == null || currentIndex < 0 || queue.isEmpty()){
                return  null
            }
            return Song.fromMediaItem((queue[currentIndex]))
        }
}