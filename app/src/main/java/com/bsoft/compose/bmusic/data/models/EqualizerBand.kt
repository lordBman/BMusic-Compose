package com.bsoft.compose.bmusic.data.models

data class EqualizerBand(
    val band: Short,
    val frequency: Int,
    val level: Short,
    val min: Short,
    val max: Short
)
