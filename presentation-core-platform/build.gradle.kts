plugins {
    id("convention.feature")
    id("convention.di.android")
}

android {
    namespace = "presentation.core.platform"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.lifecycle.service)
    implementation(projects.domainCore)
    implementation(projects.domainUsecaseApi)
    implementation(projects.presentationCoreLocalisation)

    // UVC USB-webcam support — consumed by UvcMotionSource (src/main). Available on every variant
    // so phones/tablets with a USB-OTG webcam can use the "External" camera choice, not just TV.
    // libausbc bundles a full demo UI (camera preview fragments, RTMP push, image loading),
    // dragging in transitives that only ever lived on the now-dead JCenter and resolve nowhere
    // today (com.gyf.immersionbar, com.zlc.glide:webpdecoder) plus heavy ones we don't need
    // (glide, mmkv). We only use the headless frame-callback API, so those are excluded. The
    // UVC engine itself (libnative/libuvc/libuvccommon + xlog) resolves fine from JitPack.
    implementation(libs.androidusbcamera.libausbc) {
        exclude(group = "com.gyf.immersionbar")
        exclude(group = "com.zlc.glide")
        exclude(group = "com.github.bumptech.glide")
        exclude(group = "com.tencent", module = "mmkv")
    }
    // Explicit: libausbc exposes com.serenegiant.usb.UsbControlBlock in its public callback API
    // but scopes libuvc as `implementation`, keeping that type off the compile classpath.
    implementation(libs.androidusbcamera.libuvc)
}
