package convention.source.conventionplugin.core

import convention.core.ext.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jlleitschuh.gradle.ktlint.KtlintExtension

/**
 * Convention plugin that wires Detekt and Ktlint into every module that applies it.
 *
 * Detekt is configured with `buildUponDefaultConfig = true` and auto-correct
 * enabled, reading its ruleset from `config/detekt/detekt.yml` at the root
 * project level. The `detekt-formatting` artefact is added so that
 * formatting-related rules are included.
 *
 * Ktlint runs in Android mode with console output and strict failure policy.
 * Generated and build output files are excluded from analysis.
 *
 * Both tools are wired into the standard `check` lifecycle task so that
 * `./gradlew check` runs static analysis alongside compilation.
 *
 * @see convention.source.conventionplugin.android.AndroidApplicationConventionPlugin
 * @see convention.source.conventionplugin.android.AndroidFeatureConventionPlugin
 * @see convention.source.conventionplugin.kotlin.KotlinLibraryConventionPlugin
 */
public class CodeQualityConventionPlugin : Plugin<Project> {

    /**
     * Apply Detekt and Ktlint configuration to [target] and wire them into `check`.
     *
     * @param target The Gradle project to configure with code-quality tooling.
     */
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
                // NOTE: Exclude generated and build artefacts to avoid false positives.
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
