package com.bsoft.compose.bmusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.Song
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme

@Composable
fun TestItem(resultText: String, onclick: ()-> Unit = {}){
    ListItem(
        headlineContent = { Text(resultText) },
        supportingContent = { Text("supporting item") },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable { onclick() }.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<Song>,
    onResultClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Track expanded state of search bar
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier.fillMaxSize().semantics { isTraversalGroup = true }, contentAlignment = Alignment.TopCenter) {
        SearchBar(
            modifier = Modifier.semantics { traversalIndex = 0f },
            //colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
            inputField = {
                // Customizable input field implementation
                SearchBarDefaults.InputField(
                    //modifier = Modifier.weight(1f),
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {
                        onSearch(query)
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Search") },
                    leadingIcon = { Icon(imageVector = ImageVector.vectorResource(R.drawable.fluent__search_24_regular), contentDescription = "Search") },
                    trailingIcon = {
                        if(expanded){
                            IconButton(onClick = { expanded = false }) {
                                Icon(imageVector = ImageVector.vectorResource(R.drawable.material_symbols__cancel_rounded__1_), contentDescription = "cancel")
                            }
                        }
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            // Show search results in a lazy column for better performance
            LazyColumn {
                items(count = searchResults.size) { index ->
                    val song = searchResults[index]
                    SongView(song = song){
                        onResultClick(song)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SearchPreview(){
    val songs = (0..20).map {
        Song(id = 0, displayName = "Display Name", title = "Song Title", artist = "Artist name", album = "Album Name", duration = 5000)
    }

    BMusicTheme {
        Surface {
            Search(query = "", onQueryChange = {}, onSearch = {}, searchResults = songs, onResultClick = {})
        }
    }
}