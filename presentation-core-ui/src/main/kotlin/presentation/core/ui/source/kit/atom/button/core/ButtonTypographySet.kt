package presentation.core.ui.source.kit.atom.button.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

/**
 * Interface representing a set of predefined typography styles for buttons.
 *
 * This interface defines functions to obtain different typography styles for buttons based on their sizes.
 *
 * References:
 *
 * - https://proandroiddev.com/compose-a-compose-button-by-composing-composable-functions-9f275772bd23
 * - https://github.com/aoriani/ComposeButton/tree/main
 */
public interface ButtonTypographySet {
    /**
     * Returns the typography style for a button with a size of 32dp.
     *
     * @return An [TextStyle] instance representing the typography style for a 32dp button.
     */
    @Composable
    public fun buttonSize32(): TextStyle

    /**
     * Returns the typography style for a button with a size of 40dp.
     *
     * @return An [TextStyle] instance representing the typography style for a 40dp button.
     */
    @Composable
    public fun buttonSize40(): TextStyle

    /**
     * Returns the typography style for a button with a size of 48dp.
     *
     * @return An [TextStyle] instance representing the typography style for a 48dp button.
     */
    @Composable
    public fun buttonSize48(): TextStyle

    /**
     * Returns the typography style for a button with a size of 56dp.
     *
     * @return An [TextStyle] instance representing the typography style for a 56dp button.
     */
    @Composable
    public fun buttonSize56(): TextStyle

    /**
     * Returns the typography style for a button with a size of 64dp.
     *
     * @return An [TextStyle] instance representing the typography style for a 64dp button.
     */
    @Composable
    public fun buttonSize64(): TextStyle
}
