package convention.source.conventionplugin.android

import convention.core.ext.configureAndroidBase
import convention.core.ext.configureKotlinBase
import convention.core.ext.configureSigning
import convention.core.ext.implementDependency
import convention.core.ext.lib
import convention.core.ext.libs
import convention.core.ext.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin that configures an Android library feature module with
 * Compose, Kotlin serialization, KSP, and a standard set of dependencies.
 *
 * Applied plugins:
 * - `com.android.library` (AGP 9 with built-in Kotlin support)
 * - `org.jetbrains.kotlin.plugin.serialization`
 * - `org.jetbrains.kotlin.plugin.compose`
 * - `com.google.devtools.ksp`
 * - `convention.quality` (Detekt + Ktlint)
 *
 * AGP 9 removed `buildTypes`, `buildConfig`, and `targetSdk` from library
 * modules, so none of those are configured here.
 *
 * @see AndroidApplicationConventionPlugin
 * @see convention.core.ext.configureAndroidBase
 */
public class AndroidFeatureConventionPlugin : Plugin<Project> {

    /**
     * Apply feature-module conventions to [target].
     *
     * @param target The Gradle project to configure as an Android feature library.
     */
    override fun apply(target: Project): Unit = with(target) {
        plugins {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.plugin.serialization")
            apply("org.jetbrains.kotlin.plugin.compose")
            apply("com.google.devtools.ksp")
            apply("convention.quality")
        }

        configureKotlinBase()

        lib {
            configureAndroidBase(this)
            configureSigning(this)

            buildFeatures {
                compose = true
            }
        }

        dependencies {
            // NOTE: Compose BOM is applied as a platform so that individual
            // Compose artefact versions are aligned automatically.
            val bom = libs.findLibrary("compose-bom").get()
            add("implementation", platform(bom))

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
