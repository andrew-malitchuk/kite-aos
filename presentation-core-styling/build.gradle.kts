plugins {
    id("convention.feature")
}

android {
    namespace = "presentation.core.styling"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // `api` so consumers reading LocalWindowSizeClass get the WindowSizeClass type
    // transitively. Version is managed by the Compose BOM applied by convention.feature.
    api(libs.material3.window.size)

    implementation(projects.domainCore)
}
