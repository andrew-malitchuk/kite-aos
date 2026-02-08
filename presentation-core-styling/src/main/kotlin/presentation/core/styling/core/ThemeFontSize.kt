package presentation.core.styling.core

import androidx.compose.ui.unit.TextUnit

/**
 * Defines the font sizes used throughout the application.
 * Uses functional naming to map sizes to specific UI roles.
 */
public data class ThemeFontSize(
    /** Size for large display or hero text */
    val display: TextUnit,
    /** Size for main screen titles */
    val title: TextUnit,
    /** Size for small headers, card titles, or input labels */
    val label: TextUnit,
    /** Size for standard readable body text */
    val body: TextUnit,
    /** Size for secondary information and captions */
    val caption: TextUnit,
    /** Size for interactive elements like buttons and links */
    val action: TextUnit,
)