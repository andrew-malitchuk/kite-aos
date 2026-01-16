plugins {
    id("dev.yahk.convention.feature")
    id("dev.yahk.convention.di")
}

android {
    namespace = "data.platform.impl"
}

dependencies {
    implementation(projects.commonCore)
    implementation(projects.dataCore)
    implementation(projects.dataPlatformApi)
}