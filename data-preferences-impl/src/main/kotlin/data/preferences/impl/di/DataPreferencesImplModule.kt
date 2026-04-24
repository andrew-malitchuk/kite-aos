package data.preferences.impl.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import data.preferences.impl.core.configure.PreferenceConfigure
import data.preferences.impl.core.serializer.DashboardProtoSerializer
import data.preferences.impl.core.serializer.DockProtoSerializer
import data.preferences.impl.core.serializer.LanguageProtoSerializer
import data.preferences.impl.core.serializer.MoveDetectorProtoSerializer
import data.preferences.impl.core.serializer.MqttProtoSerializer
import data.preferences.impl.core.serializer.OnboardingProtoSerializer
import data.preferences.impl.core.serializer.ThemeProtoSerializer
import data.preferences.impl.proto.DashboardDataProto
import data.preferences.impl.proto.DockDataProto
import data.preferences.impl.proto.LanguagePreferenceProto
import data.preferences.impl.proto.MoveDetectorDataProto
import data.preferences.impl.proto.MqttDataProto
import data.preferences.impl.proto.OnboardingDataProto
import data.preferences.impl.proto.ThemeDataProto
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

/**
 * Koin dependency injection module for the `data-preferences-impl` layer.
 *
 * This module is responsible for providing all Proto DataStore instances used across the
 * preference storage layer. Each DataStore is created via [DataStoreFactory] with the
 * appropriate [androidx.datastore.core.Serializer] and persisted to a `.pb` file defined in
 * [PreferenceConfigure.Filename].
 *
 * The `@ComponentScan` annotation enables KSP-based automatic discovery of all `@Single`
 * and `@KoinViewModel` annotated classes within the `data.preferences.impl` package.
 *
 * @see PreferenceConfigure.Filename
 * @see data.preferences.impl.core.serializer
 * @since 0.0.1
 */
@Module
@ComponentScan("data.preferences.impl")
public class DataPreferencesImplModule {

    /**
     * Provides the [DataStore] instance for theme preferences.
     *
     * @param context the Android [Context] used to resolve the DataStore file location.
     * @return a [DataStore] backed by [ThemeProtoSerializer] and stored in [PreferenceConfigure.Filename.THEME].
     * @see ThemeProtoSerializer
     */
    @Single
    @Named("themeDataStore")
    public fun themeDataStore(context: Context): DataStore<ThemeDataProto.ThemeProtoModel> {
        return DataStoreFactory.create(
            serializer = ThemeProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.THEME) },
        )
    }

    /**
     * Provides the [DataStore] instance for onboarding preferences.
     *
     * @param context the Android [Context] used to resolve the DataStore file location.
     * @return a [DataStore] backed by [OnboardingProtoSerializer] and stored in [PreferenceConfigure.Filename.ONBOARDING].
     * @see OnboardingProtoSerializer
     */
    @Single
    @Named("onboardingDataStore")
    public fun onboardingDataStore(context: Context): DataStore<OnboardingDataProto.OnboardingProtoModel> {
        return DataStoreFactory.create(
            serializer = OnboardingProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.ONBOARDING) },
        )
    }

    /**
     * Provides the [DataStore] instance for dashboard preferences.
     *
     * @param context the Android [Context] used to resolve the DataStore file location.
     * @return a [DataStore] backed by [DashboardProtoSerializer] and stored in [PreferenceConfigure.Filename.DASHBOARD].
     * @see DashboardProtoSerializer
     */
    @Single
    @Named("dashboardDataStore")
    public fun dashboardDataStore(context: Context): DataStore<DashboardDataProto.DashboardProtoModel> {
        return DataStoreFactory.create(
            serializer = DashboardProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.DASHBOARD) },
        )
    }

    /**
     * Provides the [DataStore] instance for dock position preferences.
     *
     * @param context the Android [Context] used to resolve the DataStore file location.
     * @return a [DataStore] backed by [DockProtoSerializer] and stored in [PreferenceConfigure.Filename.DOCK].
     * @see DockProtoSerializer
     */
    @Single
    @Named("dockPositionDataStore")
    public fun dockPositionDataStore(context: Context): DataStore<DockDataProto.DockProtoModel> {
        return DataStoreFactory.create(
            serializer = DockProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.DOCK) },
        )
    }

    /**
     * Provides the [DataStore] instance for motion detector preferences.
     *
     * @param context the Android [Context] used to resolve the DataStore file location.
     * @return a [DataStore] backed by [MoveDetectorProtoSerializer] and stored in [PreferenceConfigure.Filename.MOVE_DETECTOR].
     * @see MoveDetectorProtoSerializer
     */
    @Single
    @Named("moveDetectorDataStore")
    public fun moveDetectorDataStore(context: Context): DataStore<MoveDetectorDataProto.MoveDetectorProtoModel> {
        return DataStoreFactory.create(
            serializer = MoveDetectorProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.MOVE_DETECTOR) },
        )
    }

    /**
     * Provides the [DataStore] instance for MQTT broker preferences.
     *
     * @param context the Android [Context] used to resolve the DataStore file location.
     * @return a [DataStore] backed by [MqttProtoSerializer] and stored in [PreferenceConfigure.Filename.MQTT].
     * @see MqttProtoSerializer
     */
    @Single
    @Named("mqttDataStore")
    public fun mqttDataStore(context: Context): DataStore<MqttDataProto.MqttProtoModel> {
        return DataStoreFactory.create(
            serializer = MqttProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.MQTT) },
        )
    }

    /**
     * Provides the [DataStore] instance for language preferences.
     *
     * @param context the Android [Context] used to resolve the DataStore file location.
     * @return a [DataStore] backed by [LanguageProtoSerializer] and stored in [PreferenceConfigure.Filename.LANGUAGE].
     * @see LanguageProtoSerializer
     */
    @Single
    @Named("languageDataStore")
    public fun languageDataStore(context: Context): DataStore<LanguagePreferenceProto> {
        return DataStoreFactory.create(
            serializer = LanguageProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.LANGUAGE) },
        )
    }
}
