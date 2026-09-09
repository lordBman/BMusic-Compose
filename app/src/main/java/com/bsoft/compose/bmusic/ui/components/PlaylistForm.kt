package com.bsoft.compose.bmusic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import kotlinx.coroutines.launch

@Composable
fun PlaylistForm(modifier: Modifier = Modifier, error: String? = null,  add: (String)-> Unit){
    val textFieldState = rememberTextFieldState()
    val coroutine = rememberCoroutineScope()

    val buttonEnabled by remember {
        derivedStateOf { textFieldState.text.isNotEmpty() }
    }


    Column(modifier = modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(modifier = Modifier.size(40.dp), imageVector = ImageVector.vectorResource(R.drawable.marketeq__add_playlist), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(text = "Add Custom Playlist", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        }
        HorizontalDivider(thickness = 1.dp)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(modifier = Modifier.height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(modifier = Modifier.weight(1f),
                    state = textFieldState, placeholder = {
                        Text("Specify Playlist name")
                    }
                )
                FilledTonalButton(
                    modifier = Modifier.fillMaxHeight(),
                    enabled = buttonEnabled,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(4.dp), onClick = {
                        val title = textFieldState.text.toString()
                        coroutine.launch {
                            try {
                                add(title)
                                textFieldState.clearText()
                            }catch (error: Exception){

                            }
                        }
                    }) {
                    Text("Create", fontSize = 16.sp)
                }
            }
            if(error != null){
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.error,
                        imageVector = ImageVector.vectorResource(R.drawable.marketeq__caution), contentDescription = null)
                    Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 16.sp)
                }
            }
        }
    }

}

@Preview
@Composable
private fun PlaylistFormPreview(){
    BMusicTheme {
        Surface {
            PlaylistForm(error = "This is an error, try again"){
                //return@PlaylistForm true
            }
        }
    }
}