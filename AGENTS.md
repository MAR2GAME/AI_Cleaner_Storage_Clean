# 项目规则

本文件是硬规则、优先级与代码风格的唯一入口。README / `docs/mvi-architecture.md` 只保留产品说明、数据流细节和现状。

## 规则入口与 Profile

- `AGENTS.md` 是规则唯一来源；`profiles/android-compose-mvi.json` 只放项目差异：`sourceRoot`、`packageRoot`、页面、目的地、资源前缀、`navHost` 和 `checks` 开关。
- 优先级：用户当前明确指令 > 本文件与当前 profile > Android / Compose 平台规则 > 通用工程规则 > 工具默认行为。
- 代码与规范不一致时默认代码是错的，除非已在 `docs/rules/exceptions.md` 登记。
- 检查脚本与规范不一致时，在当次变更里同时改脚本或规则，不允许长期漂移。
- 规则冲突、规则未覆盖、需要例外：先在 `docs/rules/exceptions.md` 登记，再改代码。

## 定位

Android 单 Activity + Compose + MVI + Koin + 类型化导航。当前只有 `:app` 和 `:junkcc` 两个 Gradle 模块；原 `cleanerkit`、`devicekit`、`networkkit` 已全部并入 `data`，不再有独立 kit 包。所有页面 ViewModel 使用 `MviViewModel`，app 统一装配 Koin、类型化 NavHost 与权限 Gate。

## 包结构

```text
app/src/main/java/com/aicleanerstorage/
├─ spaceclean/
│  ├─ app/        组合根：Application、MainActivity、root/、di/、background/、view/<page>/
│  ├─ base/       designsystem/、mvi/、navigation/、permission/、util/
│  ├─ data/       repository/ 仓库实现、local/ 平台适配与删除授权
│  ├─ domain/     model/<concept>/、port/、rule/、error/
│  └─ app/view/<page>/{interaction,ui}
```

**层优先（layer-first）**：`domain` 按职责分为 `model / port / rule / error`，同类文件集中；`data` 分为 `repository / local`，仓库实现与平台适配分开。业务概念通过 `model/<concept>` 与 `local/<capability>` 表达，不再给每个 feature 复制一套 `model/port/error` 子包。真出现跨多个 repository 的编排时再建 `domain/usecase/`。

依赖方向（允许列表，由 `scripts/check-rules.ps1` 逐条检查）：

- `domain` 只依赖自身 + Kotlin/JDK（coroutines 例外见 `docs/rules/exceptions.md`）；不得 import `base`、`data`、`app`。
- `data` 允许 `domain`、`base`；不得 import `app`。
- `app/view` 允许 `base`、`domain`；不得 import `data`。
- `app/root` 允许 `app/view`、`base`、`domain`；不得 import `data`。
- `app/di` 是唯一装配 data 实现的包。
- `base` 不得依赖 `data` 或 `app`。

目录名 = 包名，全小写；空目录不建。

## domain

- 结构：`domain/model/<concept>/`、`domain/port/`、`domain/rule/`、`domain/error/`；模型按概念分组，端口/规则/错误平铺。真出现跨仓库编排时再建 `domain/usecase/`。
- 顶层业务数据类统一 `<Concept>Model`，文件名与类型名一致；枚举保留 `Type`、`Mode`、`Sort` 等语义名称。
- 写操作返回 `kotlin.Result`；读操作直接返回；失败语义写进 KDoc。`suspend` 只表达“可能耗时”，不绑定线程。
- 端口按业务能力划分，不按介质/实现细节拆分；端口名用业务动作或业务名词，不用 `MediaStore`、`SAF` 这类介质名。
- 错误定义在 `domain/error/`；每个 `data object` / `data class` 分支必须有 KDoc，写清“何时发生、调用方怎么处理”。
- 对外提供能力的类型必须有类级 KDoc；公开方法写作用、`@param`、`@return`。
- 禁止 `android.*`、`androidx.*`、`Context`、`Uri`、`ContentResolver`、`Compose`、`Dispatchers`、资源 id、SQL 字符串、日志实现。

## data

- 只做实现：`repository/` 放端口实现，`local/` 放 MediaStore/SAF/系统服务/文件系统等平台适配；不定义业务规则。
- 具体实现类默认 `internal`；Android 组件（`Service` / `Receiver` / `Provider` / `Activity`）例外并登记。
- 命名按角色：聚合仓库 `<概念>Repository`；单介质实现 `<概念><介质>Repository`；端口名被占用时用 `Default<概念>Repository` 或 `<端口名>Impl`；平台机制用机制前缀。
- 异常在实现层收口成 `Result`；`CancellationException` 必须继续传播。
- 直接返回 domain 模型；只有出现 Entity/DTO 时才建 `mapper/`，当前不建。
- `data` 的平台实现必须直接实现 domain 端口；不再新增“转发”的包装类。

## UI（Compose + MVI）

UI 细节以 `docs/mvi-architecture.md` 为准，摘要：

- 状态只能由 `setState` / Reducer 修改；副作用只出现在 Route / EffectHandler。
- 事件通道由 `base/mvi/CollectEffects` 单消费者收集，禁止手写 `LaunchedEffect { viewModel.event.collect { } }`。
- `ui/part` 无状态：参数是数据 + lambda，不接 ViewModel、不 collect Flow、不做导航、不读 Koin。
- `XxxScreen` 参数只用 `state`、`onAction`、`modifier`；插槽和平台对象回调例外。
- 用户可见文案走 `strings.xml` / `UiText`；错误到文案收口在页面 `interaction/XxxErrorText.kt`。
- 系统返回键必须 dispatch 页面 Action，由 Route 消费 Event 再 `back()`。

## 反过度防御

- 信任层间契约：data 已用 `Result` 收口异常，ViewModel 直接 `fold` / `onFailure`，不再 `try-catch` 转消息。
- `try-catch` 只允许出现在平台边界（data、base 的平台工具）；`try-finally` 只用于清理资源；`CancellationException` 必须重抛。
- 非空类型参数不写 `?.` / `?: emptyList()` 兜底；只有业务上确实可空的状态才使用 nullable。
- `require` / `check` 只放在外部输入或构造入参边界；内部函数不重复校验。
- `UiState` 只保留渲染必须的字段；能由 `error: UiText?` 推导的 `isError` 不建。
- 不因为“以后可能替换”创建接口、Factory 或空实现；domain port 是业务契约，按业务需要保留。

## 代码风格

- Kotlin 官方风格：4 空格缩进、多行调用尾逗号、无通配 import。
- 一个文件一个顶层公开类型，文件名与类型名一致；例外：`XxxContract.kt`、`XxxErrorText.kt`、`XxxPreviews.kt`、`XxxReducer.kt`、`XxxModels.kt`、`PreviewFixtures.kt`、`Theme.kt`、`AppThemeTokens.kt`、`AppModule.kt`、`CollectEffects.kt`、`AppDestination.kt`、`BatteryChartModels.kt`、`VirusAndroidEffects.kt`。
- 接口不加 `I` 前缀；页面内类型带页面前缀：`JunkCleanUiState`、`NetworkSpeedUiState`。
- 组件统一 `CleanX` / `App` 前缀；通用设计组件放 `base/designsystem/component`。
- `XxxAction` 是 UI 上报的事实（过去式）；`XxxEvent` 是 VM 下发的命令（祈使式）。
- 术语只有一套：`document`、`file`、`junk`、`clean`、`scan`、`delete`、`permission`；代码、资源键、文案一致。
- 资源键：新增页面用 `<page>_<thing>`；存量键保留现有前缀，不一次性重命名；`values/` 与 `values-zh/` 等语言包键集合一致。
- `const val` 一律 UPPER_SNAKE_CASE；普通 `val` 用 camelCase；布尔字段用 `is` / `has` / `can` / `should` 开头；集合用复数。
- 单函数（不含 `when` 分支体）不超过 40 行；参数不超过 5 个，更多时用参数对象；嵌套不超过 3 层，优先早返回。
- 缩写按单词大小写：`Api`、`Dto`、`Id`、`Url`、`Ui`、`Json`；不写 `API`、`DTO`、`ID`、`URL`。
- 返回 `Flow` 的函数用 `observeXxx`；挂起函数直接动词，不加 `Async` 后缀。
- KDoc 用中文：写“为什么”和对外契约，不逐行复述实现；纯载体（UiState / Action / Event / Reducer）靠命名表达。

## 测试

- domain 用纯 JVM 测试，不引 Robolectric。
- ViewModel 测试用 `runTest`；需要主调度器时用 `Dispatchers.setMain`，外部依赖用 Fake 端口实现。
- data 的端口实现测试用真实临时目录或内存介质，不依赖设备。
- 文件名 `<Type>Test.kt`；方法用 camelCase 行为句（`nextRequiresSelection`），不写 `test1` / `should` 前缀。
- 替身用 `FakeXxx`，夹具用 `XxxFixture`，不用 `Mock` 前缀，除非真的引了 mock 框架。
- 修 bug 时补一条能复现的用例。

## 提交

- 一次提交一个主题；信息用 `feat:` / `fix:` / `docs:` / `refactor:` / `test:` / `chore:` 前缀，可带 scope 和中文描述。
- 提交前至少跑 `.\scripts\check-rules.ps1`；合入前跑 `.\scripts\ci.ps1`（规范 + 编译 + 单测）。

## 不要提前建

| 东西 | 什么时候建 |
|---|---|
| `mapper/` | 存储模型 ≠ 领域模型 |
| 新 usecase | 出现跨多个 repository 或跨域的编排；单端口、只转发的不建 |
| `data/remote/` | 第一个真实网络调用 |
| `base/util/` 再拆文件 | 出现可独立复用的职责时，不按文件行数拆分 |
| `domain/rule/` 新文件 | 出现第一条纯规则时 |

判断法：目录里现在放进去的文件数是 0 → 不建；同一调用链上只有单目标、无独立语义的转发层 → 不建；判断不了放哪层 → 看它 import 不 import 平台 API。

## 检查

```powershell
.\scripts\check-rules.ps1    # 执行策略允许时
.\scripts\check-rules.cmd    # 执行策略受限时
```

当前门禁覆盖：domain 纯净、分层方向、data 可见性/契约、view 不 import data、domain 模型命名、错误分支 KDoc、单文件公开类型、命名、UiText、CollectEffects、BackHandler、effect 位置、package/目录、语言包 key。任何一项不通过退出码非 0。

新克隆执行一次 `.\scripts\install-hooks.ps1` 启用 `pre-commit`；钩子可被 `git commit --no-verify` 绕过，CI 必须跑同一脚本兜底。
