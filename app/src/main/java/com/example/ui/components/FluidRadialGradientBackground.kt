package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BlueGrey
import com.example.ui.theme.PureWhite
import com.example.ui.theme.VividOrange
import com.example.ui.theme.WarmBeige
import kotlin.math.cos
import kotlin.math.sin

/**
 * A custom Composable with a fluid, slowly animating radial gradient background
 * utilizing the specified color palette:
 * - Warm Beige: #D9CCBE
 * - Vivid Orange: #FB5624
 * - Blue/Grey: #98B2BF
 * - Pure White: #FFFFFF
 *
 * Implemented with smooth orbital phases and [Brush.radialGradient] over a warm beige canvas,
 * generating an ethereal, slow-drifting atmospheric glow.
 */
@Composable
fun FluidRadialGradientBackground(
    modifier: Modifier = Modifier,
    reduceMotion: Boolean = false,
    content: @Composable BoxScope.() -> Unit = {}
) {
    // Reusable color stop lists to avoid per-frame allocations
    val orangeGradientStops = remember {
        listOf(
            VividOrange.copy(alpha = 0.44f),
            VividOrange.copy(alpha = 0.16f),
            Color.Transparent
        )
    }

    val blueGreyGradientStops = remember {
        listOf(
            BlueGrey.copy(alpha = 0.48f),
            BlueGrey.copy(alpha = 0.18f),
            Color.Transparent
        )
    }

    val whiteHighlightStops = remember {
        listOf(
            PureWhite.copy(alpha = 0.65f),
            PureWhite.copy(alpha = 0.22f),
            Color.Transparent
        )
    }

    if (reduceMotion) {
        // High-performance static radial composition when reduced motion is preferred
        Box(
            modifier = modifier
                .fillMaxSize()
                .drawWithCache {
                    val width = size.width
                    val height = size.height
                    val maxDim = maxOf(width, height)

                    val orangeCenter = Offset(width * 0.35f, height * 0.28f)
                    val orangeRadius = maxDim * 0.75f
                    val orangeBrush = Brush.radialGradient(
                        colors = orangeGradientStops,
                        center = orangeCenter,
                        radius = orangeRadius
                    )

                    val blueCenter = Offset(width * 0.72f, height * 0.68f)
                    val blueRadius = maxDim * 0.80f
                    val blueBrush = Brush.radialGradient(
                        colors = blueGreyGradientStops,
                        center = blueCenter,
                        radius = blueRadius
                    )

                    val whiteCenter = Offset(width * 0.50f, height * 0.46f)
                    val whiteRadius = maxDim * 0.55f
                    val whiteBrush = Brush.radialGradient(
                        colors = whiteHighlightStops,
                        center = whiteCenter,
                        radius = whiteRadius
                    )

                    onDrawBehind {
                        // 1. Base Warm Beige (#D9CCBE)
                        drawRect(color = WarmBeige)

                        // 2. Vivid Orange radial gradient (#FB5624)
                        drawCircle(
                            brush = orangeBrush,
                            center = orangeCenter,
                            radius = orangeRadius
                        )

                        // 3. Blue/Grey radial gradient (#98B2BF)
                        drawCircle(
                            brush = blueBrush,
                            center = blueCenter,
                            radius = blueRadius
                        )

                        // 4. Pure White luminous highlight (#FFFFFF)
                        drawCircle(
                            brush = whiteBrush,
                            center = whiteCenter,
                            radius = whiteRadius
                        )
                    }
                },
            content = content
        )
    } else {
        // Fluid, slowly animating multi-phase radial gradients
        val infiniteTransition = rememberInfiniteTransition(label = "fluid_radial_transition")

        val phase1 by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 22000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "radial_orbit_phase1"
        )

        val phase2 by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 30000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "radial_orbit_phase2"
        )

        Box(
            modifier = modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawBehind {
                        val width = size.width
                        val height = size.height
                        val maxDim = maxOf(width, height)

                        val angle1 = phase1 * 2f * Math.PI.toFloat()
                        val angle2 = phase2 * 2f * Math.PI.toFloat()

                        // 1. Base canvas fill: Warm Beige (#D9CCBE)
                        drawRect(color = WarmBeige)

                        // 2. Vivid Orange Radial Gradient (#FB5624): shifting orbit
                        val orangeCenterX = width * (0.35f + 0.18f * cos(angle1))
                        val orangeCenterY = height * (0.28f + 0.15f * sin(angle1))
                        val orangeRadius = maxDim * (0.75f + 0.08f * sin(angle2))
                        val orangeCenter = Offset(orangeCenterX, orangeCenterY)

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = orangeGradientStops,
                                center = orangeCenter,
                                radius = orangeRadius
                            ),
                            center = orangeCenter,
                            radius = orangeRadius
                        )

                        // 3. Blue/Grey Radial Gradient (#98B2BF): counter-orbit
                        val blueCenterX = width * (0.72f + 0.16f * sin(angle2))
                        val blueCenterY = height * (0.68f + 0.16f * cos(angle2))
                        val blueRadius = maxDim * (0.80f + 0.06f * cos(angle1))
                        val blueCenter = Offset(blueCenterX, blueCenterY)

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = blueGreyGradientStops,
                                center = blueCenter,
                                radius = blueRadius
                            ),
                            center = blueCenter,
                            radius = blueRadius
                        )

                        // 4. Pure White Luminous Center (#FFFFFF): subtle focal pulse
                        val whiteCenterX = width * (0.50f + 0.12f * cos(angle2 * 0.7f))
                        val whiteCenterY = height * (0.46f + 0.12f * sin(angle1 * 0.7f))
                        val whiteRadius = maxDim * (0.55f + 0.06f * sin(angle1))
                        val whiteCenter = Offset(whiteCenterX, whiteCenterY)

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = whiteHighlightStops,
                                center = whiteCenter,
                                radius = whiteRadius
                            ),
                            center = whiteCenter,
                            radius = whiteRadius
                        )
                    }
                },
            content = content
        )
    }
}
