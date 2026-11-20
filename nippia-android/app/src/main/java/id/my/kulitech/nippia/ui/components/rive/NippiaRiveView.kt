// RiveAnimationView + State Machine inputs match docs/RIVE_DOCS.md until assets migrate to Data Binding.
@file:Suppress("DEPRECATION")

package id.my.kulitech.nippia.ui.components.rive

import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Alignment as RiveAlignment
import app.rive.runtime.kotlin.core.Fit

/**
 * Shared Jetpack Compose wrapper around [RiveAnimationView] for Nippia Rive assets.
 * Renders a lightweight placeholder in Compose `@Preview` (`LocalInspectionMode`).
 */
@Composable
fun NippiaRiveView(
    @RawRes resId: Int,
    artboardName: String,
    stateMachineName: String,
    modifier: Modifier = Modifier,
    fit: Fit = Fit.CONTAIN,
    previewLabel: String = artboardName,
    onUpdate: (RiveAnimationView) -> Unit = {},
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = previewLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    var riveView by remember { mutableStateOf<RiveAnimationView?>(null) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            RiveAnimationView(context).apply {
                setRiveResource(
                    resId = resId,
                    artboardName = artboardName,
                    stateMachineName = stateMachineName,
                    autoplay = true,
                    fit = fit,
                    alignment = RiveAlignment.CENTER,
                )
                riveView = this
            }
        },
        update = { view ->
            riveView = view
            onUpdate(view)
        },
        onRelease = { view ->
            view.stop()
            if (riveView === view) {
                riveView = null
            }
        },
    )
}

/**
 * Fires a Rive StateMachine Trigger whenever [triggerCount] increments above 0,
 * without firing prematurely on initial composition.
 */
@Composable
internal fun rememberRiveTriggerEffect(
    riveView: RiveAnimationView?,
    stateMachineName: String,
    triggerName: String,
    triggerCount: Int,
) {
    var lastHandledCount by remember { mutableIntStateOf(triggerCount) }
    LaunchedEffect(riveView, triggerCount) {
        val view = riveView ?: return@LaunchedEffect
        if (triggerCount > lastHandledCount) {
            lastHandledCount = triggerCount
            view.fireState(stateMachineName, triggerName)
        } else if (triggerCount < lastHandledCount) {
            lastHandledCount = triggerCount
        }
    }
}
