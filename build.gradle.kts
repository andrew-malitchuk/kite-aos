// Top-level build file where you can add configuration options common to all sub-projects/modules.

// Gradle 9: test task now fails when zero tests are discovered.
// Suppress globally until test suites are added.
subprojects {
    tasks.withType<Test>().configureEach {
        failOnNoDiscoveredTests = false
    }
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.kotlin.serialization)
        classpath(libs.protobuf.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.com.google.devtools.ksp) apply false
    alias(libs.plugins.org.jetbrains.kotlin.plugin.compose) apply false
    alias(libs.plugins.com.google.gms.google.services) apply false
    alias(libs.plugins.com.google.firebase.crashlytics) apply false
}
