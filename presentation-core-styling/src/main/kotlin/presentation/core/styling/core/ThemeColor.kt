package presentation.core.styling.core

import androidx.compose.ui.graphics.Color

/**
 * Theme color palette used across the application.
 *
 * Contains named colors grouped by purpose:
 * 1. Brand: accent colors for primary actions
 * 2. Surface: backgrounds and layers
 * 3. Ink: typography and icons
 * 4. Outline: borders and dividers
 * 5. Status: functional feedback
 * 6. Interaction: special states and overlays
 *
 * @property brand Primary brand color.
 * @property brandVariant Hover, pressed, or alternative brand state.
 * @property canvas Main background of the application (lowest layer).
 * @property surface Surface for cards, dialogs, and sheets.
 * @property surfaceVariant Subtle background for inputs or highlighted sections.
 * @property inkMain Primary text color with highest contrast.
 * @property inkSubtle Secondary text for captions and metadata.
 * @property inkOnBrand Text color used on top of brand backgrounds.
 * @property outlineLow Subtle dividers and separators.
 * @property outlineHigh Stronger borders for active inputs or focus states.
 * @property success Success states and confirmations.
 * @property error Error messages and destructive actions.
 * @property warning Warnings and non-blocking alerts.
 * @property disabled Background/text for inactive elements.
 * @property scrim Darkening overlay for modal backgrounds.
 * @see Theme
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
public data class ThemeColor(
    //region 1. Brand (Accent colors for primary actions)
    /**
     * Primary brand color.
     *
     * @since 0.0.1
     */
    val brand: Color,
    /**
     * Hover, pressed, or alternative brand state.
     *
     * @since 0.0.1
     */
    val brandVariant: Color,
    //endregion 1. Brand (Accent colors for primary actions)
    //region 2. Surface (Backgrounds and layers)
    /**
     * Main background of the application (lowest layer).
     *
     * @since 0.0.1
     */
    val canvas: Color,
    /**
     * Surface for cards, dialogs, and sheets.
     *
     * @since 0.0.1
     */
    val surface: Color,
    /**
     * Subtle background for inputs or highlighted sections.
     *
     * @since 0.0.1
     */
    val surfaceVariant: Color,
    //endregion 2. Surface (Backgrounds and layers)
    //region 3. Ink (Typography and icons)
    /**
     * Primary text color with highest contrast.
     *
     * @since 0.0.1
     */
    val inkMain: Color,
    /**
     * Secondary text for captions and metadata.
     *
     * @since 0.0.1
     */
    val inkSubtle: Color,
    /**
     * Text color used on top of brand backgrounds.
     *
     * @since 0.0.1
     */
    val inkOnBrand: Color,
    //endregion 3. Ink (Typography and icons)
    //region 4. Outline (Borders and dividers)
    /**
     * Subtle dividers and separators.
     *
     * @since 0.0.1
     */
    val outlineLow: Color,
    /**
     * Stronger borders for active inputs or focus states.
     *
     * @since 0.0.1
     */
    val outlineHigh: Color,
    //endregion 4. Outline (Borders and dividers)
    //region 5. Status (Functional feedback)
    /**
     * Success states and confirmations.
     *
     * @since 0.0.1
     */
    val success: Color,
    /**
     * Error messages and destructive actions.
     *
     * @since 0.0.1
     */
    val error: Color,
    /**
     * Warnings and non-blocking alerts.
     *
     * @since 0.0.1
     */
    val warning: Color,
    //endregion 5. Status (Functional feedback)
    //region 6. Interaction (Special states)
    /**
     * Background/text for inactive elements.
     *
     * @since 0.0.1
     */
    val disabled: Color,
    /**
     * Darkening overlay for modal backgrounds.
     *
     * @since 0.0.1
     */
    val scrim: Color,
    //endregion 6. Interaction (Special states)
)
