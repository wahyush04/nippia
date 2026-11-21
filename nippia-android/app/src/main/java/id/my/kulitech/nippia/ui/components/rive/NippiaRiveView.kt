// RiveAnimationView + State Machine inputs match docs/RIVE_DOCS.md until assets migrate to Data Binding.
@file:Suppress("DEPRECATION")

package id.my.kulitech.nippia.ui.components.rive

import android.content.Context
import android.graphics.SurfaceTexture
import android.view.TextureView
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
    placeholder: (@Composable () -> Unit)? = null,
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

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (placeholder != null && contentAlpha < 1f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = 1f - contentAlpha },
                contentAlignment = Alignment.Center,
            ) {
                placeholder()
            }
        }

        AndroidView(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { alpha = contentAlpha },
            factory = { context ->
                RiveAnimationView(context).apply {
                    alpha = 0f
                    isOpaque = false
                    setRiveBytes(
                        bytes = RiveFileCache.getOrLoad(context, resId),
                        artboardName = artboardName,
                        stateMachineName = stateMachineName,
                        autoplay = true,
                        fit = fit,
                        alignment = RiveAlignment.CENTER,
                    )
                    val delegate = surfaceTextureListener
                    surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                        private var updateCount = 0
                        private fun markReady() {
                            if (!isFirstFrameReady) {
                                alpha = 1f
                                isFirstFrameReady = true
                            }
                        }

                        override fun onSurfaceTextureAvailable(
                            surface: SurfaceTexture,
                            width: Int,
                            height: Int,
                        ) {
                            delegate?.onSurfaceTextureAvailable(surface, width, height)
                        }

                        override fun onSurfaceTextureSizeChanged(
                            surface: SurfaceTexture,
                            width: Int,
                            height: Int,
                        ) {
                            delegate?.onSurfaceTextureSizeChanged(surface, width, height)
                        }

                        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                            return delegate?.onSurfaceTextureDestroyed(surface) ?: true
                        }

                        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                            delegate?.onSurfaceTextureUpdated(surface)
                            updateCount += 1
                            // Skip the very first surface buffer update (RIVE boot logo) and reveal on artboard frame
                            if (updateCount >= 2) {
                                post { markReady() }
                            }
                        }
                    }
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
