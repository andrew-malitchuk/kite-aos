package presentation.core.ui.source.kit.atom.button.text.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.Theme
import presentation.core.ui.core.ext.has
import presentation.core.ui.source.kit.atom.button.core.ButtonColor
import presentation.core.ui.source.kit.atom.button.core.ButtonInteractionState

/**
 * [ButtonColor] implementation for text buttons.
 *
 * Resolves border, foreground (text), and background colors based on the current
 * interaction state and enabled flag. The border is always transparent. The background
 * is transparent except during a press, where a subtle overlay is applied.
 *
 * @see ButtonColor
 * @since 0.0.1
 */
public class TextButtonColor : ButtonColor {
    /**
     * Returns a transparent border color for text buttons.
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
     * Resolves the foreground (text) color for the text button.
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
                // Visual feedback for the text itself when pressed
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.brandVariant
                // Primary brand color (Lilac) for the text
                else -> Theme.color.brand
            },
        )

    /**
     * Resolves the background color for the text button.
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
                // Background is always transparent unless pressed
                !enabled -> Color.Transparent
                // Subtle overlay to indicate the click
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.outlineLow
                else -> Color.Transparent
            },
        )
}
