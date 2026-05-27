package presentation.core.ui.source.kit.organism.animatedsequence

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import co.touchlab.stately.collections.ConcurrentMutableMap
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

/**
 * Default timing constants for sequential animations.
 *
 * @since 0.0.1
 */
private object AnimationDefaults {
    /** Default animation duration in milliseconds. */
    const val DEFAULT_DURATION = 300L

    /** Default delay between consecutive animation items in milliseconds. */
    const val DEFAULT_DELAY = 400L
}

// CompositionLocal providing the nearest SequentialAnimationHost to child composables.
private val LocalSequentialAnimationHost =
    staticCompositionLocalOf<SequentialAnimationHost?> { null }

/**
 * Data class to represent an animation item in the sequence.
 *
 * @param index Unique identifier and position in the animation sequence.
 * @param visibilityState The mutable transition state controlling visibility.
 * @param enterTransition The transition played when the item enters.
 * @param exitTransition The transition played when the item exits.
 * @param delay Time in milliseconds to wait after this item before starting the next.
 * @param enterDuration Pre-calculated enter transition duration in milliseconds.
 * @param exitDuration Pre-calculated exit transition duration in milliseconds.
 * @since 0.0.1
 */
private data class AnimationItem(
    val index: Int,
    val visibilityState: MutableTransitionState<Boolean>,
    val enterTransition: EnterTransition,
    val exitTransition: ExitTransition,
    val delay: Long, // Custom delay in milliseconds
    val enterDuration: Long, // Pre-calculated enter duration
    val exitDuration: Long, // Pre-calculated exit duration
)

/**
 * Scope interface defining the operations that can be performed on a sequence of animations.
 *
 * Implementations orchestrate enter and exit transitions for a collection of indexed items,
 * allowing both batch and single-item control.
 *
 * @see AnimationSequenceHost
 * @since 0.0.1
 */
public interface SequentialAnimationScope {
    /**
     * Plays the enter animation for all registered items in index order.
     *
     * Items are first hidden (exit), then revealed one by one with their configured delays.
     *
     * @since 0.0.1
     */
    public suspend fun enter()

    /**
     * Plays the exit animation for all registered items.
     *
     * @param all When `true`, all items are hidden simultaneously without staggered delays.
     *   When `false`, items exit in reverse index order with their configured delays.
     * @since 0.0.1
     */
    public suspend fun exit(all: Boolean = false)

    /**
     * Plays the enter animation for a single item identified by [index].
     *
     * @param index The unique index of the item to animate.
     * @return `true` if the item was found and animated, `false` otherwise.
     * @since 0.0.1
     */
    public suspend fun enter(index: Int): Boolean

    /**
     * Plays the exit animation for a single item identified by [index].
     *
     * @param index The unique index of the item to animate.
     * @return `true` if the item was found and animated, `false` otherwise.
     * @since 0.0.1
     */
    public suspend fun exit(index: Int): Boolean

    /**
     * Checks whether an animation sequence is currently in progress.
     *
     * @return `true` if an animation is running, `false` otherwise.
     * @since 0.0.1
     */
    public fun isAnimating(): Boolean
}

/**
 * Internal implementation of [SequentialAnimationScope] that manages a sequence of animations
 * with custom delays between them. Supports parent-child nesting for cascading exit calls.
 *
 * @since 0.0.1
 */
private class SequentialAnimationHost : SequentialAnimationScope {
    // Using ConcurrentHashMap for thread safety
    private val items = ConcurrentMutableMap<Int, AnimationItem>()
    private val isAnimationInProgress = atomic(false)

    private var parent: SequentialAnimationHost? = null
    private val children = mutableListOf<SequentialAnimationHost>()

    /**
     * Registers a child with the host
     *
     * @param child Child to add to this host
     */
    fun addChild(child: SequentialAnimationHost) {
        children.add(child)
        child.parent = this
    }

    /**
     * Removes a child from the host
     *
     * @param child Child to remove from this host
     */
    fun removeChild(child: SequentialAnimationHost) {
        children.remove(child)
        child.parent = null
    }

    /**
     * Registers an animation item with the host
     *
     * @param index Unique identifier and position in the animation sequence
     * @param enterTransition Animation to play when entering
     * @param exitTransition Animation to play when exiting
     * @param delay Time to wait after this animation before starting the next one
     * @return MutableTransitionState that controls the visibility of this item
     */
    fun registerItem(
        index: Int,
        enterTransition: EnterTransition,
        exitTransition: ExitTransition,
        delay: Long,
    ): MutableTransitionState<Boolean> {
        // Check if this index is already registered
        if (items.containsKey(index)) {
            val existingItem = items[index]

            // If the item exists with the same parameters, just return its visibility state
            if (existingItem != null &&
                @Suppress("ComplexCondition")
                existingItem.enterTransition
                == enterTransition &&
                existingItem.exitTransition == exitTransition &&
                existingItem.delay == delay
            ) {
                return existingItem.visibilityState
            }

            // Otherwise, throw an exception
            throw IllegalArgumentException(
                "Animation with index $index is already registered. Each index must be unique.",
            )
        }

        // Create a new item
        val visibilityState = MutableTransitionState(false)
        val newItem =
            AnimationItem(
                index = index,
                visibilityState = visibilityState,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                delay = delay,
                enterDuration = calculateTransitionDuration(enterTransition),
                exitDuration = calculateTransitionDuration(exitTransition),
            )
        items[index] = newItem
        return visibilityState
    }

    /**
     * Unregisters an animation item
     *
     * @param index The index of the item to unregister
     */
    fun unregisterItem(index: Int) {
        items.remove(index)
    }

    /** Clears all items from the host. */
    fun clearItems() {
        items.clear()
    }

    /** Plays the enter animation for all items in sequence based on their index. */
    override suspend fun enter() {
        if (isAnimationInProgress.value) return
        try {
            isAnimationInProgress.compareAndSet(false, true)

            // Calculate the maximum exit transition duration
            val maxExitDuration = items.values.maxOfOrNull { it.exitDuration } ?: 0L

            // Set all items to hidden state
            items.values.forEach { it.visibilityState.targetState = false }

            // Wait for all exit animations to complete
            delay(maxExitDuration)

            // Sort items by index to ensure correct animation order
            val sortedItems = items.values.sortedBy { it.index }

            // Start enter animations in sequence with custom delays
            for (item in sortedItems) {
                try {
                    item.visibilityState.targetState = true
                    delay(item.delay)
                } catch (e: CancellationException) {
                    // Propagate cancellation
                    throw e
                } catch (e: Exception) {
                    // Log error but continue with next animation
                    android.util.Log.e("AnimatedSequence", "Error animating item ${item.index}", e)
                }
            }
        } catch (e: CancellationException) {
            // Propagate cancellation
            throw e
        } catch (e: Exception) {
            android.util.Log.e("AnimatedSequence", "Error in enter sequence", e)
        } finally {
            isAnimationInProgress.compareAndSet(true, false)
        }
    }

    /** Plays the exit animation for all items, either simultaneously or in reverse order. */
    override suspend fun exit(all: Boolean) {
        if (isAnimationInProgress.value) return
        try {
            isAnimationInProgress.compareAndSet(false, true)

            for (child in children) {
                child.exit(all)
            }

            if (all) {
                items.values.forEach { it.visibilityState.targetState = false }
                isAnimationInProgress.compareAndSet(true, false)
                return
            }

            // Sort items by index to ensure correct animation order
            val sortedItems = items.values.sortedByDescending { it.index }

            // Start exit animations in sequence with custom delays
            for (item in sortedItems) {
                try {
                    item.visibilityState.targetState = false
                    delay(item.delay)
                } catch (e: CancellationException) {
                    // Propagate cancellation
                    throw e
                } catch (e: Exception) {
                    // Log error but continue with next animation
                    android.util.Log.e("AnimatedSequence", "Error animating item ${item.index}", e)
                }
            }
            items.values.forEach { it.visibilityState.targetState = false }
        } catch (e: CancellationException) {
            // Propagate cancellation
            throw e
        } catch (e: Exception) {
            println("Error in exit sequence: ${e.message}")
        } finally {
            isAnimationInProgress.compareAndSet(true, false)
        }
    }

    /**
     * Plays the enter animation for a specific item by index
     *
     * @param index The index of the item to animate
     * @return true if the item was found and animated, false otherwise
     */
    override suspend fun enter(index: Int): Boolean {
        val matchingItem = items.values.find { it.index == index }
        if (matchingItem == null) return false

        try {
            // Set items to hidden state
            matchingItem.visibilityState.targetState = false

            // Wait for exit animations to complete
            delay(matchingItem.exitDuration)

            // Start enter animations
            matchingItem.visibilityState.targetState = true
            return true
        } catch (e: Exception) {
            println("Error in enter(index): ${e.message}")
            return false
        }
    }

    /**
     * Plays the exit animation for a specific item by index
     *
     * @param index The index of the item to animate
     * @return true if the item was found and animated, false otherwise
     */
    override suspend fun exit(index: Int): Boolean {
        val matchingItem = items.values.find { it.index == index }
        if (matchingItem == null) return false

        matchingItem.visibilityState.targetState = false
        return true
    }

    /**
     * Checks if animation is currently in progress
     *
     * @return true if an animation is running, false otherwise
     */
    override fun isAnimating(): Boolean {
        return isAnimationInProgress.value
    }

    /**
     * Calculates the duration of a transition Uses a safer approach than reflection
     *
     * @param transition The transition to analyze
     * @return The duration in milliseconds
     */
    private fun calculateTransitionDuration(transition: Any): Long {
        if (transition is Transition<*>) {
            // Convert nanoseconds to milliseconds (1_000_000 ns = 1 ms) and guard against overflow
            val millis = (transition.totalDurationNanos / 1_000_000L).coerceAtMost(Long.MAX_VALUE)
            return millis
        }

        // For more complex transitions, use a conservative estimate
        // Most Compose animations default to around 300ms
        return AnimationDefaults.DEFAULT_DURATION
    }
}

/**
 * A composable container that orchestrates sequential enter/exit animations for its children.
 *
 * Child composables register themselves via [AnimatedItem] and are animated in index order when
 * the host starts. Hosts can be nested; a parent host will cascade exit calls to its children.
 *
 * @param modifier Modifier to be applied to the [Box].
 * @param startByDefault When `true`, the enter sequence starts automatically on first composition.
 * @param content The content block receiving a [SequentialAnimationScope] for manual control.
 * @see AnimatedItem
 * @see SequentialAnimationScope
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun AnimationSequenceHost(
    modifier: Modifier = Modifier,
    startByDefault: Boolean = true,
    content: @Composable BoxScope.(scope: SequentialAnimationScope) -> Unit,
) {
    val parent = LocalSequentialAnimationHost.current

    // Create and remember the animation host
    val host = remember { SequentialAnimationHost() }

    // Register this host with its parent, if one exists
    parent?.addChild(host)

    // Start the animation sequence automatically if startByDefault is true
    if (startByDefault) {
        LaunchedEffect(Unit) {
            try {
                host.enter()
            } catch (e: CancellationException) {
                // Normal cancellation, no action needed
            } catch (e: Exception) {
                println("Error starting animations: ${e.message}")
            }
        }
    }

    // Provide the host to child composables through composition local
    CompositionLocalProvider(LocalSequentialAnimationHost provides host) {
        Box(modifier = modifier) { content(host) }
    }

    DisposableEffect(Unit) {
        onDispose {
            parent?.removeChild(host)
            host.clearItems() // Clear items when the composable is disposed
        }
    }
}

/**
 * A composable wrapper that registers its content as an animated item in the nearest
 * [AnimationSequenceHost].
 *
 * Each item is identified by a unique [index] that determines its position in the animation
 * sequence. The item is hidden by default and becomes visible when the host triggers
 * its enter sequence.
 *
 * @param modifier Modifier to be applied to the [AnimatedVisibility].
 * @param index The unique index that determines the item's position in the animation sequence.
 *   Must be unique within the enclosing [AnimationSequenceHost].
 * @param delayAfterAnimation The delay in milliseconds to wait after this item's enter animation
 *   before the next item starts. Defaults to [AnimationDefaults.DEFAULT_DELAY] (400 ms).
 * @param enter The [EnterTransition] to apply when the item becomes visible.
 *   Defaults to a 300 ms fade-in.
 * @param exit The [ExitTransition] to apply when the item becomes hidden.
 *   Defaults to a 300 ms fade-out.
 * @param content The composable content to animate.
 * @see AnimationSequenceHost
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun AnimatedItem(
    modifier: Modifier = Modifier,
    index: Int,
    delayAfterAnimation: Long = AnimationDefaults.DEFAULT_DELAY,
    enter: EnterTransition = fadeIn(tween(AnimationDefaults.DEFAULT_DURATION.toInt())),
    exit: ExitTransition = fadeOut(tween(AnimationDefaults.DEFAULT_DURATION.toInt())),
    content: @Composable () -> Unit,
) {
    // Get the animation host from the composition local
    val host =
        LocalSequentialAnimationHost.current
            ?: error("SequentialAnimation must be used within a SequentialAnimationHost")

    // Register with index as key
    val visibilityState =
        remember(index) {
            host.registerItem(
                index = index,
                enterTransition = enter,
                exitTransition = exit,
                delay = delayAfterAnimation,
            )
        }

    // Clean up when the composable leaves the composition
    DisposableEffect(index) { onDispose { host.unregisterItem(index) } }

    // Update if parameters change
    LaunchedEffect(enter, exit, delayAfterAnimation) {
        try {
            host.registerItem(
                index = index,
                enterTransition = enter,
                exitTransition = exit,
                delay = delayAfterAnimation,
            )
        } catch (e: IllegalArgumentException) {
            // Already registered with this index, ignore
        }
    }

    // Use AnimatedVisibility with the state managed by the host
    AnimatedVisibility(
        modifier = modifier,
        visibleState = visibilityState,
        enter = enter,
        exit = exit,
    ) { content() }
}
