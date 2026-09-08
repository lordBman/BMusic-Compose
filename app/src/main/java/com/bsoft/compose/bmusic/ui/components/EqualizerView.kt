package com.bsoft.compose.bmusic.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsoft.compose.bmusic.data.models.EqualizerPresent
import com.bsoft.compose.bmusic.data.states.EqualizerState
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun EqualizerView(modifier: Modifier = Modifier, enabled: Boolean = false, state: EqualizerState = EqualizerState(), selectedBand: Short, customValues: List<Short> = emptyList(), toggleEnable: ()-> Unit, bandModified: (Short, Short)-> Unit, selectedPreset: (EqualizerPresent)->Unit) {
    Column(modifier = modifier.fillMaxSize().padding(top = 30.dp, start = 20.dp, end = 20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {

        // Switch to enable/disable equalizer
        EqualizerSwitch(enabled = enabled){
            toggleEnable()
        }

        // Equalizer sliders (shown only when enabled)
        AnimatedVisibility(visible = enabled, enter = fadeIn() + slideInVertically(), exit = fadeOut() + slideOutVertically()) {
            EqualizerControls(bands = state.bands){
                    band, level -> bandModified(band, level)
            }
        }

        // Presets (shown only when enabled)
        AnimatedVisibility(visible = enabled, enter = fadeIn() + slideInVertically(), exit = fadeOut() + slideOutVertically()) {
            PresetsView(selectedBand = selectedBand, customValues = customValues, presets = state.presets){ preset ->
                selectedPreset(preset)
            }
        }
    }
}

@Preview
@Composable
fun EqualizerViewPreview(){
    BMusicTheme {
        Surface(color = Color.DarkGray) {
            //EqualizerView(, selectedPreset = {}, toggleEnable = {}, bandModified = { band, level -> })
        }
    }
}