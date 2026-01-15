plugins {
    id("dev.yahk.convention.library")
    id("dev.yahk.convention.di")
}

dependencies {
    implementation(projects.dataCore)
    implementation(projects.dataMqttApi)
    implementation(libs.kmqtt.common)
    implementation(libs.kmqtt.client)
    implementation(libs.kotlinx.serialization.json)
}

