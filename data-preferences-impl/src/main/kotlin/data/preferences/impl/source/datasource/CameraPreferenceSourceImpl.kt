package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.CameraPreferenceSource
import data.preferences.api.source.resource.CameraPreference
import data.preferences.impl.core.mapper.CameraProtobufPreferenceMapper
import data.preferences.impl.proto.CameraDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.CameraPreferenceStorage
import org.koin.core.annotation.Single

/**
 * Implementation of [CameraPreferenceSource] backed by Proto DataStore.
 *
 * Delegates persistence to [CameraPreferenceStorage] and uses [CameraProtobufPreferenceMapper]
 * to convert between the Protobuf model and the [CameraPreference] resource.
 *
 * @param cameraPreferenceDao the [CameraPreferenceStorage] providing DataStore access.
 * @see CameraPreferenceSource
 * @see CameraPreferenceStorage
 * @see CameraProtobufPreferenceMapper
 * @see BasePreferenceSourceImpl
 * @since 1.4.0
 */
@Single(binds = [CameraPreferenceSource::class])
internal class CameraPreferenceSourceImpl(
    cameraPreferenceDao: CameraPreferenceStorage,
) : BasePreferenceSourceImpl<CameraDataProto.CameraProtoModel, CameraPreference>(
    storage = cameraPreferenceDao,
    mapper = CameraProtobufPreferenceMapper,
),
    CameraPreferenceSource
