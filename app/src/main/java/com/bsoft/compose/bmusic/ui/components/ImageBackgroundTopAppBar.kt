package com.bsoft.compose.bmusic.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageBackgroundTopAppBar(modifier: Modifier = Modifier, title: String, @DrawableRes image: Int, scrollBehavior: TopAppBarScrollBehavior? = null, navigationIcon: @Composable (() -> Unit) = {}) {
    // The outer Box constraints the stack layer
    Box(modifier = modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null, // Decorative image
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        MediumTopAppBar(
            title = { Text(title) },
            navigationIcon = navigationIcon,
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
    }
}