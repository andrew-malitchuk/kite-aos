package presentation.core.ui.source.kit.atom.button.core

/**
 * Interface representing a set of predefined button sizes.
 *
 * This interface defines functions to obtain different button size configurations.
 *
 * References:
 *
 * - https://proandroiddev.com/compose-a-compose-button-by-composing-composable-functions-9f275772bd23
 * - https://github.com/aoriani/ComposeButton/tree/main
 */
// TODO remove to t-shirts
public interface ButtonSizeSet {
    /**
     * Returns the button size configuration for a button with a size of 32dp.
     *
     * @return A [ButtonSize] instance representing the button size configuration for 32dp.
     */
    public fun buttonSize32(): ButtonSize

    /**
     * Returns the button size configuration for a button with a size of 40dp.
     *
     * @return A [ButtonSize] instance representing the button size configuration for 40dp.
     */
    public fun buttonSize40(): ButtonSize

    /**
     * Returns the button size configuration for a button with a size of 48dp.
     *
     * @return A [ButtonSize] instance representing the button size configuration for 48dp.
     */
    public fun buttonSize48(): ButtonSize

    /**
     * Returns the button size configuration for a button with a size of 56dp.
     *
     * @return A [ButtonSize] instance representing the button size configuration for 56dp.
     */
    public fun buttonSize56(): ButtonSize

    /**
     * Returns the button size configuration for a button with a size of 64dp.
     *
     * @return A [ButtonSize] instance representing the button size configuration for 64dp.
     */
    public fun buttonSize64(): ButtonSize
}
