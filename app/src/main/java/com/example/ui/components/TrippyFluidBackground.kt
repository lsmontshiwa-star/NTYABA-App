package com.example.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A refined, high-performance fluid radial gradient background inspired by ethereal spiritual dawns.
 * Blends Warm Beige (#D9CCBE), Vivid Orange (#FB5624), and Blue/Grey (#98B2BF) with White highlights (#FFFFFF).
 *
 * Delegates to the dedicated [FluidRadialGradientBackground] custom Composable.
 */
@Composable
fun TrippyFluidBackground(
    modifier: Modifier = Modifier,
    reduceMotion: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    FluidRadialGradientBackground(
        modifier = modifier,
        reduceMotion = reduceMotion,
        content = content
    )
}
