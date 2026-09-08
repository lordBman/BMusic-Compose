package com.bsoft.compose.bmusic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun SongSelectedStatus (modifier: Modifier = Modifier, state: TextFieldState, selected: Int){

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            prefix = { Icon(imageVector = ImageVector.vectorResource(R.drawable.reicon__music_filter), contentDescription = null) },
            state = state,
            shape = OutlinedTextFieldDefaults.roundedShape,
            placeholder = { Text("Filter song list") },
            lineLimits = TextFieldLineLimits.SingleLine,
        )
        Text("Selected: $selected", fontSize = 14.sp, fontWeight = FontWeight.Light)
    }
}

@Preview
@Composable
private fun SongSelectedStatusPreview(){
    val state = rememberTextFieldState()

    BMusicTheme {
        Surface {
            SongSelectedStatus(state = state, selected = 12)
        }
    }
}