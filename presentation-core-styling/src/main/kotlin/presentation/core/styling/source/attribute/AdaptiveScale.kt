package presentation.core.styling.source.attribute

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import presentation.core.styling.core.ThemeFontSize
import presentation.core.styling.core.ThemeLineHeight
import presentation.core.styling.core.ThemeSize
import presentation.core.styling.core.ThemeSpacing
import presentation.core.styling.core.ThemeTypography

/**
 * Multiplier applied to spacing, sizing, and typography tokens when the UI runs in
 * the "10-foot" mode (Android TV / expanded window).
 *
 * Chosen to enlarge the design for across-the-room legibility without so much growth
 * that dense screens overflow. Applied centrally in
 * [presentation.core.styling.source.theme.AppTheme]; individual screens do not scale
 * tokens themselves.
 *
 * @since 1.2.0
 */
public const val TEN_FOOT_SCALE: Float = 1.3f

// Scales a TextUnit, leaving Unspecified values untouched (multiplying Unspecified throws).
private fun TextUnit.scaledBy(factor: Float): TextUnit = if (isSpecified) this * factor else this

/**
 * Returns a copy of these spacing tokens with every value multiplied by [factor].
 *
 * @since 1.2.0
 */
internal fun ThemeSpacing.scaledBy(factor: Float): ThemeSpacing =
    ThemeSpacing(
        sizeXXS = sizeXXS * factor,
        sizeXS = sizeXS * factor,
        sizeS = sizeS * factor,
        sizeM = sizeM * factor,
        sizeL = sizeL * factor,
        sizeXL = sizeXL * factor,
        size2XL = size2XL * factor,
        size3XL = size3XL * factor,
        size4XL = size4XL * factor,
        size5XL = size5XL * factor,
    )

/**
 * Returns a copy of these sizing tokens with every value multiplied by [factor].
 *
 * @since 1.2.0
 */
internal fun ThemeSize.scaledBy(factor: Float): ThemeSize =
    ThemeSize(
        sizeXXS = sizeXXS * factor,
        sizeXS = sizeXS * factor,
        sizeS = sizeS * factor,
        sizeM = sizeM * factor,
        sizeL = sizeL * factor,
        sizeXL = sizeXL * factor,
        size2XL = size2XL * factor,
        size3XL = size3XL * factor,
        size4XL = size4XL * factor,
        size5XL = size5XL * factor,
    )

/**
 * Returns a copy of these font-size tokens with every value multiplied by [factor].
 *
 * @since 1.2.0
 */
internal fun ThemeFontSize.scaledBy(factor: Float): ThemeFontSize =
    ThemeFontSize(
        display = display.scaledBy(factor),
        title = title.scaledBy(factor),
        label = label.scaledBy(factor),
        body = body.scaledBy(factor),
        caption = caption.scaledBy(factor),
        action = action.scaledBy(factor),
    )

/**
 * Returns a copy of these line-height tokens with every value multiplied by [factor].
 *
 * @since 1.2.0
 */
internal fun ThemeLineHeight.scaledBy(factor: Float): ThemeLineHeight =
    ThemeLineHeight(
        display = display.scaledBy(factor),
        title = title.scaledBy(factor),
        label = label.scaledBy(factor),
        body = body.scaledBy(factor),
        caption = caption.scaledBy(factor),
        action = action.scaledBy(factor),
    )

/**
 * Returns a copy of this typography with each style's font size and line height
 * multiplied by [factor]. Font family and weight are preserved.
 *
 * @since 1.2.0
 */
internal fun ThemeTypography.scaledBy(factor: Float): ThemeTypography {
    fun TextStyle.scaled(): TextStyle =
        copy(fontSize = fontSize.scaledBy(factor), lineHeight = lineHeight.scaledBy(factor))
    return ThemeTypography(
        display = display.scaled(),
        title = title.scaled(),
        label = label.scaled(),
        body = body.scaled(),
        bodyEmphasis = bodyEmphasis.scaled(),
        caption = caption.scaled(),
        action = action.scaled(),
    )
}
