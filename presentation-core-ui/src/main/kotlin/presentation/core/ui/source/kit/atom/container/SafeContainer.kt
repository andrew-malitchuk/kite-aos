package presentation.core.ui.source.kit.atom.container

import presentation.core.ui.source.kit.atom.snackbar.StackedSnackbarHost
import presentation.core.ui.source.kit.atom.snackbar.StackedSnakbarHostState
import presentation.core.ui.source.kit.atom.snackbar.rememberStackedSnackbarHostState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.Scaffold

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

@Composable
public fun SafeContainer(
    modifier: Modifier = Modifier,
    snackbarHostState: StackedSnakbarHostState = rememberStackedSnackbarHostState(),
    snackbarHost: @Composable (StackedSnakbarHostState) -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            StackedSnackbarHost(hostState = it)
        }
    },
    content: @Composable (PaddingValues) -> Unit
) {
    // On API < 30, we don't use Edge-to-Edge, so the system already handles 
    // system bars and keyboard resizing (adjustResize) at the window level.
    // Providing insets here would cause double padding or layout "gaps".
    val windowInsets = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            ) {
                content(paddingValues)
            }
        }
    )
}