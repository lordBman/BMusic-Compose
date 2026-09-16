package com.bsoft.compose.bmusic.viewmodels

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import com.bsoft.compose.bmusic.data.QueueManager
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.data.preferences.AppSettingsPreferences
import com.bsoft.compose.bmusic.data.repositories.FavouriteRepository
import com.bsoft.compose.bmusic.data.repositories.PlayerCounterRepository
import com.bsoft.compose.bmusic.data.states.PlayingState
import com.bsoft.compose.bmusic.data.states.QueueState
import com.bsoft.compose.bmusic.services.PlaybackService
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayingViewModel @Inject constructor(
    private val queueManager: QueueManager,
    private val playerCounterRepository: PlayerCounterRepository,
    private val favouriteRepository: FavouriteRepository,
    private val appSettingsPreferences: AppSettingsPreferences
) : ViewModel() {

    private val mutableState = MutableStateFlow(PlayingState())
    val state = mutableState.asStateFlow()

    val isCurrentTrackFavorite: Flow<Boolean> = combine(
        queueManager.state,
        favouriteRepository.getAllFavorites()
    ) { queueState, favorites ->
        val currentId = queueState.current?.id
        favorites.any { it.song == currentId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val getLastPlayed = playerCounterRepository.getLastPlayed().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    val getMostPlayed = playerCounterRepository.getMostPlayed().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    val queueState = queueManager.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = QueueState()
    )

    init {
        viewModelScope.launch {
            queueManager.state.collect {
                appSettingsPreferences.setLastShuffleMode(it.shuffle)
            }
        }
    }

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
                Player.STATE_ENDED -> {
                    /*queueManager.state.value.current?.let {
                        viewModelScope.launch {
                            playerCounterRepository.incrementCount(it)
                        }
                    }*/
                }
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
            mediaItem?.let{ item ->
                mediaBrowser?.let { browser ->
                    queueManager.updateCurrentIndex(browser.currentMediaItemIndex)
                    queueManager.state.value.current?.let { currentSong ->
                        viewModelScope.launch {
                            playerCounterRepository.incrementCount(currentSong)
                        }
                    }
                    if (queueManager.currentQueue.isNotEmpty()) {
                        appSettingsPreferences.setLastPlayedMediaId(queueManager.getCurrentContextMediaId())
                    }
                }
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            queueManager.setRepeatMode(repeatMode)
            appSettingsPreferences.setLastRepeatMode(repeatMode)
        }
    }

    fun toggleFavorite() {
        queueManager.state.value.current?.let { song ->
            viewModelScope.launch {
                favouriteRepository.toggleFavourite(song)
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
                
                // Restore last played queue and song selection perfectly on startup if configured
                viewModelScope.launch {
                    appSettingsPreferences.data.collect { settings ->
                        val savedMediaId = settings.lastPlayedMediaId
                        if (!savedMediaId.isNullOrEmpty() && mediaBrowser?.currentMediaItem == null) {
                            val initialItem = MediaItem.Builder().setMediaId(savedMediaId)
                                .setMediaMetadata(
                                    MediaMetadata.Builder().setIsBrowsable(true).build()
                                ).build()
                            mediaBrowser?.setMediaItem(initialItem)
                            mediaBrowser?.repeatMode = settings.lastRepeatMode
                            mediaBrowser?.prepare()
                        } else if (mediaBrowser?.currentMediaItem == null) {
                            val defaultItem = MediaItem.Builder().setMediaId("songs_0")
                                .setMediaMetadata(
                                    MediaMetadata.Builder().setTitle("Songs").setIsBrowsable(true).build()
                                ).build()
                            mediaBrowser?.setMediaItem(defaultItem)
                            mediaBrowser?.prepare()
                        }
                    }
                }
            }, MoreExecutors.directExecutor())
        }
    }

    fun playSong(index: Int) {
        val item = MediaItem.Builder().setMediaId("songs_${index}")
        .setMediaMetadata(
            MediaMetadata.Builder().setTitle("Songs").setIsBrowsable(true).build()
        ).build()

        this.playLibrary(item)
    }

    fun playLibrary(mediaItem: MediaItem){
        mediaBrowser?.apply {
            setMediaItem(mediaItem)
            prepare()
            sendCustomCommand(
                SessionCommand(PlaybackService.SHUFFLE_COMMAND, Bundle.EMPTY),
                Bundle.EMPTY
            )
            play()
        }
    }

    fun playLibraryAll(mediaItem: MediaItem, shuffle: Boolean){
        val bundle = Bundle()
        shuffle.let {
            bundle.putString(
                PlaybackService.SHUFFLE_COMMAND_ARGS_SET,
                if(it) PlaybackService.SHUFFLE_COMMAND_ON else PlaybackService.SHUFFLE_COMMAND_OFF)
        }

        mediaBrowser?.apply {
            repeatMode = Player.REPEAT_MODE_ALL
            setMediaItem(mediaItem)
            prepare()
            sendCustomCommand(
                SessionCommand(PlaybackService.SHUFFLE_COMMAND, Bundle.EMPTY),
                bundle
            )
            play()
        }
    }

    fun playLibraryList(songs: List<Song>, startIndex: Int, shuffle: Boolean) {
        val mediaItems = songs.map { it.toMediaItem() }
        if (shuffle) {
            val shuffledItems = mediaItems.shuffled()
            mediaBrowser?.setMediaItems(shuffledItems, 0, 0L)
        } else {
            mediaBrowser?.setMediaItems(mediaItems, startIndex, 0L)
        }
        mediaBrowser?.prepare()
        mediaBrowser?.play()
    }

    fun playPlaylistIndex(index: Int){
        mediaBrowser?.apply {
            seekToDefaultPosition(index)
            play()
        }
    }

    fun togglePlayPause() {
        mediaBrowser?.apply {
            if (isPlaying) pause() else play()
        }
    }

    fun next() {
        mediaBrowser?.apply {
            if(hasNextMediaItem()){
                seekToNextMediaItem()
            }
        }
    }

    fun forward(){
        mediaBrowser?.apply{
            seekForward()
            seekTo(currentPosition + 10000)
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
        mediaBrowser?.sendCustomCommand(
            SessionCommand(PlaybackService.SHUFFLE_COMMAND, Bundle.EMPTY),
            Bundle.EMPTY
        )
    }

    override fun onCleared() {
        mediaBrowser?.release()
        mediaBrowser = null
    }
}