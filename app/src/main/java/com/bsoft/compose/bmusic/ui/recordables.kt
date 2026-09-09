package com.bsoft.compose.bmusic.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bsoft.compose.bmusic.R

data class Song(
    val id: Long,
    val title: String,
    val artist: String
)

@Composable
private fun SongRow(
    song: Song,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.03f else 1f,
        animationSpec = tween(
            durationMillis = 150
        ),
        label = "drag_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isDragging) 8.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 150
        ),
        label = "drag_elevation"
    )

    val backgroundColor by animateColorAsState(
        targetValue =
            if (isDragging) {
                MaterialTheme.colorScheme.surfaceContainerHighest
            } else {
                MaterialTheme.colorScheme.surface
            },
        animationSpec = tween(
            durationMillis = 150
        ),
        label = "drag_background"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(elevation)
            .zIndex(
                if (isDragging) 1f else 0f
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RectangleShape
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Drag handle
            val handleScale by animateFloatAsState(
                targetValue = if (isDragging) 1.2f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "handle_scale"
            )

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.streamline_ultimate__move_expand_vertical),
                contentDescription = "Reorder",
                tint =
                    if (isDragging)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,

                modifier = Modifier
                    .size(32.dp)
                    .graphicsLayer {
                        scaleX = handleScale
                        scaleY = handleScale
                    }
                    .pointerInput(Unit) {

                        detectDragGestures(

                            onDragStart = {
                                onDragStart()
                            },

                            onDragEnd = {
                                onDragEnd()
                            },

                            onDragCancel = {
                                onDragEnd()
                            },

                            onDrag = { change, dragAmount ->

                                change.consume()

                                onDrag(dragAmount.y)
                            }
                        )
                    }
            )
        }
    }
}

@Composable
fun ReorderableSongList(
    songs: List<Song>,
    onSongsReordered: (List<Song>) -> Unit
) {
    var items by remember {
        mutableStateOf(songs)
    }

    var draggedItemIndex by remember {
        mutableStateOf<Int?>(null)
    }

    var dragOffset by remember {
        mutableFloatStateOf(0f)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = items,
            key = { _, song -> song.id }
        ) { index, song ->

            val isDragging = draggedItemIndex == index

            SongRow(
                song = song,
                isDragging = isDragging,

                onDragStart = {
                    draggedItemIndex = index
                    dragOffset = 0f
                },

                onDrag = { dragAmount ->

                    dragOffset += dragAmount

                    val currentIndex =
                        draggedItemIndex ?: return@SongRow

                    val itemHeight = 72f

                    // Move down
                    if (dragOffset > itemHeight / 2) {

                        if (currentIndex < items.lastIndex) {

                            val newList = items.toMutableList()

                            val item = newList.removeAt(currentIndex)
                            newList.add(currentIndex + 1, item)

                            items = newList
                            draggedItemIndex = currentIndex + 1

                            dragOffset -= itemHeight
                        }
                    }

                    // Move up
                    else if (dragOffset < -itemHeight / 2) {

                        if (currentIndex > 0) {

                            val newList = items.toMutableList()

                            val item = newList.removeAt(currentIndex)
                            newList.add(currentIndex - 1, item)

                            items = newList
                            draggedItemIndex = currentIndex - 1

                            dragOffset += itemHeight
                        }
                    }
                },

                onDragEnd = {
                    draggedItemIndex = null
                    dragOffset = 0f

                    onSongsReordered(items)
                }
            )
        }
    }
}

@Preview
@Composable
fun PlaylistScreen() {
    var songs by remember {
        mutableStateOf(
            listOf(
                Song(1, "Song One", "Artist One"),
                Song(2, "Song Two", "Artist Two"),
                Song(3, "Song Three", "Artist Three"),
                Song(4, "Song Four", "Artist Four"),
                Song(5, "Song Five", "Artist Five")
            )
        )
    }

    ReorderableSongList(
        songs = songs,
        onSongsReordered = {
            songs = it
        }
    )
}