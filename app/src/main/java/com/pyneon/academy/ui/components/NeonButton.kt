package com.pyneon.academy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pyneon.academy.ui.effects.neonBorder

@Composable
fun NeonButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = Color(0xFF00E5FF),
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val alpha = if (enabled) 1f else 0.35f
    Box(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.CutCornerShape(8.dp))
            .background(accent.copy(alpha = 0.10f * alpha))
            .border(1.dp, accent.copy(alpha = 0.7f * alpha), androidx.compose.foundation.shape.CutCornerShape(8.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = accent.copy(alpha = alpha),
                    modifier = Modifier.size(16.dp)
                )
                androidx.compose.foundation.layout.Spacer(Modifier.size(6.dp))
            }
            Text(label, style = androidx.compose.material3.MaterialTheme.typography.labelLarge, color = accent.copy(alpha = alpha))
        }
    }
}
