package presentation.core.ui.source.kit.atom.snackbar.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarDuration

/**
 * Sealed class representing the data model for a stacked snackbar item.
 *
 * @param showDuration how long this snackbar should be displayed.
 */
@Stable
internal sealed class StackedSnackbarData(val showDuration: StackedSnackbarDuration) {
    /**
     * A standard text-based snackbar with optional description and action button.
     *
     * @param title the primary title text.
     * @param description optional secondary description text.
     * @param actionTitle optional label for the action button.
     * @param action optional callback for the action button.
     * @param duration how long to display this snackbar.
     */
    data class Normal(
        val title: String,
        val description: String? = null,
        val actionTitle: String? = null,
        val action: (() -> Unit)? = null,
        val duration: StackedSnackbarDuration = StackedSnackbarDuration.Short,
    ) : StackedSnackbarData(duration)

    /**
     * A custom composable snackbar with fully user-defined content.
     *
     * @param content the composable content. Receives a dismiss callback as parameter.
     * @param duration how long to display this snackbar.
     */
    data class Custom(
        val content: @Composable (() -> Unit) -> Unit,
        val duration: StackedSnackbarDuration = StackedSnackbarDuration.Short,
    ) : StackedSnackbarData(duration)
}
