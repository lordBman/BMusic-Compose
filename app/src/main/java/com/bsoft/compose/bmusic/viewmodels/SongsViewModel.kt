package com.bsoft.compose.bmusic.viewmodels

import android.app.Application
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import com.bsoft.compose.bmusic.BMusicApp
import com.bsoft.compose.bmusic.data.SongsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SongsViewModel(application: Application): AndroidViewModel(application) {
    private val app = application.applicationContext as BMusicApp

    val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            query()
        }
    }

    private val mutableState = MutableStateFlow(SongsState())
    val state = mutableState.asStateFlow()

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

    fun onStoragePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            this.query()
        } else {
            mutableState.update { it.copy( loaded = true, error = "Storage permission required to load song list") }
        }
    }

    override fun onCleared() {
        super.onCleared()
        app.contentResolver.unregisterContentObserver(observer)
    }
}