package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ArcCyan
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonEmerald
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FridayAiOrb(
    isListening: Boolean,
    isSpeaking: Boolean,
    amplitude: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")

    // Rotation angle for futuristic cybernetic orbital rings
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Breathing pulse for idle & active modes
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isListening || isSpeaking) 700 else 1800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val primaryColor = when {
        isSpeaking -> NeonEmerald
        isListening -> ArcCyan
        else -> ElectricBlue
    }

    val glowColor = primaryColor.copy(alpha = 0.35f)

    Box(
        modifier = modifier
            .size(200.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag("friday_ai_orb"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(190.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 2f) * 0.72f
            val dynamicRadius = baseRadius * pulse * (1f + amplitude * 0.18f)

            // 1. Outer ambient glow aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor, primaryColor.copy(alpha = 0.08f), Color.Transparent),
                    center = center,
                    radius = dynamicRadius * 1.35f
                ),
                radius = dynamicRadius * 1.35f,
                center = center
            )

            // 2. Rotating orbital segmented ring
            val ringRadius = dynamicRadius * 1.12f
            for (i in 0 until 8) {
                val startAngle = rotation + (i * 45f)
                drawArc(
                    color = primaryColor.copy(alpha = if (i % 2 == 0) 0.8f else 0.3f),
                    startAngle = startAngle,
                    sweepAngle = 26f,
                    useCenter = false,
                    topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
                    size = androidx.compose.ui.geometry.Size(ringRadius * 2, ringRadius * 2),
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // 3. Counter-rotating inner ring
            val innerRingRadius = dynamicRadius * 0.92f
            for (i in 0 until 4) {
                val startAngle = -rotation * 1.4f + (i * 90f)
                drawArc(
                    color = ArcCyan.copy(alpha = 0.6f),
                    startAngle = startAngle,
                    sweepAngle = 45f,
                    useCenter = false,
                    topLeft = Offset(center.x - innerRingRadius, center.y - innerRingRadius),
                    size = androidx.compose.ui.geometry.Size(innerRingRadius * 2, innerRingRadius * 2),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // 4. Central Solid Core Sphere with Holographic Gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        primaryColor,
                        primaryColor.copy(alpha = 0.4f),
                        Color(0xFF071226)
                    ),
                    center = Offset(center.x - dynamicRadius * 0.15f, center.y - dynamicRadius * 0.15f),
                    radius = dynamicRadius * 0.75f
                ),
                radius = dynamicRadius * 0.72f,
                center = center
            )

            // 5. Soundwave frequency ticks around the sphere when listening/speaking
            if (isListening || isSpeaking) {
                val tickCount = 24
                for (t in 0 until tickCount) {
                    val angleRad = Math.toRadians((t * (360.0 / tickCount)).toDouble())
                    val barLen = (8.dp.toPx() + amplitude * 18.dp.toPx() * (if (t % 2 == 0) 1.2f else 0.7f))
                    val innerP = Offset(
                        (center.x + (dynamicRadius * 0.76f) * cos(angleRad)).toFloat(),
                        (center.y + (dynamicRadius * 0.76f) * sin(angleRad)).toFloat()
                    )
                    val outerP = Offset(
                        (center.x + (dynamicRadius * 0.76f + barLen) * cos(angleRad)).toFloat(),
                        (center.y + (dynamicRadius * 0.76f + barLen) * sin(angleRad)).toFloat()
                    )
                    drawLine(
                        color = if (isSpeaking) NeonEmerald else ArcCyan,
                        start = innerP,
                        end = outerP,
                        strokeWidth = 2.5.dp.toPx()
                    )
                }
            }
        }

        // Central Icon Indicator
        val icon = when {
            isSpeaking -> Icons.Default.VolumeUp
            else -> Icons.Default.Mic
        }
        val iconTint = if (isSpeaking) NeonEmerald else if (isListening) ArcCyan else Color.White

        Icon(
            imageVector = icon,
            contentDescription = if (isSpeaking) "Friday Speaking" else if (isListening) "Listening" else "Tap to Speak",
            tint = iconTint,
            modifier = Modifier.size(38.dp)
        )
    }
}
