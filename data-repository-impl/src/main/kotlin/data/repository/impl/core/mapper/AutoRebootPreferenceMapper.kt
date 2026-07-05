package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.AutoRebootPreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.AutoRebootModel

/**
 * Mapper for converting between [AutoRebootModel] and [AutoRebootPreference].
 *
 * Handles bidirectional mapping between the domain representation of the automatic reboot
 * schedule (enabled state, time, and interval) and the preference storage resource.
 *
 * @see AutoRebootModel
 * @see AutoRebootPreference
 * @see ModelResourceMapper
 * @since 0.0.1
 */
internal object AutoRebootPreferenceMapper :
    ModelResourceMapper<AutoRebootModel, AutoRebootPreference> {

    override val toModel: Mapper<AutoRebootPreference, AutoRebootModel> =
        Mapper { input ->
            AutoRebootModel(
                enabled = input.enabled,
                hour = input.hour,
                minute = input.minute,
                intervalDays = input.intervalDays,
            )
        }

    override val toResource: Mapper<AutoRebootModel, AutoRebootPreference> =
        Mapper { input ->
            AutoRebootPreference(
                enabled = input.enabled,
                hour = input.hour,
                minute = input.minute,
                intervalDays = input.intervalDays,
            )
        }
}
