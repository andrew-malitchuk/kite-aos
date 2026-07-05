package presentation.core.platform.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Koin dependency injection module for the platform core layer.
 *
 * Automatically scans and provides all platform-level components within the
 * `presentation.core.platform` package, including [presentation.core.platform.source.scheduler.AutoRebootScheduler]
 * and [presentation.core.platform.source.streaming.MjpegHttpServer].
 *
 * @see presentation.core.platform.source.scheduler.AutoRebootScheduler
 * @see presentation.core.platform.source.streaming.MjpegHttpServer
 * @since 0.0.1
 */
@Module
@ComponentScan("presentation.core.platform")
public class PresentationCorePlatformModule
