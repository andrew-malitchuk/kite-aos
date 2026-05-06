plugins {
    id("convention.feature")
    id("convention.di")
}

android {
    namespace = "data.platform.impl"
}

dependencies {
    implementation(projects.commonCore)
    implementation(projects.dataCore)
    implementation(projects.dataPlatformApi)
}
