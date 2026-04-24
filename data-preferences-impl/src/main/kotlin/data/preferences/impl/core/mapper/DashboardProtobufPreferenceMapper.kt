package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.DashboardPreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.DashboardDataProto

/**
 * Bidirectional mapper between [DashboardDataProto.DashboardProtoModel] and [DashboardPreference].
 *
 * Handles conversion of dashboard URL configuration between the Protobuf serialization format
 * used by Proto DataStore and the preference resource exposed to the data layer API.
 *
 * Null URL values are converted to empty strings when writing to Protobuf, since Protobuf
 * does not natively support nullable string fields.
 *
 * @see DashboardPreference
 * @see DashboardDataProto.DashboardProtoModel
 * @see ProtobufPreferenceMapper
 * @see data.preferences.impl.source.datasource.DashboardPreferenceSourceImpl
 * @since 0.0.1
 */
internal object DashboardProtobufPreferenceMapper :
    ProtobufPreferenceMapper<DashboardDataProto.DashboardProtoModel, DashboardPreference> {

    /** Converts a [DashboardPreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<DashboardPreference, DashboardDataProto.DashboardProtoModel> =
        Mapper { input ->
            DashboardDataProto.DashboardProtoModel.newBuilder()
                // Default to empty string for null URLs since Protobuf does not support nullable strings
                .setDashboardUrl(input.dashboardUrl ?: "")
                .setWhitelistUrl(input.whitelistUrl ?: "")
                .build()
        }

    /** Converts a Protobuf [DashboardDataProto.DashboardProtoModel] back to a [DashboardPreference]. */
    override val toPreference: Mapper<DashboardDataProto.DashboardProtoModel, DashboardPreference> =
        Mapper { input ->
            DashboardPreference(
                dashboardUrl = input.dashboardUrl,
                whitelistUrl = input.whitelistUrl,
            )
        }
}
