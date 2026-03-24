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
 * future releases:
 * 1. Support maxFontSize
 * 2. Add types: Maximisation, Minimization, Balanced
 */

/**
 * Resizeable Text element that contains all the default behavior and description
 * that you can find in [Text].
 * Minimization applied only by height. If you want to reach result,
 * you need to set [maxLines] or your container should have fixed size, otherwise [fontSize] will
 * be used.
 *
 * Extra parameters:
 * @param minFontSize - Allows you to specify minimum allowed font size for text. If [minFontSize]
 * reached but text still overflows, you can use default [overflow] param.
 * @param keepLineHeight - Allows you to control line height decreasing. If you want to make your
 * line height unchanged provide `true`. By default `false` means that line height will be
 * decreased by default aspect ratio from provided values.
 * Example:
 *  [fontSize] = 12.sp; [lineHeight] = 24.sp.
 *  In that case, if [keepLineHeight] = false, [lineHeight] will be always 2 times bigger
 *  than [fontSize].
 *  If [keepLineHeight] = true, [lineHeight] will have always 24.sp.
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

internal const val SIZE_DECREASER = 0.9f

internal enum class SizeDecreasingStage(val value: Float) {
    Offense(0.5f),
    Defence(1.2f),
    Diplomacy(0.95f),
    Peace(Float.NaN),
}

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

internal data class InnerMetrics(
    val fontSize: TextUnit,
    val lineHeight: TextUnit,
)

internal fun coerceTextUnit(expected: TextUnit, default: TextUnit) =
    if (expected != TextUnit.Unspecified) expected else default
