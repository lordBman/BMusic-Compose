package com.bsoft.compose.bmusic.ui.components

import android.graphics.Bitmap
import android.util.Size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.PlayingState
import com.bsoft.compose.bmusic.utils.Util

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomControl(modifier: Modifier = Modifier, state: PlayingState, clicked: ()-> Unit, playToggle: ()-> Unit){
    val context = LocalContext.current

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(state.current) {
        bitmap = Util.loadArtwork(context, state.current?.artworkUri, Size(140, 140))
    }

    Surface(modifier = modifier.fillMaxWidth().clickable{ clicked() }, shadowElevation = 4.dp, shape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp)){
        Column(modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)){
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)){
                Surface(modifier = Modifier.size(60.dp), color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(10.dp)) {
                    if(bitmap == null){
                        Box(contentAlignment = Alignment.Center) {
                            Icon(modifier = Modifier.size(30.dp), tint = MaterialTheme.colorScheme.tertiary, imageVector = ImageVector.vectorResource(R.drawable.solar__music_notes_bold_duotone), contentDescription = null)
                        }
                    }else{
                        BitmapImage(bitmap = bitmap as Bitmap,  contentScale = ContentScale.Crop)
                    }
                }
                Column(modifier = Modifier.weight(1f)){
                    Text(state.current?.title ?: "______", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, overflow = TextOverflow.MiddleEllipsis, maxLines = 1)
                    Text("Artist: ${state.current?.artist ?: "_____"}", fontSize = 12.sp, fontWeight = FontWeight.Light, overflow = TextOverflow.MiddleEllipsis, maxLines = 1)
                }
                IconButton(onClick = { playToggle() }) {
                    if(state.playing){
                        Icon(modifier = Modifier.size(30.dp), imageVector = ImageVector.vectorResource(R.drawable.fluent__pause_24_filled), contentDescription = null)
                    }else{
                        Icon(modifier = Modifier.size(30.dp), imageVector = ImageVector.vectorResource(R.drawable.fluent__play_24_filled), contentDescription = null)
                    }
                }
            }
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(3.dp),
                progress = {
                    val current = state.position.toFloat()
                    val total = (state.current?.duration ?: 0).toFloat()
                    current/total
                }
            )
        }
    }
}