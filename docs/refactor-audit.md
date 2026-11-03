# Storage Clean 架构技术债排查报告

> **2026-11-03 复核与执行说明**：下文保留 `da1d7eb` 原始审计基线，不代表当前分支的全部事实。已开始在 `codex/refactor-v3` 分阶段修复，进度见 `refactor-plan.md` 顶部 v3 台账。本批已移除两套重复文件算法的名称／大小降级，并加入删除前复核；其他问题不得据此视为已解决。

## 复核更正（优先于原始结论）

| 原始结论 | 校正 |
|---|---|
| 无 Crashlytics 运行时、线上零上报 | release 合并报告和 mapping 含 Crashlytics Java／NDK 20.0.1；实际上传状态仍需后台验证 |
| 9 页取消删除无法关闭 | 未找到 `blockedPhase = ConfirmDelete` 的可达路径，不能标为已确认缺陷 |
| 只跑过一个测试类 | 本地一个结果 XML 不能证明历史执行情况；本批另有 19 条定向测试通过 |
| WakeLock 到期导致 AppLock 10 分钟后必然失效 | 到期不等于服务停止，需要休眠和后台场景验证 |
| R8／资源收缩全应用失效 | keep 的直接证据限于 junkcc 等被保留的类和资源，不能推广到整个应用 |
| check-dependencies 依赖未安装的 rg | 该脚本实际使用 Select-String，当前环境也有 rg |
| check-brand-residue 不传参数完全不检查 | 品牌词循环为空，但仍检查 google-services.json 和广告配置 |
| 结果页移除威胁会跳回扫描页 | 引用代码导航目标是结果页／无威胁页，原触发链未被证明 |
| 缺 domain／MVI 基类、SharedFlow 等均可列 P0 | 结构问题与行为缺陷分别分级；不以目录、类型或关键词计数替代行为证据 |

---

- **排查对象**：`D:\pros\my\AI_Cleaner_Storage_Clean`（git `da1d7eb`，tracked 文件 4318 个）
- **基准项目**：`D:\pros\my\ai-video-generator`（20 模块 + `core:presentation.MviViewModel` + `core:logging.EventLogger` + 类型安全路由 + CI 门禁）
- **性质**：**只读排查**，未修改任何源码
- **组织方式**：按团队确认的 6 条技术债线编排；每条线给出「现状证据 → 缺陷 → 与基准差距 → 目标形态 → 验收断言」
- **证据约定**：`文件:行号` 均可直接复核；标 **[确认缺陷]** 的条目有代码或构建产物硬证据，标 **[改进建议]** 的为结构性问题

> 后续「逐点对齐」直接在本文件对应章节内迭代，不另开文档。

---

## 0. 量化基线（实测，非估算）

| 指标 | 实测值 |
|---|---|
| Kotlin 文件 / 行数 | **406 / 42,532**（`app` 3,980 · `common` 6,608 · `feature` 31,944） |
| Gradle 模块 | **2**（`:app` + `:junkcc`）；基准项目 23 个 |
| `feature` 下文件数 | 275，**`domain/` 目录数 = 0**，`presentation/` 目录数 = 0 |
| ViewModel | **29**，继承统一基类 **0**，有纯 Reducer **3**，有测试 **6** |
| Screen / View 文件 | 68（其中 26 个 import `AppDestination`） |
| 顶层 `object` 单例 | **43**（其中约 15 个有状态或绑三方 SDK） |
| `runCatching` / `catch` | **177 / 56**；`catch (_:` 29 处 |
| 统一错误类型 | **0**（无 `AppError` / `Result<T,E>` / `BusinessOutcome`） |
| SharedPreferences 键空间 | **3**（`quick_clean_settings` / `phone_cleaner_sp` / `notification_blocker`）；DataStore **0**、Room **0** |
| `.commit()` 同步落盘 | **13** 处 |
| 日志抽象 | **0**；裸 `android.util.Log` **18** 处 / 10 个文件 |
| Crashlytics | 仅 Gradle 插件，**无运行时依赖** → 线上崩溃零上报 |
| `Dispatchers.*` 硬编码 | **80** 处；`AppDispatchers` 只有 **1** 个消费文件 |
| 设计 token | 15 个 token/theme 文件、**156** 个 Color 常量、**167** 个不同色值、**1,710** 处裸 `dp`/`sp` |
| 单元测试 / androidTest | **33 / 1**（`test-results` 里只有 1 个 XML → 只跑过 1 个测试类） |
| 跨 feature 直接 import | **20** 处 |
| `feature`/`common` → `app.*` 反向依赖 | **94** 处（符号级：`AppDestination` 73、`AppNavigator` 38） |
| `:junkcc` | 1,800 个 `.java` / **25.2 MB** / 1,802 个资源 / 自身 manifest **900** 个 `<activity>` / 占仓库 tracked 文件 **84%** |
| release 合并 manifest | **950** 个 `<activity>` + `usesCleartextTraffic="true"` + `QUERY_ALL_PACKAGES` |
| release AAB | `app/release/…_1.0.7.aab` = **51.3 MB** |
| CI | **无**（`.github` 不存在） |
| 架构门禁 | 5 个 `scripts/check-*.ps1` **全部失效**；`MotherTemplateBoundaryTest` **4/6 断言空转** |

---

## 1. P0 台账（11 类，必须先处理）

| # | P0 | 类型 | 主要证据 | 详见 |
|---|---|---|---|---|
| P0-1 | 插屏无超时 + `requestId` 去重静默吞 `onContinue` → 入口/返回/结果页**全站卡死** | 广告 | `common/ads/AdRuntime.kt:156-208` | §5.2 |
| P0-2 | 权限会话可永久卡死 → `permissionFlowActive` 恒 true → **广告全停 + 权限功能全停**，只能杀进程 | 权限 | `app/permission/PermissionCoordinator.kt:97-104,143-153` | §5.3 |
| P0-3 | release manifest 被本地 AAR 注入 `usesCleartextTraffic="true"` + `QUERY_ALL_PACKAGES` | 合规 | `app/libs/cloudscan_sdk_*.aar` 合并结果 | §6.1 |
| P0-4 | AppLock 4 位 PIN **明文**存储 + `allowBackup=true`；WakeLock 10 分钟后不续期；浮层非前台服务 | 安全/功能 | `AppLockRepositoryImpl.kt:31-36` | §6.2 |
| P0-5 | `:junkcc` 950 Activity + 25 MB 源码 + proguard keep → R8/资源收缩全部失效 | 体积/审核 | merged manifest 950 activities | §6.3 |
| P0-6 | 33/34 广告位 `interval_seconds: 0`；「拒绝权限」后立刻弹插屏 | 政策 | `config/ad_policy.json`、`FeatureEventAdPolicy.kt:21-24` | §5.4 |
| P0-7 | 生产 banner/native/rewarded 用 **Google 测试广告位**，且 banner/native **零调用**（无降级变现） | 收入 | `config/product.json`、`common/ads/AdHosts.kt:21,43` | §5.5 |
| P0-8 | 177 处 `runCatching` 把「权限拒绝/未挂载/IO 错误/目录为空」全退化成 `emptyList()`；哈希失败退化为 `name+size` 匹配 → **可能误删文件** | 数据正确性 | `MediaStoreFileQuery.kt:31-68`、`BaseFileScanner.kt:120-122`、`FileContentHasher.kt:69` | §4.3 |
| P0-9 | 错误文案 i18n 失效（`appString` 返回硬编码英文 / `else -> ""`）；9 页「取消删除」关不掉 | 体验 | `FileManagerMessages.kt:11-27`、`photos/PhotosManagerViewModel.kt:113` | §3.5 §4.4 |
| P0-10 | `feature`/`common` → `app.*` 反向依赖 **94** 处 → `app` 既是最顶层又是最底层（**环形依赖**） | 结构 | `app/navigation/AppDestination.kt` 被 111 文件引用 | §3.1 |
| P0-11 | 门禁全线失效 + 无 CI + 零崩溃上报 | 工程 | §7 | §7 |

---

## 2. 章节索引（对应 6 条技术债线）

| 章节 | 技术债线 | 一句话病灶 |
|---|---|---|
| §3 | ① 路由框架 | `AppDestination` 是一个 **447 行的全局字符串注册表**，同时充当导航源、通知别名源、广告白名单源、埋点来源；111 个文件依赖它 |
| §4 | ② 架构分层 | **没有 domain 层**；20 个 `*RepositoryImpl` 全是 1 行转发到 `object *DataSource`；没有统一错误类型 |
| §5 | ③ MVI 模式 | 0 个基类；「精髓」缺三样：单一 State/Intent/Effect、纯 Reducer、Effect 走 Channel 不走 State |
| §5.2–5.5 | ④ 权限 / 广告 / 打点 | 三件事用同一种错法实现：**回调 / 装饰器 / 全局单例寄生在业务流程里**，应统一收进 `IntentInterceptor` |
| §6 | ⑤ 日志 + ⑥ app 层 + DI | 18 处裸 `Log`、确实泄露 key；`AppRoot` 手工 `remember { new }` 组 5 层对象图，DI 只有 2 个文件、25 处位置参数 `get()` |
| §7 | 门禁与工程化 | 5 个 check 脚本全部空转或必然失败，所以上述缺陷长期没人发现 |

---

# 3. ① 路由框架

## 3.1 现状与证据

| 项 | 证据 |
|---|---|
| 字符串路由 + 手工拼参（两套并存） | `app/navigation/AppDestination.kt:21-27` `withArgs` 拼 query（`URLEncoder`，空格→`+`）；`:29-31` `withDetailInitialIndex` 拼 path 段；全仓 **0 处 `navArgument`** |
| 参数读取靠手工解析 | `app/navigation/register/FileManagerRoutes.kt:124-125` `arguments?.getString(DETAIL_INITIAL_INDEX_ARG)?.toIntOrNull()?.coerceAtLeast(0) ?: 0` |
| 同一目的地四重登记 | `AppDestination.kt:14-20` 构造器同时承载 `route` / `featureKey` / `featureEntry` / `notificationAliases`+`notificationTarget` |
| 手工 `all` 列表 + 6 个派生集合 | `:322-364` 手写 39 项；`:366-393` 派生 `featureEntries`/`byRoute`/`byFeature`/`rootRoutes`/`notificationTargetRoutes`/`notificationAliasToRoute` |
| 外部还有 3 份视图 | `common/ads/AdNavigationPolicy.kt:44-56`（黑名单又写一遍字符串）、`common/ads/AdvertiseConfigFactory.kt:175-197`（route→图标）、`app/notification/NotificationIntentRouteResolver.kt:60`（合法路由集合） |
| 全量 `runCatching` 吞导航异常 | `app/navigation/AppNavigator.kt:43-47,49-57,59-66,68-89,91-93`；`back()` 用 `getOrDefault(false)` |
| 广告塞进导航 | `app/navigation/FeatureEntryNavigator.kt:12-49`：`open/openRoute/resetTo/resetToRoute/openNotificationTarget` 插广告，`replace/home/back` 不插 |
| 语义不自洽 | `resetTo` 对 home 家族**空操作**（`:59-66`）；`resetToRoute` 清空全栈且不垫根（`:68-89`）；`back()` 靠 route 集合判 root（`:97`） |
| 无回执机制 | `SavedStateHandle` / `previousBackStackEntry` **0 处**；跨页结果靠"父 VM 恰好还活着"+探测（`FileManagerRoutes.kt:127-131`） |
| 通知入口四方分裂 | `MainActivity` → `AppLaunchCoordinator` → `NotificationLaunchEffect`（`AppRuntimeEffects.kt:56-57` **裸 `navController.navigate`**，绕过 `AppNavigator`）→ Splash 消费 |

## 3.2 缺陷清单

### D-①-1 `resetTo` 对 home 家族目的地是空操作【中 · 潜伏】
`AppNavigator.kt:59-66`：先 `navigateHomeClearingStack()`（当前已在 home 家族时直接 return），再 `if (destination.route !in homeRoutes) navigate(...)` → 传 `HomeFileManager` / `HomeToolbox` 时**什么都不跳**，停在 `${Home}`。

### D-①-2 通知进工具箱 → 按返回直接退出 App【高 · 确认】
通知别名表把 `toolbox` / `home_toolbox` 列为合法目标（`AppDestination.kt:51-56`）→ `resetToRoute("home_toolbox")` 清栈后**不垫 `Home`** → `back()` 判定 `currentRoute in rootRoutes`（`:97` + `:373-375`）→ 返回 `false` → Activity `finish()`。用户从通知进来，一按返回 App 就退了。

### D-①-3 导航失败完全不可观测【高 · 确认】
5 个方法全包 `runCatching`，无 `onFailure`、无日志、无埋点。未知路由、转场期调用、`popUpTo` 目标不存在全部被吞；`back()` 把异常翻译成"不能返回"，进而 `finish()`。

### D-①-4 `all` 漏登记 = 静默无导航【中 · 确认】
`forRoute` 返回 null → `resetToRoute` 退化为 `openRoute` → `navigate(未知route)` 抛异常 → 被 D-①-3 吞掉 → **点击无反应、无日志**。现有测试 `AppDestinationTest.kt:11-19` 只校验 route 唯一性，**没有任何测试校验 `all` 每一项都在 NavGraph 注册过**。

### D-①-5 父 entry 探测失败会造出第二个 ViewModel【高 · 确认】
`FileManagerRoutes.kt:127-131` `fileManagerViewModelOwnerOr` 探测失败时 **fallback 到详情页自己的 entry** → `koinViewModel(viewModelStoreOwner = backStackEntry)` 新建一个 manager VM（列表为空、删除不同步、状态丢失），**零日志**。触发路径真实：`forRoute` 用前缀匹配（`AppDestination.kt:397`），任何 `photos_detail/<任意>` 都能进详情页。

### D-①-6 详情页双源状态【中 · 确认】
`PhotosManagerRoute.kt:111-114` 同时写 `viewModel.openDetail(index)` 与 `router.openRoute(...)`；`PhotosManagerViewModel.kt:100` 的 `closeDetail()` 只改 VM 不改路由 → 返回分发顺序变化时出现"详情路由还在但 flag 已关"或反之。

### D-①-7 三个 Home 目的地 = 三份 `HomeViewModel`【高 · 确认】
`register/HomeRoutes.kt:18/27/37` 三个 `composable` 各自 `koinViewModel()` → **3 个独立 VM 实例**，各自 `init { refreshSummary(); scheduleAutoRatePrompt() }`（`HomeViewModel.kt:68-70`）→ 3 个 15s 自动评分计时器、3 次 IO/埋点；tab 状态有 3 份真相（路由 / `initialTabIndex` / `pagerState`）。

### D-①-8 transition 按 route 字符串分支【中】
`AppRouteTransitions.kt:21-82` 全部按 `Splash` 字符串分支，其余一律 slide+fade；`home ↔ home_toolbox` 兄弟切换也走 push 动画。无"目的地类别"概念。

### D-①-9 导航决策散落 12+ 文件【中 · 确认】
- VM 持路由字符串：`HomeViewModel.kt:36`（`exitPromptRoute: String?`）、`:159-173`（`nextExitPromptRoute()` 里 `suggestions.randomOrNull()`）
- Screen 反查路由：`HomeScreen.kt:73,164-169`（`AppDestination::forRoute`）
- Screen 直接改栈：`VirusScanScreens.kt:61-69`（`router.replace(...)`）、`FileDetailScreen.kt:37-39`
- **共享 UI 组件**接收 `AppDestination` 并把埋点与导航绑死：`common/ui/components/ToolFeatureBanners.kt:136,166,180-187`、`CleanXCompat.kt:321,341`
- 8 个 manager 把 `router::resetTo` 透传进 Screen

反面事实（做对了，重构时别改坏）：**没有任何 Screen 直接持有 `NavController`**；`AppNavigator` 确实是唯一改栈入口（`NavController` 仅出现在 `AppRoot.kt:36`、`AppNavigator.kt:34`、`AppRuntimeEffects.kt:53`）。

### D-①-10 返回键无统一策略【中 · 确认】
`Settings` / `ManagePermissions` / `VirusQuickScan` / `VirusDeepScan` / `NetworkScanDevices` **完全没有 `BackHandler`** → 系统返回走 `rememberNavController()` 自注册的 `popBackStack()`，**跳过 `AppNavigator.back()`** 的 rootRoutes 守卫与退出广告策略。

## 3.3 与基准项目差距

| 维度 | 本项目 | 基准 |
|---|---|---|
| 路由类型 | `sealed class` + 字符串 route | `@Serializable sealed interface AppDestination`（参数即字段） |
| 参数 | query / path 手工拼接 | `toRoute<T>()` |
| 浮层 | 与页面混在 NavHost | `AppOverlayDestination` 独立于 NavHost |
| 导航接口 | 7 个方法（含 `openRoute`/`resetToRoute`/`openNotificationTarget`） | 6 个方法（`navigate`/`replaceCurrent`/`navigateTopLevel`/`resetTo`/`show`/`back`） |
| 回执 | 无 | `NavigationTicket + Deferred<NavigationReceipt>` |
| 鉴权 | 无策略概念 | `DestinationAuthPolicy` 纯函数 |
| 外部入口 | Activity 层 + 3 处分散 | `ExternalNavigationCoordinator`（FIFO + 跨重建 + ack） |
| 广告 | 装饰器插在导航里 | 导航层完全不认识广告 |

## 3.4 目标形态

见 §8.1。

## 3.5 验收断言

```text
grep -c "runCatching" app/navigation/                       == 0
grep -rc "navigate(" 排除 AppNavigator.kt                    == 0
grep -c "withArgs\|withDetailInitialIndex\|detailPattern"    == 0
grep -c "navArgument"                                        == 0（改用类型安全后不需要）
composable<AppDestination.X> 覆盖率                          == 100%
AppDestination.all × navController.graph 完整性测试          == 1 个且通过
Screen 文件内 "ViewModel|AppNavigator|AppDestination"        == 0
```

> **迁移成本提示**：`:app` 已声明 `kotlin-serialization` 插件（root `build.gradle.kts:6` `apply false`），`libs.versions.toml:64` 已声明 `kotlinx-serialization-json`（当前未使用）。
> **不需要引入任何新依赖**，加两行即可启用 `composable<T>` / `toRoute<T>()`。这是本项目**唯一一类"删代码就消除"的缺陷**。

---

# 4. ② 架构分层

## 4.1 现状与证据

| 项 | 证据 |
|---|---|
| 无 domain 层 | `feature/` 下 275 文件，`domain/` 与 `presentation/` 目录数 **0**；实际只有 `ui/`、`data/`、`model/`、`scanner/`、`service/`、`shared/` |
| Repository 接口与实现同包 | `CleanRepository`/`Impl` 同在 `feature.junkclean`；`FileRepository`/`Impl` 同在 `feature.files.shared.data`；共 23 接口 / 20 Impl |
| Impl 是 1 行转发 | `feature/files/shared/data/FileRepositoryImpl.kt:8-37` 全文 12 行，每行 `= FileManagerDataSource.xxx(appContext)` |
| `DataSource` 全是 `object` | `FileManagerDataSource.kt:20`、`NotificationDataSource.kt:15,237`、`AppUsageDataSource.kt:15`、`DeviceInfoDataSource.kt:13`、`StorageDataSource.kt:10`、`NetworkToolboxDataSource.kt:70`、`MediaStoreFileQuery.kt:15`、`DirectoryFileScanner.kt:10`、`FileContentHasher.kt:15` |
| 抽象被绕过 | `NotificationDataSource.kt:296-299`：`StorageCleanNotificationListener` 在 data 文件里直接调 `NotificationDataSource.recordBlockedNotification(...)`，不走 `NotificationRepository` |
| 构造里硬 new 6 个 scanner | `feature/junkclean/CleanRepositoryImpl.kt:43-49`（`CacheScanner`/`TempFileScanner`/`ApkScanner`/`ResidualScanner`/`AdJunkScanner`/`DuplicateFileScanner` + `MemoryCleaner` object）→ 无法替换、无法单测 |
| 无统一错误类型 | 0 个 `AppError`/`Outcome`；177 `runCatching` + 29 `catch (_:` |
| 跨 feature 直接依赖 | **20 处**（见 4.2） |
| 无门禁 | `MotherTemplateBoundaryTest.kt:29,46` 检查 `"/logic/"` —— 项目里没有 `logic` 目录，**断言恒真** |

## 4.2 跨 feature 依赖：精确 20 处

| 源 → 目标 | 次数 | 代表 file:line |
|---|---|---|
| `home` → `settings` | 5 | `HomeScreen.kt:51,52`、`HomeRoute.kt:81,82`、`HomeViewModel.kt:11` |
| `home` → `toolbox` | 4 | `HomeViewModel.kt:7,8,10`、`HomeTabContent.kt:51` |
| `onboarding` → `toolbox` | 3 | `OnboardingScanScreen.kt:60`、`OnboardingScanViewModel.kt:5,6` |
| `toolbox` → `files` | 3 | `WhatsAppCleanerViewModel.kt:10,11,12`（**直接依赖 files 的 data 层 `FileRepository`**） |
| `toolbox` → `settings` | 2 | `BatteryInfoViewModel.kt:5`、`DeviceInfoViewModel.kt:18` |
| `notificationcleaner` → `toolbox` | 1 | `NotificationCleanerViews.kt:76` |
| `home` → `applock` | 1 | `HomeViewModel.kt:9` |
| `startup` → `onboarding` | 1 | `SplashViewModel.kt:5` |

最脏的三处：
1. `WhatsAppCleanerViewModel` 依赖 `feature.files.shared.data.FileRepository` → toolbox 被绑到 files 的 **data** 层。
2. `BatteryInfoViewModel` / `DeviceInfoViewModel` 依赖 `feature.settings.SettingsRepository` → settings 变成"全局配置袋"。
3. `HomeScreen`/`HomeRoute` 依赖 `feature.settings.ui.SettingsRateDialog` → **UI 组件被错放在 settings 内**。

## 4.3 错误吞噬：从"没扫到垃圾"退化为"没有权限"【P0-8】

```kotlin
// MediaStoreFileQuery.kt:31-68  —— MediaStore 查询失败统一吞成 emptyList()
return runCatching { resolver.query(...)?.use { ... } ?: emptyList() }
    .getOrElse { coroutineContext.ensureActive(); emptyList() }

// BaseFileScanner.kt:120-122
private fun safeListFiles(directory: File): List<File> =
    runCatchingLogged("listFiles:${directory.absolutePath}") { directory.listFiles()?.toList() }
        .getOrNull().orEmpty()

// FileContentHasher.kt:69  —— 哈希失败退化为 name+size 匹配
```

**四种语义**（权限被拒 / 存储未挂载 / IO 错误 / 目录为空）在 4 个地方**全部退化成 `emptyList()` / `null` / `false`**。后果：
- UI 无法区分「没扫到垃圾」与「没有 `MANAGE_EXTERNAL_STORAGE` 权限」→ **扫描显示 0 B，用户以为清理完成**；
- 哈希失败时 `DuplicateFileGrouping` 退化为 `name+size` 匹配（`FileContentHasher.kt:54-90`、`FileContentHasher.kt:69,79-84`）→ **误报重复文件** → `DuplicateFileScanner.kt:33 flatMap { drop(1) }` 把它当作"可删除冗余"呈现 → **删错文件风险**。

## 4.4 结构化病灶（会直接造成用户可见 bug）

| 病灶 | 证据 | 后果 |
|---|---|---|
| 状态撕裂：`flowState` + 子类 `uiState` 两个 StateFlow，Route 手工合成 | `BaseFileManagerViewModel.kt:19-22,43-52` + **9 个 Route** 的 `val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState`（`Audios:68`/`Documents:76`/`Duplicates:77`/`LargeFiles:75`/`PhotoPrivacy:65`/`Photos:70`/`Screenshots:69`/`SimilarPhotos:69`/`Videos:68`） | 「取消删除」关不掉：`cancelDelete()` 只改 `uiState.phase`（`PhotosManagerViewModel.kt:113-115`），`blockedPhase` 仍为 `ConfirmDelete` → UI 又改回去 |
| 错误文案不查资源 | `feature/files/shared/FileManagerMessages.kt:11-27`：`appString(resId)` 直接返回硬编码英文，`else -> ""` | 10 个 `values-*` 语言包（各 455 条，翻译完整）在这条路径**全部失效**；`else` 分支弹**空白 Toast** |
| `@StringRes Int` 与 `String` 双错误通道 | `JunkCleanContract.kt:57-58`；Reducer 同时写两套（`JunkCleanReducer.kt:57,90` vs `:91`） | UI 常优先显示 `error.message` → 用户看到 `java.io.IOException: ...` |
| 9 个文件管理器字节级复制粘贴 | `audios` 与 `videos` 的 Route(6351B)/VM(6601B)/Screen(5434B) **完全相同**，仅标识符改名 | 一个 bug 要改 N 处；`BaseFileManagerViewModel` 的 8-lambda 回调包（`:88-120`）正是这种复制的成因 |

## 4.5 目标形态

```
:core:foundation      AppError / Outcome / Clock / AppDispatchers(interface)
:core:device          DeviceInfoRepository / StorageInfo / BatteryInfo / NetworkInfoReader
:core:preferences     DataStore<UserSettings> + 各域 Preferences port
:shared:filestore     ManagedFileItem / FileRepository(port) / FileManagerFilters
:shared:clean         CleanRepository(port) / JunkScanner 家族 / CleanSessionStore
:feature:<x>/domain/{model,service,port,rules,error}
:feature:<x>/data/{local,remote,repository,platform}
:feature:<x>/view/<page>/{presentation,ui}
:feature:<x>/di/XxxFeatureModule
```

## 4.6 验收断言

```text
feature 内 "import ...spaceclean.app."                       == 0
跨 feature import（A != B）                                  == 0
每个 feature 都有 domain/ 目录                                == 100%
domain 内 "import android."                                  == 0
"object *DataSource"                                         == 0（全部 class + 构造注入）
sealed AppError / Outcome 类型                               == 1（core:foundation）
架构测试检查的目录真实存在（presentation/domain/ui）           == 100%，且进 CI
```

---

# 5. ③ MVI 模式 + ④ 权限/广告/打点接入

> 这两条是同一件事的两半：**基类**是骨架，**拦截器**是插槽。必须一起定。

## 5.1 ③ 现状：29 个 ViewModel 的形态矩阵（摘要）

| 维度 | 结果 |
|---|---|
| 继承统一基类 | **0 / 29** |
| 持有 `Application` | 5（`AndroidViewModel` ×4 + 注入 ×1）；另 1 个方法收 Activity `Context`（`JunkCleanViewModel.kt:100`） |
| 单一 `StateFlow` | 20 / 29 |
| **2 个独立 `StateFlow`** | **9 / 29**（`VirusScan` + 9 个文件管理器继承的 `flowState`） |
| 安全副作用通道（`Channel`） | 6 / 29 |
| **有风险的 `MutableSharedFlow(extraBufferCapacity = 1)`（replay=0）** | **6 / 29** |
| state-as-event | 3（`Home.exitPromptRoute`、`AppLock.toastRes`+`overlayPermissionRequired`、`NotificationCleaner` 部分） |
| 有纯 Reducer | **3 / 29** |
| 有直接测试 | **6 / 29** |

「精髓」缺失的三条：

### D-③-1 状态用多布尔摊平，可同时 Loading + Error + Scanning【高 · 确认】
- `NotificationCleanerUiState`（`NotificationCleanerViewModel.kt:39-60`）：10 个渲染标志 + `:96-101` 五个裸 Long/Bool 当隐式状态机（`activeScanGeneration` / `pendingScanCompletionGeneration` / `deferredScanCompletionGeneration` / `cleaningCompletionPending` / `postCompletionPage`）。
- `JunkCleanUiState`（`JunkCleanContract.kt:44-61`）：**两个重叠枚举** `phase` + `scanState` → 可表示 `phase=Cleaning` + `scanState=Scanning` 这类非法组合，Reducer 自己要不断交叉校验（`JunkCleanReducer.kt:29,89`）。
- `AppUsage`/`NetworkUsage`/`DeviceInfo`：各 5 个布尔（`isLoading`/`errorMessage`/`isScanning`/`showStopDialog`/`permissionRequestPending`）。
- **实证后果**：`NotificationCleanerViews.kt:177-184` 里 `errorMessage != null` 优先于 `page` → 清理完成页收到迟到错误就被**整页替换**，`cleanedCount` 丢失。

### D-③-2 Effect 用 `SharedFlow(replay=0)` → 事件丢失【高 · 确认】
6 个 VM：`DeviceInfoViewModel.kt:365`、`BatteryInfoViewModel.kt:43`、`AppUsageViewModel.kt:122`、`NetworkUsageViewModel.kt:156`、`OnboardingScanViewModel.kt:47`、`VirusScanViewModel.kt:60`。
收集者在 `LaunchedEffect` 里才启动（如 `DeviceInfoRoute.kt:32-39`）；`AppUsageRoute.kt:41` 甚至把 `context` 放进 key → 配置变更即重启收集器。
**后果**：返回键无响应、`OpenAppInfo` 点了没反应。

### D-③-3 状态驱动导航 → 旋转/计数变化重复跳页【高 · 确认】
`VirusScanScreens.kt:61-69`：`LaunchedEffect(scanStarted, scanCompleted, effectiveThreatCount) { router.replace(...) }` —— `scanCompleted` 是**持久状态**不是一次性效果；删掉一条威胁后 `effectiveThreatCount` 变化 → **把用户从结果页踢回扫描页**。
同类：`VirusResultRoutes.kt:58-60`、`FileDetailScreen.kt:37-39`（Screen 内发导航指令）。

### D-③-4 UiState 含 `Drawable`【中 · 确认】
`VirusScanModels.kt:12,22`：`VirusThreat.icon: Drawable?`、`VirusScanUiState.currentIcon: Drawable?` → `equals` 依赖引用相等 → `distinctUntilChanged` 永远认为"变了"（无意义重组）+ JVM 单测不可构造。

### D-③-5 入口命名 5 套，`onIntent` 全项目 0 次【中 · 确认】
`onAction`(12) / `onHomeAction`(1) / 私有 `dispatch`(2) / 散装 public fun（`JunkCleanViewModel` 12 个、9 个文件管理器各 ~15 个）/ `NotificationPermissionViewModel` 的 7 个专用方法。
Effect 命名 4 套：`XxxEffect` / `JunkCleanEvent` / `NotificationPermissionCommand.Host` / 完全没有。
**三套层级类型**（明令禁止）：`JunkCleanAction` + `JunkCleanUiAction` + `JunkCleanEvent`（`JunkCleanContract.kt:63,91,101`）。

### D-③-6 Reducer 不纯【中 · 确认】
`JunkCleanReducer.reduce` 内直接调 `FileSizeFormatter.format(...)`（`:35,47`）并把结果缓存进 state（`JunkCleanContract.kt:51 formattedFoundSize`）→ locale/单位变化不刷新，且 reducer 不再是纯函数映射。

## 5.2 ④ 广告接入：**把"业务继续 / 导航"当成广告回调**【P0-1】

```kotlin
// common/feature/FeatureFlow.kt:3-7 —— 接口只有两件事，且都带 onContinue
interface FeatureFlow {
    fun handleOperation(event: FeatureOperationEvent, onContinue: () -> Unit = {})
    fun exit(feature: FeatureKey, reason: FeatureExitReason, onContinue: () -> Unit)
}

// app/runtime/AdFeatureFlow.kt:26-38 —— 业务继续 = 广告关闭
if (request == null) continueOnce() else executor.execute(request, continueOnce)
```

**`onContinue` 被当作业务继续的实际位置（7 处，全是卡死风险点）**
| 位置 | 被广告阻塞的业务动作 |
|---|---|
| `feature/junkclean/JunkCleanRoute.kt:67-69` | 清理完成后切结果页 `showResult()` |
| `feature/files/shared/FileManagerRouteSupport.kt:52-54` | 删除后 `showPendingResult()` |
| `feature/toolbox/networkspeed/NetworkSpeedRoute.kt:53-55` | 测速结果页 |
| `feature/notificationcleaner/NotificationCleanerRoute.kt:48-52` | `ShowStatus(scanGeneration)` |
| `feature/notificationcleaner/NotificationCleanerRoute.kt:57-60` | `CompletionAdFinished` + `ShowStatusAfterCompletionAd` |
| `feature/toolbox/whatsappcleaner/WhatsAppCleanerRoute.kt:46-48` | `ShowResult` |
| `app/navigation/register/OnboardingRoutes.kt:24-33` | **首次启动"进入主页"** |

另有 **19 处 `featureFlow.exit(...) { navigator.back()/home() }`**：`JunkCleanRoute:56,77,96`、`AppLockRoute:87`、`NetworkSpeedRoute:36`、`NetworkScanRoute:48`、`NetworkUsageRoute:41`、`AppUsageRoute:63`、`DeviceInfoRoute:36`、`BatteryInfoRoute:33`、`AntiVirusRoute:41`、`NotificationCleanerRoute:71`、`WhatsAppCleanerRoute:29,60,63`、`FileManagerRouteSupport:119,130,141,184`。

### D-④-1 插屏无超时 → 队列永久死锁 → 全站导航失效【致命 · 确认】
`common/ads/AdRuntime.kt:178-208` `execute()` 只有 SDK 回调一条出路；**对比开屏有超时**（`:333` `OPEN_AD_START_TIMEOUT_MS`、`:269` `STARTUP_CONSENT_SHOW_TIMEOUT_MS`）—— 说明作者知道要超时，只是插屏漏了。
`AdvertiseAdRuntimeDriver.showInterstitial` 恒返回 `true`（`:423-427`），`started` 无信息量。
**一旦回调丢失**：`activeRequest` 永不清空（只有 `complete()` 会清，`:219-240`）→ 后续所有请求进队列永不执行 → 入口点击 / 返回键 / 结果页全部无反应，且无日志、无埋点、无崩溃。

### D-④-2 重复 `requestId` 静默丢弃 `onContinue`【致命 · 确认】
`AdRuntime.kt:156-176`：`if (!accepted) return` —— **不调 `onContinue`**。
而 id 是常量拼的：`:97` `"route_enter_${feature}_${route}"`、`FeatureEventAdPolicy.kt:19,22` `"return_home_${feature}"` / `"permission_rejected_${feature}"`、`OnboardingRoutes.kt:25` `"onboarding_scan_finish"`。
`once{}`（`AdCallbacks.kt:5-12`）只保证"至多一次"，**没人保证"至少一次"**；被吞掉的那次不留痕迹。

### D-④-3 两个 `AdRuntime` 实例竞争同一份全局 SDK 开关【高 · 确认】
`AppRuntimeBindings.kt:37-48`（主，带 `stateProvider`）与 `OpenAdHostActivity.kt:102`（自建，`activityProvider`）。
二者各有独立 `ColdStartAppOpenGuard` / `ExternalAppOpenGuard`，但操作的是同一份 `AdvertiseSdk.isAppOpenAdEnabled` / `suppressNextAppOpenAd`（`AdvertiseSdkAdapter.kt:122-126`）→ 互相踩踏：可能出现"该抑制的前台开屏照样弹"或"冷启动后开屏能力被永久关掉"。

### D-④-4 `busy` 标志命令式置位可永久为 true → 静默停投【高 · 确认】
`AppRuntimeBindings.kt:40-45` 的 `featureOperationActive` 唯一复位入口是 `AdFeatureFlow.kt:41-45`（`Finished` 或 `exit()`）。
**真实路径**：扫描中从底部工具条离开 → `JunkCleanRoute.kt:38` `onNavigateTool = navigator::resetTo` → **不经过 `featureFlow.exit()`** → `featureOperationActive` 恒 true → `AdRuntime.kt:215` 拒绝一切广告 → **本次会话剩余时间零收入，且无日志**。

### D-④-5 `completionAdInFlight` 未复位 → 返回键永久失效【中 · 确认】
`NotificationCleanerRoute.kt:55-61` 先置 `true`，只有广告回调才置 `false`；`:75` `fun handleBack() { if (state.completionAdInFlight) return }` → 回调丢失则**返回键彻底死掉**（连 `BackHandler` 都走这里）。

## 5.3 ④ 权限接入：26 处回调 + 三套体系 + 可永久死锁【P0-2】

### D-④-6 三/四套权限体系并存，状态不共享【高 · 确认】
| 层 | 文件 |
|---|---|
| 引擎层 | `common/permission/PermissionEngine.kt:7-84`、`CommonPermissionHandlers.kt`、`PermissionHandler.kt`、`AppPermissionCoordinator.kt` |
| 实现层 | `app/permission/PermissionCoordinator.kt:16-189`、`NotificationPermissionPolicy.kt`（305 行）、`NotificationPermissionHost` |
| UI 层 | `common/ui/permission/StorageCleanPermissionUi.kt`、`NotificationEnableGuideDialog.kt`、`common/ui/components/NotificationPermissionDialog.kt`、`popups/PermissionPopups.kt` |
| feature 自带 | `feature/applock/AppLockPermissionUtils.kt`、`feature/notificationcleaner/NotificationSettingsGateway.kt`、`feature/settings/ManagePermissions*` |

后果：`ManagePermissionsScreen` 展示的权限状态与 `PermissionCoordinator`/`PermissionEngine` **没有共享源** → "显示已授权但功能仍要求授权"。

### D-④-7 两套几乎相同的通知权限弹窗可同时叠加【高 · 确认】
`common/ui/components/NotificationPermissionDialog.kt:33-124` 与 `common/ui/permission/NotificationEnableGuideDialog.kt:37-157` 结构与文案完全相同；`AppRoot.kt:53-72` 同时挂载 `AppPermissionHost` 与 `NotificationPermissionHost` → 两个 Dialog 的 scrim 互相遮挡 → 用户看到"点了没反应"。
且同一弹窗被用于两种权限：`StorageCleanPermissionUi.kt:21-27` 判据是 `PermissionType.NotificationListener`，文案却是"允许通知" → **文案错配 + 埋点无法区分**。

### D-④-8 `ensure()` 在已有会话时静默 return → 调用方回调永不执行【高 · 确认】
`app/permission/PermissionCoordinator.kt:97-104` `if (session != null) return`。
调用方按"一定会有一个回调"写代码：`JunkCleanRoute.kt:49-62`（`onGranted = { viewModel.startScanIfNeeded() }`）。
**最坏组合**：`AppUsageRoute.kt:80-89` / `NetworkUsageRoute.kt:58-66` **先把 `permissionRequestPending` 消费掉再申请** → 被丢弃后页面永久停在"请求权限中"，无重试入口。

### D-④-9 `settingsLaunchObservedPause` 丢失 → 权限与广告双停【P0-2 链路】
`PermissionCoordinator.kt:143-153` `queueSettings` 后 `showDialog = false`（UI 不再可见）；`:91-95` `onSettingsReturnIfReady()` 要求**必须观察到 `ON_PAUSE`**。
若该事件未被观察到（轻量/透明设置页、result 回调先于 pause、lifecycle owner 被替换）：
```
session 永久非 null（:20）
→ AppPermissionHost.kt:36-38 → sessionCoordinator.set(Permission, true)
→ AdRuntime.kt:213 permissionFlowActive 恒 true → 所有插屏被拒（收入归零）
→ 回到 D-④-8 → 所有后续 ensure() 被静默丢弃（权限功能全停）
→ 唯一恢复手段：杀进程
```

### D-④-10 Android 13+ 每次 `ON_RESUME` 都可能再弹系统通知权限【高 · 确认】
`NotificationPermissionPolicy.kt:217-246`：`shouldShowRationale` 成立就再发 `RequestSystemPermission(HomeSystem)`；`customPromptDeferredForSession` 只拦**自定义**弹窗，**不拦系统弹窗**。`NotificationPermissionHost.kt:87-98` 在 `ON_RESUME` 触发。
→ 用户拒绝一次后每次回前台都弹 → Play「重复权限请求」风险。

### D-④-11 「拒绝权限」后立刻弹插屏【P0-6 · 确认】
`FeatureEventAdPolicy.kt:21-24` `PermissionRejected -> InterstitialRequest(...)`；接入点 5 处：`WhatsAppCleanerRoute.kt:35-41`、`FileManagerRouteSupport.kt:183-188`、`JunkCleanRoute.kt:55-61`、`AppUsageRoute.kt:86`、`NetworkUsageRoute.kt:64`。
`AdRuntime.kt:213` 里**唯独 `PermissionRejected` 被放行**绕过 `permissionFlowActive` 保护 → 作者知道权限流与广告互斥，却为收入开了特例。**这是明确的政策红线。**

## 5.4 ④ 广告频率与合规【P0-6】

- `config/ad_policy.json`：**33 处 `interval_seconds: 0`，仅 1 处非 0**；全局 `limited: 50` + `first_open_enabled: true`。
- 一次"清垃圾"路径最多 3 屏插屏：`ENTER → MAIN_JUNK_CLEAN` → `OperationFinished → MAIN_JUNK_CLEAN_FINISH` → `exit(Return) → RETURN_FROM_JUNK_CLEAN`，后两步间隔常 < 2 秒（`JunkCleanRoute.kt:94-104,133`）。
- `AdPlacementRegistry.kt:12-13`：`PermissionRejected` **复用** `returnArea` → 两个语义不同的场景互相挤占频控额度。

## 5.5 ④ 打点接入：建了一层"假抽象"【高 · 确认】

```kotlin
// common/analytics/AnalyticsReporter.kt:6-8      接口
interface AnalyticsReporter { fun report(event: AnalyticsEvent) }
// 同文件 :10-27                                   sealed 15 个事件
// common/analytics/AnalyticsPayloadMapper.kt      sealed → AnalyticsTracker.Event 字符串常量（第二层映射，纯浪费）
// app/analytics/AppAnalyticsReporter.kt:8-17      实现 = 转调全局 object
// common/analytics/AnalyticsTracker.kt:19-33      325 行 object + private lateinit var preferences
// app/runtime/AppRuntimeBindings.kt:51            remember { AppAnalyticsReporter() }  ← Compose 里 new，绕过 Koin
```

**打点实际发生的 7 处，全在错的地方**
| 位置 | 内容 |
|---|---|
| `app/runtime/AppRuntimeEffects.kt:41,44` | Home 进入 / 功能进入（**Composable 里直接调全局 object**） |
| `app/MyApp.kt:91-92` | 前后台 |
| `app/runtime/AdFeatureFlow.kt:30` | **业务操作事件**（业务→广告流里顺手打点） |
| `HomeRoute.kt:133`、`SettingsRoute.kt:39,42`、`OnboardingRoute.kt:29-38`、`NotificationPermissionHost.kt:52`、`SplashRoutes.kt:126` | 5 个 Route 各自 `analyticsReporter.report(...)` |

结构性问题：
1. **ViewModel 层完全无法打点**（拿不到 reporter、无拦截器）→ 最该在 VM 打的"点击/结果/失败"被迫塞进 Route 与 Composable；
2. 三层映射（`AnalyticsEvent` sealed → `AnalyticsPayloadMapper` → `AnalyticsTracker.Event` 字符串）中间两层是自造负担；
3. `AnalyticsTracker` 是 `object` + `lateinit` → 单测无法替换 sink；`ProcessLifecycleOwner` 回调若早于 `initialize`（`MyApp.kt:46`）→ `UninitializedPropertyAccessException` 崩在 Application；
4. **广告侧 0 埋点**（`AdRuntime.kt` 551 行无一次 analytics）→ 上述所有"静默失效"线上不可见。

## 5.6 ④ 目标形态：三件事统一收口

```kotlin
// core:presentation
sealed interface IntentPolicy {
    val priority: Int
    data object AuthRequired : IntentPolicy
    data class PermissionRequired(val action: ProtectedAction) : IntentPolicy
    data class ShowAd(val placement: AdPlacement) : IntentPolicy
    data class Track(val event: AnalyticsEvent) : IntentPolicy
}
sealed interface InterceptResult {
    data object Proceed : InterceptResult
    data class Blocked(val reason: IntentBlockReason) : InterceptResult
    data class Deferred(val requestId: String, val completion: Deferred<GateOutcome>) : InterceptResult
}
fun interface IntentInterceptor<I : Any> {
    fun intercept(intent: I, policy: IntentPolicy): InterceptResult
}
```

收敛效果：

| 现状 | 收敛后 |
|---|---|
| 26 处 `permissionCoordinator.ensure { vm.xxx() }` | 每页 `policiesFor(intent)` 里一行声明；鉴权成功**自动重派原 Intent** |
| 12 个 Route 带 `featureFlow` 参数 + 8 个 `register*Routes` 传它 | `FeatureFlow` / `AdFeatureFlow` / `FeatureEntryNavigator` **全部删除** |
| 7 处分散打点 | 打点随 Intent 走，VM 内自足 |
| Route 签名 `(args, permissionCoordinator, featureFlow, analyticsReporter, navigator, vm, 4~6 个 lambda)` | 回到 `(args, navigator, viewModel)` + 可选一个 typed `onHostEvent` |

## 5.7 验收断言

```text
feature 内 "grep -c featureFlow"                             == 0
feature 内 "grep -c permissionCoordinator"                   == 0
feature/common 内 "grep -rc AnalyticsTracker\."              == 0
每个 VM 的 MutableStateFlow 声明数                            == 1
"MutableSharedFlow"                                          == 0
"UiAction|UiIntent|InternalAction|UiEffect"                  == 0
"onAction"                                                   == 0（统一 onIntent）
Screen 文件内 "ViewModel|AppNavigator|AppDestination"        == 0
每个 VM 至少 1 条 policiesFor 单测                            == 100%
```

---

# 6. ⑤ 日志 + ⑥ app 层/DI（含构建与持久化）

## 6.1 ⑤ 日志：18 处裸 Log + 真实泄露 + 零上报

| 项 | 证据 |
|---|---|
| 裸 `android.util.Log` | `MainActivity.kt:68,75`、`MyApp.kt:72-80`、`OpenAdHostActivity.kt:69,89,112,120,180,184`、`NotificationIntentRouteResolver.kt:43`、`AdRuntime.kt:544,549`、`AdvertiseConfigFactory.kt:200`、`AnalyticsTracker.kt:25`、`VirusScanSupport.kt:23`、`VirusScanViewModel.kt:522`、`BaseFileScanner.kt:128` —— **10 个文件 18 处，各定 TAG** |
| 自造半个框架 | `AdRuntime.kt:543-551` 写了 `logD`/`logW` + `runCatching`，只在本文件生效 |
| Koin 日志进生产 | `MyApp.kt:42` `androidLogger()` **无 DEBUG 门禁**（同文件 `:52` 却给广告 SDK 加了 `BuildConfig.DEBUG`，说明团队知道该门禁） |
| **确实泄露密钥** | `VirusSecurityRepositoryImpl.kt:55-61` `logState = "present(length=, suffix=${apiKey.takeLast(6)})"` → `VirusScanViewModel.kt:522` `Log.w` —— **release 同样输出** |
| 无法关/无法脱敏/无法接 Crashlytics | release 下 `Log.w/e` 照常输出；`Log.d` 的字符串拼接照常求值；Crashlytics 只有插件无运行时依赖 |

**目标**：`core:logging`（纯 JVM）= `LogEvent` + `LogLevel` + `LogSink` + `EventLogger`（同步按序分发、单 sink 异常隔离但 `CancellationException` 必须传播、`setEnabled` 不缓存不补发、空 sink 列表是合法"无输出"），全 app 唯一 `AndroidLogSink`（JSON 转义 + 字段白名单），Crashlytics 作为第二个 sink。

**验收**：`grep -rc "android.util.Log"` = 0；`grep -c androidLogger` = 0；`LogEvent` 白名单有单测（key/token/secret/pin/password/URL/异常详情默认不落）；release AAB 内 `Log.d` 字符串 = 0。

## 6.2 ⑥ app 层与 DI

| 项 | 证据 |
|---|---|
| 装配是手搓的 | `AppRuntimeBindings.kt:37-66`：5 层 `remember` 手工组图（`AdRuntime → AdRuntimeFacade → InterstitialExecutor → ExternalActivityTracker → AdFeatureFlow`）；`AnalyticsReporter`、`PermissionCoordinator`、`AppSessionCoordinator`、`AppLaunchCoordinator`、`AppSessionAnalytics` 全在 Compose/Activity 里 `new` |
| DI 只有 2 个巨型文件 | `DataModule.kt`（99 行 / 30 个 `single{}`，import 来自 9 个 feature 的 22 个子包）+ `PresentationModule.kt`（85 行 / 25 个 `viewModel{ X(get(), get(), get()) }`）→ **位置参数**，加一个构造参数全靠猜 |
| dispatcher 两种风格混用 | `get<AppDispatchers>().io`（9 处）vs 硬编码 `Dispatchers.IO`（9 处，含 `JunkCleanViewModel`/9 个 FileManager/`AppLockViewModel`）；全仓 `Dispatchers.*` 字面量 **80** 处 |
| 有状态/SDK 单例绕过 Koin（约 15 个） | `AnalyticsTracker`（可变状态 + `lateinit`）、`AdvertiseSdkAdapter`、`AdvertiseConfigFactory`、`AdPlacementRegistry`、`AppPrefsUtils`（需手动 `initialize`，漏调 `error()` 崩）、`AppLockManager`、`NetworkScanSessionStore`、7 个 `object *DataSource` |
| 双路径取依赖 | Koin（`by inject()`/`koinInject`/`koinViewModel`）与 `application as? MyApp`（`MainActivity.kt:66`、`OpenAdHostActivity.kt:95`）并存；`MyApp.instance` 定义但**零使用**（死代码） |
| 无 Koin 校验 | `checkModules()` 单测 0 个 |

**目标**：`app` 只剩 Application / Activity / AppRoot(≤120 行) / DI 聚合 / NavGraph 聚合；每 feature 一个 `XxxFeatureModule`；`AdRuntime` 进程级唯一（Koin `single`）；`AppRoot` 只 `koinInject` 不做 `remember { new }`；`viewModelOf(::X)` / `singleOf(::X)` 替代位置参数 `get()`。

**验收**：`AppRoot.kt`/`AppRuntimeBindings.kt` 内"构造业务对象"的 `remember` = 0；DI 文件拆分后单文件 ≤ 40 行；`grep -c "Dispatchers.IO"` = 0；`grep -c androidLogger` = 0；`checkModules()` 通过。

## 6.3 持久化与安全

| 缺陷 | 证据 | 级别 |
|---|---|---|
| PIN 明文 + `allowBackup=true` | `AppLockRepositoryImpl.kt:32,36`（明文写/明文比对）+ `AndroidManifest.xml:39` `android:allowBackup="true"`（`SharedPreferences` 默认进云备份 → **换机恢复后 PIN 仍生效**） | 致命 |
| 绕过 Repository 二次访问 PIN | `LockScreenOverlayService.kt:167` 直接 `AppPrefsUtils.getString(KEY_PIN, "")`；`AppLockManager.kt:5-9` object 门面绕过 DI | 高 |
| `AppPrefsUtils` 全局静态 + 全 `commit()` | `AppPrefsUtils.kt:8-27`（185 行 / 24 个方法 / 双份 API）+ `:33,42,51,60,74,86,98,110,122,137` 全部 `.commit()`；`error()` 是运行期崩溃；`PREFS_NAME` 在 `AppPrefsUtils.kt:9` 与 `AppLockRepositoryImpl.kt:127` **重复定义** | 高 |
| 3 个无主键空间 | `quick_clean_settings`（`AppPreferences.kt:14`，母版品牌残留）、`phone_cleaner_sp`（`AppPrefsUtils.kt:9`）、`notification_blocker`（`NotificationDataSource.kt:16`）；**0 DataStore / 0 Room** | 高 |
| 无 `:core:security` | 全仓 0 处 `KeyStore`/`Cipher`/`EncryptedSharedPreferences` | 高 |

## 6.4 后台任务与生命力

| 缺陷 | 证据 | 级别 |
|---|---|---|
| FGS 5 秒规则依赖"onCreate 前半段不抛异常" | `AppBackgroundRuntimeService.kt:36-55`：`onCreate` 先构造 3 个 `by inject()` + 3 个 coordinator + 命令分发器，**第 55 行才 `notification.initialize()`（含 `startForeground`）** → 中间任何异常 → `RemoteServiceException: did not then call Service.startForeground()` | 高 |
| 后台启动失败被静默吞 | `AppBackgroundRuntimeService.kt:198-219` `runCatching{ startForegroundService }.isSuccess` → 无日志、无重试 | 高 |
| 8 秒内命令被静默丢弃 | `:202` `if (!startInFlight.compareAndSet(false,true)) return` | 高 |
| 停止后被系统重启即复活 | `:78,88`：`intent == null` 按 `Start` 处理并 `stopRequested.set(false)` | 中 |
| 通知被 60s 复活 + WakeLock 不续期 | `ForegroundServiceNotificationController.kt:103-115`（watchdog 每 60s 重发通知）+ `:137` `acquire(10min)` 无续期 → AppLock 10 分钟后静默失效 | 高 |
| 双常驻通知 | 自建 id=17（`AppBackgroundRuntimeService.kt:132`）+ SDK `CommonService`（`AdvertiseConfigFactory.kt:111` 硬编码 `persistentServiceEnabled = true`） | 中 |
| 无开机自启 | manifest 无 `RECEIVE_BOOT_COMPLETED`/boot receiver → AppLock/采样只有用户手动开 App 才恢复 | 中 |
| 浮层服务非 FGS + `START_NOT_STICKY` + 标志不复位 | `AppLockMonitorHandler.kt:97-104` + `LockScreenOverlayService.kt:48-60`；`lockScreenShowing` 只靠广播复位（`AppLockMonitorHandler.kt:92` 之后永不再锁） | 高 |

## 6.5 构建与依赖

| 缺陷 | 证据 | 级别 |
|---|---|---|
| **本地 AAR 注入 `usesCleartextTraffic="true"` + `QUERY_ALL_PACKAGES`** | `app/libs/cloudscan_sdk_5.0.18.20250821.aar` 的 manifest 被合并进 release；`app/src/main/AndroidManifest.xml` 从未声明、`res/xml/network_security_config.xml` **不存在**；实测 `usesCleartextTraffic="true"`、`QUERY_ALL_PACKAGES`、`requestLegacyExternalStorage="true"`（后者在 targetSdk 36 已被忽略） | 致命 |
| `:junkcc` 让收缩全部失效 | `proguard-rules.pro:70-72` `-keep class ...junkcc.** { *; }` + `res/raw/junkcc_keep.xml`（54 KB 全量 keep）→ R8 与 `isShrinkResources` 对 1,800 类 / 1,802 资源失效；AAB 51.3 MB | 致命 |
| 无 signingConfig | `app/build.gradle.kts` 全文无 `signingConfigs` → `assembleRelease` 产出 unsigned | 中 |
| 无 testOptions / lint / packaging / flavor | 同上；`ktlint` 已 apply 但未串入 `check` | 中 |
| jitpack 域名笔误 | `settings.gradle.kts:23` `https://www.jitpack.io`（正确为 `jitpack.io`） | 中 |
| `flatDir app/libs` 多余且危险 | `settings.gradle.kts:32-34`：AAR 是 `files(...)` 引入不经仓库，`flatDir` 会让任意坐标解析失败时静默回退本地裸文件 | 中 |
| 内网明文 maven | `settings.gradle.kts:17-18` `http://192.168.10.128:8081` + `isAllowInsecureProtocol = true` | 中 |
| pangle `exclude` 可能是空操作 | `app/build.gradle.kts:65-69` exclude `com.pangle.global:pag-sdk`，但直接依赖的是 `pag-sdk-ad`（**不同 artifactId**） | 高 |
| 非语义版本进生产 | `panglePagSdkAd = "unfat-7502-20250805192841-release"`；`tiktok = "1.3.7-rc.2"`（RC） | 中 |
| 未使用依赖 + 残留 proguard | 版本目录 8 个库 + 1 个插件未使用（`glide`/`gson`/`work`/`integrity`/`serialization`/`constraintlayout`/`coroutines-core`）；`proguard-rules.pro:33-51` 保留 WorkManager/Room keep 但库不存在 | 低 |
| `app_name` 双重定义 | `product-config.gradle:52` resValue + `strings.xml:4`（后者是死资源） | 低 |
| 密钥入库 | `config/product.json` 提交 Trustlook key / Singular key+secret / remote config encryption key / Play Integrity parseTokenKey；`app/google-services.json` 同样入库 | 致命 |
| 母版拷贝残留 | `config/variant-source.json`（含绝对路径，无代码读取）；"多变体"实为 `BuildConfig.PRODUCT_THEME_KEY` 的 `when`（`Theme.kt:57-60`），只有 2 套 token | 中 |

---

# 7. 门禁与工程化失效（附录 A）

**这是本项目最深层的问题**：不是"没人写规范"，而是**写了 5 个门禁脚本 + 1 个架构测试，全部空转或必然失败**，所以上述缺陷能长期存在于主干。

| 脚本 | 状态 | 原因 |
|---|---|---|
| `scripts/check-product-config.ps1` | **必然 exit 1** | 7 条断言是"白牌包不得含商业化集成"，与当前商业化状态完全相反 |
| `scripts/check-brand-residue.ps1` | **必然 exit 1**；不传参时 foreach 不执行 → **什么都不检查** | 同上 + 默认 `$Forbidden*` 为空数组 |
| `scripts/check-template.ps1` | **必然 throw** | 要求 `app/google-services.json` 不存在 |
| `scripts/check-variant-boundaries.ps1` | **恒空转通过（永远报绿）** | 检查 `app/.../use`、`use/skin`、`use/core`、`use/feature` —— 这些目录 4 代重构前已不存在 |
| `scripts/check-dependencies.ps1` | **必然 exit 1** | 引用 6 个不存在的 `use/skin/*Screen.kt` + 依赖本机未安装的 `rg` |
| `app/src/test/.../architecture/MotherTemplateBoundaryTest.kt` | **4/6 断言恒真空跑** | 检查不存在的 `logic/` 目录与 `ui/` 下的 `*ViewModel.kt` |
| CI | **无** | `.github` 不存在；`app/build/test-results/` 只有 1 个 XML → 33 个测试类只跑过 1 个 |

**附带的测试体系问题**
- 33 个单测文件的**目录与 `package` 严重错位**（`use/skin/...`、`use/feature/.../presentation/...`、`presentation/app/...`、`battery/`、`operation/`、`config/` 等**生产代码里都不存在的路径**）—— 是 4 代重构的化石层；import 全部可解析，所以不会编译失败，只是"Go to Test"全部失效。
- `androidTest` 只有 1 个模板产物（断言 `packageName`），而 `applicationId` 来自 `product.json`。
- `templates/feature/*`（8 个文件）**100% 未被采用**：真实代码里 `feature/*/domain/` 与 `feature/*/presentation/` 目录数 = 0。
- 无测试的 feature 恰好是业务价值最高的：`home` / `antivirus`（`VirusScanViewModel` 25.7 KB 全仓最大）/ `applock`（17.8 KB）/ `notificationcleaner`（26.3 KB）/ `toolbox/{battery,networkscan,networkspeed,networkusage,whatsappcleaner}` / `common/ads`（`AdRuntime` 19.5 KB）/ `common/analytics`。

**文档漂移**
- `docs/ice_age_survival_game_design_and_development.md`（57 KB，冰河时代游戏设计，与项目无关，占 docs 总字节 96%）
- `docs/testtt.md`（0 字节）
- `docs/figma-screen-map.md` 只覆盖 6/20 个屏幕且 2 个才 Implemented；其 `Rules` 段写着"Keep device data, ViewModels, repositories, navigation, and effects **from the mother project**"—— 与重构方向相反

---

# 8. 重构方向（仅方向，实施计划另议）

## 8.1 目标模块图

```
:app                       Application / Activity / AppRoot(≤120) / DI 聚合 / NavGraph 聚合
:core:foundation           AppError / Outcome / Clock / AppDispatchers(interface) / FileSizeFormatter
:core:logging              LogEvent / LogLevel / LogSink / EventLogger（纯 JVM）
:core:presentation         MviViewModel / IntentPolicy / IntentInterceptor / IntentPolicy 四类
:core:navigation           AppDestination(类型安全) / AppOverlayDestination / AppNavigator / DestinationPolicy
:core:designsystem         common/ui(41 文件) + theme tokens + CleanX* 组件
:core:permission           common/permission(11 文件) + PermissionEngine
:core:preferences          DataStore<UserSettings> + 各域 Preferences port
:core:security             KeystoreCipher / PinHasher(PBKDF2)
:core:monetization         common/ads + common/analytics + common/commercial（唯一允许 import 三方 SDK）
:core:device               DeviceInfoRepository / StorageInfo / BatteryInfo / NetworkInfoReader
:shared:filestore          ManagedFileItem / FileRepository(port) / FileManagerFilters
:shared:clean              CleanRepository(port) / JunkScanner 家族 / CleanSessionStore
:shared:session            AppLaunchRequest / NotificationLaunchSource
:feature:<x>               home / files / junkclean / antivirus / applock / notificationcleaner
                           toolbox / settings / startup / onboarding
```

依赖方向：`app → feature/shared/core`，`feature → shared/core`，`shared → core`，**feature 之间零依赖**。

## 8.2 六条线的依赖顺序（不是 1→6）

```
⑥ 装配（DI/app）      ── 让 ③④ 可测
② 物理边界（分层）    ── 让 ③ 有 Service 可调
③ MVI 骨架            ── 定契约
④ 拦截器              ── 同一件事的另一半，与 ③ 一起定
⑤ 日志                ── 横切，独立可先做
① 路由                ── 机械量最大，放最后（类型安全化能顺手删一堆代码）
```

**建议实施序**：`⑤ 日志`（零依赖、一天）→ `③+④ MVI 骨架与拦截器`（最高杠杆）→ `⑥ DI` → `② 分层` → `① 路由`。

## 8.3 每阶段的"行为不变"保证方式

1. 一次只改一层：Contract 类型改名不与行为修改同 commit；
2. 先写等价性测试再改：对可 reducer 化的部分，先补「旧实现输入 → 关键字段快照」，迁移后断言新实现产生相同快照；
3. `@Deprecated` 过渡：新旧入口可并存 1~2 个迭代，旧入口委托新入口，最后统一删除。

---

# 9. 排查中被推翻的假设（附录 B，保留以免重复踩坑）

| 假设 | 实测事实 |
|---|---|
| `resValues = true` 但没有 `resValue` 声明 | **有 5 个消费者**（`product-config.gradle:52-56`）。真问题是 `strings.xml:4` 也定义 `app_name` → 双来源 |
| `compileSdk { release(36) { minorApiLevel = 1 } }` 写法可疑 | **是 AGP 9 的正确 DSL**，`android-36.1` 已安装、构建成功。真问题是与 `targetSdk = 36` 不一致 + 让 CI 硬依赖非默认安装的 SDK Platform |
| ViewModel 直接读 MediaStore / 做文件 IO | 33 个 VM 里**只有 1 处**（`JunkCleanViewModel.kt:134` `MediaStore.createDeleteRequest`，属平台交互） |
| 测试在测不存在的路径导致失效 | 33 个测试的 import **全部可解析**（仅 `BuildConfig`/`R` 为生成类）；问题是**目录与 package 错位**，不是断链 |
| `AdRuntime` 是绕 DI 的静态单例 | `AdRuntime` 是**普通 class**（`AdRuntime.kt:36`，含 `internal constructor`）。真正绕 DI 的是 `AdvertiseSdkAdapter`/`AdvertiseConfigFactory`/`AdPlacementRegistry` |
| 43 个 `object` 都是问题 | 数量对，但**要分级**：有状态/绑 SDK 的约 15 个有问题；`AppConfig`（2 const + 1 纯函数）、`FileSizeFormatter`、`JunkCleanReducer` 无害 |
| `check-brand-residue.ps1` 在检查品牌残留 | 不传参时两个 `foreach` 一次都不执行 → **什么都不检查** |
| 「跨 feature 直接依赖 12 处」（本报告早期口径） | 更正为 **20 处**（`home→settings 5` + `home→toolbox 4` + `onboarding→toolbox 3` + `toolbox→files 3` + `toolbox→settings 2` + 其余 3） |

---

# 10. 待决策清单（阻塞后续实施）

| # | 待决 | 影响 |
|---|---|---|
| 1 | `:junkcc`（1,800 类 / 900 Activity / 25 MB）是否必须保留？ | 决定 P0-5 与 AAB 体积、审核风险 |
| 2 | 是否废止"同步母版"规则（`docs/figma-screen-map.md` Rules 段、`config/variant-source.json`）？ | 否则每次重构都会被"同步 `Quick_Clean_Pro_s`"打回 |
| 3 | 广告是否接受阶段性降级：从"业务链路中段"改为"Intent 拦截 → 展示 → 重派"？ | 短期收入曲线波动需业务侧确认 |
| 4 | `product.json` 里的 4 个密钥已进 git 历史 —— 是否立即轮换（Trustlook / Singular / ThinkingData / remote config）？ | 安全底线 |
| 5 | 是否需要真正多变体（白牌）？若需要 → 改用 flavor + sourceSets；若不需要 → 删 `variant-source.json` 与 `product.schema.json`，并清理 31 个 `ADV_*` BuildConfig 字段 | 决定构建配置改造范围 |
| 6 | 重构节奏：单模块内按包演进（推荐）还是一次性拆多模块？ | 后者在 94 处反向依赖下风险极高 |

---

## 附：本轮排查未做的事

- 未修改任何源码、构建脚本、资源
- 未执行 Gradle 构建（结论基于既有构建产物 `app/build/intermediates/merged_manifests/release/`、`app/release/*.aab` 与静态读取）
- 未运行 `scripts/*.ps1`（本机 ExecutionPolicy 禁止；结论基于脚本静态读取）
- 运行时行为（崩溃率、ANR、广告填充率）无数据佐证，相关结论均为代码路径推理
