plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "dev.yahk.convention"

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
    implementation(libs.compose.compiler.gradle)
}
gradlePlugin {
    plugins {

        //region android
        register("androidApplication") {
            id = "dev.yahk.convention.application"
            implementationClass =
                "dev.yahk.convention.source.conventionplugin.android.AndroidApplicationConventionPlugin"
        }
        register("androidFeature") {
            id = "dev.yahk.convention.feature"
            implementationClass =
                "dev.yahk.convention.source.conventionplugin.android.AndroidFeatureConventionPlugin"
        }

        //endregion android

        //region di
        register("kotlinDi") {
            id = "dev.yahk.convention.di"
            implementationClass =
                "dev.yahk.convention.source.conventionplugin.di.DiConventionPlugin"
        }
        register("androidDi") {
            id = "dev.yahk.convention.di.android"
            implementationClass =
                "dev.yahk.convention.source.conventionplugin.di.DiAndroidConventionPlugin"
        }
        //endregion di

        //region kotlin
        register("kotlinLibrary") {
            id = "dev.yahk.convention.library"
            implementationClass =
                "dev.yahk.convention.source.conventionplugin.kotlin.KotlinLibraryConventionPlugin"
        }
        //endregion kotlin
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
    }
}