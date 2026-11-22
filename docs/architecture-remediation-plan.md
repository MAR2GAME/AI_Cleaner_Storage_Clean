# 架构与规范整改方案

- 仓库：`ai_-cleaner_-storage_-clean`
- 版本：v1（2026-11-23）
- 范围：Android 应用架构、分层规则、MVI 一致性、规范脚本、构建配置、测试与文档编码
- 非目标：`:junkcc`、本地 AAR、Trustlook Key、商店合规与产品功能（本次不评估、不修改）

## 0. 摘要

当前项目的**架构意图明显强于落地一致性**：

- 设计侧：domain 纯净、依赖方向明确、MVI 基座、类型化导航、Koin、双保险边界测试，值得保留。
- 落地侧：业务规则下沉到 `data`、MVI 状态字段泄漏、5 个规范脚本已失效、3 条门禁检查失明、Compose 版本错配、文档编码损坏。

本方案的目标不是“换一套架构”，而是**把已有意图变成可执行、可验证、可维护的约束**。预计 1 人约 26 个工作日（含 20% buffer 约 5–6 周），2 人并行约 3 周。

验收后的目标状态：

| 维度 | 现状 | 目标 |
|---|---|---|
| domain 平台/时间泄漏 | 3 处（`java.io.File`、`System.currentTimeMillis` 默认值） | 0 |
| ViewModel 业务状态字段 | JunkClean 8 个、VirusScan 11 个 | 0（只保留 Job/取消信号） |
| `ReducerOnly` 混用 | 22 处引用 | 0 |
| 失效规范脚本 | 5 个（exit 1） | 0 |
| 失明门禁检查 | 3 条（dataVisibility / functionLength / localeParity） | 0，且有负向测试 |
| ktlint | test 源集失败，CI 不跑 | 通过且 CI 必跑 |
| CI 门禁步骤 | 3 步（规则/单测/debug） | 7 步（编码/规则/风格/单测/lint/release/报告） |
| 未使用版本别名 | 8 个 | 0 |
| Compose 版本 | ui 1.11.2 与 material3 1.3.1 错配 | 同一版本线 |
| 文档乱码 | AGENTS/README + 7 个 KDoc | 0 |
| 单测 | 172 | ≥ 210，domain 覆盖率 ≥ 70% |

## 1. 范围与边界

### 1.1 纳入范围

- `app/src/main/java/.../{app,base,data,domain}` 的分层与依赖方向。
- MVI 契约、状态所有权、Action/Event 语义、导航与 DI。
- `scripts/` 下的规范检查与 CI。
- `gradle/libs.versions.toml`、`app/build.gradle.kts` 的构建规范。
- `app/src/test`、`app/src/androidTest` 的测试结构。
- `AGENTS.md`、`README.md`、`docs/` 的文档与编码。

### 1.2 明确不做

- 不删除、不重构 `:junkcc`。
- 不替换、不审计 `app/libs/cloudscan_sdk_5.0.18.20250821.aar`。
- 不处理 Trustlook Key 的注入/轮换。
- 不改产品行为（清理策略、扫描阈值、页面功能、商店合规文案）。

## 2. 现状基线（实测）

| 项 | 现状 | 证据 |
|---|---|---|
| 页面/VM/Reducer | 25 页面、25 ViewModel、11 个 `*Reducer.kt` | `app/src/main/java/.../app/view` |
| MVI 状态泄漏 | JunkClean 8 个状态字段；VirusScan 11 个 | `JunkCleanViewModel.kt:42-52`、`VirusScanViewModel.kt:35-53` |
| Action 语义 | `JunkCleanAction.ReducerOnly` 把内部更新混入 UI Action | `JunkCleanContract.kt:61-155` |
| 导航 | `AppNavHost.kt` 643 行 / 36 个 `composable<...>` | `AppNavHost.kt` |
| tab 类型 | `HomeUiState.selectedTab: Int`，与 `MainTab` 枚举并存 | `HomeContract.kt:16-31` |
| domain 泄漏 | `FileClassificationRule` 触盘；两处 `System.currentTimeMillis()` 默认值 | `FileClassificationRule.kt:87-91`、`JunkFileModel.kt:15`、`BatteryHistoryRepository.kt:27` |
| data 业务规则 | APK/缓存/临时文件的分类策略在 scanner | `ApkScanner.kt:12-27`、`CacheScanner.kt:8-11` |
| 规范脚本 | 11 个脚本，5 个直接 exit 1，2 个能跑但 CI 不调用 | 见附录 A |
| 门禁失明 | dataVisibility 匹配 0 个文件；functionLength 漏掉 173 个 `internal fun`、43 个 `override fun`、4 个泛型函数；localeParity 只查 key | `check-rules.ps1:97,200,372` |
| 风格 | `ktlintCheck` main 通过、test 失败；CI 未接 | 实测 `:app:ktlintCheck` |
| 构建 | Compose ui 1.11.2 vs material3 1.3.1；8 个未用别名 | `dependencyInsight`、`libs.versions.toml` |
| 测试 | 50 类 / 172 用例 / 0 失败 | `:app:testDebugUnitTest --rerun-tasks` |
| 文档 | AGENTS/README 乱码；`docs/` 33 个文件被删 | 当前 HEAD `412cf00` |

## 3. 目标架构（To-Be）

### 3.1 保持单模块，但把边界写死

继续使用 `:app` 单模块 + layer-first。25 个页面、3.8 万行仍在单模块可维护范围内；现在拆 `feature/*` 会放大成本。但要把 `base` 明确定位为“未命名的 core”，并为未来拆出 `:core:*` 留好缝。

允许的依赖边（与 `AGENTS.md` 一致，新增两条）：

```text
app/view  -> base + domain
app/root  -> app/view + base + domain
app/di    -> data + domain + base
data      -> domain + base
domain    -> Kotlin/JDK only（Flow 例外仅限 port）
base      -> Kotlin/JDK + AndroidX（不得依赖 data/app）
```

新增约束：

- `domain` 禁止 `java.io.File`、`System.currentTimeMillis()`、`android.*`、`androidx.*`。
- `data` 禁止定义“什么算垃圾/什么该删除”的策略；只做枚举、平台调用、映射。
- `app/view` 的 `XxxScreen` 只接收 `state`、`onAction`、`modifier`；`ui/part` 不接触 ViewModel/导航/Koin。

### 3.2 职责重新划分

| 层 | 应该负责 | 不应该负责 |
|---|---|---|
| domain | 分类/去重/相似/WhatsApp/锁策略、端口契约、错误语义 | 文件遍历、MediaStore、时间读取、日志 |
| data | 平台枚举、权限/删除授权、DTO→Model 映射、异常收口 | 判定“哪些是垃圾”、页面文案、导航 |
| app/view | 状态渲染、Action 上报、effect 消费 | 直接读 data、持有第二个状态源、业务规则 |
| app/di | 唯一装配点 | 业务逻辑、页面状态 |

### 3.3 规范门禁形态

目标不是“更多 PowerShell”，而是三类门禁各司其职：

1. **风格**：ktlint（格式）+ detekt（复杂度/坏味道）。
2. **架构**：JUnit/Konsist 测试（分层、命名、状态所有权、domain 纯净）。
3. **流程**：PowerShell 只做编码检查、任务编排和 CI 入口。

每条门禁必须有**负向测试**（fixture 违反规则时必须失败），否则会重演 `dataVisibility` 这种“永远通过”的检查。

## 4. 实施原则

1. **行为不变**：Phase 2/3 是纯重构，先补特征测试（characterization tests）再动代码。
2. **小 PR、单主题**：一个 PR 只做一件事，提交用 `refactor:`/`test:`/`chore:`。
3. **先修门禁，再改代码**：否则重构完没有可信的验收标准。
4. **一条规则一个负向测试**：门禁自己也要被测。
5. **能 JUnit 就不 PowerShell**：正则无法表达 Kotlin 语义，维护成本高。
6. **不做“以后可能用”的抽象**：沿用 `AGENTS.md` 的“不要提前建”。
7. **编码强制 UTF-8**：新增/修改文件必须通过严格 UTF-8 解码，且不含 U+FFFD。

## 5. 阶段总览

| 阶段 | 目标 | 交付物 | 预估 |
|---|---|---|---|
| P0 | 文档与构建基线 | 可读的 AGENTS/README、对齐的 Compose、干净的脚本清单 | 2 天 |
| P1 | 规范门禁重建 | 可信的 check-rules/ktlint/DI 验证/负向测试 | 4.5 天 |
| P2 | 领域规则回收 | domain 分类规则 + data 只枚举 + 纯 JVM 测试 | 7 天 |
| P3 | MVI 状态收口 | VM 只保留 Job；Action/Event 语义纯净 | 7 天 |
| P4 | 导航与 DI 拆分 | 小 NavHost、可验证的 Koin 图 | 2.5 天 |
| P5 | 构建与 CI 收尾 | 7 步 CI、release 产物、架构文档 | 2.5 天 |
| P6 | 模块化决策 | ADR：继续单模块或拆 `:core:*` | 0.5 天 |

## 6. 阶段详情

### Phase 0：文档与构建基线（2 天）

**目标**：先让“规则”和“构建”可读、可复现，避免在坏基线上重构。

**任务**

1. 从干净提交恢复文档：
   - `AGENTS.md` 正文：`git show 6d9e6df:AGENTS.md`（132 行，已验证 UTF-8 正常）。
   - `README.md`：`git show bfbf15e^:README.md`（已验证 UTF-8 正常）。
   - 例外 E1/E2：`git show 6d9e6df:docs/rules/exceptions.md`。
   - 合并时用 UTF-8 工具（不要用 Windows PowerShell 5.1 的默认 `Get-Content`/`Set-Content`），合并后跑 `check-encoding`。
2. 新增 `docs/architecture.md`（1–2 页）：分层图、允许依赖边、MVI 生命周期、导航/DI、门禁清单。把 `AGENTS.md` 和 `README.md` 里的自引用改为指向它。
3. 增加编码约束：
   - `.editorconfig` 增加 `charset = utf-8`、`end_of_line = lf`、`insert_final_newline = true`。
   - 新增 `.gitattributes`（`*.kt`/`*.md`/`*.xml` 强制 LF，`*.ps1` 允许 CRLF）。
   - 新增 `scripts/check-encoding.ps1`：严格 UTF-8 解码、拒绝 U+FFFD、拒绝已知 mojibake 片段；接入 CI。
4. Compose 版本对齐：
   - 运行 `:app:dependencyInsight --dependency androidx.compose.ui:ui --configuration debugRuntimeClasspath`。
   - 二选一：升级 BOM 到同时包含 ui 1.11.x 与 material3 1.4.x 的版本；或去掉 BOM、把 ui/material3/foundation/runtime 全部锁到同一版本线。
   - 删除 8 个未使用别名：`androidx-constraintlayout`、`androidx-work-runtime-ktx`、`lottie`、`lottie-compose`、`kotlinx-coroutines-core`、`glide`、`compiler`、`gson`。
5. 脚本清点：把 `scripts/` 分成“保留 / 删除”两类，先删 5 个已失效脚本（`check-mvi-boundaries`、`check-variant-boundaries`、`check-dependencies`、`check-template`、`check-permission-ad-boundaries`）。删除前在 PR 描述里记录它们原本想覆盖的规则，避免规则丢失。

**验收**

```powershell
.\scripts\check-encoding.ps1
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:assembleRelease
```

- 文档无乱码、无 U+FFFD。
- debug/release 均构建成功。
- 脚本目录只剩实际在用的入口。

**回滚**：Phase 0 只有文档/版本/脚本删除，逐 PR revert 即可；不改业务代码。

### Phase 1：规范门禁重建（4.5 天）

**目标**：让“检查通过”重新等于“规则满足”。

**任务**

1. 修 `dataVisibility`：
   - 当前只匹配 `*RepositoryImpl.kt`，项目实际是 12 个 `Default*Repository.kt`。
   - 改为扫描 `data/repository/*.kt` 中所有顶层 `class`，要求 `internal`（Android 组件除外）；或改用 JUnit 测试。
2. 修 `functionLengthLimit`：
   - 当前正则漏掉 `internal fun`（173 个）、`override fun`（43 个）、泛型函数（4 个）。
   - 改用能识别修饰符/泛型/表达式体的 Kotlin 测试；建议规则调整为“普通函数 ≤ 40 行，`@Composable` ≤ 60 行”，并在 `AGENTS.md` 登记。
   - 用 `FileManagerFlowHost`（65 行）作为首个真实违规用例，先让它红，再拆分。
3. 拆 `localeParity`：
   - 保留“key 集合一致”为硬门禁。
   - 新增“翻译覆盖率”报告（非阻塞）：zh 100%，其余语种 ≥ 80% 才允许宣称支持；当前 36–50%，先出报告不阻塞。
4. 明确检查范围：
   - `check-rules` 增加 `-Scope main|test|all`；至少把 `app/src/test` 纳入编码/命名检查。
   - profile 中未被读取的 `pages`/`destinations`/`resourcePrefixes`/`navHost` 二选一：接进校验，或删除。
5. 接 ktlint：
   - 先 `:app:ktlintFormat` 修 test 源集，人工 review diff。
   - CI 增加 `:app:ktlintCheck`；pre-commit 增加同一命令。
6. 接 detekt（可选但建议）：
   - 规则：LongMethod、ComplexMethod、TooManyFunctions、MagicNumber（domain 例外）、ReturnCount。
   - 先 baseline，只拦新增问题。
7. 加 Koin 图验证：
   - `DiModuleTest`：`koinApplication { androidContext(mock); modules(dataModule, presentationModule) }.checkModules()`；对 Android Context 依赖用 fake。
8. 加负向测试：
   - `scripts/fixtures/` 放违规样例；每个检查至少 1 个“应失败”用例、1 个“应通过”用例。
   - 若检查迁移到 JUnit，负向测试也放 JUnit，避免两套逻辑。
9. 迁移策略：
   - 短期保留 `check-rules.ps1`，只修上述 3 条。
   - 中期把语义检查迁到 `ArchitectureBoundaryTest`/`MviStateOwnershipTest`/`DomainPurityTest`，PowerShell 只保留编码/编排。
   - 不建议引入“又一套规则引擎”；Konsist 可以先在独立 PR 验证 Kotlin 2.1/AGP 9.2 兼容性，再决定是否替换 JUnit 扫描。

**验收**

```powershell
.\scripts\check-rules.ps1
.\gradlew.bat :app:ktlintCheck
.\gradlew.bat :app:testDebugUnitTest
```

- 每个检查都有负向测试，且负向测试在 CI 中运行。
- ktlint main/test 全绿。
- 5 个死脚本已删除，`check-rules` 不再有“空转”检查。

**回滚**：门禁变更按检查逐条提交；若某条误报，先降级为 report，再修规则。

### Phase 2：领域规则回收（7 天）

**目标**：把“什么算垃圾/该删什么”从 `data` 收回 `domain`，让策略可纯 JVM 测试。

**任务**

1. 新增 domain 模型与规则：

```kotlin
// domain/model/junk/JunkCandidateModel.kt
data class JunkCandidateModel(
    val path: String,
    val name: String,
    val sizeBytes: Long,
    val modifiedMillis: Long,
    val mimeType: String? = null,
)

// domain/rule/JunkClassificationRule.kt
internal object JunkClassificationRule {
    fun categoryOf(candidate: JunkCandidateModel, nowMillis: Long): JunkCategory?
}
```

2. 把 `ApkScanner`、`CacheScanner`、`TempFileScanner` 的判断迁入规则：
   - APK：30 天以上、文件名含 `(1)`/`copy`/`backup`、非系统路径。
   - Cache：cacheDir 下的文件。
   - Temp：扩展名集合。
   - data 只负责 `File -> JunkCandidateModel` 和遍历。
3. 去掉 domain 的 `File` 与时间默认值：
   - `FileClassificationRule.identity` 改为接收已规范化路径/URI；规范化放到 data。
   - `BatteryHistoryRepository.loadRecent(nowMillis)` 去掉默认值，调用方显式传时钟。
   - `JunkFileModel.modifiedMillis` 去掉默认值，由 data mapper 填。
4. 加特征测试：
   - 先记录当前扫描结果（临时目录 + 固定文件集）作为 golden。
   - 重构后逐项比对；不允许改阈值/扩展名集合。
5. 加 domain 纯净门禁：
   - `DomainPurityTest`：domain 源码不得出现 `java.io.File`、`System.currentTimeMillis`、`android.`、`androidx.`、`Dispatchers`。
   - 迁移完成后把 `AGENTS.md` 对应规则同步。

**验收**

```powershell
.\gradlew.bat :app:testDebugUnitTest
git grep -n "java.io.File" -- app/src/main/java/com/aicleanerstorage/spaceclean/domain
git grep -n "System.currentTimeMillis" -- app/src/main/java/com/aicleanerstorage/spaceclean/domain
```

- 两条 `git grep` 均为空。
- 新增分类规则测试 ≥ 15 个用例（APK/缓存/临时/边界）。
- 扫描行为 golden 对比一致。

**回滚**：保留旧 scanner 分支，按扫描器逐个切换；每个 scanner 独立 PR。

### Phase 3：MVI 状态收口（7 天）

**目标**：让“单一状态源”从基类继承变成真实约束。

**任务**

1. JunkCleanViewModel：
   - 保留 `scanJob`/`cleaningJob`（资源句柄，不算 UI 状态）。
   - `lastScanResult`、`pendingCleanFiles`、`cleaningExecutionState`、`permissionPrevious` 迁入 `JunkCleanUiState` 或一个不可变 `JunkCleanSession`。
   - `stopCleaning` 改为 `AtomicBoolean`（取消信号）或直接用 `Job.cancel()`，去掉 `@Volatile`。
   - 删除外层 `try-catch`：仓库已返回 `Result`，只处理 `Result` 失败分支。
2. VirusScanViewModel：
   - 抽出 `VirusScanSession`（阶段、威胁集合、结果可见性）或把字段迁入 `VirusScanUiState`。
   - 15 个 `private var` 中，除 Job 外全部收口；目标文件 ≤ 250 行。
3. 移除 `ReducerOnly`：
   - 公共 `JunkCleanAction` 只保留 UI 事实与权限结果。
   - 内部更新改为私有 `JunkCleanUpdate` + 独立 reducer 函数，VM 直接调用 `reduceUpdate(...)`。
4. 修 `MviViewModel`：
   - 删除无意义的 `launch` 包装。
   - `Channel(Channel.BUFFERED)`。
   - `sendEvent` 不 `check`，关闭后直接丢弃。
   - 统一 `onAction(action)` 参数名（当前大量 `onAction(intent)`）。
5. Home 类型化：
   - `HomeUiState.selectedTab: MainTab`。
   - `HomeAction.TabSelected(tab: MainTab)`。
   - `AppNavHost` 直接用 `entry.toRoute<AppDestination.Main>().initialTab`，删除 `MainTab.valueOf(...).ordinal`。
6. 加状态所有权测试：
   - `MviStateOwnershipTest`：ViewModel 中允许的字段白名单 = `Job`、`Channel`、`AtomicBoolean`、`CoroutineScope`；其他 `private var` 失败。
   - 用 JunkClean/VirusScan 作为首批断言对象。

**验收**

```powershell
.\gradlew.bat :app:testDebugUnitTest
git grep -n "ReducerOnly"
git grep -n "selectedTab: Int"
git grep -n "onAction(intent"
```

- 三条 `git grep` 为空。
- JunkClean/VirusScan 的字段只剩 Job/取消信号。
- 现有 172 个测试全部通过；新增状态所有权测试。

**回滚**：按页面拆分 PR；每个页面先补行为测试，再迁字段。

### Phase 4：导航与 DI 拆分（2.5 天）

**目标**：降低 `AppNavHost` 与 `DataModule` 的单点复杂度。

**任务**

1. 拆 `AppNavHost`：
   - `RootNavGraph`（Splash/Onboarding/Main/Settings）。
   - `ToolboxNavGraph`（设备/电池/网络/用量）。
   - `FileManagerNavGraph`（8 个文件页 + 详情页）。
   - `VirusNavGraph`（扫描/结果）。
   - 每个文件 ≤ 250 行；目的地定义仍集中在 `AppDestination`。
2. DI 清理：
   - 删除 `MyApp.instance`（当前无外部引用）。
   - 删除未使用的 `APPLICATION_SCOPE` 绑定。
   - `BatteryInfoViewModel(get(), get(), get(), get(), get(), ...)` 改为具名 qualifier 或参数对象，避免位置注入。
   - `DataModule` 按能力拆成 `platformModule`/`repositoryModule`（可选，若 ≤ 120 行可不拆）。
3. 加 DI 测试：
   - `DiModuleTest` 用 `checkModules()` 验证全图；Android Context 用 fake。
4. 导航测试：
   - `TestNavHostController` + 关键路径：Splash → Main → JunkClean → 返回；文件详情进入/返回；权限拒绝回退。
   - Compose UI 测试可用 Robolectric 或 instrumented，二选一，先在 CI 跑 3 条冒烟。

**验收**

- `AppNavHost` 单文件 ≤ 250 行，总路由数仍为 36。
- `DiModuleTest` 通过；无未使用绑定。
- 导航冒烟 3 条通过。

**回滚**：NavHost 拆分是纯移动；按图逐个 PR。

### Phase 5：构建与 CI 收尾（2.5 天）

**目标**：让 CI 成为真正的合并门禁，而不是“能编过”。

**任务**

1. CI 拆成 5–7 个 job：
   - `encoding`：`check-encoding.ps1`。
   - `rules`：`check-rules.ps1`（或迁移后的架构测试）。
   - `style`：`ktlintCheck` + `detekt`。
   - `test`：`testDebugUnitTest` + 覆盖率报告（Kover/Jacoco）。
   - `lint`：`:app:lintDebug`。
   - `release`：`:app:assembleRelease`（CI secret 签名）或 unsigned + R8 冒烟。
   - `report`：汇总/上传。
2. 签名：
   - `signingConfigs.release` 从环境变量/`local.properties` 读取，不提交 keystore。
   - CI 缺少 secret 时只跑 unsigned，不阻塞 PR；release 分支强制签名。
3. Toolchain：
   - 当前 `jvmToolchain(11)`，CI 只装 JDK 17。二选一：CI 同时装 JDK 11；或 `jvmToolchain(17)` + `jvmTarget = 11`。
   - 在 CI 里加 `:app:javaToolchains` 输出，便于排障。
4. 文档：
   - `docs/architecture.md`（Phase 0 创建）补上门禁清单与数据流图。
   - `docs/decisions/ADR-0001-single-module.md`：记录“为什么暂不拆模块”。
   - README 删除自引用，补 release/测试命令。
5. 清理：
   - `.tmp_aar`、`build/` 等生成物不入库（已忽略）。
   - 删除 `ExampleUnitTest`/`ExampleInstrumentedTest` 模板残留，或替换为真实冒烟测试。

**验收**

- PR 上 7 个 job 全绿。
- release 产物存在且签名校验通过（release 分支）。
- `docs/architecture.md` 可读，AGENTS/README 无乱码。

**回滚**：CI 变更按 job 逐个上线；签名配置放最后。

### Phase 6：模块化决策（0.5 天）

**触发条件**（满足任一再评估）：

- `app/src/main` > 45k 行或页面 > 30。
- 增量编译 > 2 分钟。
- `AppNavHost` 或 `app/view/common/files` 成为长期冲突热点。

**决策**：

- 不满足：写 ADR 记录“继续单模块”，并删除所有 `core/*`/`feature/*` 幽灵脚本。
- 满足：先拆 `:core:designsystem`、`:core:mvi`、`:core:navigation`、`:core:permission`，不拆 `feature/*`；重写模块边界检查（复用被删脚本里的好规则）。

**验收**：ADR 入库；若拆分，模块图无环、构建通过。

## 7. 详细任务清单

| ID | 阶段 | 任务 | 涉及文件 | 验收 | 预估 |
|---|---|---|---|---|---|
| P0-1 | P0 | 恢复 AGENTS/README/例外 | `AGENTS.md`、`README.md` | 严格 UTF-8、无 U+FFFD | 0.5d |
| P0-2 | P0 | 编码约束与检查 | `.editorconfig`、`.gitattributes`、`scripts/check-encoding.ps1` | CI 通过 | 0.5d |
| P0-3 | P0 | Compose 版本对齐 | `libs.versions.toml`、`app/build.gradle.kts` | `dependencyInsight` 单版本 | 0.5d |
| P0-4 | P0 | 删除 8 个未用别名 | `libs.versions.toml` | 构建通过 | 0.1d |
| P0-5 | P0 | 删除 5 个死脚本 | `scripts/` | 目录只剩活入口 | 0.3d |
| P1-1 | P1 | 修 dataVisibility | `check-rules.ps1` 或 JUnit | 负向测试失败 | 0.3d |
| P1-2 | P1 | 修 functionLength | 同上 | `FileManagerFlowHost` 被拦截 | 0.5d |
| P1-3 | P1 | 拆 localeParity | `check-rules.ps1` | key 硬门禁 + 覆盖率报告 | 0.5d |
| P1-4 | P1 | 检查范围/profile | `check-rules.ps1`、profile | 范围明确 | 0.3d |
| P1-5 | P1 | 接 ktlint | `.github/workflows/ci.yml`、`.githooks/pre-commit` | test 源集绿 | 0.5d |
| P1-6 | P1 | 接 detekt（可选） | `detekt.yml` | baseline 通过 | 0.5d |
| P1-7 | P1 | 门禁负向测试 | `app/src/test/.../architecture` 或 `scripts/fixtures` | 每条检查有红/绿样例 | 1d |
| P1-8 | P1 | Koin checkModules | `app/src/test/.../di` | 全图通过 | 0.3d |
| P1-9 | P1 | 覆盖率报告 | 构建脚本 | domain ≥ 70% 可见 | 0.5d |
| P2-1 | P2 | JunkCandidateModel + 规则 | `domain/model/junk`、`domain/rule` | 纯 JVM 测试 | 1d |
| P2-2 | P2 | 三个 scanner 迁移 | `data/local/junkclean/scanner` | 行为 golden 一致 | 2d |
| P2-3 | P2 | 去 File/时间默认值 | domain 多处 | `git grep` 为空 | 0.5d |
| P2-4 | P2 | 分类规则测试 | `app/src/test/.../domain/rule` | ≥ 15 用例 | 1d |
| P2-5 | P2 | DomainPurityTest | 测试目录 | 新违规被拦截 | 0.5d |
| P3-1 | P3 | JunkClean 状态收口 | `JunkCleanViewModel.kt` 等 | 字段只剩 Job | 1.5d |
| P3-2 | P3 | VirusScan 状态收口 | `VirusScanViewModel.kt` | 文件 ≤ 250 行 | 2d |
| P3-3 | P3 | 移除 ReducerOnly | `JunkCleanContract.kt` | `git grep` 为空 | 0.5d |
| P3-4 | P3 | 修 MviViewModel | `MviViewModel.kt` | Channel/BUFFERED、无 check | 0.3d |
| P3-5 | P3 | Home 用 MainTab | `HomeContract.kt`、`AppNavHost.kt` | 无 Int tab | 0.3d |
| P3-6 | P3 | 统一 onAction 命名 | 25 个 VM | `git grep onAction(intent` 为空 | 0.2d |
| P3-7 | P3 | 状态所有权测试 | 测试目录 | 新字段被拦截 | 0.5d |
| P3-8 | P3 | 回归测试 | 测试目录 | 172+ 全绿 | 1d |
| P4-1 | P4 | 拆 NavHost | `app/root` | 单文件 ≤ 250 行 | 1d |
| P4-2 | P4 | DI 清理 | `DataModule.kt`、`PresentationModule.kt`、`MyApp.kt` | 无死绑定 | 0.5d |
| P4-3 | P4 | DI/导航冒烟 | 测试目录 | 3 条通过 | 0.5d |
| P5-1 | P5 | CI 7 步 | `.github/workflows/ci.yml` | 全绿 | 1d |
| P5-2 | P5 | 签名/release | `app/build.gradle.kts` | 产物可校验 | 0.5d |
| P5-3 | P5 | Toolchain 统一 | 构建脚本、CI | CI 不再依赖隐含 JDK | 0.3d |
| P5-4 | P5 | 架构文档/ADR | `docs/` | 可读、无自引用 | 0.5d |
| P6-1 | P6 | 模块化决策 | ADR | 决策入库 | 0.5d |

## 8. 关键设计

### 8.1 领域分类规则（Phase 2）

```kotlin
data class JunkCandidateModel(
    val path: String,
    val name: String,
    val sizeBytes: Long,
    val modifiedMillis: Long,
    val mimeType: String? = null,
    val source: JunkCandidateSource = JunkCandidateSource.FileSystem,
)

enum class JunkCandidateSource { FileSystem, AppCache, ExternalCache }

internal object JunkClassificationRule {
    fun categoryOf(candidate: JunkCandidateModel, nowMillis: Long): JunkCategory? {
        if (candidate.sizeBytes <= 0) return null
        if (FileClassificationRule.shouldSkipPath(candidate.path, candidate.name)) return null
        val extension = candidate.name.substringAfterLast('.', "").lowercase(Locale.US)
        return when {
            extension == "apk" -> apkCategory(candidate, nowMillis)
            candidate.source != JunkCandidateSource.FileSystem -> JunkCategory.CACHE
            extension in TEMP_EXTENSIONS -> JunkCategory.TEMP
            else -> null
        }
    }
}
```

要点：

- `nowMillis` 显式传入，测试可固定。
- 规则不读磁盘，只吃候选模型。
- data 侧的 `File` 只用于枚举和读 `lastModified/length`。

### 8.2 MVI 状态所有权（Phase 3）

```kotlin
data class JunkCleanUiState(
    val phase: JunkCleanPhase = JunkCleanPhase.Idle,
    val lastScan: JunkScanResultModel? = null,
    val pendingCleanFiles: List<JunkFileModel> = emptyList(),
    val execution: JunkCleanExecution = JunkCleanExecution(),
    // ...
)

class JunkCleanViewModel(...) : MviViewModel<...>(JunkCleanUiState()) {
    // 允许：资源句柄与取消信号
    private var scanJob: Job? = null
    private var cleaningJob: Job? = null
    private val stopRequested = AtomicBoolean(false)

    // 不再允许：lastScanResult / pendingCleanFiles / cleaningExecutionState / @Volatile stopCleaning
}
```

公共 Action 只表达 UI 事实：

```kotlin
sealed interface JunkCleanAction {
    data object Entered : JunkCleanAction
    data object ScanRequested : JunkCleanAction
    data object CleanClicked : JunkCleanAction
    data object BackClicked : JunkCleanAction
    data class PermissionResultReceived(...) : JunkCleanAction
}

internal sealed interface JunkCleanUpdate { /* ScanStarted / Progress / Completed ... */ }
```

VM 内部调用 `reduceUpdate(...)`，不再把 `ReducerOnly` 暴露给 UI。

### 8.3 门禁负向测试（Phase 1）

每条检查至少要有一个“故意违规”的 fixture。例如：

```kotlin
class DataVisibilityGateTest {
    @Test
    fun `public repository implementation is rejected`() {
        val fixture = tempProject(
            "data/repository/PublicJunkRepository.kt" to
                "package x.data.repository\nclass PublicJunkRepository",
        )
        val result = runRuleCheck(fixture, "dataVisibility")
        assertFalse(result.passed)
        assertTrue(result.messages.any { it.contains("internal") })
    }
}
```

没有负向测试的检查，一律视为“可能空转”。

### 8.4 CI 目标形态（Phase 5）

```yaml
jobs:
  encoding:
    steps: [checkout, setup-java, "pwsh scripts/check-encoding.ps1"]
  rules:
    steps: [checkout, setup-java, "pwsh scripts/check-rules.ps1"]
  style:
    steps: [checkout, setup-java, "./gradlew :app:ktlintCheck detekt"]
  test:
    steps: [checkout, setup-java, "./gradlew :app:testDebugUnitTest koverXmlReport"]
  lint:
    steps: [checkout, setup-java, "./gradlew :app:lintDebug"]
  release:
    steps: [checkout, setup-java, "./gradlew :app:assembleRelease"]
```

要求：`needs` 按依赖编排；失败上传报告；缓存 Gradle；总时长 ≤ 20 分钟。

### 8.5 Compose 版本对齐（Phase 0）

决策树：

1. 如果目标 BOM 同时提供 ui 1.11.x 与 material3 1.4.x → 升级 BOM，删除显式 ui/foundation/runtime 版本。
2. 如果没有这样的 BOM → 删除 BOM，把所有 Compose artifact 锁到同一版本线，并在 `libs.versions.toml` 用 `compose = "..."` 统一引用。
3. 无论哪种，验收命令必须显示 `ui` 与 `material3` 来自同一 release train。

### 8.6 文档恢复（Phase 0）

```powershell
git show 6d9e6df:AGENTS.md > AGENTS.md
git show bfbf15e^:README.md > README.md
git show 6d9e6df:docs/rules/exceptions.md > docs/rules/exceptions.md
```

注意：

- 不要用 Windows PowerShell 5.1 的默认编码管道；用 `git show` 重定向或 `[IO.File]::WriteAllText`。
- 合并 E1/E2 后，用 `check-encoding.ps1` 校验；任何 U+FFFD 都必须修掉。
- 若不想恢复 `docs/`，把 `docs/architecture.md` 放根目录并同步 `AGENTS.md` 链接。

## 9. 验证矩阵

| 检查 | 命令 | 期望 |
|---|---|---|
| 编码 | `.\scripts\check-encoding.ps1` | exit 0，无 U+FFFD |
| 规则 | `.\scripts\check-rules.ps1` | 全 ok，无空转检查 |
| 风格 | `.\gradlew.bat :app:ktlintCheck` | main/test 全绿 |
| 单测 | `.\gradlew.bat :app:testDebugUnitTest` | ≥ 210 通过 |
| Lint | `.\gradlew.bat :app:lintDebug` | 0 fatal |
| Release | `.\gradlew.bat :app:assembleRelease` | 产物存在、可签名校验 |
| domain 纯净 | `git grep -n "java.io.File\|System.currentTimeMillis" -- app/src/main/java/.../domain` | 空 |
| MVI 状态 | `git grep -n "ReducerOnly\|selectedTab: Int\|onAction(intent"` | 空 |
| Compose | `dependencyInsight` | ui/material3 同线 |
| 死脚本 | `Get-ChildItem scripts` | 仅活入口 |
| 文档 | `check-encoding` + 人工打开 | 可读、链接有效 |

## 10. 指标（Before → After）

| 指标 | Before | After |
|---|---|---|
| domain `java.io.File` | 1 | 0 |
| domain `System.currentTimeMillis` 默认值 | 2 | 0 |
| VM 业务状态字段 | 8 + 11 | 0（仅 Job/取消信号） |
| `ReducerOnly` 引用 | 22 | 0 |
| 死脚本 | 5 | 0 |
| 失明检查 | 3 | 0（含负向测试） |
| ktlint test 源集 | FAIL | PASS |
| CI 门禁步骤 | 3 | 7 |
| 未用版本别名 | 8 | 0 |
| Compose 错配 | ui 1.11.2 / material3 1.3.1 | 同版本线 |
| 编码异常文件 | 11（7 个 KDoc 乱码 + 2 个非 UTF-8 + 2 个文档乱码） | 0 |
| 单测数量 | 172 | ≥ 210 |
| domain 覆盖率 | 未统计 | ≥ 70% |
| AppNavHost | 643 行 | ≤ 4 个图，每个 ≤ 250 行 |

## 11. 时间表与人力

### 1 人（推荐按此排期）

| 周 | 内容 |
|---|---|
| 第 1 周 | P0 + P1（文档/构建/门禁） |
| 第 2 周 | P2（领域规则回收） |
| 第 3 周 | P3（MVI 状态收口） |
| 第 4 周 | P4 + P5（导航/DI/CI/文档） |
| 第 5 周 | buffer、回归、P6 决策 |

### 2 人

- 第 1 周：P0 + P1 共同完成。
- 第 2–3 周：A 做 P2，B 做 P3（互不冲突，P3 依赖 P2 的测试约定但不依赖代码）。
- 第 4 周：P4/P5 分工，合并回归。

> 预估为工程日，不含产品验收和商店提审。

## 12. 风险与回滚

| 风险 | 影响 | 缓解 |
|---|---|---|
| 扫描行为变化 | 清理结果/删除范围变化 | 先做 golden 测试，逐 scanner 切换，不改阈值 |
| MVI 重构引入时序 bug | effect 丢失/重复导航 | 先补特征测试；按页面拆 PR；保留旧实现一个版本 |
| Compose 升级破坏 UI | 页面样式/运行时崩溃 | 独立 PR；截图冒烟；必要时回退 BOM |
| 门禁过严阻塞开发 | 合并变慢 | baseline + 只拦新增；规则分级（error/warn） |
| 文档恢复内容错误 | 规则失真 | 从干净提交恢复 + diff review + 编码校验 |
| 时间超支 | 交付延期 | 阶段门禁；P3 后可暂停；P6 可延后 |
| 规范脚本迁移遗漏规则 | 边界退化 | 删除前逐条列出规则并迁到 JUnit/ADR |

**回滚原则**：每个 PR 独立可 revert；不做数据库/协议变更；不在同一 PR 混“重构 + 行为修改”。

## 13. Definition of Done

- [ ] `AGENTS.md`、`README.md`、`docs/architecture.md` 均为严格 UTF-8，无 U+FFFD，无自引用。
- [ ] `scripts/` 只保留实际接线脚本；5 个死脚本已删除。
- [ ] `check-rules` 的每条检查都有负向测试；`dataVisibility`/`functionLength`/`localeParity` 不再空转。
- [ ] `ktlintCheck` main/test 全绿，并在 CI/pre-commit 中执行。
- [ ] CI 覆盖编码/规则/风格/单测/lint/release，PR 全绿。
- [ ] domain 无 `java.io.File`、`System.currentTimeMillis`、`android.*`。
- [ ] JunkClean/VirusScan 的 VM 只保留 Job/取消信号；`ReducerOnly` 删除。
- [ ] `Home` 使用 `MainTab`，无 `Int` tab；`onAction(action)` 统一。
- [ ] `AppNavHost` 拆分后每个图 ≤ 250 行；Koin `checkModules` 通过。
- [ ] Compose ui/material3 同版本线；8 个未用别名删除。
- [ ] 单测 ≥ 210，domain 覆盖率 ≥ 70%，关键导航/DI 冒烟通过。
- [ ] release 产物可构建、可签名校验；ADR 记录模块化决策。

## 附录 A：现状证据索引

| 问题 | 位置 |
|---|---|
| dataVisibility 空转 | `scripts/check-rules.ps1:97` |
| functionLength 正则漏函数 | `scripts/check-rules.ps1:372` |
| localeParity 只查 key | `scripts/check-rules.ps1:200` |
| 幽灵模块脚本 | `scripts/check-mvi-boundaries.ps1:5-7` |
| PS 5.1 不兼容 | `scripts/check-dependencies.ps1:12` |
| 分类策略在 data | `data/local/junkclean/scanner/ApkScanner.kt:12-27` |
| domain 触盘 | `domain/rule/FileClassificationRule.kt:87-91` |
| domain 时间默认值 | `domain/model/junk/JunkFileModel.kt:15`、`domain/port/BatteryHistoryRepository.kt:27` |
| JunkClean 状态字段 | `app/view/junkclean/interaction/JunkCleanViewModel.kt:42-52` |
| VirusScan 状态字段 | `app/view/antivirus/interaction/VirusScanViewModel.kt:35-53` |
| MVI 基类问题 | `base/mvi/MviViewModel.kt:29,45-57` |
| Home Int tab | `app/view/home/interaction/HomeContract.kt:16-31` |
| NavHost 规模 | `app/root/AppNavHost.kt`（643 行） |
| 死全局/绑定 | `app/MyApp.kt:11-24`、`app/di/DataModule.kt:52-62` |
| Compose 错配 | `gradle/libs.versions.toml:4-20` |
| 文档损坏 | `AGENTS.md`、`README.md`（HEAD `412cf00`） |
| 模板残留 | `app/src/test/.../ExampleUnitTest.kt`、`app/src/androidTest/.../ExampleInstrumentedTest.kt` |

## 附录 B：常用命令

```powershell
# 基线
.\scripts\check-rules.ps1
.\scripts\check-encoding.ps1
.\gradlew.bat :app:ktlintCheck
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:lintDebug
.\gradlew.bat :app:assembleRelease

# 依赖
.\gradlew.bat :app:dependencyInsight --dependency androidx.compose.ui:ui --configuration debugRuntimeClasspath
.\gradlew.bat :app:javaToolchains

# 文档恢复（Phase 0）
git show 6d9e6df:AGENTS.md > AGENTS.md
git show bfbf15e^:README.md > README.md
git show 6d9e6df:docs/rules/exceptions.md > docs/rules/exceptions.md

# 规则扫描
git grep -n "java.io.File" -- app/src/main/java/com/aicleanerstorage/spaceclean/domain
git grep -n "System.currentTimeMillis" -- app/src/main/java/com/aicleanerstorage/spaceclean/domain
git grep -n "ReducerOnly"
git grep -n "selectedTab: Int"
git grep -n "onAction(intent"
```
