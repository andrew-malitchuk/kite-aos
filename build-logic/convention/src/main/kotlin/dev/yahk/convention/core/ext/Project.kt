package dev.yahk.convention.core.ext

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

/**
 * Retrieves the [VersionCatalog] from the project's extensions.
 *
 * This property provides access to the `VersionCatalog` named "libs" from the project's
 * extensions. It simplifies the process of accessing library versions and dependencies
 * defined in the version catalog.
 *
 * Usage:
 *
 * ```kotlin
 * val versionCatalog = project.libs
 * val libraryVersion = versionCatalog.findLibrary("some.library").get()
 * ```
 *
 * @return The [VersionCatalog] instance associated with the name "libs".
 */
val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Retrieves version as a [String] from the project's extensions by the given name.
 *
 * This extension function simplifies the process of accessing specific version
 * from the version catalog.
 *
 * Usage:
 *
 * ```kotlin
 * val version: String = project.getVersionAsString("someVersion")
 * ```
 *
 * @param name The exact name of the version entry in the version catalog.
 *
 * @return The version as a [String].
 */
fun Project.getVersionAsString(name: String) = libs.findVersion(name).get().requiredVersion

/**
 * Retrieves version as a [Int] from the project's extensions by the given name.
 *
 * This extension function simplifies the process of accessing specific version
 * from the version catalog.
 *
 * Usage:
 *
 * ```kotlin
 * val version: Int = project.getVersionAsInt("someVersion")
 * ```
 *
 * @param name The exact name of the version entry in the version catalog.
 *
 * @return The version as a [Int].
 */
fun Project.getVersionAsInt(name: String) = libs.findVersion(name).get().requiredVersion.toInt()

/**
 * Centralized base configuration for Android modules.
 *
 * Sets up standard SDK versions (`compileSdk`, `minSdk`) and Java compatibility
 * (source and target) based on values defined in the version catalog.
 *
 * @param extension The [CommonExtension] to apply base Android settings to.
 */
internal fun Project.configureAndroidBase(extension: CommonExtension<*, *, *, *, *, *>) {
    extension.apply {
        compileSdk = getVersionAsInt("compileSdk")

        defaultConfig {
            minSdk = getVersionAsInt("minSdk")
        }

        compileOptions {
            val javaVersion = JavaVersion.toVersion(getVersionAsInt("java"))
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }
    }
}

/**
 * Centralized base configuration for Kotlin projects.
 *
 * Automatically detects whether the project is an Android or pure JVM module
 * and applies [ExplicitApiMode.Strict]. This ensures that all public declarations
 * have explicit visibility and type specifications, improving API design and maintainability.
 */
internal fun Project.configureKotlinBase() {
    val pluginManager = this.pluginManager
    if (pluginManager.hasPlugin("org.jetbrains.kotlin.android")) {
        extensions.configure<KotlinAndroidProjectExtension> {
            explicitApi = ExplicitApiMode.Strict
        }
    } else if (pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")) {
        extensions.configure<KotlinProjectExtension> {
            explicitApi = ExplicitApiMode.Strict
        }
    }
}

/**
 * Configures the [ApplicationExtension] for an Android project.
 *
 * This extension function allows you to configure the `ApplicationExtension` for an Android project
 * using a lambda expression. It provides a convenient way to set up application-specific configurations.
 *
 * Usage:
 *
 * ```kotlin
 * project.app {
 *     defaultConfig {
 *         applicationId = "com.example.app"
 *     }
 *     buildTypes {
 *         getByName("release") {
 *             isMinifyEnabled = true
 *         }
 *     }
 * }
 * ```
 *
 * @param block A lambda expression that configures the [ApplicationExtension] for the project.
 */
@Suppress("unused")
fun Project.app(block: ApplicationExtension.() -> Unit) {
    this.extensions.configure<ApplicationExtension> {
        this.block()
    }
}

/**
 * Configures the [LibraryExtension] for an Android library project.
 *
 * This extension function allows you to configure the `LibraryExtension` for an Android library project
 * using a lambda expression. It provides a convenient way to set up library-specific configurations.
 *
 * Usage:
 *
 * ```kotlin
 * project.lib {
 *     defaultConfig {
 *         minSdk = 21
 *         targetSdk = 30
 *     }
 *     buildTypes {
 *         getByName("release") {
 *             isMinifyEnabled = true
 *         }
 *     }
 * }
 * ```
 *
 * @param block A lambda expression that configures the [LibraryExtension] for the project.
 */
@Suppress("unused")
fun Project.lib(block: LibraryExtension.() -> Unit) {
    this.extensions.configure<LibraryExtension> {
        this.block()
    }
}

/**
 * Configures plugins for the project.
 *
 * This extension function provides a convenient way to configure plugins for a project
 * using a lambda expression. It allows you to add or configure plugins in a project context.
 *
 * Usage:
 *
 * ```kotlin
 * project.plugins {
 *     id("com.android.application")
 *     id("org.jetbrains.kotlin.android")
 * }
 * ```
 *
 * @param block A lambda expression that configures the [PluginManager] for the project.
 */
@Suppress("unused")
fun Project.plugins(block: PluginManager.() -> Unit) {
    this.pluginManager.block()
}






/**
 * Configures the [JavaPluginExtension] for an JVM library project.
 *
 * This extension function allows you to configure the `JavaPluginExtension` for an library project
 * using a lambda expression. It provides a convenient way to set up library-specific configurations.
 *
 * Usage:
 *
 * ```kotlin
 * project.jvm {
 *      ...
 * }
 * ```
 *
 * @param block A lambda expression that configures the [LibraryExtension] for the project.
 */
@Suppress("unused")
fun Project.jvm(block: JavaPluginExtension.() -> Unit) {
    this.extensions.configure<JavaPluginExtension> {
        this.block()
    }
}