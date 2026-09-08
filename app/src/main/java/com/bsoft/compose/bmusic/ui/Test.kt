package com.bsoft.compose.bmusic.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TwoRowsTopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.recordable.ReorderableItem
import com.bsoft.compose.recordable.detectReorderAfterLongPress
import com.bsoft.compose.recordable.rememberReorderableLazyListState
import com.bsoft.compose.recordable.reorderable
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A sample for a [TwoRowsTopAppBar] that collapses when the content is scrolled up, and appears
 * when the content is completely scrolled back down.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CustomTwoRowsTopAppBar() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TwoRowsTopAppBar(
                title = { expanded ->
                    if (expanded) {
                        Text("Expanded TopAppBar", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    } else {
                        Text("Collapsed TopAppBar", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                },
                subtitle = { expanded ->
                    if (expanded) {
                        Text(
                            "Expanded Subtitle",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = 24.dp),
                        )
                    } else {
                        Text("Collapsed Subtitle", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                },
                collapsedHeight = 64.dp,
                expandedHeight = 156.dp,
                navigationIcon = {
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Left
                            ),
                        tooltip = {
                            PlainTooltip(
                                modifier =
                                    Modifier.semantics {
                                        // TODO(b/496338253): Remove this modifier once bug where
                                        //   tooltip text is not announced by a11y screen readers
                                        //   is resolved.
                                        liveRegion = LiveRegionMode.Assertive
                                        paneTitle = "Menu"
                                    }
                            ) {
                                Text("Menu")
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = { /* doSomething() */ }) {
                            Icon(imageVector = ImageVector.vectorResource(R.drawable.glyphs__grid_bold), contentDescription = "Menu")
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        content = { innerPadding ->
            Column(
                Modifier.fillMaxWidth().padding(innerPadding).verticalScroll(rememberScrollState())
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyLarge
                ) {
                    Text(text = remember { LoremIpsum().values.first() })
                }
            }
        },
    )
}

@Preview
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableLazyColumn(
    modifier: Modifier = Modifier
) {
    // 1. Manage state and backing list
    var items by remember { mutableStateOf((1..20).map { "Item $it" }) }
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // 2. Track drag parameters
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var overscrollJob by remember { mutableStateOf<Job?>(null) }

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize().pointerInput(Unit) {
                // 3. Capture gestures globally over the LazyColumn
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        lazyListState.layoutInfo.visibleItemsInfo
                            .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
                            ?.let { draggedIndex = it.index }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount.y

                        val currentIndex = draggedIndex ?: return@detectDragGesturesAfterLongPress
                        val itemInfo = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == currentIndex } ?: return@detectDragGesturesAfterLongPress

                        val currentItemCenter = itemInfo.offset + (itemInfo.size / 2) + dragOffset

                        // 4. Check for swap thresholds with neighboring items
                        lazyListState.layoutInfo.visibleItemsInfo
                            .firstOrNull { item ->
                                val itemCenter = item.offset + (item.size / 2)
                                when {
                                    item.index > currentIndex -> currentItemCenter > itemCenter
                                    item.index < currentIndex -> currentItemCenter < itemCenter
                                    else -> false
                                }
                            }?.let { hoveringItem ->
                                val mutableList = items.toMutableList()
                                val item = mutableList.removeAt(currentIndex)
                                mutableList.add(hoveringItem.index, item)
                                items = mutableList

                                // Adjust structural drag offset during structural mutation
                                dragOffset += (itemInfo.offset - hoveringItem.offset)
                                draggedIndex = hoveringItem.index
                            }

                        // 5. Handle automated list scrolling when dragged near borders
                        val viewportHeight = lazyListState.layoutInfo.viewportEndOffset
                        val topBoundary = viewportHeight * 0.1f
                        val bottomBoundary = viewportHeight * 0.9f
                        val globalY = itemInfo.offset + dragOffset

                        if (globalY < topBoundary && overscrollJob?.isActive != true) {
                            overscrollJob = scope.launch { lazyListState.scrollBy(-15f) }
                        } else if (globalY > bottomBoundary && overscrollJob?.isActive != true) {
                            overscrollJob = scope.launch { lazyListState.scrollBy(15f) }
                        } else if (globalY in topBoundary..bottomBoundary) {
                            overscrollJob?.cancel()
                        }
                    },
                    onDragEnd = {
                        draggedIndex = null
                        dragOffset = 0f
                        overscrollJob?.cancel()
                    },
                    onDragCancel = {
                        draggedIndex = null
                        dragOffset = 0f
                        overscrollJob?.cancel()
                    }
                )
            }
    ) {
        itemsIndexed(items, key = { _, item -> item }) { index, item ->
            val isDragging = index == draggedIndex

            // 6. Smooth elevation & translation effects
            val elevation by animateFloatAsState(if (isDragging) 8f else 0f, label = "elevation")

            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp).graphicsLayer {
                        // Apply movement translation explicitly to the active item
                translationY = if (isDragging) dragOffset else 0f
                shadowElevation = elevation
            }.animateItem(), // Automatic smooth layout transitions for surrounding items
                colors = CardDefaults.cardColors(containerColor = if (isDragging) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = item, modifier = Modifier.weight(1f))
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.glyphs__grid_bold),
                        contentDescription = "Drag Handle",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun VerticalReorderList() {
    val data = remember { mutableStateListOf<String>().apply {
        addAll(List(100) { "Item $it" })
    } }
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        data.apply {
            add(to.index, removeAt(from.index))
        }
    })

    LazyColumn(state = state.listState, modifier = Modifier.reorderable(state).detectReorderAfterLongPress(state)) {
        items(data, key = { item -> item }) { item ->
            //val item = data[index]
            ReorderableItem(state, key = item) { isDragging ->
                val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                Column(modifier = Modifier.fillMaxWidth().shadow(elevation.value).background(Color.White)) {
                    Text(modifier = Modifier.padding(12.dp), text = item)
                }
            }
        }
    }
}
