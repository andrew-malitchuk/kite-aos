plugins {
    id("convention.feature")
    id("convention.di.android")
}

android {
    namespace = "common.core.analytics.provider.firebase"
}

dependencies {
    implementation(projects.commonCoreAnalyticsApi)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
}
