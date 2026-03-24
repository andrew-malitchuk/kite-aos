package presentation.core.ui.source.kit.atom.button.icon.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.Theme
import presentation.core.ui.core.ext.has
import presentation.core.ui.source.kit.atom.button.core.ButtonColor
import presentation.core.ui.source.kit.atom.button.core.ButtonInteractionState

public class IconButtonColor(
    private val containerColor: Color,
    private val contentColor: Color,
    private val selectedContainerColor: Color,
    private val selectedContentColor: Color,
    private val disabledContainerColor: Color,
    private val disabledContentColor: Color,
) : ButtonColor {
    @Composable
    override fun borderColor(interactionState: Int, enabled: Boolean, loading: Boolean): State<Color> =
        rememberUpdatedState(Color.Transparent)

    @Composable
    override fun foregroundColor(interactionState: Int, enabled: Boolean, loading: Boolean): State<Color> =
        rememberUpdatedState(
            when {
                !enabled -> disabledContentColor
                interactionState has ButtonInteractionState.SELECTED -> selectedContentColor
                else -> contentColor
            },
        )

    @Composable
    override fun backgroundColor(interactionState: Int, enabled: Boolean, loading: Boolean): State<Color> =
        rememberUpdatedState(
            when {
                !enabled -> disabledContainerColor
                // Show subtle background on press
                interactionState has ButtonInteractionState.PRESSED -> Theme.color.brandVariant
                interactionState has ButtonInteractionState.SELECTED -> selectedContainerColor
                else -> containerColor
            },
        )
}
