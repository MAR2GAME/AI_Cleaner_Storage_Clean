plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.aicleanerstorage.spaceclean.core.presentation"
    compileSdk { version = release(36) { minorApiLevel = 1 } }
    defaultConfig { minSdk = 29 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin { jvmToolchain(11) }

dependencies {
    api(project(":core:contracts"))
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
