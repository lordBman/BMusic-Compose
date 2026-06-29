package com.bsoft.compose.bmusic.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bsoft.compose.bmusic.ui.components.Search
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import com.bsoft.compose.bmusic.viewmodels.SongsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(modifier: Modifier = Modifier, viewModel: SongsViewModel = viewModel(),){
    Surface {
        Search(modifier = modifier, query = "", onQueryChange = {}, onSearch = {}, searchResults = emptyList(), onResultClick = {})
    }
}


@Preview
@Composable
private fun SearchScreenPreview(){
    BMusicTheme {
        SearchScreen()
    }
}