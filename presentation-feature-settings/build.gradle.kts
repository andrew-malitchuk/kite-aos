plugins {
    id("dev.yahk.convention.feature")
    id("dev.yahk.convention.di.android")
}

android {
    namespace = "presentation.feature.settings"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.bundles.arrow)

    implementation(projects.commonCore)
    implementation(projects.domainCore)
    implementation(projects.domainUsecaseApi)

    implementation(projects.presentationCoreLocalisation)
    implementation(projects.presentationCorePlatform)
    implementation(projects.presentationCoreNavigationApi)
    implementation(projects.presentationCoreStyling)
    implementation(projects.presentationCoreUi)

    implementation("org.orbit-mvi:orbit-core:11.0.0")
    implementation("org.orbit-mvi:orbit-viewmodel:11.0.0")
    implementation("org.orbit-mvi:orbit-compose:11.0.0")
}
