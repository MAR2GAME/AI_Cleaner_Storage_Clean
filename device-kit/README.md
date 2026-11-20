# Device Kit

以 Android Gradle 项目依赖接入，最低支持 API 29。宿主使用仓库的 `gradle/libs.versions.toml` 管理协程、JUnit 和 ktlint 版本。

```kotlin
// settings.gradle.kts
include(":device-kit")

// app/build.gradle.kts
dependencies {
    implementation(project(":device-kit"))
}
```

`DeviceKit.create(context)` 返回 `DeviceInfoRepository`，读取硬件、内存、存储和即时电池数据。例如 `repository.batteryInfo()` 返回电池快照，`repository.internalStorageInfo()` 返回内部存储统计。

这些读数使用 Android 提供的设备信息和设备允许读取的系统文件。宿主可在后台线程调用同步读取接口，避免硬件枚举或文件访问占用主线程。`readCpuUsagePercent(previousPercent)` 是挂起函数，创建时可传入 IO 调度器。

传感器、电流和 CPU 温度的可用性取决于硬件和系统访问限制。处理可空读数，并在展示前检查模型中的有效性字段。电池历史持久化、周期采样和服务生命周期由宿主管理。
