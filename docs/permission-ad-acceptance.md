# 权限与广告框架验收记录

日期：2026-11-04。

当前结论：**静态检查通过；新增 Host 集成测试限定编译通过、尚未运行；正式 Gradle 验证被 loopback 错误阻塞，APK 构建、安装和真机功能验收未执行。**

## 代码与环境

- Git HEAD：`ff262a180a798cc29501053e7c29bcec20b1a3ff`。本记录针对该提交及当前未提交工作树，不是仅针对 HEAD。
- 开始本轮时已有权限快照、Host 回调归属、通知单一计时、Route 退出收敛、相关单元测试、MVI 规范与边界脚本等未提交改动；全部保留。
- 本轮新增 `AppPermissionHostTest`、本记录及验证日志，更新架构文档；没有修改生产接口或业务实现，也没有新增广告抽象。
- Gradle Wrapper：9.4.1；AGP：9.2.1。当前 shell 默认 JAVA_HOME 指向 JDK 17，本次 Gradle 子进程使用 Android Studio JBR 21.0.10；未修改系统配置。
- 设备：V2352A / PD2352，Android API 36，序列号 `10CF1E06F2002G6`，ADB 状态 `device`。
- 设备已有 `com.aicleanerstorage.spaceclean`，versionCode 8 / versionName 1.0.7。它不代表当前工作树的构建。

## 正式构建尝试

PowerShell 使用 TcpListener/TcpClient 在 `127.0.0.1:64310` 成功监听、连接并接受连接。这只证明 .NET 的 IPv4 loopback 可用，不证明 Java/Gradle 的内部通信正常。

随后仅执行一次：

```powershell
$env:JAVA_HOME = 'C:/Program Files/Android/Android Studio/jbr'
./gradlew.bat :app:testDebugUnitTest `
  --tests '*PermissionInterceptorTest' `
  --tests '*NotificationPermissionStateMachineTest' `
  --tests '*NotificationPermissionEffectsTest' `
  --tests '*AdInterceptorTest' `
  --tests '*AdQueueTest' `
  --tests '*AdvertiseAdDriverTest' `
  --tests '*ColdStartAdsTest' `
  --tests '*AppFeatureFlowTest' `
  --tests '*FeatureEntryNavigatorTest' `
  --tests '*JunkClean*Test' `
  --tests '*JunkDeleteAuthorizationLauncherTest' `
  :app:compileDebugKotlin --console=plain
```

退出码 1，错误为 `java.io.IOException: Unable to establish loopback connection`；日志没有任务执行或测试结果。按约定停止，不以 `--stacktrace`、daemon 参数或其他 JDK 反复尝试。

证据：[Gradle 日志](verification/permission-ad-2026-11-04/gradle.txt)。未执行 `assembleDebug`、`connectedDebugAndroidTest`、安装或任何 Release/全量测试。

## 新增 Android 集成覆盖

`app/src/androidTest/java/com/aicleanerstorage/spaceclean/app/permission/AppPermissionHostTest.kt` 包含 10 个用例。使用真实 Host、PermissionInterceptor 和通知 ViewModel；测试 ActivityResultRegistry 记录启动阶段并提供受控结果；通过隔离 Koin、独立测试偏好文件和测试 Context 提供授权事实，不打开系统页面或修改设备权限。

| 用例 | 覆盖行为 | 本轮状态 |
| --- | --- | --- |
| explanationCancellationCompletesOnceWithoutLaunchingSystem | 说明弹窗取消返回一次 Denied，不启动系统页面 | 编译通过，未运行 |
| confirmationAndSynchronousRuntimeCallbackLaunchOnlyOnce | 确认仅启动一次；启动前已进入等待态；同步及重复回调不重复完成 | 编译通过，未运行 |
| settingsLifecycleAndActivityResultCompleteOnlyOnce | 暂停/恢复与 ActivityResult 重复通知仅完成一次 | 编译通过，未运行 |
| settingsReturnWithoutGrantDeniesOnce | 设置页返回未授权返回一次 Denied | 编译通过，未运行 |
| earlierSettingsResultCannotFinishNextPermissionStep | 多权限保持顺序，旧 Launcher 迟到结果不能结束下一步骤 | 编译通过，未运行 |
| cancellationBeforeLaunchRemovesExplanation | 启动前取消释放请求并移除说明弹窗 | 编译通过，未运行 |
| cancellationAfterLaunchStaysBusyUntilSystemReturns | 启动后取消仍 Busy，返回后静默释放 | 编译通过，未运行 |
| removingHostCancelsBusinessAndIgnoresLateSystemResult | 移除 Host 取消业务等待，迟到结果不恢复业务 | 编译通过，未运行 |
| notificationPromptWaitsBehindBusinessPermission | 两种弹窗互斥；通知提示实际显示后才记录频率 | 编译通过，未运行 |
| pausedHomeDoesNotPromptAndLeavingHomePreventsResumePrompt | 暂停时不提示，离开首页后恢复也不提示 | 编译通过，未运行 |

测试暂限定 API 33+，匹配本轮设备及通知权限分支；不声称覆盖 API 29 存储运行时权限。已有 NotificationPermissionEffectsTest 覆盖等待中暂停、重新恢复后的完整 350ms 延迟。

本地 Kotlin 2.2.10 编译器使用缓存依赖、Compose 编译插件及上一轮 app 编译产物，成功编译新增集成测试文件。仅有现有 Compose 测试 Rule 的弃用警告。此结果不验证 Android 测试 APK 打包、依赖解析、设备调度或测试断言。

证据：[限定编译日志](verification/permission-ad-2026-11-04/host-cached-compile.txt)。本轮没有重新运行上一轮的 69 个 JVM 测试，不将历史通过数量记为本轮正式结果。

## 静态检查

- `scripts/check-mvi-boundaries.ps1`：通过，48 个限定 Kotlin 文件。
- `scripts/check-permission-ad-boundaries.ps1`：通过，412 个生产 Kotlin 文件；唯一 Host、Gateway 绑定、AdDriver 和旧框架残留检查通过。
- `git diff --check`：通过。

证据：[边界检查日志](verification/permission-ad-2026-11-04/boundaries.txt)。静态检查不替代运行验收。

## 设备只读基线与未覆盖项

本轮只执行 `adb devices`、`getprop`、目标应用的 `dumpsys package` 和 `cmd appops get`；未启动或安装应用、未修改权限、未创建或删除测试文件。

| 项目 | 操作前读取结果 |
| --- | --- |
| POST_NOTIFICATIONS | granted=true |
| ACCESS_FINE_LOCATION | granted=false |
| MANAGE_EXTERNAL_STORAGE | UID mode: allow；包级记录: default |
| GET_USAGE_STATS | default |
| SYSTEM_ALERT_WINDOW | default |

权限、偏好和历史均未由本轮设备操作修改，无需恢复。未读取通知监听器设置，因此不判断其授权状态。

以下项目全部为**未执行**：垃圾清理拒绝权限后退出一次及扫描调用为零、授权后单任务、多权限实际设置页、通知实际弹窗、真实广告关闭或无填充路径、冷启动 consent/开屏、外部返回抑制、测试文件的删除授权与部分失败汇总。设备当前已有全部文件访问权限，不能用现有安装结果推断未授权流程。

## 恢复验收的条件和顺序

1. 有证据确认 Java/Gradle loopback 问题恢复后，正式运行上述限定测试与 Kotlin 编译；未恢复不重复执行。
2. 通过后构建 Debug 与 AndroidTest APK，只选择 AppPermissionHostTest 运行集成测试。安装前确认签名兼容，冲突时保留旧应用，不卸载或清除数据。
3. 记录当时权限基线，执行计划内系统权限、通知与广告路径，清理只选择专门创建的测试文件；恢复能恢复的权限，记录不可恢复的提示频率和历史变化。
4. 无广告填充仅记为“无广告继续通过”；没有实际关闭回调证据时保留真实 SDK 路径为未覆盖。任何扫描调用次数或重复导航断言都必须有测试或日志证据，不能只凭界面推断。
5. 更新本记录各项状态；只有实际运行成功才标记正式构建、集成测试或设备验收通过。
