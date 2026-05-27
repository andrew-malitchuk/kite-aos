package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.AutoReturnPreferenceSource
import data.preferences.api.source.resource.AutoReturnPreference
import data.preferences.impl.core.mapper.AutoReturnProtobufPreferenceMapper
import data.preferences.impl.proto.AutoReturnDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.AutoReturnPreferenceStorage
import org.koin.core.annotation.Single

/**
 * Implementation of [AutoReturnPreferenceSource] backed by Proto DataStore.
 *
 * @param autoReturnPreferenceDao the [AutoReturnPreferenceStorage] providing DataStore access.
 * @see AutoReturnPreferenceSource
 * @see AutoReturnPreferenceStorage
 * @see AutoReturnProtobufPreferenceMapper
 * @since 0.0.4
 */
@Single(binds = [AutoReturnPreferenceSource::class])
internal class AutoReturnPreferenceSourceImpl(
    autoReturnPreferenceDao: AutoReturnPreferenceStorage,
) : BasePreferenceSourceImpl<AutoReturnDataProto.AutoReturnProtoModel, AutoReturnPreference>(
    storage = autoReturnPreferenceDao,
    mapper = AutoReturnProtobufPreferenceMapper,
),
    AutoReturnPreferenceSource
