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

// Configuration constants moved to a single place
private object AnimationDefaults {
    const val DEFAULT_DURATION = 300L
    const val DEFAULT_DELAY = 400L
}

// Composition local to provide the animation host
private val LocalSequentialAnimationHost =
    staticCompositionLocalOf<SequentialAnimationHost?> { null }

/** Data class to represent an animation item in the sequence */
private data class AnimationItem(
    val index: Int,
    val visibilityState: MutableTransitionState<Boolean>,
    val enterTransition: EnterTransition,
    val exitTransition: ExitTransition,
    val delay: Long, // Custom delay in milliseconds
    val enterDuration: Long, // Pre-calculated enter duration
    val exitDuration: Long // Pre-calculated exit duration
)

/** Interface defining the operations that can be performed on a sequence of animations */
public interface SequentialAnimationScope {
    public suspend fun enter()
    public suspend fun exit(all: Boolean = false)
    public suspend fun enter(index: Int): Boolean
    public suspend fun exit(index: Int): Boolean
    public fun isAnimating(): Boolean
}

/** Class that manages a sequence of animations with custom delays between them */
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
        delay: Long
    ): MutableTransitionState<Boolean> {
        // Check if this index is already registered
        if (items.containsKey(index)) {
            val existingItem = items[index]

            // If the item exists with the same parameters, just return its visibility state
            if (existingItem != null &&
                existingItem.enterTransition == enterTransition &&
                existingItem.exitTransition == exitTransition &&
                existingItem.delay == delay
            ) {
                return existingItem.visibilityState
            }

            // Otherwise, throw an exception
            throw IllegalArgumentException(
                "Animation with index $index is already registered. Each index must be unique."
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
                exitDuration = calculateTransitionDuration(exitTransition)
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

    /** Clears all items from the host */
    fun clearItems() {
        items.clear()
    }

    /** Plays the enter animation for all items in sequence based on their index */
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

    /** Plays the exit animation for all items simultaneously */
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
            // Convert nanoseconds to milliseconds (and handle potential overflow)
            val millis = (transition.totalDurationNanos / 1_000_000L).coerceAtMost(Long.MAX_VALUE)
            return millis
        }

        // For more complex transitions, use a conservative estimate
        // Most Compose animations default to around 300ms
        return AnimationDefaults.DEFAULT_DURATION
    }
}

/** Composable function that creates and manages a sequential animation host */
@Composable
public fun AnimationSequenceHost(
    modifier: Modifier = Modifier,
    startByDefault: Boolean = true,
    content: @Composable BoxScope.(scope: SequentialAnimationScope) -> Unit
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

/** Composable for individual animated items that participate in a sequence */
@Composable
public fun AnimatedItem(
    modifier: Modifier = Modifier,
    index: Int,
    delayAfterAnimation: Long = AnimationDefaults.DEFAULT_DELAY,
    enter: EnterTransition = fadeIn(tween(AnimationDefaults.DEFAULT_DURATION.toInt())),
    exit: ExitTransition = fadeOut(tween(AnimationDefaults.DEFAULT_DURATION.toInt())),
    content: @Composable () -> Unit
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
                delay = delayAfterAnimation
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
                delay = delayAfterAnimation
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
        exit = exit
    ) { content() }
}