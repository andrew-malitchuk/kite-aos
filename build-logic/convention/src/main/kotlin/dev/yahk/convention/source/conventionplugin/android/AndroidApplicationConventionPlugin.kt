package dev.yahk.convention.source.conventionplugin.android

import dev.yahk.convention.core.ext.app
import dev.yahk.convention.core.ext.configureAndroidBase
import dev.yahk.convention.core.ext.configureKotlinBase
import dev.yahk.convention.core.ext.configureSigning
import dev.yahk.convention.core.ext.getVersionAsInt
import dev.yahk.convention.core.ext.getVersionAsString
import dev.yahk.convention.core.ext.implementDependency
import dev.yahk.convention.core.ext.libs
import dev.yahk.convention.core.ext.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for Android application modules.
 *
 * Applies the standard configuration shared across all app modules in the project, including:
 * - Android application, Kotlin, Compose, and code quality plugins.
 * - [ExplicitApiMode.Strict][org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode] for Kotlin.
 * - Base Android SDK settings (compileSdk, minSdk, targetSdk) from the version catalog.
 * - Signing configuration loaded from local property files.
 * - Compose build features and BuildConfig generation.
 * - ProGuard/R8 minification and resource shrinking for release builds.
 * - Core Compose and AndroidX dependencies (Material 3, Activity Compose, Lifecycle ViewModel).
 *
 * Registered as `dev.yahk.convention.application` in the Gradle plugin registry.
 *
 * @see Plugin
 * @see dev.yahk.convention.core.ext.configureAndroidBase
 * @see dev.yahk.convention.core.ext.configureSigning
 * @since 0.0.1
 */
public class AndroidApplicationConventionPlugin : Plugin<Project> {

    /**
     * Applies the Android application convention to the given [target] project.
     *
     * @param target The project to which this convention plugin is applied.
     * @since 0.0.1
     */
    override fun apply(target: Project): Unit = with(target) {
        // Apply required Gradle plugins for Android app, Kotlin, Compose, and code quality
        plugins {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
            apply("org.jetbrains.kotlin.plugin.compose")
            apply("dev.yahk.convention.quality")
        }

        // Enforce ExplicitApiMode.Strict for public API surface
        configureKotlinBase()

        app {
            // Set compileSdk, minSdk, and Java compatibility from version catalog
            configureAndroidBase(this)
            // Load signing configs from local properties (skipped if file is absent)
            configureSigning(this)

            buildFeatures {
                // Enable BuildConfig generation for version/build-type constants
                buildConfig = true
                // Enable Jetpack Compose support
                compose = true
            }

            dependenciesInfo {
                // Exclude dependency metadata from APK and AAB for smaller artifacts
                includeInApk = false
                includeInBundle = false
            }

            defaultConfig {
                applicationId = getVersionAsString("applicationId")
                targetSdk = getVersionAsInt("targetSdk")
                versionCode = getVersionAsInt("versionCode")
                versionName = getVersionAsString("versionName")
            }

            buildTypes {
                release {
                    // Enable code minification with R8
                    isMinifyEnabled = true
                    // Enable resource shrinking to remove unused resources
                    isShrinkResources = true
                    // Use the release signing config defined in configureSigning
                    signingConfig = signingConfigs.findByName("release")
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        rootProject.file("proguard-rules.pro")
                    )
                }
            }

            packaging {
                resources {
                    // Exclude duplicate license files that cause merge conflicts
                    excludes += listOf(
                        "META-INF/LICENSE.md",
                        "META-INF/LICENSE-notice.md",
                    )
                }
            }
        }

        dependencies {
            // Use Compose BOM for consistent Compose library versions
            val bom = libs.findLibrary("compose-bom").get()
            add("implementation", platform(bom))

            // Core AndroidX and Compose dependencies
            implementDependency(versionCatalog = libs, value = "core.ktx")
            implementDependency(versionCatalog = libs, value = "material3")
            implementDependency(versionCatalog = libs, value = "ui.tooling")
            implementDependency(versionCatalog = libs, value = "ui.tooling.preview")
            implementDependency(versionCatalog = libs, value = "activity.compose")
            implementDependency(versionCatalog = libs, value = "lifecycle.viewmodel.compose")
            implementDependency(versionCatalog = libs, value = "compose.runtime")
        }
    }
}
