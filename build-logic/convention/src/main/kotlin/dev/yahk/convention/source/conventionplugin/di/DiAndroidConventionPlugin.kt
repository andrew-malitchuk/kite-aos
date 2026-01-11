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
 * - KSP plugin.
 * - Koin BOM and core dependencies for Android and Compose.
 * - Koin annotations and compiler.
 *
 * Usage:
 *
 * ```kotlin
 * plugins {
 *     id("dev.yahk.convention.di.android")
 * }
 * ```
 *
 * @see org.gradle.api.Project
 */
public class DiAndroidConventionPlugin : Plugin<Project> {

    /**
     * Applies the plugin to the given target project.
     *
     * @param target The target project to which the plugin is applied.
     */
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply("com.google.devtools.ksp")

        dependencies {
            val koinBom = libs.findLibrary("koin-bom").get()
            add("implementation", platform(koinBom))

            implementKsp(versionCatalog = libs, value = "koin.ksp.compiler")

            implementDependency(versionCatalog = libs, value = "koin.core")
            implementDependency(versionCatalog = libs, value = "koin.android")
            implementDependency(versionCatalog = libs, value = "koin.androidx.compose")
            implementDependency(versionCatalog = libs, value = "koin.ktor")
            implementDependency(versionCatalog = libs, value = "koin.core.coroutines")
            implementDependency(versionCatalog = libs, value = "koin.core.viewmodel")
            implementDependency(versionCatalog = libs, value = "koin.androidx.navigation")
            // koin-androidx-workmanager is removed as it's not resolved in Koin 4.0.0 BOM
            implementDependency(versionCatalog = libs, value = "koin.annotations")
        }
    }
}