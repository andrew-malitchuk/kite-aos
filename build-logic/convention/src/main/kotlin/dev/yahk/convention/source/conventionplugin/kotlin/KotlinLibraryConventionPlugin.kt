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
 * - Plugins: org.jetbrains.kotlin.jvm, serialization.
 * - Java compatibility based on version catalog.
 * - Strict explicit API mode for Kotlin.
 * - Base coroutines dependency.
 *
 * Usage:
 *
 * ```kotlin
 * plugins {
 *     id("dev.yahk.convention.library")
 * }
 * ```
 */
public class KotlinLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        plugins {
            apply("org.jetbrains.kotlin.jvm")
            apply("org.jetbrains.kotlin.plugin.serialization")
        }

        configureKotlinBase()

        jvm {
            val javaVersion = JavaVersion.toVersion(getVersionAsInt("java"))
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }

        dependencies {
            implementDependency(versionCatalog = libs, value = "kotlinx.coroutines.core")
        }
    }
}

