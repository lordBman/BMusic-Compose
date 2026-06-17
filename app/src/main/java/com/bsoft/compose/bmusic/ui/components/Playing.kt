package com.bsoft.compose.bmusic.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Playing(modifier: Modifier = Modifier){
    val colorStops = arrayOf( 0.0f to Color.Transparent, 0.2f to MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), 0.55f to MaterialTheme.colorScheme.surface)

    Box(modifier = modifier.fillMaxWidth().height(420.dp)){
        Image(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter), painter = painterResource(id = R.drawable.lady), contentScale = ContentScale.FillWidth, contentDescription = null)
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
                Text("Name of the Songs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, overflow = TextOverflow.MiddleEllipsis)
                Text("Album: Album Name", fontSize = 12.sp, fontWeight = FontWeight.Light, overflow = TextOverflow.MiddleEllipsis)
            }
            Row(modifier = Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallFloatingActionButton(onClick = {}) {
                    Icon(modifier = Modifier.size(18.dp), imageVector = ImageVector.vectorResource( R.drawable.fluent__previous_24_filled), contentDescription = null)
                }
                SmallFloatingActionButton(onClick = {}) {
                    Icon(modifier = Modifier.size(18.dp), imageVector = ImageVector.vectorResource( R.drawable.fluent__rewind_24_filled), contentDescription = null)
                }
                FloatingActionButton (onClick = {}, shape = CircleShape, modifier = Modifier.size(80.dp)) {
                    Icon(modifier = Modifier.size(40.dp), imageVector = ImageVector.vectorResource( R.drawable.fluent__play_24_filled), contentDescription = null)
                }
                SmallFloatingActionButton(onClick = {}) {
                    Icon(modifier = Modifier.size(18.dp), imageVector = ImageVector.vectorResource( R.drawable.fluent__fast_forward_24_filled), contentDescription = null)
                }
                SmallFloatingActionButton(onClick = {}) {
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
        Playing()
    }
}