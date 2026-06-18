package com.bsoft.compose.bmusic.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun SongView(modifier: Modifier = Modifier){
    Column(modifier = modifier){
        Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)){
            Surface(modifier = Modifier.size(50.dp), color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(4.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(modifier = Modifier.size(26.dp), tint = MaterialTheme.colorScheme.primary, imageVector = ImageVector.vectorResource(R.drawable.solar__music_notes_bold_duotone), contentDescription = null)
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Top){
                Text("Name of the Songs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, overflow = TextOverflow.MiddleEllipsis)
                Text("3:10:21 Artist Name", fontSize = 12.sp, fontWeight = FontWeight.Light, overflow = TextOverflow.MiddleEllipsis)
            }
        }
        HorizontalDivider(thickness = 0.5.dp)
    }
}

@Preview
@Composable
private fun SongViewPreview(){
    BMusicTheme {
        Surface {
            SongView()
        }
    }
}