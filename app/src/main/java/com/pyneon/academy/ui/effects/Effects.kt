package com.pyneon.academy.ui.effects

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.pyneon.academy.ui.theme.NeonCyan
import com.pyneon.academy.ui.theme.NeonMagenta
import com.pyneon.academy.ui.theme.SurfaceDark
import com.pyneon.academy.ui.theme.TextHi
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

fun Modifier.scanlines(): Modifier = drawBehind {
    var y = 0f
    while (y < size.height) {
        drawRect(
            color = Color.Black.copy(alpha = 0.05f),
            topLeft = Offset(0f, y),
            size = Size(size.width, 1f)
        )
        y += 4f
    }
}

fun Modifier.cyberGrid(color: Color, cell: Dp): Modifier = drawBehind {
    val step = cell.toPx()
    if (step > 1f) {
        var x = 0f
        while (x < size.width) {
            drawLine(color, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
            x += step
        }
        var y = 0f
        while (y < size.height) {
            drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
            y += step
        }
    }
}

@Composable
fun GlitchText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = TextHi
) {
    val transition = rememberInfiniteTransition(label = "glitch")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing)),
        label = "phase"
    )
    val jitterX = (sin(phase * Math.PI * 4) * 1.6).toFloat().dp
    val jitterY = (cos(phase * Math.PI * 7) * 1.1).toFloat().dp
    Box(modifier) {
        Text(
            text = text,
            style = style,
            color = NeonMagenta.copy(alpha = 0.75f),
            modifier = Modifier.offset(x = jitterX + 1.dp, y = jitterY)
        )
        Text(
            text = text,
            style = style,
            color = NeonCyan.copy(alpha = 0.75f),
            modifier = Modifier.offset(x = -jitterX - 1.dp, y = -jitterY)
        )
        Text(text = text, style = style, color = color)
    }
}

@Composable
fun TypewriterText(
    text: String,
    color: Color,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    val initial = if (text.length > 600) text.length else 0
    var shown by remember(text) { mutableIntStateOf(initial) }
    LaunchedEffect(text) {
        if (text.length <= 600) {
            val step = if (text.length > 200) 3 else 1
            val frameDelay = if (text.length > 200) 8L else 14L
            var i = 0
            while (i < text.length) {
                i = (i + step).coerceAtMost(text.length)
                shown = i
                delay(frameDelay)
            }
        }
        shown = text.length
    }
    Text(text = text.take(shown), color = color, style = style, modifier = modifier)
}

@Composable
fun BlinkingCursor(color: Color = NeonCyan, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "cursor")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(tween(560), RepeatMode.Reverse),
        label = "alpha"
    )
    Text(
        "▊",
        color = color.copy(alpha = alpha),
        style = TextStyle(fontSize = TextUnit(16f, TextUnitType.Sp)),
        modifier = modifier
    )
}

fun neonBorder(accent: Color): BorderStroke = BorderStroke(
    width = 1.dp,
    brush = Brush.linearGradient(
        listOf(accent.copy(alpha = 0.9f), accent.copy(alpha = 0.12f), accent.copy(alpha = 0.55f))
    )
)

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    accent: Color = NeonCyan,
    filled: Boolean = false,
    elevation: Dp = 0.dp,
    shape: Shape = androidx.compose.foundation.shape.CutCornerShape(14.dp),
    backgroundColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
): Unit {
    val bg = backgroundColor ?: (if (filled) accent.copy(alpha = 0.10f) else SurfaceDark)
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (pressed && onClick != null) 0.98f else 1f,
        animationSpec = androidx.compose.animation.core.tween(90),
        label = "cardScale"
    )
    var base = Modifier
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .shadow(elevation, shape)
        .clip(shape)
        .background(bg)
        .border(neonBorder(accent), shape)
    if (onClick != null) {
        base = base.clickable(interactionSource = interaction, indication = null) {
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
            onClick()
        }
    }
    Box(modifier.then(base)) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun ProgressRing(
    fraction: Float,
    diameter: Dp,
    stroke: Dp,
    accent: Color,
    trackColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val animated by animateFloatAsState(targetValue = fraction.coerceIn(0f, 1f), label = "ring")
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val inset = stroke.toPx() / 2
            val arcSize = Size(this.size.width - inset * 2, this.size.height - inset * 2)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(accent.copy(alpha = 0.30f), accent)),
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round)
            )
        }
        content()
    }
}

@Composable
fun SectionHeader(title: String, accent: Color = NeonCyan, modifier: Modifier = Modifier) {
    Text(
        "▍ $title",
        style = MaterialTheme.typography.titleMedium,
        color = accent,
        modifier = modifier
    )
}
