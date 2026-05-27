package convention.source.conventionplugin.di

import convention.core.ext.implementDependency
import convention.core.ext.implementKsp
import convention.core.ext.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin that configures Koin dependency injection for Android modules.
 *
 * Applies the KSP plugin and adds the Koin BOM together with Android-specific
 * artefacts (Compose integration, ViewModel scoping, AndroidX Navigation).
 * The KSP annotation compiler is registered so that `@Module`, `@Single`,
 * `@Factory`, and `@KoinViewModel` annotations are processed at build time.
 *
 * @see DiConventionPlugin
 * @see convention.core.ext.implementKsp
 */
public class DiAndroidConventionPlugin : Plugin<Project> {

    /**
     * Apply Android Koin DI conventions to [target].
     *
     * @param target The Gradle project to configure with Koin Android dependencies.
     */
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply("com.google.devtools.ksp")

        dependencies {
            // NOTE: The Koin BOM aligns all Koin artefact versions automatically.
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
            // WORKAROUND: koin-androidx-workmanager is excluded because the
            // Koin 4.0.0 BOM does not resolve it; re-add when upstream fixes this.
            implementDependency(versionCatalog = libs, value = "koin.annotations")
        }
    }
}
