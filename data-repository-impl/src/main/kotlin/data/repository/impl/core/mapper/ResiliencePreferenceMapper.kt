package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.ResiliencePreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.ResilienceModel

/**
 * Mapper for converting between [ResilienceModel] and [ResiliencePreference].
 *
 * A straight field-for-field pass-through: the storage-side encoding tricks (inverted booleans,
 * offset hour) are already undone by
 * [data.preferences.impl.core.mapper.ResilienceProtobufPreferenceMapper], so both sides here speak
 * plain nullable values with the same meaning.
 *
 * @see ResilienceModel
 * @see ResiliencePreference
 * @see ModelResourceMapper
 * @since 2.2.0
 */
internal object ResiliencePreferenceMapper : ModelResourceMapper<ResilienceModel, ResiliencePreference> {

    override val toResource: Mapper<ResilienceModel, ResiliencePreference> =
        Mapper { model ->
            ResiliencePreference(
                crashRelaunchEnabled = model.crashRelaunchEnabled,
                scheduledReloadEnabled = model.scheduledReloadEnabled,
                scheduledReloadHour = model.scheduledReloadHour,
                memoryRecoveryEnabled = model.memoryRecoveryEnabled,
                connectionMonitorEnabled = model.connectionMonitorEnabled,
                wifiLockEnabled = model.wifiLockEnabled,
            )
        }

    override val toModel: Mapper<ResiliencePreference, ResilienceModel> =
        Mapper { preference ->
            ResilienceModel(
                crashRelaunchEnabled = preference.crashRelaunchEnabled,
                scheduledReloadEnabled = preference.scheduledReloadEnabled,
                scheduledReloadHour = preference.scheduledReloadHour,
                memoryRecoveryEnabled = preference.memoryRecoveryEnabled,
                connectionMonitorEnabled = preference.connectionMonitorEnabled,
                wifiLockEnabled = preference.wifiLockEnabled,
            )
        }
}
