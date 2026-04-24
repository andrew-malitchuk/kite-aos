package presentation.core.ui.source.kit.atom.button.core

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Immutable interface representing the size configuration for a button.
 *
 * This interface defines properties for various size-related attributes of a button,
 * such as icon size, border size, content padding, spacing, minimum height, and loading size.
 *
 * References:
 *
 * - https://proandroiddev.com/compose-a-compose-button-by-composing-composable-functions-9f275772bd23
 * - https://github.com/aoriani/ComposeButton/tree/main
 *
 * @since 0.0.1
 */
@Immutable
public interface ButtonSize {
    /**
     * The size of the icon within the button.
     *
     * @return A [Dp] value representing the icon size.
     */
    public val iconSize: Dp
        @Composable
        get() = 0.dp

    /**
     * The size of the border around the button.
     *
     * @return A [Dp] value representing the border size.
     */
    public val borderSize: Dp
        @Composable
        get() = 0.dp

    /**
     * The padding values for the content within the button.
     *
     * @return A [PaddingValues] instance representing the content padding.
     */
    public val contentPadding: PaddingValues
        @Composable
        get() = PaddingValues()

    /**
     * The spacing within the button.
     *
     * @return A [Dp] value representing the spacing.
     */
    public val spacing: Dp
        @Composable
        get() = 0.dp

    /**
     * The minimum height of the button.
     *
     * @return A [Dp] value representing the minimum height.
     */
    public val minHeight: Dp
        @Composable
        get() = 0.dp

    /**
     * The size of the loading indicator within the button.
     *
     * @return A [Dp] value representing the loading size.
     */
    public val loadingSize: Dp
        @Composable
        get() = 0.dp
}
