package presentation.feature.main.core.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.core.ui.source.kit.atom.dialog.BlurredCustomBottomDrawerOverlay
import presentation.core.ui.source.kit.atom.dialog.BlurredCustomSideDrawerOverlay

/**
 * A wrapper component that chooses between Bottom and Side drawer overlays.
 *
 * This component abstracts the placement of the control drawer, using either
 * [BlurredCustomBottomDrawerOverlay] or [BlurredCustomSideDrawerOverlay] from
 * the Design System based on the kiosk configuration.
 *
 * @param modifier The modifier for the sidebar container.
 * @param isBottom Whether the drawer should appear from the bottom (true) or left side (false).
 * @param isDrawerOpen Visibility state of the drawer.
 * @param onDismiss Callback when the drawer is dismissed (e.g., clicking outside).
 * @param drawer The content to display inside the drawer (typically [ControlDrawer]).
 * @param content The primary screen content (the [KioskWebView]).
 */
@Composable
public fun SideBar(
    modifier: Modifier = Modifier,
    isBottom: Boolean,
    isDrawerOpen: Boolean,
    onDismiss: () -> Unit,
    drawer: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    when (isBottom) {
        true ->
            BlurredCustomBottomDrawerOverlay(
                modifier = modifier,
                isDrawerOpen = isDrawerOpen,
                onDismiss = onDismiss,
                drawerContent = drawer,
                content = content,
            )

        false ->
            BlurredCustomSideDrawerOverlay(
                modifier = modifier,
                isDrawerOpen = isDrawerOpen,
                onDismiss = onDismiss,
                drawerContent = drawer,
                content = content,
            )
    }
}
