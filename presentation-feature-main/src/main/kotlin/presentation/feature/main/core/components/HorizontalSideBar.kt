package presentation.feature.main.core.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.core.ui.source.kit.atom.dialog.BlurredCustomSideDrawerOverlay

/**
 * Specific implementation of [SideBar] that uses a side-docked blurred overlay.
 */
@Composable
public fun HorizontalSideBar(
    modifier: Modifier = Modifier,
    isDrawerOpen: Boolean,
    onDismiss: () -> Unit,
    drawer: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    BlurredCustomSideDrawerOverlay(
        modifier = modifier,
        isDrawerOpen = isDrawerOpen,
        onDismiss = onDismiss,
        drawerContent = drawer,
        content = content
    )
}