package com.bsoft.compose.bmusic.ui.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bsoft.compose.bmusic.data.Artist
import com.bsoft.compose.bmusic.data.Song
import com.bsoft.compose.bmusic.ui.components.ArtistView
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun ArtistsPage(modifier: Modifier = Modifier, artists: List<Artist>, chosen: (artist: Artist)-> Unit) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(count = artists.size) { index ->
            ArtistView(artist = artists[index]){
                chosen(artists[index])
            }
        }
    }
}

@Preview
@Composable
fun ArtistsPagePreview(){
    val artists = (0..20).map {
        Artist(id = 0, name = "Artist Name", albumCount = 5, songCount = 34)
    }
    BMusicTheme {
        Surface {
            ArtistsPage(artists = artists){}
        }
    }
}