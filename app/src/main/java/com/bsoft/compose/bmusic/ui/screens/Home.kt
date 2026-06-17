package com.bsoft.compose.bmusic.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.HomeDestination
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.ui.components.Playing
import com.bsoft.compose.bmusic.ui.pages.AlbumPage
import com.bsoft.compose.bmusic.ui.pages.ArtistsPage
import com.bsoft.compose.bmusic.ui.pages.PlaylistPage
import com.bsoft.compose.bmusic.ui.pages.SongsPage
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(modifier: Modifier = Modifier){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val pagerState = rememberPagerState(pageCount = { HomeDestination.entries.size })
    val coroutineScope = rememberCoroutineScope()
    var showPlaying by remember { mutableStateOf(false) }

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
            ) },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth().padding(10.dp).clickable{ showPlaying = true }, shadowElevation = 4.dp, shape = RoundedCornerShape(12.dp)){
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)){
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)){
                        Surface(modifier = Modifier.size(60.dp), color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(10.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(modifier = Modifier.size(30.dp), tint = MaterialTheme.colorScheme.primary, imageVector = ImageVector.vectorResource(R.drawable.solar__music_notes_bold_duotone), contentDescription = null)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)){
                            Text("Name of the Songs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, overflow = TextOverflow.MiddleEllipsis)
                            Text("Album: Album Name", fontSize = 12.sp, fontWeight = FontWeight.Light, overflow = TextOverflow.MiddleEllipsis)
                        }
                        Icon(modifier = Modifier.size(30.dp), imageVector = ImageVector.vectorResource(R.drawable.fluent__play_24_filled), contentDescription = null)
                    }
                    Slider(modifier = Modifier.fillMaxWidth().height(2.dp),
                        track = { sliderState -> SliderDefaults.Track(
                                modifier = Modifier.height(2.dp),
                                sliderState = sliderState,
                                thumbTrackGapSize = 0.dp,
                                trackInsideCornerSize = 0.dp
                            )
                        },
                        thumb = {},
                        onValueChange = {}, value = 20f, valueRange = 0f..50f)
                }
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
            HorizontalPager(modifier = modifier.fillMaxWidth().weight(1f), state = pagerState) {
                when (pagerState.currentPage) {
                    0 -> SongsPage()
                    1 -> AlbumPage()
                    2 -> ArtistsPage()
                    3 -> PlaylistPage()
                }
            }
        }
        if(showPlaying){
            ModalBottomSheet(
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                onDismissRequest = { showPlaying = false },
                dragHandle = null) {
                Playing()
            }
        }
    }
}

@Preview
@Composable
private fun HomePreview(){
    BMusicTheme {
        Home()
    }
}
