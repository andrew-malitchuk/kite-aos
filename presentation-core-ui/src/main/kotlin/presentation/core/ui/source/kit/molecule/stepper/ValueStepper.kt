package presentation.core.ui.source.kit.molecule.stepper

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.shape.SquircleShape

@Composable
public fun ValueStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    range: IntRange = 0..360,
    step: Int = 1,
    label: String? = null,
    suffix: String = ""
) {
    var textValue by remember(value) { mutableStateOf(value.toString()) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeXS)) {
        if (label != null) {
            Text(
                text = label,
                style = Theme.typography.label,
                color = Theme.color.surface
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = Theme.color.surfaceVariant.copy(alpha = 0.5f),
                    shape = SquircleShape(Theme.size.sizeL)
                )
                .padding(4.dp)
        ) {
            StepperButton(
                text = "-",
                enabled = value > range.first,
                onAction = {
                    val newValue = value - step
                    if (newValue >= range.first) onValueChange(newValue)
                }
            )

            Box(
                modifier = Modifier.width(48.dp),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = textValue,
                    onValueChange = { newText: String ->
                        if (newText.all { it.isDigit() } || newText.isEmpty()) {
                            textValue = newText
                            newText.toIntOrNull()?.let { if (it in range) onValueChange(it) }
                        }
                    },
                    textStyle = Theme.typography.body.copy(
                        textAlign = TextAlign.Center,
                        color = Theme.color.inkMain
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    decorationBox = { innerTextField: @Composable () -> Unit ->
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            innerTextField()
                            if (suffix.isNotEmpty()) {
                                Text(
                                    text = suffix,
                                    modifier = Modifier.padding(start = 2.dp),
                                    style = Theme.typography.body,
                                    color = Theme.color.inkMain
                                )
                            }
                        }
                    }
                )
            }

            StepperButton(
                text = "+",
                enabled = value < range.last,
                onAction = {
                    val newValue = value + step
                    if (newValue <= range.last) onValueChange(newValue)
                }
            )
        }
    }
}

@Composable
private fun StepperButton(
    text: String,
    onAction: () -> Unit,
    enabled: Boolean
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    val currentOnAction by rememberUpdatedState(onAction)

    LaunchedEffect(isPressed) {
        if (isPressed) {
            var currentDelay = 300L
            while (isPressed) {
                if (enabled) {
                    currentOnAction()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                delay(currentDelay)
                if (currentDelay > 60L) currentDelay -= 40L
            }
        }
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(SquircleShape(Theme.size.sizeM))
            .background(if (isPressed) Theme.color.inkMain.copy(alpha = 0.1f) else Color.Transparent)
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
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = Theme.typography.body,
            color = if (enabled) Theme.color.inkMain else Theme.color.inkMain.copy(alpha = 0.3f)
        )
    }
}
