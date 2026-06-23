package com.bsoft.compose.bmusic.viewmodels

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.bsoft.compose.bmusic.data.Album
import com.bsoft.compose.bmusic.data.PlayingState
import com.bsoft.compose.bmusic.data.Song
import com.bsoft.compose.bmusic.services.PlaybackService
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlayingViewModel: ViewModel() {
    private val mutableState = MutableStateFlow(PlayingState())
    val state = mutableState.asStateFlow()

    private var mediaBrowser: MediaBrowser? = null

    val playerListener = object : Player.Listener {

        // Triggered when the player starts buffering, becomes ready, or ends
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> { /* Show loading spinner */ }
                Player.STATE_READY -> { /* Hide loading spinner */ }
                Player.STATE_ENDED -> { /* Play next video or loop */ }
                Player.STATE_IDLE -> { /* Player stopped or failed */ }
            }
        }

        // Triggered when play/pause changes
        override fun onIsPlayingChanged(playing: Boolean) {
            mutableState.update { it.copy(playing = playing) }
        }

        // Triggered when a critical playback error occurs
        override fun onPlayerError(error: PlaybackException) {
            // Handle error (e.g., network failure, bad file format)
        }

        // Triggered when moving to a new song/video in the playlist
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            // Update track metadata like title or artwork in UI
            mediaItem?.let{ item ->
                if (item.mediaMetadata.isPlayable == true){
                    mutableState.update { it.copy(current = Song.fromMediaItem(item)) }
                }
            }
        }
    }

    fun initializeMediaController(context: Context) {
        if (mediaBrowser == null) {
            val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
            val controllerFuture = MediaBrowser.Builder(context, sessionToken).buildAsync()
            controllerFuture.addListener({
                mediaBrowser = controllerFuture.get()
                mediaBrowser?.addListener(playerListener)
            }, MoreExecutors.directExecutor())
        }
    }

    fun playMedia(song: Song) {
        mediaBrowser?.apply {
            setMediaItem(song.toMediaItem())
            prepare()
            play()
        }
    }

    fun playSong(index: Int) {
        val item = MediaItem.Builder().setMediaId("songs")
        .setMediaMetadata(
            MediaMetadata.Builder().setTitle("Songs").setIsBrowsable(true).build()
        ).build()

        mediaBrowser?.apply {
            setMediaItem(item)
            seekToDefaultPosition(index)
            prepare()
            play()
        }
    }

    fun playLibrary(mediaItem: MediaItem){
        mediaBrowser?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        mediaBrowser?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    override fun onCleared() {
        mediaBrowser?.release()
        mediaBrowser = null
        super.onCleared()
    }
}