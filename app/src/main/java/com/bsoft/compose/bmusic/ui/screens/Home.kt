package com.bsoft.compose.bmusic.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bsoft.compose.bmusic.HomeDestination
import com.bsoft.compose.bmusic.R
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
fun HomeScreen(modifier: Modifier = Modifier, viewModel: SongsViewModel = viewModel(), playingViewModel: PlayingViewModel = viewModel()){
    val content = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val sheetState = rememberBottomSheetState(initialValue = SheetValue.Expanded)
    var showPlaying by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { HomeDestination.entries.size })

    val songState by viewModel.state.collectAsStateWithLifecycle()
    val playState by playingViewModel.state.collectAsStateWithLifecycle()

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->

        val allGranted = resultMap.values.all{ it }
        viewModel.onStoragePermissionResult(allGranted)
        if(allGranted){
            playingViewModel.initializeMediaController(content)
            viewModel.query()
        }
    }

    LaunchedEffect(Unit) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.FOREGROUND_SERVICE)
        } else {
            mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK)
        }

        val allGranted = permissions.map {
            ContextCompat.checkSelfPermission(content, it) == PackageManager.PERMISSION_GRANTED
        }.all{ it }

        if(allGranted){
            playingViewModel.initializeMediaController(content)
            viewModel.query()
        }else{
            permissionsLauncher.launch(permissions.toTypedArray())
        }
    }

    Scaffold(modifier = modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "BMusic")
                },
                actions = {
                    Icon(modifier = Modifier.size(28.dp), imageVector = ImageVector.vectorResource(R.drawable.fluent__search_24_filled), contentDescription = null)
                    Icon(modifier = Modifier.size(28.dp), imageVector = ImageVector.vectorResource(R.drawable.glyphs__cog_bold), contentDescription = null)
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
                    0 -> SongsPage(songs = songState.songs){ index, song ->
                        playingViewModel.playSong(index = index)
                    }
                    1 -> AlbumPage(albums = songState.albums){
                        playingViewModel.playLibrary(it.toMediaItem())
                    }
                    2 -> ArtistsPage(artists = songState.artists){
                        playingViewModel.playLibrary(it.toMediaItem())
                    }
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
                    seek = { playingViewModel.seek(it) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview(){
    BMusicTheme {
        HomeScreen()
    }
}
