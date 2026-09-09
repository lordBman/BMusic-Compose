package com.bsoft.compose.bmusic.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.bsoft.compose.bmusic.data.states.PlayingState
import com.bsoft.compose.bmusic.data.states.QueueState

data class SeekerState(val value: Long = 0, val use: Boolean = false )

@Composable
fun Seeker(modifier: Modifier = Modifier, playingState: PlayingState, queueState: QueueState, seek: (Long)-> Unit){
    var localState by remember { mutableStateOf(SeekerState()) }

    LaunchedEffect(playingState.position) {
        if(localState.use){
            localState = SeekerState(use = false)
        }
    }

    Slider(modifier = modifier, value = (if(localState.use) localState.value else playingState.position).toFloat(),
        onValueChange = {
            seek(it.toLong())
            localState = SeekerState(value = it.toLong(), use = true)
        },
        valueRange = 0f..(queueState.current?.duration?.toFloat() ?: 0f),
        thumb = { state ->
            Surface(modifier = Modifier.size(20.dp),
                shadowElevation =  1.dp,
                border = BorderStroke( width = 2.dp,
                    color = if(state.isDragging) MaterialTheme.colorScheme.primary else Color.White),
                shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
            }
        },
        track = { state ->
            LinearWavyProgressIndicator(modifier = Modifier.fillMaxWidth(),
                stroke = Stroke(width = 3f),
                trackStroke = Stroke(width = 3f),
                amplitude = { if(playingState.playing && !localState.use) 1.0f else 0.0f  },
                color = Color.White, progress = { state.value / state.valueRange.endInclusive })
        }
    )
}