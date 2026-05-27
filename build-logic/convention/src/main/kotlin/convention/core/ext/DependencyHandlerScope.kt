package convention.core.ext

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.DependencyHandlerScope

/**
 * Add a version-catalog library to the `implementation` configuration.
 *
 * Resolves the library provider from [versionCatalog] and registers it as an
 * `implementation` dependency in the current scope.
 *
 * @param versionCatalog The version catalog that contains the library definition.
 * @param value The library alias as declared in the version catalog (e.g., `"core.ktx"`).
 * @see implementProject
 * @see implementKsp
 */
fun DependencyHandlerScope.implementDependency(versionCatalog: VersionCatalog, value: String) {
    add("implementation", versionCatalog.findLibrary(value).get())
}

/**
 * Add a project module to the `implementation` configuration.
 *
 * Resolves the Gradle project at [value] and registers it as an `implementation`
 * dependency, enabling inter-module compilation.
 *
 * @param project The root or parent project used to resolve the module path.
 * @param value The Gradle project path (e.g., `":domain-core"`).
 * @see implementDependency
 */
@Suppress("unused")
fun DependencyHandlerScope.implementProject(project: Project, value: String) {
    add("implementation", project.project(value))
}

/**
 * Add a version-catalog library to the `kapt` annotation-processing configuration.
 *
 * @param versionCatalog The version catalog that contains the library definition.
 * @param value The library alias as declared in the version catalog.
 * @see implementKsp
 */
@Suppress("unused")
fun DependencyHandlerScope.implementKapt(versionCatalog: VersionCatalog, value: String) {
    add("kapt", versionCatalog.findLibrary(value).get())
}

/**
 * Add a version-catalog library to the `kaptTest` annotation-processing configuration.
 *
 * Behaves identically to [implementKapt] but targets the test compilation classpath.
 *
 * @param versionCatalog The version catalog that contains the library definition.
 * @param value The library alias as declared in the version catalog.
 * @see implementKapt
 */
@Suppress("unused")
fun DependencyHandlerScope.implementKaptTest(versionCatalog: VersionCatalog, value: String) {
    add("kaptTest", versionCatalog.findLibrary(value).get())
}

/**
 * Add a version-catalog library to the `ksp` (Kotlin Symbol Processing) configuration.
 *
 * Resolves the KSP processor from [versionCatalog] and registers it so that the
 * KSP Gradle plugin runs the processor during compilation.
 *
 * @param versionCatalog The version catalog that contains the library definition.
 * @param value The library alias as declared in the version catalog (e.g., `"koin.ksp.compiler"`).
 * @see implementKapt
 * @see implementDependency
 */
fun DependencyHandlerScope.implementKsp(versionCatalog: VersionCatalog, value: String) {
    add("ksp", versionCatalog.findLibrary(value).get())
}

/**
 * Add a version-catalog library to the `androidTestImplementation` configuration.
 *
 * @param versionCatalog The version catalog that contains the library definition.
 * @param value The library alias as declared in the version catalog.
 * @see implementTestDependency
 */
@Suppress("unused")
fun DependencyHandlerScope.implementAndroidTestDependency(
    versionCatalog: VersionCatalog,
    value: String
) {
    add("androidTestImplementation", versionCatalog.findLibrary(value).get())
}

/**
 * Add a version-catalog library to the `debugImplementation` configuration.
 *
 * Use this for dependencies that should only be packaged in debug builds
 * (e.g., leak-detection or debug-drawer libraries).
 *
 * @param versionCatalog The version catalog that contains the library definition.
 * @param value The library alias as declared in the version catalog.
 * @see implementDependency
 */
@Suppress("unused")
fun DependencyHandlerScope.implementDebugImplementation(
    versionCatalog: VersionCatalog,
    value: String
) {
    add("debugImplementation", versionCatalog.findLibrary(value).get())
}

/**
 * Add a version-catalog library to the `testImplementation` configuration.
 *
 * @param versionCatalog The version catalog that contains the library definition.
 * @param value The library alias as declared in the version catalog.
 * @see implementTestRuntimeOnly
 * @see implementAndroidTestDependency
 */
fun DependencyHandlerScope.implementTestDependency(versionCatalog: VersionCatalog, value: String) {
    add("testImplementation", versionCatalog.findLibrary(value).get())
}

/**
 * Add a version-catalog library to the `testRuntimeOnly` configuration.
 *
 * Dependencies added here are available at test runtime but not at compile time,
 * which is useful for engine implementations (e.g., JUnit Platform).
 *
 * @param versionCatalog The version catalog that contains the library definition.
 * @param value The library alias as declared in the version catalog.
 * @see implementTestDependency
 */
fun DependencyHandlerScope.implementTestRuntimeOnly(versionCatalog: VersionCatalog, value: String) {
    add("testRuntimeOnly", versionCatalog.findLibrary(value).get())
}
