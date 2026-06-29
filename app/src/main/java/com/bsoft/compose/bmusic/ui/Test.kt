package com.bsoft.compose.bmusic.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsoft.compose.bmusic.data.states.PlayingState
import com.bsoft.compose.bmusic.ui.components.BottomControl
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Test(){
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetDragHandle = null,
        sheetShadowElevation = 2.dp,
        //sheetPeekHeight = 428.dp,
        sheetContent = {
            Box(contentAlignment = Alignment.TopCenter){
                BottomControl(state = PlayingState(), clicked = {}, ) { }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.fillMaxWidth().height(128.dp), contentAlignment = Alignment.Center) {
                        Text("Swipe up to expand sheet")
                    }
                    Text("Sheet content")
                    Button(
                        modifier =
                            Modifier.padding(bottom = 64.dp).focusProperties {
                                // Make sure the button is not keyboard focusable when it's offscreen.
                                canFocus =
                                    scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
                            },
                        onClick = { scope.launch { scaffoldState.bottomSheetState.partialExpand() } },
                    ) {
                        Text("Click to collapse sheet")
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text("Scaffold Content")
        }
    }
}

@Preview
@Composable
private fun TestPreview(){
    BMusicTheme {
        Test()
    }
}