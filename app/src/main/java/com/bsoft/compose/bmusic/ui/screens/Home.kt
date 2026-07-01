package com.bsoft.compose.bmusic.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bsoft.compose.bmusic.HomeDestination
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.Route
import com.bsoft.compose.bmusic.data.models.Album
import com.bsoft.compose.bmusic.data.models.Artist
import com.bsoft.compose.bmusic.ui.components.BottomControl
import com.bsoft.compose.bmusic.ui.components.Playing
import com.bsoft.compose.bmusic.ui.pages.AlbumPage
import com.bsoft.compose.bmusic.ui.pages.ArtistsPage
import com.bsoft.compose.bmusic.ui.pages.PlaylistsPage
import com.bsoft.compose.bmusic.ui.pages.SongsPage
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import com.bsoft.compose.bmusic.viewmodels.PlayingViewModel
import com.bsoft.compose.bmusic.viewmodels.SongsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier, viewModel: SongsViewModel = viewModel(), playingViewModel: PlayingViewModel = hiltViewModel(),
    toScreen: (Route)-> Unit, toAlbum: (Album)-> Unit, toArtist: (Artist)-> Unit
){
    val coroutineScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val sheetState = rememberBottomSheetState(
        enabledValues = setOf(Expanded, Hidden),
        initialValue = Expanded
    )
    var showPlaying by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { HomeDestination.entries.size })

    val songState by viewModel.state.collectAsStateWithLifecycle()
    val playState by playingViewModel.state.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "BMusic")
                },
                actions = {
                    IconButton(onClick = { toScreen(Route.Search) }) {
                        Icon(modifier = Modifier.size(28.dp), imageVector = ImageVector.vectorResource(R.drawable.fluent__search_24_filled), contentDescription = null)
                    }
                    IconButton(onClick = { toScreen(Route.Settings) }) {
                        Icon(modifier = Modifier.size(28.dp), imageVector = ImageVector.vectorResource(R.drawable.glyphs__cog_bold), contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomControl(state = playState, clicked = { showPlaying = true }){
                playingViewModel.togglePlayPause()
            }
        }
        ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            PrimaryTabRow(selectedTabIndex = pagerState.currentPage, tabs = {
                HomeDestination.entries.forEachIndexed { index, destination ->
                    val selected = pagerState.currentPage == index
                    Tab(
                        selected = selected,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        icon = {
                            if(selected){
                                Icon(modifier = Modifier.size(30.dp), imageVector = ImageVector.vectorResource(destination.selectedIcon), contentDescription = null)
                            }else{
                                Icon(modifier = Modifier.size(30.dp), imageVector = ImageVector.vectorResource(destination.icon), contentDescription = null)
                            }
                        },
                        text = {
                            Text(text = destination.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    )
                }
            })
            HorizontalPager(modifier = modifier.fillMaxWidth().weight(1f), state = pagerState) { page ->
                when (page) {
                    0 -> SongsPage(modifier = Modifier.fillMaxSize(), songs = songState.songs){ index ->
                        playingViewModel.playSong(index = index)
                    }
                    1 -> AlbumPage(albums = songState.albums){
                        //playingViewModel.playLibrary(it.toMediaItem())
                        toAlbum(it)
                    }
                    2 -> ArtistsPage(artists = songState.artists, chosen = { toArtist(it) })
                    3 -> PlaylistsPage()
                }
            }
        }

        if(showPlaying){
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showPlaying = false },
                dragHandle = null) {
                Playing(playingState = playState,
                    next = { playingViewModel.next() },
                    previous = { playingViewModel.previous() },
                    playToggled = { playingViewModel.togglePlayPause() },
                    forward = { playingViewModel.forward() },
                    rewind = { playingViewModel.rewind() },
                    seek = { playingViewModel.seek(it) },
                    repeatToggled = { playingViewModel.toggleRepeat() },
                    shuffleToggled = { playingViewModel.toggleShuffle() }
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview(){
    BMusicTheme {
        HomeScreen(toScreen = {}, toAlbum = {}, toArtist = {})
    }
}
