package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing screensaver configuration.
 *
 * Controls the kiosk screensaver that activates after a period of inactivity,
 * displaying a slideshow of images with an optional clock overlay.
 *
 * Serialized via Proto DataStore with the following schema:
 * ```protobuf
 * message ScreensaverProtoModel {
 *     bool enabled = 1;
 *     int64 activation_delay = 2;
 *     int64 slide_interval = 3;
 *     bool show_clock = 4;
 *     int32 source = 5;
 *     string local_folder_uri = 6;
 * }
 * ```
 *
 * @property enabled whether the screensaver is active.
 * @property activationDelay the delay in seconds of inactivity before the screensaver starts.
 *   Defaults to 60 seconds when null.
 * @property slideInterval the time in seconds between image transitions in the slideshow.
 *   Defaults to 30 seconds when null.
 * @property showClock whether a clock overlay is displayed on the screensaver.
 * @property source the image source type identifier (e.g., 0 = built-in, 1 = local folder).
 * @property localFolderUri the content URI of the local folder to use for slideshow images,
 *   or `null` if no local folder has been selected.
 *
 * @see data.preferences.api.source.datasource.ScreensaverPreferenceSource
 *
 * @since 0.0.1
 */
public data class ScreensaverPreference(
    val enabled: Boolean? = null,
    val activationDelay: Long? = null,
    val slideInterval: Long? = null,
    val showClock: Boolean? = null,
    val source: Int? = null,
    val localFolderUri: String? = null,
) : Resource
