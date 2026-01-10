package dev.yahk.convention.core.ext

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.DependencyHandlerScope

/**
 * Adds a dependency to the `implementation` configuration using the version catalog.
 *
 * This extension function allows you to add a dependency to the `implementation` configuration
 * of a project by specifying a library reference from the version catalog. It simplifies
 * the addition of dependencies by automatically resolving the library version from the catalog.
 *
 * Usage:
 *
 * ```kotlin
 * dependencies {
 *     implementDependency("some.library")
 * }
 * ```
 *
 * @param versionCatalog Version catalog of this project.
 * @param value The library reference as defined in the version catalog.
 */
fun DependencyHandlerScope.implementDependency(versionCatalog: VersionCatalog, value: String) {
    add("implementation", versionCatalog.findLibrary(value).get())
}

/**
 * Adds a project dependency to the `implementation` configuration.
 *
 * This extension function allows you to add a project dependency to the `implementation` configuration
 * by specifying the project path. It simplifies the process of including project modules as dependencies.
 *
 * Usage:
 *
 * ```kotlin
 * dependencies {
 *     implementProject(":module")
 * }
 * ```
 *
 * @param project Project module for dependency to be added.
 * @param value The path to the project module to be added as a dependency.
 */
@Suppress("unused")
fun DependencyHandlerScope.implementProject(project: Project, value: String) {
    add("implementation", project.project(value))
}

/**
 * Adds a dependency to the `kapt` configuration using the version catalog.
 *
 * This extension function allows you to add a dependency to the `kapt` configuration
 * of a project by specifying a library reference from the version catalog. It simplifies
 * the addition of annotation processor dependencies by automatically resolving the library version
 * from the catalog.
 *
 * Usage:
 *
 * ```kotlin
 * dependencies {
 *     implementKapt("some.annotationProcessor")
 * }
 * ```
 *
 * @param versionCatalog Version catalog of this project.
 * @param value The library reference as defined in the version catalog.
 */
@Suppress("unused")
fun DependencyHandlerScope.implementKapt(versionCatalog: VersionCatalog, value: String) {
    add("kapt", versionCatalog.findLibrary(value).get())
}

/**
 * Adds a dependency to the `kapt` configuration using the version catalog.
 *
 * This extension function allows you to add a dependency to the `kapt` configuration
 * of a project by specifying a library reference from the version catalog. It simplifies
 * the addition of annotation processor dependencies by automatically resolving the library version
 * from the catalog.
 *
 * Usage:
 *
 * ```kotlin
 * dependencies {
 *     implementKapt("some.annotationProcessor")
 * }
 * ```
 *
 * @param versionCatalog Version catalog of this project.
 * @param value The library reference as defined in the version catalog.
 */
@Suppress("unused")
fun DependencyHandlerScope.implementKaptTest(versionCatalog: VersionCatalog, value: String) {
    add("kaptTest", versionCatalog.findLibrary(value).get())
}


/**
 * Adds a dependency to the `ksp` configuration using the version catalog.
 *
 * This extension function allows you to add a dependency to the `ksp` configuration
 * of a project by specifying a library reference from the version catalog. It simplifies
 * the addition of Kotlin Symbol Processing (KSP) dependencies by automatically resolving the library version
 * from the catalog.
 *
 * Usage:
 *
 * ```kotlin
 * dependencies {
 *     implementKsp("some.kspProcessor")
 * }
 * ```
 *
 * @param versionCatalog Version catalog of this project.
 * @param value The library reference as defined in the version catalog.
 */
fun DependencyHandlerScope.implementKsp(versionCatalog: VersionCatalog, value: String) {
    add("ksp", versionCatalog.findLibrary(value).get())
}

/**
 * Adds a dependency to the `androidTestImplementation` configuration using the version catalog.
 *
 * This extension function allows you to add a dependency to the `androidTestImplementation` configuration
 * of a project by specifying a library reference from the version catalog. It simplifies
 * the addition of Android test dependencies by automatically resolving the library version from the catalog.
 *
 * Usage:
 *
 * ```kotlin
 * dependencies {
 *     implementAndroidTestDependency("some.android.test.library")
 * }
 * ```
 *
 * @param versionCatalog Version catalog of this project.
 * @param value The library reference as defined in the version catalog.
 */
@Suppress("unused")
fun DependencyHandlerScope.implementAndroidTestDependency(
    versionCatalog: VersionCatalog,
    value: String
) {
    add("androidTestImplementation", versionCatalog.findLibrary(value).get())
}

/**
 * Adds a dependency to the `debugImplementation` configuration using the version catalog.
 *
 * This extension function allows you to add a dependency to the `debugImplementation` configuration
 * of a project by specifying a library reference from the version catalog. It simplifies
 * the addition of debug dependencies by automatically resolving the library version from the catalog.
 *
 * Usage:
 *
 * ```kotlin
 * dependencies {
 *     implementDebugImplementation("some.debug.library")
 * }
 * ```
 *
 * @param versionCatalog Version catalog of this project.
 * @param value The library reference as defined in the version catalog.
 */
@Suppress("unused")
fun DependencyHandlerScope.implementDebugImplementation(
    versionCatalog: VersionCatalog,
    value: String
) {
    add("debugImplementation", versionCatalog.findLibrary(value).get())
}

/**
 * Adds a dependency to the `testImplementation` configuration using the version catalog.
 *
 * This extension function allows you to add a dependency to the `testImplementation` configuration
 * of a project by specifying a library reference from the version catalog. It simplifies
 * the addition of test dependencies by automatically resolving the library version from the catalog.
 *
 * Usage:
 *
 * ```kotlin
 * dependencies {
 *     implementTestDependency("some.test.library")
 * }
 * ```
 *
 * @param versionCatalog Version catalog of this project.
 * @param value The library reference as defined in the version catalog.
 */
fun DependencyHandlerScope.implementTestDependency(versionCatalog: VersionCatalog, value: String) {
    add("testImplementation", versionCatalog.findLibrary(value).get())
}


/**
 * Adds a dependency to the `testRuntimeOnly` configuration using the version catalog.
 *
 * This extension function allows you to add a dependency to the `testRuntimeOnly` configuration
 * of a project by specifying a library reference from the version catalog. It simplifies
 * the addition of test dependencies by automatically resolving the library version from the catalog.
 *
 * Usage:
 *
 * ```kotlin
 * dependencies {
 *     testRuntimeOnly("some.test.library")
 * }
 * ```
 *
 * @param versionCatalog Version catalog of this project.
 * @param value The library reference as defined in the version catalog.
 */
fun DependencyHandlerScope.implementTestRuntimeOnly(versionCatalog: VersionCatalog, value: String) {
    add("testRuntimeOnly", versionCatalog.findLibrary(value).get())
}