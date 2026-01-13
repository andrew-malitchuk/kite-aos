plugins {
    id("dev.yahk.convention.feature")
    id("dev.yahk.convention.di.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "data.database.impl"
}

dependencies {
    implementation(projects.commonCore)
    implementation(projects.dataCore)
    implementation(projects.dataPreferencesApi)
    implementation(libs.datastore)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(projects.dataDatabaseApi)
}
    