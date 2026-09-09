package com.bsoft.compose.bmusic.ui.components

import android.graphics.Bitmap
import androidx.activity.compose.LocalActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TwoRowsTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageBackgroundTopAppBar(
    modifier: Modifier = Modifier, title: String, playAll: ()-> Unit, shuffle: ()-> Unit, subtitle: String? = null,
    @DrawableRes image: Int, scrollBehavior: TopAppBarScrollBehavior, bitmap: Bitmap? = null,
    showListControls: Boolean = false , navigationIcon: @Composable (() -> Unit) = {}
) {
    val collapsedFraction = scrollBehavior.state.collapsedFraction

    val activity = LocalActivity.current
    val view = LocalView.current
    val isLightMode = !isSystemInDarkTheme()

    DisposableEffect(collapsedFraction) {
        activity?.let {
            WindowCompat.getInsetsController(it.window, view).isAppearanceLightStatusBars = collapsedFraction >= 0.7f
        }

        onDispose {
            activity?.let {
                WindowCompat.getInsetsController(it.window, view).isAppearanceLightStatusBars = isLightMode
            }
        }
    }

    // 3. Linearly interpolate between your expanded and collapsed colors
    val titleColor = lerp(
        start = Color.White,    // Color when fully EXPANDED
        stop = MaterialTheme.colorScheme.onSurface,      // Color when fully COLLAPSED
        fraction = collapsedFraction
    )

    val containerColor = lerp(
        start = Color.Black.copy(alpha = 0.25f),
        stop = Color.Transparent,
        fraction = collapsedFraction
    )
    // The outer Box constraints the stack layer
    Box(modifier = modifier.fillMaxWidth()) {
        if(bitmap == null) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null, // Decorative image
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize().blur(radius = 2.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle)
            )
        } else {
            BitmapImage(modifier = Modifier.matchParentSize().align(Alignment.TopCenter).blur(radius = 2.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle),
                bitmap = bitmap, contentScale = ContentScale.Crop)
        }

        TwoRowsTopAppBar(
            title = { expanded->
                if(showListControls && expanded){
                    Column(modifier = Modifier.padding(bottom = 8.dp), horizontalAlignment = Alignment.Start) {
                        Text(title)
                        ListControls(modifier = Modifier.fillMaxWidth(), playAll = playAll, shuffle = shuffle)
                    }
                }else{
                    Text(title)
                }
            },
            navigationIcon = navigationIcon,
            scrollBehavior = scrollBehavior,
            collapsedHeight = TopAppBarDefaults.MediumAppBarCollapsedHeight,
            expandedHeight = if(showListControls) TopAppBarDefaults.LargeAppBarExpandedHeight else TopAppBarDefaults.MediumAppBarExpandedHeight,
            colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor,
                navigationIconContentColor = titleColor,
                titleContentColor = titleColor,
                actionIconContentColor = titleColor )
        )
    }
}