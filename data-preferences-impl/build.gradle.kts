plugins {
    id("dev.yahk.convention.feature")
    id("dev.yahk.convention.di.android")
    id("com.google.protobuf")
}

android{
    namespace = "data.preferences.impl"
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.20.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    implementation(projects.commonCore)
    implementation(projects.dataCore)
    implementation(projects.dataPreferencesApi)
    implementation(libs.datastore)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.kotlinx.serialization.json)
}