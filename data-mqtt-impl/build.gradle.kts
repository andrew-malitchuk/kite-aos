plugins {
    id("convention.library")
    id("convention.di")
}

dependencies {
    implementation(projects.dataCore)
    implementation(projects.dataMqttApi)
    implementation(libs.kmqtt.common)
    implementation(libs.kmqtt.client)
    implementation(libs.kotlinx.serialization.json)
}
