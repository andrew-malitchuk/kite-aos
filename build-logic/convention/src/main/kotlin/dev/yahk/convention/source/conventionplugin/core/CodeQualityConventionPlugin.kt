package dev.yahk.convention.source.conventionplugin.core

import dev.yahk.convention.core.ext.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jlleitschuh.gradle.ktlint.KtlintExtension

/**
 * A convention plugin for configuring code quality tools like Detekt and Ktlint.
 *
 * This plugin applies and configures:
 * - **Detekt**: Static code analysis with `detekt-formatting` rules, auto-correct enabled,
 *   and a shared configuration file at `config/detekt/detekt.yml`.
 * - **Ktlint**: Kotlin linter and formatter with Android code style, console output,
 *   and exclusions for generated/build directories.
 *
 * The plugin also wires both tools into the standard `check` lifecycle task, so that
 * running `./gradlew check` automatically invokes both `detekt` and `ktlintCheck`.
 *
 * Key Gradle tasks:
 * - `./gradlew detekt`: Performs static analysis and reports issues.
 * - `./gradlew ktlintCheck`: Checks Kotlin code style.
 * - `./gradlew ktlintFormat`: Automatically fixes Kotlin style violations.
 * - `./gradlew check`: Standard verification task that now depends on `detekt` and `ktlintCheck`.
 *
 * Registered as `dev.yahk.convention.quality` in the Gradle plugin registry.
 *
 * @see DetektExtension
 * @see KtlintExtension
 * @since 0.0.1
 */
public class CodeQualityConventionPlugin : Plugin<Project> {

    /**
     * Applies the code quality convention to the given [target] project.
     *
     * @param target The project to which this convention plugin is applied.
     * @since 0.0.1
     */
    override fun apply(target: Project): Unit = with(target) {
        // Apply Detekt and Ktlint Gradle plugins
        pluginManager.apply("io.gitlab.arturbosch.detekt")
        pluginManager.apply("org.jlleitschuh.gradle.ktlint")

        extensions.configure<DetektExtension> {
            // Extend the default Detekt rule set rather than replacing it
            buildUponDefaultConfig = true
            // Allow Detekt to auto-correct fixable issues
            autoCorrect = true
            // Point to the shared Detekt configuration at the root project level
            config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
        }

        extensions.configure<KtlintExtension> {
            // Use Android-specific Kotlin code style conventions
            android.set(true)
            // Print lint results to the console for quick feedback
            outputToConsole.set(true)
            // Fail the build on Ktlint violations
            ignoreFailures.set(false)
            filter {
                // Exclude generated and build directories from Ktlint analysis
                exclude { it.file.path.contains("build/") }
                exclude { it.file.path.contains("generated/") }
            }
        }

        dependencies {
            // Add the Detekt formatting rule set for additional style enforcement
            "detektPlugins"(libs.findLibrary("detekt-formatting").get())
        }

        // Wire Detekt and Ktlint into the standard "check" lifecycle task
        tasks.named("check").configure {
            dependsOn("detekt")
            dependsOn("ktlintCheck")
        }
    }
}
