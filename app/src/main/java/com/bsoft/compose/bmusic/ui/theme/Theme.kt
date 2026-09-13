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
)

// Complete Material 3 Color Schemes for each color accent option
private val BlueLightScheme = lightColorScheme(
    primary = Color(0xFF0061A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF535F70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7E3F7),
    onSecondaryContainer = Color(0xFF101C2B),
    tertiary = Color(0xFF6B5778),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF2DAFF),
    onTertiaryContainer = Color(0xFF251431),
    background = Color(0xFFFDFCFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFDFCFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDFE2EB),
    onSurfaceVariant = Color(0xFF43474E),
    outline = Color(0xFF73777F)
)

private val BlueDarkScheme = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F7),
    tertiary = Color(0xFFD6BAE4),
    onTertiary = Color(0xFF3B2947),
    tertiaryContainer = Color(0xFF523F5F),
    onTertiaryContainer = Color(0xFFF2DAFF),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC3C6CF),
    outline = Color(0xFF8D9199)
)

private val PurpleLightScheme = lightColorScheme(
    primary = Color(0xFF8E24AA),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF3E5F5),
    onPrimaryContainer = Color(0xFF2A0033),
    secondary = Color(0xFF6D5E70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF6EAFF),
    onSecondaryContainer = Color(0xFF26162B),
    tertiary = Color(0xFF7F525D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD9E1),
    onTertiaryContainer = Color(0xFF32101A),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1D1B1E),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1D1B1E)
)

private val PurpleDarkScheme = darkColorScheme(
    primary = Color(0xFFE1BEE7),
    onPrimary = Color(0xFF56006B),
    primaryContainer = Color(0xFF72008F),
    onPrimaryContainer = Color(0xFFF3E5F5),
    secondary = Color(0xFFD9C2D9),
    onSecondary = Color(0xFF3D2C3E),
    secondaryContainer = Color(0xFF544255),
    onSecondaryContainer = Color(0xFFF6EAFF),
    tertiary = Color(0xFFF2B7C4),
    onTertiary = Color(0xFF4B252E),
    tertiaryContainer = Color(0xFF643B45),
    onTertiaryContainer = Color(0xFFFFD9E1),
    background = Color(0xFF1D1B1E),
    onBackground = Color(0xFFE6E1E6),
    surface = Color(0xFF1D1B1E),
    onSurface = Color(0xFFE6E1E6)
)

private val OrangeLightScheme = lightColorScheme(
    primary = Color(0xFF8B5000),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDCBE),
    onPrimaryContainer = Color(0xFF2C1600),
    secondary = Color(0xFF725A42),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFEDFBF),
    onSecondaryContainer = Color(0xFF281805),
    tertiary = Color(0xFF56654A),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDAE9C8),
    onTertiaryContainer = Color(0xFF141F0C),
    background = Color(0xFFFFFBF9),
    onBackground = Color(0xFF201B16),
    surface = Color(0xFFFFFBF9),
    onSurface = Color(0xFF201B16)
)

private val OrangeDarkScheme = darkColorScheme(
    primary = Color(0xFFFFB866),
    onPrimary = Color(0xFF4A2800),
    primaryContainer = Color(0xFF6A3C00),
    onPrimaryContainer = Color(0xFFFFDCBE),
    secondary = Color(0xFFE1C3A5),
    onSecondary = Color(0xFF402D18),
    secondaryContainer = Color(0xFF58432C),
    onSecondaryContainer = Color(0xFFFEDFBF),
    tertiary = Color(0xFFBECCA3),
    onTertiary = Color(0xFF293620),
    tertiaryContainer = Color(0xFF3F4D34),
    onTertiaryContainer = Color(0xFFDAE9C8),
    background = Color(0xFF201B16),
    onBackground = Color(0xFFECE0D8),
    surface = Color(0xFF201B16),
    onSurface = Color(0xFFECE0D8)
)

private val GreenLightScheme = lightColorScheme(
    primary = Color(0xFF006E2A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF96F9A2),
    onPrimaryContainer = Color(0xFF002207),
    secondary = Color(0xFF526350),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD5E8D0),
    onSecondaryContainer = Color(0xFF101F11),
    tertiary = Color(0xFF38656A),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFBCEBF0),
    onTertiaryContainer = Color(0xFF002023),
    background = Color(0xFFF8FBF7),
    onBackground = Color(0xFF1A1C19),
    surface = Color(0xFFF8FBF7),
    onSurface = Color(0xFF1A1C19)
)

private val GreenDarkScheme = darkColorScheme(
    primary = Color(0xFF7BC988),
    onPrimary = Color(0xFF003912),
    primaryContainer = Color(0xFF00531E),
    onPrimaryContainer = Color(0xFF96F9A2),
    secondary = Color(0xFFBACCB5),
    onSecondary = Color(0xFF253424),
    secondaryContainer = Color(0xFF3B4B39),
    onSecondaryContainer = Color(0xFFD5E8D0),
    tertiary = Color(0xFF80E3E7),
    onTertiary = Color(0xFF00363B),
    tertiaryContainer = Color(0xFF1F4D52),
    onTertiaryContainer = Color(0xFFBCEBF0),
    background = Color(0xFF1A1C19),
    onBackground = Color(0xFFE2E3DE),
    surface = Color(0xFF1A1C19),
    onSurface = Color(0xFFE2E3DE)
)

private val PinkLightScheme = lightColorScheme(
    primary = Color(0xFFB90063),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFD9E2),
    onPrimaryContainer = Color(0xFF3E001D),
    secondary = Color(0xFF74565F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD9E2),
    onSecondaryContainer = Color(0xFF2B151C),
    tertiary = Color(0xFF7C5635),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDCC1),
    onTertiaryContainer = Color(0xFF2E1500),
    background = Color(0xFFFFFBFB),
    onBackground = Color(0xFF201A1B),
    surface = Color(0xFFFFFBFB),
    onSurface = Color(0xFF201A1B)
)

private val PinkDarkScheme = darkColorScheme(
    primary = Color(0xFFFFB1C8),
    onPrimary = Color(0xFF650033),
    primaryContainer = Color(0xFF8E004A),
    onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = Color(0xFFE3BDC6),
    onSecondary = Color(0xFF422931),
    secondaryContainer = Color(0xFF5A3F47),
    onSecondaryContainer = Color(0xFFFFD9E2),
    tertiary = Color(0xFFEFBD94),
    onTertiary = Color(0xFF48290C),
    tertiaryContainer = Color(0xFF623F1E),
    onTertiaryContainer = Color(0xFFFFDCC1),
    background = Color(0xFF201A1B),
    onBackground = Color(0xFFECE0E1),
    surface = Color(0xFF201A1B),
    onSurface = Color(0xFFECE0E1)
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
            val redPart = (accentColor.red * 255).toInt()
            val greenPart = (accentColor.green * 255).toInt()
            val bluePart = (accentColor.blue * 255).toInt()

            when {
                // Blue: 0xFF2196F3
                redPart in 30..36 && greenPart in 147..153 && bluePart in 240..246 -> {
                    if (darkTheme) BlueDarkScheme else BlueLightScheme
                }
                // Purple: 0xFF9C27B0
                redPart in 153..159 && greenPart in 36..42 && bluePart in 173..179 -> {
                    if (darkTheme) PurpleDarkScheme else PurpleLightScheme
                }
                // Orange: 0xFFFF9800
                redPart in 252..255 && greenPart in 149..155 && bluePart in 0..5 -> {
                    if (darkTheme) OrangeDarkScheme else OrangeLightScheme
                }
                // Green: 0xFF4CAF50
                redPart in 73..79 && greenPart in 172..178 && bluePart in 77..83 -> {
                    if (darkTheme) GreenDarkScheme else GreenLightScheme
                }
                // Pink: 0xFFE91E63
                redPart in 230..236 && greenPart in 27..33 && bluePart in 96..102 -> {
                    if (darkTheme) PinkDarkScheme else PinkLightScheme
                }
                else -> if (darkTheme) BlueDarkScheme else BlueLightScheme
            }
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
