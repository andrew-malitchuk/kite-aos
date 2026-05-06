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

    implementation("org.orbit-mvi:orbit-core:11.0.0")
    implementation("org.orbit-mvi:orbit-viewmodel:11.0.0")
    implementation("org.orbit-mvi:orbit-compose:11.0.0")
    implementation("dev.chrisbanes.haze:haze:1.7.1")
}
