plugins {
    id("convention.feature")
    id("convention.di.android")
}

android {
    namespace = "presentation.feature.host"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.bundles.navigation)
    implementation(projects.commonCore)
    implementation(projects.presentationCoreLocalisation)
    implementation(projects.presentationCoreNavigationApi)
    implementation(projects.presentationCoreNavigationImpl)
    implementation(projects.presentationCoreStyling)
    implementation(projects.presentationCoreUi)
    implementation(projects.presentationCorePlatform)
    implementation(projects.domainCore)
    implementation(projects.domainUsecaseApi)
    implementation(libs.orbit.core)
    implementation(libs.orbit.viewmodel)
    implementation(libs.orbit.compose)
    implementation(libs.androidx.runtime)
}
