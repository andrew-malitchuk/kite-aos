package presentation.core.ui.source.kit.atom.button.primary.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.Theme
import presentation.core.ui.core.ext.has
import presentation.core.ui.source.kit.atom.button.core.ButtonColor
import presentation.core.ui.source.kit.atom.button.core.ButtonInteractionState

/**
 * [ButtonColor] implementation for primary buttons.
 *
 * Resolves border, foreground (text/icon), and background colors based on the current
 * interaction state and enabled flag. The background is always transparent; the button
 * relies on its border and foreground to convey state.
 *
 * @see ButtonColor
 * @since 0.0.1
 */
public class PrimaryButtonColor : ButtonColor {
    /**
     * Resolves the border color for the primary button.
     *
     * @param interactionState Bitmask of active [ButtonInteractionState] flags.
     * @param enabled Whether the button is enabled.
     * @param loading Whether the button is in a loading state.
     * @return A [State] of [Color] representing the resolved border color.
     *
     * @since 0.0.1
     */
    @Composable
    override fun borderColor(interactionState: Int, enabled: Boolean, loading: Boolean): State<Color> =
        rememberUpdatedState(
            when {
                !enabled -> Color.Transparent
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.brand
                else -> Color.White // Default white border
            },
        )

    /**
     * Resolves the foreground (text and icon) color for the primary button.
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
                !enabled -> Theme.color.inkSubtle
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.brand
                else -> Color.White // Default white foreground (text/icon)
            },
        )

    /**
     * Returns a transparent background color for primary buttons.
     *
     * @param interactionState Bitmask of active [ButtonInteractionState] flags.
     * @param enabled Whether the button is enabled.
     * @param loading Whether the button is in a loading state.
     * @return A [State] of [Color] that is always [Color.Transparent].
     *
     * @since 0.0.1
     */
    @Composable
    override fun backgroundColor(interactionState: Int, enabled: Boolean, loading: Boolean): State<Color> =
        rememberUpdatedState(Color.Transparent) // Always transparent background
}
