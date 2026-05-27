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
 *
 * Outfit is a geometric sans-serif typeface that provides strong visual hierarchy
 * for large-scale text elements.
 *
 * @see PlusJakartaFontFamily
 * @since 0.0.1
 */
internal val OutfitFontFamily: FontFamily =
    FontFamily(
        Font(R.font.outfit_regular, FontWeight.Normal),
        Font(R.font.outfit_semi_bold, FontWeight.SemiBold),
    )

/**
 * Font family used for body text, labels, and actions.
 *
 * Plus Jakarta Sans is a humanist sans-serif typeface optimized for readability
 * at smaller sizes, making it well-suited for content and interactive elements.
 *
 * @see OutfitFontFamily
 * @since 0.0.1
 */
internal val PlusJakartaFontFamily: FontFamily =
    FontFamily(
        Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
        Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
        Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    )

/**
 * Builds the application's typography system using semantic tokens.
 *
 * Pairs Outfit for display/titles and Plus Jakarta Sans for content/actions.
 * The resulting [ThemeTypography] is remembered across recompositions to avoid
 * redundant allocations.
 *
 * @return A [ThemeTypography] instance with pre-configured [TextStyle]s.
 * @see ThemeTypography
 * @see attributeFontSize
 * @see attributeLineHeight
 * @see OutfitFontFamily
 * @see PlusJakartaFontFamily
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
internal fun AttributeTypography(): ThemeTypography = remember {
    ThemeTypography(
        // Display: large hero text using Outfit SemiBold.
        display =
        TextStyle(
            fontSize = attributeFontSize.display,
            lineHeight = attributeLineHeight.display,
            fontWeight = FontWeight.SemiBold,
            fontFamily = OutfitFontFamily,
        ),
        // Title: screen headers using Outfit SemiBold.
        title =
        TextStyle(
            fontSize = attributeFontSize.title,
            lineHeight = attributeLineHeight.title,
            fontWeight = FontWeight.SemiBold,
            fontFamily = OutfitFontFamily,
        ),
        // Label: sub-headers and input labels using Plus Jakarta Sans Medium.
        label =
        TextStyle(
            fontSize = attributeFontSize.label,
            lineHeight = attributeLineHeight.label,
            fontWeight = FontWeight.Medium,
            fontFamily = PlusJakartaFontFamily,
        ),
        // Body: standard readable content using Plus Jakarta Sans Regular.
        body =
        TextStyle(
            fontSize = attributeFontSize.body,
            lineHeight = attributeLineHeight.body,
            fontWeight = FontWeight.Normal,
            fontFamily = PlusJakartaFontFamily,
        ),
        // Body Emphasis: bold variant for highlighted body content.
        bodyEmphasis =
        TextStyle(
            fontSize = attributeFontSize.body,
            lineHeight = attributeLineHeight.body,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaFontFamily,
        ),
        // Caption: small secondary text using Plus Jakarta Sans Regular.
        caption =
        TextStyle(
            fontSize = attributeFontSize.caption,
            lineHeight = attributeLineHeight.caption,
            fontWeight = FontWeight.Normal,
            fontFamily = PlusJakartaFontFamily,
        ),
        // Action: buttons and interactive elements using Plus Jakarta Sans Bold.
        action =
        TextStyle(
            fontSize = attributeFontSize.action,
            lineHeight = attributeLineHeight.action,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaFontFamily,
        ),
    )
}
