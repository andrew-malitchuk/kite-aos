package data.preferences.api.source.datasource

import data.preferences.api.source.datasource.base.PreferenceSource
import data.preferences.api.source.resource.MqttPreference

/**
 * Preference data source for MQTT broker connection settings.
 *
 * Manages persistence and observation of the MQTT configuration used for telemetry
 * reporting and Home Assistant integration.
 *
 * @see MqttPreference
 * @see PreferenceSource
 *
 * @since 0.0.1
 */
public interface MqttPreferenceSource : PreferenceSource<MqttPreference>
