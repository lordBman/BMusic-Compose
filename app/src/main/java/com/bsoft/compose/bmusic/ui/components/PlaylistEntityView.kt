package com.bsoft.compose.bmusic.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.entities.PlaylistEntity
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import com.bsoft.compose.bmusic.utils.toTimeFormat

@Composable
fun PlaylistEntityView(modifier: Modifier = Modifier, playlistEntity: PlaylistEntity, menu: ()-> Unit = {}, clicked: ()-> Unit = {}){
    Surface(modifier = modifier.clickable{ clicked() }, shape = RoundedCornerShape(8.dp), border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))) {
        Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(modifier = Modifier.size(40.dp), imageVector = ImageVector.vectorResource(R.drawable.hugeicons__playlist), tint = MaterialTheme.colorScheme.secondary, contentDescription = null)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = playlistEntity.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${playlistEntity.count} Song(s)", fontSize = 12.sp, fontWeight = FontWeight.Light)
                    Text(playlistEntity.duration?.toTimeFormat() ?: "00:00", fontSize = 12.sp, fontWeight = FontWeight.Light)
                }
            }
            Icon(modifier = Modifier.clickable{ menu() },
                imageVector = ImageVector.vectorResource(R.drawable.circum__menu_kebab), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview
@Composable
private fun PlaylistEntityViewPreview(){
    BMusicTheme {
        PlaylistEntityView(playlistEntity = PlaylistEntity(name = "MyPlaylist"))
    }
}