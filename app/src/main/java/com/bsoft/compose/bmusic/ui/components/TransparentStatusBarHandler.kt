package com.bsoft.compose.bmusic.ui.components

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun TransparentStatusBarHandler(){
    val view = LocalView.current
    val isLightMode = !isSystemInDarkTheme()

    DisposableEffect(Unit) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false

        onDispose {
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLightMode
        }
    }
}