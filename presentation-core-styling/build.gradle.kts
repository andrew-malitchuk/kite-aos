plugins {
    id("dev.yahk.convention.feature")
}

android{
    namespace = "presentation.core.styling"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(projects.domainCore)
}