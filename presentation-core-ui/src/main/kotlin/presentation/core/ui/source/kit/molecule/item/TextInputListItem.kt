package presentation.core.ui.source.kit.molecule.item

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.SquircleCard
import presentation.core.ui.source.kit.atom.container.IconContainer
import presentation.core.ui.source.kit.atom.icon.IcClose24
import presentation.core.ui.source.kit.atom.icon.IcOutline3
import presentation.core.ui.source.kit.atom.shape.SquircleShape

@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun TextInputListItem(
    modifier: Modifier = Modifier,
    initialText: String = "",
    onTextChanged: (String) -> Unit,
    placeholder: String,
    textStyle: TextStyle = Theme.typography.body,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconForegroundColor: Color,
    validationRegex: Regex? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done,
        keyboardType = KeyboardType.Text,
        autoCorrect = false,
        capitalization = KeyboardCapitalization.None
    ),
    enabled: Boolean = true
) {
    val handleColor = Theme.color.brand
    val backgroundColor = Theme.color.brand.copy(alpha = 0.4f)


    var text by remember(initialText) { mutableStateOf(initialText) }
    var isError by remember(text) {
        mutableStateOf(validationRegex?.let { !it.matches(text) && text.isNotEmpty() } ?: false)
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val isImeVisible = WindowInsets.isImeVisible
    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
        }
    }

    val borderColor by animateColorAsState(
        targetValue = if (isError && enabled) Theme.color.error else Color.Transparent,
        label = "BorderColor"
    )

    BaseListItem(
        modifier = modifier.border(Theme.size.sizeXXS, borderColor, SquircleShape(Theme.size.sizeXL)),
        icon = icon,
        iconBackgroundColor = iconBackgroundColor,
        iconForegroundColor = iconForegroundColor,
        enabled = enabled,
        content = {
            Box(modifier = Modifier.weight(1f)) {
                val customTextSelectionColors = remember {
                    TextSelectionColors(
                        handleColor = handleColor,
                        backgroundColor = backgroundColor
                    )
                }

                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    BasicTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            onTextChanged(it)
                        },
                        enabled = enabled,
                        textStyle = textStyle.copy(color = Theme.color.inkMain),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        cursorBrush = SolidColor(Theme.color.brand),
                        keyboardOptions = keyboardOptions,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            },
                            onNext = {
                                // Keep focus but move to next
                            },
                            onSearch = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        )
                    )
                }
                if (text.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = textStyle,
                        color = Theme.color.inkMain.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        trailingContent = {
            if (text.isNotEmpty()) {
                IconButton(
                    onClick = {
                        text = ""
                        onTextChanged("")
                    },
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = IcClose24,
                        contentDescription = "Clear input",
                        tint = Theme.color.inkMain
                    )
                }
            }
        }
    )
}


@Preview
@Composable
private fun TextInputListItemPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing.sizeM),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.sizeM)
        ) {
            var text1 by remember { mutableStateOf("invalid") } // Text that will trigger error
            var text2 by remember { mutableStateOf("") }

            TextInputListItem(
                modifier = Modifier.fillMaxWidth(),
                initialText = text1,
                onTextChanged = { text1 = it },
                placeholder = "Enter 'valid' text",
                icon = IcOutline3, // Using LoginIcon for the first preview
                iconBackgroundColor = Theme.color.brand,
                iconForegroundColor = Theme.color.inkMain,
                validationRegex = Regex("^valid$") // Regex to demonstrate error
            )

            Spacer(modifier = Modifier.size(Theme.spacing.sizeM))

            TextInputListItem(
                modifier = Modifier.fillMaxWidth(),
                initialText = text2,
                onTextChanged = { text2 = it },
                placeholder = "White list",
                icon = IcOutline3, // Using a standard Material icon
                iconBackgroundColor = Theme.color.brand, // Different color for distinction
                iconForegroundColor = Theme.color.surface,
            )
        }
    }
}
