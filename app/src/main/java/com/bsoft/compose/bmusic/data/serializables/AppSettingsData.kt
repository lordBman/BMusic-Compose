package com.bsoft.compose.bmusic.data.serializables

import kotlinx.serialization.Serializable

@Serializable
data class AppSettingsData(
    val accentColor: Int = 0xFF2196F3.toInt(), // Default Blue
    val language: String = "en",
    val rememberLastTab: Boolean = true,
    val hideShortSongs: Boolean = false,
    val shakeToChange: Boolean = false,
    val soundFade: Boolean = false
)
