package presentation.feature.main.source.screensaver

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.documentfile.provider.DocumentFile
import coil.compose.AsyncImage
import coil.request.ImageRequest
import domain.core.source.model.ScreensaverSource
import kotlinx.coroutines.delay
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.molecule.ScreensaverClock

/**
 * A full-screen overlay that appears when the screensaver is activated.
 *
 * Fades in over a black background when [isVisible] is `true`. If [source] is
 * [ScreensaverSource.LOCAL_FOLDER] and [folderUri] is non-null, a [SlideshowBackground] cycles
 * through local images at [slideIntervalSeconds] intervals. An optional clock widget is rendered
 * on top when [showClock] is `true`.
 *
 * @param isVisible Whether the screensaver overlay should be shown.
 * @param source The background source type (solid black or local image folder slideshow).
 * @param folderUri SAF tree URI of the image folder used for slideshow mode. Ignored when
 *   [source] is not [ScreensaverSource.LOCAL_FOLDER].
 * @param slideIntervalSeconds Seconds between image transitions in slideshow mode.
 * @param showClock Whether to display a [ScreensaverClock] on the overlay.
 * @see SlideshowBackground
 * @see ScreensaverClock
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 */
@Composable
internal fun ScreensaverOverlay(
    isVisible: Boolean,
    source: ScreensaverSource,
    folderUri: String?,
    slideIntervalSeconds: Long,
    showClock: Boolean,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(1000)),
        exit = fadeOut(tween(500)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            if (source == ScreensaverSource.LOCAL_FOLDER && !folderUri.isNullOrEmpty()) {
                SlideshowBackground(
                    folderUri = folderUri,
                    slideIntervalSeconds = slideIntervalSeconds,
                )
            }

            if (showClock) {
                ScreensaverClock(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(Theme.spacing.sizeXL),
                )
            }
        }
    }
}

/**
 * Cycles through images in a local folder as a fullscreen slideshow background.
 *
 * Reads image files from the SAF tree at [folderUri] on first composition. If the folder
 * contains more than one image, an infinite loop advances [currentIndex] every
 * [slideIntervalSeconds] seconds. Switching [folderUri] resets the image list.
 *
 * @param folderUri SAF tree URI string of the image folder. Must be a valid persisted URI.
 * @param slideIntervalSeconds Seconds to display each image before transitioning to the next.
 */
@Composable
private fun SlideshowBackground(
    folderUri: String,
    slideIntervalSeconds: Long,
) {
    val context = LocalContext.current
    var imageUris by remember(folderUri) { mutableStateOf<List<Uri>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(folderUri) {
        val treeUri = Uri.parse(folderUri)
        val dir = DocumentFile.fromTreeUri(context, treeUri)
        imageUris = dir?.listFiles()
            ?.filter { it.isFile && it.type?.startsWith("image/") == true }
            ?.map { it.uri } ?: emptyList()
    }

    LaunchedEffect(imageUris, slideIntervalSeconds) {
        if (imageUris.size <= 1) return@LaunchedEffect
        while (true) {
            delay(slideIntervalSeconds * 1000L)
            currentIndex = (currentIndex + 1) % imageUris.size
        }
    }

    val currentUri = imageUris.getOrNull(currentIndex)
    if (currentUri != null) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(currentUri)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
