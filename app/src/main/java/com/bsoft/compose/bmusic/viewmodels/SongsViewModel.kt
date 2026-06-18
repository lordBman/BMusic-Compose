package com.bsoft.compose.bmusic.viewmodels

import android.app.Application
import android.content.Context
import android.provider.MediaStore
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.bsoft.compose.bmusic.BMusicApp
import com.bsoft.compose.bmusic.SongsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class SongsViewModel(application: Application): AndroidViewModel(application) {
    private val app = application.applicationContext as BMusicApp

    private val mutableState = MutableStateFlow(SongsState(songs = app.songs))
    val state = mutableState.asStateFlow()

    /*val player = ExoPlayer.Builder(app).build()

    init {
        player.prepare()
    }

    override fun onCleared() {
        player.release()
    }*/
}