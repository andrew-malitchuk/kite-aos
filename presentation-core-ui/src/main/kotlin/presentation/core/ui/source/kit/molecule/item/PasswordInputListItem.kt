package presentation.core.ui.source.kit.molecule.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.SquircleCard
import presentation.core.ui.source.kit.atom.container.IconContainer
import presentation.core.ui.source.kit.atom.icon.IcClose24
import presentation.core.ui.source.kit.atom.icon.IcOutline3

@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun PasswordInputListItem(
    modifier: Modifier = Modifier,
    initialText: String = "",
    onTextChanged: (String) -> Unit,
    placeholder: String,
    textStyle: TextStyle = Theme.typography.body,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconForegroundColor: Color,
    enabled: Boolean = true
) {
    val handleColor = Theme.color.brand
    val backgroundColor = Theme.color.brand.copy(alpha = 0.4f)

    var text by remember(initialText) { mutableStateOf(initialText) }
    val focusManager = LocalFocusManager.current

    val isImeVisible = WindowInsets.isImeVisible
    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
        }
    }

    BaseListItem(
        modifier = modifier,
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
                        visualTransformation = PasswordVisualTransformation(),
                        cursorBrush = SolidColor(Theme.color.brand),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Password,
                            autoCorrect = false
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
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
private fun PasswordInputListItemPreview() {
    AppTheme {
        PasswordInputListItem(
            onTextChanged = {},
            placeholder = "Password",
            icon = IcOutline3,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.inkMain
        )
    }
}
