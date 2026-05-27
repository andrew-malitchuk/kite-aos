package convention.core.ext

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
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
 * Provide the `libs` version catalog declared in `settings.gradle.kts`.
 *
 * @return The [VersionCatalog] instance named `"libs"`.
 * @see getVersionAsString
 * @see getVersionAsInt
 */
val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Retrieve a version entry from the `libs` catalog as a [String].
 *
 * @param name The version alias exactly as it appears in `libs.versions.toml`.
 * @return The resolved version string.
 * @see getVersionAsInt
 * @see libs
 */
fun Project.getVersionAsString(name: String) = libs.findVersion(name).get().requiredVersion

/**
 * Retrieve a version entry from the `libs` catalog as an [Int].
 *
 * Useful for numeric SDK levels (`compileSdk`, `minSdk`, `targetSdk`).
 *
 * @param name The version alias exactly as it appears in `libs.versions.toml`.
 * @return The resolved version parsed to an [Int].
 * @see getVersionAsString
 * @see libs
 */
fun Project.getVersionAsInt(name: String) = libs.findVersion(name).get().requiredVersion.toInt()

/**
 * Apply standard Android base settings to an application module.
 *
 * Configures `compileSdk`, `minSdk`, and Java source/target compatibility from
 * the version catalog. Separate overloads exist for [ApplicationExtension] and
 * [LibraryExtension] because AGP 9.0 removed these properties from the shared
 * `CommonExtension` supertype.
 *
 * @param extension The [ApplicationExtension] to configure.
 * @see configureKotlinBase
 * @see configureSigning
 */
internal fun Project.configureAndroidBase(extension: ApplicationExtension) {
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
 * Apply standard Android base settings to a library module.
 *
 * Mirrors the application overload but operates on [LibraryExtension].
 *
 * @param extension The [LibraryExtension] to configure.
 * @see configureKotlinBase
 * @see configureSigning
 */
internal fun Project.configureAndroidBase(extension: LibraryExtension) {
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
 * Enforce strict explicit-API mode and set the JVM target for Kotlin compilation.
 *
 * Detects the project type by inspecting applied AGP plugins
 * (`com.android.application` / `com.android.library`) or the JVM plugin
 * (`org.jetbrains.kotlin.jvm`). AGP 9 provides built-in Kotlin support, so
 * Android modules are identified by the AGP plugin -- not by `kotlin-android`.
 *
 * @see configureAndroidBase
 */
internal fun Project.configureKotlinBase() {
    val pluginManager = this.pluginManager
    val jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(getVersionAsString("java"))
    // NOTE: AGP 9 embeds the Kotlin compiler, so we check for AGP plugin IDs
    // rather than the standalone kotlin-android plugin.
    if (pluginManager.hasPlugin("com.android.application") ||
        pluginManager.hasPlugin("com.android.library")
    ) {
        extensions.configure<KotlinAndroidProjectExtension> {
            explicitApi = ExplicitApiMode.Strict
            compilerOptions {
                this.jvmTarget.set(jvmTarget)
            }
        }
    } else if (pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")) {
        extensions.configure<KotlinProjectExtension> {
            explicitApi = ExplicitApiMode.Strict
        }
    }
}

/**
 * Open a configuration block for the [ApplicationExtension] of this project.
 *
 * @param block Lambda receiver scoped to [ApplicationExtension].
 * @see lib
 * @see jvm
 */
@Suppress("unused")
fun Project.app(block: ApplicationExtension.() -> Unit) {
    this.extensions.configure<ApplicationExtension> {
        this.block()
    }
}

/**
 * Open a configuration block for the [LibraryExtension] of this project.
 *
 * @param block Lambda receiver scoped to [LibraryExtension].
 * @see app
 * @see jvm
 */
@Suppress("unused")
fun Project.lib(block: LibraryExtension.() -> Unit) {
    this.extensions.configure<LibraryExtension> {
        this.block()
    }
}

/**
 * Open a configuration block for the project's [PluginManager].
 *
 * @param block Lambda receiver scoped to [PluginManager].
 * @see app
 * @see lib
 */
@Suppress("unused")
fun Project.plugins(block: PluginManager.() -> Unit) {
    this.pluginManager.block()
}

/**
 * Open a configuration block for the [JavaPluginExtension] of this project.
 *
 * Primarily used in pure Kotlin/JVM library modules to set Java source and
 * target compatibility.
 *
 * @param block Lambda receiver scoped to [JavaPluginExtension].
 * @see app
 * @see lib
 */
@Suppress("unused")
fun Project.jvm(block: JavaPluginExtension.() -> Unit) {
    this.extensions.configure<JavaPluginExtension> {
        this.block()
    }
}
