package com.bsoft.compose.bmusic.viewmodels

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.bsoft.compose.bmusic.BMusicApp
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.data.states.PlayingState
import com.bsoft.compose.bmusic.services.PlaybackService
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class PlayingViewModel(application: Application): AndroidViewModel(application) {
    private val app = application.applicationContext as BMusicApp

    private val mutableState = MutableStateFlow(PlayingState())
    val state = mutableState.asStateFlow()

    private var mediaBrowser: MediaBrowser? = null

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            mediaBrowser?.let { player ->
                if (player.isPlaying) {
                    mutableState.update { it.copy(position = player.currentPosition, playing = player.isPlaying) }

                    // Poll again in 1000ms (or 16ms for smooth 60fps video trackers)
                    handler.postDelayed(this, 200)
                }
            }
        }
    }

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
            if (playing) {
                handler.post(updateProgressRunnable)
            } else {
                handler.removeCallbacks(updateProgressRunnable)
            }
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

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            when(repeatMode){
                Player.REPEAT_MODE_ALL -> mutableState.update { it.copy(repeatMode = PlayingState.RepeatMode.All) }
                Player.REPEAT_MODE_ONE -> mutableState.update { it.copy(repeatMode = PlayingState.RepeatMode.Single) }
                Player.REPEAT_MODE_OFF -> mutableState.update { it.copy(repeatMode = PlayingState.RepeatMode.Disabled) }
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            mutableState.update { it.copy(shuffle = shuffleModeEnabled) }
        }
    }

    fun initializeMediaController(context: Context) {
        if (mediaBrowser == null) {
            val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
            val controllerFuture = MediaBrowser.Builder(context, sessionToken).buildAsync()
            val item = MediaItem.Builder().setMediaId("songs")
                .setMediaMetadata(
                    MediaMetadata.Builder().setTitle("Songs").setIsBrowsable(true).build()
                ).build()
            controllerFuture.addListener({
                mediaBrowser = controllerFuture.get()
                mediaBrowser?.addListener(playerListener)
                mediaBrowser?.setMediaItem(item)
                mediaBrowser?.seekToDefaultPosition(0)
                mediaBrowser?.prepare()
            }, MoreExecutors.directExecutor())
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

    fun playLibrary(mediaItem: MediaItem, index: Int = 0){
        mediaBrowser?.apply {
            setMediaItem(mediaItem)
            seekToDefaultPosition(index)
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        mediaBrowser?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun next() {
        mediaBrowser?.let {
            if(it.hasNextMediaItem()){
                it.seekToNextMediaItem()
            }
        }
    }

    fun forward(){
        mediaBrowser?.let{
            it.seekForward()
            it.seekTo(it.currentPosition + 10000)
        }
    }

    fun previous() {
        mediaBrowser?.let {
            if(it.hasPreviousMediaItem()){
                it.seekToPreviousMediaItem()
            }
        }
    }

    fun rewind(){
        mediaBrowser?.let{
            it.seekForward()
            it.seekTo(it.currentPosition - 10000)
        }
    }

    fun seek(position: Long){
        mediaBrowser?.seekTo(position)
    }

    fun toggleRepeat(){
        mediaBrowser?.let { player ->
            if(player.availableCommands.contains(Player.COMMAND_SET_REPEAT_MODE)){
                when(player.repeatMode){
                    Player.REPEAT_MODE_ALL -> {
                        player.repeatMode = Player.REPEAT_MODE_ONE
                    }
                    Player.REPEAT_MODE_ONE -> {
                        player.repeatMode = Player.REPEAT_MODE_OFF
                    }
                    Player.REPEAT_MODE_OFF -> {
                        player.repeatMode = Player.REPEAT_MODE_ALL
                    }
                }
            }
        }
    }

    fun toggleShuffle(){
        mediaBrowser?.let { player ->
            if(player.availableCommands.contains(Player.COMMAND_SET_REPEAT_MODE)){
                player.shuffleModeEnabled = !player.shuffleModeEnabled
            }
        }
    }

    override fun onCleared() {
        mediaBrowser?.release()
        mediaBrowser = null
    }
}