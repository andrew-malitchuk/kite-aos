package presentation.core.ui.source.kit.atom.button.secondary.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.Theme
import presentation.core.ui.core.ext.has
import presentation.core.ui.source.kit.atom.button.core.ButtonColor
import presentation.core.ui.source.kit.atom.button.core.ButtonInteractionState

/**
 * [ButtonColor] implementation for secondary buttons.
 *
 * Resolves border, foreground (text/icon), and background colors based on the current
 * interaction state and enabled flag. The border is always transparent; the button uses
 * a tonal surface background to differentiate from primary buttons.
 *
 * @see ButtonColor
 * @since 0.0.1
 */
public class SecondaryButtonColor : ButtonColor {
    /**
     * Returns a transparent border color for secondary buttons.
     *
     * @param interactionState Bitmask of active [ButtonInteractionState] flags.
     * @param enabled Whether the button is enabled.
     * @param loading Whether the button is in a loading state.
     * @return A [State] of [Color] that is always [Color.Transparent].
     *
     * @since 0.0.1
     */
    @Composable
    override fun borderColor(interactionState: Int, enabled: Boolean, loading: Boolean): State<Color> =
        rememberUpdatedState(Color.Transparent)

    /**
     * Resolves the foreground (text and icon) color for the secondary button.
     *
     * @param interactionState Bitmask of active [ButtonInteractionState] flags.
     * @param enabled Whether the button is enabled.
     * @param loading Whether the button is in a loading state.
     * @return A [State] of [Color] representing the resolved foreground color.
     *
     * @since 0.0.1
     */
    @Composable
    override fun foregroundColor(interactionState: Int, enabled: Boolean, loading: Boolean): State<Color> =
        rememberUpdatedState(
            when {
                // Disabled text color
                !enabled -> Theme.color.disabled
                // Pressed state feedback (slightly different brand shade if needed)
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.brandVariant
                // Primary brand color for text on a subtle background
                else -> Theme.color.brand
            },
        )

    /**
     * Resolves the background color for the secondary button.
     *
     * @param interactionState Bitmask of active [ButtonInteractionState] flags.
     * @param enabled Whether the button is enabled.
     * @param loading Whether the button is in a loading state.
     * @return A [State] of [Color] representing the resolved background color.
     *
     * @since 0.0.1
     */
    @Composable
    override fun backgroundColor(interactionState: Int, enabled: Boolean, loading: Boolean): State<Color> =
        rememberUpdatedState(
            when {
                // Subtle transparent background for disabled state; 0.5f alpha for a faded look
                !enabled -> Theme.color.surfaceVariant.copy(alpha = 0.5f)
                // Darker background when pressed
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.outlineLow
                // Tonal background (Soft Blue/Grey)
                else -> Theme.color.surfaceVariant
            },
        )
}
