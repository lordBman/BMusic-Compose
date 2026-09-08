package com.bsoft.compose.bmusic.ui.components

import android.graphics.Bitmap
import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import com.bsoft.compose.bmusic.utils.Util
import com.bsoft.compose.bmusic.utils.toTimeFormat

@Composable
fun QueueSongView(
    modifier: Modifier = Modifier, selectionMode: Boolean = false, movable: Boolean = false, moving: Boolean = false,
    checked: Boolean = false, showMenu: Boolean = false, menuClicked: (Song)-> Unit = {}, song: Song,
    clicked: ()-> Unit){
    val context = LocalContext.current

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(Unit) {
        bitmap = Util.loadArtwork(context, song.artworkUri, Size(140, 140))
    }

    val textColor = if(moving) MaterialTheme.colorScheme.primary else Color.White

    Column(
        modifier = modifier.background(color = if(moving) Color.White else { Color.Transparent }).clickable{ clicked() }//.combinedClickable( onClick = clicked, onLongClick = longPressed )
    ){
        Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)){
            Surface(modifier = Modifier.size(56.dp), color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(4.dp)) {
                if(bitmap == null){
                    Box(contentAlignment = Alignment.Center) {
                        Icon(modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.tertiary, imageVector = ImageVector.vectorResource(R.drawable.solar__music_notes_bold_duotone), contentDescription = null)
                    }
                }else{
                    BitmapImage(bitmap = bitmap as Bitmap, contentScale = ContentScale.Crop)
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)){
                Text(song.title, fontSize = 16.sp, fontWeight = FontWeight.Light, color = textColor, letterSpacing = 1.2.sp, overflow = TextOverflow.MiddleEllipsis, maxLines = 1)
                Text("${song.duration.toTimeFormat()} ${song.artist}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor, overflow = TextOverflow.MiddleEllipsis, maxLines = 1)
            }
            if(selectionMode){
                Checkbox(checked = checked, onCheckedChange = { clicked() })
            }else if(showMenu){
                Icon(modifier = Modifier.clickable{ menuClicked(song) }, imageVector = ImageVector.vectorResource(R.drawable.circum__menu_kebab), tint = textColor, contentDescription = null)
            }

            if(movable){
                Icon(modifier = Modifier.clickable{}, imageVector = ImageVector.vectorResource(R.drawable.streamline_ultimate__move_expand_vertical), tint = textColor,
                    contentDescription = null)
            }
        }
        HorizontalDivider(thickness = 1.dp,  color = Color.White.copy(alpha = 0.1f))
    }
}

@Preview
@Composable
private fun QueueSongViewPreview(){
    BMusicTheme {
        QueueSongView(showMenu = true, song = Song(id = 0, displayName = "Display Name", title = "Song Title", artist = "Artist name", album = "Album Name", duration = 5000)){

        }
    }
}