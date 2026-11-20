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
        flatDir {
            dirs("app/libs")
        }
    }
}

rootProject.name = "AICleanerStorageClean"
include(":app")
include(":junkcc")
