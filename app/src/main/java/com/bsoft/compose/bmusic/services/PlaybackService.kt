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
import com.bsoft.compose.bmusic.data.repositories.SongRepository
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private data class FetchData(val mediaItems: List<MediaItem>, val index: Int = 0)

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

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        exoPlayer.repeatMode = queueManager.state.value.repeatMode
        exoPlayer.addListener(object: Player.Listener{
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                super.onAudioSessionIdChanged(audioSessionId)
                equalizerManager.attach(audioSessionID = audioSessionId)
            }
        })
        //equalizerManager.attach(audioSessionID = exoPlayer.audioSessionId)
        mediaLibrarySession = MediaLibrarySession.Builder(this, exoPlayer, LibraryCallback()).build()
    }

    // Remember to release the player and media session in onDestroy
    override fun onDestroy() {
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
                            val id = parentId.removePrefix("albums_").toLong()
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
            val song = songRepository.findSongById(mediaId.toLong())
            val item = song?.toMediaItem() ?: MediaItem.EMPTY

            return Futures.immediateFuture(LibraryResult.ofItem(item, null))
        }

        private fun fetch(id: String): FetchData?{
            Log.d("fetching id data", id)
            val splits = id.split("_")
            if(splits.size >= 2){
                val name = splits.first()
                val id: Long? = if(splits.size >= 3) splits[1].toLong() else null
                val index = (if (splits.size >= 3) splits[2] else splits[1]).toInt()
                val resolvedItems = when(name){
                    "songs" -> songRepository.songs.map { it.toMediaItem() }
                    "album" -> {
                        id?.let {
                            songRepository.findSongsByAlbumId(it).map { song-> song.toMediaItem() }
                        }
                    }
                    "artist" -> {
                        id?.let {
                            songRepository.findArtistDetailsByArtistId(it).songs.map { song-> song.toMediaItem() }
                        }
                    }
                    else -> null
                } ?: emptyList()

                return FetchData(mediaItems = resolvedItems, index = index)
            }
            return null
        }

        @OptIn(UnstableApi::class)
        override fun onSetMediaItems(
            mediaSession: MediaSession, controller: MediaSession.ControllerInfo, mediaItems: List<MediaItem>, startIndex: Int, startPositionMs: Long
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
            val first = mediaItems.first()
            val data = fetch(first.mediaId)
            if(data == null){
                return Futures.immediateFuture(
                    MediaSession.MediaItemsWithStartPosition(mediaItems, startIndex, startPositionMs)
                )
            }else{
                val newIndex = queueManager.setQueue(data.mediaItems, data.index)
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
                        player.setMediaItems(queueManager.currentQueue, newIndex,position)
                        player.prepare()
                        //player.play()
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