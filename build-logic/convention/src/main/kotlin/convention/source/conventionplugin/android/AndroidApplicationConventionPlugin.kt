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
 * Offset added to the base `versionCode` for the `tv` form-factor so mobile and TV APKs, which
 * share one `applicationId`, get distinct codes for Play multi-APK delivery. Chosen far above
 * any realistic base value so the mobile and TV ranges never overlap.
 */
private const val TV_VERSION_CODE_OFFSET = 1_000_000

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

            flavorDimensions += listOf("distribution", "formfactor")
            productFlavors {
                // GeckoView 147+ declares minSdk 26 in its manifest; override here
                // so the foss variant satisfies the library constraint while gms
                // (standard Android WebView) can target API 25.
                create("foss") {
                    dimension = "distribution"
                    minSdk = 26
                }
                create("gms") { dimension = "distribution" }

                // Second dimension for the Android TV form-factor. Both WebView
                // engines stay available on TV, so no variantFilter prunes the
                // matrix: {mobile,tv} x {foss,gms} x {debug,release}. IS_TV lets
                // the application module seed the runtime form-factor detection
                // (see AppConfig); feature libraries read it via AppConfig, not
                // their own BuildConfig.
                create("mobile") {
                    dimension = "formfactor"
                    buildConfigField("boolean", "IS_TV", "false")
                }
                create("tv") {
                    dimension = "formfactor"
                    buildConfigField("boolean", "IS_TV", "true")
                    // Multi-APK delivery under a shared applicationId requires distinct
                    // versionCodes so Play can serve mobile vs TV. Mobile keeps the base
                    // versionCode (release continuity); TV is offset into a separate range that
                    // still tracks the base as it increments. The offset is far above any
                    // realistic base value, so the ranges never collide.
                    versionCode = getVersionAsInt("versionCode") + TV_VERSION_CODE_OFFSET
                }
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
