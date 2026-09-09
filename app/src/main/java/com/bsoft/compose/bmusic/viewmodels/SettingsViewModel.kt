package com.bsoft.compose.bmusic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bsoft.compose.bmusic.data.preferences.AppSettingsPreferences
import com.bsoft.compose.bmusic.data.serializables.AppSettingsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsPreferences: AppSettingsPreferences
) : ViewModel() {

    val settings: StateFlow<AppSettingsData> = appSettingsPreferences.data.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettingsData()
    )

    fun setAccentColor(color: Int) = appSettingsPreferences.setAccentColor(color)
    fun setLanguage(language: String) = appSettingsPreferences.setLanguage(language)
    fun setRememberLastTab(remember: Boolean) = appSettingsPreferences.setRememberLastTab(remember)
    fun setHideShortSongs(hide: Boolean) = appSettingsPreferences.setHideShortSongs(hide)
    fun setShakeToChange(shake: Boolean) = appSettingsPreferences.setShakeToChange(shake)
    fun setSoundFade(fade: Boolean) = appSettingsPreferences.setSoundFade(fade)
}
