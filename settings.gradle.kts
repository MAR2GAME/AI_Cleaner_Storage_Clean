// 国内镜像优先：AGP / Kotlin 插件与依赖都先走阿里云，原生仓库留作兜底。
pluginManagement {
    repositories {
        maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
        maven(url = "https://maven.aliyun.com/repository/google")
        maven(url = "https://maven.aliyun.com/repository/public")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://maven.aliyun.com/repository/google")
        maven(url = "https://maven.aliyun.com/repository/public")
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven {
            name = "Mar2SdkGitHubPackages"
            url = uri("https://maven.pkg.github.com/mar2game/mar2sdk")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: providers.environmentVariable("GITHUB_ACTOR").orNull
                    ?: "x-access-token"
                password = providers.gradleProperty("gpr.key").orNull
                    ?: providers.environmentVariable("GITHUB_TOKEN").orNull.orEmpty()
            }
            content { includeGroup("com.mar2game.mar2sdk") }
        }
        maven(url = "https://maven.singular.net/")
        maven(url = "https://cboost.jfrog.io/artifactory/chartboost-ads/")
        maven(url = "https://android-sdk.is.com/")
        maven(url = "https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
        maven(url = "https://artifact.bytedance.com/repository/pangle")
        maven(url = "https://artifacts.applovin.com/android")
        maven(url = "https://jfrog.anythinktech.com/artifactory/overseas_sdk") {
            content { includeGroup("com.anythink.sdk") }
        }
        flatDir {
            dirs("app/libs")
        }
    }
}

rootProject.name = "AICleanerStorageClean"
include(":app")
include(":junkcc")
