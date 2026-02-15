package presentation.core.ui.source.kit.atom.button.secondary.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.Theme
import presentation.core.ui.core.ext.has
import presentation.core.ui.source.kit.atom.button.core.ButtonColor
import presentation.core.ui.source.kit.atom.button.core.ButtonInteractionState

public class SecondaryButtonColor : ButtonColor {
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
                // Pressed state feedback (slightly different brand shade if needed)
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.brandVariant
                // Primary brand color for text on a subtle background
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
                // Subtle transparent background for disabled state
                !enabled -> Theme.color.surfaceVariant.copy(alpha = 0.5f)
                // Darker background when pressed
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.outlineLow
                // Tonal background (Soft Blue/Grey)
                else -> Theme.color.surfaceVariant
            },
        )
}