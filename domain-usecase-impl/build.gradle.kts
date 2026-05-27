plugins {
    id("convention.library")
    id("convention.di")
}

dependencies {
    implementation(projects.commonCore)
    implementation(projects.domainCore)
    implementation(projects.domainRepositoryApi)
    implementation(projects.domainUsecaseApi)
}
