package presentation.core.ui.source.kit.atom.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import presentation.core.ui.source.kit.atom.snackbar.internal.StackedSnackbar
import presentation.core.ui.source.kit.atom.snackbar.internal.StackedSnackbarData

/**
 * A composable host that displays stacked snackbar notifications managed by the given [hostState].
 *
 * Snackbars are displayed in a stacked layout with the newest on top. Each snackbar auto-dismisses
 * based on its configured duration. The host handles enter/exit animations and coordinates
 * removal timing.
 *
 * @param hostState the [StackedSnakbarHostState] that manages the snackbar queue and visibility.
 * @param modifier Modifier to be applied to the [Box].
 *
 * @see StackedSnakbarHostState
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
 */
@Composable
public fun StackedSnackbarHost(hostState: StackedSnakbarHostState, modifier: Modifier = Modifier) {
    val firstItemVisible by hostState.newSnackbarHosted.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(hostState.currentSnackbarData) {
        val data = hostState.currentSnackbarData
        data.firstOrNull()?.let {
            delay(it.showDuration.toMillis())
            if (data.size == 1) {
                hostState.newSnackbarHosted.value = false
                delay(500)
                hostState.currentSnackbarData =
                    hostState.currentSnackbarData.toMutableList().apply {
                        remove(it)
                    }
                hostState.newSnackbarHosted.value = true
            }
        }
    }
    if (hostState.currentSnackbarData.isNotEmpty()) {
        StackedSnackbar(
            snackbarData = hostState.currentSnackbarData.toList(),
            onSnackbarRemoved = {
                hostState.newSnackbarHosted.value = false
                coroutineScope.launch {
                    delay(500)
                    hostState.currentSnackbarData =
                        hostState.currentSnackbarData.toMutableList().apply {
                            removeLastOrNull()
                        }
                    hostState.newSnackbarHosted.value = true
                }
            },
            firstItemVisibility = firstItemVisible,
            maxStack = hostState.maxStack,
            animation = hostState.animation,
            modifier = modifier,
        )
    }
}

/**
 * State holder for the stacked snackbar host, managing the queue of visible snackbar data.
 *
 * Use [showSnackbar] to enqueue a standard text snackbar or [showCustomSnackbar] to enqueue
 * a fully custom composable snackbar.
 *
 * @param coroutinesScope the [CoroutineScope] used for animation delays and state transitions.
 * @param animation the [StackedSnackbarAnimation] preset controlling enter/exit transitions.
 * @param maxStack the maximum number of snackbars visible in the stack simultaneously.
 *
 * @see StackedSnackbarHost
 * @see rememberStackedSnackbarHostState
 *
 * @since 0.0.1
 */
@Stable
public class StackedSnakbarHostState(
    private val coroutinesScope: CoroutineScope,
    public val animation: StackedSnackbarAnimation,
    public val maxStack: Int = Int.MAX_VALUE,
) {
    internal var currentSnackbarData by mutableStateOf<List<StackedSnackbarData>>(emptyList())
    internal val newSnackbarHosted = MutableStateFlow(false)

    /**
     * Enqueues a standard text-based snackbar for display.
     *
     * @param title the primary title text of the snackbar.
     * @param description optional secondary description text.
     * @param actionTitle optional label for the action button.
     * @param action optional callback invoked when the action button is clicked.
     * @param duration how long the snackbar should remain visible.
     *
     * @since 0.0.1
     */
    public fun showSnackbar(
        title: String,
        description: String? = null,
        actionTitle: String? = null,
        action: (() -> Unit)? = null,
        duration: StackedSnackbarDuration = StackedSnackbarDuration.Indefinite,
    ) {
        showSnackbar(
            data =
            StackedSnackbarData.Normal(
                title,
                description,
                actionTitle,
                action,
                duration,
            ),
        )
    }

    /**
     * Enqueues a custom composable snackbar for display.
     *
     * @param content the composable content of the snackbar. Receives a dismiss callback as parameter.
     * @param duration how long the snackbar should remain visible.
     *
     * @since 0.0.1
     */
    public fun showCustomSnackbar(
        content: @Composable (() -> Unit) -> Unit,
        duration: StackedSnackbarDuration = StackedSnackbarDuration.Indefinite,
    ) {
        showSnackbar(
            data = StackedSnackbarData.Custom(content, duration),
        )
    }

    private fun showSnackbar(data: StackedSnackbarData) {
        newSnackbarHosted.value = false
        currentSnackbarData =
            currentSnackbarData.toMutableList().apply {
                if (!contains(data)) {
                    add(data)
                }
            }
        // Short delay (100ms) allows the composition to process the new snackbar data
        // before triggering the enter animation visibility flag
        @Suppress("MagicNumber")
        coroutinesScope.launch {
            delay(1_00)
            newSnackbarHosted.value = true
        }
    }
}

// Suppressed: duration values are intentional UI timing constants, not arbitrary magic numbers
@Suppress("MagicNumber")
private fun StackedSnackbarDuration.toMillis(): Long = when (this) {
    StackedSnackbarDuration.Short -> 4000L
    StackedSnackbarDuration.Long -> 10000L
    StackedSnackbarDuration.Indefinite -> Long.MAX_VALUE
}
