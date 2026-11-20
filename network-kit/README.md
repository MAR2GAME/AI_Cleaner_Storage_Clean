# Network Kit

以 Android Gradle 项目依赖接入，最低支持 API 29。宿主使用仓库的 `gradle/libs.versions.toml` 管理协程、JUnit 和 ktlint 版本。

```kotlin
// settings.gradle.kts
include(":network-kit")

// app/build.gradle.kts
dependencies {
    implementation(project(":network-kit"))
}
```

`NetworkKit.create(context, endpoints)` 返回 `NetworkRepository`，测速地址由可选的 `NetworkSpeedEndpoints` 配置。即时网络状态为同步快照，流量统计、测速和 Wi-Fi 扫描使用挂起函数并在 IO 调度器执行，界面更新由宿主切回主线程。

网络状态和 Wi-Fi 信息分别需要 `ACCESS_NETWORK_STATE` 和 `ACCESS_WIFI_STATE`，测速需要 `INTERNET`。SSID 可见性还受位置权限和系统位置设置影响。按应用读取流量需要声明 `PACKAGE_USAGE_STATS` 并由用户授予使用情况访问权限。宿主可调用 `hasNetworkUsageAccess()` 检查。

`NetworkInfo.transport` 返回 `NetworkTransport`，SSID 不可用时返回 null。`SpeedTestPhase` 描述测速阶段，速率单位为 Mbps，null 表示该方向缺失，0.0 表示零速率。`NetworkSpeed.measured` 区分探针实测和系统带宽估算。由宿主格式化数值及标签。

流量归属为 `TrafficSource.App` 或 `TrafficSource.SystemAndUnknown`。读取受限时检查 `needsUsageAccess` 和 `isToday`，回退统计可能是设备累计值。宿主按选中总量合并未知流量和未归属字节。
