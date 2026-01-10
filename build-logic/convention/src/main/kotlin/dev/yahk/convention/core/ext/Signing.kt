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
 * @param extension The [CommonExtension] (Application or Library) to apply signing to.
 */
internal fun Project.configureSigning(extension: CommonExtension<*, *, *, *, *, *>) {
    extension.apply {
        val properties = Properties().also {
            val propertiesFile = File(project.rootDir, "./configure/secrets/signing.properties")
            if (propertiesFile.exists()) {
                it.load(FileInputStream(propertiesFile))
            }
        }

        signingConfigs {
            getByName("debug") {
                keyAlias = properties.getProperty("debugKey")
                keyPassword = properties.getProperty("debugPassword")
                storePassword = properties.getProperty("debugPassword")
                storeFile = File(project.rootDir, "./configure/signing/yahk.debug")
            }
            create("release") {
                keyAlias = properties.getProperty("releaseKey")
                keyPassword = properties.getProperty("releasePassword")
                storePassword = properties.getProperty("releasePassword")
                storeFile = File(project.rootDir, "./configure/signing/yahk.release")
            }
        }
    }
}
