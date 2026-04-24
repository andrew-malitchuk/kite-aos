package dev.yahk.convention.core.ext

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.Properties

/**
 * Configures signing for an Android project using local property files.
 *
 * This function performs the following steps:
 * 1. Locates the `./configure/secrets/signing.properties` file in the root directory.
 * 2. Loads property keys for aliases and passwords.
 * 3. Configures the 'debug' and 'release' signing configurations with the loaded data.
 * 4. Points to the keystore files located in `./configure/signing/`.
 *
 * If the properties file does not exist, signing configuration is skipped silently,
 * allowing builds to proceed without signing (e.g., on CI or fresh clones).
 *
 * @param extension The [CommonExtension] (Application or Library) to apply signing to.
 * @see CommonExtension
 * @since 0.0.1
 */
internal fun Project.configureSigning(extension: CommonExtension<*, *, *, *, *, *>) {
    extension.apply {
        val propertiesFile = File(project.rootDir, "./configure/secrets/signing.properties")
        // Skip signing configuration if properties file is absent (e.g., CI environment)
        if (!propertiesFile.exists()) return@apply

        // Load signing credentials from the properties file
        val properties = Properties().also {
            it.load(FileInputStream(propertiesFile))
        }

        signingConfigs {
            // Configure debug signing with the debug keystore
            getByName("debug") {
                keyAlias = properties.getProperty("debugKey")
                keyPassword = properties.getProperty("debugPassword")
                storePassword = properties.getProperty("debugPassword")
                storeFile = File(project.rootDir, "./configure/signing/yahk.debug")
            }
            // Configure release signing with the release keystore
            create("release") {
                keyAlias = properties.getProperty("releaseKey")
                keyPassword = properties.getProperty("releasePassword")
                storePassword = properties.getProperty("releasePassword")
                storeFile = File(project.rootDir, "./configure/signing/yahk.release")
            }
        }
    }
}
