package com.bsoft.compose.bmusic.services

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.bsoft.compose.bmusic.data.QueueManager
import com.bsoft.compose.bmusic.data.repositories.SongRepository
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService: MediaLibraryService() {
    private lateinit var mediaLibrarySession: MediaLibrarySession

    @Inject
    lateinit var songRepository: SongRepository

    @Inject
    lateinit var queueManager: QueueManager

    override fun onCreate() {
        super.onCreate()

        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this))
            .build()
        mediaLibrarySession = MediaLibrarySession.Builder(this, player, LibraryCallback()).build()
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
                            songRepository.findSongsByArtistId(id)
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

        @OptIn(UnstableApi::class)
        override fun onSetMediaItems(
            mediaSession: MediaSession, controller: MediaSession.ControllerInfo, mediaItems: List<MediaItem>, startIndex: Int, startPositionMs: Long
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
            val first = mediaItems.first()
            val resolvedItems = when {
                first.mediaId == "songs" -> songRepository.songs.map { it.toMediaItem() }
                first.mediaId.startsWith("album_") -> {
                    val id = first.mediaId.removePrefix("album_").toLong()
                    songRepository.findSongsByAlbumId(id).map { it.toMediaItem() }
                }
                first.mediaId.startsWith("artist_") -> {
                    val id = first.mediaId.removePrefix("artist_").toLong()
                    songRepository.findSongsByArtistId(id).map { it.toMediaItem() }
                }
                else -> {
                    mediaItems
                }
            }

            return Futures.immediateFuture(
                MediaSession.MediaItemsWithStartPosition(resolvedItems, startIndex, startPositionMs)
            )
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
    }
}