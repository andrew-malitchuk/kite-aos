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

public class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        plugins {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
            apply("org.jetbrains.kotlin.plugin.compose")
            apply("dev.yahk.convention.quality")
        }

        configureKotlinBase()

        app {
            configureAndroidBase(this)
            configureSigning(this)

            buildFeatures {
                buildConfig = true
                compose = true
            }

            dependenciesInfo {
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
                    isMinifyEnabled = true
                    isShrinkResources = true
                    signingConfig = signingConfigs.findByName("release")
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        rootProject.file("proguard-rules.pro")
                    )
                }
            }

            packaging {
                resources {
                    excludes += listOf(
                        "META-INF/LICENSE.md",
                        "META-INF/LICENSE-notice.md",
                    )
                }
            }
        }

        dependencies {
            val bom = libs.findLibrary("compose-bom").get()
            add("implementation", platform(bom))

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
