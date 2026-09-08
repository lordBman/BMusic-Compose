package com.bsoft.compose.bmusic.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.CompositingStrategy.Companion.Offscreen
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.states.PlayingState
import com.bsoft.compose.bmusic.data.states.QueueState
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import com.bsoft.compose.bmusic.utils.toTimeFormat

@Composable
fun PlayerControl(
    modifier: Modifier = Modifier, playingState: PlayingState, queueState: QueueState, bitmap: Bitmap? = null,
    previous: ()-> Unit = {}, rewind: ()-> Unit = {},
    next: ()-> Unit = {}, forward: ()-> Unit = {},
    playToggled: ()-> Unit = {},
    repeatToggled: (mode: @Player.RepeatMode Int)-> Unit = {}, shuffleToggled: ()-> Unit = {},
    favouriteToggled: ()-> Unit = {}, seek: (Long)-> Unit = {}){
    // 1. Set up the infinite transition manager
    /*val infiniteTransition = rememberInfiniteTransition(label = "rotation transition")

    // 2. Animate a float value from 0 to 360 degrees infinitely
    val transitionState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (playingState.playing) 360f else 0f,
        animationSpec = infiniteRepeatable(
            // Duration is set to 2000 milliseconds for a full spin
            animation = tween(durationMillis = 30000, easing = LinearEasing)
        ),
        label = "rotation angle"
    )

    val angle by transitionState;*/

    val rotationState = remember { Animatable(0f) }
    LaunchedEffect(playingState.playing, rotationState.isRunning) {
        if (playingState.playing) {
            // Infinite loop that resumes perfectly from where it stopped
            //while (true) {
                val remainingAngle = 360f - rotationState.value
                val duration = (remainingAngle / 360f * 30000).toInt() // 2000ms full rotation

                rotationState.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(
                        durationMillis = duration,
                        easing = LinearEasing
                    )
                )
                // Snap back to 0 so the angle doesn't exceed 360f
                rotationState.snapTo(0f)
            //}
        } else if(rotationState.isRunning) {
            // 2. Stop immediately when isRotating becomes false
            rotationState.stop()
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally){
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Surface(modifier = Modifier.size(320.dp).align(alignment = Alignment.Center)
                .graphicsLayer {
                    compositingStrategy = Offscreen
                }
                // 2. Draw the underlying content and punch the hole
                .drawWithContent {
                    // First draw the image normally
                    drawContent()

                    // Clear out a circle right in the center
                    drawCircle(
                        color = androidx.compose.ui.graphics.Color.Black, // The color doesn't matter
                        radius = 60f, // Define your circle radius in pixels
                        center = Offset(x = size.width / 2f, y = size.height / 2f),
                        blendMode = BlendMode.DstOut // This blend mode acts as an eraser
                    )
                }
                .rotate(rotationState.value),//.graphicsLayer( rotationZ = angle ),
                shape = CircleShape, color = Color.DarkGray) {
                Surface(modifier = Modifier.padding(2.dp), shape = CircleShape) {
                    if(bitmap == null){
                        Image(painter = painterResource(id = R.drawable.lady), contentScale = ContentScale.Crop, contentDescription = null)
                    }else{
                        BitmapImage(bitmap = bitmap, contentScale = ContentScale.Crop)
                    }
                }
                Box(contentAlignment = Alignment.Center){
                    Surface(shape = CircleShape, color = Color.DarkGray){
                        Surface(modifier = Modifier.size(120.dp).padding(4.dp), shape = CircleShape, color = Color.White){}
                    }
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(modifier = Modifier.padding(4.dp), shape = RoundedCornerShape(30.dp), shadowElevation = 2.dp) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        RepeatToggle(mode = queueState.repeatMode){ repeatToggled(it) }
                        ShuffleToggle(active = queueState.shuffle) { shuffleToggled() }
                    }
                }
                Surface(modifier = Modifier.padding(4.dp), shape = RoundedCornerShape(30.dp), shadowElevation = 2.dp) {
                    FavouriteToggle { favouriteToggled() }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                Text(queueState.current?.title ?: "_________" , fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, overflow = TextOverflow.MiddleEllipsis)
                Text("${queueState.current?.artist ?: "____"}: ${queueState.current?.album ?: "____"}", fontSize = 12.sp, fontWeight = FontWeight.Light, color = Color.White, overflow = TextOverflow.MiddleEllipsis)
            }
            Row(modifier = Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallFloatingActionButton(onClick = { previous() }) {
                    Icon(modifier = Modifier.size(18.dp), imageVector = ImageVector.vectorResource( R.drawable.fluent__previous_24_filled), contentDescription = null)
                }
                SmallFloatingActionButton(onClick = { rewind() }) {
                    Icon(modifier = Modifier.size(18.dp), imageVector = ImageVector.vectorResource( R.drawable.fluent__rewind_24_filled), contentDescription = null)
                }
                FloatingActionButton (onClick = { playToggled() }, shape = CircleShape, modifier = Modifier.size(80.dp)) {
                    if(playingState.playing){
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
                Text(playingState.position.toTimeFormat(), fontSize = 12.sp, fontWeight = FontWeight.Light, color = Color.White)
                Text((queueState.current?.duration ?: 0).toTimeFormat(), fontSize = 12.sp, fontWeight = FontWeight.Light, color = Color.White)
            }
            Seeker(playingState = playingState, queueState = queueState) {
                seek(it)
            }
        }
    }
}

@Preview
@Composable
fun PlayerControlPreview(){
    BMusicTheme {
        PlayerControl(playingState = PlayingState(), queueState = QueueState())
    }
}