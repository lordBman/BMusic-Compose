package com.bsoft.compose.bmusic.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.Album
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun AlbumLargeView(modifier: Modifier = Modifier, album: Album){
    Surface(modifier = modifier.width(250.dp), shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceContainerHighest, shadowElevation = 4.dp) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)){
            Surface(modifier = Modifier.size(250.dp, 120.dp), shape = RoundedCornerShape(10.dp)) {
                Image(modifier = Modifier.fillMaxSize(), bitmap = ImageBitmap.imageResource(R.drawable.album_bg), contentScale = ContentScale.Crop, contentDescription = "")
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Album Name", fontSize = 16.sp, fontWeight = FontWeight.Light, letterSpacing = 1.2.sp, overflow = TextOverflow.MiddleEllipsis, maxLines = 1)
                Text("12 Songs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary, overflow = TextOverflow.MiddleEllipsis, maxLines = 1)
            }
        }
    }
}

@Preview
@Composable
private fun AlbumLargeViewPreview(){
    BMusicTheme{
        AlbumLargeView(album = Album(id = 0, name = "Album Title", artist = "", songCount = 12 ))
    }
}