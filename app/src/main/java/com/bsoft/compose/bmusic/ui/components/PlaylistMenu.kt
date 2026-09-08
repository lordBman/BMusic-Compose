package com.bsoft.compose.bmusic.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
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

@Composable
fun PlaylistMenuItem(modifier: Modifier = Modifier, title: String, @DrawableRes icon: Int, clicked: ()-> Unit){
    Row(modifier = modifier.clickable{ clicked() }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(imageVector = ImageVector.vectorResource(icon), contentDescription = null)
        Text(text = title, fontSize = 16.sp)
    }
}

@Composable
fun PlaylistMenu(
    modifier: Modifier = Modifier,
    playlist: Long,
    playlistName: String,
    open: ((Long)-> Unit)? = null,
    edit: ((Long)-> Unit)? = null,
    play: (Long)-> Unit = {},
    shuffle: (Long)-> Unit = {},
    rename: (Long)-> Unit = {},
    addSongs: (Long)-> Unit = {},
    delete: (Long)-> Unit = {}
){
    Column(modifier = modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(modifier = Modifier.size(40.dp), imageVector = ImageVector.vectorResource(R.drawable.hugeicons__playlist), tint = MaterialTheme.colorScheme.primary, contentDescription = null)
            Text(text = playlistName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        }
        HorizontalDivider(thickness = 1.dp)
        open?.let {
            PlaylistMenuItem(title = "Open", icon = R.drawable.solar__square_forward_linear, clicked = { open(playlist) })
        }
        PlaylistMenuItem(title = "Play", icon = R.drawable.solar__play_outline, clicked = { play(playlist) })
        PlaylistMenuItem(title = "Shuffle", icon = R.drawable.solar__shuffle_linear, clicked = { shuffle(playlist) })
        PlaylistMenuItem(title = "Rename", icon = R.drawable.solar__pen_new_square_linear, clicked = { rename(playlist) })
        edit?.let{
            PlaylistMenuItem(title = "Edit", icon = R.drawable.solar__settings_linear, clicked = { shuffle(playlist) })
        }
        PlaylistMenuItem(title = "Add Songs", icon = R.drawable.solar__add_square_linear, clicked = { addSongs(playlist) })
        PlaylistMenuItem(title = "Delete", icon = R.drawable.solar__trash_bin_trash_linear, clicked = { delete(playlist) })
    }
}

@Preview
@Composable
private fun PlaylistMenuPreview(){
    BMusicTheme {
        Surface {
            PlaylistMenu(playlist = 0, playlistName = "Placeholder Song")
        }
    }
}