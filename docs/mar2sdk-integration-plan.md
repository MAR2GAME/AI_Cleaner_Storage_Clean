# Mar2SDK 2.0.15 接入方案

本文档针对当前 `AI_Cleaner_Storage_Clean` 项目，按 `Mar2SDK` 2.0.15 和 `DataRestoreFileRecovery` 的 `new_sdk` 分支整理。文档描述接入边界、目标目录、关键代码、配置发布和人工验证流程。

## 1. 当前项目与参考项目的差异

当前项目已经具备以下能力：

- `MainActivity` 使用 Compose、类型化 Navigation 和 Koin。
- `MyApp` 直接继承 `Application`，只负责启动 Koin。
- Splash 已经有 `SplashViewModel`、最短视觉流程和通知权限引导。
- Home 已经是 `AppDestination.Main`，各功能页通过 `AppDestination` 导航。
- `AppRuntimeService` 为 App Lock 和后台电池采样提供前台服务，并发布自己的服务通知。
- `docs/configs` 已经存在一套未纳入 Git 的 SDK 风格配置文件。

当前项目尚未具备以下能力：

- Mar2SDK Maven 仓库、`core`、`impl` 和 `debug` 依赖。
- `Mar2Application`、`Core.init` 和 `AppOpenAdManager` 初始化。
- 使用 `AdActivity.showAd` 的开屏、插屏和通知返回广告。
- SDK 的常驻通知、APP 通知配置和通知点击元数据消费。
- 参考项目中的 UMP 流程关联、Splash 权限 Host 和通知点击状态机。
- 设置页的 `{APP版本}_{SDK版本}_{来源码}_{策略码}` 显示。

## 2. 接入前必须准备的外部资料

以下值无法从当前仓库推导，必须由广告、中台和 Firebase 配置方提供：

| 项目 | 用途 |
| --- | --- |
| GitHub Packages PAT | 拉取 `com.mar2game.mar2sdk:*:2.0.15` |
| Firebase `google-services.json` | Remote Config、Firebase 初始化和 SDK 配置拉取 |
| `serverAppID`、`serverClientKey`、`serverUrl` | SDK 自动登录、配置搜索和启动上报 |
| 正式 AdMob / MAX / TopOn / TradPlus / Unity ID | 生产广告展示 |
| RemoteConfig 条件和中台策略 | 买量模式、审核模式、线上模式分流 |
| 当前应用的真实通知文案与跳转业务 | `notification_content.json` 的 `Route` 映射 |

PAT 只放在用户级 Gradle 配置或 CI Secret 中，不写入项目文件。建议配置在 `%USERPROFILE%\\.gradle\\gradle.properties`：

```properties
gpr.user=MAR2GAME
gpr.key=<read:packages PAT>
```

## 3. 目标目录结构

下面的目录是在现有 layer-first 结构上增加 SDK 适配层后的目标结构。SDK 调用集中在 `app/integration` 和 `app/root`，业务 ViewModel 仍只处理页面事实和事件。

```text
app/src/main/
├─ AndroidManifest.xml
├─ java/com/aicleanerstorage/spaceclean/
│  ├─ app/
│  │  ├─ MyApp.kt
│  │  ├─ MainActivity.kt
│  │  ├─ integration/
│  │  │  ├─ Mar2AdGateway.kt
│  │  │  ├─ Mar2AdPlacementRegistry.kt
│  │  │  ├─ Mar2ConsentGateway.kt
│  │  │  ├─ Mar2NotificationGateway.kt
│  │  │  ├─ NotificationRouteCoordinator.kt
│  │  │  ├─ NotificationRouteHost.kt
│  │  │  ├─ NotificationPermissionRequestCoordinator.kt
│  │  │  ├─ PermissionRepository.kt
│  │  │  ├─ SplashNotificationPermissionHost.kt
│  │  │  └─ model/
│  │  │     ├─ NotificationRouteModels.kt
│  │  │     ├─ NotificationPermissionModels.kt
│  │  │     └─ SplashNotificationPermissionWait.kt
│  │  ├─ background/
│  │  ├─ di/
│  │  ├─ root/
│  │  └─ view/
│  │     ├─ splash/
│  │     ├─ home/
│  │     └─ settings/
│  ├─ base/
│  ├─ data/
│  └─ domain/
└─ res/
   ├─ layout/
   │  ├─ notification_1_mini.xml
   │  ├─ notification_1.xml
   │  ├─ notification_2_mini.xml
   │  ├─ notification_2.xml
   │  ├─ notification_3_mini.xml
   │  ├─ notification_3.xml
   │  ├─ notification_4_mini.xml
   │  └─ notification_4.xml
   ├─ raw/
   │  ├─ ad_config.json
   │  ├─ admob_config.json
   │  ├─ common_config.json
   │  ├─ log_config.json
   │  ├─ notification_config.json
   │  ├─ notification_content.json
   │  ├─ singular_config.json
   │  ├─ thinking_config.json
   │  ├─ max_config.json
   │  ├─ topon_config.json
   │  ├─ tradplus_config.json
   │  └─ unity_config.json
   ├─ values/strings.xml
   └─ xml/
      ├─ backup_rules.xml
      └─ data_extraction_rules.xml

docs/configs/
├─ ad_config_COMMON.json
├─ ad_config_RISK.json
├─ ad_config_NATURE.json
├─ ad_config_HIGH_VALUE.json
├─ ad_config_UNKNOW.json
├─ notification_config_COMMON.json
├─ notification_config_RISK.json
├─ notification_config_NATURE.json
├─ notification_config_HIGH_VALUE.json
├─ notification_config_UNKNOW.json
├─ notification_content.json
├─ common_config.json
├─ log_config.json
├─ admob_config.json
├─ max_config.json
├─ topon_config.json
├─ tradplus_config.json
├─ unity_config.json
├─ singular_config.json
└─ thinking_config.json
```

`docs/configs` 是上传到 RemoteConfig 的参数值集合。`app/src/main/res/raw` 是 APK 内的默认值集合。两者的 JSON 结构相同，但 RemoteConfig 的参数名带策略后缀，宿主 raw 文件名不带策略后缀。

## 4. Gradle 和 Application 初始化

### 4.1 根工程仓库和插件

在根 `build.gradle.kts` 增加 Google Services 插件：

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}
```

在 `settings.gradle.kts` 的 `dependencyResolutionManagement.repositories` 中加入 Mar2SDK 仓库和 SDK 所需的广告平台仓库：

```kotlin
maven {
    name = "Mar2SdkGitHubPackages"
    url = uri("https://maven.pkg.github.com/mar2game/mar2sdk")
    credentials {
        username = providers.gradleProperty("gpr.user").orNull ?: "x-access-token"
        password = providers.gradleProperty("gpr.key").orNull
            ?: providers.environmentVariable("GITHUB_TOKEN").orNull
            ?: throw GradleException("Missing Mar2SDK GitHub Packages credentials")
    }
    content { includeGroup("com.mar2game.mar2sdk") }
}
maven(url = "https://maven.singular.net/")
maven(url = "https://cboost.jfrog.io/artifactory/chartboost-ads/")
maven(url = "https://android-sdk.is.com/")
maven(url = "https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
maven(url = "https://artifact.bytedance.com/repository/pangle")
maven(url = "https://artifacts.applovin.com/android")
maven(url = "https://jfrog.anythinktech.com/artifactory/overseas_sdk")
```

### 4.2 app 模块依赖

在 `app/build.gradle.kts` 应用 Google Services，并显式声明三个版本一致的 SDK 模块：

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.gms.google-services")
}

dependencies {
    implementation("com.mar2game.mar2sdk:core:2.0.15")
    implementation("com.mar2game.mar2sdk:impl:2.0.15")
    debugImplementation("com.mar2game.mar2sdk:debug:2.0.15")
}
```

将 Firebase 控制台中与 `applicationId = "com.aicleanerstorage.spaceclean"` 完全一致的 `google-services.json` 放到 `app/google-services.json`。没有这个文件时，RemoteConfig、UMP 和部分 SDK 初始化不能按参考项目工作。

### 4.3 Application

文档源码中的 `Mar2Application` 支持覆盖 `appMod`，但 SDK 文档明确说明已发布的 Maven `2.0.15` 可能不包含这个属性。当前按 Maven 坐标接入时，使用普通 `Application` 显式初始化，既能选择 DEBUG/RELEASE，也不会依赖 AAR 与源码版本是否一致。SDK 初始化必须发生在 Koin 和业务代码使用 SDK 之前：

```kotlin
package com.aicleanerstorage.spaceclean.app

import android.app.Application
import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.app.di.dataModule
import com.aicleanerstorage.spaceclean.app.di.presentationModule
import com.mar2sdk.core.AppMod
import com.mar2sdk.core.Core
import com.mar2sdk.impl.AppOpenAdManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Core.init(this, if (BuildConfig.DEBUG) AppMod.DEBUG else AppMod.RELEASE)
        AppOpenAdManager.init(this)
        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(dataModule, presentationModule)
        }
    }
}
```

如果实际解析到的 `impl` AAR 已确认包含 `appMod`，也可以继承 `Mar2Application` 并只调用 `super.onCreate()`，但不能再调用 `Core.init` 或 `AppOpenAdManager.init`。两种方式只能选一种，避免重复初始化。

当前 `MyApp.onTrimMemory()` 中的图片缓存清理逻辑继续保留，放在 Koin 初始化之后或之前都不影响 SDK 初始化顺序。

当前 `AppRuntimeService` 仍负责 App Lock 和后台电池采样。SDK 的 `CommonService` 负责 SDK 常驻通知。两者职责不同，不能因为接入 SDK 就删除 `AppRuntimeService`，否则后台电池采样和 App Lock 会失效。后续若要求系统只保留一个前台服务通知，需要另行把这两个业务迁移到同一个服务。

## 5. 广告接入

### 5.1 统一广告入口

新增 `app/integration/Mar2AdGateway.kt`，所有全屏广告都从这里进入。Banner 和原生广告暂时不接入，原页面调用点注释或移除，`ad_banner` 和 `ad_native` 保持空对象，便于后续恢复。

`show` 只在主线程、当前 Activity 处于可交互状态时调用。广告点位准入、频控、概率和实际展示由 SDK 负责。

```kotlin
package com.aicleanerstorage.spaceclean.app.integration

import android.app.Activity
import com.mar2sdk.core.ad.AdConfig
import com.mar2sdk.core.ad.policy.ScreenAdContext
import com.mar2sdk.core.ad.policy.ScreenAdTrigger
import com.mar2sdk.core.ad.status.AdFormat
import com.mar2sdk.impl.AdActivity

object Mar2AdGateway {
    fun show(
        activity: Activity,
        areaKey: String,
        format: AdFormat,
        trigger: ScreenAdTrigger,
        fromRoute: String,
        toRoute: String,
    ): Boolean {
        if (activity.isFinishing || activity.isDestroyed) return false
        return AdActivity.showAd(
            activity,
            ScreenAdContext(
                areaKey = areaKey,
                adFormat = format,
                adPlatform = AdConfig.defaultPlatform,
                trigger = trigger,
                fromRoute = fromRoute,
                toRoute = toRoute,
            ),
        )
    }
}
```

`AdActivity.showAd` 自己执行全局开关、点位频控、概率和路由策略。宿主不再额外调用 `Core.logAdTrigger` 或 `AdPolicy.canShowAd`。

通知点击和新手引导需要等待广告页交接完成后继续业务动作。`showAd` 没有关闭回调，因此由宿主在 Activity 生命周期中观察一次 `ON_PAUSE -> ON_RESUME`，并为“未启动”分支立即继续：

```kotlin
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

fun showThenContinue(
    activity: ComponentActivity,
    context: ScreenAdContext,
    onContinue: () -> Unit,
) {
    var hostPaused = false
    lateinit var observer: LifecycleEventObserver
    observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) hostPaused = true
        if (event == Lifecycle.Event.ON_RESUME && hostPaused) {
            activity.lifecycle.removeObserver(observer)
            onContinue()
        }
    }
    activity.lifecycle.addObserver(observer)
    if (!AdActivity.showAd(activity, context)) {
        activity.lifecycle.removeObserver(observer)
        onContinue()
    }
}
```

实际实现中再用 `AtomicBoolean` 或状态机保证 `onContinue` 只执行一次。Activity 销毁、广告页启动异常和超时都走同一个继续分支。

### 5.2 广告位对应关系

广告 key 以 `feature/add-ads-sdk` 分支的 `config/ad_policy.json` 和 `AdAreaKeys.kt` 为准，接入时不得改名、合并或自行增加同义 key。`AdActivity.showAd` 的 `ScreenAdContext.areaKey` 必须使用下列字符串，最终的格式、频控和比例由 RemoteConfig 中实际生效的 `ad_config_*` 决定。

| 分类 | areaKey | 宿主调用位置 | 请求格式 |
| --- | --- | --- | --- |
| 开屏 | `openPageAdv` | Splash 完成通知权限、UMP 和最短展示时间后 | `OPEN` |
| 返回前台 | `foreGroundAdv` | 热启动通知点击，完成广告后再导航 | `OPEN` |
| 首页底部 Banner | `openMainBottomAdv` | Home 底部 Banner 预留点位 | `BANNER`，暂时关闭 |
| 新手引导完成 | `newGuideScanFinishAdv` | 扫描引导完成后进入 Home 前 | `INTER` |
| 新手引导跳过 | `newGuideSkipAdv` | 用户跳过扫描引导后进入 Home 前 | `INTER` |
| 清理完成 | `mainJunkCleanFinishAdv` | Junk 清理成功完成页 | `INTER` |
| 文件管理完成 | `mainFileManageFinishAdv` | Photos、Videos、Audios、Large Files、Duplicates、Documents 等文件功能完成页 | `INTER` |
| 通知清理完成 | `mainNotificationCleanFinishAdv` | Notification Cleaner 成功完成页 | `INTER` |
| WhatsApp 清理完成 | `mainWhatsappCleanFinishAdv` | WhatsApp Cleaner 成功完成页 | `INTER` |
| 网络测速完成 | `mainNetworkSpeedFinishAdv` | Network Speed 成功完成页 | `INTER` |
| 功能入口 | `mainJunkCleanAdv`、`mainFileManageAdv`、`mainNotificationCleanAdv`、`mainWhatsappCleanAdv`、`mainVirusAntiAdv`、`mainAppLockAdv`、`mainDeviceInfoAdv`、`mainBatteryInfoAdv`、`mainAppUsageAdv`、`mainNetworkUsageAdv`、`mainNetworkScanAdv`、`mainNetworkSpeedAdv` | 从 Home 进入对应功能页 | `INTER` |
| 返回 Home | `returnFromJunkCleanAdv`、`returnFromVirusAntiAdv`、`returnFromAppLockAdv`、`returnFromFileManageAdv`、`returnFromDeviceInfoAdv`、`returnFromBatteryInfoAdv`、`returnFromAppUsageAdv`、`returnFromNotificationCleanAdv`、`returnFromWhatsappCleanAdv`、`returnFromNetworkUsageAdv`、`returnFromNetworkScanAdv`、`returnFromNetworkSpeedAdv` | 从对应功能页返回 Home，或权限拒绝后回到 Home | `INTER` |

功能入口、完成页和返回 Home 的映射应集中在 `Mar2AdPlacementRegistry`，按 feature 分支的规则保持以下关系：所有文件管理子功能共用 `mainFileManageAdv`、`mainFileManageFinishAdv` 和 `returnFromFileManageAdv`；只有成功完成的功能触发完成点位；取消、失败或中途返回不误记为完成。

feature 分支源码还声明了 `rewardedVideoAdv` 默认 key，但 `config/ad_policy.json` 没有该点位，当前项目也没有对应业务流程，本次不接入、不写入 RemoteConfig。

Banner 和原生点位保留在点位注册表和 RemoteConfig 兼容说明中，但本轮暂时不调用。当前 `docs/configs/ad_config_*.json` 将 `ad_banner`、`ad_native` 置为空对象，并故意不把 `openMainBottomAdv` 放入生效的 `ad_units`，这样不会误触发 Banner。恢复 Banner 时再按 feature 分支的原 key 补回完整配置。原生点位调用全部停用，清单如下：

```text
mainSettingNativeAdv
mainJunkCleanFinishNativeAdv
mainVirusAntiFinishNativeAdv
mainFileManageFinishNativeAdv
mainNotificationCleanFinishNativeAdv
mainWhatsappCleanFinishNativeAdv
mainNetworkSpeedFinishNativeAdv
mainHomeCleanerNativeAdv
mainHomeFileManagerNativeAdv
mainHomeToolboxNativeAdv
mainFileAccessDialogNativeAdv
mainQuitFunctionDialogNativeAdv
mainQuitAppDialogNativeAdv
mainDeleteDialogNativeAdv
mainAppUsageAccessDialogNativeAdv
mainLocationAccessDialogNativeAdv
mainJunkScanFinishNativeAdv
mainVirusScanFinishNativeAdv
mainNotificationScanFinishNativeAdv
mainWhatsappScanFinishNativeAdv
mainBatteryInfo1NativeAdv
mainBatteryInfo2NativeAdv
mainBatteryInfo3NativeAdv
mainDeviceInfo1NativeAdv
mainDeviceInfo2NativeAdv
mainAppLockList1NativeAdv
mainAppLockList2NativeAdv
mainAppLockNativeAdv
```

原有打点事件仍由 SDK 的 `AdActivity.showAd` 统一记录。宿主只传入正确的 `areaKey`、`trigger`、`fromRoute` 和 `toRoute`，不再额外调用旧广告 SDK、`Core.logAdTrigger` 或自定义概率判断。

## 6. Splash 启动流程

启动顺序必须固定为：

```text
MainActivity 创建 Activity 级通知 Host
    -> Splash 进入即尝试通知运行时权限
    -> 权限结果、跳过、已授权恢复或 5 秒超时释放 Splash 等待
    -> Activity RESUMED 且有焦点时初始化 UMP
    -> UMP 信息更新成功且需要表单时展示 UMP
    -> 从 Splash 进入开始累计至少 3 秒
    -> 使用 AdActivity 展示 openPageAdv
    -> 广告页返回或启动失败
    -> 未完成两次新手引导时进入 OnboardingScan
    -> 完成或跳过引导后分别使用 newGuideScanFinishAdv 或 newGuideSkipAdv
    -> 广告页返回或启动失败后进入 Home
```

当前项目已有一个 `OnboardingScan` 页面和一个 `onboarding_scan_completed` 标记。参考项目实际使用两个“待完成”标记 `is_first_launch_1`、`is_second_launch_1`。要与参考项目的两次引导一致，需要把设置仓库扩展为两个标记：

```kotlin
fun isFirstOnboardingPending(): Boolean
fun isSecondOnboardingPending(): Boolean
fun markFirstOnboardingCompleted(): Result<Unit>
fun markSecondOnboardingCompleted(): Result<Unit>
```

建议将现有标记迁移为兼容读取，旧标记只用于判断历史用户是否已经完成，不覆盖两个新标记。第一次完成 Onboarding 写入 `is_first_launch_1=false`，第二次完成写入 `is_second_launch_1=false`。点击跳过使用 `newGuideSkipAdv`，正常扫描完成使用 `newGuideScanFinishAdv`。广告策略拒绝或启动失败也必须继续进入 Home。中途返回不消耗标记。Splash 根据两个标记决定进入下一次引导或 Home。现有单一 `isOnboardingScanCompleted()` 只能表达一次完成，不能满足“走 2 遍”。

UMP 协调器使用同一个 `ConsentFlow` 串起信息更新和表单：

```kotlin
class Mar2ConsentGateway {
    fun begin() = Core.beginConsentFlow(scene = "startup")

    fun update(activity: Activity, flow: ConsentFlow, onResult: (Boolean) -> Unit): Boolean =
        Core.initConsent(activity, flow, onResult)

    fun showForm(activity: Activity, flow: ConsentFlow, onDismiss: () -> Unit) {
        Core.showSplashConsent(activity, flow, onDismiss)
    }

    fun finish(flow: ConsentFlow, cancelled: Boolean) {
        Core.endConsentFlow(flow, cancelled)
    }
}
```

Splash 的 `LaunchedEffect` 只负责调用 Host 和协调器。开屏广告启动条件必须包含当前 Splash entry、Activity 和 entry 都是 `RESUMED`、窗口有焦点、没有权限或外链弹层、最短 3 秒已经结束。

## 7. 通知授权弹框

参考项目有三种界面：Android 系统授权框、首页自定义说明框、系统通知设置页。三者必须分开处理。

### 7.1 权限状态

首页状态使用 SDK 的完整状态读取：

```kotlin
val channelIds = AppNotificationUtil.getConfiguredChannelIds()
val access = NotificationUtil.getAccessState(context, channelIds)
val hasNotificationPermission = !access.needsSettings
```

运行时权限只决定是否可以调用 Android launcher。首页提示条和自定义说明框使用 `needsSettings`，还要包含应用通知总开关和 SDK APP 通知通道状态。

### 7.2 冷启动和 Home 两次机会

`MainActivity.onCreate(savedInstanceState)` 中先创建 Activity 级 `SplashNotificationPermissionHost`。新 Activity 每轮恢复 Splash 和 Home 各一次机会，Activity 重建保留 pending request、requestId、来源和 5 秒起点。

核心请求流程如下：

```kotlin
fun request(source: NotificationPermissionRequestSource): Long? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return null
    if (NotificationUtil.getAccessState(activity).runtimePermissionGranted) return null
    if (coordinator.isPending || coordinator.used(source)) return null

    val requestId = coordinator.begin(source) ?: return null
    launcher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
    return requestId
}
```

系统回调必须使用 requestId 过滤迟到结果。缺少 `POST_NOTIFICATIONS` 键的回调记为未知，不改写成 false。Splash 从开始准备申请计时 5 秒。超时只释放 Splash 等待，不伪造拒绝，也不撤销 SDK 共享的 pending 状态。

Home 首次 `RESUMED` 时再尝试 HOME 来源。有效 Boolean 回调且 `needsSettings` 仍为 true 时显示自定义 `NotificationPermissionGuideDialog`。用户点击通知提示条时只打开自定义说明框，不重新消耗系统权限申请机会。

### 7.3 设置页处理

确认按钮按以下顺序打开设置：

1. 存在被关闭的 SDK 通知通道时打开通道设置。
2. 打开应用通知设置。
3. 打开应用详情。
4. 打开系统设置作为最后回退。

打开设置前调用 `AppOpenAdManager.suppressNextForegroundAd()`。启动失败时调用 `completeOriginalOperation()`，回到 Home 后重新读取实际权限，不把“打开过设置页”当作已授权。

## 8. 通知样式和常驻通知

### 8.1 SDK 常驻通知

业务层通过以下入口启动 SDK 常驻通知：

```kotlin
import com.mar2sdk.core.Core
import com.mar2sdk.core.notify.NotificationUtil

override fun onResume() {
    super.onResume()
    if (NotificationUtil.hasNotiAccess()) {
        Core.startFGS()
    }
}
```

`Core.startFGS()` 使用 SDK 的 `CommonService` 和 `notification_config.json`。原来的 `ForegroundNotification` 只服务于 `AppRuntimeService`，不能把它当作 SDK 常驻通知的实现。若产品要求最终只出现一个常驻通知，需要把 App Lock 和后台电池采样的前台服务设计重新合并。

### 8.2 保持常驻通知按钮样式

在 `app/src/main/res/values/strings.xml` 覆盖 SDK 同名资源：

```xml
<string name="common_notification_action_1">Photos</string>
<string name="common_notification_action_2">Files</string>
<string name="common_notification_action_3">Videos</string>
<string name="common_notification_action_4">Recovered</string>
```

上面是 SDK 和参考项目的默认四项。若当前产品旧通知的文案、顺序或图标不同，以旧版本实际布局逐项替换，Route 仍固定为 `Action1`～`Action4`。在 `app/src/main/res/drawable/` 放置同名 `notification_photos`、`notification_files`、`notification_videos`、`notification_recovered` 资源，不能只覆盖文字而遗漏图标。

### 8.3 保持 APP 通知样式

SDK APP 通知样式由 `styles` 选择。为了保持现有样式，复制旧布局到 `app/src/main/res/layout/` 并保留 SDK 写入的 ID：

```text
notification_N_mini.xml  root, iv_push, tv_detail, button
notification_N.xml       root, iv_push, tv_title, tv_message, button
```

`notification_config_*` 的每个场景先设置为原来使用的样式编号。若原项目只有一种样式，使用 `styles: [1]`，不要一开始改成 `[1, 2, 3, 4]`，这样人工核对更容易。

## 9. 通知点击先广告后跳转

将当前 Manifest 的 `singleTop` 改为 `singleTask`。这样冷启动走 `onCreate`，已有实例走 `onNewIntent`，两条入口可以共用同一个 pending 路由状态机：

```xml
<activity
    android:name="com.aicleanerstorage.spaceclean.app.MainActivity"
    android:launchMode="singleTask"
    android:exported="true" />
```

SDK 会把以下 extras 放入启动 Intent：

```text
AppOpenFrom = persistent 或 app_push
Scene       = 场景名
Route       = notification_content.json 中的 Route
Mar2SDK.NotificationActionId
```

`MainActivity` 在 `onCreate` 和 `onNewIntent` 中都调用同一个解析函数。冷启动的通知先进入 Splash，Home 就绪后消费。热启动的通知在 Home 已就绪时先展示 `foreGroundAdv`，广告页返回后再导航。

核心模型：

```kotlin
data class NotificationRouteAction(
    val id: String,
    val source: String,
    val scene: String,
    val destination: AppDestination,
)

enum class NotificationRoutePhase {
    WAITING_FOREGROUND_AD,
    SHOWING_FOREGROUND_AD,
    PENDING,
    NAVIGATING,
}
```

接收 Intent 的核心代码：

```kotlin
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleNotificationIntent(intent, warmDelivery = true)
}

private fun handleNotificationIntent(incoming: Intent, warmDelivery: Boolean) {
    setIntent(incoming)
    NotificationUtil.trackAppOpen(incoming)
    val rawRoute = incoming.getStringExtra("Route")
    val destination = notificationDestination(rawRoute) ?: return
    val actionId = NotificationRouteIntent.ensureActionId(incoming) ?: return
    val action = NotificationRouteAction(
        id = actionId,
        source = incoming.getStringExtra("AppOpenFrom").orEmpty(),
        scene = incoming.getStringExtra("Scene").orEmpty(),
        destination = destination,
    )
    notificationRoutes.accept(
        action = action,
        waitForForegroundAd = warmDelivery && isHomeReady(),
    )
}
```

在 `NotificationRouteHost` 中，`WAITING_FOREGROUND_AD` 阶段调用：

```kotlin
Mar2AdGateway.show(
    activity = activity,
    areaKey = "foreGroundAdv",
    format = AdFormat.OPEN,
    trigger = ScreenAdTrigger.RETURN,
    fromRoute = "main",
    toRoute = pending.destination.routeKey,
)
```

无论广告策略拒绝、广告页启动失败还是广告页正常返回，都必须把状态推进到 `PENDING`，然后只导航一次。每个 actionId 只能完成一次，新的通知点击会替换旧 pending，并把旧动作记录为 `SUPERSEDED`。

当前项目使用类型化导航，因此 `Route` 映射建议集中在 `notificationDestination`：

```kotlin
private fun notificationDestination(route: String?): AppDestination? = when (route) {
    "/junkClean" -> AppDestination.JunkClean
    "/virusAnti" -> AppDestination.AntiVirus
    "/appLock" -> AppDestination.AppLock
    "/managePhotos" -> AppDestination.PhotosManager
    "/manageSimilarPhotos" -> AppDestination.SimilarPhotosManager
    "/manageVideos" -> AppDestination.VideosManager
    "/appUsage" -> AppDestination.AppUsage
    "/checkBatteryInfo" -> AppDestination.BatteryInfo
    "/notificationClean" -> AppDestination.NotificationCleaner
    "/whatsapp" -> AppDestination.WhatsAppCleaner
    "/networkUsage" -> AppDestination.NetworkUsage
    "/networkScan" -> AppDestination.NetworkScan
    "/networkSpeed" -> AppDestination.NetworkSpeed
    else -> null
}
```

上面的列表与当前 `docs/configs/notification_content.json` 的 Route 集合一致。常驻通知的 `Action1`～`Action4` 是 SDK 固定的按钮 Route，不能直接当作普通通知的斜杠 Route。接入前从旧版本产品定义确认四个按钮各自目标，再在同一个解析器中单独建立映射。未知 Route 记录拒绝原因并清除 pending，不能降级跳转到 Home。

## 10. 设置页版本号

当前 `app/build.gradle.kts` 的 APP 版本是 `1.0.7`。设置页展示值不应手写 SDK 版本，也不应把展示串写回 `versionName`。

新增 `app/integration/Mar2VersionLabel.kt`：

```kotlin
package com.aicleanerstorage.spaceclean.app.integration

import com.mar2sdk.core.Core
import com.mar2sdk.core.common.RiskUtil
import com.mar2sdk.core.common.status.UserType

fun mar2VersionLabel(appVersionName: String): String {
    val snapshot = RiskUtil.getPolicySourceSnapshot()
    val (source, type) = when {
        snapshot.serverAdPolicyUserType != UserType.UNKNOW ->
            "S" to snapshot.serverAdPolicyUserType
        snapshot.localAdPolicyUserType != UserType.UNKNOW ->
            "L" to snapshot.localAdPolicyUserType
        else -> "U" to UserType.UNKNOW
    }
    val policy = when (type) {
        UserType.RISK -> "R"
        UserType.COMMON -> "C"
        UserType.NATURE -> "N"
        UserType.HIGH_VALUE -> "H"
        UserType.UNKNOW -> "U"
    }
    return "${appVersionName}_${Core.SDK_VERSION}_${source}_${policy}"
}
```

在 `SettingsRoute` 进入和恢复时读取 `BuildConfig.VERSION_NAME`，把结果传给设置页。根据 SDK 文档，当前正常显示类似 `1.0.7_2.0.15_L_C`。`U_U` 表示启动时尚未得到策略来源记录，不代表广告一定关闭。

## 11. 配置文件规则

### 11.1 APK raw 默认配置

从 `docs/configs` 选定一份审核基线复制到 `app/src/main/res/raw`，并将 `common_config.json` 的 `package` 改成 `com.aicleanerstorage.spaceclean`。广告 ID、服务器 ID 和 Singular、ThinkingData 凭据必须替换成当前应用自己的值。

`ad_config.json` 至少保留：

- `openPageAdv`，用于 Splash。
- `foreGroundAdv`，用于通知点击后的返回广告。
- 当前原有打点对应的各个 `main*Adv` 和 `return*Adv`。
- `ad_native: {}` 和 `ad_banner: {}`，表示暂时不启用原生和 Banner。

`openMainBottomAdv` 虽然属于 feature 分支的 Banner 点位，当前 `ad_config_*` 不加入其 `ad_units`，避免配置存在但页面误创建 Banner。恢复 Banner 时必须使用同名 key，并同步补齐 RemoteConfig 和中台配置。

`notification_config.json` 必须包含 `home_recent`。只保留原来实际使用的样式编号，`isSend` 是否打开由测试阶段和线上策略决定。

### 11.2 RemoteConfig 参数

上传参数名使用 SDK 规定的策略后缀：

```text
common_config
ad_config_COMMON
ad_config_RISK
ad_config_NATURE
ad_config_HIGH_VALUE
ad_config_UNKNOW
notification_config_COMMON
notification_config_RISK
notification_config_NATURE
notification_config_HIGH_VALUE
notification_config_UNKNOW
notification_content
admob_config
max_config
topon_config
tradplus_config
unity_config
singular_config
thinking_config
log_config
```

值直接使用 JSON 对象，不再包一层策略名。`common_config`、广告策略和通知策略必须放在同一 RemoteConfig 条件中，避免 ABTestName 与实际策略来自不同分组。

## 12. 买量、审核和线上发布策略

### 12.1 两个固定测试版本

建议使用：

| 测试版本 | 用途 | RemoteConfig / 中台策略 |
| --- | --- | --- |
| `1.0.8` | 买量模式 | 广告打开、买量策略、通知样式和点击广告全部打开 |
| `1.0.9` | 审核模式 | 广告按审核策略关闭或低频，通知按审核策略，保留功能可用性 |

这两个版本只用于测试，不作为线上发布版本。正式线上版本可以继续使用 `1.0.7` 之后的独立版本号，例如 `1.0.10`。

### 12.2 上架顺序

上架流程固定为：

```text
提交审核
    -> 审核期间不配置线上策略
    -> 默认命中审核策略
    -> 审核通过
    -> 同时修改 RemoteConfig 和中台线上策略
    -> 人工核对发布内容
    -> 手动发布线上策略
```

提交审核时不要自动发布线上买量策略。未命中线上策略时，SDK 默认走审核策略，因此审核包需要具备可接受的默认 raw 配置。

## 13. 人工验收清单

### 构建和配置

- `core`、`impl`、`debug` 均为 2.0.15。
- `google-services.json` 的包名与 `applicationId` 一致。
- `app/src/main/res/raw` 文件完整，`common_config.package` 正确。
- 正式包不再使用测试广告 ID。
- `log_config.netEvents` 按需要加入 `ad_trigger`、`ad_policy`、UMP 和通知事件。
- `notification_config` 的实际生效策略含 `home_recent`。

### 广告

- 买量版本和审核版本各安装一次，分别核对 RemoteConfig 分组。
- Splash 开屏使用 `AdActivity`，日志和 Activity 栈中不能出现旧 Banner 或原生展示入口。
- 通知点击返回广告使用 `foreGroundAdv` 和 `AdActivity`。
- 原有广告位 key 和打点事件名称仍然存在。
- Banner 和原生广告暂时不展示。

### 通知

- 常驻通知由 SDK `CommonService` 发布，按钮文案、图标和点击目标与旧样式一致。
- APP 通知分别发送 1、2、3、4 四种样式，收起和展开布局均显示正常。
- 冷启动进入 Splash 时尝试一次通知授权。
- 到达 Home 首次 `RESUMED` 时再尝试一次通知授权。
- 系统拒绝后显示自定义通知说明框，确认后进入系统设置。
- 通知点击先看到广告页，广告结束后才进入目标功能页。
- 冷启动、热启动、重复点击和旋转重建都不会重复导航。

### 版本号

- 设置页显示格式为 `{APP_VERSION}_{2.0.15}_{L|S|U}_{R|C|N|H|U}`。
- 当前 APP 版本为 `1.0.7` 时，展示串首段必须是 `1.0.7`。
- 策略异步更新后重新进入设置页能够刷新来源和策略码。

## 14. 已确认的决策与接入前数据

1. `AppRuntimeService` 暂时保留，继续负责 App Lock 和后台电池采样。SDK `CommonService` 单独负责 SDK 常驻通知，接入时不能删除或替换前者。
2. 广告点位完全参考 `feature/add-ads-sdk` 分支，点位表以本方案第 5.2 节为准。Banner 和原生点位保留配置兼容性，本轮调用关闭。
3. 当前 `docs/configs/notification_content.json` 已使用 `/junkClean`、`/virusAnti`、`/appLock`、`/managePhotos`、`/manageSimilarPhotos`、`/manageVideos`、`/appUsage`、`/checkBatteryInfo`、`/notificationClean`、`/whatsapp`、`/networkUsage`、`/networkScan`、`/networkSpeed`，实现时按第 9 节映射。常驻通知 `Action1`～`Action4` 仍需从旧版本确认四个按钮目标，不能套用普通通知 Route。

