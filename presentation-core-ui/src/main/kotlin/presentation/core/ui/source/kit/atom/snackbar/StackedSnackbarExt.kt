package presentation.core.ui.source.kit.atom.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

/**
 * Remembers and returns a [StackedSnakbarHostState] scoped to the current composition.
 *
 * The returned state is backed by a [CoroutineScope] tied to the composition lifecycle,
 * ensuring snackbar animations and timeouts are properly cancelled on disposal.
 *
 * @param maxStack the maximum number of snackbars that can be stacked simultaneously.
 * @param animation the [StackedSnackbarAnimation] preset to use for transitions.
 * @return a remembered [StackedSnakbarHostState] instance.
 *
 * @see StackedSnakbarHostState
 *
 * @since 0.0.1
 */
@Composable
public fun rememberStackedSnackbarHostState(
    maxStack: Int = Int.MAX_VALUE,
    animation: StackedSnackbarAnimation = StackedSnackbarAnimation.Bounce,
): StackedSnakbarHostState = run {
    val scope = rememberCoroutineScope()
    remember {
        StackedSnakbarHostState(animation = animation, maxStack = maxStack, coroutinesScope = scope)
    }
}
