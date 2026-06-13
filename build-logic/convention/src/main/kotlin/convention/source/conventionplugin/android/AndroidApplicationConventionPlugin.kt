package convention.source.conventionplugin.android

import convention.core.ext.app
import convention.core.ext.configureAndroidBase
import convention.core.ext.configureKotlinBase
import convention.core.ext.configureSigning
import convention.core.ext.getVersionAsInt
import convention.core.ext.getVersionAsString
import convention.core.ext.implementDependency
import convention.core.ext.libs
import convention.core.ext.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin that configures an Android application module with Compose,
 * signing, ProGuard, and a standard set of Compose/Lifecycle dependencies.
 *
 * Applied plugins:
 * - `com.android.application` (AGP 9 with built-in Kotlin support)
 * - `org.jetbrains.kotlin.plugin.compose`
 * - `convention.quality` (Detekt + Ktlint)
 *
 * The plugin reads `applicationId`, `targetSdk`, `versionCode`, and
 * `versionName` from the version catalog so that all application metadata is
 * centralised in `libs.versions.toml`.
 *
 * @see AndroidFeatureConventionPlugin
 * @see convention.core.ext.configureAndroidBase
 * @see convention.core.ext.configureSigning
 */
public class AndroidApplicationConventionPlugin : Plugin<Project> {

    /**
     * Apply application conventions to [target].
     *
     * @param target The Gradle project to configure as an Android application.
     */
    override fun apply(target: Project): Unit = with(target) {
        plugins {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.plugin.compose")
            apply("convention.quality")
        }

        configureKotlinBase()

        app {
            configureAndroidBase(this)
            configureSigning(this)

            buildFeatures {
                buildConfig = true
                compose = true
            }

            defaultConfig {
                applicationId = getVersionAsString("applicationId")
                targetSdk = getVersionAsInt("targetSdk")
                versionCode = getVersionAsInt("versionCode")
                versionName = getVersionAsString("versionName")
            }

            flavorDimensions += "distribution"
            productFlavors {
                // GeckoView 147+ declares minSdk 26 in its manifest; override here
                // so the foss variant satisfies the library constraint while gms
                // (standard Android WebView) can target API 25.
                create("foss") {
                    dimension = "distribution"
                    minSdk = 26
                }
                create("gms") { dimension = "distribution" }
            }

            buildTypes {
                debug {
                    applicationIdSuffix = ".debug"
                }
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
                    // NOTE: Exclude duplicate META-INF licence files that ship
                    // with certain transitive dependencies and cause merge conflicts.
                    excludes += listOf(
                        "META-INF/LICENSE.md",
                        "META-INF/LICENSE-notice.md",
                    )
                }
            }
        }

        dependencies {
            // NOTE: Compose BOM is applied as a platform so that individual
            // Compose artefact versions are aligned automatically.
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
