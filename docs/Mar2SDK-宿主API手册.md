# Mar2SDK 2.0.15 宿主 API 手册

> 只讲**你能调用的东西**：包名、签名、返回值语义、可见性边界。
> 内部实现（策略怎么算、竞价怎么排）见 [广告模块解析](Mar2SDK-广告模块解析.md)；接入步骤见 [接入方案 v2](Mar2SDK-2.0.15-接入方案-v2.md)。

---

## 0. 三十秒接入

```kotlin
// 1. 依赖（core 必须显式声明，见 §1）
implementation("com.mar2game.mar2sdk:core:2.0.15")
implementation("com.mar2game.mar2sdk:impl:2.0.15")

// 2. 初始化
Core.init(this, AppMod.RELEASE)

// 3. 全屏广告：一个 Activity + 一个点位
AdActivity.showAd(activity, ScreenAdContext(
    areaKey = "openPageAdv",
    adFormat = AdFormat.OPEN,
    adPlatform = AdConfig.defaultPlatform,
    trigger = ScreenAdTrigger.ENTER,
))

// 4. 通知权限（Android 13+）
NotificationUtil.reqNotiAccess(activity, launcher) { granted -> if (granted) Core.startFGS() }
```

---

## 1. 依赖与可见性

### 1.1 坐标

```kotlin
implementation("com.mar2game.mar2sdk:core:2.0.15")      // 必须显式：impl 用 implementation 传递，宿主编译期看不到 core
implementation("com.mar2game.mar2sdk:impl:2.0.15")      // AdActivity / AppOpenAdManager / Compose 助手
debugImplementation("com.mar2game.mar2sdk:debug:2.0.15")// 仅调试页
```

> 只有 `impl` 不声明 `core` 时，`Core`、`AdConfig`、`AdFormat` 这些会报 `unresolved reference`。

### 1.2 哪些能调、哪些是内部 API

| 能调（public） | 不能调（internal，宿主编译不过） |
|---|---|
| `Core` / `AppMod` / `AdStatus` | `AdConfig.applyConfig`（远程配置专用） |
| `AdActivity.showAd` | `AdmobConfig.applyConfig` / `MaxConfig.applyConfig` 等 |
| `Core.showAd` / `AdShower.*` / `AdLoader.fillAd` | `FullscreenAuction`（777/888 的实现） |
| `AdPolicy.canShowAd` | `AdIniter.ensureInitialized` |
| `AdConfig`（字段可读可写 + `saveAdConfig`） | `RiskUtil.applyJson` / `RiskUtil.activeRemoteConfig` |
| `AdmobConfig`（字段可读可写 + `saveAdmobConfig`） | `NotificationContentSelector` |
| `ScreenAdContext` / `ShowCallback` / `AdFormat` / `AdPlatform` | `AdPolicy.queryAutoRateImpressionCounts` |
| `NotificationUtil` / `AppNotificationUtil` / `NotificationRouteIntent` | `AdmobShower` 内部类 |
| `RiskUtil.getPolicySourceSnapshot` / `UserType` | `ScreenAdSession` / `PageAdRequestState` |

换句话说：**`com.mar2sdk.core.ad.impl.*` 下的 `Shower/Loader/Config` 类本身多是 public，但分析/竞价/配置应用这些“内部动作”是 internal**；宿主只用 `AdShower` 的门面方法。

---

## 2. 初始化

```kotlin
// com.mar2sdk.core.Core
object Core {
    const val SDK_VERSION: String            // "2.0.15"，构建时注入
    lateinit var app: Application
    lateinit var appMod: AppMod
    var userType: UserType                   // 动态
    var testMod: TestMod
    val isPrivacyOptionsRequired: Boolean

    fun init(app: Application, appMod: AppMod)
}

enum class AppMod { DEBUG, TEST, PRE_RELEASE, RELEASE }
```

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Core.init(this, if (BuildConfig.DEBUG) AppMod.DEBUG else AppMod.RELEASE)
    }
}
```

要点：

- `AppMod.DEBUG/TEST` 强制使用 AdMob **官方测试广告位**，`RELEASE` 才用 `admob_config` 里的正式 ID；
- 必须在**任何** `Core.*`、`AdActivity`、`NotificationUtil` 调用之前完成；
- 不要重复 `Core.init`（会重复初始化 Firebase/Singular/通知观察者）；
- 也可以继承 `com.mar2sdk.impl.Mar2Application`，但它会自己 `Core.init` + `AppOpenAdManager.init`；**Maven 2.0.15 包里的 `Mar2Application` 没有 `appMod` / `isPayApp` 两个属性**（那是工作树新增的），所以用 Maven 时建议自己调 `Core.init`，别覆写属性。

---

## 3. 全屏广告（最常用）

### 3.1 三个入口，选一个

| 入口 | 签名 | 有回调 | 跑策略/频控 | 什么时候用 |
|---|---|---|---|---|
| **`AdActivity.showAd`** | `fun showAd(activity: Activity, adContext: ScreenAdContext): Boolean` | ❌ | ✅ 会（`ad_trigger` + 11 项） | **推荐**。开屏/插屏/激励，宿主不用管 Activity |
| `Core.showAd` | `suspend fun showAd(activity: Activity, callback: ShowCallback): AdShowStatus` | ✅ | ❌ **不会** | 已经在自己的 Activity 里、需要展示结果/奖励回调 |
| `AdShower.showXxx` | `suspend fun showOpen/showInter/showVideo/...` | ✅ | ❌ 不会 | 低层单平台单格式，**跳过 autoRate 与 showMod** |

> ⚠️ **只有 `AdActivity.showAd` 会执行 `AdPolicy`（点位存在性、频控、概率、路由）并打 `ad_trigger`。**
> 直接调 `Core.showAd` / `AdShower.showXxx` = 绕过全部策略：不看 `isOpen`、不看 `1HMax/interval`、不看 `ad_units` 里有没有这个点位。
> 如果确实要直连（比如自建广告页），策略与 `Core.logAdTrigger(ctx)` 都要宿主自己补。

`AdActivity.showAd` 返回值：

- `true` = 广告页已启动（**不代表加载成功，更不代表曝光**）；
- `false` = 策略拒绝（`AdPolicy.canShowAd`）、宿主 Activity 已结束、已有广告页在展示、或 `startActivity` 抛异常。

因为没有回调，**判断“广告播完了”只能靠生命周期**：

```kotlin
// 宿主 Activity 里：广告页返回 = ON_PAUSE 之后的下一次 ON_RESUME
private var paused = false
private var adShowing = false

fun playSplashAd() {
    adShowing = AdActivity.showAd(this, splashContext())
    if (!adShowing) onAdFlowFinished()
}

override fun onPause() { paused = true; super.onPause() }

override fun onResume() {
    super.onResume()
    if (adShowing && paused) {
        adShowing = false
        onAdFlowFinished()
    }
}
```

### 3.2 `ScreenAdContext`（必填字段的样子）

```kotlin
// com.mar2sdk.core.ad.policy
@Parcelize
data class ScreenAdContext(
    val requestId: String = UUID.randomUUID().toString(),
    var areaKey: String = "preload",     // 必须与 ad_config 的 ad_units 键一致
    var adFormat: AdFormat,
    var adPlatform: AdPlatform,
    val trigger: ScreenAdTrigger,        // ENTER / RETURN / LEAVE / UNKNOW，仅入日志
    val fromRoute: String = "",
    val toRoute: String = "",
    var adUnitId: String = "",           // SDK 会回写实际广告位
    val preloadScene: String? = null,
)
```

| 字段 | 谁写 | 说明 |
|---|---|---|
| `areaKey` | 宿主 | **决定策略与频控**；不存在 → `placement_config` 拒 |
| `adFormat` | 宿主（会被回写） | 请求意图；`AdPolicy` 通过后会改成配置里的 `format` |
| `adPlatform` | 宿主（会被回写） | 请求平台；777 会改成实际平台 |
| `trigger` | 宿主 | 只进日志 |
| `fromRoute`/`toRoute` | 宿主 | 与 `fromRoutes`/`toRoutes` 完整匹配（大小写敏感） |
| `adUnitId` | SDK | 不要自己填 |

### 3.3 `ShowCallback`（走 `Core.showAd` 时才需要）

```kotlin
// com.mar2sdk.core.ad.callback
interface ShowCallback {
    val adContext: ScreenAdContext
    fun canShowAd(): Boolean = true          // 主线程、展示前最后一刻复查
    fun onShowCommitted() = Unit             // 即将调用 SDK show（可在这里落“已占用”）
    fun onShowAttemptFailed() = Unit         // 已提交但曝光前失败（换候选前恢复宿主状态）
    fun showFailed(reason: ShowFailResult)   // 最终失败，最多一次
    fun showSuccess()                        // 真实曝光
    fun onClicked()
    fun onAdClosed()                         // 关闭；可能没有先收到 showSuccess
    fun onPaid()                             // 收益回调
    fun onReward()                           // 激励奖励
}
```

宿主必须遵守：

- `canShowAd()` 里只做**廉价同步判断**（`!isFinishing && lifecycle >= RESUMED`），别查网络；
- `showSuccess()` 与 `onAdClosed()` **不是成对**：可能只来关闭；
- 奖励只看 `onReward()`，关闭/成功都不算发奖。

### 3.4 最小示例

**开屏（`AdActivity`）**

```kotlin
val context = ScreenAdContext(
    areaKey = "openPageAdv",
    adFormat = AdFormat.OPEN,
    adPlatform = AdConfig.defaultPlatform,
    trigger = ScreenAdTrigger.ENTER,
    fromRoute = "splash",
    toRoute = "home",
)
if (!AdActivity.showAd(this, context)) goHome()
```

**插屏 + 结果（`Core.showAd`）**

```kotlin
lifecycleScope.launch {
    val status = Core.showAd(
        this@MainActivity,
        object : ShowCallback {
            override val adContext = context
            override fun canShowAd() = !isFinishing && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            override fun showFailed(reason: ShowFailResult) = goNext()
            override fun showSuccess() = Unit
            override fun onClicked() = Unit
            override fun onAdClosed() = goNext()
            override fun onPaid() = Unit
            override fun onReward() = Unit
        },
    )
    if (status != AdShowStatus.SHOW_SUCCESS) goNext()
}
```

### 3.5 预加载

```kotlin
// 只是把池填满，不展示；重复调用会被 SDK 合并
lifecycleScope.launch { AdLoader.fillAd(LogAdParam.scene_app_cold_start) }
```

可选场景值：`scene_app_cold_start`、`scene_app_foreground`、`scene_ad_consumed`、`scene_ad_show_failed`、`scene_ad_abandoned`、`scene_ad_invalidated`。

### 3.6 策略自检（只在自建入口时需要）

```kotlin
// 仅当你不走 AdActivity、自己调 Core.showAd 时，才需要补这两步
Core.logAdTrigger(context)
if (!AdPolicy.canShowAd(context)) return
```

> ⚠️ `AdActivity.showAd` 内部已经做了这两步：**再自己判一次会多打一条 `ad_policy`、概率项也会被掷两次**（一次自检、一次内部），导致“自检通过但实际没播”。走 `AdActivity` 时不要自检。

---

## 4. Banner 与原生（内嵌，不走 AdActivity）

```kotlin
// com.mar2sdk.core.Core
fun getBanner(activity: Activity, callback: ShowCallback, adSize: AdSize = AdSize.BANNER): AdView?
suspend fun getNative(activity: Activity, callback: ShowCallback, adIndex: Int = 0): NativeAdResult?
suspend fun getAdmobNative(activity: Activity, callback: ShowCallback, adIndex: Int = 0): NativeAd?
```

生命周期（**必须自己管**）：

```kotlin
// Banner
banner = Core.getBanner(this, callback)?.also { container.addView(it) }
override fun onPause()   { banner?.pause() }
override fun onResume()  { banner?.resume() }
override fun onDestroy() { banner?.let { (it.parent as? ViewGroup)?.removeView(it); it.destroy() }; banner = null }

// 原生
when (val ad = Core.getNative(this, callback)) {
    is NativeAdResult.Admob    -> nativeAdView.setNativeAd(ad.ad)      // 销毁 ad.ad.destroy()
    is NativeAdResult.Max      -> ad.handle.render(maxNativeView) or destroy()
    is NativeAdResult.Topon    -> ad.handle.render(toponView) or destroy()
    is NativeAdResult.Tradplus -> ad.handle.render(container, R.layout.xxx) or destroy()
    null -> Unit
}
```

平台专用（当统一入口不满足时）：

| 平台 | Banner | 原生 |
|---|---|---|
| MAX | `Core.getMaxBanner(activity, callback, MaxAdFormat.BANNER): MaxAdView?` | `Core.getMaxNative(...): MaxNativeAdHandle?` |
| TopOn | `Core.getToponBanner(activity, callback, widthDp=320, heightDp=50): ATBannerView?` | `Core.getToponNative(...)` |
| TradPlus | `Core.getTradplusBanner(activity, callback, widthDp, heightDp): TPBanner?` | `Core.getTradplusNative(...)` |
| Unity | `Core.getUnityBanner(activity, callback): LevelPlayBannerAdView?` | 不支持（返回 null） |

`Core.getBanner` **只对 AdMob 有效**：MAX/TopOn/TradPlus 会 `require` 直接抛异常，必须用专用入口；Unity 走同一入口时返回 `null` 并回调 `showFailed(LOAD_AD_EXCEPTION)`，Banner 要用 `getUnityBanner`。

---

## 5. UMP（同意管理）

```kotlin
// com.mar2sdk.core.Core
fun beginConsentFlow(scene: String = "startup"): ConsentFlow
fun initConsent(activity: Activity, flow: ConsentFlow, onComplete: (success: Boolean) -> Unit): Boolean  // 返回 true = 命中缓存且不回调
fun showSplashConsent(activity: Activity, flow: ConsentFlow, onComplete: () -> Unit)
fun endConsentFlow(flow: ConsentFlow, cancelled: Boolean = false)
fun canRequestAds(): Boolean
val isPrivacyOptionsRequired: Boolean
fun showPrivacyOptions(activity: Activity)
// 旧签名（不传 flow，各自独立事件，不拼启动漏斗）：initConsent(activity, onComplete)、showSplashConsent(activity, onComplete)
```

```kotlin
val flow = Core.beginConsentFlow("startup")
var finished = false
fun finish(cancelled: Boolean) {
    if (finished) return
    finished = true
    Core.endConsentFlow(flow, cancelled)
}

val cached = Core.initConsent(activity, flow) { ok ->
    if (ok) Core.showSplashConsent(activity, flow) { finish(false) } else finish(true)
}
if (cached) finish(false)
```

要点：表单必须在 Activity **RESUMED 且有焦点**时展示；`initConsent` 返回 `true` 时**不会**再回调；`canRequestAds()` 是查询，不会自动阻塞广告。

---

## 6. 通知

### 6.1 权限与常驻服务

```kotlin
// com.mar2sdk.core.notify.NotificationUtil
fun reqNotiAccess(activity: Activity, launcher: ActivityResultLauncher<String>, onResult: (Boolean) -> Unit)
fun getAccessState(context: Context, channelIds: Collection<String> = emptyList()): NotificationAccessState
fun hasNotiAccess(): Boolean
fun reportNotificationPermission()
fun startFGS()
fun trackAppOpen(intent: Intent?)
```

```kotlin
// com.mar2sdk.core.notify.NotificationAccessState
data class NotificationAccessState(
    val runtimePermissionGranted: Boolean,
    val appNotificationsEnabled: Boolean,
    val blockedChannelIds: List<String> = emptyList(),
    val missingChannelIds: List<String> = emptyList(),
) {
    val hasPermission: Boolean     // runtime && app 总开关
    val needsSettings: Boolean     // !hasPermission || blockedChannelIds 非空
}
```

```kotlin
private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
    if (granted) Core.startFGS()
}
NotificationUtil.reqNotiAccess(this, launcher) { granted -> if (granted) Core.startFGS() }

// 页面里判断“要不要提示去设置”
val state = NotificationUtil.getAccessState(context, AppNotificationUtil.getConfiguredChannelIds())
if (state.needsSettings) showGuide()
```

### 6.2 点击契约（Intent extras）

```kotlin
// com.mar2sdk.core.notify.NotificationRouteIntent
object NotificationRouteIntent {
    const val EXTRA_ACTION_ID = "Mar2SDK.NotificationActionId"
    const val EXTRA_NOTIFICATION_ID = "Mar2SDK.NotificationId"
    const val EXTRA_PUBLICATION_ID = "Mar2SDK.NotificationPublicationId"
    fun ensureActionId(intent: Intent?): String?
}
```

| extra | 普通通知 | 常驻通知 |
|---|---|---|
| `AppOpenFrom` | `app_push` | `persistent` |
| `Route` | 内容里的 Route | `Action1..Action4` / `persistent` |
| `Scene` | 实际发送场景 | `persistent` |
| `Mar2SDK.NotificationActionId` | 发布时写入 | 交付后补 UUID |

宿主入口固定三步：

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    NotificationRouteIntent.ensureActionId(intent)   // 要在 super.onCreate 之前
    super.onCreate(savedInstanceState)
    handleIntent(intent)
}

override fun onNewIntent(intent: Intent) {
    NotificationRouteIntent.ensureActionId(intent)
    super.onNewIntent(intent)
    setIntent(intent)
    handleIntent(intent)
}

private fun handleIntent(intent: Intent) {
    NotificationUtil.trackAppOpen(intent)            // notification_clicked + traffic_source
    val route = intent.getStringExtra("Route") ?: return
    val source = intent.getStringExtra("AppOpenFrom").orEmpty()
    val actionId = intent.getStringExtra(NotificationRouteIntent.EXTRA_ACTION_ID) ?: return
    // 自己按 whitelist 映射 route → 页面，并按 actionId 去重
}
```

### 6.3 发通知

```kotlin
// com.mar2sdk.core.notify.app.AppNotificationUtil
fun getContent(scene: String): NotificationContent?
fun sendNotificationContent(scene: String)          // 用 notification_content.json 里的内容发一条
fun sendNotification(context, id, channelId, notificationGroupKey, scene, icon, title, message, button, route)
fun getConfiguredChannelIds(): List<String>
fun getAppPendingIntent(route: String = "", scene: String = ""): PendingIntent
```

- `sendNotificationContent("home_recent")` 是最省事的“按配置发一条”；
- `getContent(scene)` 会按 `Scenes` 过滤（支持 `"*"`）+ 随机 + 语言回退，可能返回 null（没有匹配项）；
- 自建通知时 `getAppPendingIntent(route, scene)` 能拿到带正确 extras 的 PendingIntent。

### 6.4 可选：SDK 自带的权限控制器

```kotlin
// com.mar2sdk.impl.notify.NotificationPermissionController
class NotificationPermissionController(context: Context) {
    fun getNotificationAccessState(): NotificationAccessState
    fun checkNotificationPermission(): Boolean
    fun requestNotificationPermission(launcher: ActivityResultLauncher<Array<String>>, activity: Activity): Boolean
    fun onNotificationPermissionResult(permissions: Map<String, Boolean>): Boolean
    fun openNotificationSettings(activity: Activity): Boolean
}
```

适合“每台设备只申请一次”的简单场景；**它和参考项目宿主的“每轮 Splash/Home 各一次”策略不同**，要后者的行为得自己写（`impl.notify.NotificationRouteCoordinator` 也只覆盖路由，不覆盖申请轮次）。

### 6.5 样式覆盖（只需资源）

常驻：`common_notification_action_1..4` + `notification_photos/files/videos/recovered` 图标。
普通通知：`notification_1..4(_mini).xml`，必须保留 `root/iv_push/tv_detail|tv_title|tv_message/button`。
内容图标：`mipmap/ic_push_files`、`ic_push_photos`、`ic_push_videos`、`ic_push_recoverd`（**注意 SDK 拼写**）。

---

## 7. 用户、风控、日志

```kotlin
// com.mar2sdk.core.Core
fun log(eventName: String, params: Map<String, Any>)
fun setUserAttr(key: String, value: Any)
fun setUserAttr(attributes: Map<String, Any>)
fun setUserOnceAttr(key: String, value: String)
fun setEventAttr(key: String, value: Any)
```

```kotlin
// com.mar2sdk.core.common.RiskUtil
fun getPolicySourceSnapshot(): PolicySourceSnapshot

// com.mar2sdk.core.common.PolicySourceSnapshot
data class PolicySourceSnapshot(
    val localUserType: UserType,
    val serverUserType: UserType,
    val localAdPolicyUserType: UserType,
    val serverAdPolicyUserType: UserType,
    val localNotificationPolicyUserType: UserType,
    val serverNotificationPolicyUserType: UserType,
)

// com.mar2sdk.core.common.status.UserType
enum class UserType { RISK, COMMON, NATURE, HIGH_VALUE, UNKNOW }
```

版本号显示用 `Core.SDK_VERSION` + 上面的快照，**不要手写 "2.0.15"**。

事件名常量可复用：`LogAdEvent.ad_impression`、`LogNotifyEvent.notification_clicked`、`LogUmpEvent.*`。自定义事件必须进 `log_config` 对应渠道白名单（默认 `["*"]`，网络只有 4 项）。

---

## 8. 配置 API

**能改**（public）：

```kotlin
// com.mar2sdk.core.ad.AdConfig
var defaultPlatform: AdPlatform
var activePlatforms: MutableSet<AdPlatform>
var isOpen: Boolean
var isRisk: Boolean
var showMod: ShowMod          // MODE_555 / 666 / 777 / 888
var showMaxTime: Long
var showMinTime: Long
var max1H: Int
var max24H: Int
var interval: Int
var autoRate: AutoRateConfig
var adUnits: Map<String, AdUnitConfig>
fun saveAdConfig()            // 落盘到 Preference，下次启动生效

// com.mar2sdk.core.ad.impl.admob.AdmobConfig
var openConfig / interConfig / videoConfig: AdUnitConfig
var bannerConfig: BannerConfig
var nativeConfig: NativeConfig
val openID / interID / videoID / bannerID: String   // 测试模式下是官方测试 ID
fun saveAdmobConfig()
```

**不能改**（internal）：`AdConfig.applyConfig(JSONObject)`、`AdmobConfig.applyConfig(...)`、`RiskUtil.applyJson(...)`——这些是 RemoteConfig/服务端下发的入口。

**推荐做法**：默认值放 `res/raw`（同名覆盖 SDK 的 raw），线上用 RemoteConfig/中台，**不要用代码硬改再 `saveAdConfig`**（会和远程配置打架，且难排查）。

配置优先级：`服务端 > RemoteConfig > Preference(上次保存) > raw`。

---

## 9. Compose 页面助手（impl）

```kotlin
// 页面级开屏：点位必须是 OPEN / OPEN_INTER
@Composable
fun SplashScreen(areaKey: String = "splash_open", consentReady: Boolean = false, onFinished: () -> Unit = {})

// 页面级插屏 + 带广告跳转
fun NavGraphBuilder.contentComposable(route: String, content: @Composable () -> Unit)
@Composable fun BaseScreen(content: @Composable () -> Unit)
@Composable fun rememberNavigateWithAd(): (toRoute: String, navigation: () -> Unit) -> Unit
@Composable fun rememberNavigateWithAd(navController: NavController): (toRoute: String, NavOptionsBuilder.() -> Unit) -> Unit
@Composable fun ObserveScreenAdRoutes(navController: NavController)
```

⚠️ 这套助手用**路由名派生点位**：`contentComposable("home")` 会请求 `home_start`、`home_back`、`home_leave`。你的 `ad_config` 里没有这些键，就会被 `placement_config` 拒。想用历史点位（`openPageAdv`、`enterHomeFromAppIntroAdv`）就**别套 `BaseScreen`**，自己调 `AdActivity.showAd`。

---

## 10. 前台广告管理（impl，可选）

```kotlin
// com.mar2sdk.impl.AppOpenAdManager
fun init(application: Application)
fun updateHostState(activity: Activity, isResumed: Boolean, currentRoute: String?, pageAllowsAd: Boolean, operationPending: Boolean)
fun suppressNextForegroundAd()
fun completeOriginalOperation()
fun beginExternalOperation(activity: Activity): ExternalOperation?

class ExternalOperation {
    fun launchFailed()
    fun completeWithoutLeaving()
}
```

- 它的点位**固定**是 `app_foreground_open`；`ad_config` 里没有这个键就不会出广告（当前生成的配置就是这样）；
- 如果不用它，**不要调 `init`**，否则只是注册了一堆生命周期回调；
- 用了它就必须每次状态变化都 `updateHostState`，否则 `pageAllowsAd` 一直是 false。

---

## 11. 一个最小可用宿主

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Core.init(this, if (BuildConfig.DEBUG) AppMod.DEBUG else AppMod.RELEASE)
    }
}

class MainActivity : ComponentActivity() {
    private var adShowing = false
    private var paused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        NotificationRouteIntent.ensureActionId(intent)
        super.onCreate(savedInstanceState)
        NotificationUtil.trackAppOpen(intent)
        setContent { App() }
        playOpen()
    }

    override fun onNewIntent(intent: Intent) {
        NotificationRouteIntent.ensureActionId(intent)
        super.onNewIntent(intent)
        setIntent(intent)
        NotificationUtil.trackAppOpen(intent)
    }

    private fun playOpen() {
        val ctx = ScreenAdContext(
            areaKey = "openPageAdv",
            adFormat = AdFormat.OPEN,
            adPlatform = AdConfig.defaultPlatform,
            trigger = ScreenAdTrigger.ENTER,
        )
        adShowing = AdActivity.showAd(this, ctx)
        if (!adShowing) finishStartup()
    }

    override fun onPause() { paused = true; super.onPause() }

    override fun onResume() {
        super.onResume()
        if (adShowing && paused) { adShowing = false; finishStartup() }
        else if (!adShowing) Core.startFGS()
    }

    private fun finishStartup() { /* 决定去引导还是首页 */ }
}
```

---

## 12. 报错对照表

| 现象 | 原因 | 处理 |
|---|---|---|
| 编译报 `Unresolved reference: Core` | 只依赖了 `impl` | 显式加 `core` |
| 编译报 `Cannot access 'applyConfig'` | 调了 internal API | 改用 raw/远程配置 |
| 运行崩溃 `No launcher Activity` | 用了 `getAppPendingIntent` 但没 Launcher | 检查 Manifest |
| 启动即崩、日志 `package` 相关 | `common_config.package` ≠ applicationId | 改成完全一致 |
| `showAd` 返回 false | 策略拒 / Activity finishing / 已有广告页 | 看 `ad_policy` 与 `ad_trigger` |
| 一直没有广告，有 `ad_trigger` 无 `ad_occur` | 被 `AdPolicy` 拒 | 看 `ad_policy` 的 `policyName` |
| 有 `ad_occur` 无 `ad_impression` | 加载/展示失败 | 看 `ad_show_fail.show_fail_reason` |
| 之后所有全屏都 `OTHER_AD_IS_SHOWING` | 上次展示没收到 dismiss 回调 | 杀进程重启自愈 |
| AdMob 广告总输给别家 | 价格未知（`REFLECT` 版本不符/混淆变化） | 看 `ad_load_success` 有无 `ad_price_micros` |
| 通知点不开 | `launchMode`/PendingIntent 被覆盖 | 用 `getAppPendingIntent` + `singleTop/singleTask` |
