package com.bsoft.compose.bmusic.ui.screens

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TwoRowsTopAppBar
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.ui.components.ListControls
import com.bsoft.compose.bmusic.ui.components.PlaylistMenu
import com.bsoft.compose.bmusic.ui.components.QueueSongView
import com.bsoft.compose.bmusic.ui.components.TransparentStatusBarHandler
import com.bsoft.compose.bmusic.ui.components.TransparentTopBar
import com.bsoft.compose.bmusic.ui.components.alerts.PlaylistDeleteAlertDialog
import com.bsoft.compose.bmusic.ui.pages.PlaylistsOptions
import com.bsoft.compose.bmusic.utils.ScreenOverlay
import com.bsoft.compose.bmusic.utils.toTimeFormat
import com.bsoft.compose.bmusic.viewmodels.PlayingViewModel
import com.bsoft.compose.bmusic.viewmodels.SongsViewModel

private data class PlaylistScreenState(val songs: List<Song> = emptyList(), val title: String = "", val bg: Int)

@Composable
private fun SelectionMenuItem(@DrawableRes icon: Int, label: String, clicked: ()-> Unit){
    Column(modifier = Modifier.clickable{ clicked() }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(imageVector = ImageVector.vectorResource(icon), contentDescription = null)
        Text(text = label, fontSize = 16.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    modifier: Modifier = Modifier, playlistsOptions: PlaylistsOptions? = null, id: Long? = null,
    playingViewModel: PlayingViewModel = hiltViewModel(), songsViewModel: SongsViewModel = hiltViewModel(),
    add:(Long, String)-> Unit, back: ()-> Unit){

    TransparentStatusBarHandler()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    //val queueState by playingViewModel.queueState.collectAsStateWithLifecycle()
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    // 3. Linearly interpolate between your expanded and collapsed colors
    val titleColor = lerp(
        start = Color.White,    // Color when fully EXPANDED
        stop = MaterialTheme.colorScheme.onSurface,      // Color when fully COLLAPSED
        fraction = collapsedFraction
    )

    var state by remember { mutableStateOf(PlaylistScreenState(bg = R.drawable.playlist_bg)) }
    var editMode by remember { mutableStateOf(false) }

    LaunchedEffect(playlistsOptions, id) {
        when(playlistsOptions){
            PlaylistsOptions.MostPlayed -> {
                state = state.copy(
                    songs = playingViewModel.getMostPlayed.value.mapNotNull {
                        songsViewModel.loadSongsByID(it.song)
                    },
                    title = playlistsOptions.title,
                    bg = playlistsOptions.bg
                )
            }
            PlaylistsOptions.RecentlyAdded -> {
                state = state.copy(
                    songs = songsViewModel.state.value.last,
                    title = playlistsOptions.title,
                    bg = playlistsOptions.bg
                )
            }
            PlaylistsOptions.RecentlyPlayed -> {
                state = state.copy(
                    songs = playingViewModel.getLastPlayed.value.mapNotNull {
                        songsViewModel.loadSongsByID(it.song)
                    },
                    title = playlistsOptions.title,
                    bg = playlistsOptions.bg
                )
            }
            PlaylistsOptions.Favourites -> {
                state = state.copy(
                    songs = songsViewModel.favourites.value.mapNotNull {
                        songsViewModel.loadSongsByID(it.song)
                    },
                    title = playlistsOptions.title,
                    bg = playlistsOptions.bg
                )
            }
            else -> {
                id?.let {
                    val playlist = songsViewModel.playlistRepository.getPlayList(it)
                    state = state.copy(
                        title = playlist.name,
                        songs = songsViewModel.playlistRepository.getSongsFromPlayList(it).map { entity-> entity.song }
                    )
                }
            }
        }
    }

    val duration by remember { derivedStateOf { state.songs.sumOf { it.duration }.toTimeFormat() } }

    var showOverlay by remember { mutableStateOf(ScreenOverlay()) }
    val playlistMenuSheetState = rememberBottomSheetState(
        enabledValues = setOf(SheetValue.Expanded, SheetValue.Hidden),
        initialValue = SheetValue.Expanded
    )

    BackHandler(enabled = editMode) {
        editMode = false
    }

    Image(modifier = Modifier.fillMaxSize().blur(radius = 10.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle), painter = painterResource(id = state.bg), contentScale = ContentScale.Crop, contentDescription = null)
    Scaffold(modifier = modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Black.copy(alpha = 0.2f),
        topBar = {
            TransparentTopBar(
                title = state.title,
                details = listOf("${state.songs.size} songs - $duration"),
                scrollBehavior = scrollBehavior,
                add = { id?.let { add(it, state.title)  } },
                playAll = {

                },
                shuffle = {

                },
                menuClicked = {
                    showOverlay = ScreenOverlay(item = ScreenOverlay.Item.PlaylistMenu)
                },
                bg = state.bg , back = back)
        },
        bottomBar = {
            if(editMode){
                Surface(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        SelectionMenuItem(label = "play", icon = R.drawable.solar__play_outline) { }
                        SelectionMenuItem(label = "Remove", icon = R.drawable.fluent__heart_24_regular) { }
                        SelectionMenuItem(label = "Remove", icon = R.drawable.solar__trash_bin_trash_linear) { }
                    }
                }
            }
        }
    ) { padding ->
        Surface(modifier = modifier.padding(padding), color = Color.Transparent) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(count = state.songs.size) { index ->
                    QueueSongView(
                        song = state.songs[index],
                        showMenu = true, menuClicked = {},
                        selectionMode = editMode
                    ) {  }
                }
            }
        }

        if(showOverlay.item == ScreenOverlay.Item.PlaylistMenu){
            id?.let { id ->
                ModalBottomSheet(sheetState = playlistMenuSheetState, onDismissRequest = { showOverlay = ScreenOverlay() }, dragHandle = null) {
                    PlaylistMenu(
                        playlist = id,
                        playlistName = state.title,
                        addSongs = { add(it, state.title) },
                        play = {},
                        shuffle = {},
                        rename = {},
                        edit = { editMode = true },
                        delete = {
                            showOverlay = showOverlay.copy(item = ScreenOverlay.Item.PlaylistDeleteDialog)
                        }
                    )
                }
            }
        }

        if(showOverlay.item == ScreenOverlay.Item.PlaylistDeleteDialog){
            id?.let {
                PlaylistDeleteAlertDialog(
                    playlist = it,
                    playlistName = state.title,
                    onConfirmation = {},
                    onDismissRequest = {
                        showOverlay = ScreenOverlay()
                    }
                )
            }
        }
    }
}