# 单模块、路由对齐、权限退役与广告/埋点拆除方案（v2）

- 状态：方案 v2，已按 2026-11-20 的 8 项决策修订
- 参考基准：`D:\pros\my\PDFScanRead\AGENTS.md`、`docs/rules/00-precedence.md`、`base/navigation/*`、`app/root/*`
- 适用工程：`AI_Cleaner_Storage_Clean`

## 0. 决策落地

| # | 决策 | 落地方式 |
| --- | --- | --- |
| 1 | 埋点不保留 | 删除 `core:contracts/analytics`、`app/analytics`、`app/platform/analytics` 和全部 `AnalyticsReporter` 参数/调用 |
| 2 | Crashlytics 不保留 | 删除 `google-services` / `firebase-crashlytics` 插件与依赖；`google-services.json` 文件保留但不再参与构建 |
| 3 | 推送、Remote Config、UMP、应用更新不保留 | 随 `:advertise` 一起删除 |
| 4 | 权限 UX 接受直接系统页 | 删除权限解释弹窗；Route 直接拉系统权限/设置页 |
| 5 | 文件管理/病毒流程用显式宿主 | 删除 `FeatureOwnerRegistry`，新增 `AppFlowHost` 显式解析流程根 entry |
| 6 | 单模块 | 合并 `core:*`、`feature:*` 到 `:app`；`:junkcc` 按第 8 项保留为唯一第三方库模块 |
| 7 | Gate 层删除 | 删除 `IntentGate`、`GateResult`、`IntentExecutor.gates`、`ExecutionResult.Rejected/Busy/Cancelled` |
| 8 | `PDFfox/`、`:junkcc` 不动 | 不进入任何提交；`:junkcc` 保留 |

执行顺序调整为：

```text
P0 基线 → P1 单模块合并 → P2 广告/埋点/Firebase 拆除
   → P3 权限与 Gate 拆除 → P4 路由对齐 → P5 文档与门禁
```

先合并模块，再做其余三件事：后续所有改动只在一个 Gradle 模块里，依赖边界、资源合并和编译验证都更简单。

## 1. 目标与非目标

### 1.1 目标

1. `:app` 成为唯一第一方模块，`core:*` 与 `feature:*` 变成 `:app` 内的包；`settings.gradle.kts` 只保留 `:app` 和 `:junkcc`。
2. 导航骨架与 PDFScanRead 一致：纯类型 `AppDestination`、单 `AppNavHost`、`AppNavigator` 只有 `navigate / replace / resetToRoot / back`，页面不拿 `NavHostController`。
3. 权限只保留 `AppPermission` + `rememberPermissionRequest`，副作用归 Route；删除全局拦截器、Host、解释弹窗和 Gate 层。
4. 删除 `:advertise`、`com.pdffox.adv` 接入、埋点、Crashlytics、推送、Remote Config、UMP、应用更新；保留广告/产品配置源文件。
5. 文档、检查脚本、测试与最终代码一致。

### 1.2 非目标

- 不改包名。合并后仍保留 `com.aicleanerstorage.spaceclean.core.*` / `.feature.*` / `.app.*`，不做 `base/domain/data` 的包级搬迁。
- 不把 MVI `Intent/Effect` 改名为 `Action/Event`；现有 `docs/mvi-architecture.md` 继续有效。
- 不改 UI、页面流程、资源内容、产品配置值、签名。
- 不合并 `:junkcc`，不处理 `PDFfox/`。
- 不引入新框架。

## 2. 参考基准（与本次相关的硬规则）

| 主题 | PDFScanRead 规则 | 本项目落地 |
| --- | --- | --- |
| 目的地 | `sealed interface`，只有 `data object/data class`，禁止字符串拼接 | `AppDestination` 去掉 `route/featureKey/notificationAliases` |
| 导航器 | 只有 `navigate / replace / resetToRoot / back` | `AppNavigator` 收敛为 4 个方法，实现直接包 `NavHostController` |
| 注册 | `app/root/AppNavHost.kt` 唯一注册 | 合并 9 个 `register/*` 和 8 个嵌套图 |
| 页面 | Route 管 `koinViewModel`、effect、导航、权限 launcher；Screen 无状态 | 权限 launcher 从全局 Host 移到 Route |
| 返回 | 子页 `back()`；根目的地返回 `false` | 删 `backToMain/navigateTopLevel`，用 `resetToRoot(Main())` |
| 依赖 | 页面只依赖 `AppNavigator`，不 import `app.root` | 删 `forEntry`、流程注册表 |
| 反过度防御 | 不留兼容别名、空实现、转发层 | 不保留 `route` 兼容属性；Gate/UiRequest 死层一并删除 |

PDFScanRead 的 `AGENTS.md` 是单模块 + `base/domain/data` 包结构；本项目采用单 Gradle 模块，但保留现有包名，属于有意的差异（见 1.2）。

## 3. 现状盘点

### 3.1 模块与规模

| 模块 | 生产 Kotlin | 测试 Kotlin | 资源 | Manifest |
| --- | ---: | ---: | ---: | --- |
| `app` | 79 | 24 | 0 | 有 |
| `core:contracts` | 41 | 5 | 0 | 无 |
| `core:navigation` | 4 | 2 | 0 | 无 |
| `core:presentation` | 4 | 3 | 0 | 无 |
| `core:ui` | 46 | 1 | 178 | 无 |
| `feature:startup` | 5 | 1 | 0 | 无 |
| `feature:onboarding` | 5 | 0 | 0 | 无 |
| `feature:home` | 12 | 1 | 0 | 无 |
| `feature:settings` | 12 | 0 | 0 | 无 |
| `feature:junkclean` | 41 | 3 | 0 | 无 |
| `feature:antivirus` | 32 | 0 | 0 | 无 |
| `feature:files` | 95 | 7 | 0 | 无 |
| `feature:applock` | 25 | 0 | 0 | 无 |
| `feature:notificationcleaner` | 10 | 0 | 0 | 无 |
| `feature:toolbox` | 99 | 6 | 0 | 无 |
| `advertise` | 67 | 6 | 88 xml | 有（随 SDK 删除） |
| `junkcc` | 2 | 2 | 大量 | 保留 |

第一方生产 Kotlin 约 450 个，加 app 共 515 个；测试 50 个。`core:ui` 是唯一带资源和 Manifest 引用的库模块。

### 3.2 单模块合并成本

- 132 个文件 `import com.aicleanerstorage.spaceclean.core.ui.R`，2 个文件全限定引用 `core.ui.R`；合并后统一改成 `com.aicleanerstorage.spaceclean.R`。
- `core:ui/src/main/res` 178 个文件整体移到 `app/src/main/res`；app 当前没有 res 目录，不会冲突。
- 14 个模块的 `build.gradle.kts` 依赖高度重复（同样的 Compose/Lifecycle/Koin/Lottie 列表），合并后由 app 一份依赖覆盖。
- 各 feature 模块没有 Manifest、没有独立资源；不需要合并组件。
- 测试目录无同名文件冲突；可整体平移到 `app/src/test/java` / `app/src/androidTest/java`。
- `:junkcc` 仍由 app `implementation(project(":junkcc"))` 引入，是唯一保留的库模块。

### 3.3 路由现状

- `AppDestination` 39 个条目、37 个类型，带 `route`、`featureKey`、`notificationAliases`、`all/forRoute/forFeature/...` 注册表。
- 9 个 `register/*`、8 个嵌套图（文件管理 7 组 + 病毒 1 组）、5 个 owner 文件、`NavigationCoordinator`、`FeatureOwnerRegistry`、`FeatureFlowOwners`。
- 77 个文件引用 `AppDestination`；通知、埋点、转场、首页退出提示都依赖 `route` 字符串。
- 本次埋点删除后，`route` 的消费方只剩转场、首页退出提示和通知深链；通知深链随推送一起删除后，路由可以完全类型化。

### 3.4 权限现状

- `PermissionGateway`、`PermissionInterceptor`（180 行状态机）、`PermissionPlatform`、`AppPermissionHost`、`PermissionGate`、`StorageCleanPermissionUi`、`PermissionObservation`。
- 6 种权限：存储、定位、UsageAccess、通知监听、悬浮窗、POST_NOTIFICATIONS。
- 5 个功能 Gate + 4 个 ViewModel 直接 `ActionPermissionGate`；权限结果进入 `GateResult` / `ExecutionResult`，影响 `CheckingPermission`、`awaitingAuthorization`、退出原因。
- 34 个文件引用 `core.contracts.permission`。

### 3.5 广告与埋点现状

- `:advertise` 222 个源文件（67 kt、51 java、88 xml），约 1.4 MB；同时承载广告、UMP、SDK 常驻通知、FCM 推送、Remote Config、ThinkingData、Play Integrity/Singular 归因、应用更新。
- `MyApp : AdvApplicaiton`、`MainActivity : AdvActivity`；`AdvertiseSdkAdapter` 是唯一接入层。
- 本项目广告业务：`AdvertiseAdDriver`、`InterstitialController`、`InterstitialEffect`、`FeatureEventAdPolicy`、`RouteEntryAdPolicy`、`NavigationEffectHost`、`AppRuntimeBindings`、冷启动、完成/返回广告位；`FeatureFlow` 中段等待广告。
- 埋点：`core:contracts/analytics` 3 个文件、`app/analytics` 5 个、`app/platform/analytics` 4 个、`SdkAnalyticsSink`、`AnalyticsTracker`、`AppSessionAnalytics`；23 个文件使用 `AnalyticsReporter`。
- `ExternalAppOpenGuard` + `ExternalActivityTracker` 17 个文件，用于广告抑制。
- 本项目自己的前台服务、通知监听、通知渠道与 SDK 无关，保留。

### 3.6 当前工作树

已有未提交的“广告 SDK 本地化”改动：`settings.gradle.kts` 新增 `:advertise`、`app/build.gradle.kts` 改本地模块、删除 `OpenAdHostActivity/OpenAdHostWatchdog/AdvertiseConfigFactory`。这批改动本来就是为了接广告；本次直接拆除，P0 只需确认没有其他未提交业务改动被覆盖，不必为它单独保留提交。

## 4. 目标架构

### 4.1 单模块布局

```text
settings.gradle.kts
  include(":app")
  include(":junkcc")          // 按决策 8 保留，唯一第三方库模块

app/
├─ build.gradle.kts           // 吸收全部第一方依赖
├─ product-config.gradle      // 保留广告/产品配置生成
├─ src/main/
│  ├─ AndroidManifest.xml
│  ├─ res/                    // 从 core:ui 整体迁入
│  └─ java/com/aicleanerstorage/spaceclean/
│     ├─ app/                 // 组合根、root、di、platform
│     ├─ core/contracts/      // 原 core:contracts 包
│     ├─ core/navigation/
│     ├─ core/presentation/
│     ├─ core/ui/
│     └─ feature/             // 10 个 feature 包
├─ src/test/java/...          // 各模块测试平移到 app
└─ src/androidTest/java/...
```

包名不变。`core:ui.R` 合并为 app `R`。`:junkcc` 仍是独立 module，app 继续依赖它。

### 4.2 路由

#### 目的地

```kotlin
@Keep
@Serializable
sealed interface AppDestination {
    @Serializable data object Splash : AppDestination
    @Serializable data object OnboardingScan : AppDestination
    @Serializable data class Main(val initialTab: MainTab = MainTab.Home) : AppDestination
    @Serializable data object Settings : AppDestination
    @Serializable data object ManagePermissions : AppDestination
    @Serializable data object JunkClean : AppDestination
    @Serializable data object AntiVirus : AppDestination
    @Serializable data object VirusQuickScan : AppDestination
    // ... 其余 object
    @Serializable data class PhotosDetail(val initialIndex: Int = 0) : AppDestination
    // ... 其余 detail
}
```

删除 `route`、`featureKey`、`featureEntry`、`notificationAliases`、`notificationTarget`、`all/forRoute/forFeature/featureForRoute/normalizeNotificationRoute`。

通知深链随推送删除：不再需要 `AppLaunchRequest`、`AppLaunchCoordinator`、`NotificationIntentRouteResolver`、`AppDestination.notificationAliases`。`MainActivity` 只保留普通 `onCreate`。

#### 导航器

```kotlin
interface AppNavigator {
    fun navigate(destination: AppDestination)
    fun replace(destination: AppDestination)
    fun resetToRoot(destination: AppDestination)
    fun back(): Boolean
}
```

实现 `NavControllerAppNavigator` 直接包 `NavHostController`。删除 `NavigationCoordinator`、`NavigationReceipt/Event`、`forEntry`、`navigateTopLevel`、`backToMain`。

`resetToRoot` 需要兼容 Splash 起始（`Main` 不在栈内）：

```kotlin
override fun resetToRoot(destination: AppDestination) {
    val hasMain = runCatching { navController.getBackStackEntry<AppDestination.Main>() }.isSuccess
    if (hasMain) {
        if (destination is AppDestination.Main) {
            navController.getBackStackEntry<AppDestination.Main>()
                .savedStateHandle[MAIN_TAB_REQUEST] = destination.initialTab
            navController.popBackStack<AppDestination.Main>(inclusive = false)
        } else {
            navController.navigate(destination) {
                popUpTo<AppDestination.Main> { inclusive = false }
                launchSingleTop = true
            }
        }
        return
    }
    if (destination is AppDestination.Main) {
        navController.navigate(destination) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    } else {
        navController.navigate(AppDestination.Main()) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
        navController.navigate(destination) { launchSingleTop = true }
    }
}
```

`Main(tab)` 的坑：栈内已有 `Main` 时 `launchSingleTop` 不更新参数，Tab 走 `MAIN_TAB_REQUEST`（`SavedStateHandle`，值类型 `MainTab`），由 `HomeRoute` 消费后 dispatch `TabSelected`。

#### 单 NavHost

`app/root/AppNavHost.kt` 合并全部 `composable<T>`。页面从构造参数拿 `AppNavigator`，不拿 `NavHostController`。

文件管理/病毒扫描保留“列表/详情共享 VM + 流程级 effect 只消费一次”，但改为显式宿主：

- `app/navigation/AppFlowHost.kt`：`when` + `hasRoute` 解析当前流程根 entry，`key(rootEntry.id)` 挂载唯一 effect 消费者。
- `FileManagerFlowHost`：root entry 上持有 `FileManagerOwnerState`（保留现有 factory），列表/详情都用 `viewModelStoreOwner = rootEntry`。
- `VirusFlowHost`：root entry 上持有 `VirusScanViewModel`。
- 删除 `FeatureGraphs`、`FeatureOwner*`、`FeatureFlowOwners`、`owner/`。

`AppRouteTransitions` 改用 `NavDestination.hasRoute<AppDestination.Splash>()`，不再比较字符串。

`HomeUiState.exitPromptRoute: String?` 改为 `exitPromptDestination: AppDestination?`。

### 4.3 权限

只保留：

```kotlin
// core/ui/permission/AppPermission.kt
enum class AppPermission {
    StorageFiles, Location, UsageAccess, NotificationListener, Overlay, PostNotifications;
    fun isGranted(context: Context): Boolean
    fun runtimePermissions(context: Context): List<String>
    fun settingsIntents(context: Context): List<Intent>
}

// core/ui/permission/PermissionRequest.kt
@Composable
fun rememberPermissionRequest(onResult: (AppPermission, Boolean) -> Unit): (AppPermission) -> Unit
```

Route 负责申请和复检，VM 只接收布尔状态。删除：

- `core:contracts/permission/` 5 个文件；
- `app/permission/PermissionInterceptor/PermissionPlatform/AppPermissionHost/PermissionGate`；
- `StorageCleanPermissionUi`、`PermissionObservation`；
- 5 个 `*PermissionGate`；
- `PresentationModule` 的 `PermissionGateway` 绑定；
- `UiRequest.Permission`、`UiRequestPort`、`UiRequestBroker`、`AppUiRequestHost`（媒体删除授权改为 Route 级 `rememberLauncherForActivityResult`）；
- `IntentGate`、`GateResult`、`IntentExecutor.gates`、`ExecutionResult` 的 `Rejected/Busy/Cancelled`。

`PermissionPreferences` 只保留通知提示策略需要的字段。

通知权限：保留 `NotificationPermissionViewModel/Policy`（首页 350ms、每日频率、会话延后、启动页暂停）；`RequestSystemPermission/OpenAppSettings` 改为 Route launcher，删除 `Busy` 等待。

### 4.4 广告与埋点

删除：

- `:advertise` 整个模块、`include(":advertise")`、app 依赖、广告 Maven 仓库；
- `core:contracts/ads`、`commercial`；
- `app/commercial`、`app/platform/ads`；
- `FeatureFlow` 广告参数、`FeatureEventAdPolicy`、`RouteEntryAdPolicy`、`NavigationEffectHost`、`AppSessionCoordinator`、`ExternalAppOpenGuard`、`ExternalActivityTracker`；
- `MyApp : AdvApplicaiton` → `Application`；`MainActivity : AdvActivity` → `ComponentActivity`；
- SDK 常驻通知、FCM 推送、Remote Config、UMP、应用更新。

埋点全删：

- `core:contracts/analytics/`；
- `app/analytics/`（`AnalyticsEffect`、`AnalyticsIntentTelemetry`、`AppAnalyticsReporter`、`AppSessionAnalytics` 等）；
- `app/platform/analytics/`；
- 所有 `AnalyticsReporter` 参数、`AnalyticsEvent` 调用、`AnalyticsTracker`；
- `RouteAnalyticsEffect`、`HomeRoute/SettingsRoute/SplashRoute/OnboardingRoute` 的 reporter 参数。

`FeatureFlow` 只保留“完成清理记录”：

```kotlin
interface FeatureFlow {
    fun recordOperation(event: FeatureOperationEvent)
}
```

删除 `handleOperation`、`exit`、`FeatureExitReason` 的广告/权限语义。完成/退出导航直接执行，不再等待异步链路。

Firebase：

- 删除 `google-services` / `firebase-crashlytics` 插件与依赖；
- `app/google-services.json` 保留为配置，但不再参与构建；
- 删除 Crashlytics/Firebase 相关 proguard 规则。

配置保留：

```text
config/ad_policy.json
config/product.json
config/remote_config_*.json
config/release-readiness.properties
config/product.schema.json / variant-source.json
core/ui/src/main/res/raw/adload_config.json
core/ui/src/main/res/raw/notification_config.json / notification_content.json
app/product-config.gradle（ADV_* 生成逻辑）
docs/ad-sdk-*.md / docs/permission-ad-acceptance.md（历史）
```

## 5. 分阶段实施

### P0 基线与工作树（0.5～1 人日）

1. 新建分支 `refactor/single-module-route-permission-ad`。
2. 记录 `git status`，确认除广告本地化改动外没有需要保留的未提交业务代码；把当前广告本地化改动视为将被本方案替代，不做单独提交。
3. 跑基线并留日志：
   ```powershell
   .\scripts\check-dependencies.ps1
   .\scripts\check-mvi-boundaries.ps1
   .\gradlew.bat :app:assembleDebug --console=plain
   .\gradlew.bat :app:testDebugUnitTest --console=plain
   ```
4. 记录 APK 大小、失败用例、构建耗时。

退出条件：基线可复现。

### P1 单模块合并（4～6 人日）

1. **迁移源码**
   - 把 `core:contracts/navigation/presentation/ui` 与 10 个 `feature:*` 的 `src/main/java|kotlin` 平移到 `app/src/main/java`，保留包路径。
   - `app/src/main/kotlin` 的现有文件保留，或一并并入 `app/src/main/java`。
2. **迁移资源**
   - `core/ui/src/main/res` 整体移到 `app/src/main/res`。
   - 确认 app Manifest 的 `@style/@mipmap/@xml` 引用全部可解析。
3. **R 类改写**
   - 132 个 `import com.aicleanerstorage.spaceclean.core.ui.R` → `import com.aicleanerstorage.spaceclean.R`。
   - 2 个全限定 `core.ui.R` 同步改写。
4. **迁移测试**
   - 各模块 `src/test` / `src/androidTest` 平移到 app 对应目录；保留包路径。
5. **构建收口**
   - 删除 14 个模块目录下的 `build.gradle.kts`、`proguard`、空 `src`。
   - `settings.gradle.kts` 只留 `:app`、`:junkcc`。
   - `app/build.gradle.kts` 吸收全部依赖：Compose BOM、core-ktx、appcompat、lifecycle、activity、navigation、material3/icons、koin、coroutines、lottie、serialization、junkcc、cloudscan AAR、测试依赖。
   - 删除 app 对 `:core:*` / `:feature:*` 的 `implementation(project(...))`。
6. **验证**
   - `:app:compileDebugKotlin`
   - `:app:testDebugUnitTest`
   - `:app:assembleDebug`

退出条件：`:app` 是唯一第一方模块；无 `core:*`/`feature:*` 的 Gradle include；资源、R、测试全部编译通过；APK 可安装。

### P2 广告、埋点、Firebase 拆除（5～8 人日）

1. 删 `:advertise`、广告 Maven 仓库、广告依赖；`MyApp`/`MainActivity` 换基类。
2. 删 `core:contracts/ads`、`commercial`、`app/commercial`、`app/platform/ads`。
3. 删 `AppRuntimeBindings`、`AppSessionCoordinator`、`NavigationEffectHost`、`RouteEntryAdPolicy`、`ExternalAppOpenGuard`、`ExternalActivityTracker`。
4. 简化 `FeatureFlow`：只记录清理历史；删 `handleOperation/exit`；改所有调用 Route。
5. Splash/Onboarding/JunkClean 删除广告链路。
6. 删埋点层与所有 reporter 参数；删 `AppSessionAnalytics`、`AnalyticsTracker`。
7. 删 Firebase 插件/依赖；删 `NotificationDefaultsInitializer`、SDK 通知/推送。
8. Manifest 删除 AdMob/Facebook meta-data；保留权限/服务/Trustlook。
9. 测试：删广告/埋点测试，保留 `CleanupHistoryStoreTest`。

退出条件：全仓无 `com.pdffox.adv`、无 `AnalyticsReporter`、无 Firebase 依赖；`assembleDebug` 通过；自有前台服务、通知清理正常。

### P3 权限与 Gate 拆除（5～8 人日）

1. 新增 `AppPermission` + `rememberPermissionRequest`。
2. 删权限契约、拦截器、Host、解释 UI、Observation、5 个 Gate。
3. 改 4 个直接调用 `ActionPermissionGate` 的 VM 和 5 个 executor factory。
4. 改 Route：JunkClean、FileManager 流程宿主、AntiVirus、WhatsApp、NotificationCleaner、AppLock、AppUsage、NetworkUsage、NetworkScan、Settings/ManagePermissions、AppRoot。
5. 删 `IntentGate/GateResult/IntentExecutor.gates`，收敛 `ExecutionResult`。
6. 删 `UiRequest*`/`UiRequestBroker`/`AppUiRequestHost`，媒体删除授权改为 Route 级 launcher。
7. 通知权限 Effects 改 launcher；`PermissionPreferences` 缩到通知策略字段。
8. 测试：删权限测试；重写 `JunkCleanViewModelTest` 权限用例。

退出条件：6 类权限真机可拉起并复检；无 `PermissionGateway|PermissionInterceptor|AppPermissionHost|ActionPermissionGate`。

### P4 路由对齐（8～12 人日）

1. **R1 目的地纯类型化**
   - 重写 `AppDestination`；删通知/埋点元数据。
   - 删除 `AppLaunchRequest/AppLaunchCoordinator/NotificationIntentRouteResolver`（随推送删除）。
   - `AppRouteTransitions` 改 `hasRoute`；`HomeUiState.exitPromptDestination`。
2. **R2 导航器直连**
   - 新增 `NavControllerAppNavigator`；`AppNavigator` 4 方法。
   - 所有 Route 从 `forEntry(entry)` 改为构造注入 `navigator`。
   - 删 `NavigationCoordinator`、`NavHostAppNavigator`。
3. **R3 扁平化 + 流程宿主**
   - 新增 `AppFlowHost/FileManagerFlowHost/VirusFlowHost`。
   - 删 `FeatureGraphs`、`FeatureOwner*`、`FeatureFlowOwners`、`owner/`。
4. **R4 单 NavHost**
   - 新增 `app/root/AppNavHost.kt`，合并 9 个 `register/*`。
   - `AppRoot.kt` 移到 `app/root/AppRootView.kt`，创建 `rememberNavController` + `NavControllerAppNavigator`。
5. **R5 收尾**
   - `MAIN_TAB_REQUEST` 类型化，删 `navigateTopLevel`。
   - `back()` 根目的地判定。
   - 重写 `NavigationOwnershipTest`。

退出条件：唯一 `NavHost`；页面不出现 `NavHostController`；返回栈、Tab、流程内导航、进程重建手测通过。

### P5 文档、门禁与配置（2～4 人日）

1. 重写 `docs/mvi-architecture.md` 的权限/广告链路章节；历史文档加“历史记录”。
2. 新增精简根 `AGENTS.md`：单模块、纯类型路由、Route 权限、广告只留配置、埋点已删。
3. 重写 `check-permission-ad-boundaries.ps1` 为 `check-architecture-boundaries.ps1`：
   - 禁止 `com.pdffox.adv`、`AnalyticsReporter`、`PermissionGateway`、`PermissionInterceptor`、`AdDriver`、`IntentGate`；
   - 禁止 `AppDestination` 出现 `route/featureKey/notificationAliases`；
   - 禁止 `NavHostController` 出现在 `app/root`、`NavControllerAppNavigator` 之外；
   - 唯一 `AppNavHost`。
4. 更新 `check-dependencies.ps1`、`check-mvi-boundaries.ps1` 的模块清单（只剩 app/junkcc）。
5. `check-product-config.ps1` 保留广告配置校验。
6. 配置隔离说明：配置保留、消费代码已删。

退出条件：门禁与代码一致；文档不再描述已删除链路。

## 6. 文件级变更

### 6.1 单模块合并（移动）

```text
core/contracts/src/main/kotlin/**      → app/src/main/java/**
core/navigation/src/main/kotlin/**     → app/src/main/java/**
core/presentation/src/main/kotlin/**   → app/src/main/java/**
core/ui/src/main/kotlin/**             → app/src/main/java/**
core/ui/src/main/res/**                → app/src/main/res/**
feature/*/src/main/kotlin/**           → app/src/main/java/**
feature/*/src/test/kotlin/**           → app/src/test/java/**
core/*/src/test/kotlin/**              → app/src/test/java/**
app/src/test/**                        → 保持
```

删除：`core/*/build.gradle.kts`、`feature/*/build.gradle.kts`、`core/*/src`/`feature/*/src` 空目录、`settings.gradle.kts` 中 12 个 include。

### 6.2 删除（P2/P3/P4）

```text
advertise/                                     整个模块
core/contracts/.../ads/                        6 个
core/contracts/.../commercial/                 1 个
core/contracts/.../permission/                 5 个
core/contracts/.../analytics/                  3 个
core/contracts/.../startup/                    2 个（AppLaunchRequest/SdkInitialization）
app/analytics/                                 5 个
app/platform/analytics/                        4 个
app/permission/                                4 个 + NotificationPermissionEffects 简化
app/commercial/                                4 个
app/platform/ads/                              5 个
app/runtime/NavigationEffectHost.kt
app/runtime/RouteEntryAdPolicy.kt
app/runtime/AppSessionCoordinator.kt
app/startup/AppLaunchCoordinator.kt
app/notification/NotificationIntentRouteResolver.kt
app/notification/NotificationDefaultsInitializer.kt
app/navigation/NavigationCoordinator.kt
app/navigation/NavHostAppNavigator.kt
app/navigation/FeatureGraphs.kt
app/navigation/FeatureFlowOwners.kt
app/navigation/owner/                          5 个
app/navigation/register/                       9 个
app/navigation/AppNavigation.kt
app/ui/AppUiRequestHost.kt
core/ui/permission/StorageCleanPermissionUi.kt
core/ui/permission/PermissionObservation.kt
core/presentation/intent/UiRequestBroker.kt
core/contracts/intent/UiRequest.kt / Gate.kt / ExternalActivityTracker.kt
feature/*/.../*PermissionGate.kt               5 个
```

测试删除：广告 4 个、权限 3 个、`NavigationCoordinatorTest`、`RouteEntryAdPolicyTest`、`ColdStartAdsTest`、`AnalyticsPayloadMapperTest`、`AppNotificationTargetRoutingTest`、`AppLaunchCoordinatorTest`。

### 6.3 新增

```text
app/src/main/java/.../core/navigation/AppNavigator.kt
app/src/main/java/.../core/ui/permission/AppPermission.kt
app/src/main/java/.../core/ui/permission/PermissionRequest.kt
app/src/main/java/.../app/root/AppRootView.kt
app/src/main/java/.../app/root/AppNavHost.kt
app/src/main/java/.../app/navigation/NavControllerAppNavigator.kt
app/src/main/java/.../app/navigation/AppFlowHost.kt
app/src/main/java/.../app/navigation/FileManagerFlowHost.kt
app/src/main/java/.../app/navigation/VirusFlowHost.kt
AGENTS.md（P5）
docs/single-module-route-permission-ad-plan.md（本文件）
```

## 7. 测试与验收

### 7.1 自动

| 层级 | 用例 |
| --- | --- |
| app 单测 | `AppDestination` 序列化；`AppNavigator` 映射；`FeatureFlow` 只记录；`JunkCleanViewModel` 权限状态；`NotificationTargets` 已删则删对应测试 |
| core 单测（合并后仍在 app） | `MviViewModel`、`IntentExecutor`、`FileUri`、`DuplicateContentGuard`、文件安全 |
| androidTest | `AppNavigator` 返回栈、Tab、流程内共享 VM；6 类权限 launcher |
| 构建 | `compileDebugKotlin`、`testDebugUnitTest`、`assembleDebug` |
| 脚本 | `check-architecture-boundaries.ps1`、`check-dependencies.ps1`、`check-mvi-boundaries.ps1` |

命令：

```powershell
.\gradlew.bat :app:testDebugUnitTest --console=plain
.\gradlew.bat :app:assembleDebug --console=plain
.\scripts\check-dependencies.ps1
.\scripts\check-architecture-boundaries.ps1
```

### 7.2 手测

- 首启：Splash → Onboarding → Main；无广告等待、无埋点依赖。
- 首页三 Tab、底部切换、返回退出确认。
- JunkClean：未授权 → 系统设置 → 返回 → 扫描；拒绝回 Idle；清理授权部分成功。
- FileManager 7 组：列表 ↔ 详情共享 VM；删除授权；返回首页。
- AntiVirus：首页 → 快扫/深扫 → 结果/无结果。
- AppUsage/NetworkUsage/NetworkScan/AppLock/NotificationCleaner：6 类权限设置页往返。
- 进程重建/旋转；通知权限提示频率。

## 8. 风险与回滚

| 风险 | 影响 | 缓解 |
| --- | --- | --- |
| 单模块一次性移动 500+ 文件 | 编译大面积失败、rename 跟踪混乱 | P1 单独分支；先移动再统一改 R；`compileDebugKotlin` 作为门禁 |
| 资源合并冲突 | Manifest 引用找不到 | app 当前无 res，整体搬运；先编译 Manifest |
| `:junkcc` 例外 | “单模块”不彻底 | 明确它是第三方库模块，不参与第一方重构 |
| 埋点全删 | 无法观测 | 决策已确认；后续需要时重新接，不在本次留空实现 |
| Crashlytics 删除 | 线上崩溃不可见 | 决策已确认；保留 `google-services.json` 便于恢复 |
| 权限 UX 直接系统页 | 用户不理解为何跳设置 | 页面文案/空状态说明；不做全局弹窗 |
| 扁平路由 + 流程宿主 | 共享 VM/完成事件丢失 | `AppFlowHost` + root entry；Plan B 保留 8 个嵌套图 |
| 通知深链删除 | 旧推送链接失效 | 推送 SDK 已删，链接本身不再产生 |

## 9. 工作量

| 阶段 | 人日 |
| --- | ---: |
| P0 基线 | 0.5～1 |
| P1 单模块合并 | 4～6 |
| P2 广告/埋点/Firebase | 5～8 |
| P3 权限与 Gate | 5～8 |
| P4 路由 | 8～12 |
| P5 文档/门禁 | 2～4 |
| 合计 | **24.5～39** |
| 含 20% 缓冲 | **29～47** |

## 10. 执行假设（未再确认项）

1. **包名不变**：单模块只合并 Gradle 模块，不改 `core.*`/`feature.*` 包名。
2. **`:junkcc` 例外**：按决策 8 保留为唯一库模块；“单模块”指第一方代码。
3. **埋点删除方式**：删接口和调用，不保留 `NoOpAnalyticsReporter`。
4. **Firebase**：删插件和依赖，`google-services.json` 留作配置。
5. **通知深链**：随推送一起删除；不再保留 `AppLaunchCoordinator`。
6. **`UiRequest` 层**：媒体删除授权改 Route launcher，删 broker/host。
7. **配置保留**：`config/*`、`product-config.gradle` 的 `ADV_*`、raw 配置原样保留。
8. **不提交**：执行期间不主动 commit；每个阶段结束由你决定是否提交。

## 11. 执行进度（2026-11-20）

### P0 基线

- 使用缓存 Gradle 9.4.1（wrapper 下载卡住，改用 `~\\.gradle\\wrapper\\dists\\gradle-9.4.1-bin\\45w7...\\bin\\gradle.bat`）。
- 基线 `:app:compileDebugKotlin` 通过；基线任务均为 up-to-date。

### P1 单模块合并（已完成）

- 510 个主源码、50 个测试、178 个资源文件已并入 `app`。
- 133 处 `core.ui.R` 改为 app `R`；删除重复的 `app_name` 资源（由 `product-config.gradle` 生成）。
- `core/`、`feature/` 目录删除；`settings.gradle.kts` 仅保留 `:app`、`:advertise`（待 P2 删除）、`:junkcc`。
- `:app:compileDebugKotlin` 通过（需先 `:app:clean` 清掉旧 source snapshot）。
- `:app:testDebugUnitTest`：239 项，4 项失败，均为待删除的旧测试：`PermissionInterceptorTest` 2 项、`JunkCleanRouteEffectsTest` 的广告等待 2 项。
- 下一步：P2 删除 `:advertise`、埋点、Firebase、推送与广告业务；同时删除上述失败测试。

- `:app:assembleDebug` 通过（2m 9s），APK 生成成功。
- P1 结束；未提交任何 commit。


### P2 广告、埋点、Firebase 拆除（已完成）

- 删除 `:advertise` 模块（源码已归档到 `D:\pros\my\_aicleaner_advertise_src_backup.zip`）、`com.pdffox.adv` 全部引用、广告业务层、SDK 常驻通知/推送/Remote Config/UMP/应用更新。
- 删除埋点层（`core.contracts.analytics`、`app/analytics`、`app/platform/analytics`、`AnalyticsReporter` 调用）与 Firebase/Crashlytics 插件/依赖。
- `MyApp` 改为 `Application`、`MainActivity` 改为 `ComponentActivity`；Splash 去掉冷启动/通知深链/埋点，Onboarding 去掉完成广告，JunkClean 去掉完成广告等待。
- `:app:compileDebugKotlin` 通过；`:app:testDebugUnitTest` 通过；`:app:assembleDebug` 通过。
- APK 从 180.5 MB 降到 99.6 MB（`:junkcc` 仍保留）。
- Gradle 配置对齐 PDFScanRead：wrapper 改腾讯镜像，`settings.gradle.kts` 阿里云镜像优先，`gradle.properties` 开启 `parallel`/`caching`。
- 下一步：P3 权限框架与 Gate 层拆除。

- 镜像生效后增量构建：`gradlew :app:assembleDebug` 2.4s（首次换仓库约 5 分钟做一次依赖重解析，之后命中缓存）；`--offline` 3.7s。


### P3 权限与 Gate 拆除（已完成）

- 删除 `core.contracts.permission`、`PermissionInterceptor`、`PermissionPlatform`、`AppPermissionHost`、`PermissionGate`、`StorageCleanPermissionUi`、`PermissionObservation`、5 个功能 PermissionGate。
- 新增 `core.ui.permission.AppPermission`、`rememberPermissionRequest`、`PermissionChecker/ContextPermissionChecker`；VM 只做只读检查，申请与结果处理归 Route。
- 删除 `IntentGate`/`GateResult`/`IntentExecutor.gates`；`ExecutionResult` 的 Rejected/Busy/Cancelled 不再由权限产生。
- 删除 `UiRequest.Permission`；`UiRequestBroker` 暂时只保留 MediaStoreDelete，留到 P4 路由收口一起删。
- Route 级权限入口已接到：JunkClean/FileManager/Virus 深扫/WhatsApp（存储）、AppUsage/NetworkUsage（UsageAccess）、NetworkScan（定位）、NotificationCleaner（通知监听）、AppLock（悬浮窗）、ManagePermissions（四类设置页）。
- 删除旧权限测试与 `NavigationOwnershipTest`（P4 重写）；`:app:testDebugUnitTest` 通过，`:app:assembleDebug` 通过；APK 100.4 MB。
- 下一步：P4 路由对齐（纯类型目的地、单 NavHost、流程宿主、删 UiRequestBroker）。


### P3.5 包结构与 MVI 命名对齐（已完成）

- 顶层包收敛为 PDFScanRead 同构：`app/`、`base/`、`data/`、`domain/`；删除 `core/`、`feature/` 包。
- 页面统一到 `app/view/<page>/`：`ui/` 放 Route/Screen/part，`interaction/` 放 Contract/ViewModel/Reducer。
- `base/` 下：`navigation/`、`mvi/`、`designsystem/`、`permission/`、`util/`。
- `domain/` 按功能收口（antivirus/applock/device/feature/files/junkclean/notificationcleaner/onboarding/settings/toolbox）。
- `data/` 按功能收口（+ `local/`、`storage/`）。
- MVI 命名对齐：`MviViewModel<State, Action, Event>`、`onAction`、`sendEvent`、`setState`、`event` Flow；页面类型 `XxxIntent`→`XxxAction`、`XxxEffect`→`XxxEvent`。
- `AppRoot` 迁到 `app/root/AppRootView.kt`。
- 迁移 466 个文件、改写 479 个文件 import；`compileDebugKotlin`、`testDebugUnitTest`、`assembleDebug` 全部通过；APK 99.9 MB。
- 仍未做：`AppDestination` 纯类型化、单 `AppNavHost`、删 `FeatureFlow`/`AppRuntimeBindings`/`UiRequestBroker`/`IntentExecutor`、`domain/model` 与 `domain/port` 细分。这些属于 P4。


### P4 路由对齐（核心已完成）

- `AppDestination` 改为纯 `sealed interface`：删除 `route`/`featureKey`/`notificationAliases`/`all`/`forRoute`/`featureForRoute`。
- `AppNavigator` 收敛为 `navigate / replace / resetToRoot / back`；`NavHostAppNavigator` 直接包 `NavHostController`，删除 `NavigationCoordinator` 与 `FeatureFlowOwners`。
- 新增 `app/root/AppNavHost.kt`（原 `AppNavigation`），`AppRootView` 创建并注入 navigator；转场改 `NavDestination.hasRoute`，首页退出提示改类型化目的地。
- 删除 `NavigationCoordinatorTest`、旧 `AppDestinationTest`/`TypedDestinationTest`；`VariantProfileTest` 只保留产品身份。
- `compileDebugKotlin`、`testDebugUnitTest`、`assembleDebug` 全部通过；APK 99.9 MB。
- 仍未收口：9 个 `register/*` 扩展函数、文件管理/病毒嵌套图与 `FeatureOwnerRegistry`、`FeatureFlow`/`AppRuntimeBindings`/`UiRequestBroker`/`IntentExecutor` 仍在。下一步是 P4.1：把这些合并/删除。


### P4.2 删除 UiRequest 层（已完成）

- 删除 `AppUiRequestHost`、`UiRequestBroker`、`UiRequest`、`UiRequestPort`、`UiRequestBrokerTest`。
- 媒体删除授权改为 Route 级 `rememberMediaStoreDeleteRequest()`（`base/permission/MediaStoreDeleteRequest.kt`），JunkClean 和 FileManager 不再经过 broker。
- `IntentExecutor` / `IntentEffect` 去掉 `ui` 参数；5 个 `*ExecutorFactory` 和 DI 同步删除。
- `AppRuntimeBindings` 已删除，`AppRootView` 直接创建 `FeatureFlow` 与 `ExternalActivityTracker`。
- `compileDebugKotlin`、`testDebugUnitTest`、`assembleDebug` 通过；APK 100.7 MB。

仍未收口：`FeatureFlow`（约 30 个 Route）、`IntentExecutor`/`*ExecutorFactory`、9 个 `register/*`、嵌套图与 `FeatureOwnerRegistry`。

### P4.3 删除 FeatureFlow 与 UiRequest 残留（已完成）

- 删除 `FeatureFlow`、`AppFeatureFlow`；清理历史改为 `FeatureOperationEvent.recordCleanupCompletion(CleanupHistoryStore)`（`domain/feature/FeatureCleanupRecorder.kt`）。
- 30 个 Route 的 `featureFlow` 参数、空 `exit` 调用和 `LaunchedEffect` key 全部移除。
- 删除 `AppRuntimeBindings`、`AppUiRequestHost`、`UiRequest*`、`UiRequestBroker`、`UiRequestBrokerTest`。
- 媒体删除授权改为 `rememberMediaStoreDeleteRequest()`。
- `IntentExecutor` / `IntentEffect` 去掉 `ui` 参数。
- `compileDebugKotlin`、`testDebugUnitTest`、`assembleDebug` 通过；APK 100.7 MB。

剩余：`IntentExecutor`/`*ExecutorFactory`、9 个 `register/*`、文件管理/病毒嵌套图与 `FeatureOwnerRegistry`。

### P4.4 删除 ExternalActivityTracker（已完成）

- 删除 `ExternalActivityTracker`；12 个文件的 `externalActivities` 参数、`markLaunch/cancelLaunch/markReturn` 调用移除。
- `openDeveloperSettings` / `openAppSettings` / `openWifiSettings` 去掉 tracker 回调。
- `compileDebugKotlin`、`testDebugUnitTest`、`assembleDebug` 通过；APK 99.6 MB。

剩余：`IntentExecutor`/`*ExecutorFactory`（需改业务数据流，不是纯删除）、9 个 `register/*`、文件管理/病毒嵌套图与 `FeatureOwnerRegistry`。

### P4.5 文件管理导航扁平化（已完成）

- 7 组文件管理（照片/相似照片/截图/视频/音频/大文件/文档）共 14 个目的地从 `navigation<XxxFlow>` 嵌套图改为 `AppNavHost` 下平级 `composable`。
- 列表/详情通过 `navController.getBackStackEntry<AppDestination.XxxManager>()` 共享同一个 ViewModel。
- 新增 `FileManagerFlowHost`：只在当前 entry RESUMED 时收集流程 event，保证单消费者；`FeatureOwnerRegistry`、`FileManagerRouteOwner` 删除。
- `FeatureGraphs` 只保留 `AntiVirusFlow`；`FeatureOwner` 只保留 `JunkClean`。
- 修正了此前 FileManagerEvent 没有消费者导致的列表→详情导航、返回、扫描启动死链路。
- `compileDebugKotlin`、`testDebugUnitTest`、`assembleDebug` 通过；APK 100.2 MB。

剩余：病毒 5 页嵌套图 + `FeatureOwner.JunkClean`、`IntentExecutor`/`*ExecutorFactory`。

### P4.6 病毒扁平化与 owner 清理（已完成）

- 病毒 5 页（AntiVirus/QuickScan/DeepScan/Result/NoVirusResult）从 `navigation<AntiVirusFlow>` 改为平级 `composable`。
- 5 页通过 `getBackStackEntry<AppDestination.AntiVirus>()` 共享同一个 `VirusScanViewModel`，每页挂 `VirusEffectHandler`。
- JunkClean 直接内联到 `registerCleanRoutes`，删除 `JunkCleanRouteOwner`。
- 删除 `FeatureGraphs.kt`、`FeatureOwner.kt`、`FeatureOwnerRegistry.kt`、`owner/` 目录。
- `app/root/navigation/` 只剩 `AppRouteTransitions.kt`、`NavHostAppNavigator.kt`。
- `compileDebugKotlin`、`testDebugUnitTest`、`assembleDebug` 通过；APK 100.4 MB。

剩余：`IntentExecutor`/`*ExecutorFactory`（5 组，业务数据流改动）。

### P4.7 删除 IntentExecutor（已完成）

- AppUsage / NetworkUsage / NetworkScan：删除 `*ExecutorFactory`、`*Handler`、`*Operation`、`*Result`，ViewModel 直接调用仓库。
- FileManager：`FileManagerExecutorFactory` 改为 `FileManagerPipelineFactory`，`FileManagerPipeline` 直接调用 `FileManagerHandler`，不再经过 `IntentExecutor`。
- JunkClean：删除 `JunkCleanExecutorFactory` / `JunkCleanHandler` / `JunkCleanOperation`，ViewModel 直接调用 `CleanRepository`。
- 删除整个 `base/mvi/intent` 包（`IntentExecutor`、`ExecutionResult`、`IntentRequest`、`RequestId`、测试）。
- `base/mvi/` 只剩 `MviViewModel.kt`。
- `compileDebugKotlin`、`testDebugUnitTest`、`assembleDebug` 通过；APK 100.4 MB。

### P4.8 导航核心归位（已完成）

- `NavHostAppNavigator` 移到 `base/navigation/AppNavigatorImpl.kt`。
- `AppRouteTransitions` 移到 `app/root/AppRouteTransitions.kt`。
- 删除 `app/root/navigation/` 目录。
- `base/navigation` 现在包含：`AppDestination`、`AppNavigator`、`AppNavigatorImpl`、`MainTab`、`ToolNavigationTarget`。
- `app/root` 只放 `AppNavHost`、`AppRootView`、`AppRouteTransitions`。
- `IntentExecutor` / `ExecutionResult` / `IntentHandler` 全部删除。
- `compileDebugKotlin`、`testDebugUnitTest`、`assembleDebug` 通过；APK 100.4 MB。

### P4.9 domain 按 model/port/rule/usecase 拆分（第一批）

- 新增 `domain/model`、`domain/port`、`domain/rule`、`domain/usecase`、`domain/error`。
- `feature` → `model/feature`（FeatureKey/Group/Operation）、`port/feature`（CleanupHistoryStore）、`rule/feature`（recordCleanupCompletion）。
- `files/intent` → `model/files`（FileManagerOperation）、`usecase/files`（Handler/PipelineFactory）。
- `junkclean` → `port/junkclean`（CleanRepository/CleanSessionStore）、`model/junkclean`（原 model/*）。
- `antivirus`/`device`/`applock`/`notificationcleaner`/`onboarding`/`settings` 全部拆到 `model` / `port`；`TrustlookConfigurationException` 放 `domain/error`。
- `compileDebugKotlin`、`testDebugUnitTest`、`assembleDebug` 通过；APK 99.6 MB。

剩余：`domain/files`、`domain/toolbox` 仍按功能分包，可继续按同样规则拆。

### P4.10 domain 拆分收口（已完成）

- `domain/files` 拆为 `model/files`（ManagedFileModels、FileDeleteResult、FileManagerOperation）、`port/files`（FileRepository、DuplicateFilesPreferences）、`rule/files`（DuplicateContentGuard）。
- `domain/toolbox` 拆为 `model/toolbox/{appusage,battery,shared}` 与 `port/toolbox/{appusage,battery,deviceinfo,shared}`。
- `domain` 顶层现在只有 `model / port / rule / usecase / error`。
- 项目顶层只有 `app / base / data / domain`。
- `compileDebugKotlin`、`testDebugUnitTest`、`assembleDebug` 通过；APK 99.6 MB。

### P4.11 data 按 repository/local 拆分（已完成）

- `data` 顶层现在只有 `repository/` 与 `local/`。
- 仓库实现、DataSource、扫描器、Helper 移到 `data/repository/<feature>`。
- `data/storage/*PreferencesImpl` 移到 `data/local/preferences/`。
- 更新 `AndroidManifest.xml` 中 `StorageCleanNotificationListener` 的类名到 `data.repository.notificationcleaner`。
- `compileDebugKotlin`、`testDebugUnitTest`（112 个测试）、`assembleDebug` 通过；APK 99.6 MB。

### P4.12 权限入口收敛（已完成）

- 新增 `base/permission/PermissionGate.kt`：已授权放行 / 拉起系统界面 / 返回后复检；删除旧入口 `PermissionRequest.kt`。
- 9 个 Route/EffectHandler 去掉手写 `isGranted` 预检与 `pendingIntent` 暂存，改用 `allow` / `request`。
- 删除 4 个零调用点权限弹窗：`AppLockPermissionPopups`、`NotificationEnableGuideDialog`、`popups/PermissionPopups`、`popups/PermissionCopy`（533 行）。
- `AppPermission`、`PermissionChecker`、`MediaStoreDeleteRequest` 与通知权限三件套（Policy/ViewModel/Effects）未改。
- 新增 `PermissionGateTest` 6 项；`compileDebugKotlin`、`testDebugUnitTest`（118 个测试）、`assembleDebug` 通过。
- 详见 [权限层全貌](permission-layer-review.md)。
- 遗留：`NotificationPermissionCommand.OpenAppSettings` 复用了运行时申请入口，API 33+ 上实际不会打开设置页；待确认产品意图后单独修复。

### P4.13 通知权限改为「申请结果反推」（已完成）

- 删除 `NotificationPermissionPolicy`（323）、`NotificationPermissionViewModel`（104）、`NotificationPermissionEffects`（78）及两个状态机测试（471）。
- 新增 `app/permission/NotificationPermissionPrompt.kt`（113 行）：先拉系统框；失败且无 rationale ⇒ 弹说明窗 ⇒ `gate.openSettings`。
- 去掉 `hasRequestedBefore`、`RequestSource`、`Surface`、5 个 phase、会话延后与 splash 联动；`PermissionPreferences` 只保留提示频率时间戳。
- `PermissionGate` 增加 `openSettings` / `shouldShowRationale`，并把 `findActivity` 收口到 `base/permission`。
- `compileDebugKotlin`、`testDebugUnitTest`（105 个测试）、`assembleDebug` 通过。
- 遗留：真机验证未执行；`AppConfig` 与 Prompt 的通知判定口径不一致（`checkSelfPermission` vs `areNotificationsEnabled`）。

### P4.14 删除功能操作事件与清理历史（已完成）

- 删除 `FeatureOperationEvent`、`OperationAction`、`FeatureExitReason`、`FeatureGroup`；删除 `domain/port/feature/CleanupHistoryStore`、`data/local/preferences/CleanupHistoryStore`、`domain/rule/feature/FeatureCleanupRecorder` 与其测试（`hasCompletedCleanup()` 在 P2 删除广告后已无生产读取方）。
- 9 个文件管理 ViewModel 删除 `trackScanStarted/trackScanFinished/trackOperation*`、`pendingResult` 中转与 `CompletionAdFinished` 派回；操作成功在 `completeDelay` 结束后直接推进结果页。
- JunkClean、WhatsApp、NotificationCleaner、NetworkSpeed 同样去掉「VM → Route → VM」闭环；`completionAdInFlight`、`CompletionAdStarted/Finished`、`PostAdStatusRequested` 删除。
- `FileManagerEvent.Exit` 退化为 `Exit(home)`；删除 `leaveBackForPermissionRejected` 和忽略参数的 `exit(feature, reason)`。
- 两个管理 VM 测试的断言由 `CompleteAnimation` 改为 `Result`：结果态不再依赖 Route 派回。
- `compileDebugKotlin`、`testDebugUnitTest`（109 个测试）、`assembleDebug` 通过。
- 剩余：`FeatureKey` 与 `ToolNavigationTarget` 仍是同一份功能清单的两份拷贝，合并需改约 45 个文件，单独提交。

### P4.15 `FeatureKey` 合并进 `ToolNavigationTarget`（已完成）

- 删除 `domain/model/feature/FeatureKey.kt` 与整个 `domain/model/feature` 包；`domain/port/feature` 同时清空。
- `HomeFeatureUiSpec` 的 `titleRes()` / `iconRes()` / 两个列表改为 `ToolNavigationTarget` 扩展，列表更名 `ToolboxTargets` / `FileManagerTargets`。
- 删除 `ToolNavigationTarget.featureKey` 与 `FeatureKey.toToolNavigationTarget()` 两段各 20 行的互转映射。
- `ToolBoxTabContent` / `FilesManagerTabContent` 去掉 `filter { it.toToolNavigationTarget() != null }`；`HomeScreen.openFeature` 直接转发目标。
- `ToolNavigationTarget.Toolbox` 补了文案与图标分支（网格不会渲染它）。
- `compileDebugKotlin`、`testDebugUnitTest`（109 个测试）、`assembleDebug` 通过。
- 效果：功能清单从三份（`AppDestination` / `ToolNavigationTarget` / `FeatureKey`）收敛为两份，`domain` 中不再有页面身份模型。
