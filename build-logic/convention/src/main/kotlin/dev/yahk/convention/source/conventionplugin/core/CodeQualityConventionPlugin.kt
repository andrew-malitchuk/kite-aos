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
 * - **Detekt**: Static code analysis with `detekt-formatting` rules.
 * - **Ktlint**: Kotlin linter and formatter.
 *
 * Key Gradle tasks:
 * - `./gradlew detekt`: Performs static analysis and reports issues.
 * - `./gradlew ktlintCheck`: Checks Kotlin code style.
 * - `./gradlew ktlintFormat`: Automatically fixes Kotlin style violations.
 * - `./gradlew check`: Standard verification task that now depends on `detekt` and `ktlintCheck`.
 */
public class CodeQualityConventionPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply("io.gitlab.arturbosch.detekt")
        pluginManager.apply("org.jlleitschuh.gradle.ktlint")

        extensions.configure<DetektExtension> {
            buildUponDefaultConfig = true
            autoCorrect = true
            config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
        }

        extensions.configure<KtlintExtension> {
            android.set(true)
            outputToConsole.set(true)
            ignoreFailures.set(false)
            filter {
                exclude { it.file.path.contains("build/") }
                exclude { it.file.path.contains("generated/") }
            }
        }

        dependencies {
            "detektPlugins"(libs.findLibrary("detekt-formatting").get())
        }

        tasks.named("check").configure {
            dependsOn("detekt")
            dependsOn("ktlintCheck")
        }
    }
}
