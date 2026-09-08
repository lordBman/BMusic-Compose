package com.bsoft.compose.bmusic.viewmodels

import android.app.Application
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Size
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bsoft.compose.bmusic.BMusicApp
import com.bsoft.compose.bmusic.data.entities.PlaylistEntity
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.data.repositories.FavouriteRepository
import com.bsoft.compose.bmusic.data.repositories.PlaylistRepository
import com.bsoft.compose.bmusic.data.states.AlbumState
import com.bsoft.compose.bmusic.data.states.ArtistState
import com.bsoft.compose.bmusic.data.states.SongsState
import com.bsoft.compose.bmusic.utils.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    application: Application,
    val favouriteRepository: FavouriteRepository,
    val playlistRepository: PlaylistRepository): AndroidViewModel(application) {
    private val app = application.applicationContext as BMusicApp

    val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            query()
        }
    }

    val favourites = favouriteRepository.getAllFavorites().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    val playlists = playlistRepository.getPlayLists().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    private val mutableState = MutableStateFlow(SongsState())
    val state = mutableState.asStateFlow()

    private val mutableAlbumState = MutableStateFlow(AlbumState())
    val albumState = mutableAlbumState.asStateFlow()

    private val mutableArtistState = MutableStateFlow(ArtistState())
    val artistState = mutableArtistState.asStateFlow()

    init {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        app.contentResolver.registerContentObserver(uri, true, observer)
    }

    fun query(){
        mutableState.update { it.copy(message = "Fetch songs, please wait", loaded = false) }
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val init = app.loadSongsState()
                mutableState.update { init }
            }catch (_: Exception){
                mutableState.update { it.copy( loaded = true, error = "Unexpected error encountered, try again") }
            }
        }
    }

    fun loadSongsByID(song: Long): Song? {
        return app.songRepository.findSongById(song)
    }

    fun queryAlbum(albumId: Long){
        mutableAlbumState.update { AlbumState() }
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val album = app.songRepository.findAlbumById(albumId)
                if(album == null){
                    mutableAlbumState.update { it.copy( loaded = true, error = "Album not found with id: $albumId") }
                }else{
                    val init = app.loadSongsByAlbum(albumId)
                    mutableAlbumState.update { it.copy(loaded = true, songs = init, album = album) }
                    mutableAlbumState.value.album?.let {
                        val bitmap = Util.loadArtwork(app, it.artworkUri, Size(300, 300))
                        mutableAlbumState.update { state -> state.copy(bitmap = bitmap) }
                    }
                }
            }catch (_: Exception){
                mutableAlbumState.update { it.copy( loaded = true, error = "Unexpected error encountered, try again") }
            }
        }
    }

    fun queryArtist(artistId: Long){
        mutableArtistState.update { ArtistState() }
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val artist = app.songRepository.findArtistById(artistId)
                if(artist == null){
                    mutableArtistState.update { it.copy( loaded = true, error = "Artist not found with id: $artistId") }
                }else{
                    val init = app.loadArtistDetails(artistId)
                    mutableArtistState.update { it.copy(loaded = true, songs = init.songs, albums = init.albums, artist = artist) }
                    mutableArtistState.value.artist?.let {
                        val bitmap = Util.loadArtwork(app, it.artworkUri, Size(300, 300))
                        mutableArtistState.update { state -> state.copy(bitmap = bitmap) }
                    }
                }
            }catch (_: Exception){
                mutableArtistState.update { it.copy( loaded = true, error = "Unexpected error encountered, try again") }
            }
        }
    }

    fun createPlaylist(name: String){
        CoroutineScope(Dispatchers.IO).launch {
            val playlist = PlaylistEntity(name = name)
            playlistRepository.createPlayList(playlist)
        }
    }

    fun deletePlaylist(playlist: Long){
        CoroutineScope(Dispatchers.IO).launch {
            playlistRepository.deletePlayList(playlist)
        }
    }

    fun addToPlaylist(playlist: Long, songs: List<Song>){
        CoroutineScope(Dispatchers.IO).launch {
            playlistRepository.insertSongsIntoPlayList(
                playlistSongEntities = songs.map { PlaylistEntity.fromSong(playlist, it) }
            )
        }
    }

    fun onStoragePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            this.query()
        } else {
            mutableState.update { it.copy( loaded = true, error = "Storage permission required to load song list") }
        }
    }

    override fun onCleared() {
        app.contentResolver.unregisterContentObserver(observer)
    }
}