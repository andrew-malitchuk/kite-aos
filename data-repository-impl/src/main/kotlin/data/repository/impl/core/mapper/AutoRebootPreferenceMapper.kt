package data.repository.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.AutoRebootPreference
import data.repository.impl.core.mapper.base.ModelResourceMapper
import domain.core.source.model.AutoRebootModel

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
