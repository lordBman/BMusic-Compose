package com.bsoft.compose.bmusic.data.states

import com.bsoft.compose.bmusic.data.models.EqualizerBand
import com.bsoft.compose.bmusic.data.models.EqualizerPresent

data class EqualizerState(
    val bands: List<EqualizerBand> = emptyList(),
    val presets: List<EqualizerPresent> = emptyList())