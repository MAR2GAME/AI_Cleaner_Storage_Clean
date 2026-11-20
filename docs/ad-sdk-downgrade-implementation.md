# 广告 SDK 降级实施记录

状态：源码迁移已落地，尚未完成 APK 构建及真机验收。未创建 Git 提交。

## 实际目录与边界

```text
advertise/
├── build.gradle.kts
├── consumer-rules.pro
├── proguard-rules.pro
└── src/
    ├── main/
    │   ├── AndroidManifest.xml
    │   ├── java/com/pdffox/adv/use/
    │   │   ├── Ads.kt / Config.kt / AdvApplicaiton.kt
    │   │   ├── adv/                 DataRestore 播放、广告池、频控及 policy
    │   │   ├── notification/        通知引擎、常驻服务、广播接收器
    │   │   ├── push/                FCM、WorkManager、JobService、宿主 Intent 契约
    │   │   ├── remoteconfig/        旧配置逻辑及明文/加密配置读取兼容
    │   │   ├── log/
    │   │   └── util/、update/ 等旧模块支持代码
    │   └── res/                    通知布局、图标及旧 SDK 资源
    └── test/java/com/pdffox/adv/
        ├── AdPolicyCompatibilityTest.kt
        ├── notification/NotificationConfigTest.kt
        └── remoteconfig/           配置路由及加密兼容测试

app/src/main/
├── AndroidManifest.xml
├── java/com/aicleanerstorage/spaceclean/app/
│   ├── MyApp.kt / MainActivity.kt
│   ├── commercial/AdvertiseAdDriver.kt
│   └── navigation/register/SplashRoutes.kt
└── kotlin/com/aicleanerstorage/spaceclean/app/platform/
    ├── ads/AdvertiseSdkAdapter.kt
    ├── ads/LegacyNotificationConfig.kt
    ├── ads/AdHosts.kt
    └── analytics/SdkAnalyticsSink.kt
```

- SDK 主体来自 `D:/pros/my/DataRestoreFileRecovery/advertise`；常驻服务和 FCM 等通知载体参考 `D:/pros/andriod/StorageCleaner/advertise`。
- 广告播放、重试、频控及广告池保留旧实现。开屏新增请求有效性判断，用于阻止超时/取消后迟到的广告展示；前后台开屏增加现有 `AdDriver` 所需的开关及一次性抑制能力。
- app 依赖改为 `project(":advertise")`，移除新版 aar。Pangle/TikTok 依赖收至 SDK 模块，保留替代 Pangle 包的排除规则；旧模块动态依赖改成固定版本，保留 Bigo、Google Ad Manager 等聚合适配器。
- 删除 `AdvertiseConfigFactory.kt`、`OpenAdHostActivity.kt`（包含原 Contract）、`OpenAdHostWatchdog.kt`。业务未使用的 Native/Rewarded app 包装入口一并移除。
- `SdkInitializationCoordinator`、`NotificationDefaultsInitializer`、`NotificationIntentRouteResolver`、`AdvertiseSdkPrivacyGateway` 的既有接口可复用，不需要改动。
- `core/**` 和 `feature/**` 未改动；既有导航行为及 `AppDestination.notificationAliases()` 保留。其他工作区改动不属于本次迁移。

## 核心接入

`MyApp : AdvApplicaiton`、`MainActivity : AdvActivity` 满足旧 SDK 宿主要求。SDK 初始化仍由既有协调器调度；通知图标/路由配置与缓存恢复在 Application 中同步准备，广告与远程配置异步初始化。

`Ads.init(application, isTest, R.raw.ad_policy)` 的资源参数必传，读取 app 生成的 policy；删除旧模块自己的 DataRestore policy。保留包名校验与 `AdChecker`，移除 `:safe` 及未启用的 `SafeChecker`。旧模型为 `platform`、`ad_network`、`ad_format` 提供默认值，并忽略当前产品 policy 的扩展字段。

调用链：

```text
feature → AdDriver → AdvertiseAdDriver → AdvertiseSdkAdapter → :advertise
:advertise notification → 宿主启动 Intent → MainActivity
                       → NotificationIntentRouteResolver → AppDestination
```

Splash 在 MainActivity 上直接执行冷启动广告流程。缺少宿主、初始化失败、UMP 信息请求失败/超时、广告加载超时均可结束；取消时撤销广告请求有效性，避免迟到回调再次推进导航。UMP 表单加载最长等待 6 秒，已经展示的同意表单等待用户操作；广告展示成功后等待真实关闭回调。

## 与骨架方案的必要差异

1. 通知包名保留 `com.pdffox.adv.use.notification` / `use.push`，与旧源码一致。
2. `PushIntegration` 通过 PackageManager 获取宿主启动 Intent，不导入 app 的 MainActivity，避免模块循环依赖。action、FileProvider authority 根据宿主包名生成。
3. `LegacyNotificationConfig` 仅保存当前产品的通知图标、动作和路由映射，不再构建新版 SDK 配置对象。直接 SDK 依赖限于平台适配层、Driver，以及必须继承旧基类的两个宿主类。
4. 广告 ID 以当前 `config/product.json` 为准。骨架中的 `/3639017161` 实际是 **Open ID**；Banner 保留产品配置中的 `/6300978111`。当前 Banner/Native 值本身是测试 ID。没有当前产品的 MAX/TopOn 账号参数，故跳过其初始化，平台选择回退至 AdMob；未沿用 DataRestore 的账号。
5. 当前产品已有 `remote_config_json` 加密配置。新增读取兼容，支持旧 Firebase 独立键、明文 JSON 和 AES-256-GCM JSON；密钥与 key ID 使用当前产品值。密文无效时保留本地配置，旧路由与频控逻辑仍负责应用有效配置。
6. 通知服务先进入前台，再注册媒体观察器；权限不足时跳过对应观察器。前台启动失败时停止服务，避免继续返回 START_STICKY。开机广播直接请求启动服务，避免延时回调随进程回收丢失。
7. `check-brand-residue.ps1` 增加 `-WhitePackage`，只有显式白包检查才要求不存在 Firebase/policy 文件；普通产品检查包含新增 SDK 源码。依赖检查新增 SDK 不得反向导入 app/core/feature 的约束。

## 验证结果与限制

- 通过权限/广告边界、依赖/MVI 边界、产品配置、品牌及旧包名残留静态检查。
- 独立 Kotlin 检查通过旧 SDK Kotlin 源码，以及 app Adapter、Driver、隐私适配、通知映射、AnalyticsSink。使用本机缓存依赖和临时 R/ViewBinding 类型桩；此检查不替代 Android 构建、资源合并、Java 编译或完整依赖解析。
- 12 个定向 JVM 测试通过：当前实际产品 policy 解析、通知默认字段、明文/加密配置、错误 key ID/密文拒绝、配置路由，以及插屏重复关闭/取消/异常。policy 测试使用真实 Kotlin serialization 编译插件生成的序列化器。
- `:app:assembleDebug` 在 Gradle 启动阶段失败：`java.io.IOException: Unable to establish loopback connection`。禁用 daemon / IPv4 重试仍失败，尚未进入项目编译。未反复重试完整构建。
- 未做真机测试；待环境恢复后确认最终依赖解析和 Manifest 合并，并验收冷启动无填充、UMP 慢加载、广告关闭、后台返回、通知冷/热启动路由、媒体权限拒绝及 FCM 图文消息。

业务验收完成前，不应把定向 JVM 检查视为可发布 APK 的证明。产品配置以后变更时，需要同步 `Config`、`AdvIDs` 中的单产品默认值；app 的 AdMob application ID 和 policy 仍由既有生成流程提供。
