package presentation.core.styling.source.attribute

import androidx.compose.ui.unit.sp
import presentation.core.styling.core.ThemeFontSize

/**
 * Default internal font size configuration for the application's design system.
 *
 * All values are specified in scaled pixels (sp) to support user accessibility
 * preferences for text scaling.
 *
 * @see ThemeFontSize
 * @see attributeLineHeight
 * @since 0.0.1
 */
internal val attributeFontSize: ThemeFontSize =
    ThemeFontSize(
        display = 40.sp, // 40sp - hero/display text
        title = 24.sp, // 24sp - screen titles
        label = 14.sp, // 14sp - card titles, input labels
        body = 16.sp, // 16sp - standard readable body text
        caption = 12.sp, // 12sp - secondary/metadata text
        action = 14.sp, // 14sp - buttons and interactive elements
    )
