package dev.yahk.convention.source.conventionplugin.android

import dev.yahk.convention.core.ext.configureAndroidBase
import dev.yahk.convention.core.ext.configureKotlinBase
import dev.yahk.convention.core.ext.configureSigning
import dev.yahk.convention.core.ext.getVersionAsInt
import dev.yahk.convention.core.ext.implementDependency
import dev.yahk.convention.core.ext.lib
import dev.yahk.convention.core.ext.libs
import dev.yahk.convention.core.ext.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for Android feature library modules.
 *
 * Applies the standard configuration shared across all feature modules in the project, including:
 * - Android library, Kotlin, Compose, KSP, serialization, and code quality plugins.
 * - [ExplicitApiMode.Strict][org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode] for Kotlin.
 * - Base Android SDK settings (compileSdk, minSdk, targetSdk) from the version catalog.
 * - Signing configuration loaded from local property files.
 * - Compose build features and BuildConfig generation.
 * - Consumer ProGuard rules for release builds (no minification in library modules).
 * - Core Compose, coroutines, serialization, and AndroidX dependencies.
 *
 * Registered as `dev.yahk.convention.feature` in the Gradle plugin registry.
 *
 * @see Plugin
 * @see dev.yahk.convention.core.ext.configureAndroidBase
 * @see dev.yahk.convention.core.ext.configureSigning
 * @since 0.0.1
 */
public class AndroidFeatureConventionPlugin : Plugin<Project> {

    /**
     * Applies the Android feature library convention to the given [target] project.
     *
     * @param target The project to which this convention plugin is applied.
     * @since 0.0.1
     */
    override fun apply(target: Project): Unit = with(target) {
        // Apply required Gradle plugins for Android library, Kotlin, Compose, KSP, and quality
        plugins {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
            apply("org.jetbrains.kotlin.plugin.serialization")
            apply("org.jetbrains.kotlin.plugin.compose")
            apply("com.google.devtools.ksp")
            apply("dev.yahk.convention.quality")
        }

        // Enforce ExplicitApiMode.Strict for public API surface
        configureKotlinBase()

        lib {
            // Set compileSdk, minSdk, and Java compatibility from version catalog
            configureAndroidBase(this)
            // Load signing configs from local properties (skipped if file is absent)
            configureSigning(this)

            buildFeatures {
                // Enable Jetpack Compose support
                compose = true
            }

            defaultConfig {
                @Suppress("DEPRECATION")
                targetSdk = getVersionAsInt("targetSdk")
            }

            buildTypes {
                release {
                    // Library modules do not minify; consumers handle minification
                    isMinifyEnabled = false
                    consumerProguardFiles(rootProject.file("proguard-rules.pro"))
                }
            }
        }

        // Enable BuildConfig generation separately to allow feature modules to access build metadata
        this.extensions.configure<com.android.build.gradle.LibraryExtension> {
            buildFeatures.buildConfig = true
        }

        dependencies {
            // Use Compose BOM for consistent Compose library versions
            val bom = libs.findLibrary("compose-bom").get()
            add("implementation", platform(bom))

            // Core coroutines, serialization, and Compose dependencies
            implementDependency(versionCatalog = libs, value = "kotlinx.coroutines.core")
            implementDependency(versionCatalog = libs, value = "kotlinx.serialization.json")
            implementDependency(versionCatalog = libs, value = "core.ktx")
            implementDependency(versionCatalog = libs, value = "material3")
            implementDependency(versionCatalog = libs, value = "ui.tooling")
            implementDependency(versionCatalog = libs, value = "activity.compose")
            implementDependency(versionCatalog = libs, value = "lifecycle.viewmodel.compose")
            implementDependency(versionCatalog = libs, value = "compose.runtime")
            implementDependency(versionCatalog = libs, value = "lifecycle.runtime.compose")
        }
    }
}
