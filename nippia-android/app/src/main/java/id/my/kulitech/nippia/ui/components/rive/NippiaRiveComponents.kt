// RiveAnimationView + State Machine inputs match docs/RIVE_DOCS.md until assets migrate to Data Binding.
@file:Suppress("DEPRECATION")

package id.my.kulitech.nippia.ui.components.rive

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.rive.runtime.kotlin.RiveAnimationView
import id.my.kulitech.nippia.R

/**
 * Status states for [UnitNodeRive] matching `NodeState.status` (`0 | 1 | 2`).
 */
enum class UnitNodeStatus(val value: Int) {
    LOCKED(0),
    ACTIVE(1),
    COMPLETED(2),
}

/**
 * Nippia Mascot animation (`mascot_nippia.riv` -> `MascotState`), built directly from
 * `docs/Maskot Burung Kuning Berseragam ASN.svg`.
 *
 * Supports interactive pointer tap directly on the Rive canvas as well as programmatic triggers:
 * - Increment [correctTriggerCount], [wrongTriggerCount], [waveTriggerCount],
 *   [tapTriggerCount], or [celebrateTriggerCount] to fire one-shot reactions.
 * - Toggle [isThinking], [isTalking], or [isSleeping] for continuous loop states.
 * - Set [comboLevel] (`>= 3` activates the golden combo aura).
 */
@Composable
fun MascotRive(
    modifier: Modifier = Modifier.size(160.dp),
    isThinking: Boolean = false,
    isTalking: Boolean = false,
    isSleeping: Boolean = false,
    comboLevel: Int = 0,
    correctTriggerCount: Int = 0,
    wrongTriggerCount: Int = 0,
    waveTriggerCount: Int = 0,
    tapTriggerCount: Int = 0,
    celebrateTriggerCount: Int = 0,
) {
    var viewRef by remember { mutableStateOf<RiveAnimationView?>(null) }

    rememberRiveTriggerEffect(viewRef, "MascotState", "triggerCorrect", correctTriggerCount)
    rememberRiveTriggerEffect(viewRef, "MascotState", "triggerWrong", wrongTriggerCount)
    rememberRiveTriggerEffect(viewRef, "MascotState", "triggerWave", waveTriggerCount)
    rememberRiveTriggerEffect(viewRef, "MascotState", "triggerTap", tapTriggerCount)
    rememberRiveTriggerEffect(viewRef, "MascotState", "triggerCelebrate", celebrateTriggerCount)

    NippiaRiveView(
        resId = R.raw.mascot_nippia,
        artboardName = "MascotArtboard",
        stateMachineName = "MascotState",
        modifier = modifier,
        previewLabel = "Mascot",
        onUpdate = { view ->
            viewRef = view
            view.setBooleanState("MascotState", "isThinking", isThinking)
            view.setBooleanState("MascotState", "isTalking", isTalking)
            view.setBooleanState("MascotState", "isSleeping", isSleeping)
            view.setNumberState("MascotState", "comboLevel", comboLevel.coerceAtLeast(0).toFloat())
        },
    )
}

/**
 * Daily Streak Flame indicator (`streak_flame.riv` -> `FlameState`).
 * Increment [igniteTriggerCount] to play the ignite burst animation.
 */
@Composable
fun StreakFlameRive(
    isActive: Boolean,
    modifier: Modifier = Modifier.size(28.dp),
    igniteTriggerCount: Int = 0,
) {
    var viewRef by remember { mutableStateOf<RiveAnimationView?>(null) }

    rememberRiveTriggerEffect(viewRef, "FlameState", "igniteTrigger", igniteTriggerCount)

    NippiaRiveView(
        resId = R.raw.streak_flame,
        artboardName = "FlameArtboard",
        stateMachineName = "FlameState",
        modifier = modifier,
        previewLabel = "Streak",
        onUpdate = { view ->
            viewRef = view
            view.setBooleanState("FlameState", "isActive", isActive)
        },
    )
}

/**
 * Heart / Energy status icon (`heart_status.riv` -> `HeartState`).
 * Supports standard heart, Nippia Pro infinity (`isUnlimited = true`), crack, and refill animations.
 */
@Composable
fun HeartStatusRive(
    modifier: Modifier = Modifier.size(28.dp),
    isUnlimited: Boolean = false,
    crackTriggerCount: Int = 0,
    refillTriggerCount: Int = 0,
) {
    var viewRef by remember { mutableStateOf<RiveAnimationView?>(null) }

    rememberRiveTriggerEffect(viewRef, "HeartState", "crackTrigger", crackTriggerCount)
    rememberRiveTriggerEffect(viewRef, "HeartState", "refillTrigger", refillTriggerCount)

    NippiaRiveView(
        resId = R.raw.heart_status,
        artboardName = "HeartArtboard",
        stateMachineName = "HeartState",
        modifier = modifier,
        previewLabel = "Heart",
        onUpdate = { view ->
            viewRef = view
            view.setBooleanState("HeartState", "isUnlimited", isUnlimited)
        },
    )
}

/**
 * Learning Path Unit Node button (`unit_node.riv` -> `NodeState`).
 */
@Composable
fun UnitNodeRive(
    status: UnitNodeStatus,
    modifier: Modifier = Modifier.size(72.dp),
    isPressed: Boolean = false,
) {
    NippiaRiveView(
        resId = R.raw.unit_node,
        artboardName = "UnitNodeArtboard",
        stateMachineName = "NodeState",
        modifier = modifier,
        previewLabel = "Node(${status.name})",
        onUpdate = { view ->
            view.setNumberState("NodeState", "status", status.value.toFloat())
            view.setBooleanState("NodeState", "isPressed", isPressed)
        },
    )
}

/**
 * Lesson Complete Celebration hero (`lesson_complete.riv` -> `CelebrationState`).
 * [accuracyTier] should be `1`, `2`, or `3` (clamped automatically).
 */
@Composable
fun LessonCompleteRive(
    accuracyTier: Int = 3,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(260.dp),
) {
    NippiaRiveView(
        resId = R.raw.lesson_complete,
        artboardName = "CelebrationArtboard",
        stateMachineName = "CelebrationState",
        modifier = modifier,
        previewLabel = "LessonComplete(T$accuracyTier)",
        onUpdate = { view ->
            view.setNumberState("CelebrationState", "accuracyTier", accuracyTier.coerceIn(1, 3).toFloat())
        },
    )
}
