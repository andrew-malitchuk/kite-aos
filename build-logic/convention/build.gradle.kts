plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "convention"

java {
    val javaVersion = JavaVersion.toVersion(libs.versions.java.get())

    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.google.services.plugin)
    compileOnly(libs.firebase.crashlytics.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.ktlint.gradle.plugin)
    implementation(libs.compose.compiler.gradle)
}
gradlePlugin {
    plugins {

        //region android
        register("androidApplication") {
            id = "convention.application"
            implementationClass =
                "convention.source.conventionplugin.android.AndroidApplicationConventionPlugin"
        }
        register("androidFeature") {
            id = "convention.feature"
            implementationClass =
                "convention.source.conventionplugin.android.AndroidFeatureConventionPlugin"
        }

        //endregion android

        //region di
        register("kotlinDi") {
            id = "convention.di"
            implementationClass =
                "convention.source.conventionplugin.di.DiConventionPlugin"
        }
        register("androidDi") {
            id = "convention.di.android"
            implementationClass =
                "convention.source.conventionplugin.di.DiAndroidConventionPlugin"
        }
        //endregion di

        //region kotlin
        register("kotlinLibrary") {
            id = "convention.library"
            implementationClass =
                "convention.source.conventionplugin.kotlin.KotlinLibraryConventionPlugin"
        }
        //endregion kotlin

        register("codeQuality") {
            id = "convention.quality"
            implementationClass =
                "convention.source.conventionplugin.core.CodeQualityConventionPlugin"
        }

        //region firebase
        register("firebase") {
            id = "convention.firebase"
            implementationClass =
                "convention.source.conventionplugin.firebase.FirebaseConventionPlugin"
        }
        //endregion firebase
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
    }
}
