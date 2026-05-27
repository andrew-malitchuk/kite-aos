package convention.source.conventionplugin.kotlin

import convention.core.ext.configureKotlinBase
import convention.core.ext.getVersionAsInt
import convention.core.ext.getVersionAsString
import convention.core.ext.implementDependency
import convention.core.ext.jvm
import convention.core.ext.libs
import convention.core.ext.plugins
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/**
 * Convention plugin that configures a pure Kotlin/JVM library module.
 *
 * Applied plugins:
 * - `org.jetbrains.kotlin.jvm`
 * - `org.jetbrains.kotlin.plugin.serialization`
 * - `convention.quality` (Detekt + Ktlint)
 *
 * Sets Java source/target compatibility and the Kotlin JVM target from the
 * version catalog, enforces strict explicit-API mode, and adds the
 * `kotlinx-coroutines-core` dependency.
 *
 * @see convention.source.conventionplugin.android.AndroidFeatureConventionPlugin
 * @see convention.core.ext.configureKotlinBase
 */
public class KotlinLibraryConventionPlugin : Plugin<Project> {

    /**
     * Apply Kotlin/JVM library conventions to [target].
     *
     * @param target The Gradle project to configure as a pure Kotlin library.
     */
    override fun apply(target: Project): Unit = with(target) {
        plugins {
            apply("org.jetbrains.kotlin.jvm")
            apply("org.jetbrains.kotlin.plugin.serialization")
            apply("convention.quality")
        }

        configureKotlinBase()

        jvm {
            val javaVersion = JavaVersion.toVersion(getVersionAsInt("java"))
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }

        // NOTE: The JVM target is set explicitly here in addition to
        // configureKotlinBase() because pure JVM modules use
        // KotlinJvmProjectExtension which requires its own compilerOptions block.
        extensions.configure<KotlinJvmProjectExtension> {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(getVersionAsString("java")))
            }
        }

        dependencies {
            implementDependency(versionCatalog = libs, value = "kotlinx.coroutines.core")
        }
    }
}
