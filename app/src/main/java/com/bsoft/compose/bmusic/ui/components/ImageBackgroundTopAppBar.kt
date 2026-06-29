package com.bsoft.compose.bmusic.ui.components

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageBackgroundTopAppBar(modifier: Modifier = Modifier, title: String, @DrawableRes image: Int, scrollBehavior: TopAppBarScrollBehavior, bitmap: Bitmap? = null, navigationIcon: @Composable (() -> Unit) = {}) {
    val collapsedFraction = scrollBehavior.state.collapsedFraction

    // 3. Linearly interpolate between your expanded and collapsed colors
    val titleColor = lerp(
        start = Color.White,    // Color when fully EXPANDED
        stop = MaterialTheme.colorScheme.onSurface,      // Color when fully COLLAPSED
        fraction = collapsedFraction
    )
    // The outer Box constraints the stack layer
    Box(modifier = modifier.fillMaxWidth()) {
        if(bitmap == null) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null, // Decorative image
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        } else {
            BitmapImage(modifier = Modifier.matchParentSize().align(Alignment.TopCenter),
                bitmap = bitmap, contentScale = ContentScale.Crop)
        }

        MediumTopAppBar(
            title = { Text(title) },
            navigationIcon = navigationIcon,
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent,
                navigationIconContentColor = titleColor,
                titleContentColor = titleColor,
                actionIconContentColor = titleColor )
        )
    }
}