package presentation.core.ui.source.kit.atom.snackbar.internal

import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
internal sealed class StackedSnackbarData(val showDuration: StackedSnackbarDuration) {
    data class Normal(
        val title: String,
        val description: String? = null,
        val actionTitle: String? = null,
        val action: (() -> Unit)? = null,
        val duration: StackedSnackbarDuration = StackedSnackbarDuration.Short,
    ) : StackedSnackbarData(duration)

    data class Custom(
        val content: @Composable (() -> Unit) -> Unit,
        val duration: StackedSnackbarDuration = StackedSnackbarDuration.Short,
    ) : StackedSnackbarData(duration)
}



