package com.bsoft.compose.bmusic.ui.components

import android.graphics.Bitmap
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.Song
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import com.bsoft.compose.bmusic.utils.Util

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Playing(
    modifier: Modifier = Modifier, song: Song?, playing: Boolean,
    previous: ()-> Unit = {}, rewind: ()-> Unit = {},
    next: ()-> Unit = {}, forward: ()-> Unit = {},
    queue: ()-> Unit = {}, playToggle: ()-> Unit = {},
){
    val colorStops = arrayOf( 0.0f to Color.Transparent, 0.3f to MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), 0.55f to MaterialTheme.colorScheme.surface)

    val context = LocalContext.current

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(song) {
        bitmap = Util.getAudioArtwork(context, song?.id ?: 0, song?.id ?: 0, Size(300, 300))
    }

    Box(modifier = modifier.fillMaxWidth().height(420.dp)){
        if(bitmap == null){
            Image(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter), painter = painterResource(id = R.drawable.lady), contentScale = ContentScale.FillWidth, contentDescription = null)
        }else{
            BitmapImage(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter), bitmap = bitmap as Bitmap, contentScale = ContentScale.FillWidth)
        }
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colorStops = colorStops)))
        Column(modifier = Modifier.padding(20.dp).align(Alignment.BottomCenter), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(modifier = Modifier.padding(4.dp), shape = RoundedCornerShape(30.dp), shadowElevation = 2.dp) {
                    FavouriteToggle { }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(modifier = Modifier.padding(4.dp), shape = RoundedCornerShape(30.dp), shadowElevation = 2.dp) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            RepeatToggle{ }
                            ShuffleToggle { }
                        }
                    }
                    Surface(modifier = Modifier.padding(4.dp), shape = RoundedCornerShape(30.dp), shadowElevation = 2.dp) {
                        IconButton(onClick = {},  colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer, contentColor = MaterialTheme.colorScheme.primary)) {
                            Icon(imageVector = ImageVector.vectorResource(R.drawable.fluent__music_note_2_play_20_regular), contentDescription = null)
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                Text(song?.title ?: "_________" , fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, overflow = TextOverflow.MiddleEllipsis)
                Text("${song?.artist ?: "____"}: ${song?.album ?: "____"}", fontSize = 12.sp, fontWeight = FontWeight.Light, overflow = TextOverflow.MiddleEllipsis)
            }
            Row(modifier = Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallFloatingActionButton(onClick = { previous() }) {
                    Icon(modifier = Modifier.size(18.dp), imageVector = ImageVector.vectorResource( R.drawable.fluent__previous_24_filled), contentDescription = null)
                }
                SmallFloatingActionButton(onClick = { rewind() }) {
                    Icon(modifier = Modifier.size(18.dp), imageVector = ImageVector.vectorResource( R.drawable.fluent__rewind_24_filled), contentDescription = null)
                }
                FloatingActionButton (onClick = {playToggle()}, shape = CircleShape, modifier = Modifier.size(80.dp)) {
                    if(playing){
                        Icon(modifier = Modifier.size(40.dp), imageVector = ImageVector.vectorResource( R.drawable.fluent__pause_24_filled), contentDescription = null)
                    }else{
                        Icon(modifier = Modifier.size(40.dp), imageVector = ImageVector.vectorResource( R.drawable.fluent__play_24_filled), contentDescription = null)
                    }
                }
                SmallFloatingActionButton(onClick = { forward() }) {
                    Icon(modifier = Modifier.size(18.dp), imageVector = ImageVector.vectorResource( R.drawable.fluent__fast_forward_24_filled), contentDescription = null)
                }
                SmallFloatingActionButton(onClick = { next() }) {
                    Icon(modifier = Modifier.size(18.dp), imageVector = ImageVector.vectorResource( R.drawable.fluent__next_24_filled), contentDescription = null)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Text("00:00", fontSize = 12.sp, fontWeight = FontWeight.Light)
                Text("00:00", fontSize = 12.sp, fontWeight = FontWeight.Light)
            }
            Slider(modifier = Modifier.height(20.dp),
                value = 20f, onValueChange = {},
                valueRange = 0f..50f,
                track = { sliderState -> SliderDefaults.Track(
                    modifier = Modifier.height(6.dp),
                    thumbTrackGapSize = 2.dp,
                    sliderState = sliderState)
                },

            )
        }
    }
}

@Preview
@Composable
private fun PlayingPreview(){
    BMusicTheme {
        Playing(song = null, playing = false)
    }
}