package com.bsoft.compose.bmusic.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bsoft.compose.bmusic.ui.Main
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import com.bsoft.compose.bmusic.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            val accentColor = Color(settings.accentColor)

            BMusicTheme(dynamicColor = true, accentColor = accentColor) {
                Main()
            }
        }
    }
}
