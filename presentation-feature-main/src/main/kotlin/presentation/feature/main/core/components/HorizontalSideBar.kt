package presentation.feature.main.core.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.core.ui.source.kit.atom.dialog.BlurredCustomSideDrawerOverlay

/**
 * Specific implementation of [SideBar] that uses a side-docked blurred overlay.
 *
 * Wraps [BlurredCustomSideDrawerOverlay] from the Design System to provide a
 * consistent drawer experience when docked at the left side of the screen.
 *
 * @param modifier Modifier to be applied to the [BlurredCustomSideDrawerOverlay] root composable.
 * @param isDrawerOpen Whether the drawer overlay is currently visible.
 * @param onDismiss Callback invoked when the user dismisses the drawer (e.g., tapping outside).
 * @param drawer The composable content to display inside the drawer panel.
 * @param content The primary screen content rendered behind the overlay.
 * @see SideBar
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun HorizontalSideBar(
    modifier: Modifier = Modifier,
    isDrawerOpen: Boolean,
    onDismiss: () -> Unit,
    drawer: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    BlurredCustomSideDrawerOverlay(
        modifier = modifier,
        isDrawerOpen = isDrawerOpen,
        onDismiss = onDismiss,
        drawerContent = drawer,
        content = content,
    )
}
