package dev.yahk.convention.source.conventionplugin.di

import dev.yahk.convention.core.ext.implementDependency
import dev.yahk.convention.core.ext.implementKsp
import dev.yahk.convention.core.ext.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * A convention plugin for configuring Koin dependency injection in Android projects.
 *
 * This plugin sets up:
 * - KSP plugin for compile-time annotation processing.
 * - Koin BOM for consistent dependency versions.
 * - Koin core, Android, Compose, Ktor, coroutines, ViewModel, and navigation dependencies.
 * - Koin annotations and KSP compiler for annotation-driven module generation.
 *
 * Registered as `dev.yahk.convention.di.android` in the Gradle plugin registry.
 *
 * Usage:
 *
 * ```kotlin
 * plugins {
 *     id("dev.yahk.convention.di.android")
 * }
 * ```
 *
 * @see Plugin
 * @see org.gradle.api.Project
 * @since 0.0.1
 */
public class DiAndroidConventionPlugin : Plugin<Project> {

    /**
     * Applies the Android Koin DI convention to the given [target] project.
     *
     * @param target The target project to which the plugin is applied.
     * @since 0.0.1
     */
    override fun apply(target: Project): Unit = with(target) {
        // Apply KSP for compile-time annotation processing of Koin modules
        pluginManager.apply("com.google.devtools.ksp")

        dependencies {
            // Use Koin BOM to align all Koin dependency versions
            val koinBom = libs.findLibrary("koin-bom").get()
            add("implementation", platform(koinBom))

            // KSP compiler for processing Koin annotations at compile time
            implementKsp(versionCatalog = libs, value = "koin.ksp.compiler")

            // Core Koin dependencies
            implementDependency(versionCatalog = libs, value = "koin.core")
            implementDependency(versionCatalog = libs, value = "koin.android")
            implementDependency(versionCatalog = libs, value = "koin.androidx.compose")
            implementDependency(versionCatalog = libs, value = "koin.ktor")
            implementDependency(versionCatalog = libs, value = "koin.core.coroutines")
            implementDependency(versionCatalog = libs, value = "koin.core.viewmodel")
            implementDependency(versionCatalog = libs, value = "koin.androidx.navigation")
            // koin-androidx-workmanager is removed as it's not resolved in Koin 4.0.0 BOM
            // Koin annotations for @Single, @KoinViewModel, @Module, etc.
            implementDependency(versionCatalog = libs, value = "koin.annotations")
        }
    }
}
