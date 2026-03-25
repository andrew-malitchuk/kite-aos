plugins {
    id("dev.yahk.convention.feature")
}

android {
    namespace = "presentation.core.localisation"
}
dependencies {
    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
}
