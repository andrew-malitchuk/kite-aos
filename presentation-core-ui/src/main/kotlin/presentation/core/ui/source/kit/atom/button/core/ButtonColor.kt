package presentation.core.ui.source.kit.atom.button.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color

/**
 * Interface representing the color configuration for a button.
 *
 * This interface defines composable functions to obtain the border, foreground, and background colors of a
 * button based on its interaction state, enabled state, and loading state.
 *
 * References:
 *
 * - https://proandroiddev.com/compose-a-compose-button-by-composing-composable-functions-9f275772bd23
 * - https://github.com/aoriani/ComposeButton/tree/main
 */
public interface ButtonColor {
    /**
     * Returns the border color of the button based on the interaction state, enabled state, and loading state.
     *
     * @param interactionState An integer representing the current interaction state of the button.
     * @param enabled A boolean indicating whether the button is enabled.
     * @param loading A boolean indicating whether the button is in a loading state.
     * @return A [State] of [Color] representing the border color of the button.
     */
    @Stable
    @Composable
    public fun borderColor(
        interactionState: Int,
        enabled: Boolean,
        loading: Boolean,
    ): State<Color>

    /**
     * Returns the foreground color of the button based on the interaction state, enabled state, and loading state.
     *
     * @param interactionState An integer representing the current interaction state of the button.
     * @param enabled A boolean indicating whether the button is enabled.
     * @param loading A boolean indicating whether the button is in a loading state.
     * @return A [State] of [Color] representing the foreground color of the button.
     */
    @Stable
    @Composable
    public fun foregroundColor(
        interactionState: Int,
        enabled: Boolean,
        loading: Boolean,
    ): State<Color>

    /**
     * Returns the background color of the button based on the interaction state, enabled state, and loading state.
     *
     * @param interactionState An integer representing the current interaction state of the button.
     * @param enabled A boolean indicating whether the button is enabled.
     * @param loading A boolean indicating whether the button is in a loading state.
     * @return A [State] of [Color] representing the background color of the button.
     */
    @Stable
    @Composable
    public fun backgroundColor(
        interactionState: Int,
        enabled: Boolean,
        loading: Boolean,
    ): State<Color>
}
