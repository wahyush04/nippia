package id.my.kulitech.nippia.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.my.kulitech.nippia.ui.theme.NippiaEmerald
import id.my.kulitech.nippia.ui.theme.NippiaEmeraldDark
import id.my.kulitech.nippia.ui.theme.NippiaWhite

/**
 * Tactile 3D button with a 4.dp bottom shadow lip that depresses vertically with haptic feedback.
 */
@Composable
fun TactileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = NippiaEmerald,
    shadowColor: Color = NippiaEmeraldDark,
    contentColor: Color = NippiaWhite,
    border: BorderStroke? = null,
    cornerRadius: Dp = 16.dp,
    shadowDepth: Dp = 4.dp,
    content: @Composable RowScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offsetY by animateDpAsState(
        targetValue = if (isPressed && enabled) shadowDepth else 0.dp,
        label = "tactileOffset"
    )
    val shape = RoundedCornerShape(cornerRadius)
    val effectiveContainer = if (enabled) containerColor else containerColor.copy(alpha = 0.5f)
    val effectiveShadow = if (enabled) shadowColor else shadowColor.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = shadowDepth)
    ) {
        // Bottom 3D lip
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = shadowDepth)
                .clip(shape)
                .background(effectiveShadow)
        )

        // Top interactive face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = offsetY)
                .clip(shape)
                .background(effectiveContainer)
                .then(if (border != null) Modifier.border(border, shape) else Modifier)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    }
                )
                .heightIn(min = 52.dp)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}
