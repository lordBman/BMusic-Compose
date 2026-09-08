package com.bsoft.compose.bmusic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.data.models.EqualizerBand
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

private fun Int.formatFrequency(): String {
    return when {
        this >= 1_000_000 -> "${(this.toFloat() / 1_000_000f).toInt()} GHz"
        this >= 1_000 -> "${(this.toFloat() / 1_000f).toInt()} kHz"
        else -> "$this Hz"
    }
}

@Composable
fun EqualizerControls(modifier: Modifier = Modifier, bands: List<EqualizerBand> = emptyList(), seek: (Short, Short)-> Unit){
    Column(modifier = modifier.fillMaxWidth().height(320.dp).graphicsLayer { rotationZ = 270f }, // Rotate to make sliders vertical
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        bands.forEach { band->
            Row(horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = band.frequency.formatFrequency(),
                    modifier = Modifier.rotate(90f),
                    fontSize = 10.sp,
                    color = Color.White
                )
                Slider(
                    modifier = Modifier.width(240.dp),//.offset(x = 20.dp),
                    value = band.level.toFloat(),//audioEffects!!.gainValues[index].times(1000f).coerceIn(-3000f, 3000f),
                    onValueChange = {
                        seek(band.band, it.toInt().toShort())
                    },
                    valueRange = band.min.toFloat()..band.max.toFloat(),
                    colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White, inactiveTrackColor = Color.White.copy(alpha = 0.67f))
                )
            }
        }
    }
}

@Preview
@Composable
fun EqualizerControlsPreview(){
    BMusicTheme {
        Surface(color = Color.DarkGray) {
            EqualizerControls{
                band, level ->
            }
        }
    }
}