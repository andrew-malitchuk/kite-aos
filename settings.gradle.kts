pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.mozilla.org/maven2/")
    }
    components {
        // GeckoView's module metadata declares play-services-fido as a required
        // dependency (in its debug runtime variant) which ends up in the foss APK.
        // Strip all com.google.android.gms transitive deps from GeckoView here so
        // that the foss build produces a GMS-free APK.
        withModule("org.mozilla.geckoview:geckoview") {
            allVariants {
                withDependencies {
                    removeAll { it.group == "com.google.android.gms" }
                }
            }
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "kite-aos"

includeBuild("build-logic")

include(":common-core")
include(":common-core-analytics-api")
include(":common-core-analytics-impl")
include(":common-core-analytics-provider-console")
include(":common-core-analytics-provider-firebase")

include(":data-core")
include(":data-database-api")
include(":data-database-impl")
include(":data-mqtt-api")
include(":data-mqtt-impl")
include(":data-platform-api")
include(":data-platform-impl")
include(":data-preferences-api")
include(":data-preferences-impl")
include(":data-repository-impl")

include(":domain-core")
include(":domain-repository-api")
include(":domain-usecase-api")
include(":domain-usecase-impl")

include(":presentation-core-application")
include(":presentation-core-localisation")
include(":presentation-core-navigation-api")
include(":presentation-core-navigation-impl")
include(":presentation-core-platform")
include(":presentation-core-styling")
include(":presentation-core-ui")

include(":presentation-feature-main")
include(":presentation-feature-onboarding")
include(":presentation-feature-settings")
include(":presentation-feature-about")
include(":presentation-feature-application")
include(":presentation-feature-host")


 