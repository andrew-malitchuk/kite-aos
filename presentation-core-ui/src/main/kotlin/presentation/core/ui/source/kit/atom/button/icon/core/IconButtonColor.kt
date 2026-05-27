package presentation.core.ui.source.kit.atom.button.icon.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.Theme
import presentation.core.ui.core.ext.has
import presentation.core.ui.source.kit.atom.button.core.ButtonColor
import presentation.core.ui.source.kit.atom.button.core.ButtonInteractionState

/**
 * [ButtonColor] implementation for icon buttons.
 *
 * Resolves border, foreground (icon tint), and background colors based on the current
 * interaction state, enabled flag, and loading flag. The border is always transparent for
 * icon buttons.
 *
 * @param containerColor The default background color when the button is idle.
 * @param contentColor The default icon tint color when the button is idle.
 * @param selectedContainerColor The background color when the button is selected.
 * @param selectedContentColor The icon tint color when the button is selected.
 * @param disabledContainerColor The background color when the button is disabled.
 * @param disabledContentColor The icon tint color when the button is disabled.
 *
 * @see ButtonColor
 * @since 0.0.1
 */
public class IconButtonColor(
    private val containerColor: Color,
    private val contentColor: Color,
    private val selectedContainerColor: Color,
    private val selectedContentColor: Color,
    private val disabledContainerColor: Color,
    private val disabledContentColor: Color,
) : ButtonColor {
    /**
     * Returns a transparent border color for icon buttons.
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
     * Resolves the icon tint color based on the button state.
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
                !enabled -> disabledContentColor
                interactionState has ButtonInteractionState.SELECTED -> selectedContentColor
                else -> contentColor
            },
        )

    /**
     * Resolves the background color based on the button state.
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
                !enabled -> disabledContainerColor
                // Show subtle background on press
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.brandVariant
                interactionState has ButtonInteractionState.SELECTED -> selectedContainerColor
                else -> containerColor
            },
        )
}
