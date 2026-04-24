package data.mqtt.impl.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin dependency injection module for the MQTT data layer implementation.
 *
 * Uses KSP annotation processing to automatically discover and register all
 * injectable components within the `data.mqtt.impl` package, including
 * [data.mqtt.impl.source.datasource.TelemetryMqttSourceImpl].
 *
 * @see data.mqtt.api.source.datasource.TelemetryMqttSource
 * @see data.mqtt.impl.source.datasource.TelemetryMqttSourceImpl
 * @since 0.0.1
 */
@Module
@ComponentScan("data.mqtt.impl")
public class DataMqttImplModule
