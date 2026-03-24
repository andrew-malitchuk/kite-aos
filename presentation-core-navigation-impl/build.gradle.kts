plugins {
    id("dev.yahk.convention.feature")
}

android {
    namespace = "presentation.core.navigation.impl"
}

dependencies {
    implementation(libs.bundles.navigation)
    implementation(projects.presentationCoreNavigationApi)
    implementation(projects.presentationFeatureMain)
    implementation(projects.presentationFeatureOnboarding)
    implementation(projects.presentationFeatureSettings)
    implementation(projects.presentationFeatureAbout)
    implementation(projects.presentationFeatureApplication)
}
