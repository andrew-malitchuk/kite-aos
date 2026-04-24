package dev.yahk.convention.source.conventionplugin.kotlin

import dev.yahk.convention.core.ext.configureKotlinBase
import dev.yahk.convention.core.ext.getVersionAsInt
import dev.yahk.convention.core.ext.implementDependency
import dev.yahk.convention.core.ext.jvm
import dev.yahk.convention.core.ext.libs
import dev.yahk.convention.core.ext.plugins
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * A convention plugin for pure Kotlin/JVM library modules.
 *
 * This plugin sets up the base configuration for JVM libraries, including:
 * - Plugins: `org.jetbrains.kotlin.jvm`, `org.jetbrains.kotlin.plugin.serialization`,
 *   and `dev.yahk.convention.quality` for code quality.
 * - [ExplicitApiMode.Strict][org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode] for Kotlin,
 *   requiring explicit visibility modifiers and return types on all public declarations.
 * - Java source and target compatibility from the version catalog.
 * - Base `kotlinx-coroutines-core` dependency.
 *
 * Registered as `dev.yahk.convention.library` in the Gradle plugin registry.
 *
 * Usage:
 *
 * ```kotlin
 * plugins {
 *     id("dev.yahk.convention.library")
 * }
 * ```
 *
 * @see Plugin
 * @see dev.yahk.convention.core.ext.configureKotlinBase
 * @since 0.0.1
 */
public class KotlinLibraryConventionPlugin : Plugin<Project> {

    /**
     * Applies the Kotlin/JVM library convention to the given [target] project.
     *
     * @param target The project to which this convention plugin is applied.
     * @since 0.0.1
     */
    override fun apply(target: Project): Unit = with(target) {
        // Apply Kotlin JVM, serialization, and code quality plugins
        plugins {
            apply("org.jetbrains.kotlin.jvm")
            apply("org.jetbrains.kotlin.plugin.serialization")
            apply("dev.yahk.convention.quality")
        }

        // Enforce ExplicitApiMode.Strict for public API surface
        configureKotlinBase()

        // Configure Java source and target compatibility from the version catalog
        jvm {
            val javaVersion = JavaVersion.toVersion(getVersionAsInt("java"))
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }

        dependencies {
            // Add kotlinx-coroutines-core as a baseline dependency for all JVM libraries
            implementDependency(versionCatalog = libs, value = "kotlinx.coroutines.core")
        }
    }
}
