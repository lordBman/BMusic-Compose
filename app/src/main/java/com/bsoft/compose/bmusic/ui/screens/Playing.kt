package com.bsoft.compose.bmusic.ui.screens

import android.graphics.Bitmap
import android.util.Size
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.data.models.Song
import com.bsoft.compose.bmusic.ui.components.BitmapImage
import com.bsoft.compose.bmusic.ui.components.EqualizerView
import com.bsoft.compose.bmusic.ui.components.PlayerControl
import com.bsoft.compose.bmusic.ui.components.PlayingPage
import com.bsoft.compose.bmusic.ui.components.PlayingTab
import com.bsoft.compose.bmusic.ui.components.QueueSongView
import com.bsoft.compose.bmusic.ui.components.TransparentStatusBarHandler
import com.bsoft.compose.bmusic.utils.Util
import com.bsoft.compose.bmusic.viewmodels.EqualizerViewModel
import com.bsoft.compose.bmusic.viewmodels.PlayingViewModel
import com.bsoft.compose.recordable.ReorderableItem
import com.bsoft.compose.recordable.detectReorderAfterLongPress
import com.bsoft.compose.recordable.rememberReorderableLazyListState
import com.bsoft.compose.recordable.reorderable
import kotlinx.coroutines.launch

@Composable
fun PlayingScreen(modifier: Modifier = Modifier, playingViewModel: PlayingViewModel = hiltViewModel(), equalizerViewModel: EqualizerViewModel = hiltViewModel(), back: ()-> Unit){
    TransparentStatusBarHandler()

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { PlayingPage.entries.size })

    val playState by playingViewModel.state.collectAsStateWithLifecycle()
    val queueState by playingViewModel.queueState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(queueState.current) {
        bitmap = Util.loadArtwork(context, queueState.current?.artworkUri, Size(400, 400))
    }

    val isEqualizerEnabled by equalizerViewModel.isEqualizerEnabled.collectAsStateWithLifecycle()
    val equalizerState by equalizerViewModel.state.collectAsStateWithLifecycle()
    val selectedBand by equalizerViewModel.selected.collectAsStateWithLifecycle()
    val customValues by equalizerViewModel.bandValues.collectAsStateWithLifecycle()

    val recordableState = rememberReorderableLazyListState(onMove = { from, to ->

    })

    Surface(modifier = modifier) {
        if(bitmap == null){
            Image(modifier = Modifier.fillMaxSize().blur(radius = 16.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle), painter = painterResource(id = R.drawable.lady), contentScale = ContentScale.Crop, contentDescription = null)
        }else{
            BitmapImage(modifier = Modifier.fillMaxSize().blur(radius = 16.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle), bitmap = bitmap as Bitmap, contentScale = ContentScale.Crop)
        }
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Black.copy(alpha = 0.2f)) { screenPadding ->
            Column(modifier = Modifier.padding(screenPadding).fillMaxSize()) {
                Box(modifier = Modifier.padding(top = 10.dp).fillMaxWidth()) {
                    PlayingTab(modifier = Modifier.align(Alignment.Center), active = pagerState.currentPage){
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(it)
                        }
                    }
                    IconButton(modifier = Modifier.align(Alignment.CenterStart), onClick = { back() }) {
                        Icon(tint = Color.White,
                            imageVector = ImageVector.vectorResource(R.drawable.material_symbols__arrow_back_ios_new_rounded),
                            contentDescription = "")
                    }
                }
                HorizontalPager(modifier = Modifier.weight(1f), state = pagerState) { currentPage ->
                    when(currentPage){
                        0 ->{
                            LazyColumn(state = recordableState.listState, modifier = Modifier.fillMaxSize().reorderable(recordableState).detectReorderAfterLongPress(recordableState), contentPadding = PaddingValues(top = 10.dp)) {
                                items(queueState.queue.map { Song.fromMediaItem(it) }, { it.id}) { song ->
                                    ReorderableItem(recordableState, key = song.id) { isDragging ->
                                        //val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                                        QueueSongView(modifier = Modifier.fillMaxWidth(), moving = isDragging, song = song){
                                            //playingViewModel.playPlaylistIndex(index = index)
                                        }
                                    }
                                }
                            }
                        }
                        1 ->{
                            PlayerControl(playingState = playState, queueState = queueState, bitmap = bitmap,
                                next = { playingViewModel.next() },
                                previous = { playingViewModel.previous() },
                                playToggled = { playingViewModel.togglePlayPause() },
                                forward = { playingViewModel.forward() },
                                rewind = { playingViewModel.rewind() },
                                seek = { playingViewModel.seek(it) },
                                repeatToggled = { playingViewModel.toggleRepeat() },
                                shuffleToggled = { playingViewModel.toggleShuffle() }
                            )
                        }
                        2 ->{
                            EqualizerView(enabled = isEqualizerEnabled, state = equalizerState,
                                selectedBand = selectedBand, customValues = customValues,
                                selectedPreset = { equalizerViewModel.choosePreset(it.preset) },
                                bandModified = { band, level -> equalizerViewModel.modifyBand(band, level) },
                                toggleEnable = { equalizerViewModel.toggleEnable() })
                        }
                    }
                }
            }
        }
    }
}