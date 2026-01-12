plugins {
    id("dev.yahk.convention.library")
}

dependencies {
    implementation(projects.dataCore)
    implementation(libs.androidx.room.runtime)
}
