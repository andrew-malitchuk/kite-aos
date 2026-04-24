package presentation.core.styling.source.attribute

import androidx.compose.ui.unit.sp
import presentation.core.styling.core.ThemeLineHeight

/**
 * Default internal line height configuration for the application's design system.
 *
 * Each value pairs with the corresponding [attributeFontSize] token to establish
 * consistent vertical rhythm. Line heights are specified in scaled pixels (sp)
 * so they scale proportionally with font size accessibility settings.
 *
 * @see ThemeLineHeight
 * @see attributeFontSize
 * @since 0.0.1
 */
internal val attributeLineHeight: ThemeLineHeight =
    ThemeLineHeight(
        display = 48.sp, // 48sp - 1.2x ratio with 40sp display font
        title = 32.sp, // 32sp - 1.33x ratio with 24sp title font
        label = 20.sp, // 20sp - 1.43x ratio with 14sp label font
        body = 24.sp, // 24sp - 1.5x ratio with 16sp body font
        caption = 16.sp, // 16sp - 1.33x ratio with 12sp caption font
        action = 20.sp, // 20sp - 1.43x ratio with 14sp action font
    )
