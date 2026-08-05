package presentation.core.ui.source.kit.molecule.stepper

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.shape.SquircleShape
import presentation.core.ui.source.kit.core.focus.tvFocusRing

/**
 * A numeric stepper control with decrement and increment buttons and an editable text field.
 *
 * The user can tap the "-" / "+" buttons to adjust the value by [step], or type a value directly
 * into the centre text field. Long-pressing a button triggers auto-repeat with an accelerating
 * rate (starts at 300 ms, decreases by 40 ms per tick down to a 60 ms floor) and haptic feedback.
 *
 * @param value The current integer value.
 * @param onValueChange Callback invoked when the value changes (via buttons or direct input).
 * @param modifier Modifier to be applied to the [Column].
 * @param range The allowed [IntRange] for the value. Defaults to `0..360`.
 * @param step The increment/decrement amount per button press. Defaults to `1`.
 * @param label An optional label displayed above the stepper row.
 * @param suffix An optional suffix string rendered after the numeric value (e.g., "s").
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun ValueStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    range: IntRange = 0..360,
    step: Int = 1,
    label: String? = null,
    suffix: String = "",
) {
    var textValue by remember(value) { mutableStateOf(value.toString()) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeXS)) {
        if (label != null) {
            Text(
                text = label,
                style = Theme.typography.label,
                color = Theme.color.surface,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
            Modifier
                .background(
                    color = Theme.color.surfaceVariant.copy(alpha = 0.5f),
                    shape = SquircleShape(Theme.size.sizeL),
                )
                .padding(4.dp),
        ) {
            // Decrement button — disabled when the value is already at the minimum
            StepperButton(
                text = "-",
                enabled = value > range.first,
                onAction = {
                    val newValue = value - step
                    if (newValue >= range.first) onValueChange(newValue)
                },
            )

            // 48 dp wide centre slot for the editable numeric text field
            Box(
                modifier = Modifier.width(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                BasicTextField(
                    value = textValue,
                    onValueChange = { newText: String ->
                        // Only accept digits or empty input; commit to the model only when within range
                        if (newText.all { it.isDigit() } || newText.isEmpty()) {
                            textValue = newText
                            newText.toIntOrNull()?.let { if (it in range) onValueChange(it) }
                        }
                    },
                    textStyle =
                    Theme.typography.body.copy(
                        textAlign = TextAlign.Center,
                        color = Theme.color.inkMain,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    decorationBox = { innerTextField: @Composable () -> Unit ->
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            innerTextField()
                            if (suffix.isNotEmpty()) {
                                Text(
                                    text = suffix,
                                    modifier = Modifier.padding(start = 2.dp),
                                    style = Theme.typography.body,
                                    color = Theme.color.inkMain,
                                )
                            }
                        }
                    },
                )
            }

            // Increment button — disabled when the value is already at the maximum
            StepperButton(
                text = "+",
                enabled = value < range.last,
                onAction = {
                    val newValue = value + step
                    if (newValue <= range.last) onValueChange(newValue)
                },
            )
        }
    }
}

@Composable
private fun StepperButton(text: String, onAction: () -> Unit, enabled: Boolean) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    val currentOnAction by rememberUpdatedState(onAction)
    // enabled must be rememberUpdatedState so the running LaunchedEffect sees the latest value.
    // Without this, the auto-repeat loop captures a stale enabled=true and keeps firing haptic
    // feedback even after the value reaches the range boundary and the button turns grey.
    val currentEnabled by rememberUpdatedState(enabled)

    // Shared with the TV focus ring so the D-pad-focused button is visibly highlighted.
    val interactionSource = remember { MutableInteractionSource() }
    val buttonShape = SquircleShape(Theme.size.sizeM)

    // Auto-repeat loop: fires the action while the button is held down (touch press OR a held
    // D-pad SELECT — both drive `isPressed`). The repeat interval starts at 300 ms and accelerates
    // by 40 ms per tick until it reaches the 60 ms floor, giving the user finer control over time.
    LaunchedEffect(isPressed) {
        if (isPressed) {
            var currentDelay = 300L // Initial delay between repeats in milliseconds
            while (isPressed) {
                if (currentEnabled) {
                    currentOnAction()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                delay(currentDelay)
                if (currentDelay > 60L) currentDelay -= 40L // Accelerate repeat rate
            }
        }
    }

    Box(
        modifier =
        Modifier
            .size(44.dp)
            .tvFocusRing(interactionSource = interactionSource, shape = buttonShape)
            .clip(buttonShape)
            .background(if (isPressed) Theme.color.inkMain.copy(alpha = 0.1f) else Color.Transparent)
            // TV: the stepper is driven by the remote. Holding SELECT presses the button, and the
            // KeyUp releases it — reusing the exact touch auto-repeat loop above, so a held key
            // accelerates through large ranges just like a long touch-press does.
            .onKeyEvent { event ->
                if (event.key != Key.DirectionCenter && event.key != Key.Enter && event.key != Key.NumPadEnter) {
                    return@onKeyEvent false
                }
                when (event.type) {
                    KeyEventType.KeyDown -> { isPressed = true; true }
                    KeyEventType.KeyUp -> { isPressed = false; true }
                    else -> false
                }
            }
            .focusable(enabled = enabled, interactionSource = interactionSource)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown()
                        isPressed = true
                        waitForUpOrCancellation()
                        isPressed = false
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = Theme.typography.body,
            color = if (enabled) Theme.color.inkMain else Theme.color.inkMain.copy(alpha = 0.3f),
        )
    }
}
