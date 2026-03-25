package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.DashboardPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.DashboardDataProto

internal object DashboardProtobufPreferenceMapper :
    ProtobufPreferenceMapper<DashboardDataProto.DashboardProtoModel, DashboardPreference> {
    override val toProtobuf: Mapper<DashboardPreference, DashboardDataProto.DashboardProtoModel> =
        Mapper { input ->
            DashboardDataProto.DashboardProtoModel.newBuilder()
                .setDashboardUrl(input.dashboardUrl ?: "")
                .setWhitelistUrl(input.whitelistUrl ?: "")
                .build()
        }

    override val toPreference: Mapper<DashboardDataProto.DashboardProtoModel, DashboardPreference> =
        Mapper { input ->
            DashboardPreference(
                dashboardUrl = input.dashboardUrl,
                whitelistUrl = input.whitelistUrl,
            )
        }
}
