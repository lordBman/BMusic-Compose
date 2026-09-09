package com.bsoft.compose.bmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bsoft.compose.bmusic.R
import com.bsoft.compose.bmusic.ui.theme.BMusicTheme
import com.bsoft.compose.bmusic.utils.Route
import com.bsoft.compose.bmusic.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(), back: ()-> Unit) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Settings")
                },
                navigationIcon = {
                    IconButton(onClick = { back() }) {
                        Icon(modifier = Modifier.size(28.dp), imageVector = ImageVector.vectorResource(R.drawable.material_symbols__arrow_back_ios_new_rounded), contentDescription = null)
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            AccentColorSelection(
                selectedColor = Color(settings.accentColor),
                onColorSelected = { viewModel.setAccentColor((it.value shr 32).toInt()) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "General",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            LanguageSelection(
                selectedLanguage = settings.language,
                onLanguageSelected = { viewModel.setLanguage(it) }
            )

            SettingSwitchItem(
                title = "Remember last open tab",
                checked = settings.rememberLastTab,
                onCheckedChange = { viewModel.setRememberLastTab(it) }
            )

            SettingSwitchItem(
                title = "Hide short songs (< 1 min)",
                checked = settings.hideShortSongs,
                onCheckedChange = { viewModel.setHideShortSongs(it) }
            )

            SettingSwitchItem(
                title = "Shake to change song",
                checked = settings.shakeToChange,
                onCheckedChange = { viewModel.setShakeToChange(it) }
            )

            SettingSwitchItem(
                title = "Sound fades as songs ends",
                checked = settings.soundFade,
                onCheckedChange = { viewModel.setSoundFade(it) }
            )
        }
    }
}

@Composable
fun AccentColorSelection(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color(0xFF2196F3), // Blue
        Color(0xFF9C27B0), // Purple
        Color(0xFFFF9800), // Orange
        Color(0xFF4CAF50), // Green
        Color(0xFFE91E63)  // Pink
    )

    Column {
        Text(text = "Accent Color", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { onColorSelected(color) }
                        .then(
                            if (selectedColor == color) {
                                Modifier.background(Color.White.copy(alpha = 0.3f))
                            } else {
                                Modifier
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedColor == color) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageSelection(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val languages = mapOf("en" to "English", "ja" to "Japanese")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Language", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = languages[selectedLanguage] ?: "English",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languages.forEach { (code, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onLanguageSelected(code)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    BMusicTheme {
        SettingsScreen{}
    }
}
