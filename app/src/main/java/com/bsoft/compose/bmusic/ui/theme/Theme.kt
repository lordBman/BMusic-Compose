package com.bsoft.compose.bmusic.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun BMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    accentColor: Color? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        accentColor != null -> {
            val baseScheme = if (darkTheme) DarkColorScheme else LightColorScheme
            val (secondaryColor, tertiaryColor) = when (accentColor) {
                Color(0xFF2196F3) -> { // Blue
                    if (darkTheme) Color(0xFF90CAF9) to Color(0xFF80DEEA)
                    else Color(0xFF1976D2) to Color(0xFF00BCD4)
                }
                Color(0xFF9C27B0) -> { // Purple
                    if (darkTheme) Color(0xFFCE93D8) to Color(0xFFF48FB1)
                    else Color(0xFF7B1FA2) to Color(0xFFE040FB)
                }
                Color(0xFFFF9800) -> { // Orange
                    if (darkTheme) Color(0xFFFFCC80) to Color(0xFFFFE082)
                    else Color(0xFFF57C00) to Color(0xFFFFB74D)
                }
                Color(0xFF4CAF50) -> { // Green
                    if (darkTheme) Color(0xFFA5D6A7) to Color(0xFF80CBC4)
                    else Color(0xFF388E3C) to Color(0xFF81C784)
                }
                Color(0xFFE91E63) -> { // Pink
                    if (darkTheme) Color(0xFFF48FB1) to Color(0xFFFFAB91)
                    else Color(0xFFC2185B) to Color(0xFFFF4081)
                }
                else -> baseScheme.secondary to baseScheme.tertiary
            }
            baseScheme.copy(
                primary = accentColor,
                secondary = secondaryColor,
                tertiary = tertiaryColor
            )
        }

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}