plugins {
    id("com.android.library")
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.aicleanerstorage.devicekit"
    compileSdk {
        version = release(36) { minorApiLevel = 1 }
    }

    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    jvmToolchain(11)
}

ktlint {
    android.set(true)
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}
