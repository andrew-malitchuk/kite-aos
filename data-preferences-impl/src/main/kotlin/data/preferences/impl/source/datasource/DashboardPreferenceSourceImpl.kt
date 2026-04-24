package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.DashboardPreferenceSource
import data.preferences.api.source.resource.DashboardPreference
import data.preferences.impl.core.mapper.DashboardProtobufPreferenceMapper
import data.preferences.impl.proto.DashboardDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.DashboardPreferenceStorage
import org.koin.core.annotation.Single

/**
 * Implementation of [DashboardPreferenceSource] backed by Proto DataStore.
 *
 * Delegates persistence to [DashboardPreferenceStorage] and uses
 * [DashboardProtobufPreferenceMapper] for converting between the Protobuf model and the
 * [DashboardPreference] resource.
 *
 * @param storage the [DashboardPreferenceStorage] providing DataStore access.
 * @see DashboardPreferenceSource
 * @see DashboardPreferenceStorage
 * @see DashboardProtobufPreferenceMapper
 * @see BasePreferenceSourceImpl
 * @since 0.0.1
 */
@Single(binds = [DashboardPreferenceSource::class])
internal class DashboardPreferenceSourceImpl(
    storage: DashboardPreferenceStorage,
) : BasePreferenceSourceImpl<DashboardDataProto.DashboardProtoModel, DashboardPreference>(
    storage = storage,
    mapper = DashboardProtobufPreferenceMapper,
),
    DashboardPreferenceSource
