package com.bsoft.compose.bmusic.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

enum class PlayingPage(val title: String, val icon: Int){
    Queue(title = "Queue", icon = R.drawable.fluent__music_note_2_play_20_regular),
    Playing(title = "Playing",  icon =R.drawable.lucide__mic_vocal),
    Equalizer(title = "Equalizer", icon = R.drawable.si__equalizer_fill)
}

@Composable
fun PlayingTab(modifier: Modifier = Modifier, active: Int = 1, clicked: (Int)-> Unit = {}){
    val activeStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
    val inActiveStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Light, color = Color.White.copy(alpha = 0.6f))
    
    Row(modifier = modifier.height(56.dp), verticalAlignment = Alignment.CenterVertically) {
        PlayingPage.entries.forEachIndexed { index, page ->
            Column(modifier = Modifier.clickable{ clicked(index) }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)){
                Image(modifier = Modifier.size(30.dp), imageVector = ImageVector.vectorResource(page.icon),
                    colorFilter = ColorFilter.tint(color = if(active == index) Color.White else Color.White.copy(alpha = 0.6f)),
                    contentDescription = null)
                Text(modifier = Modifier.width(100.dp),
                    text = page.title, textAlign = TextAlign.Center,
                    style = if(active == index) activeStyle else inActiveStyle)
            }
            if(page.title != PlayingPage.entries.last().title){
                VerticalDivider(thickness = 0.5.dp, color = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}

@Preview
@Composable
private fun PlayingTabPreview(){
    BMusicTheme {
        Surface {
            PlayingTab()
        }
    }
}