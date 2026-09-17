package com.bsoft.compose.bmusic.services

import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.bsoft.compose.bmusic.data.EqualizerManager
import com.bsoft.compose.bmusic.data.QueueManager
import com.bsoft.compose.bmusic.data.repositories.PlaylistRepository
import com.bsoft.compose.bmusic.data.repositories.SongRepository
import com.bsoft.compose.bmusic.data.repositories.FavouriteRepository
import com.bsoft.compose.bmusic.data.repositories.PlayerCounterRepository
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.data.preferences.AppSettingsPreferences
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private data class FetchData(val mediaItems: List<MediaItem>, val index: Int = 0, val contextPrefix: String = "songs")

@AndroidEntryPoint
class PlaybackService: MediaLibraryService() {
    private lateinit var mediaLibrarySession: MediaLibrarySession

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var songRepository: SongRepository

    @Inject
    lateinit var queueManager: QueueManager

    @Inject
    lateinit var equalizerManager: EqualizerManager

    @Inject
    lateinit var playerCounterRepository: PlayerCounterRepository

    @Inject
    lateinit var favouriteRepository: FavouriteRepository

    @Inject
    lateinit var playlistRepository: PlaylistRepository

    @Inject
    lateinit var appSettingsPreferences: AppSettingsPreferences

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var lastProcessedSongId: Long = -1

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        exoPlayer.repeatMode = queueManager.state.value.repeatMode
        exoPlayer.addListener(object: Player.Listener{
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                super.onAudioSessionIdChanged(audioSessionId)
                equalizerManager.attach(audioSessionID = audioSessionId)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.let { item ->
                    val song = Song.fromMediaItem(item)
                    if (song.id != 0L) {
                        // 1. Avoid double-counting and redundant refreshes during shuffle or playlist shifts.
                        // We only proceed if the song ID changed, or if it's an explicit repeat/auto-advance.
                        if (song.id != lastProcessedSongId || reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT || reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                            lastProcessedSongId = song.id
                            
                            serviceScope.launch {
                                // 2. Record play count (Updates DB, triggering UI refreshes)
                                playerCounterRepository.incrementCount(song)

                                // 3. Sync queue manager with the player's true position
                                queueManager.updateCurrentIndex(exoPlayer.currentMediaItemIndex)

                                // 4. Persist state for restoration
                                if (queueManager.currentQueue.isNotEmpty()) {
                                    appSettingsPreferences.setLastPlayedMediaId(queueManager.getCurrentContextMediaId())
                                }

                                // 5. Reactive Refresh for Recently Played: 
                                // If we are in this dynamic context, we must rebuild the queue 
                                // so that the "Next" songs are based on the updated DB order.
                                val currentContext = queueManager.getCurrentContextMediaId().split("#")[0]
                                if (currentContext == "recently_played") {
                                    val newEntities = playerCounterRepository.getLastPlayed().first()
                                    val newItems = newEntities.mapNotNull { entity ->
                                        songRepository.findSongById(entity.song)?.toMediaItem()
                                    }

                                    val foundIdx = newItems.indexOfFirst { it.mediaId == song.id.toString() }
                                    if (foundIdx != -1) {
                                        queueManager.setQueue(newItems, foundIdx, "recently_played")
                                        // Update the player's queue without interrupting current playback
                                        exoPlayer.setMediaItems(newItems, foundIdx, exoPlayer.currentPosition)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
        mediaLibrarySession = MediaLibrarySession.Builder(this, exoPlayer, LibraryCallback()).build()
    }

    // Remember to release the player and media session in onDestroy
    override fun onDestroy() {
        serviceJob.cancel()
        mediaLibrarySession.run {
            player.release()
            release()
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return mediaLibrarySession
    }

    private inner class LibraryCallback : MediaLibrarySession.Callback {
        @OptIn(UnstableApi::class)
        override fun onConnect(session: MediaSession, controller: MediaSession.ControllerInfo): MediaSession.ConnectionResult {
            // 1. Grab the default allowed commands
            val sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
                .buildUpon()
                // 2. Explicitly register your custom shuffle command
                .add(SessionCommand(SHUFFLE_COMMAND, Bundle.EMPTY))
                .build()

            // 3. Return the AcceptedResultBuilder containing your updated commands list
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session, controller)
                .setAvailableSessionCommands(sessionCommands)
                .build()
        }

        override fun onGetLibraryRoot(session: MediaLibrarySession, browser: MediaSession.ControllerInfo, params: LibraryParams?): ListenableFuture<LibraryResult<MediaItem>> {

            val rootItem = MediaItem.Builder().setMediaId("root")
                .setMediaMetadata(MediaMetadata.Builder().setIsBrowsable(true).setTitle("Library").build()).build()

            return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
        }

        private fun browsableItem(id: String, title: String): MediaItem {
            return MediaItem.Builder().setMediaId(id)
                .setMediaMetadata(
                    MediaMetadata.Builder().setTitle(title).setIsBrowsable(true).build()
                ).build()
        }

        override fun onGetChildren(
            session: MediaLibrarySession, browser: MediaSession.ControllerInfo, parentId: String, page: Int, pageSize: Int, params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {

            val items = when (parentId) {
                "root" -> listOf(
                    browsableItem("songs", "Songs"),
                    browsableItem("albums", "Albums"),
                    browsableItem("artists", "Artists")
                )
                "songs" -> songRepository.songs.map { it.toMediaItem() }
                "albums" -> songRepository.albums.map { it.toMediaItem() }
                "artists" -> songRepository.artists.map { it.toMediaItem() }
                else -> {
                    when {
                        parentId.startsWith("album_") -> {
                            val id = parentId.removePrefix("album_").toLong()
                            songRepository.findSongsByAlbumId(id)
                        }
                        parentId.startsWith("artist_") -> {
                            val id = parentId.removePrefix("artist_").toLong()
                            songRepository.findArtistDetailsByArtistId(id).songs
                        }
                        else -> emptyList()
                    }.map { it.toMediaItem() }
                }
            }

            return Futures.immediateFuture(
                LibraryResult.ofItemList(items, params)
            )
        }

        override fun onGetItem(
            session: MediaLibrarySession, browser: MediaSession.ControllerInfo, mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val id = mediaId.toLongOrNull()
            val song = id?.let { songRepository.findSongById(it) }
            val item = song?.toMediaItem() ?: MediaItem.EMPTY

            return Futures.immediateFuture(LibraryResult.ofItem(item, null))
        }

        private fun fetch(id: String): FetchData?{
            Log.d("fetching id data", id)
            // 1. Separate target song ID for positioning
            val parts = id.split("|")
            val contextWithIndex = parts[0]
            val targetSongId = if (parts.size > 1) parts[1].toLongOrNull() else null

            // 2. Separate prefix from index using the new '#' delimiter
            val contextParts = contextWithIndex.split("#")
            if (contextParts.size < 2) return null

            val contextPrefix = contextParts[0]
            val rawIndex = contextParts[1].toIntOrNull() ?: 0

            // 3. Parse prefix into name and optional resource ID (for albums/artists)
            val prefixSplits = contextPrefix.split("_")
            val name = prefixSplits[0]
            val idVal: Long? = if (prefixSplits.size >= 2) prefixSplits[1].toLongOrNull() else null

            val resolvedItems = when(name){
                "songs" -> songRepository.songs.map { it.toMediaItem() }
                "album" -> {
                    idVal?.let {
                        songRepository.findSongsByAlbumId(it).map { song-> song.toMediaItem() }
                    }
                }
                "artist" -> {
                    idVal?.let {
                        songRepository.findArtistDetailsByArtistId(it).songs.map { song-> song.toMediaItem() }
                    }
                }
                "recently" -> { // Handles "recently_played" and "recently_added"
                    if (contextPrefix == "recently_played") {
                        runBlocking {
                            playerCounterRepository.getLastPlayed().first().mapNotNull { entity ->
                                songRepository.findSongById(entity.song)?.toMediaItem()
                            }
                        }
                    } else if (contextPrefix == "recently_added") {
                        songRepository.last.map { it.toMediaItem() }
                    } else null
                }
                "most" -> { // Handles "most_played"
                    if (contextPrefix == "most_played") {
                        runBlocking {
                            playerCounterRepository.getMostPlayed().first().mapNotNull { entity ->
                                songRepository.findSongById(entity.song)?.toMediaItem()
                            }
                        }
                    } else null
                }
                "favourites" -> {
                    runBlocking {
                        favouriteRepository.getAllFavorites().first().mapNotNull { entity ->
                            songRepository.findSongById(entity.song)?.toMediaItem()
                        }
                    }
                }
                "playlist" -> {
                    idVal?.let { playlistId ->
                        runBlocking {
                            playlistRepository.getSongsFromPlayList(playlistId).map { playlistSong ->
                                playlistSong.song.toMediaItem()
                            }
                        }
                    }
                }
                else -> null
            } ?: emptyList()

            // 4. Resolve final index (target song ID takes priority over raw index)
            val finalIndex = if (targetSongId != null) {
                val foundIdx = resolvedItems.indexOfFirst { it.mediaId == targetSongId.toString() }
                if (foundIdx != -1) foundIdx else rawIndex
            } else {
                rawIndex
            }

            return FetchData(mediaItems = resolvedItems, index = finalIndex, contextPrefix = contextPrefix)
        }

        @OptIn(UnstableApi::class)
        override fun onSetMediaItems(
            mediaSession: MediaSession, controller: MediaSession.ControllerInfo, mediaItems: List<MediaItem>, startIndex: Int, startPositionMs: Long
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
            if (mediaItems.isEmpty()) {
                return super.onSetMediaItems(mediaSession, controller, mediaItems, startIndex, startPositionMs)
            }
            val first = mediaItems.first()
            val data = fetch(first.mediaId)
            if(data == null){
                val newIndex = queueManager.setQueue(mediaItems, startIndex, "playlist")
                return Futures.immediateFuture(
                    MediaSession.MediaItemsWithStartPosition(queueManager.currentQueue, newIndex, startPositionMs)
                )
            }else{
                val newIndex = queueManager.setQueue(data.mediaItems, data.index, data.contextPrefix)
                return Futures.immediateFuture(
                    MediaSession.MediaItemsWithStartPosition(queueManager.currentQueue, newIndex, startPositionMs)
                )
            }
        }

        override fun onSearch(
            session: MediaLibrarySession, browser: MediaSession.ControllerInfo, query: String, params: LibraryParams?
        ): ListenableFuture<LibraryResult<Void>> {
            return Futures.immediateFuture(LibraryResult.ofVoid())
        }

        override fun onGetSearchResult(
            session: MediaLibrarySession, browser: MediaSession.ControllerInfo, query: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            val results = songRepository.search(query).map { it.toMediaItem() }

            return Futures.immediateFuture(LibraryResult.ofItemList(results, params))
        }

        override fun onCustomCommand(
            session: MediaSession, controller: MediaSession.ControllerInfo, customCommand: SessionCommand, args: Bundle
        ): ListenableFuture<SessionResult> {
            when(customCommand.customAction){
                SHUFFLE_COMMAND ->{
                    val position = mediaLibrarySession.player.currentPosition
                    val newIndex = when(args.getString(SHUFFLE_COMMAND_ARGS_SET)){
                        SHUFFLE_COMMAND_ON -> queueManager.enableShuffle()
                        SHUFFLE_COMMAND_OFF -> queueManager.disableShuffle()
                        else -> queueManager.toggleShuffle()
                    }

                    mediaLibrarySession.run {
                        player.setMediaItems(queueManager.currentQueue, newIndex, position)
                        player.prepare()
                    }
                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
            }
            return super.onCustomCommand(session, controller, customCommand, args)
        }
    }

    companion object {
        const val SHUFFLE_COMMAND = "toggle_command"
        const val SHUFFLE_COMMAND_ARGS_SET = "toggle_command_set"
        const val SHUFFLE_COMMAND_ON = "toggle_command_on"
        const val SHUFFLE_COMMAND_OFF = "toggle_command_off"
    }
}