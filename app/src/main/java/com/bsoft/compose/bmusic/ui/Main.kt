package com.bsoft.compose.bmusic.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.bsoft.compose.bmusic.Route
import com.bsoft.compose.bmusic.ui.screens.AlbumScreen
import com.bsoft.compose.bmusic.ui.screens.ArtistScreen
import com.bsoft.compose.bmusic.ui.screens.HomeScreen
import com.bsoft.compose.bmusic.ui.screens.PlaylistScreen
import com.bsoft.compose.bmusic.ui.screens.SearchScreen
import com.bsoft.compose.bmusic.ui.screens.SettingsScreen
import com.bsoft.compose.bmusic.viewmodels.PlayingViewModel
import com.bsoft.compose.bmusic.viewmodels.SongsViewModel
import kotlin.collections.listOf

@Composable
fun Main(modifier: Modifier = Modifier, viewModel: SongsViewModel = viewModel(), playingViewModel: PlayingViewModel = hiltViewModel()){
    val rootBackStack = remember { mutableStateListOf<Route>(Route.Home) }

    val content = LocalContext.current
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

    val songState by viewModel.state.collectAsStateWithLifecycle()

    NavDisplay(
        backStack = rootBackStack,
        onBack = { rootBackStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(checkNotNull<ViewModelStoreOwner>(
                LocalViewModelStoreOwner.current) {
                        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
                    })
        ),
        entryProvider = entryProvider {
            entry<Route.Home>{
                HomeScreen(
                    modifier = modifier, viewModel = viewModel, playingViewModel = playingViewModel,
                    toScreen = { rootBackStack.add(it) },
                    toAlbum = { rootBackStack.add(Route.Album(it.id)) },
                    toArtist = { rootBackStack.add(Route.Artist(it.id)) }
                )
            }
            entry<Route.Album>{ it ->
                AlbumScreen(modifier = modifier, id = it.id, state = songState,
                    play = {  album, index -> playingViewModel.playLibrary(album.toMediaItem(), index) },
                    back = { rootBackStack.removeLastOrNull() })
            }
            entry<Route.Artist>{
                ArtistScreen(modifier = modifier, id = it.id, state = songState,
                    toAlbum = {album -> rootBackStack.add(Route.Album(album.id)) },
                    play = { artist, index -> playingViewModel.playLibrary(artist.toMediaItem(), index) },
                    back = { rootBackStack.removeLastOrNull() })
            }
            entry<Route.Search>{
                SearchScreen(modifier = modifier, viewModel = viewModel)
            }
            entry<Route.Playlist> {
                PlaylistScreen(modifier = modifier, viewModel = viewModel)
            }
            entry<Route.Settings>{
                SettingsScreen(modifier = modifier)
            }
        }
    )
}