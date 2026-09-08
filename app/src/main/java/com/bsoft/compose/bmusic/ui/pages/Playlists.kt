package com.bsoft.compose.bmusic.ui.pages

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.entities.PlaylistEntity
import com.bsoft.compose.bmusic.ui.components.PlaylistEntityView
import com.bsoft.compose.bmusic.ui.components.SongView
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import com.bsoft.compose.bmusic.viewmodels.PlayingViewModel
import com.bsoft.compose.bmusic.viewmodels.SongsViewModel
import kotlinx.coroutines.launch

enum class PlaylistsOptions(val title: String, @DrawableRes val bg: Int, @DrawableRes val icon: Int){
    Favourites(title = "Favourites", bg = R.drawable.favourites, icon = R.drawable.ph__list_heart_thin),
    RecentlyAdded(title = "Recently Added", bg = R.drawable.recently_added, icon = R.drawable.arcticons__folder_music),
    MostPlayed(title = "Most Played", bg = R.drawable.most_played, icon = R.drawable.arcticons__vibe_music),
    RecentlyPlayed(title = "Recently Played", bg = R.drawable.recently_played, icon = R.drawable.arcticons__niagara_launcher_recently_installed),
}

@Composable
fun PlaylistsPage(
    modifier: Modifier = Modifier, viewModel: SongsViewModel = hiltViewModel(), playingViewModel: PlayingViewModel = hiltViewModel(),
    openPlaylist: (PlaylistsOptions)-> Unit, openCustomPlaylist: (PlaylistEntity)-> Unit, openPlaylistMenu: (PlaylistEntity)->Unit) {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberContainedSearchBarState()
    val scope = rememberCoroutineScope()
    val songsState by viewModel.state.collectAsStateWithLifecycle()
    val favourites by viewModel.favourites.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
            //colors = TextFieldDefaults.colors(),
            placeholder = {
                Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search songs")
            },
            leadingIcon = { Icon(imageVector = ImageVector.vectorResource(R.drawable.fluent__search_24_regular), contentDescription = "Search") },
            trailingIcon = {
                IconButton(onClick = {
                    scope.launch {
                        searchBarState.animateToCollapsed()
                        textFieldState.clearText()
                    }
                }) {
                    Icon(imageVector = ImageVector.vectorResource(R.drawable.material_symbols__cancel_rounded__1_), contentDescription = "cancel")
                }
            },
        )
    }

    val searchResults by remember {
        derivedStateOf {
            val query = textFieldState.text.toString()
            if(textFieldState.text.toString().isNotEmpty()){
                songsState.songs.filter {
                    it.album.lowercase().contains(query) || it.title.lowercase().contains(query) || it.artist.lowercase().contains(query)
                }
            }else{
                emptyList()
            }
        }
    }

    val itemsList = listOf(
        Pair(PlaylistsOptions.Favourites, favourites.size),
        Pair(PlaylistsOptions.RecentlyAdded, songsState.last.size),
        Pair(PlaylistsOptions.MostPlayed, null),
        Pair(PlaylistsOptions.RecentlyPlayed, null)
    )

    Box(modifier.fillMaxSize().semantics { isTraversalGroup = true }, contentAlignment = Alignment.TopCenter) {
        Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SearchBar(
                modifier = Modifier.fillMaxWidth().semantics { traversalIndex = 0f },
                state = searchBarState,
                inputField = inputField,
            )
            LazyVerticalGrid(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(itemsList.size) { index ->
                    val (option, count) = itemsList[index]
                    Surface(modifier = Modifier.fillMaxWidth().height(130.dp).clickable{ openPlaylist(option) }, shape = RoundedCornerShape(0.dp)) {
                        Image(modifier = Modifier.fillMaxSize().blur(radius = 3.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle),
                            bitmap = ImageBitmap.imageResource(option.bg), contentScale = ContentScale.Crop, contentDescription = null)
                        Box(modifier = Modifier.fillMaxSize().background(color = Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.CenterVertically)) {
                                Icon(modifier = Modifier.size(60.dp),
                                    imageVector = ImageVector.vectorResource(option.icon),
                                    contentDescription = null,
                                    tint = Color.White)
                                Text(text = option.title, fontSize = 16.sp, fontWeight = FontWeight.Light, letterSpacing = 1.2.sp, lineHeight = 1.sp, color = Color.White)
                                count?.let {
                                    Text(text = "Songs(${it})",  fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 1.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
            playlists.forEach {
                PlaylistEntityView(
                    playlistEntity = it, clicked = { openCustomPlaylist(it) }, menu = { openPlaylistMenu(it) })
            }
        }
        ExpandedFullScreenSearchBar(state = searchBarState, inputField = inputField) {
            LazyColumn {
                items(count = searchResults.size) { index ->
                    val song = searchResults[index]
                    SongView(song = song){
                        //onResultClick(song)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PlaylistPagePreview(){
    BMusicTheme {
        Surface {
            PlaylistsPage(
                openPlaylist = {},
                openPlaylistMenu = {},
                openCustomPlaylist = {}
            )
        }
    }
}