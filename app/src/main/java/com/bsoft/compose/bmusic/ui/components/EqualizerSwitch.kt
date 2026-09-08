package com.bsoft.compose.bmusic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun EqualizerSwitch(modifier: Modifier = Modifier, enabled: Boolean = false, check: (Boolean)-> Unit) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Equalizer",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Switch(
            checked = enabled,
            colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest),
            onCheckedChange = { check(it) }
        )
    }
}

@Preview
@Composable
fun EqualizerSwitchPreview(){
    BMusicTheme {
        EqualizerSwitch{}
    }
}