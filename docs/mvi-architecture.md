# 多模块 MVI 架构规范

本文是当前 MVI 规范。权限和广告采用 `refactor-sample.md` 第 3、4 节的职责划分；与示例不同的业务和生命周期规则以本文为准。
当前采用 4 个 core 模块与 10 个 feature 模块。所有业务页面 ViewModel 使用 MviViewModel；app 统一装配 Koin、类型化 NavHost、权限 Host 与广告实现。旧 common 生产源码已迁入对应模块，不保留兼容别名。

## 包职责

源码根包为 `com.aicleanerstorage.spaceclean`。

| 模块 | 内容 | 依赖约束 |
| --- | --- | --- |
| core:contracts | IntentPolicy、权限、广告、业务事件和共享业务数据接口 | Kotlin/JVM，不依赖 Android、app 或 feature |
| core:navigation | AppDestination、MainTab、AppNavigator | Kotlin/JVM，只依赖 core:contracts |
| core:presentation | MviViewModel、IntentInterceptor、InterceptResult | AndroidX 生命周期与协程；不依赖 app 或 feature |
| core:ui | Theme、共享资源、通用 Compose 组件、权限观察 | 只依赖 core 契约 |
| feature:* | domain、data、view/<page>/presentation、view/<page>/ui | 只依赖 core，不依赖 app 或其他 feature |
| app | Application、Koin、NavHost、PermissionInterceptor/Host、AdInterceptor/Driver、FeatureFlow 实现和平台服务 | 连接接口与实现；保留原有 junkcc 装配 |

模块清单：startup、onboarding、home、settings、junkclean、antivirus、files、applock、notificationcleaner、toolbox。
业务接口和跨功能复用的数据放入 core:contracts；单功能仓库接口仍留在自身 domain。不得为了减少依赖报错让 feature 依赖另一个 feature。

`CleanItem`、`CategoryCleanGroup` 属于 presentation.model。`CleanItem.isChecked` 不可变，修改选择时复制数据。
仓库接收 `List<JunkFile>`，由 ViewModel 筛选选中项，仓库不认识 UI 选择模型。
`CleanSessionStore` 保存业务结果，不提供第二套页面状态。`SharedScanState` 属于 data.session。
不增加无使用者的 shared 包、转发式 UseCase、通用业务 Job 管理器或 Coordinator。

## 数据流

```text
页面进入 / 用户操作 / 平台回调
    → dispatch(Intent)
    → ViewModel 执行业务任务
    → 内部 Intent → 纯 Reducer → State
    → Effect → Route / EffectHandler → 平台回调 Intent
```

- ViewModel 保留 `MviViewModel<State, Intent, Effect>` 三个类型参数。基类提供 State 和 Effect，不管理扫描/清理任务。
- junkclean 的所有页面状态修改经过 Reducer；`updateState` 内只有纯函数，不能调用仓库、埋点或发送 Effect。
- 页面输入和任务结果使用同一个 Intent 类型；内部结果通过 dispatchInternal 进入。内部结果类型可标为 internal，不再保留 Action 双通道。
- `phase` 是唯一业务阶段；扫描/清理 Job、删除结果暂存、退出去重属于任务管理，不作为 UI 阶段来源。
- Screen 只接收 State、Intent 回调和展示插槽，不持有 ViewModel、仓库或导航器。
- Route 订阅一份 State 和一个 Effect 流。平台副作用执行后派发 Intent，不能直接写 State。
- junkclean presentation 禁止依赖 `android.*`；允许资源 ID、AndroidX 生命周期和注解。ViewModel 不接收 Context，不创建 PendingIntent，也不解析文案。
- 删除授权 Effect 只携带去重后的 URI 字符串快照；状态保存 `pendingAuthorizationCount`，UI 使用 stringResource 解析等待文案。UI 在页面未退出且处于 AwaitingAuthorization 时创建并启动系统授权，通过 DeleteAuthorizationResult 回传结果。
- 不支持平台、空 URI、授权创建或启动失败均回传 false，沿用用户拒绝的部分失败汇总：保留直接删除成功和内存结果，直接失败与全部待授权文件计入失败，不调用 finalizeAuthorizedDeletes，继续历史记录和完成广告。CancellationException 继续传播。接受回调时同步离开 AwaitingAuthorization 并清零数量，重复或退出后的回调无效。

## 类型化导航与共享流程

纯 Screen 和 UI 组件只接收 `ToolNavigationTarget` 或无参数语义回调。它们不导入 `AppDestination`、`AppNavigator`、`NavHostController`、ViewModel 或 Repository；Route/EffectHandler 才把业务目标映射为 typed `AppDestination` 并执行导航。

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

## 权限与广告的唯一执行链路

```text
业务 / resolvePolicies → PermissionInterceptor（也实现 PermissionGateway）
                                  → 根 AppPermissionHost → Android
完成 Effect / FeatureFlow / 路由 → AdInterceptor.show → AdvertiseAdDriver → SDK
```

### 权限

- Koin 只创建一个 PermissionInterceptor，PermissionGateway 直接绑定这个实例。业务通过纯数据 PermissionNeed 或 PermissionTarget 请求权限，不依赖 app 实现。
- 拦截器拥有一份不可变 Prompt 快照和私有 Request/CompletableDeferred。阶段与当前权限只保存在 Prompt 中，Request 只保存等待结果及启动、取消等元数据。阶段为 AwaitingUser、LaunchRequested、WaitingRuntime、WaitingSettings；Busy 不替换当前请求。
- PermissionPlatform 集中真实授权判断、运行时权限列表、设置页候选 Intent、Activity rationale 和 Android 版本分支。偏好键继续复用原存储。
- ProtectedAction 的组合权限按原顺序申请。每步按真实授权复查；最终先清理占用，再恢复等待者。Host 回调携带 Prompt，由拦截器校验请求对象、当前权限步骤及预期阶段；Host 不另存运行时权限或设置页请求归属。
- 启动前取消立即清除提示；系统页面已经启动时取消只停止业务等待，保留占用直到系统返回，静默释放。Host 销毁取消等待和状态。取消不转换成 Denied，不发旧页面退出广告；设置页没有绝对超时。
- 仅根 AppPermissionHost 注册权限和设置 Launcher；启动前同步标记等待阶段。设置页的 ActivityResult 与暂停/恢复事件共同触发复查，重复返回不重复完成。
- PostNotifications 与 NotificationListener 是不同权限。通知提示策略保留首页 350ms 延迟、每日频率、会话延后、启动页暂停；系统执行复用同一个 Host。Busy 等空闲后按当前页面重新评估，页面切换取消等待。普通业务请求会隐藏通知专用提示，避免两套弹窗叠加。
- 通知策略只读取和更新一份 StateFlow。首页提示通过 repeatOnLifecycle(RESUMED) 统一计时：进入已恢复的首页或再次恢复后等待 350ms，暂停、离开首页或 Host 销毁时取消；不并行设置页面进入计时与恢复计时。
- 申请记录仅在真实启动系统权限时写入，提示时间仅在实际展示时写入。启动失败、Busy、取消不记作用户拒绝。通知申请不自动打开设置，只有确认专用提示后才打开。

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

`./scripts/check-mvi-boundaries.ps1` 扫描全部 core/feature/app 模块的源码和测试目录，检查模块方向、domain/presentation/data 边界、Screen 限制、旧 common 引用和 package/目录一致性。
`./scripts/check-permission-ad-boundaries.ps1` 扫描所有模块，检查唯一权限 Host、唯一 AdDriver、Gateway 绑定和旧框架残留。

各模块执行受影响的定向 JVM 测试；权限、广告及跨模块 junkclean 集成测试仍由 app 测试源集持有，避免 feature 测试反向依赖 app。不执行全量测试或 Release 构建。
本轮命令、结果、文件迁移说明和未覆盖项见 [多模块迁移记录](multi-module-migration.md)。此前权限/广告设备验收保留在 [历史验收记录](permission-ad-acceptance.md)，不作为本轮多模块变更的设备验收结果。
