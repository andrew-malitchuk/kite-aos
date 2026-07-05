plugins {
    id("convention.library")
    id("convention.di")
}

dependencies {
    implementation(projects.commonCore)
    implementation(projects.dataCore)
    implementation(projects.dataRuntimeApi)
}
