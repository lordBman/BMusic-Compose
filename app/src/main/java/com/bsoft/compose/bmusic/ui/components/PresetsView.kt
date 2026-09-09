package com.bsoft.compose.bmusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.data.models.EqualizerPresent
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
private fun PresetView(modifier: Modifier = Modifier, present: EqualizerPresent, isSelected: Boolean = false, selected: (EqualizerPresent)->Unit){
    Box(modifier = modifier.wrapContentSize()
        .border(1.dp, color = Color.White, RoundedCornerShape(40.dp))
        .clip(RoundedCornerShape(40.dp))
        .clickable {
            selected(present)
        }
        .background(if (isSelected) Color.White else Color.Transparent)
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(text = present.name, color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White)
    }
}

@Composable
fun PresetsView(modifier: Modifier = Modifier, selectedBand: Short = 0, customValues: List<Short>, presets: List<EqualizerPresent> = emptyList(), selected: (EqualizerPresent)->Unit) {
    Column(modifier = modifier){
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp,
            Alignment.CenterHorizontally)) {
            VerticalDivider(modifier = Modifier.height(20.dp), color = Color.White.copy(alpha = 0.67f), thickness = 1.dp)
            Text(
                text = "Presets",
                modifier = Modifier.padding(4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            VerticalDivider(modifier = Modifier.height(20.dp), color = Color.White.copy(alpha = 0.67f), thickness = 1.dp)
        }

        // Preset buttons in rows
        FlowRow(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            if(customValues.isNotEmpty()){
                PresetView(present = EqualizerPresent(preset = -1, name = "Custom"), isSelected = selectedBand.toInt() == -1) {
                    selected(it)
                }
            }
            presets.forEach { preset ->
                //val index = effectType.indexOf(item)
                val isSelected = preset.preset == selectedBand //audioEffects?.selectedEffectType

                PresetView(present = preset, isSelected = isSelected) {
                    selected(it)
                }
            }
        }
    }
}

@Preview
@Composable
fun PresetsViewPreview(){
    BMusicTheme {
        Surface(color = Color.Gray) {
            //PresetsView( , presets = emptyList()){}
        }
    }
}