plugins {
    id("convention.library")
    id("convention.di")
}

dependencies {
    implementation(projects.commonCore)
    implementation(projects.dataCore)
    implementation(projects.dataDatabaseApi)
    implementation(projects.dataPlatformApi)
    implementation(projects.dataPreferencesApi)
    implementation(projects.dataMqttApi)
    implementation(projects.dataRuntimeApi)
    implementation(projects.domainCore)
    implementation(projects.domainRepositoryApi)
}
