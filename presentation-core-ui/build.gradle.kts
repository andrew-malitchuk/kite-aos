plugins {
    id("dev.yahk.convention.feature")
}

android {
    namespace = "presentation.core.ui"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.stately.collections)
    implementation(libs.atomicfu)
    implementation("com.airbnb.android:lottie-compose:6.7.1")

    implementation(projects.domainCore)
    implementation(projects.presentationCoreStyling)
    implementation(libs.androidx.core.splashscreen)
    implementation("dev.chrisbanes.haze:haze:1.7.1")
}
