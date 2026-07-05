package data.preferences.impl.core.mapper

import common.core.core.mapper.Mapper
import data.preferences.api.source.resource.WebEnginePreference
import data.preferences.impl.core.mapper.base.ProtobufPreferenceMapper
import data.preferences.impl.proto.WebEngineDataProto

/**
 * Bidirectional mapper between [WebEngineDataProto.WebEngineProtoModel] and [WebEnginePreference].
 *
 * @see WebEnginePreference
 * @see WebEngineDataProto.WebEngineProtoModel
 * @see ProtobufPreferenceMapper
 * @since 0.0.4
 */
internal object WebEngineProtobufPreferenceMapper :
    ProtobufPreferenceMapper<WebEngineDataProto.WebEngineProtoModel, WebEnginePreference> {

    /** Converts a [WebEnginePreference] to its Protobuf representation for storage. */
    override val toProtobuf: Mapper<WebEnginePreference, WebEngineDataProto.WebEngineProtoModel> =
        Mapper { input ->
            WebEngineDataProto.WebEngineProtoModel.newBuilder()
                .setEngine(input.engine)
                .build()
        }

    /** Converts a Protobuf [WebEngineDataProto.WebEngineProtoModel] back to a [WebEnginePreference]. */
    override val toPreference: Mapper<WebEngineDataProto.WebEngineProtoModel, WebEnginePreference> =
        Mapper { input ->
            WebEnginePreference(
                engine = input.engine,
            )
        }
}
