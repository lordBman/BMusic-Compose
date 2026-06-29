package com.bsoft.compose.bmusic.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
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
fun Main(modifier: Modifier = Modifier, viewModel: SongsViewModel = viewModel(), playingViewModel: PlayingViewModel = viewModel()){
    val rootBackStack = remember { mutableStateListOf<Route>(Route.Home) }

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
                    toAlbum = { rootBackStack.add(Route.Album) },
                    toArtist = { rootBackStack.add(Route.Artist) }
                )
            }
            entry<Route.Album>{
                AlbumScreen(modifier = modifier, back = { rootBackStack.removeLastOrNull() })
            }
            entry<Route.Artist>{
                ArtistScreen(modifier = modifier, back = { rootBackStack.removeLastOrNull() })
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