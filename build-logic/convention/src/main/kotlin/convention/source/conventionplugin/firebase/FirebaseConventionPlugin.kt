package convention.source.conventionplugin.firebase

import convention.core.ext.libs
import convention.core.ext.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin that configures Firebase for the `gms` product flavor only.
 *
 * Applied plugins:
 * - `com.google.gms.google-services` — processes `google-services.json` from `src/gms/`
 * - `com.google.firebase.crashlytics` — enables Crashlytics symbol upload and mapping
 *
 * Dependencies are added under the `gmsImplementation` configuration so that
 * they are only compiled into `gms*` variants (`gmsDebug`, `gmsRelease`).
 * The `foss*` variants are built without any Firebase dependency.
 *
 * Google-services and Crashlytics tasks that target `foss` variants are
 * disabled in `afterEvaluate` to avoid failures from missing `google-services.json`.
 *
 * Prerequisites: place a valid `google-services.json` at
 * `presentation-core-application/src/gms/google-services.json`.
 * The file is listed in `.gitignore` and must be provisioned per environment.
 */
public class FirebaseConventionPlugin : Plugin<Project> {

    /**
     * Apply Firebase conventions to [target].
     *
     * @param target The Gradle project (app module) to configure with Firebase.
     */
    override fun apply(target: Project): Unit = with(target) {
        plugins {
            apply("com.google.gms.google-services")
            apply("com.google.firebase.crashlytics")
        }

        dependencies {
            // NOTE: Firebase BOM and libraries are scoped to `gmsImplementation` so
            // they are excluded from the `foss` flavor entirely.
            val firebaseBom = libs.findLibrary("firebase-bom").get()
            add("gmsImplementation", platform(firebaseBom))

            add("gmsImplementation", libs.findLibrary("firebase.analytics").get())
            add("gmsImplementation", libs.findLibrary("firebase.crashlytics").get())
            add("gmsImplementation", libs.findLibrary("firebase.config").get())
        }

        // Disable google-services and Crashlytics processing tasks for foss variants
        // to prevent build failures caused by the missing google-services.json.
        afterEvaluate {
            tasks.configureEach {
                val isFossTask = name.contains("Foss", ignoreCase = true)
                val isGoogleServicesTask = name.startsWith("process") && name.endsWith("GoogleServices")
                val isCrashlyticsTask = name.startsWith("injectCrashlytics") || name.startsWith("uploadCrashlytics")
                if (isFossTask && (isGoogleServicesTask || isCrashlyticsTask)) {
                    enabled = false
                }
            }
        }
    }
}
