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
import androidx.media3.common.Player
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.states.PlayingState
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun RepeatToggle(modifier: Modifier = Modifier, mode: @Player.RepeatMode Int = Player.REPEAT_MODE_ALL, toggle: (mode: @Player.RepeatMode Int) -> Unit){
    IconButton(modifier = modifier, colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer, contentColor = MaterialTheme.colorScheme.primary),
        onClick = {
            when(mode){
                Player.REPEAT_MODE_OFF -> toggle(Player.REPEAT_MODE_ALL)
                Player.REPEAT_MODE_ALL -> toggle(Player.REPEAT_MODE_ONE)
                Player.REPEAT_MODE_ONE -> toggle(Player.REPEAT_MODE_OFF)
            }
        },
        content = {
            when(mode){
                Player.REPEAT_MODE_OFF -> {
                    Icon(imageVector = ImageVector.vectorResource(R.drawable.fluent__arrow_repeat_all_off_24_regular), contentDescription = null)
                }
                Player.REPEAT_MODE_ALL -> {
                    Icon(imageVector = ImageVector.vectorResource(R.drawable.fluent__arrow_repeat_all_24_regular), contentDescription = null)
                }
                Player.REPEAT_MODE_ONE -> {
                    Icon(imageVector = ImageVector.vectorResource(R.drawable.fluent__arrow_repeat_1_24_regular), contentDescription = null)
                }
            }
        })
}

@Preview
@Composable
private fun RepeatTogglePreview(){
    var mode by remember { mutableStateOf(Player.REPEAT_MODE_ALL) }
    
    BMusicTheme {
        Surface {
            RepeatToggle(mode = mode){
                mode = it
            }
        }
    }
}