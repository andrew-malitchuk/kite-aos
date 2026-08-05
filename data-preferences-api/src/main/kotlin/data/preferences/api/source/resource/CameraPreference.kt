package data.preferences.api.source.resource

import data.core.source.resource.Resource

/**
 * Preference resource representing the selected camera source for motion detection / streaming.
 *
 * Serialized via Proto DataStore with the following schema:
 * ```protobuf
 * message CameraProtoModel {
 *     string mode = 1;
 * }
 * ```
 *
 * @property mode the camera-source identifier (e.g., "auto", "front", "rear", "external").
 *
 * @see data.preferences.api.source.datasource.CameraPreferenceSource
 *
 * @since 1.4.0
 */
public data class CameraPreference(
    val mode: String,
) : Resource
