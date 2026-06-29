package com.bsoft.compose.bmusic.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.states.PlayingState
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun RepeatToggle(modifier: Modifier = Modifier, mode: PlayingState.RepeatMode = PlayingState.RepeatMode.All, toggle: (mode: PlayingState.RepeatMode) -> Unit){
    IconButton(modifier = modifier, colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer, contentColor = MaterialTheme.colorScheme.primary),
        onClick = {
            when(mode){
                PlayingState.RepeatMode.Disabled -> toggle(PlayingState.RepeatMode.All)
                PlayingState.RepeatMode.All -> toggle(PlayingState.RepeatMode.Single)
                PlayingState.RepeatMode.Single -> toggle(PlayingState.RepeatMode.Disabled)
            }
        },
        content = {
            when(mode){
                PlayingState.RepeatMode.Disabled -> {
                    Icon(imageVector = ImageVector.vectorResource(R.drawable.fluent__arrow_repeat_all_off_24_regular), contentDescription = null)
                }
                PlayingState.RepeatMode.All -> {
                    Icon(imageVector = ImageVector.vectorResource(R.drawable.fluent__arrow_repeat_all_24_regular), contentDescription = null)
                }
                PlayingState.RepeatMode.Single -> {
                    Icon(imageVector = ImageVector.vectorResource(R.drawable.fluent__arrow_repeat_1_24_regular), contentDescription = null)
                }
            }
        })
}

@Preview
@Composable
private fun RepeatTogglePreview(){
    var mode by remember { mutableStateOf(PlayingState.RepeatMode.All) }
    
    BMusicTheme {
        Surface {
            RepeatToggle(mode = mode){
                mode = it
            }
        }
    }
}