package presentation.core.ui.source.kit.atom.button.text.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.Theme
import presentation.core.ui.core.ext.has
import presentation.core.ui.source.kit.atom.button.core.ButtonColor
import presentation.core.ui.source.kit.atom.button.core.ButtonInteractionState

public class TextButtonColor : ButtonColor {
    @Composable
    override fun borderColor(
        interactionState: Int,
        enabled: Boolean,
        loading: Boolean,
    ): State<Color> = rememberUpdatedState(Color.Transparent)

    @Composable
    override fun foregroundColor(
        interactionState: Int,
        enabled: Boolean,
        loading: Boolean,
    ): State<Color> =
        rememberUpdatedState(
            when {
                // Disabled text color
                !enabled -> Theme.color.disabled
                // Visual feedback for the text itself when pressed
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.brandVariant
                // Primary brand color (Lilac) for the text
                else -> Theme.color.brand
            },
        )

    @Composable
    override fun backgroundColor(
        interactionState: Int,
        enabled: Boolean,
        loading: Boolean,
    ): State<Color> =
        rememberUpdatedState(
            when {
                // Background is always transparent unless pressed
                !enabled -> Color.Transparent
                // Subtle overlay to indicate the click
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.outlineLow
                else -> Color.Transparent
            },
        )
}