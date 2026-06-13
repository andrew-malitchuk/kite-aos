package convention.core.ext

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import java.io.File
import java.util.Properties

/**
 * Configure debug and release signing for an Android application module.
 *
 * Loads keystore credentials from `./configure/secrets/signing.properties`
 * relative to the root project directory. If the properties file is missing the
 * signing block is silently skipped, allowing CI and first-time builds to
 * succeed without local secrets.
 *
 * @param extension The [ApplicationExtension] to attach signing configs to.
 * @see configureSigning(LibraryExtension)
 */
internal fun Project.configureSigning(extension: ApplicationExtension) {
    extension.apply {
        // NOTE: Properties are loaded eagerly so that signing config values are
        // available during the configuration phase.
        val properties = Properties().also {
            val propertiesFile = File(project.rootDir, "./configure/secrets/signing.properties")
            if (propertiesFile.exists()) {
                propertiesFile.inputStream().use { stream -> it.load(stream) }
            }
        }

        val debugKeystore = File(project.rootDir, "./configure/signing/yahk.debug")
        val releaseKeystore = File(project.rootDir, "./configure/signing/yahk.release")

        signingConfigs {
            if (debugKeystore.exists()) {
                getByName("debug") {
                    keyAlias = properties.getProperty("debugKey")
                    keyPassword = properties.getProperty("debugPassword")
                    storePassword = properties.getProperty("debugPassword")
                    storeFile = debugKeystore
                }
            }
            if (releaseKeystore.exists()) {
                create("release") {
                    keyAlias = properties.getProperty("releaseKey")
                    keyPassword = properties.getProperty("releasePassword")
                    storePassword = properties.getProperty("releasePassword")
                    storeFile = releaseKeystore
                }
            }
        }
    }
}

/**
 * No-op signing configuration for Android library modules.
 *
 * Library modules do not support signing in AGP 9.0+, so this overload
 * exists solely to keep the call site uniform across module types.
 *
 * @param extension The [LibraryExtension] (unused).
 * @see configureSigning(ApplicationExtension)
 */
@Suppress("unused_parameter")
internal fun Project.configureSigning(extension: LibraryExtension) {
    // NOTE: Signing configuration is not applicable to library modules in AGP 9.0+.
}
