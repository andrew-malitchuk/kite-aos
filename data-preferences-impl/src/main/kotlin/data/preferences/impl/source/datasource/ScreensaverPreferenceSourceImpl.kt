package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.ScreensaverPreferenceSource
import data.preferences.api.source.resource.ScreensaverPreference
import data.preferences.impl.core.mapper.ScreensaverProtobufPreferenceMapper
import data.preferences.impl.proto.ScreensaverDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.ScreensaverPreferenceStorage
import org.koin.core.annotation.Single

/**
 * Implementation of [ScreensaverPreferenceSource] backed by Proto DataStore.
 *
 * Delegates persistence to [ScreensaverPreferenceStorage] and uses
 * [ScreensaverProtobufPreferenceMapper] for converting between the Protobuf model and the
 * [ScreensaverPreference] resource.
 *
 * @param storage the [ScreensaverPreferenceStorage] providing DataStore access.
 * @see ScreensaverPreferenceSource
 * @see ScreensaverPreferenceStorage
 * @see ScreensaverProtobufPreferenceMapper
 * @see BasePreferenceSourceImpl
 * @since 0.0.1
 */
@Single(binds = [ScreensaverPreferenceSource::class])
internal class ScreensaverPreferenceSourceImpl(
    storage: ScreensaverPreferenceStorage,
) : BasePreferenceSourceImpl<ScreensaverDataProto.ScreensaverProtoModel, ScreensaverPreference>(
    storage = storage,
    mapper = ScreensaverProtobufPreferenceMapper,
),
    ScreensaverPreferenceSource
