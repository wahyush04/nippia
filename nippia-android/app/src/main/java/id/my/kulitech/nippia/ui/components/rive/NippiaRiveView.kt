// RiveAnimationView + State Machine inputs match docs/RIVE_DOCS.md until assets migrate to Data Binding.
@file:Suppress("DEPRECATION")

package id.my.kulitech.nippia.ui.components.rive

import android.content.Context
import androidx.annotation.RawRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Alignment as RiveAlignment
import app.rive.runtime.kotlin.core.Fit
import app.rive.runtime.kotlin.core.Rive
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory cache of raw `.riv` byte arrays so repeated Rive views (such as `UnitNodeRive`
 * in `LazyColumn`) avoid disk I/O while giving each [RiveAnimationView] its own native instance.
 */
object RiveFileCache {
    private val cache = ConcurrentHashMap<Int, ByteArray>()

    fun getOrLoad(context: Context, @RawRes resId: Int): ByteArray {
        return cache[resId] ?: synchronized(this) {
            cache[resId] ?: context.applicationContext.resources
                .openRawResource(resId)
                .use { it.readBytes() }
                .also { cache[resId] = it }
        }
    }

    fun preload(context: Context, @RawRes vararg resIds: Int) {
        val appContext = context.applicationContext
        runCatching { Rive.init(appContext) }
        resIds.forEach { resId ->
            runCatching { getOrLoad(appContext, resId) }
        }
    }
}

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
    var isFirstFrameReady by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(
        targetValue = if (isFirstFrameReady) 1f else 0f,
        animationSpec = tween(durationMillis = 120),
        label = "riveFadeIn",
    )

    AndroidView(
        modifier = modifier.graphicsLayer { alpha = contentAlpha },
        factory = { context ->
            RiveAnimationView(context).apply {
                isOpaque = false
                setRiveBytes(
                    bytes = RiveFileCache.getOrLoad(context, resId),
                    artboardName = artboardName,
                    stateMachineName = stateMachineName,
                    autoplay = true,
                    fit = fit,
                    alignment = RiveAlignment.CENTER,
                )
                riveView = this
                postOnAnimationDelayed({ isFirstFrameReady = true }, 120L)
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
