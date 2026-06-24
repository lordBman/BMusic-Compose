package com.bsoft.compose.bmusic.ui.components

import android.graphics.Bitmap
import android.util.Size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.data.Album
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import com.bsoft.compose.bmusic.utils.Util

@Composable
fun AlbumView(modifier: Modifier = Modifier, album: Album, clicked: ()-> Unit){
    val context = LocalContext.current

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(Unit) {
        bitmap = Util.loadArtwork(context, album.artworkUri, Size(140, 140))
    }

    Column(modifier = modifier.clickable{ clicked() }){
        Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)){
            Surface(modifier = Modifier.size(56.dp), color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(4.dp)) {
                if(bitmap == null){
                    Box(contentAlignment = Alignment.Center) {
                        Icon(modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.tertiary, imageVector = ImageVector.vectorResource(R.drawable.pinhead__vinyl_record), contentDescription = null)
                    }
                }else{
                    BitmapImage(bitmap = bitmap as Bitmap,  contentScale = ContentScale.Crop)
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)){
                Text(album.name, fontSize = 16.sp, fontWeight = FontWeight.Light, letterSpacing = 1.2.sp, overflow = TextOverflow.MiddleEllipsis, maxLines = 1)
                Text("${album.songCount} song(s) - ${album.artist}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary, overflow = TextOverflow.MiddleEllipsis, maxLines = 1)
            }
        }
        HorizontalDivider(thickness = 0.5.dp)
    }
}

@Preview
@Composable
private fun AlbumViewPreview(){
    BMusicTheme {
        Surface {
            AlbumView(album = Album(id = 0, name = "Album Name", artist = "Artist name", songCount = 5)){}
        }
    }
}