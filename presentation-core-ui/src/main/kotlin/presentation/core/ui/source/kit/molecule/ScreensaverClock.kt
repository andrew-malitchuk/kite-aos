package presentation.core.ui.source.kit.molecule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import presentation.core.styling.core.Theme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Displays a live clock with the current time and date, refreshing every second.
 *
 * The time is formatted as `HH:mm` and the date as `EEEE, d MMMM` using the device's
 * default locale. A [LaunchedEffect] loop fires every 1000 ms to update both text values.
 * Designed for use in screensaver overlays where the clock should always be visible.
 *
 * @param modifier Modifier to be applied to the [Column].
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
 */
@Composable
public fun ScreensaverClock(modifier: Modifier = Modifier) {
    var timeText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            timeText = timeFormat.format(now)
            dateText = dateFormat.format(now)
            delay(1000L)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = timeText,
            color = Color.White,
            fontSize = 80.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = (-2).sp,
        )
        Text(
            text = dateText,
            color = Color.White.copy(alpha = 0.7f),
            style = Theme.typography.body,
        )
    }
}
