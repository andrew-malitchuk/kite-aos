package data.preferences.impl.source.datasource

import data.preferences.api.source.datasource.OnboardingPreferenceSource
import data.preferences.api.source.resource.OnboardingPreference
import data.preferences.impl.core.mapper.OnboardingProtobufPreferenceMapper
import data.preferences.impl.proto.OnboardingDataProto
import data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl
import data.preferences.impl.source.storage.OnboardingPreferenceStorage
import org.koin.core.annotation.Single

/**
 * Implementation of [OnboardingPreferenceSource] backed by Proto DataStore.
 *
 * Delegates persistence to [OnboardingPreferenceStorage] and uses
 * [OnboardingProtobufPreferenceMapper] for converting between the Protobuf model and the
 * [OnboardingPreference] resource.
 *
 * @param storage the [OnboardingPreferenceStorage] providing DataStore access.
 * @see OnboardingPreferenceSource
 * @see OnboardingPreferenceStorage
 * @see OnboardingProtobufPreferenceMapper
 * @see BasePreferenceSourceImpl
 * @since 0.0.1
 */
@Single(binds = [OnboardingPreferenceSource::class])
internal class OnboardingPreferenceSourceImpl(
    storage: OnboardingPreferenceStorage,
) : BasePreferenceSourceImpl<OnboardingDataProto.OnboardingProtoModel, OnboardingPreference>(
    storage = storage,
    mapper = OnboardingProtobufPreferenceMapper,
),
    OnboardingPreferenceSource
