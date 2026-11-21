# Cleaner Kit

以 Android Gradle 项目依赖接入，最低支持 API 29。宿主使用仓库的 `gradle/libs.versions.toml` 管理协程、JUnit 和 ktlint 版本。

```kotlin
// settings.gradle.kts
include(":cleaner-kit")

// app/build.gradle.kts
dependencies {
    implementation(project(":cleaner-kit"))
}
```

通过 `CleanerKit.create(context, config)` 创建实例，在一次扫描和删除流程中复用它。`clean` 提供扫描、进度和垃圾清理，`files` 提供文件查询与删除，`mediaDeletion` 负责宿主手动授权。

## 扫描与删除

在协程中调用 `clean.performFullScan()`，通过 `scanProgress` 观察进度。文件查询、扫描和删除在 IO 调度器执行。只提交本次扫描中用户选中的条目。调用 `deleteFileItems(selected) { stopRequested }` 可在条目或批次之间停止，已提交给 Android 的删除请求仍由系统处理。

宿主按系统版本声明并取得相应存储或媒体读取权限。`sharedStorageAccessLimited` 表示扫描范围受限。查询可抛出 `SecurityException` 或 `FileAccessRequiredException`。逐项查看删除状态，仅 `DeleteStatus.Deleted` 计入释放字节。相似照片分组、默认勾选和第三方目录规则由宿主决定。

## 媒体删除授权

自动接入时，在 `CleanerConfig.authorizeMediaDeletion` 中切到主线程，通过 Activity Result 启动传入的 `IntentSender` 并等待用户的同意或拒绝。Kit 负责分批、复核和返回最终结果。

手动接入时，收集垃圾或文件删除结果中的 `authorizationId`，保留每个 ID 与原始条目的对应关系。下面的 `launchAndAwait` 表示宿主提供的 Activity Result 调用。

```kotlin
val authorization = kit.mediaDeletion
try {
    for (batch in ids.distinct().chunked(authorization.maxBatchSize)) {
        val request = authorization.prepare(batch) ?: continue
        val approved = launchAndAwait(request.sender)
        val statuses = authorization.complete(request.requestId, approved)
        // 按返回的条目 ID 和 Deleted 状态更新文件列表及释放量。
        if (!approved) break
    }
} finally {
    authorization.discard(ids)
}
```

Android 10 每次授权一项，同意后复核并重试删除。Android 11 及以上每批最多 200 项，同意后核对系统执行结果。`prepare` 遇到过期 ID、已申请的条目或元数据变化时返回 null，可重新扫描。拒绝也须回传，重复回调返回空映射。ID 仅在当前 Kit 实例内有效，重试同一仓库中的同一条目使旧 ID 失效，清空垃圾扫描会话使该会话的 ID 失效。页面放弃操作时调用 `discard`，进程重启后重新扫描。
