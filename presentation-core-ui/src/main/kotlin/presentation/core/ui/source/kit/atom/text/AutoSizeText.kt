package presentation.core.ui.source.kit.atom.text

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/**
 * A resizable [Text] element that automatically decreases font size to fit its container.
 *
 * This composable contains all the default behavior and parameters of [Text] with additional
 * auto-sizing logic. Minimization is applied based on height overflow. To achieve auto-sizing,
 * set [maxLines] or ensure the container has a fixed size; otherwise the original [fontSize]
 * will be used as-is.
 *
 * The auto-sizing algorithm uses a multi-phase approach ([SizeDecreasingStage]):
 * 1. **Offense** -- aggressively halves the font size until it fits.
 * 2. **Defence** -- increases by 20% to recover from overshrinking.
 * 3. **Diplomacy** -- fine-tunes by reducing 5% at a time.
 * 4. **Peace** -- final size is determined and text is drawn.
 *
 * @param text the text string to display.
 * @param modifier Modifier to be applied to the [Text].
 * @param minFontSize the minimum allowed font size. If reached and text still overflows,
 *        the [overflow] parameter controls truncation behavior.
 * @param color the text color.
 * @param fontSize the initial (maximum) font size.
 * @param fontStyle the font style (e.g., italic).
 * @param fontWeight the font weight (e.g., bold).
 * @param fontFamily the font family to use.
 * @param letterSpacing the letter spacing.
 * @param textDecoration the text decoration (e.g., underline).
 * @param textAlign the text alignment.
 * @param lineHeight the initial line height.
 * @param softWrap whether soft wrapping is enabled.
 * @param maxLines the maximum number of lines.
 * @param minLines the minimum number of lines.
 * @param onTextLayout callback invoked with the [TextLayoutResult] after layout.
 * @param style the base [TextStyle] to apply.
 * @param overflow the overflow behavior when text does not fit.
 * @param keepLineHeight if `true`, line height remains constant as font size decreases.
 *        If `false` (default), line height scales proportionally with font size.
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
 */
@Composable
public fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    minFontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    overflow: TextOverflow = TextOverflow.Clip,
    keepLineHeight: Boolean = false,
) {
    val defaultFontSize =
        coerceTextUnit(
            expected = fontSize,
            default = style.fontSize,
        )
    val defaultLineHeight =
        coerceTextUnit(
            expected = lineHeight,
            default = style.lineHeight,
        )

    val ratio = defaultFontSize.value / defaultLineHeight.value

    var overriddenMetrics by remember(key1 = text) {
        mutableStateOf(
            InnerMetrics(
                fontSize = defaultFontSize,
                lineHeight = defaultLineHeight,
            ),
        )
    }
    var textReadyToDraw by remember(key1 = text) {
        mutableStateOf(false)
    }
    var decreasingStage: SizeDecreasingStage? by remember(key1 = text) {
        mutableStateOf(null)
    }

    Text(
        modifier =
        modifier.drawWithContent {
            if (textReadyToDraw) {
                drawContent()
            }
        },
        text = text,
        color = color,
        textAlign = textAlign,
        fontSize = overriddenMetrics.fontSize,
        fontFamily = fontFamily,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        lineHeight = overriddenMetrics.lineHeight,
        style = style,
        maxLines = maxLines,
        minLines = minLines,
        softWrap = softWrap,
        overflow = if (textReadyToDraw) overflow else TextOverflow.Clip,
        onTextLayout = { result ->
            if (textReadyToDraw) {
                onTextLayout(result)
                return@Text
            }
            if (minFontSize == TextUnit.Unspecified || overriddenMetrics.fontSize > minFontSize) {
                if (result.didOverflowHeight.not() && decreasingStage == null) {
                    textReadyToDraw = true
                    onTextLayout(result)
                    return@Text
                }

                decreasingStage = decreasingStage.next(result.didOverflowHeight)
                if (decreasingStage == SizeDecreasingStage.Peace) {
                    textReadyToDraw = true
                } else {
                    // Non-null assertion is safe: decreasingStage is always assigned
                    // a non-Peace value on the else branch above
                    val correctedFontSize =
                        overriddenMetrics.fontSize.times(decreasingStage!!.value)
                    val correctedLineHeight =
                        if (keepLineHeight) lineHeight else correctedFontSize.div(ratio)
                    overriddenMetrics =
                        overriddenMetrics.copy(
                            fontSize = correctedFontSize,
                            lineHeight = correctedLineHeight,
                        )
                }
            } else {
                if (overriddenMetrics.fontSize <= minFontSize) {
                    val minLineHeight = if (keepLineHeight) lineHeight else minFontSize.div(ratio)
                    overriddenMetrics =
                        InnerMetrics(
                            fontSize = minFontSize,
                            lineHeight = minLineHeight,
                        )
                    textReadyToDraw = true
                }
            }
            onTextLayout(result)
        },
    )
}

/** Default font size decrease multiplier (unused, retained for potential future use). */
internal const val SIZE_DECREASER = 0.9f

/**
 * Multi-phase strategy for iteratively finding the optimal font size that fits the container.
 *
 * Each stage applies a different multiplier to the current font size:
 * - [Offense]: aggressive 50% reduction to quickly find a rough fit.
 * - [Defence]: 120% increase to recover from overshrinking.
 * - [Diplomacy]: gentle 95% reduction for fine-tuning.
 * - [Peace]: sizing complete, text is ready to draw.
 *
 * @param value the multiplier applied to the current font size at this stage.
 */
internal enum class SizeDecreasingStage(val value: Float) {
    Offense(0.5f),
    Defence(1.2f),
    Diplomacy(0.95f),
    Peace(Float.NaN),
}

/**
 * Determines the next [SizeDecreasingStage] based on the current stage and whether the text
 * overflowed its container height.
 *
 * @param didOverflowHeight whether the text exceeded the available height after the last layout pass.
 * @return the next stage in the sizing algorithm.
 */
internal fun SizeDecreasingStage?.next(didOverflowHeight: Boolean): SizeDecreasingStage {
    return when {
        this == null -> SizeDecreasingStage.Offense
        this == SizeDecreasingStage.Offense && didOverflowHeight -> SizeDecreasingStage.Offense
        this == SizeDecreasingStage.Offense -> SizeDecreasingStage.Defence
        this == SizeDecreasingStage.Defence && didOverflowHeight.not() -> SizeDecreasingStage.Defence
        this == SizeDecreasingStage.Defence -> SizeDecreasingStage.Diplomacy
        this == SizeDecreasingStage.Diplomacy && didOverflowHeight -> SizeDecreasingStage.Diplomacy
        else -> SizeDecreasingStage.Peace
    }
}

/**
 * Internal holder for the current font size and line height during the auto-sizing algorithm.
 *
 * @param fontSize the current font size being tested.
 * @param lineHeight the current line height corresponding to [fontSize].
 */
internal data class InnerMetrics(
    val fontSize: TextUnit,
    val lineHeight: TextUnit,
)

/**
 * Returns [expected] if it is specified (not [TextUnit.Unspecified]), otherwise falls back to [default].
 *
 * @param expected the preferred text unit value.
 * @param default the fallback text unit value when [expected] is unspecified.
 * @return the resolved [TextUnit].
 */
internal fun coerceTextUnit(expected: TextUnit, default: TextUnit) =
    if (expected != TextUnit.Unspecified) expected else default
