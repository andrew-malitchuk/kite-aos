plugins {
    id("convention.feature")
    id("convention.di.android")
}

android {
    namespace = "presentation.feature.main"

    // AGP 9 disables BuildConfig generation for library modules by default.
    // Re-enable here because KioskWebView uses BuildConfig.DEBUG to gate debug logging.
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.androidx.webkit)
    // compileOnly: GeckoViewEngine.kt compiles against GeckoView APIs but the library
    // is NOT bundled. The foss app flavor provides it via fossImplementation in the app module;
    // the gms flavor omits it entirely — gms/GeckoViewEngine.kt is a no-op stub.
    compileOnly(libs.geckoview)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.bundles.arrow)

    implementation(projects.commonCore)
    implementation(projects.domainCore)
    implementation(projects.domainUsecaseApi)
    implementation(projects.presentationCorePlatform)
    implementation(projects.presentationCoreNavigationApi)
    implementation(projects.presentationCoreStyling)
    implementation(projects.presentationCoreUi)
    implementation(projects.presentationCoreLocalisation)

    implementation(libs.orbit.core)
    implementation(libs.orbit.viewmodel)
    implementation(libs.orbit.compose)
    implementation("dev.chrisbanes.haze:haze:1.7.1")
}
