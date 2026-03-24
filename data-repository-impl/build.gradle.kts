plugins {
    id("dev.yahk.convention.library")
    id("dev.yahk.convention.di")
}

dependencies {
    implementation(projects.commonCore)
    implementation(projects.dataCore)
    implementation(projects.dataDatabaseApi)
    implementation(projects.dataPlatformApi)
    implementation(projects.dataPreferencesApi)
    implementation(projects.dataMqttApi)
    implementation(projects.domainCore)
    implementation(projects.domainRepositoryApi)
}
