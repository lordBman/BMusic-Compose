package com.bsoft.compose.bmusic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberContainedSearchBarState
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedFullScreenContainedSearchBarExample(){
    var expanded by remember { mutableStateOf(false) }

    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberContainedSearchBarState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val appBarWithSearchColors = SearchBarDefaults.appBarWithSearchColors(
        searchBarColors = SearchBarDefaults.containedColors(state = searchBarState)
    )
    val inputField = @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = {
                    Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search")
                },
                leadingIcon = { Icon(imageVector = ImageVector.vectorResource(R.drawable.fluent__search_24_regular), contentDescription = "Search") },
                trailingIcon = {
                    if(expanded) {
                        IconButton(onClick = {
                            scope.launch {
                                searchBarState.animateToCollapsed()
                                expanded = false
                            }
                            //expanded = false
                        }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.material_symbols__cancel_rounded__1_),
                                contentDescription = "cancel"
                            )
                        }
                    }
                }
            )
        }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBarWithSearch(
                scrollBehavior = scrollBehavior,
                state = searchBarState,
                colors = appBarWithSearchColors,
                inputField = inputField,
                navigationIcon = {
                    //SampleNavigationIcon(searchBarState, isAnimated = true)
                    Icon(imageVector = ImageVector.vectorResource(R.drawable.arcticons__musicplayer), contentDescription = null)
                                 },
                actions = {
                    //SampleActions(searchBarState, isAnimated = true)
                          },
            )
            ExpandedFullScreenSearchBar(
                state = searchBarState,
                inputField = inputField,
                colors = appBarWithSearchColors.searchBarColors,
            ) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val list = List(100) { "Result Text $it" }
                    items(count = list.size) {
                        Text(
                            text = list[it],
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        },
    ) { padding ->
        LazyColumn(contentPadding = padding, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val list = List(100) { "Text $it" }
            items(count = list.size) {
                Text(
                    text = list[it],
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun ExpandedFullScreenContainedSearchBarExamplePreview(){
    BMusicTheme {
        ExpandedFullScreenContainedSearchBarExample()
    }
}