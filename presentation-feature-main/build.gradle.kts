plugins {
    id("dev.yahk.convention.feature")
    id("dev.yahk.convention.di.android")
}

android {
    namespace = "presentation.feature.main"
}

dependencies {
    implementation(libs.core.ktx)
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