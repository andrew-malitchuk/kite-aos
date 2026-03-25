package presentation.core.ui.source.kit.atom.container

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
public fun MaxSideDetector(
    modifier: Modifier = Modifier,
    byHeight: @Composable () -> Unit,
    byWidth: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        when (maxWidth < maxHeight) {
            true -> byHeight()
            false -> byWidth()
        }
    }
}
