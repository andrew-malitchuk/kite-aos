package presentation.core.ui.source.kit.atom.container

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
public fun MaxSideDetector(
    modifier: Modifier = Modifier,
    byHeight: @Composable () -> Unit,
    byWidth: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (maxWidth < maxHeight) {
            true -> byHeight()
            false -> byWidth()
        }
    }
}