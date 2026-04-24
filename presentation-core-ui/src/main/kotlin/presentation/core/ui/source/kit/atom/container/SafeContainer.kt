package presentation.core.ui.source.kit.atom.container

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarHost
import presentation.core.ui.source.kit.atom.snackbar.StackedSnakbarHostState
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState

/**
 * A scaffold-based container that handles system bar insets, keyboard (IME) insets, and provides
 * an integrated stacked snackbar host.
 *
 * On API level 30 and above, the container applies edge-to-edge insets (system bars + IME).
 * On older APIs, insets are omitted to avoid double-padding since the system already handles
 * system bars and keyboard resizing at the window level.
 *
 * @param modifier Modifier to be applied to the [Scaffold].
 * @param snackbarHostState the [StackedSnakbarHostState] managing snackbar display.
 * @param snackbarHost composable slot for the snackbar host UI.
 * @param content the main screen content receiving [PaddingValues] from the scaffold.
 *
 * @see StackedSnackbarHost
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
 */
@Composable
public fun SafeContainer(
    modifier: Modifier = Modifier,
    snackbarHostState: StackedSnakbarHostState = rememberStackedSnackbarHostState(),
    snackbarHost: @Composable (StackedSnakbarHostState) -> Unit = {
        Box(
            modifier =
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            StackedSnackbarHost(hostState = it)
        }
    },
    content: @Composable (PaddingValues) -> Unit,
) {
    // On API < 30, we don't use Edge-to-Edge, so the system already handles
    // system bars and keyboard resizing (adjustResize) at the window level.
    // Providing insets here would cause double padding or layout "gaps".
    val windowInsets =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsets.systemBars.union(WindowInsets.ime)
        } else {
            WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
        }

    Scaffold(
        snackbarHost = { snackbarHost(snackbarHostState) },
        contentWindowInsets = windowInsets,
        modifier = modifier.fillMaxSize(),
        content = { paddingValues ->
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues),
            ) {
                content(paddingValues)
            }
        },
    )
}
