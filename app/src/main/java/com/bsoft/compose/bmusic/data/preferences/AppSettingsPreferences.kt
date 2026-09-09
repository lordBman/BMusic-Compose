package com.bsoft.compose.bmusic.data.preferences

import android.content.Context
import com.bsoft.compose.bmusic.data.datastores.appSettingsDataStore
import com.bsoft.compose.bmusic.data.serializables.AppSettingsData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AppSettingsPreferences(private val context: Context) {
    val data: Flow<AppSettingsData>
        get() = context.appSettingsDataStore.data

    fun setAccentColor(color: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            context.appSettingsDataStore.updateData { it.copy(accentColor = color) }
        }
    }

    fun setLanguage(language: String) {
        CoroutineScope(Dispatchers.IO).launch {
            context.appSettingsDataStore.updateData { it.copy(language = language) }
        }
    }

    fun setRememberLastTab(remember: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            context.appSettingsDataStore.updateData { it.copy(rememberLastTab = remember) }
        }
    }

    fun setHideShortSongs(hide: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            context.appSettingsDataStore.updateData { it.copy(hideShortSongs = hide) }
        }
    }

    fun setShakeToChange(shake: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            context.appSettingsDataStore.updateData { it.copy(shakeToChange = shake) }
        }
    }

    fun setSoundFade(fade: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            context.appSettingsDataStore.updateData { it.copy(soundFade = fade) }
        }
    }
}
