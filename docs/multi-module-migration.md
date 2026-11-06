# 多模块与页面 MVI 迁移记录

日期：2026-11-04。代码基线：`ff262a180a798cc29501053e7c29bcec20b1a3ff`，分支 `codex/refactor-v3`。
验收对象为该基线上的未提交工作树，包含此前已存在的权限、通知、广告及安全删除修改。本轮没有提交、回退或清理这些修改。

## 文件目录与职责

```text
app/src/main/{java,kotlin}/…/app/
  di/                         Koin 装配
  navigation/                 类型化 NavHost、导航实现、流程级 ViewModel 所有者
  permission/                 唯一权限拦截器与根 Host
  commercial/                 广告拦截器及唯一 AdDriver 实现
  platform/                   SDK、Android 平台适配
core/
  contracts/src/main/kotlin/…/core/contracts/
  navigation/src/main/kotlin/…/core/navigation/
  presentation/src/main/kotlin/…/core/presentation/
  ui/src/main/{kotlin,res}/
feature/<功能>/src/main/kotlin/…/feature/<功能>/
  domain/
  data/
  view/<页面>/presentation/   Contract、Reducer、ViewModel
  view/<页面>/ui/             Route、Screen、平台 Effect 执行
```

| 模块 | 迁入内容 |
| --- | --- |
| `:core:contracts` | 纯 Kotlin 权限、广告、FeatureFlow、埋点契约及跨功能业务数据 |
| `:core:navigation` | 可序列化 AppDestination、MainTab、AppNavigator、历史入口解析 |
| `:core:presentation` | AndroidX MviViewModel、IntentInterceptor、InterceptResult |
| `:core:ui` | Theme、基础组件、共享资源、UI 权限观察 |
| `:feature:startup` | Splash |
| `:feature:onboarding` | 引导扫描 |
| `:feature:home` | 首页三个 Tab、概览与退出提示 |
| `:feature:settings` | 设置、权限管理 |
| `:feature:junkclean` | 垃圾清理、扫描、安全删除、扫描会话存储 |
| `:feature:antivirus` | 病毒首页、快扫、深扫、结果、SDK 数据适配 |
| `:feature:files` | 照片、相似照片、截图、视频、音频、大文件、文档、隐私照片、重复文件 |
| `:feature:applock` | 应用锁及相关数据实现 |
| `:feature:notificationcleaner` | 通知清理及其数据实现 |
| `:feature:toolbox` | 设备、电池、应用使用、网络扫描/设备/用量/测速、WhatsApp |

app 依赖全部业务模块，feature 只依赖 core，不存在 feature → app 或跨 feature 依赖。原 `:junkcc` 保持由 app 装配。
原有资源集中迁入 `core:ui`，引用改为 `core.ui.R`；产品配置生成的 app 资源仍属于 app。没有复制各 feature 的既有图片或更换视觉资源。
原 common 生产源码和旧字符串导航 API 已移除，不保留旧包别名。测试同步迁移到实际模块；需要真实应用权限/广告实现的集成单元测试仍由 app 持有。测试源码中的旧 `common` 包也已移除：权限、冷启动广告和文件安全契约测试归入 `core:contracts`；Analytics 与 CleanupHistory 测试归入 app 的实际 `platform` 包。toolbox 的 Battery 与 DeviceInfo 共用模型、格式化函数和状态控制器已归入 `view/shared/presentation`。

## 核心实现

- 所有功能 ViewModel 继承 `MviViewModel<State, Intent, Effect>`，页面调用 `dispatch(Intent)`。公共输入和内部结果使用同一个 Intent 类型，状态经纯 Reducer 写入，Contract/Reducer 与任务代码拆开。部分内部结果仍采用完整不可变快照，未新增通用任务管理器。
- 基类使用单 StateFlow、单消费者 `Channel.UNLIMITED` Effect 队列；同步入队保序，清理时取消队列并丢弃积压。存储仓库中的业务 Flow 不是第二份页面状态；病毒扫描的内部 Channel 仅用于任务展示节奏。
- 业务扫描、删除及进度任务继续由各 ViewModel 管理；垃圾清理保留权限前动画、Busy 快照恢复、拒绝退出一次、完成广告超时及安全删除汇总。
- `AppDestination` 使用类型化参数。序列化只保存业务参数，广告场景、feature 和历史路由别名为计算属性，避免反序列化丢失入口元数据。
- `NavHostAppNavigator` 实现入口广告、进行中请求去重、来源 entry 校验和取消。相同请求不会重复导航，离开来源后迟到广告回调无效。
- 重置到 `Main(Files/Toolbox)` 直接使用目标 Tab 建立首页；恢复已有首页通过 Intent 选择 Tab。显式请求不会被稍后到达的 Entered 覆盖。
- 七组文件列表/详情分别使用图级 ViewModel；病毒各页使用同一 `AntiVirusFlow` ViewModel。app 的 `FeatureFlowOwners` 挂载唯一流程级 Effect 消费者，切换流程内页面不会丢失完成事件。
- 权限和广告实现继续留在 app。广告仅有一个 `AdDriver`，未新增 Coordinator 或 Runtime。

## 正式验证

当前代码侧收口状态：多模块结构、Screen 导航边界、类型化目标映射和导航请求去重测试已完成代码侧收口；本轮不执行安装、ADB、AndroidTest 或真机操作，设备验收由用户单独完成。

使用已有本机 Android Studio JBR，配置仅作用于当前进程：

```powershell
$env:JAVA_HOME='C:/Program Files/Android/Android Studio/jbr'
$env:JAVA_TOOL_OPTIONS='-Djdk.net.unixdomain.tmpdir=D:/tmp/cleaner-uds'
```

没有重做 loopback 诊断、清空缓存或改动系统 JDK。本轮实际使用 Gradle 执行，不使用缓存编译/JUnit 手动执行替代正式结果。

| 范围 | 实际结果 |
| --- | --- |
| `:core:contracts:test` | 7 个测试通过，包含迁入的权限、冷启动广告和文件安全契约测试 |
| `:core:navigation:test` | 修复类型序列化元数据后，7 个测试通过 |
| `:core:presentation:testDebugUnitTest` | 5 个测试通过，包含积压 Effect 顺序、清理和策略中断 |
| `:core:ui:testDebugUnitTest` | 6 个测试通过 |
| `:feature:junkclean:testDebugUnitTest` | 21 个测试通过：Reducer、授权 Launcher、删除安全 |
| `:feature:files:testDebugUnitTest` | 22 个测试通过，包含列表/详情选择共享和权限退出去重 |
| `:feature:startup:testDebugUnitTest` | 9 个测试通过 |
| `:feature:toolbox:testDebugUnitTest` | 19 个测试通过 |
| `:feature:home:testDebugUnitTest` | 1 个新增回归通过，覆盖 Tab 请求与 Entered 的两种到达顺序 |
| app 限定测试 | 一轮 112 项中 111 项通过，1 项为架构测试自身读取无 package 文件的空指针；修复后架构和导航共 9 项复测全部通过 |
| `:app:compileDebugKotlin` | 最终代码编译通过 |
| `:app:assembleDebug` | 最终执行一次，`BUILD SUCCESSFUL`，32 秒 |

app 限定选择器：

```text
MotherTemplateBoundaryTest
PermissionInterceptorTest
NotificationPermission*Test
AdInterceptorTest
AdQueueTest
AdvertiseAdDriverTest
ColdStartAdsTest
AppFeatureFlowTest
JunkCleanViewModelTest
JunkCleanRouteEffectsTest
NavHostAppNavigatorTest
RouteEntryAdPolicyTest
AppLaunchCoordinatorTest
AppNotificationTargetRoutingTest
```

对应批次命令：

```powershell
./gradlew.bat :core:contracts:test :core:ui:testDebugUnitTest `
  :feature:files:testDebugUnitTest :feature:startup:testDebugUnitTest `
  :feature:toolbox:testDebugUnitTest :app:testDebugUnitTest `
  --tests '*MotherTemplateBoundaryTest' --tests '*PermissionInterceptorTest' `
  --tests '*NotificationPermission*Test' --tests '*AdInterceptorTest' `
  --tests '*AdQueueTest' --tests '*AdvertiseAdDriverTest' --tests '*ColdStartAdsTest' `
  --tests '*AppFeatureFlowTest' --tests '*JunkCleanViewModelTest' `
  --tests '*JunkCleanRouteEffectsTest' --tests '*NavHostAppNavigatorTest' `
  --tests '*RouteEntryAdPolicyTest' --tests '*AppLaunchCoordinatorTest' `
  --tests '*AppNotificationTargetRoutingTest' :app:compileDebugKotlin --continue --console=plain

./gradlew.bat :feature:home:testDebugUnitTest :app:testDebugUnitTest `
  --tests '*MotherTemplateBoundaryTest' --tests '*NavHostAppNavigatorTest' `
  :app:compileDebugKotlin --continue --console=plain

./gradlew.bat :app:assembleDebug --console=plain
```

首次限定批次及修复后的日志均保留，避免将失败的整个批次写成成功。最终仅复测受影响的测试，没有执行全量测试或 Release 构建。
日志和各模块最近一次 XML 报告汇总见 [验证证据目录](verification/multi-module-2026-11-04/)。各报告来自不同定向批次，不表示一次全量测试。

### 静态检查

- `check-mvi-boundaries.ps1`：通过，检查 15 个迁移相关模块中的 521 个 Kotlin 文件，并检查测试包不再使用 `common`。
- `check-permission-ad-boundaries.ps1`：通过，检查 471 个生产 Kotlin 文件。
- `check-dependencies.ps1`：通过，改为检查实际模块目录和 SDK 适配边界。
- `check-variant-boundaries.ps1`：通过，移除只扫描不存在 use 包目录的空转检查，复用实际边界检查。
- `check-template.ps1`、`check-product-config.ps1` 默认检查当前 Storage Clean 产品；传入 `-WhitePackage` 才启用白包约束。默认模式通过，白包模式仍按预期因现有 Firebase 配置失败，未删除该配置。

本轮收口批次日志：`docs/verification/multi-module-2026-11-05/`。其中包含边界检查、定向测试和 Debug 打包日志；没有执行 Release、全量测试或设备操作。

### 构建产物

`app/build/outputs/apk/debug/app-debug.apk`

- applicationId：`com.aicleanerstorage.spaceclean`
- versionName / versionCode：`1.0.7 / 8`
- 大小：170,954,799 字节
- SHA-256：`ef01a9687c3b60f69b0c7c6dddddcbc33a3a0bcb182ff77d875075c010186f6f`

## 未覆盖项

本轮未安装 APK、未执行 Android 仪器测试或真机交互验收。类型化导航的真实返回栈、配置变更和进程重建，以及多页面流程在真实广告/系统授权界面中的连续交互仍需设备验证。
之前的权限/广告设备验收不替代本轮模块与导航变化的验收。没有访问或删除设备上的用户文件。

结论：多模块迁移、页面 MVI 接入、定向验证与 Debug 打包完成；设备回归尚未执行。架构规范见 [mvi-architecture.md](mvi-architecture.md)。
