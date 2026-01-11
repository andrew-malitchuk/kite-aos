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

public class AndroidFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        plugins {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
            apply("org.jetbrains.kotlin.plugin.serialization")
            apply("org.jetbrains.kotlin.plugin.compose")
            apply("com.google.devtools.ksp")
        }

        configureKotlinBase()

        lib {
            configureAndroidBase(this)
            configureSigning(this)

            buildFeatures {
                compose = true
            }

            defaultConfig {
                @Suppress("DEPRECATION")
                targetSdk = getVersionAsInt("targetSdk")
            }

            buildTypes {
                release {
                    isMinifyEnabled = false
                    consumerProguardFiles(rootProject.file("proguard-rules.pro"))
                }
            }
        }

        this.extensions.configure<com.android.build.gradle.LibraryExtension> {
            buildFeatures.buildConfig = true
        }

        dependencies {
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