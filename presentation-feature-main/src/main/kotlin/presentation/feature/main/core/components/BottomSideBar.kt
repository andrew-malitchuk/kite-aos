package presentation.feature.main.core.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.core.ui.source.kit.atom.dialog.BlurredCustomBottomDrawerOverlay

/**
 * Specific implementation of [SideBar] that uses a bottom-docked blurred overlay.
 *
 * Wraps [BlurredCustomBottomDrawerOverlay] from the Design System to provide a
 * consistent drawer experience when docked at the bottom of the screen.
 *
 * @param modifier Modifier to be applied to the [BlurredCustomBottomDrawerOverlay] root composable.
 * @param isDrawerOpen Whether the drawer overlay is currently visible.
 * @param onDismiss Callback invoked when the user dismisses the drawer (e.g., tapping outside).
 * @param drawer The composable content to display inside the drawer panel.
 * @param content The primary screen content rendered behind the overlay.
 * @see SideBar
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
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
