plugins {
    id("convention.application")
    id("convention.di.android")
    id("convention.firebase")
}

android {
    namespace = "presentation.core.application"

    buildTypes {
        release {
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = true
                nativeSymbolUploadEnabled = false
            }
        }
    }
}

dependencies {
    // region Core Libraries
    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.bundles.arrow)
    // endregion

    // region Analytics
    implementation(projects.commonCoreAnalyticsApi)
    implementation(projects.commonCoreAnalyticsImpl)
    implementation(projects.commonCoreAnalyticsProviderConsole)
    "gmsImplementation"(projects.commonCoreAnalyticsProviderFirebase)
    // endregion

    // region Data Layer
    implementation(projects.commonCore)
    implementation(projects.dataCore)
    implementation(projects.dataDatabaseApi)
    implementation(projects.dataDatabaseImpl)
    implementation(projects.dataPreferencesApi)
    implementation(projects.dataPreferencesImpl)
    implementation(projects.dataMqttApi)
    implementation(projects.dataMqttImpl)
    implementation(projects.dataPlatformApi)
    implementation(projects.dataPlatformImpl)
    implementation(projects.dataRepositoryImpl)
    // endregion

    // region Domain Layer
    implementation(projects.domainCore)
    implementation(projects.domainRepositoryApi)
    implementation(projects.domainUsecaseApi)
    implementation(projects.domainUsecaseImpl)
    // endregion

    // region Presentation Core
    implementation(projects.presentationCoreLocalisation)
    implementation(projects.presentationCoreNavigationApi)
    implementation(projects.presentationCoreNavigationImpl)
    implementation(projects.presentationCorePlatform)
    implementation(projects.presentationCoreStyling)
    implementation(projects.presentationCoreUi)
    // endregion

    // region Presentation Features
    implementation(projects.presentationFeatureMain)
    implementation(projects.presentationFeatureOnboarding)
    implementation(projects.presentationFeatureSettings)
    implementation(projects.presentationFeatureAbout)
    implementation(projects.presentationFeatureApplication)
    implementation(projects.presentationFeatureHost)
    // endregion

    // region External Libraries
    implementation(libs.androidx.remote.creation.core)
    implementation(libs.androidx.material3)
    // endregion

}
