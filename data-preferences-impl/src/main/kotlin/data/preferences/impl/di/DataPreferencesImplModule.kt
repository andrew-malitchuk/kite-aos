package data.preferences.impl.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import data.preferences.impl.core.configure.PreferenceConfigure
import data.preferences.impl.core.serializer.DashboardProtoSerializer
import data.preferences.impl.core.serializer.DockProtoSerializer
import data.preferences.impl.core.serializer.MoveDetectorProtoSerializer
import data.preferences.impl.core.serializer.OnboardingProtoSerializer
import data.preferences.impl.core.serializer.ThemeProtoSerializer
import data.preferences.impl.proto.DashboardDataProto
import data.preferences.impl.proto.DockDataProto
import data.preferences.impl.proto.MoveDetectorDataProto
import data.preferences.impl.proto.OnboardingDataProto
import data.preferences.impl.proto.ThemeDataProto
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

import data.preferences.impl.core.serializer.MqttProtoSerializer
import data.preferences.impl.proto.MqttDataProto
import data.preferences.api.source.datasource.LanguagePreferenceSource
import data.preferences.impl.proto.LanguagePreferenceProto
import data.preferences.impl.core.serializer.LanguageProtoSerializer
import data.preferences.impl.source.datasource.LanguagePreferenceSourceImpl

@Module
@ComponentScan("data.preferences.impl")
public class DataPreferencesImplModule {

    @Single
    @Named("themeDataStore")
    public fun themeDataStore(context: Context): DataStore<ThemeDataProto.ThemeProtoModel> {
        return DataStoreFactory.create(
            serializer = ThemeProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.THEME) }
        )
    }

    @Single
    @Named("onboardingDataStore")
    public fun onboardingDataStore(context: Context): DataStore<OnboardingDataProto.OnboardingProtoModel> {
        return DataStoreFactory.create(
            serializer = OnboardingProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.ONBOARDING) }
        )
    }

    @Single
    @Named("dashboardDataStore")
    public fun dashboardDataStore(context: Context): DataStore<DashboardDataProto.DashboardProtoModel> {
        return DataStoreFactory.create(
            serializer = DashboardProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.DASHBOARD) }
        )
    }

    @Single
    @Named("dockPositionDataStore")
    public fun dockPositionDataStore(context: Context): DataStore<DockDataProto.DockProtoModel> {
        return DataStoreFactory.create(
            serializer = DockProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.DOCK) }
        )
    }

    @Single
    @Named("moveDetectorDataStore")
    public fun moveDetectorDataStore(context: Context): DataStore<MoveDetectorDataProto.MoveDetectorProtoModel> {
        return DataStoreFactory.create(
            serializer = MoveDetectorProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.MOVE_DETECTOR) }
        )
    }

    @Single
    @Named("mqttDataStore")
    public fun mqttDataStore(context: Context): DataStore<MqttDataProto.MqttProtoModel> {
        return DataStoreFactory.create(
            serializer = MqttProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.MQTT) }
        )
    }

    @Single
    @Named("languageDataStore")
    public fun languageDataStore(context: Context): DataStore<LanguagePreferenceProto> {
        return DataStoreFactory.create(
            serializer = LanguageProtoSerializer(),
            produceFile = { context.dataStoreFile(PreferenceConfigure.Filename.LANGUAGE) }
        )
    }
}