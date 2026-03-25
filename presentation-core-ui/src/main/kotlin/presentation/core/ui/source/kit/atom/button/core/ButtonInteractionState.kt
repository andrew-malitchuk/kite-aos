package presentation.core.ui.source.kit.atom.button.core

/**
 * Object representing different interaction states for a button.
 *
 * This object contains constants that define various states such as hover, pressed, and focused.
 *
 * References:
 *
 * - https://proandroiddev.com/compose-a-compose-button-by-composing-composable-functions-9f275772bd23
 * - https://github.com/aoriani/ComposeButton/tree/main
 */
public object ButtonInteractionState {
    /**
     * Represents the hover state of a button.
     */
    @JvmStatic
    public val HOVER: Int = 1.shl(0)

    /**
     * Represents the pressed state of a button.
     */
    @JvmStatic
    public val PRESSED: Int = 1.shl(1)

    /**
     * Represents the focused state of a button.
     */
    @JvmStatic
    public val FOCUSED: Int = 1.shl(2)

    /**
     * Represents the selected state of a button.
     */
    @JvmStatic
    public val SELECTED: Int = 1.shl(3)
}
