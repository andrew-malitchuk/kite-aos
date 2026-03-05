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
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "kite-aos"

includeBuild("build-logic")

include(":common-core")

include(":data-core")
include(":data-database-api")
include(":data-database-impl")
include(":data-network-api")
include(":data-network-impl")
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


 