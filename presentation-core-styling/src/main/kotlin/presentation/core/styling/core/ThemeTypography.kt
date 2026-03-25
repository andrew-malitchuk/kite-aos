package presentation.core.styling.core

import androidx.compose.ui.text.TextStyle

/**
 * Combines font sizes, line heights, and other text properties into reusable styles.
 */
public data class ThemeTypography(
    /** Prominent display style for hero sections */
    val display: TextStyle,
    /** Main title style for screen headers */
    val title: TextStyle,
    /** Style for sub-headers and labels */
    val label: TextStyle,
    /** Standard style for long-form content */
    val body: TextStyle,
    /** High-emphasis version of the body style */
    val bodyEmphasis: TextStyle,
    /** Style for small secondary text */
    val caption: TextStyle,
    /** Specialized style for buttons and actions */
    val action: TextStyle,
)
