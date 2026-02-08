package presentation.core.styling.core

import androidx.compose.ui.unit.TextUnit

/**
 * Defines the line heights for each typography style to maintain vertical rhythm.
 */
public data class ThemeLineHeight(
    /** Line height for display text */
    val display: TextUnit,
    /** Line height for titles */
    val title: TextUnit,
    /** Line height for labels */
    val label: TextUnit,
    /** Line height for body text */
    val body: TextUnit,
    /** Line height for captions */
    val caption: TextUnit,
    /** Line height for buttons and interactive elements */
    val action: TextUnit,
)