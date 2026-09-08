package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DevotionalLightColorScheme = lightColorScheme(
    primary = VividOrange,
    onPrimary = PureWhite,
    primaryContainer = OrangeTint,
    onPrimaryContainer = VividOrange,
    secondary = BlueGrey,
    onSecondary = PureWhite,
    secondaryContainer = BlueGreyLight,
    onSecondaryContainer = DeepSlate,
    tertiary = WarmBeige,
    onTertiary = DeepSlate,
    background = SoftSurface,
    onBackground = DeepSlate,
    surface = CardSurface,
    onSurface = DeepSlate,
    surfaceVariant = WarmBeigeLight,
    onSurfaceVariant = CharcoalText,
    outline = WarmBeige
)

private val DevotionalDarkColorScheme = darkColorScheme(
    primary = VividOrange,
    onPrimary = PureWhite,
    primaryContainer = Color(0xFF4A1808),
    onPrimaryContainer = PureWhite,
    secondary = BlueGrey,
    onSecondary = DeepSlate,
    secondaryContainer = Color(0xFF2B3A42),
    onSecondaryContainer = PureWhite,
    tertiary = WarmBeige,
    onTertiary = DeepSlate,
    background = DeepSlate,
    onBackground = PureWhite,
    surface = Color(0xFF282420),
    onSurface = PureWhite,
    surfaceVariant = Color(0xFF38322D),
    onSurfaceVariant = WarmBeige,
    outline = BlueGrey
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We intentionally enforce our distinctive Warm Beige / Orange / BlueGrey palette
    val colorScheme = if (darkTheme) DevotionalDarkColorScheme else DevotionalLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
