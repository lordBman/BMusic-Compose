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
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun FavouriteToggle(modifier: Modifier = Modifier, active: Boolean = false, clicked: (active: Boolean) -> Unit){
    IconButton(modifier = modifier, colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer, contentColor = MaterialTheme.colorScheme.primary), onClick = { clicked(!active) }) {
        if(active){
            Icon(imageVector = ImageVector.vectorResource(R.drawable.fluent__heart_24_filled), contentDescription = null)
        }else{
            Icon(imageVector = ImageVector.vectorResource(R.drawable.fluent__heart_24_regular), contentDescription = null)
        }
    }
}

@Preview
@Composable
private fun FavouriteTogglePreview(){
    var active by remember { mutableStateOf(false) }

    BMusicTheme {
        Surface {
            FavouriteToggle(active = active) {
                active = it
            }
        }
    }
}