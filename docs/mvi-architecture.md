# MVI 规范与垃圾清理试点

本文是当前 MVI 规范；`refactor-sample.md` 为历史设计，不作为实现依据。
本轮只迁移 `core.presentation` 和 `feature.junkclean`，其他 common 包保持原位置，不拆 Gradle module。

## 包职责

源码根包为 `com.aicleanerstorage.spaceclean`。

| 包 | 内容 | 依赖约束 |
| --- | --- | --- |
| core.presentation | MviViewModel、IntentPolicy、IntentInterceptor、InterceptResult | 不依赖 app 或 feature |
| feature.junkclean.presentation | Contract、Reducer、ViewModel、选择展示模型 | 只使用业务接口，不依赖 data、ui 或 app |
| feature.junkclean.ui | Route、EffectHandler、Screen 和内容组件 | 渲染状态、执行平台操作，不依赖 app 或数据实现 |
| feature.junkclean.domain | CleanRepository、CleanSessionStore | 不依赖 Android、UI 或实现层 |
| feature.junkclean.domain.model | 文件、扫描、清理、删除结果和会话数据 | 业务数据；允许现有纯 Kotlin 格式化工具 |
| feature.junkclean.data | CleanRepositoryImpl、scanner、session | 实现扫描、删除和存储，不依赖 presentation、ui 或 app |
| app | DI、导航、权限与广告运行时装配 | 连接接口和实现 |

`CleanItem`、`CategoryCleanGroup` 属于 presentation.model。`CleanItem.isChecked` 不可变，修改选择时复制数据。
仓库接收 `List<JunkFile>`，由 ViewModel 筛选选中项，仓库不认识 UI 选择模型。
`CleanSessionStore` 保存业务结果，不提供第二套页面状态。`SharedScanState` 属于 data.session。
不增加无使用者的 shared 包、转发式 UseCase、通用业务 Job 管理器或 Coordinator。

## 数据流

```text
页面进入 / 用户操作 / 平台回调
    → dispatch(Intent)
    → ViewModel 执行业务任务
    → Action → 纯 Reducer → State
    → Effect → Route / EffectHandler → 平台回调 Intent
```

- ViewModel 保留 `MviViewModel<State, Intent, Effect>` 三个类型参数。基类提供 State 和 Effect，不管理扫描/清理任务。
- junkclean 的所有页面状态修改经过 Reducer；`updateState` 内只有纯函数，不能调用仓库、埋点或发送 Effect。
- Action 是 ViewModel 内部状态转换，UI 不直接派发 Action。
- `phase` 是唯一业务阶段；扫描/清理 Job、删除结果暂存、退出去重属于任务管理，不作为 UI 阶段来源。
- Screen 只接收 State、Intent 回调和展示插槽，不持有 ViewModel、仓库或导航器。
- Route 订阅一份 State 和一个 Effect 流。平台副作用执行后派发 Intent，不能直接写 State。
- junkclean presentation 禁止依赖 `android.*`；允许资源 ID、AndroidX 生命周期和注解。ViewModel 不接收 Context，不创建 PendingIntent，也不解析文案。
- 删除授权 Effect 只携带去重后的 URI 字符串快照；状态保存 `pendingAuthorizationCount`，UI 使用 stringResource 解析等待文案。UI 在页面未退出且处于 AwaitingAuthorization 时创建并启动系统授权，通过 DeleteAuthorizationResult 回传结果。
- 不支持平台、空 URI、授权创建或启动失败均回传 false，沿用用户拒绝的部分失败汇总：保留直接删除成功和内存结果，直接失败与全部待授权文件计入失败，不调用 finalizeAuthorizedDeletes，继续历史记录和完成广告。CancellationException 继续传播。接受回调时同步离开 AwaitingAuthorization 并清零数量，重复或退出后的回调无效。

## 拦截和副作用

权限在 ViewModel 通过 `resolvePolicies(StorageFiles)` 检查。按策略优先级处理，单个策略内跳过 `NotHandled`，首个 `Continue` 通过；`Block / Busy` 立即结束整条链。无人处理时返回 `Block`，不能绕过权限。

完成广告在 UI EffectHandler 的独立子协程执行，使用应用层注入的广告拦截器。完成事件先记录历史和埋点，再等待广告；原 `handleOperation()` 不重复展示完成广告。无广告、启动异常或 30 秒超时继续结果页；外部取消继续传播。退出先取消广告等待，迟到回调无效。Runtime 复用原实例，取消释放请求占用，不保证关闭已显示的 SDK 广告。

结果页推荐区通过 `resultTools: @Composable (Modifier, Boolean) -> Unit` 注入。feature 提供布局 Modifier 和 enabled，app 使用原 ToolFeatureBanners、排除垃圾清理入口并处理导航；禁用期间不执行导航。

Effect 使用 `Channel.UNLIMITED` 同步入队，保持调用顺序；低频导航、授权和操作事件可短暂积压。进度等高频数据必须写入 State。每个 ViewModel 只有一个 UI 消费者，禁止把同一 `receiveAsFlow()` 分给多个收集器。
ViewModel 清理时取消队列、丢弃未消费事件；之后发送不再入队。此契约仅限进程内存活期间，不保证进程恢复或平台操作恰好执行一次。业务退出、授权和广告仍各自保留去重及状态检查。

## 垃圾清理验收基准

1. Idle / CheckingPermission(Scan) 展示扫描动画，但授权前仓库扫描调用为零。
2. Granted 才扫描或删除；Busy 恢复检查前完整快照；拒绝只发一次退出 Effect。扫描拒绝回 Idle，清理拒绝保留 Preview，不展示权限错误页。
3. 返回键始终可见，退出期间忽略重复操作。扫描/清理重复输入不启动并行任务，取消后不发布迟到结果。
4. 直接成功、直接失败、待授权文件分别保存。删除授权拒绝计入失败并汇总结果，不等同于文件权限拒绝。
5. 重复文件删除前安全校验保持原实现；仓库输入转换不能遗漏任何选中文件路径。
6. 只有 CompleteAnimation 接受广告完成 Intent。记录和完成广告各执行一次，退出后不展示结果。
7. 选择变化不改变旧 State 快照，Effect 积压超过原缓冲容量也不能静默丢失。

## 检查和验证状态

运行 `./scripts/check-mvi-boundaries.ps1` 检查试点边界、旧 MVI 引用、必要文件和 package/目录一致性。脚本仅检查声明和显式依赖，不替代 Kotlin 编译或传递依赖分析。

必要定向测试：MviViewModelContractTest、MviEffectContractTest、JunkCleanReducerTest、JunkCleanViewModelTest、JunkCleanRouteEffectsTest、JunkDeleteAuthorizationLauncherTest、PermissionInterceptorTest、PermissionCoordinatorTest、AdInterceptorTest、AdRuntimeRequestTest、AdFeatureFlowTest，以及已有重复文件删除安全测试。

当前交付状态：**代码闭环完成，运行验收待完成**。此前 Gradle `Unable to establish loopback connection` 阻断运行验证，本轮不重复尝试；环境恢复后只运行相关测试及必要 Kotlin 编译，不运行全量测试或 Release 构建。
