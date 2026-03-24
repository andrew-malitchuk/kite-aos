plugins {
    id("dev.yahk.convention.library")
    id("dev.yahk.convention.di")
}

dependencies {
    implementation(projects.commonCore)
    implementation(projects.domainCore)
    implementation(projects.domainRepositoryApi)
    implementation(projects.domainUsecaseApi)
}
