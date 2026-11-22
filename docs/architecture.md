# 架构说明

本文描述当前仓库的实际架构与边界。硬规则、命名、风格与例外登记以 [AGENTS.md](../AGENTS.md) 为准；例外见 [规则例外](rules/exceptions.md)；整改计划见 [架构与规范整改方案](architecture-remediation-plan.md)。

## 1. 形态

- 单 Activity：`MainActivity` 只负责 edge-to-edge、主题、媒体删除 requester 与电池采样协调。
- Compose：页面由 `app/view/<page>/ui` 的 Route / Screen / part 组成。
- MVI：所有业务页面 ViewModel 继承 `base/mvi/MviViewModel`，单一 `StateFlow` + 事件 Channel。
- Koin：`app/di` 是唯一装配点，`MyApp` 启动 `dataModule` 与 `presentationModule`。
- 类型化导航：`AppDestination` 使用 `@Serializable` 目的地，`AppNavigator` 隔离 `NavHostController`。
- Gradle 模块：`:app` 承载业务；`:junkcc` 是独立资源模块，不参与业务代码。

## 2. 分层与依赖

```text
app/view  ──> base + domain
app/root  ──> app/view + base + domain
app/di    ──> data + domain + base
data      ──> domain + base
domain    ──> Kotlin/JDK（Flow 例外见 exceptions）
base      ──> AndroidX / Kotlin（不得依赖 data 或 app）
```

| 包 | 职责 | 禁止 |
|---|---|---|
| `domain` | `model/<concept>`、`port`、`rule`、`error`；纯策略与契约 | Android/AndroidX、Context/Uri、Dispatchers、日志、资源 id |
| `data` | `repository` 端口实现、`local` 平台适配、授权与删除 | 业务规则、页面文案、导航、`app` 依赖 |
| `app/view` | 页面状态、Action、Reducer、Route、EffectHandler、part | 直接 import `data`、持有 `NavHostController` |
| `app/root` | NavHost、组合根、启动流程 | 直接 import `data` |
| `app/di` | Koin 装配 | 业务逻辑、页面状态 |
| `base` | designsystem、mvi、navigation、permission、util | 依赖 `data` / `app` |

目录名与包名一致，全小写；空目录不建。

## 3. 页面数据流

```text
页面进入 / 用户操作 / 平台回调
    → Route 或 part 上报 Action
    → ViewModel.onAction
    → Reducer / setState 生成新 State
    → StateFlow 驱动 Compose
    → 一次性副作用通过 Event Channel
    → Route / EffectHandler 消费并导航或调用平台
```

约束：

- State 只能由 `setState` 或 Reducer 修改。
- `ui/part` 无状态：只接收数据与 lambda，不接触 ViewModel、Flow、导航或 Koin。
- `XxxScreen` 只接收 `state`、`onAction`、`modifier`；插槽和平台回调例外。
- 一次性事件由 `base/mvi/CollectEffects` 单消费者收集，只在 `RESUMED` 消费；禁止手写 `LaunchedEffect { viewModel.event.collect { } }`。
- 用户可见文案走 `strings.xml` / `UiText`；错误到文案收口在页面 `interaction/XxxErrorText.kt`。
- 系统返回键必须 dispatch 页面 Action，由 Route 消费 Event 再 `back()`。

## 4. 导航

- 目的地集中在 `base/navigation/AppDestination.kt`，参数只放业务字段。
- UI 只依赖 `AppNavigator`；`NavControllerAppNavigator` 在 `app/root` 实现。
- 文件管理的列表页与详情页共享同一个流程根 entry 的 ViewModel 与权限状态；`FileManagerFlowHost` 负责单消费者 effect。
- 页面切换动画集中在 `AppRouteTransitions`，不散落在各 Route。

## 5. 依赖注入

- `dataModule` 提供平台适配、仓库实现、应用级协程作用域。
- `presentationModule` 提供每个页面的 ViewModel，IO 调度器在此注入。
- 仓库实现类默认 `internal`，只通过 domain 端口暴露。
- 新增绑定后应跑 Koin `checkModules`（纳入测试计划）。

## 6. 测试

- domain / data 规则用纯 JVM 测试，不引 Robolectric。
- ViewModel 用 `runTest`；需要主调度器时用 `Dispatchers.setMain`；外部依赖用 Fake 端口。
- 数据实现测试使用真实临时目录或内存介质，不依赖设备。
- 架构边界由 `MotherTemplateBoundaryTest` 与 `check-rules.ps1` 双保险。

## 7. 门禁与命令

```powershell
.\scripts\check-rules.ps1
.\scripts\check-encoding.ps1
.\gradlew.bat :app:ktlintCheck
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
```

`check-rules.ps1` 用 `scripts/function-length-baseline.txt` 管理存量函数长度债务，只拒绝新增超限函数；`localeParity` 输出各语言翻译覆盖率（当前不阻塞）；`check-encoding.ps1` 做严格 UTF-8 与乱码检查；ktlint 的 `filename` 规则按 [规则例外](rules/exceptions.md) E3 关闭。

## 8. 当前已知差距

以下问题已登记在整改方案中，不是“规范允许”的做法：

- APK/缓存/临时文件的分类策略仍在 `data/local/junkclean/scanner`，应按 `AGENTS.md` 收回 `domain/rule`。
- `JunkCleanViewModel` 与 `VirusScanViewModel` 仍有较多状态字段，应收回 `UiState`。
- `AppNavHost` 与 `JunkCleanViewModel` 已接近单文件复杂度上限，后续按导航图/状态收口拆分。
- `check-rules.ps1` 的函数长度检查使用 `scripts/function-length-baseline.txt` 管理存量债务；`localeParity` 只对 key 对齐硬性失败，翻译覆盖率目前仅报告。
- ktlint 的 `filename` 规则按 E3 关闭，文件名/类型一致性由 `publicTypes` 检查兜底。
