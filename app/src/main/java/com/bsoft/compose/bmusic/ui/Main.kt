package com.bsoft.compose.bmusic.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.bsoft.compose.bmusic.Route
import com.bsoft.compose.bmusic.ui.screens.HomeScreen
import com.bsoft.compose.bmusic.ui.screens.PlaylistScreen
import com.bsoft.compose.bmusic.ui.screens.SearchScreen
import com.bsoft.compose.bmusic.ui.screens.SettingsScreen
import com.bsoft.compose.bmusic.viewmodels.SongsViewModel
import kotlin.collections.listOf

@Composable
fun Main(modifier: Modifier = Modifier, viewModel: SongsViewModel = viewModel()){
    val rootBackStack = rememberNavBackStack(Route.Home)

    NavDisplay(
        backStack = rootBackStack,
        onBack = { rootBackStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator{ false }
        ),
        entryProvider = entryProvider {
            entry<Route.Home>{
                HomeScreen(modifier = modifier, viewModel = viewModel)
            }
            entry<Route.Search>{
                SearchScreen(modifier = modifier)
            }
            entry<Route.Playlist> {
                PlaylistScreen(modifier = modifier)
            }
            entry<Route.Settings>{
                SettingsScreen(modifier = modifier)
            }
        }
    )
}