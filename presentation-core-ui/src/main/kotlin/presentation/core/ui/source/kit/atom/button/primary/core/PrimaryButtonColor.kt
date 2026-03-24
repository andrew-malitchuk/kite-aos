package presentation.core.ui.source.kit.atom.button.primary.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.Theme
import presentation.core.ui.core.ext.has
import presentation.core.ui.source.kit.atom.button.core.ButtonColor
import presentation.core.ui.source.kit.atom.button.core.ButtonInteractionState

public class PrimaryButtonColor : ButtonColor {
    @Composable
    override fun borderColor(interactionState: Int, enabled: Boolean, loading: Boolean): State<Color> =
        rememberUpdatedState(
            when {
                !enabled -> Color.Transparent
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.brand
                else -> Color.White // Default white border
            },
        )

    @Composable
    override fun foregroundColor(interactionState: Int, enabled: Boolean, loading: Boolean): State<Color> =
        rememberUpdatedState(
            when {
                !enabled -> Theme.color.inkSubtle
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.brand
                else -> Color.White // Default white foreground (text/icon)
            },
        )

    @Composable
    override fun backgroundColor(interactionState: Int, enabled: Boolean, loading: Boolean): State<Color> =
        rememberUpdatedState(Color.Transparent) // Always transparent background
}
