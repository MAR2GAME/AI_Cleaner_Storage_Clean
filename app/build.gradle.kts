import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

apply(from = "product-config.gradle")

val disableReleaseResourceShrink =
    providers
        .gradleProperty("disableReleaseResourceShrink")
        .map { it.equals("true", ignoreCase = true) }
        .getOrElse(false)

android {
    namespace = "com.aicleanerstorage.spaceclean"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 29
        targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("boolean", "FAKE_VIRUS_SCAN", "false")
    }

    buildTypes {
        debug {
            // -PfakeVirusScan=true 时才用 testres 里的假扫描器，默认仍走真实云扫描。
            buildConfigField(
                "boolean",
                "FAKE_VIRUS_SCAN",
                providers.gradleProperty("fakeVirusScan").getOrElse("false"),
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = !disableReleaseResourceShrink
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    sourceSets {
        // 只有 debug 构建会把 testres 的假扫描 fixture 打进 APK，release 不包含。
        getByName("debug").assets.srcDir(rootProject.file("testres/fake-virus-fixtures"))
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

ktlint {
    android.set(true)
}

dependencies {
    implementation("com.mar2game.mar2sdk:core:2.0.15")
    implementation("com.mar2game.mar2sdk:impl:2.0.15")
    debugImplementation("com.mar2game.mar2sdk:debug:2.0.15")
    implementation(project(":junkcc"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.unit)
    implementation(files("libs/cloudscan_sdk_5.0.18.20250821.aar"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.kotlinx.coroutines.android)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}
