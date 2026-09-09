package com.bsoft.compose.bmusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import kotlinx.coroutines.launch

@Composable
fun AlphabeticalListWithSidebar() {
    val itemsList = listOf("Apple", "Apricot", "Banana", "Cherry", "Date", "Elderberry", "Fig", "Grape")
    val alphabet = ('A'..'Z').map { it.toString() }

    // Group items dynamically by their first letter
    val groupedItems = itemsList.groupBy { it.first().uppercase() }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Store the vertical center Y-coordinates of our alphabet letters
    val alphabetOffsets = remember { mutableStateMapOf<String, Float>() }

    Row(modifier = Modifier.fillMaxSize()) {
        // Main Alphabetical List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            groupedItems.forEach { (letter, items) ->
                // Sticky Header for the letter
                stickyHeader {
                    Text(
                        text = letter,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                // List Items under the letter
                items(items) { item ->
                    Text(text = item, modifier = Modifier.padding(16.dp))
                }
            }
        }

        // Alphabet Sidebar
        Column(
            modifier = Modifier
                .width(30.dp)
                .fillMaxHeight()
                .background(Color.DarkGray)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, _ ->
                        // Find the letter closest to the drag's current Y position
                        val currentY = change.position.y
                        val closestLetter = alphabetOffsets.entries
                            .minByOrNull { kotlin.math.abs(it.value - currentY) }?.key

                        // Scroll to the item that starts with that letter
                        closestLetter?.let { letter ->
                            val sectionIndex = itemsList.indexOfFirst {
                                it.first().uppercase() == letter
                            }
                            if (sectionIndex != -1) {
                                coroutineScope.launch {
                                    listState.scrollToItem(sectionIndex)
                                }
                            }
                        }
                    }
                },
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            alphabet.forEach { letter ->
                Text(
                    text = letter,
                    color = if (/* Check if active */ true) Color.White else Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        // Register the vertical center of each letter text
                        alphabetOffsets[letter] = coordinates.boundsInParent().center.y
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun AlphabeticalListWithSidebarPreview() {
    BMusicTheme {
        Surface {
            AlphabeticalListWithSidebar()
        }
    }
}