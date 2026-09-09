package com.bsoft.compose.bmusic.data.serializables

import kotlinx.serialization.Serializable

@Serializable
data class EqualizerPreferencesData(val isEqualizerEnabled: Boolean = false, val selected: Short = 0, val values: List<Short> = emptyList()) {
    companion object{
    }
}