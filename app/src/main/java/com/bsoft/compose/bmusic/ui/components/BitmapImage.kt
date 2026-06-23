package com.bsoft.compose.bmusic.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

@Composable
fun BitmapImage(bitmap: Bitmap, modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Fit) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentScale = contentScale,
        contentDescription = "Logged user profile picture", // Provide a meaningful description for accessibility
        modifier = modifier
    )
}