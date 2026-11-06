# 广告 SDK 降级计划（v2 评审稿）

> 状态：**评审中，未改动任何代码**
> 分支：`codex/refactor-v3`
> 背景：当前 `com.pdffox:advertise:1.0.10`（aar，新代）存在未修复问题，用户判定暂不可修；内部 nexus 无旧版 aar。因此回退到 DataRestoreFileRecovery 的 `:advertise` 旧源码模块（`D:\pros\my\DataRestoreFileRecovery\advertise`）。
> 约束：单 app、无变体 → 不做多产品注入；单人 + AI 协作 → 验证收敛到本地构建 + 单 app 冒烟。
> 变更记录：v2 新增 §7「通知迁移（常驻 + 后台推送）」、§6 通知相关补丁点、对应验证/风险/提交拆分；总工期调整为 5–7 天。

---

## 1. 目标与范围

### 1.1 目标

把 `com.pdffox:advertise:1.0.10`（aar）替换为 DataRestore 的旧代源码模块（仓库内 `:advertise`），广告行为回到已知可用的旧代实现。

### 1.2 非目标（本阶段不做）

- 不重构导航层（`NavigationCoordinator` / owner 拆分等全部搁置）。
- 不改动广告双路径（`UiRequest.ShowInterstitial` ↔ `InterstitialController`），降级稳定后单独提交。
- 不迁移 `:safe` 模块（反作弊沿用旧 SDK 的 `AdChecker` + 包名校验）。
- 不重写任何业务 feature / Handler / Pipeline。
- 不重做 PRODUCT_CONFIG 的多产品流水线（按单 app 处理）。
- 不复制 DataRestore app manifest 中数百个混淆 Activity（junk code，与通知无关）。

### 1.3 保持不变

- `AdDriver` 接口（`core/contracts/ads`）——feature 模块边界。
- `InterstitialController.canShow` 门控（外部 App 返回、权限弹窗、忙碌态）。
- `UiRequestBroker` / `AppUiRequestHost` 单例。
- `AdAreaKeys` 的 area key 命名、`AdScene`、`RouteEntryAdPolicy`、`FeatureEventAdPolicy`。
- `AppDestination.notificationAliases()` 的通知路由映射（`NotificationIntentRouteResolver` 不变）。

---

## 2. 已确认事实（新旧两代差异盘点）

| 项 | 新 aar（现状） | 旧源码（目标） |
|---|---|---|
| 入口 | `AdvertiseSdk` + `AdvertiseSdkConfigs.create{}`（编程式，`BuildConfig.ADV_*` 注入） | `Ads.init(context, isTest)`（suspend）+ `ShowInterAd.showIntAd(activity, areaKey, onClosed)` |
| 配置 | `AdvertiseConfigFactory`（205 行）从 `BuildConfig.ADV_*` 组装 | 模块内 `Config` 硬编码（Thinking key / Singular key / host / privacy / packageName） |
| 广告 ID | BuildConfig 注入 AdMob/MAX/TopOn | `AdvIDs` 硬编码 DataRestore 的 AdMob/MAX/TopOn |
| ad_policy | app 生成（`app/build/generated/product-config/res/raw/ad_policy.json`，含 `package_name`），经 `resources(adPolicyRawResId=...)` 注入 | 读**模块自己的** `R.raw.ad_policy`；`package_name` 不匹配直接 kill 进程 |
| 应用类 | 无要求 | `Ads.init` 参数为 `AdvApplicaiton` → `MyApp` 必须继承 |
| Activity | 无要求 | `Ads.showInterstitialAd(activity: AdvActivity)` → `MainActivity` 必须继承 `AdvActivity` |
| 开屏 | `OpenAdHostContract`（自定义 ResultReceiver 状态机） | `UMPUtil.initUMP` → `Config.canLoadOpen` → `Ads.showOpenAd(activity, AreaKey.openPageAdv, ...)` |
| 常驻通知 | aar 内置前台服务，`ensurePersistentNotificationServiceRunning` 启动；`NotificationFeatureConfig` 编程式配置（4 个 action + 按 `AppDestination` aliases 的路由图标映射） | **App 侧** `CommonService`（前台服务载体，`MainActivity.onResume` 里 `startForegroundService`）+ SDK `NotificationManager` 引擎（频道/组/RemoteViews/多 receiver） |
| 通知内容/配置 | 远程 config + `NotificationDefaultsInitializer` 本地默认（`core/ui` res/raw） | `NotificationManager.notificationConfig/notificationContents`（public var）+ `updateNotificationConfig/updateNotificationContent` + 模块 `res/raw/notification_config.json`/`push.json` + 远程 `PushUrl` 下发 |
| 后台推送 | aar 的 FCM service + `PushSceneKeyConfig`（imageDeleted→/managePhotos 等） | `MyFirebaseMessagingService`（SDK 模块）+ `PushManager`/`ServiceStarterJobService` + 点击带 `Route` extra |
| 开机重启 / 通知被删回调 | aar 内部处理 | **App 侧** `BootReceiver` + `ForegroundServiceRestartPolicy` / `NotificationDeletedReceiver` |
| 埋点 | `AdvertiseSdk.logEvent/setSuperProperties/setUserAttr`（`SdkAnalyticsSink`） | `LogUtil.log` / `ThinkingAttr` / `TDAnalytics` |
| 反作弊 | `AdvertiseSdkConfigs.create { safe(...) }` | `AdChecker` + `AdPolicyManager` 包名校验 + 进程 kill |
| 插屏播放 | `AdvertiseSdk.showInterstitialAd` | `ShowInterAd` 自带 `isShowing` 去重、6s 超时、3 次重试、AdPool 预加载池、AdMob/MAX/TopOn/bidding 四路聚合 |
| 依赖 | aar 托管依赖；app 侧 exclude pangle + 单独引 pag-sdk + tiktok-comp | 模块 `api` 引入 AppLovin/TopOn/UMP/Facebook 全家桶；`latest.release`、`+` 适配器版本需固定 |
| minSdk | 29 | 29（一致） |

关键落地事实：

1. AI_Cleaner 生成的 `ad_policy.json` 已带 `"package_name": "com.aicleanerstorage.spaceclean"`，旧 SDK 的包名校验可通过（前提是从该资源加载 policy）。
2. DataRestore 的开屏流程是现成可抄的：`SplashScreen.kt` 中 `UMPUtil.initUMP(activity) {}` → 缓存判断 → `Ads.showOpenAd(activity, AreaKey.openPageAdv, ...)`。
3. 旧模块是**部分混淆/反编译的源码**（manifest 中有一批随机类名 Activity，`Config.kt` 残留反编译痕迹），补丁必须克制：只加参数入口，不碰播放逻辑。
4. 旧模块来源工程 `StorageCleaner`（`D:\pros\andriod\StorageCleaner`）是新代 SDK 源码（已发到 nexus 1.0.15），**不能用于降级**；但其中 `Ads.kt` 仍是 internal 编排层，证明旧代的行为逻辑（去重/超时/重试/池）在新代里延续。
5. 旧 SDK 通知体系是"**SDK 引擎 + App 侧载体**"两层：`NotificationManager`/`MyFirebaseMessagingService` 在 SDK 模块；`CommonService`/`BootReceiver`/`NotificationDeletedReceiver`/`PushConfig`/`ForegroundServiceRestartPolicy` 在 DataRestore 的 **app** 模块。且 `MyFirebaseMessagingService` **硬编码了 DataRestore 的包名**（广播 action 与 `CommonService` 类名），降级时必须打补丁。

---

### 2.1 与 StorageCleaner 的关系（为什么不能拿它降级）

`D:\pros\andriod\StorageCleaner` **不是另一个 SDK 工程，而是 AI_Cleaner 的同族产品**，app 架构与 AI_Cleaner 几乎逐字相同（`MyApp`/`MainActivity`/`SdkInitializationCoordinator`/`NotificationIntentRouteResolver`/`AppRoot`/`ensureSdkPersistentNotificationRunning`），区别仅在于：

| | AI_Cleaner | StorageCleaner |
|---|---|---|
| SDK 引入 | `com.pdffox:advertise:1.0.10`（aar） | `:advertise` 源码模块（in-repo，maven-publish 到内部 nexus） |
| SDK 门面 | `AdvertiseSdk` 直连 | 多一层 `:mar2game`（`Mar2GameAd` facade） |
| SDK 版本 | 1.0.10 | 1.0.15（git log 记录"1.0.15版本发布"） |

结论：

1. StorageCleaner 的 `:advertise` 就是"有问题的新 SDK 本体"（`AdvertiseSdk` + `AdvertiseSdkConfigs` + 竞价/探价/中介），**不能作为降级源**。
2. 降级前先做两个零成本验证：
   - **验证 StorageCleaner（1.0.15）是否有同样问题**：若无 → 问题可能特定于 1.0.10 版本或 AI_Cleaner 配置，先试升级 `pdffoxAdvertise` 到 1.0.15（一行改动）；
   - **两个都不行再降级**。
3. 若 StorageCleaner 也中招 → 降级方案升级为"共享旧 SDK"：把 DataRestore 旧模块放进 StorageCleaner 的发布工程（已有 `maven-publish` + nexus），发成 `com.pdffox:advertise-legacy:0.9.x`，AI_Cleaner 与 StorageCleaner 都 pin 该坐标（AI_Cleaner 仓库不背 SDK 源码）。
4. 新 SDK 修复后，StorageCleaner 的 app 代码就是 AI_Cleaner 迁移 `Mar2GameAd` facade 的现成模板（本阶段不动）。

---

## 3. 前置决策（已按"单 app 无变体"收敛）

| # | 决策 | 结论 |
|---|---|---|
| D1 | 旧模块引入方式 | 仓库内 `:advertise` 模块（无旧 aar，只能源码引入） |
| D2 | 配置注入 | **不做**。AI_Cleaner 的广告值直接写进旧模块默认值（单 app，无变体） |
| D3 | ad_policy 加载 | **方案 A（推荐）**：旧模块 `Ads.init` 加一行参数，读 app 生成的 `R.raw.ad_policy`（单一来源，后续 policy 改动自动生效）；备选方案 B：把生成的 JSON 复制进模块 `res/raw`（零代码改动，但要维护两份） |
| D4 | 开屏机制 | 对齐 DataRestore：`UMPUtil.initUMP` + `AdConfig.canLoadOpen` + `Ads.showOpenAd`，替换 `OpenAdHostContract` |
| D5 | 通知 | 采用旧 SDK 的"引擎 + App 载体"模式：SDK 的 `NotificationManager`/`MyFirebaseMessagingService` + App 侧从 DataRestore 复制 5 个类；`NotificationIntentRouteResolver` 不变；图标映射按 JSON 配置迁移（接受可能的图标统一化） |
| D6 | 反作弊 / `:safe` | 不迁移 `:safe`；沿用旧 SDK 的 `AdChecker` + 包名校验 |
| D7 | 广告双路径 | 本阶段不动 |

---

## 4. 目标态

```
app ──(BuildConfig ← config/product.json 仅身份)──▶ LegacyAdConfig（写死旧模块默认值）
                                                        │
                     :advertise（旧代 SDK 源码，含参数化补丁）
                     ├── Ads.init(context, isTest, adPolicyRawResId)
                     ├── ShowInterAd / ShowOpenAd / ShowBannerAd / ShowNativeAd
                     │   └── 自带 isShowing 去重 / 6s 超时 / 3 次重试 / AdPool
                     ├── UMPUtil（consent / 开屏 consent）
                     ├── NotificationManager（通知引擎）+ MyFirebaseMessagingService（FCM）
                     └── Config / AdvIDs（默认值 = AI_Cleaner 的值）

app（新增）：CommonService（常驻通知前台服务）/ BootReceiver / NotificationDeletedReceiver
app（保留）：NotificationIntentRouteResolver、AppPermissionHost、AdDriver、InterstitialController
```

`AdDriver` 接口与 feature 模块边界不变；`InterstitialController.canShow` 保留。

---

## 5. 阶段计划

> 每阶段一个独立提交，可单独回滚；通过本地构建 + 冒烟后再进入下一阶段。

### 阶段 0：基线与安全网（0.5 天，不改代码）

- [ ] 记录新 aar 现状冒烟：开屏（UMP→广告→首页）、9 个 feature 入口、完成、返回、banner、常驻通知（展示 + 4 个 action）、通知点击直达、FCM 推送、开机重启、埋点、频控——作为回归对照。
- [ ] 广告平台侧确认：旧 SDK 的广告位（`AdAreaKeys` 的 `mainJunkCleanAdv` 等）在 policy 中已启用；账号沿用 AI_Cleaner 的 AdMob / MAX / TopOn。
- [ ] 确认 nexus 与 `libs.versions.toml` 状态（无旧 aar 已确认）。
- [ ] 建立分支或提交基线（在当前 `codex/refactor-v3` 上按阶段提交）。

### 阶段 1：模块引入与构建对齐（0.5–1 天）

改动文件：

- `settings.gradle.kts`：`include(":advertise")`。
- `app/build.gradle.kts`：`implementation(libs.pdffox.advertise)` → `implementation(project(":advertise"))`；移除 pangle exclude / `pag-sdk` / `tiktok-comp`（旧模块自带）。
- 复制 `DataRestoreFileRecovery/advertise/` → `AI_Cleaner/advertise/`，保留 `namespace com.pdffox.adv`。
- `advertise/build.gradle.kts`：固定 `latest.release`（facebook）与 `+` 适配器版本；对齐 Firebase BOM / ThinkingData / Singular / AppLovin / TopOn。
- `advertise/src/main/AndroidManifest.xml`：AdMob application id `ca-app-pub-3615322193850391~8557314343` → `ca-app-pub-3643549411810853~5913258493`（AI_Cleaner 的）；Facebook meta-data 置空或换 AI_Cleaner 值；与 app 现有 3 个 service 做去重。
- `libs.versions.toml`：移除 `pdffoxAdvertise` 引用（或标记弃用）。

验收：`:app:compileDebugKotlin` 通过（本地）。

### 阶段 2：初始化与 Activity（0.5–1 天）

改动文件：

- `app/src/main/java/.../app/MyApp.kt`：`Application` → `AdvApplicaiton`；`SdkInitializationCoordinator.advertiseInitializer` 内部改为 `Ads.init(this, BuildConfig.DEBUG, adPolicyRawResId = R.raw.ad_policy)`（suspend，可在主线程 scope 中 await，不必沿用 DataRestore 的 `runBlocking`）。
- `SdkInitializationCoordinator`：`awaitAdvertiseReady` 语义改为基于 `Ads.init` 完成的信号；`notificationDefaultsInitializer` 改为调用旧 `NotificationManager.updateNotificationConfig/updateNotificationContent`（见 §7）。
- `app/src/main/java/.../app/MainActivity.kt`：`AppCompatActivity` → `AdvActivity`；`ensureSdkPersistentNotificationRunning` 替换为 `startForegroundService(CommonService)`（见 §7）。
- `app/src/main/res/values` / `AndroidManifest.xml`：`MainActivity` 继承关系与 manifest 注册一致；新增 `CommonService`/`BootReceiver`/`NotificationDeletedReceiver` 声明（见 §7）。

验收：冷启动不崩；`Ads.init` 完成信号可观测；常驻通知可启动；通知权限流程正常。

### 阶段 3：适配层重写（1.5–2 天）

改动文件：

- `app/src/main/kotlin/.../platform/ads/AdvertiseSdkAdapter.kt`（140 行）→ 重写为旧 API 映射：
  - `init` → `Ads.init`
  - `initConsent` / `showSplashConsent` → `UMPUtil.initUMP` / `UMPUtil.showSplashUMP`
  - `showInterstitial` → `Ads.showInterstitialAd(activity as AdvActivity, areaKey, onClosed)`
  - `getBanner` → `Ads.getBannerAd`；`showRewarded` → `Ads.showRewardedAd`；`showOpenAd` → `Ads.showOpenAd`
  - `ensurePersistentNotificationServiceRunning` / `updateNotificationConfig` → 旧 `NotificationManager` 与 App 侧 `CommonService`（见 §7）
- `app/src/main/java/.../commercial/AdvertiseAdDriver.kt`：接口不变，`play` → `Ads.showInterstitialAd(activity as AdvActivity, areaKey, onClosed)`；`preload` → 旧 `AdLoader` / `AppOpenHelper` 时机；`initializeConsent` / `showConsent` / `showOpen` → `UMPUtil` / `ShowOpenAd`。
- `app/src/main/java/.../navigation/register/SplashRoutes.kt`：
  - 删除 `OpenAdHostContract` 与 `openAdHostLauncher`；
  - `SplashEffect.RunColdStartAd` → `UMPUtil.initUMP(activity)`（缓存命中直接进入）→ `AdConfig.canLoadOpen` → `Ads.showOpenAd(activity, AreaKey.openPageAdv, onClosed, onLoaded)`；
  - 保留 `SplashIntent.OpenAdStateChanged` / `OpenAdFinished` 状态机，回调改为旧 API 驱动；
  - 若确认旧 SDK 的 `AppOpenHelper.startObserve()` 已自动处理前台开屏，可进一步简化（阶段 3 内验证）。
- `app/src/main/kotlin/.../platform/ads/AdvertiseSdkPrivacyGateway.kt`：→ `UMPUtil.isPrivacyOptionsRequired` / `UMPUtil.showUMP`。
- `app/src/main/kotlin/.../platform/ads/AdHosts.kt`（banner / native / rewarded）：→ 旧 API。
- `app/src/main/kotlin/.../platform/analytics/SdkAnalyticsSink.kt`：`logEvent` / `setSuperProperties` / `setUserAttr` / `deviceId` → `LogUtil.log` / `ThinkingAttr` / `TDAnalytics`。
- `app/src/main/java/.../commercial/AdvertiseConfigFactory.kt`：删除（205 行），值已下沉到旧模块默认值。

验收：广告全链路可播可关不崩：开屏、入口、完成、返回、banner。

### 阶段 3.5：通知迁移（0.5–1 天，独立成节）

详见 §7。验收：常驻通知展示 + 点击 4 个 action + FCM 推送展示 + 开机重启 + 通知被删后重建。

### 阶段 4：清理、静态检查与冒烟（1 天）

- [ ] 删除：`OpenAdHostContract`、`AdvertiseConfigFactory`、`awaitRemoteConfigInitialization` 引用。
- [ ] 更新 `scripts/check-permission-ad-boundaries.ps1`：AdDriver 唯一实现检查、`AppPermissionHost` 计数、`PermissionGateway` 绑定检查不变；补一条禁止绕过 `AdDriver` 直接调用旧 SDK 的检查。
- [ ] 更新 `scripts/check-dependencies.ps1`：`:advertise` 模块的依赖方向（SDK 不依赖 app / feature）。
- [ ] 更新 `scripts/check-brand-residue.ps1`：旧模块 res 可能带 mar2 品牌字符串，需排除或清理。
- [ ] 更新 `scripts/check-product-config.ps1`：`-WhitePackage` 模式（若有使用）需排除 `:advertise`。

验收：静态检查全绿 + 冒烟矩阵通过。

---

## 6. 旧模块补丁点

### 6.1 广告（3 个文件，全部是加参数入口）

```kotlin
// 1) advertise/src/main/java/com/pdffox/adv/use/Config.kt
//    硬编码默认值改为 AI_Cleaner 的值（单 app，不引入注入）
object Config {
    var packageName = "com.aicleanerstorage.spaceclean"
    var privacyUrl = "https://sites.google.com/view/aicleanerstorageclean/home"
    var termsUrl = "https://sites.google.com/view/aicleaner-storageclean/home"
    var thinkingKey = "d4579ef4e15f4820ada24873dff21e07"
    var thinkingUrl = "https://mar2.top"
    var singularApiKey = "mar2game_f7b9272a"
    var singularSecret = "72b3df2ee5d0a64a6c404ce01937c3d6"
    var releaseHost = "https://api.newminigame.online"
    var testHost = "http://192.168.111.226:10002"
}

// 2) advertise/src/main/java/com/pdffox/adv/use/adv/AdvIDs.kt
//    默认值改为 AI_Cleaner 的 AdMob/MAX/TopOn（已有 setAdmobIDs/setMaxIDs，可直接调用或写死默认值）
//    AdMob: ca-app-pub-3643549411810853~5913258493 / 1611619626 / 3639017161

// 3) advertise/src/main/java/com/pdffox/adv/use/Ads.kt
//    init 增加 policy 资源参数（方案 A，单一来源）
suspend fun init(
    context: AdvApplicaiton,
    isTest: Boolean,
    adPolicyRawResId: Int? = null,
) {
    // ...
    if (adPolicyRawResId != null) {
        AdPolicyManager.setPolicyFromJson(
            context.resources.openRawResource(adPolicyRawResId).bufferedReader().use { it.readText() },
        )
    } else {
        AdPolicyManager.loadPolicyFromLocal(context)
    }
}
```

注意：`AdPolicyManager.setPolicyFromJson` 里的 `package_name` 不匹配 kill 进程逻辑保留——app 生成的 policy 已带正确包名。

### 6.2 通知（2 个补丁点）

```kotlin
// 4) advertise/src/main/java/com/pdffox/adv/use/push/MyFirebaseMessagingService.kt
//    硬编码 DataRestore 包名的两处改为 Config 字段：
//    广播 action: "com.filesrecovery.prodatarestrore.use.ACTION_NOTIFICATION_DELETED"
//    → Config.notificationDeletedAction（AI_Cleaner 自定义）
//    服务类名: "com.filesrecovery.prodatarestrore.use.notification.CommonService"
//    → Config.pushServiceClassName（= AI_Cleaner 的 CommonService FQCN）

// 5) advertise/src/main/java/com/pdffox/adv/use/Config.kt 追加
var notificationDeletedAction = "com.aicleanerstorage.spaceclean.ACTION_NOTIFICATION_DELETED"
var pushServiceClassName = "com.aicleanerstorage.spaceclean.app.notification.CommonService"
```

---

## 7. 通知迁移（常驻通知 + 后台推送）

### 7.1 旧 SDK 通知体系（两层）

- **SDK 模块**：`NotificationManager`（通知引擎：频道/组/RemoteViews/多 receiver，`notificationConfig`/`notificationContents` 为 public var，`updateNotificationConfig/updateNotificationContent` 可注入）+ `MyFirebaseMessagingService`（FCM）+ `PushManager`/`ServiceStarterJobService` + 模块 `res/raw/notification_config.json`/`push.json` + 远程 `PushUrl` 下发。
- **App 侧（从 DataRestore app 模块复制）**：`CommonService`（前台服务载体）、`BootReceiver`（开机重启）、`NotificationDeletedReceiver`（通知被删回调）、`PushConfig`、`ForegroundServiceRestartPolicy`。
- 启动方式：`MainActivity.onResume` → `startForegroundService(CommonService)`（旧 SDK 不会自动拉起服务）。

### 7.2 App 侧改动

1. 新增 5 个类（复制 DataRestore app 模块 → AI_Cleaner `app/notification/` 包），改包名与资源引用：
   - `CommonService`：前台服务，内部创建常驻通知（`NotificationManager` 引擎）；
   - `BootReceiver`：BOOT_COMPLETED 后重启 `CommonService`（受 `ForegroundServiceRestartPolicy` 控制）；
   - `NotificationDeletedReceiver`：接收 `ACTION_NOTIFICATION_DELETED` 广播，重新拉起常驻通知；
   - `PushConfig`：推送 JSON 配置（场景 key → Route 映射，如 `imageDeleted`/`videoDeleted`/`fileDeleted` → `/managePhotos` 等）；
   - `ForegroundServiceRestartPolicy`：前台服务重启节流策略。
2. `app/src/main/AndroidManifest.xml` 新增声明：
   - `<service android:name="...CommonService" android:foregroundServiceType="specialUse">` + `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` 文案（沿用 AI_Cleaner 现有文案）；
   - `<receiver android:name="...NotificationDeletedReceiver">`；
   - `<receiver android:name="...BootReceiver">`（BOOT_COMPLETED + ACTION_NOTIFICATION_DELETED）。
3. `MainActivity`：`ensureSdkPersistentNotificationRunning` → 权限检查通过后 `startForegroundService(CommonService)`（可复用现有 `persistentNotificationStartJob` 防重逻辑）。
4. **内容/配置注入**：`NotificationDefaultsInitializer` 保留（本地默认 + 远程 config），把 `updateConfig`/`updateContent` 指向旧 `NotificationManager`；本地默认 JSON 继续从 `core/ui` res/raw 读取。注意注入时机在 `Ads.init`（`startObservers` 从模块 res 加载）之后，或直接用注入覆盖。
5. **FCM**：用旧 SDK 的 `MyFirebaseMessagingService`；推送 data 协议对齐现有 aar 的格式（`Route`、`FcmTitle`/`FcmContent` 等），确保 `NotificationIntentRouteResolver` 能解析。
6. **图标/文案**：新 aar 的 `NotificationRouteMapping`（按 `AppDestination.notificationAliases()` 逐路由映射图标）是编程式能力；旧 SDK 走 JSON（`NotificationConfig`/`Notice`）。**验收点：如果 JSON 不支持 per-route 图标，图标会统一化——接受或改造**。

### 7.3 常驻通知点击动作

AI_Cleaner 现有 4 个常驻 action（`junk_files`、`file_manager`、`toolbox`、`battery_info`）与场景 key（`imageDeleted`/`videoDeleted`/`fileDeleted` → `/managePhotos` 等）需要迁到旧 SDK 的 `NotificationConfig`/`PushConfig` JSON 结构里；点击后的 Route 仍由 `NotificationIntentRouteResolver` 解析为 `AppDestination`。

---

## 8. 验证矩阵（本地真机冒烟）

| 用例 | 通过标准 |
|---|---|
| 冷启动开屏 | UMP → 开屏广告 → 首页；无广告时直接进首页，无白屏 |
| 入口广告 | 9 个 feature 入口各自正常播；来源黑名单/同 feature 不播的规则与现状一致 |
| 完成广告 | JunkClean / 文件管理完成 → 广告 → 完成回调（`InterstitialEffect` / `JunkCleanEffectHandler` 路径） |
| 返回广告 | 返回首页广告正常；`PermissionRejected` 路径正常 |
| banner | 首页底部 banner 展示 + 前后台生命周期正常 |
| 常驻通知 | 权限授予后展示；App 退后台后仍在；点击 4 个 action（junk_files/file_manager/toolbox/battery_info）各自直达 |
| 通知点击直达 | 推送/常驻通知点击 → `Route` → `NotificationIntentRouteResolver` → 对应页面 |
| FCM 推送 | 收到推送展示通知；点击直达 |
| 开机重启 | 重启设备后常驻通知恢复 |
| 通知被删 | 手动删除常驻通知后按策略重建 |
| 埋点 | ThinkingData / Singular / Firebase 事件上报（`SdkAnalyticsSink` 链路） |
| 无网络 / 无广告 | 降级路径不阻塞导航，不闪退 |
| 异常宿主 | 无 Activity / 后台时广告请求被跳过，不崩溃 |

---

## 9. 风险与回滚

| 风险 | 等级 | 缓解 |
|---|---|---|
| 旧 SDK 账号 / area key 与广告平台配置不匹配 → 广告全空 | 高 | 阶段 0 先确认；policy 资源由 app 生成，单一来源 |
| `Ads.init` 阻塞启动（DataRestore 用 `runBlocking`） | 中 | 异步初始化 + 完成信号，不阻塞首帧 |
| 旧模块混淆代码，补丁点易踩坑 | 中 | 只加 3 处广告参数入口 + 2 处通知补丁，不动播放/通知逻辑 |
| `MyFirebaseMessagingService` 硬编码 DataRestore 包名 | 中 | §6.2 补丁：广播 action 与服务类名改为 `Config` 字段 |
| 通知图标/文案可能统一化（per-route 映射降级） | 中 | 迁移时确认 JSON 结构；不接受则改常驻通知为单图标方案 |
| 常驻通知服务未启动（旧 SDK 不自动拉起） | 中 | `MainActivity.onResume` 显式 `startForegroundService`；`BootReceiver` 兜底 |
| manifest 服务 / Activity 冲突 | 中 | 阶段 1 逐项比对；只搬通知相关声明，不复制混淆 Activity |
| 埋点字段 / 事件名变化 | 低 | 对照旧 SDK `LogConfig` / `LogParams` |
| 依赖版本漂移（`latest.release`、`+`） | 中 | 全部固定版本 |
| 双路径广告遗留 | 低 | 本阶段不动，降级稳定后单独收口 |

**回滚**：每阶段独立提交；`com.pdffox:advertise:1.0.10` 的依赖声明保留在 git 历史，`git revert` 阶段 1 即回到新 aar。

---

## 10. 提交拆分

```
1. 引入广告 SDK 旧源码模块（构建对齐 + manifest + 依赖固定）
2. 旧 SDK 配置默认值（Config / AdvIDs / ad_policy 加载入口）
3. 广告初始化迁移（MyApp → AdvApplicaiton、MainActivity → AdvActivity、SdkInitializationCoordinator）
4. 广告适配层与开屏重写（AdvertiseSdkAdapter / AdvertiseAdDriver / SplashRoutes / 隐私 / AdHosts）
5. 通知迁移（CommonService / BootReceiver / NotificationDeletedReceiver / PushConfig + FCM 补丁）
6. 埋点迁移（SdkAnalyticsSink）
7. 清理与静态检查更新
```

---

## 11. 时间估算

| 阶段 | 估算 |
|---|---|
| 阶段 0 基线 | 0.5 天 |
| 阶段 1 模块引入 | 0.5–1 天 |
| 阶段 2 初始化 / Activity | 0.5–1 天 |
| 阶段 3 适配层重写 | 1.5–2 天 |
| 阶段 3.5 通知迁移 | 0.5–1 天 |
| 阶段 4 清理 + 冒烟 | 1 天 |
| **合计** | **约 5–7 天**（不含本地冒烟迭代） |

本环境不能跑完整 Gradle 构建；阶段 1 必须在本地先完整 build 一次，尽早暴露依赖冲突与 manifest 合并问题。

---

## 12. 待确认

1. **ad_policy 加载方式**：选方案 A（`Ads.init` 加一行参数读 app 生成的 `R.raw.ad_policy`，推荐）还是方案 B（复制 JSON 进模块）？
2. **通知图标**：接受 per-route 图标统一化（若旧 SDK JSON 不支持逐路由图标），还是单独改造？
3. **推送协议**：现有 aar 的 FCM data 格式（`Route`、`Fcm*` 字段、场景 key）与旧 SDK `MyFirebaseMessagingService` 是否完全兼容？阶段 0 抓一次现有推送报文对照。
4. **降级前验证**（见 §2.1）：StorageCleaner（1.0.15）是否复现同样问题？若没复现，先试把 `pdffoxAdvertise` 从 1.0.10 升到 1.0.15 再决定是否降级。

确认后从**阶段 1** 开始，先只做"模块引入 + 依赖替换 + 编译验证"，跑通再进阶段 2。
