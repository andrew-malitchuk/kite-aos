package presentation.core.ui.source.kit.atom.button.core

/**
 * Object representing different interaction states for a button.
 *
 * This object contains constants that define various states such as hover, pressed, and focused.
 * Each state is represented as a single bit in an [Int] bitmask, allowing multiple states to be
 * combined via bitwise OR operations.
 *
 * References:
 *
 * - https://proandroiddev.com/compose-a-compose-button-by-composing-composable-functions-9f275772bd23
 * - https://github.com/aoriani/ComposeButton/tree/main
 *
 * @since 0.0.1
 */
public object ButtonInteractionState {
    /**
     * Represents the hover state of a button.
     *
     * Bit 0 (value = 1).
     *
     * @since 0.0.1
     */
    @JvmStatic
    public val HOVER: Int = 1.shl(0)

    /**
     * Represents the pressed state of a button.
     *
     * Bit 1 (value = 2).
     *
     * @since 0.0.1
     */
    @JvmStatic
    public val PRESSED: Int = 1.shl(1)

    /**
     * Represents the focused state of a button.
     *
     * Bit 2 (value = 4).
     *
     * @since 0.0.1
     */
    @JvmStatic
    public val FOCUSED: Int = 1.shl(2)

    /**
     * Represents the selected state of a button.
     *
     * Bit 3 (value = 8).
     *
     * @since 0.0.1
     */
    @JvmStatic
    public val SELECTED: Int = 1.shl(3)
}
