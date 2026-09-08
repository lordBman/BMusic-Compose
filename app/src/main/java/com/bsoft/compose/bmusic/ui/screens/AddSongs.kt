package com.bsoft.compose.bmusic.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.ui.components.AlphabeticList
import com.bsoft.compose.bmusic.ui.components.SongSelectedStatus
import com.bsoft.compose.bmusic.ui.components.SongView
import com.bsoft.compose.bmusic.viewmodels.SongsViewModel

@Composable
fun AddSongsScreen(modifier: Modifier = Modifier, songsViewModel: SongsViewModel = hiltViewModel(), id: Long, title: String, back: ()-> Unit){
    val selectedSongs = remember { mutableStateListOf<Song>() }

    val songsState by songsViewModel.state.collectAsStateWithLifecycle()
    val textFieldState = rememberTextFieldState()


    val songs by remember {
        derivedStateOf {
            if(textFieldState.text.isEmpty()){
                songsState.songs
            }else{
                val query = textFieldState.text.toString()
                songsState.songs.filter {
                    it.album.lowercase().contains(query) || it.title.lowercase().contains(query) || it.artist.lowercase().contains(query)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Add Songs") },
                navigationIcon = {
                    IconButton(onClick = { back() }) {
                        Icon(imageVector = ImageVector.vectorResource(R.drawable.material_symbols__arrow_back_ios_new_rounded), contentDescription = "")
                    }
                },
                actions = {
                    if(selectedSongs.isNotEmpty()){
                        IconButton(onClick = {
                            songsViewModel.addToPlaylist(playlist = id, selectedSongs)
                            back()
                        }) {
                            Icon(imageVector = ImageVector.vectorResource(R.drawable.fluent__save_32_light), contentDescription = "")
                        }
                    }
                }
            )
        }
    ) {paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            Column() {
                SongSelectedStatus(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), state = textFieldState, selected = selectedSongs.size)
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                AlphabeticList(items = songs, header = { it.title.first() }) { _, item ->
                    val isSelected = selectedSongs.contains(item)
                    SongView(selectable = true, selected = isSelected, song = item) {
                        if(isSelected){
                            selectedSongs.remove(item)
                        }else{
                            selectedSongs.add(item)
                        }
                    }
                }
            }
        }
    }
}