package com.bsoft.compose.bmusic.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun ListControls(modifier: Modifier = Modifier, add: (()-> Unit)? = {}, playAll: ()-> Unit, shuffle: ()-> Unit){
    Row(modifier = modifier, horizontalArrangement =Arrangement.spacedBy(8.dp, alignment = Alignment.End), verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.height(32.dp).clickable{ playAll() },
            shape = RoundedCornerShape(20.dp),
            color = Color.White) {
            Row(modifier = Modifier.padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
                Icon(modifier = Modifier.size(20.dp), imageVector = ImageVector.vectorResource(R.drawable.fluent__play_24_filled), tint = MaterialTheme.colorScheme.primary, contentDescription = null)
                Text(text = "Play All", fontSize = 14.sp, lineHeight = 1.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
        Surface(modifier = Modifier.size(32.dp).clickable{ shuffle() }, shape = CircleShape, color = Color.White) {
            Row(modifier = Modifier.fillMaxHeight(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary, imageVector = ImageVector.vectorResource(R.drawable.fluent__arrow_shuffle_24_regular), contentDescription = null)
            }
        }
        add?.let {
            Surface(modifier = Modifier.size(32.dp).clickable{ add() }, shape = CircleShape, color = Color.White) {
                Row(modifier = Modifier.fillMaxHeight(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary, imageVector = ImageVector.vectorResource(R.drawable.akar_icons__plus), contentDescription = null)
                }
            }
        }
    }
}

@Preview
@Composable
private fun ListControlsPreview(){
    BMusicTheme {
        ListControls(
            add = {},
            playAll = {},
            shuffle = {}
        )
    }
}