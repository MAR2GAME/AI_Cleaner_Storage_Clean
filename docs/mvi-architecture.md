# MVI 与分层架构规范

本文是 MVI 与页面数据流规范。硬规则、优先级、代码风格与例外登记以 [AGENTS.md](../AGENTS.md) 为准；本文只保留数据流、页面结构和权限执行链路等细节。权限职责划分参考 [历史样例](history/refactor-sample.md) 第 3 节；与示例不同的业务和生命周期规则以本文为准。
当前为单 `:app` 模块 + 包分层，另有 `:junkcc` 资源模块；原 `cleaner-kit`、`device-kit`、`network-kit` 已全部并入 `data`，不再有独立 kit 包。所有业务页面 ViewModel 使用 MviViewModel；app 统一装配 Koin、类型化 NavHost 与权限 Gate。旧 feature 模块已合并回 `:app`，不保留兼容别名。

> 历史说明：当前仓库没有广告实现。文中提到的广告、AdInterceptor、AdvertiseAdDriver、冷启动/插屏链路仅保留历史参考，不作为现状。

## 包职责

源码根包为 `com.aicleanerstorage.spaceclean`。

| 包 | 内容 | 依赖约束 |
| --- | --- | --- |
| app | Application、MainActivity、root/、di/、view/<page>/{interaction,ui}、background/ | 唯一装配 data 实现与 kit 端口实现的包 |
| base | designsystem/、mvi/、navigation/、permission/、util/ | 技术支撑；不得依赖 data 或 app |
| data | `repository/` 端口实现、`local/` 平台适配与删除授权 | 允许 domain、base；不得依赖 app |
| domain | `model/<concept>/`、`port/`、`rule/`、`error/` | 纯 Kotlin；只依赖自身与 Kotlin/JDK |

**层优先（layer-first）**：`domain` 按职责分为 `model / port / rule / error`，`data` 分为 `repository / local`；同类文件集中，业务概念用 `model/<concept>` 与 `local/<capability>` 表达。跨 feature 的契约放 `domain/port`（例如 `AppSettingsRepository`），不建 `shared` 包。真出现跨多个 repository 的编排时再建 `domain/usecase/`。

依赖方向（允许列表，由 `scripts/check-rules.ps1` 逐条检查）：

- `domain` 只依赖自身与 Kotlin/JDK；不得 import `base`、`data`、`app`。
- `data` 允许 `domain`、`base`；不得 import `app`。
- `app/view` 允许 `base`、`domain`；不得 import `data`。
- `app/di` 是唯一装配 data 实现的包。
- `base` 不得依赖 `data` 或 `app`。

`CleanItem`、`CategoryCleanGroup` 属于 presentation 模型，平铺在 `junkclean/interaction/`。`CleanItem.isChecked` 不可变，修改选择时复制数据。
仓库接收 `List<JunkFile>`，由 ViewModel 筛选选中项，仓库不认识 UI 选择模型。
`JunkScanSessionStore` 保存业务结果，不提供第二套页面状态；它属于 `data/local/junkclean`。
不增加无使用者的 shared 包、转发式 UseCase、通用业务 Job 管理器或 Coordinator。

领域层顶层业务数据类统一使用 `Model` 后缀，每个公开类型放在同名 Kotlin 文件中。枚举、值类及密封类型的嵌套分支保留业务名称。

## 数据流

```text
页面进入 / 用户操作 / 平台回调
    → dispatch(Action)
    → ViewModel 执行业务任务
    → 内部 Action → 纯 Reducer → State
    → Event → Route / EffectHandler → 平台回调 Action
```

- ViewModel 保留 `MviViewModel<State, Action, Event>` 三个类型参数。基类提供 State 和 Event，不管理扫描/清理任务。
- junkclean 的所有页面状态修改经过 Reducer；`setState` 内只有纯函数，不能调用仓库、埋点或发送 Effect。
- 页面输入与异步结果使用同一个 `XxxAction`，通过 `dispatch` 上报。Action 按已发生的事实命名，例如 `TermsClicked`、`DeleteConfirmed`、`ScanCompleted`。ViewModel 下发的 `XxxEvent` 使用祈使式，例如 `OpenTerms`、`NavigateBack`。
- `phase` 是唯一业务阶段；扫描/清理 Job、删除结果暂存、退出去重属于任务管理，不作为 UI 阶段来源。
- 页面入口放在 `app/view/<page>/ui/XxxScreen.kt`，按状态组装展示组件，通过回调上报交互。单页专用组件放在同页 `ui/part/`，文件名与主 Composable 一致，用 `Header`、`Row`、`Card`、`Dialog`、`Content` 等后缀表达组件职责。跨页业务组件放在 `app/view/common/`，预览放在 `ui/preview/`。
- Route 订阅一份 State 和一个 Event 流。平台副作用执行后派发 Action，不能直接写 State。
- Event 统一用 `CollectEffects`（`base/mvi/CollectEffects.kt`）收集：只在 `RESUMED` 时消费，页面不可见期间事件留在通道里，恢复后再执行。禁止在 Route 里手写 `LaunchedEffect { viewModel.event.collect { } }`，否则后台仍会执行导航、Toast、finish。
- junkclean presentation 禁止依赖 `android.*`；允许资源 ID、AndroidX 生命周期和注解。ViewModel 不接收 Context，不创建 PendingIntent，也不解析文案。
- 用户可见文案走 `UiText`（`base/mvi/UiText.kt`）：状态只保存 `error: UiText?` 这类值，UI 层用 `resolve(context)` 解析。错误到文案的映射收口在页面 `interaction/XxxErrorText.kt` 的 `internal fun XxxError.toUiText(): UiText`，`when` 必须穷尽分支；禁止在状态里塞 `String` 或异常 `message`。
- 系统返回键统一 `BackHandler { onAction/dispatch(XxxAction.BackClicked) }`，由 Route 消费 Event 再 `back()`；不在 Route/Screen 里直接调 `navigator.back()` 或 `finish()`。首页用 `enabled` 让弹窗期间不拦截；Splash/Onboarding 可以用 `BackHandler {}` 显式吞掉返回。
- `XxxScreen` 只接收 `state` + `onAction`（+ `modifier`）；需要渲染插槽时用 `@Composable (...) -> Unit` 内容槽（组合而非回调）。`permissionGranted`、`interactionsEnabled` 这类门控必须并进 `XxxUiState`，由 Route 用 `copy(...)` 注入。
- 页面副作用宿主统一放 `app/view/<page>/ui/effect/`：只 dispatch Action、计时或调用平台 API，不渲染界面。命名三类：`XxxEffectHandler.kt`（Composable 事件宿主）、`XxxEffect.kt` / `XxxEffects.kt`（进度、计时或一组小副作用）、`CollectXxxEvents.kt`（suspend 事件收集器）。`ui/` 根目录放 Screen、Route 与页面级渲染辅助（`*View`、`*Tokens`、`*Popups` 等）；纯展示组件与对话框优先放 `ui/part/`，`@Preview` 放 `ui/preview/`。
- 删除授权 Effect 只携带去重后的 URI 字符串快照；状态保存 `pendingAuthorizationCount`，UI 使用 stringResource 解析等待文案。UI 在页面未退出且处于 AwaitingAuthorization 时创建并启动系统授权，通过 DeleteAuthorizationResultReceived 回传结果。
- 不支持平台、空 URI、授权创建或启动失败均回传 false，沿用用户拒绝的部分失败汇总：保留直接删除成功和内存结果，直接失败与全部待授权文件计入失败，不调用 finalizeAuthorizedDeletes，继续历史记录和完成广告。CancellationException 继续传播。接受回调时同步离开 AwaitingAuthorization 并清零数量，重复或退出后的回调无效。

## 类型化导航与共享流程

Screen 和 UI 组件通过回调传递 `AppDestination` 或发出无参数语义事件。Route/EffectHandler 持有导航器并执行跳转。

AppNavigator 是纯 Kotlin 接口。NavHostAppNavigator 位于 app，合并入口广告、进行中请求去重、来源页面校验与迟到回调取消。功能页面通过 typed AppDestination 输出目标；只有应用层持有 NavHostController。
Main(initialTab) 表示三个首页标签；切换现有 Main 时通过 SavedStateHandle 请求更新 Home Intent。序列化参数只包含业务参数，广告场景、feature 标识和历史路由别名使用计算属性，避免反序列化时回退为默认元数据。
历史通知别名在边界转换为类型目标；详情索引支持原路径和 initialIndex 查询参数输入，内部导航不拼接字符串。

文件列表和详情各自属于对应的 typed graph，Koin 从 graph entry 获取同一个 ViewModel；不再在找不到列表 entry 时创建备用实例。FeatureFlowOwners 在当前图存活期间挂载唯一 FileManagerEffectHandler，列表离开组合后仍能消费删除完成事件。详情页使用同一个 FileManagerIntent。
病毒首页、快扫、深扫和结果属于 AntiVirusFlow，使用一个 VirusScanViewModel 和 VirusEffectHandler。扫描完成导航由 Effect 发出；结果页平台操作也通过 Intent/Effect 往返。结果状态不因扫描页正常离开而取消。

## 拦截和副作用

权限在 ViewModel 通过 `resolvePolicies(StorageFiles)` 检查。按策略优先级处理，单个策略内跳过 `NotHandled`，首个 `Continue` 通过；`Block / Busy` 立即结束整条链。无人处理时返回 `Block`，不能绕过权限。

完成广告在 UI EffectHandler 的独立子协程执行，使用应用层注入的广告拦截器。完成事件先记录历史和埋点，再等待广告；原 `handleOperation()` 不重复展示完成广告。无广告、启动异常或 30 秒超时继续结果页；外部取消继续传播。退出先取消广告等待，迟到回调无效。应用根节点装配同一个 AdInterceptor 和 AdvertiseAdDriver；取消释放请求占用，不保证关闭已显示的 SDK 广告。

结果页推荐区通过 `resultTools: @Composable (Modifier, Boolean) -> Unit` 注入。feature 提供布局 Modifier 和 enabled，app 使用原 ToolFeatureBanners、排除垃圾清理入口并处理导航；禁用期间不执行导航。

Effect 使用 `Channel.UNLIMITED` 同步入队，保持调用顺序；低频导航、授权和操作事件可短暂积压。进度等高频数据必须写入 State。每个 ViewModel 只有一个 UI 消费者，禁止把同一 `receiveAsFlow()` 分给多个收集器。
ViewModel 清理时取消队列、丢弃未消费事件；之后发送不再入队。此契约仅限进程内存活期间，不保证进程恢复或平台操作恰好执行一次。业务退出、授权和广告仍各自保留去重及状态检查。

## 权限与广告的执行链路

```text
页面 Route → PermissionRequestEffect → PermissionGate（系统弹窗 / 设置页）
                    ↓
             PermissionResult → 页面 / ViewModel 判断
```

### 权限

- `AppPermission` 只放平台事实：`isGranted` / `runtimePermissions` / `settingsIntents` / `requiresSettings` / `shouldExplain` / `resultAfterSettings`。
- `PermissionRequestEffect(request, onResult)` 是唯一的申请副作用：运行时权限拉系统弹窗，特殊权限跳设置页并在 `ON_RESUME` 重查。
- 页面持有 `pendingPermission`，结果通过 `onResult` 回传；是否先解释、是否提示去设置、弹窗长什么样，都由页面决定。
- 文案和弹窗在视图层：`app/view/common/permission/` 下的 `PermissionUiCopy` 与 `ui/*Dialog`，base 不依赖 `R`。
- ViewModel 不注入 `PermissionChecker`、不查权限；通过 Action 接收 `PermissionResult`。
- 通知权限保留每天一次节流；`PostNotifications.isGranted` 使用 `areNotificationsEnabled()`。
- `PermissionGate` 只保留 `request / openSettings / isGranted`；`allow` 已删除。

### 普通插屏、冷启动和外部返回

- 普通插屏只有 AdInterceptor.show 一个入口。Mutex 串行执行；request id 从排队到执行都去重，重复请求立即返回且不影响旧请求。
- 30 秒覆盖排队和展示等待，拿到执行权后复查 Activity、权限、业务忙碌和外部返回抑制；保留 PermissionRejected 广告的权限/外部返回例外。
- 无 Activity、无广告、启动异常和自身超时正常返回；外部 CancellationException 继续传播。finally 释放自己的 id、执行权和忙碌标记。
- AdvertiseAdDriver 只适配 SDK 展示、预加载和关闭回调；重复、同步及取消后的迟到回调不能重复恢复业务。取消不强制关闭 SDK 广告。
- 广告能力只保留一个 AdDriver 接口，由 AdvertiseAdDriver 实现；普通插屏、冷启动和外部返回保护复用该接口，不再拆出 AppOpenAdDriver 等接口。
- 冷启动由 AdDriver.showColdStart 顶层挂起函数执行：consent 初始化和展示各保留 6500ms 等待、预加载、开屏加载通知、6500ms 未加载兜底、加载后关闭的 800ms 间隔，以及 Host watchdog。finally 恢复 app-open 开关。
- ExternalAppOpenGuard 独立处理外部页面返回后的 1200ms 抑制，导航策略留在 app.navigation.RouteEntryAdPolicy。SDK 初始化、通知服务、隐私、Banner/Native/Rewarded 保持原用途。
- AppFeatureFlow 是唯一业务记录实现。recordOperation 只写忙碌状态、历史、埋点；handleOperation 先记录再等待广告；exit 先记录退出再等待。业务导航在挂起调用返回且协程有效后执行。
- Effect 收集器启动子任务等待广告，退出先取消完成广告任务。页面和导航保留去重，不能把拦截器的重复请求“立即返回”当成第二次导航许可。
- 同一 Route 的多个退出入口可收敛为本地 exitOnce 函数，统一去重、取消页面任务和等待退出广告，再执行对应导航；不为此引入通用页面控制器。

示例采用的是“拦截器直接持有请求 / 直接调用 Driver”。不采用示例中无人处理策略放行、权限拒绝错误页和简单取消即释放系统请求的行为。独立权限 Coordinator/Engine/Handler、广告 Runtime/Executor/Runner 及 callback 兼容接口已移除。

## 垃圾清理验收基准

1. Idle / CheckingPermission(Scan) 展示扫描动画，但授权前仓库扫描调用为零。
2. Granted 才扫描或删除；Busy 恢复检查前完整快照；拒绝只发一次退出 Effect。扫描拒绝回 Idle，清理拒绝保留 Preview，不展示权限错误页。
3. 返回键始终可见，退出期间忽略重复操作。扫描/清理重复输入不启动并行任务，取消后不发布迟到结果。
4. 直接成功、直接失败、待授权文件分别保存。删除授权拒绝计入失败并汇总结果，不等同于文件权限拒绝。
5. 重复文件删除前安全校验保持原实现；仓库输入转换不能遗漏任何选中文件路径。
6. 只有 CompleteAnimation 接受广告完成 Intent。记录和完成广告各执行一次，退出后不展示结果。
7. 选择变化不改变旧 State 快照，Effect 积压超过原缓冲容量也不能静默丢失。

## 检查和验证状态

`scripts/check-rules.ps1` + `profiles/android-compose-mvi.json` 是本仓库 MVI 规则的唯一门禁，覆盖：domain 纯净、分层方向、data 可见性/契约、view 不 import data、view 不依赖 root、`ui/part` 无状态、错误走 `UiText`、事件走 `CollectEffects`、返回键 dispatch、package/目录一致、语言包 key 对齐。

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts\check-rules.ps1
```

改动上述区域后必须运行；单测用 `.\gradlew.bat :app:testDebugUnitTest`，构建验证用 `.\gradlew.bat :app:assembleDebug`。CI（`.github/workflows/ci.yml`）把这三步串在一起，本地等价命令是 `scripts\ci.ps1`，详见 [CI](ci.md)。
旧脚本 `check-mvi-boundaries.ps1` / `check-permission-ad-boundaries.ps1` 已不再维护，历史记录见 [多模块迁移记录](history/multi-module-migration.md)。
