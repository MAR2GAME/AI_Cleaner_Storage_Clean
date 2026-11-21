# 通知关闭不再阻断电池采样

> 状态：已实施（2026-11-21），本文与代码一致
> 关联：[permission-layer-review.md](permission-layer-review.md)

## 1. 决定

**通知被用户关闭时，后台电池采样继续。** 通知权限不参与“采样要不要跑”，也不参与“前台服务要不要活”。

## 2. 原先的问题

“通知权限”被当成采样前置条件，出现在四处：

```kotlin
BatterySamplingCoordinator.syncSamplingState()   // 设置开 && 通知权限
SyncBackgroundBatterySamplingUseCase.reconcile() // 设置开 && 通知权限
AppRuntimeSync.syncAppRuntime()                  // armed || (设置开 && 通知权限)
AppRuntimeService.isBatterySamplingEnabled()     // 设置开 && 通知权限
```

而 `AppConfig.hasPostNotificationsPermission` 用的是 `checkSelfPermission(POST_NOTIFICATIONS)`，在 Android 10–12 上恒为 `true`（13 之前没有该运行时权限）。结果是同一段逻辑在 10–12 挡不住、13+ 才挡得住，同时 `AppConfig` 与 `AppPermission` 里各有一份实现。

## 3. 最终改动

**删除**

| 文件 | 原因 |
|---|---|
| `app/AppConfig.kt` | 权限函数无人使用；法律链接改用 `BuildConfig` |
| `app/background/AppRuntimeSync.kt` | 并入 `AppRuntimeService.reconcile()`，避免纯转发函数 |
| `app/background/BatterySamplingCoordinator.kt` | 纯转发，内联进 Service |
| `domain/usecase/BackgroundBatterySampling.kt` | 判据只剩一个条件，帮助函数无价值 |

**修改**

| 文件 | 改动 |
|---|---|
| `app/background/AppRuntimeService.kt` | 直接持有 sampler；新增 `reconcile()`；`shouldRunRuntime` 成为唯一判据 |
| `domain/usecase/SyncBackgroundBatterySamplingUseCase.kt` | 两个 lambda 合并为单个 `syncRuntime`（原 enable/disable 已同义） |
| `app/di/DataModule.kt`、`app/di/PresentationModule.kt` | 改调 `AppRuntimeService.reconcile(...)` |
| `app/root/AppNavHost.kt` | 4 处 `AppConfig.*_URL` → `BuildConfig.*_URL` |
| `app/view/common/permission/NotificationPermissionPrompt.kt` | 从 `app/permission/` 移入；删除空的 `onGranted` 回调 |
| `app/root/AppRootView.kt`、`app/MainActivity.kt` | 同步去掉 `onNotificationPermissionGranted` 参数 |

**不动**

- `NotificationPermissionPrompt` 的申请时机与频率：首次启动即申请，低于 33 每日一次引导。
- `NotificationPermissionDialog`（仍在 `base/designsystem/component/`）。
- `AppPermission`。

## 4. 目录结构

```
app/src/main/java/com/aicleanerstorage/spaceclean/
├── app/
│   ├── MainActivity.kt                    AppRootView() 无参
│   ├── background/
│   │   ├── AppRuntimeService.kt           唯一 FGS；reconcile + shouldRunRuntime
│   │   ├── AppLockMonitor.kt
│   │   ├── AppLockPackageEvents.kt
│   │   ├── ForegroundNotification.kt
│   │   └── PinAttemptPolicy.kt
│   ├── di/{DataModule,PresentationModule}.kt
│   ├── root/{AppRootView,AppNavHost}.kt
│   └── view/common/permission/
│       └── NotificationPermissionPrompt.kt
├── base/designsystem/component/
│   └── NotificationPermissionDialog.kt    纯 UI，不依赖 domain
└── domain/usecase/
    └── SyncBackgroundBatterySamplingUseCase.kt
```

## 5. 关键代码

### 5.1 `AppRuntimeService`

```kotlin
class AppRuntimeService : Service() {
    // ...
    private fun sync() {
        appLock.sync()
        syncBatterySampling()
        if (!shouldRunRuntime(appLockRepository, settingsRepository)) stopSelf()
    }

    /** 后台历史只看用户开关；通知被关只是看不见提示，不该停采样。 */
    private fun syncBatterySampling() {
        val enabled = settingsRepository.isBackgroundBatterySamplingEnabled()
        runCatching {
            if (enabled) {
                batteryHistorySampler.start(BatteryHistoryOwner.Service)
            } else {
                batteryHistorySampler.stop(BatteryHistoryOwner.Service)
            }
        }
    }

    companion object {
        /** 让服务与当前设置对齐；调用方不直接 start/stop。 */
        fun reconcile(
            context: Context,
            appLockRepository: AppLockRepository,
            settingsRepository: SettingsRepository,
        ) {
            if (shouldRunRuntime(appLockRepository, settingsRepository)) start(context) else stop(context)
        }
    }
}

/** 服务存在的唯一两个理由。 */
private fun shouldRunRuntime(
    appLockRepository: AppLockRepository,
    settingsRepository: SettingsRepository,
): Boolean =
    AppLockMonitoringRule.shouldMonitorApps(appLockRepository) ||
        settingsRepository.isBackgroundBatterySamplingEnabled()
```

### 5.2 `SyncBackgroundBatterySamplingUseCase`

```kotlin
class SyncBackgroundBatterySamplingUseCase(
    private val settingsRepository: SettingsRepository,
    private val syncRuntime: () -> Unit,
) {
    fun activate() {
        settingsRepository.enableBackgroundBatterySampling()
        reconcile()
    }

    fun reconcile() = syncRuntime()
}
```

### 5.3 `DataModule`

```kotlin
SyncBackgroundBatterySamplingUseCase(
    settingsRepository = settingsRepository,
    syncRuntime = { AppRuntimeService.reconcile(context, appLockRepository, settingsRepository) },
)
```

### 5.4 `AppNavHost`

```kotlin
termsUrl = BuildConfig.TERMS_OF_SERVICE_URL,
privacyUrl = BuildConfig.PRIVACY_POLICY_URL,
```

## 6. 行为

| 场景 | 通知开 | 通知关 |
|---|---|---|
| 后台采样开关开 | 采样，常驻通知可见 | **采样继续**，常驻通知不可见 |
| 后台采样开关关 | 不采样，服务可因 App Lock 存活 | 同左 |
| 服务存活判定 | App Lock 武装 或 采样开 | 同左 |
| App Lock | 不受影响 | 不受影响 |
| 首次启动请求通知权限 | 仍会请求 | 拒绝后不再弹 |

## 7. 风险

1. Play 审核：`specialUse` + 通知不可见，审核可能质疑必要性；产品/合规取舍。
2. 部分 ROM 在通知不可见时更激进地清理前台服务，后台曲线会断档。
3. 用户没有“后台正在采样”的提示。可选补救（未做）：电池页顶部提示通知被关闭。

## 8. 验证

1. `grep -r "AppConfig\|syncAppRuntime\|BatterySamplingCoordinator" app/src` = 0；
2. `:app:testDebugUnitTest`：26 suites / 79 tests 通过；
3. `:app:assembleDebug` 通过；
4. 真机（未执行，无设备）：Android 12/13 关通知 → 开电池页 → 服务在跑、通知栏无提示、曲线继续。

## 9. 收敛过程（记录）

1. 最初方案给 `AppPermission` 加 `isActive()`；确定“通知不阻断采样”后，整个判据删除，该函数不再需要。
2. 删除通知门禁后，`SyncBackgroundBatterySamplingUseCase` 的 enable/disable 两个 lambda 变成同一个函数，if/else 合并为单个 `syncRuntime`。
3. 抽出 `shouldRunRuntime` 后 `syncAppRuntime` 退化为纯转发，遂并入 `AppRuntimeService.reconcile()`，`AppRuntimeSync.kt` 删除。
4. `BatterySamplingCoordinator` 同样被判定为纯转发，逻辑内联进 Service 后删除。
