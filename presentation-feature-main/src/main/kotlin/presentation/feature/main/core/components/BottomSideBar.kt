package presentation.feature.main.core.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.core.ui.source.kit.atom.dialog.BlurredCustomBottomDrawerOverlay

/**
 * Specific implementation of [SideBar] that uses a bottom-docked blurred overlay.
 */
@Composable
public fun BottomSideBar(
    modifier: Modifier = Modifier,
    isDrawerOpen: Boolean,
    onDismiss: () -> Unit,
    drawer: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    BlurredCustomBottomDrawerOverlay(
        modifier = modifier,
        isDrawerOpen = isDrawerOpen,
        onDismiss = onDismiss,
        drawerContent = drawer,
        content = content,
    )
}
