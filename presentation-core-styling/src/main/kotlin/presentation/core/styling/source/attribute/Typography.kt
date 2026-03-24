package presentation.core.styling.source.attribute

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import presentation.core.styling.R
import presentation.core.styling.core.ThemeTypography

/**
 * Font family used primarily for headers and display text.
 */
internal val OutfitFontFamily: FontFamily =
    FontFamily(
        Font(R.font.outfit_regular, FontWeight.Normal),
        Font(R.font.outfit_semi_bold, FontWeight.SemiBold),
    )

/**
 * Font family used for body text, labels, and actions.
 */
internal val PlusJakartaFontFamily: FontFamily =
    FontFamily(
        Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
        Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
        Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    )

/**
 * Implementation of the typography system using semantic tokens.
 * Pairs Outfit for display/titles and Plus Jakarta Sans for content/actions.
 * Returns a [ThemeTypography] instance with pre-configured [TextStyle]s.
 */
@Composable
internal fun AttributeTypography(): ThemeTypography = remember {
    ThemeTypography(
        display =
        TextStyle(
            fontSize = attributeFontSize.display,
            lineHeight = attributeLineHeight.display,
            fontWeight = FontWeight.SemiBold,
            fontFamily = OutfitFontFamily,
        ),
        title =
        TextStyle(
            fontSize = attributeFontSize.title,
            lineHeight = attributeLineHeight.title,
            fontWeight = FontWeight.SemiBold,
            fontFamily = OutfitFontFamily,
        ),
        label =
        TextStyle(
            fontSize = attributeFontSize.label,
            lineHeight = attributeLineHeight.label,
            fontWeight = FontWeight.Medium,
            fontFamily = PlusJakartaFontFamily,
        ),
        body =
        TextStyle(
            fontSize = attributeFontSize.body,
            lineHeight = attributeLineHeight.body,
            fontWeight = FontWeight.Normal,
            fontFamily = PlusJakartaFontFamily,
        ),
        bodyEmphasis =
        TextStyle(
            fontSize = attributeFontSize.body,
            lineHeight = attributeLineHeight.body,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaFontFamily,
        ),
        caption =
        TextStyle(
            fontSize = attributeFontSize.caption,
            lineHeight = attributeLineHeight.caption,
            fontWeight = FontWeight.Normal,
            fontFamily = PlusJakartaFontFamily,
        ),
        action =
        TextStyle(
            fontSize = attributeFontSize.action,
            lineHeight = attributeLineHeight.action,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaFontFamily,
        ),
    )
}
