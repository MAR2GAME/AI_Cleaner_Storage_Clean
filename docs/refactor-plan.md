# Storage Clean 重构实施计划（v3 执行台账；保留 v2 基线）

## v3 执行依据（2026-11-03）

以下 v3 约束及进度优先于后文保留的 v2。v2 的人日、P0 清零承诺、模块创建时机和已被推翻的审计结论不再作为执行依据。

- 当前工作分支：`codex/refactor-v3`，由 `fix/tp` / `da1d7eb` 创建。
- 用户选择完整重构路线，并要求分阶段执行；本批只处理重复文件数据安全。
- 保留 junkcc、有效产品配置及现有未提交改动；不轮换密钥，不增加广告展示入口，不处理代码注释。
- 先在 app 内建立 core/shared/feature 包边界，再迁移业务、DI、持久化和类型安全导航，最后拆 Gradle 模块。
- 不把所有架构偏好列为 P0；不为未证实的取消删除问题清状态，不重复添加已存在的 Crashlytics 运行时。
- 不在权限设置页停留期间设置绝对超时；不在宿主销毁时执行旧页面广告 continuation；不以 PIN 迁移失败解除保护。
- 不直接删除 QUERY_ALL_PACKAGES；验证杀毒和应用锁用途。本轮不增加 WakeLock 无限续期。
- 只做与改动相关的检查；发布节点再进行 release 与真机集成验证，不每日无差别全量构建。

### 阶段与顺序

| 阶段 | 交付 | 当前状态 |
|---|---|---|
| A1a | 两套重复文件判定与删除前安全校验 | 本批代码完成，19 条针对性测试通过；真机待验收 |
| A1b | 区分扫描失败、部分成功与空结果，覆盖目录枚举／MediaStore 错误 | 待执行；本批没有宣称扫描错误模型已收敛 |
| A2 | 广告 operation ID、去重、超时、宿主取消和迟到回调 | 待执行 |
| A3 | 权限明确结果与设置返回恢复 | 待执行 |
| A4 | busy 所有权与字符串导航语义 | 待执行 |
| A5 | PIN 全入口验证、原子迁移与备份排除 | 待执行 |
| A6 | 错误资源化、日志脱敏、网络配置及后台恢复 | 待执行 |
| B | 修门禁、基础包边界与最小 CI | 待执行；CI runner 需能访问现有内网 Maven |
| C | MVI、可取消 gate 与 settings/junkclean 试点；DI 随迁 | 待执行 |
| D | files → toolbox → startup/onboarding → home → applock → antivirus → notificationcleaner | 待执行 |
| E | 运行时单例与宿主注册、存储迁移、设计系统和平台边界 | 待执行 |
| F | 类型安全路由、Home owner、导航回执与外部入口恢复 | 待执行 |
| G | core → shared → feature → app，最后拆 Gradle 模块 | 待执行 |

### 本批 A1a 的行为与验收

1. 两套算法只按成功的非空内容哈希分组。一个文件哈希失败不污染同大小的其他文件；垃圾扫描器超过 100 MiB 的文件保留跳过策略，不能成为重复删除候选。
2. 扫描结果携带哈希及保留副本信息。删除前重新校验内容；原件变化、消失、无法读取或被同时选中时跳过。垃圾清理还排除原件所在目录被选中的情况。
3. 重复文件页不再提交 `MediaStore.createDeleteRequest`，避免系统先删除再回调校验。应用重新校验后执行直接删除；没有相应权限时保留文件、报告未完成数量。垃圾重复文件也不进入系统代删授权队列。
4. 哈希及垃圾目录遍历响应协程取消；文件路径哈希前后比较大小和修改时间。普通文件管理页继续使用现有删除流程。
5. 回归范围：`DuplicateFileGroupingTest`、`FileContentHasherTest`、`DuplicateContentGuardTest`、`DuplicateFileSafetyTest`、`DuplicateDeletionSafetyTest`、`DuplicateFilesWarningStateTest`，合计 19 条，失败 0。
6. 测试任务包含必要 Kotlin 编译，未执行 assembleRelease、全量测试或格式化。

实现边界：跨应用并发修改下，哈希检查与删除不是文件系统原子事务，仍存在极短的检查／使用时间窗口；本批不承诺消除外部进程的所有并发修改。新增校验会增加删除前读取时间。Android 真机上的权限、Toast 和文件保留行为仍需验收。

本机 Gradle 默认启动出现 Windows Unix-domain socket `Invalid argument: connect`。本批通过一次性 JVM 参数让本地管道回退到 TCP，未改仓库 Gradle 配置。用于测试的启动方式（下列目录必须不存在）：

```powershell
& 'C:/Program Files/Android/Android Studio/jbr/bin/java.exe' '-Djdk.net.unixdomain.tmpdir=D:/pros/my/AI_Cleaner_Storage_Clean/.gradle/no-unix-sockets' -classpath gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain :app:testDebugUnitTest --tests '*DuplicateFileGroupingTest' --tests '*FileContentHasherTest' --tests '*DuplicateContentGuardTest' --tests '*DuplicateFileSafetyTest' --tests '*DuplicateDeletionSafetyTest' --tests '*DuplicateFilesWarningStateTest' '-Dorg.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8 -Djdk.net.unixdomain.tmpdir=D:/pros/my/AI_Cleaner_Storage_Clean/.gradle/no-unix-sockets' --console=plain
```

完整路线估算 58～87 人日，加约 20% 集成余量约 14～21 周；每阶段完成后更新剩余估算，不承诺 v2 的 W1 可发版。

---

## v2 基线（历史内容，冲突处以以上 v3 为准）

- **版本**：v2，基于 v1 评审结论修订（5 项决策已定，见 §1.3）
- **基线**：git `da1d7eb`（`fix/tp` 分支），单模块 406 kt / 42,532 行
- **执行资源**：**1 人 + AI 协作**（§5 给出按周排期与 AI 分工）

---

## 0. 本文档怎么用

| 你想知道的 | 看哪节 |
|---|---|
| 决策结论落地成了什么 | §1.3 / §1.4 |
| 为什么要这个顺序 | §2 总体路线 |
| 具体先干什么、多大工作量 | §3 Phase 明细 |
| 10 个 feature 谁先谁后 | §4 Feature 迁移顺序 |
| 单人怎么排期、AI 干什么 | §5 周排期与 AI 分工 |
| 怎么算"改完了" | §6 验收度量 |
| 可能出什么事 | §7 风险登记册 |
| 哪些事**不要**做 | §8 反向清单 |
| 明天就能开的 PR | §9 第一批 PR |

---

## 1. 目标、原则与约束

### 1.1 三个目标（按优先级）
        
1. **可靠性**：消掉 原排查报告 §1 中 11 类 P0 里的 10 类（P0-5 因决定保留 `:junkcc` 转为已接受债务）。这些不是架构洁癖，是「全站导航卡死 / 只能杀进程 / 可能删错文件 / 审核风险」的实际故障。
2. **可演进**：把「单模块 + 无 domain + 无 MVI 基类 + 双向依赖」变成「core/shared/feature 分层 + 统一 MVI 契约 + 拦截器承载横切」。
3. **可验证**：让门禁真实生效（当前 5 个 check 脚本全部空转）。没有这一条，前两条会在 3 个月内退化回原点。

### 1.2 五条硬原则

| # | 原则 | 为什么 |
|---|---|---|
| R1 | 一次只改一层：类型改名不与行为修改同 PR | 现在零回归网（33 个测试只跑过 1 个） |
| R2 | **先搬家，后改形状** | `AppDestination` 先只改 `package` 解 94 处反向依赖，P7 才类型安全化 |
| R3 | 先补断言，再改实现 | 每 feature 迁移第一步写"旧实现输入 → 关键字段快照" |
| R4 | 每个 PR 可独立合并、独立回滚 | 无 CI 环境下唯一安全网 |
| R5 | 门禁随迁：迁完一个 feature 就把它纳入守卫 | 否则规则永远只是纸面 |

### 1.3 决策结论（已定，v2 按此执行）

| # | 议题 | **决策** | 对计划的影响 |
|---|---|---|---|
| D1 | `:junkcc`（1,800 类 / 900 Activity / 25 MB） | **不删** | 移除原 P0-07；转为**已接受债务 AC-1**（§1.4）。AAB 51.3 MB / release manifest 950 Activity **保持不变** |
| D2 | 母版同步规则（`figma-screen-map.md` Rules、`config/variant-source.json`） | **废止 + 清理** | 新增 **PR-0**（§9）：改掉 Rules 段、清理母版拷贝记录与无主配置。**本决策解锁 P3 及以后的所有工作** |
| D3 | 广告从"业务链路中段"移到"Intent 拦截派发" | **接受短期收入波动** | P2 的 `AdIntentInterceptor` 全量推进，不做灰度开关；但保留远端"广告位总开关"作为紧急刹车 |
| D4 | `product.json` 里已入库的 4 个密钥 | **暂不轮换** | 移除原 P0-14；转为**已接受风险 AC-2**（§7 R-10 给出触发轮换的条件） |
| D5 | 是否真需要多变体（白牌） | **未决（默认按单产品）** | 计划按单产品执行；`product-config.gradle` 的 31 个 `ADV_*` BuildConfig 暂不动，待 D5 明确后处理 |
| D6 | 演进路径 | **单模块内按包演进，最后一步才拆 Gradle** | 与 v1 一致：P6 在 P5 之后 |

### 1.4 已接受的技术债（记录在案，不进重构范围）

| ID | 内容 | 保留原因 | 代价 | 重新评估的触发条件 |
|---|---|---|---|---|
| **AC-1** | `:junkcc`（1,800 java / 900 Activity / 1,802 资源 / proguard 全量 keep） | 决策 D1 | release AAB 51.3 MB；manifest 950 组件；R8 与 `isShrinkResources` 对该模块失效；审核面扩大 | ① Play 审核问询组件清单 ② 需要压体积到某阈值 ③ 构建时长成为瓶颈 |
| **AC-2** | `config/product.json` 明文含 Trustlook key / Singular key+secret / remote-config 加密密钥 / Play Integrity token key，且已进 git 历史 | 决策 D4 | 可被 `apktool d` 提取；git 历史永久留存 | ① 仓库转为公开/多人协作 ② 任一密钥在服务端出现异常调用 ③ 上架前安全审计 |
| **AC-3** | 测试体系薄弱（33 单测 / 1 androidTest / 最大 5 个 VM 共 2,816 行零覆盖） | 现有结构下很多逻辑不可测 | P3 期间只能靠快照断言兜底 | 每个 feature 迁移时自然改善（§4） |
| **AC-4** | `product-config.gradle` 的 31 个 `ADV_*` BuildConfig 字段 + `resolveValue` 允许环境变量覆盖（破坏可复现构建） | 依赖 D5 决策 | 同一 commit 不同机器可能产出不同 APK | D5 明确后统一处理 |

> **AC-1 与重构正交**：junkcc 位于独立 Gradle 模块、零引用、不参与任何 feature 迁移。P0~P7 全程不需要碰它。唯一注意点：`app/build.gradle.kts:64` 的 `implementation(project(":junkcc"))` 保持不变。

---

## 2. 总体路线

### 2.1 八阶段图（v2）

```
PR-0 决策落地       ── 废止母版同步规则 + 清理母版拷贝记录（0.5 人日）
      │
P0  止血            ── 12 个工单，不碰架构，2~3 人日，10/11 类 P0 清零
      │
P1  地基            ── core:foundation / logging / presentation / navigation(搬家版) + 最小 CI + 修门禁
      │                退出条件：feature 不再 import app.*；日志统一出口；CI 绿
      │
P2  MVI 骨架 + 拦截器 ── MviViewModel 基类 + IntentPolicy 四类 + 3 个拦截器
      │                退出条件：新页面只能按新契约写；旧页面并存过渡
      │
P3  逐 feature 迁移  ── 10 个 feature 按 §4 顺序，每个 10 步
      │                退出条件：featureFlow / permissionCoordinator / AnalyticsTracker 三处归零
      │
P4  DI 收口         ── per-feature module；AdRuntime 单例；删手工 remember{new}
      │
P5  物理分层        ── core:designsystem/permission/preferences/security/monetization/device + shared:*
      │
P6  拆 Gradle 模块   ── 10 个 feature:* 独立；app 只做装配
      │
P7  路由类型安全 + 收尾 ── @Serializable 路由、删 withArgs/detailPattern、回执机制、返回键统一
```

### 2.2 为什么是这个顺序

| 依赖 | 说明 |
|---|---|
| **PR-0 必须最先** | D2 已决：母版同步规则若还在，P3 之后每次改动都会被"同步 `Quick_Clean_Pro_s`"打回 |
| P0 先于一切架构工作 | 10 类 P0 是每天在流血的故障，且与架构无关，纯 bugfix |
| P1 先于 P2 | 拦截器要收编权限/广告/打点，这三者现在都在 `common/` 里反向依赖 `app.navigation`；不先解 94 处，`core:presentation` 落不了地 |
| **P1 的 `core:navigation` 只搬家** | 关键取舍：111 文件依赖 `AppDestination`，先只改 `package`（机械替换、编译期验证），类型安全化留到 P7 |
| P2 先于 P3 | 没有基类就没有"往哪儿迁"；没有拦截器，迁移完的 VM 还得继续拿 `permissionCoordinator` 参数 |
| P3 先于 P4 | DI 模块化只有在"每 feature 有 domain service + 五件套"之后才有意义 |
| P5 先于 P6 | `core:*` 与 `common` 拆完才有物理边界可依 |
| P7 最后 | 机械量最大（111 文件）但风险最低；提前做与 P3/P5 搬迁反复冲突 |

---

## 3. Phase 明细

### PR-0 · 决策落地（0.5 人日）

| 工单 | 内容 | 验收 |
|---|---|---|
| PR0-1 | `docs/figma-screen-map.md` 的 `Rules` 段：删除 "Keep device data, ViewModels, repositories, navigation, and effects **from the mother project**"，改为"以本仓库当前实现为准；母版不再作为同步来源" | 文档内无 "mother project" 表述 |
| PR0-2 | 删除 `config/variant-source.json`（母版拷贝记录，含 `D:/pros/my/...` 绝对路径，无任何代码读取） | `grep -r variant-source` = 0 |
| PR0-3 | 清理无主配置：`config/product.schema.json`（5.7 KB，无人校验）、`docs/testtt.md`（0 字节） | 二者删除 |
| PR0-4 | `docs/figma-screen-map.md` 补一句"当前进度以 原排查报告 §2 章节索引为准"，避免与新文档冲突 | — |

> `docs/ice_age_survival_game_design_and_development.md` 已处于"已暂存删除"状态（非本次改动），按你的清理意图保留该删除。

### P0 · 止血（2~3 人日 · 无架构改动）

| 工单 | 内容 | 涉及 | 规模 | 验收 |
|---|---|---|---|---|
| P0-01 | 插屏加硬超时（8~15s）+ 超时强制重置队列 + 上报 `ad_timeout` | `common/ads/AdRuntime.kt:178-208` | S | 模拟 SDK 不回调 → 导航仍能继续 |
| P0-02 | `enqueue` 去重改**合并回调**（`Map<id, List<() -> Unit>>`）；拒绝分支必须显式 `onContinue()` | `AdRuntime.kt:156-176` | S | 同一入口连点两次 → 两次都有响应 |
| P0-03 | `dispose()` 清空 `activeRequest`/队列/`knownRequestIds`，按"未展示"完成待决回调 | `AdRuntime.kt:154` | S | Activity 销毁后再返回不卡死 |
| P0-04 | `PermissionCoordinator.startSession` 的 `if (session != null) return` → 回调 `onDenied(Busy)`；会话加 20s 绝对超时 | `app/permission/PermissionCoordinator.kt:97-104,143-153` | M | 连续两次 `ensure` 不丢回调；打开设置页不返回也能恢复 |
| P0-05 | `RuntimeBusyReason` 增加 `reset()`，目标路由变化时按"离开该 feature"自动清位 | `app/runtime/AppSessionCoordinator.kt` | S | 扫描中切走再回来，广告仍能展示 |
| P0-06 | `FileManagerMessages.appString` 改真读资源；`cancelDelete()` 一并清 `blockedPhase` | `FileManagerMessages.kt:11-27`、9 个 `*ManagerViewModel.kt` | S | 中文包下错误文案为中文；取消删除能关掉 |
| P0-07 | `network_security_config.xml` + `usesCleartextTraffic="false"` + 摘掉 `QUERY_ALL_PACKAGES`（`tools:replace` 覆盖 AAR） | `res/xml/`、`AndroidManifest.xml` | M | `aapt dump xmltree` 无 cleartext；无 QUERY_ALL_PACKAGES |
| P0-08 | PIN 改 PBKDF2 哈希（含一次性迁移：读明文→哈希→覆写→删明文键）；`allowBackup=false` + `dataExtractionRules` 排除 | `AppLockRepositoryImpl.kt:31-36`、`AndroidManifest.xml:39` | M | 老用户升级后 PIN 仍可验证；备份不含 PIN |
| P0-09 | `WAKE_LOCK` 随采样启停续期；`lockScreenShowing` 由 Service 生命周期回执复位 | `ForegroundServiceNotificationController.kt:128-139`、`AppLockMonitorHandler.kt:97-104` | M | 后台 15 分钟后 AppLock 仍生效；浮层被回收后能恢复 |
| P0-10 | `ad_policy.json` 全部 `interval_seconds ≥ 30`（开屏 ≥ 60）+ 删 `AdScene.PermissionRejected` 广告路径 | `config/ad_policy.json`、`FeatureEventAdPolicy.kt:21-24` | S | 拒绝权限后立即返回不再出插屏 |
| P0-11 | banner/native/rewarded 换真广告位（至少 banner 恢复可作降级） | `config/product.json` | S | 三处非 `3940256099942544` |
| P0-12 | 加 Crashlytics 运行时依赖；`VirusSecurityRepositoryImpl.logState` 去掉 key 后缀 | `app/build.gradle.kts`、`VirusSecurityRepositoryImpl.kt:55-61` | S | 制造一次崩溃能在后台看到 |

> **v2 变更**：删除原 P0-07（删 junkcc，见 AC-1）、原 P0-14（密钥轮换，见 AC-2）。原 P0-08~13 顺延编号。

**退出标准**：`assembleRelease` 通过；AC-1 之外的 P0 全部有可复现验证；Crashlytics 能收到一条测试崩溃。

**不做**：任何包结构、任何 DI、任何 MVI 改动；任何对 `:junkcc` 的操作。

---

### P1 · 地基（5~8 人日，AI 可压缩机械部分）

| 工单 | 内容 | 规模 | AI 可承担比例 | 验收 |
|---|---|---|---|---|
| P1-01 | 新建 `:core:foundation`（`AppDispatchers` 接口、`FileSizeFormatter`、`Clock`）+ `:core:logging`（`LogEvent`/`LogLevel`/`LogSink`/`EventLogger`，纯 JVM） | M | 高 | 两 module 编译通过，logging 无 Android 依赖 |
| P1-02 | app 侧 `AndroidLogSink`（JSON 转义 + 字段白名单）；替换 `app/*` 的裸 `Log`；`androidLogger()` 加 DEBUG 门禁 | M | 高 | `grep -rc "android.util.Log" app/` 收敛到 ≤ 6 |
| P1-03 | **`:core:navigation`（搬家版）**：`AppDestination`(447 行)、`AppNavigator`、`AppRouteTransitions` **原样**搬入，只改 `package`；111 个 import 机械替换 | **L** | **极高**（纯机械） | `feature/**`、`common/**` 对 `spaceclean.app.navigation` 的 import = 0；`assembleDebug` 通过 |
| P1-04 | `NotificationAlias` / `notificationTargets` 注册表随迁（行为不变） | S | 高 | `AppDestinationTest` 全绿 |
| P1-05 | 新建 `:core:designsystem` 骨架（先只放 `common/ui/theme/**`），`Theme.kt` 不再依赖 `BuildConfig.PRODUCT_THEME_KEY`（改由 app 注入 token 集合） | M | 中 | `core:designsystem` 不 import `BuildConfig` |
| P1-06 | `:core:presentation` 只放 `IntentPolicy`/`InterceptResult`/`IntentInterceptor`（基类留 P2） | S | 高 | 编译通过 |
| P1-07 | **修 5 个 check 脚本**：3 个白牌遗留断言重写为当前产品；2 个把 `use/skin|use/feature|core/*` 改为真实路径、`rg` 换 `Select-String` | M | 高 | 5 个脚本在当前仓库**全部 exit 0** |
| P1-08 | 新增 `check-module-boundaries.ps1`（解析 `build.gradle.kts` 的 `project(":...")`）+ `check-page-boundaries.ps1`（Route/Screen 签名） | M | 高 | 能捕获"故意注入的违规" |
| P1-09 | 最小 CI（`.github/workflows/ci.yml`）：`assembleDebug` → `testDebugUnitTest` → `ktlintCheck` → 上述脚本 | M | 中 | PR 跑通；故意破坏能被拦 |
| P1-10 | 修 `MotherTemplateBoundaryTest` 的 4 个恒真断言（`logic/` → `domain/`、`ui/*ViewModel` → 真实路径） | S | 高 | 6/6 断言真实生效 |

**退出标准**：`feature`/`common` → `app.*` 反向依赖 = 0；CI 绿；5 个门禁脚本真实通过。

---

### P2 · MVI 骨架 + 拦截器（5~7 人日）

| 工单 | 内容 | 规模 | 验收 |
|---|---|---|---|
| P2-01 | `MviViewModel<S,I,E>`（`dispatch`/`dispatchInternal`/`onIntent`/`updateState`/`emitEffect`/`policiesFor`/`canDispatch`/`onIntentBlocked`/`onCleared` 关 channel） | M | 基类单测覆盖 5 个场景（正常/Blocked/Deferred 成功重派/Deferred 取消不重派/`onCleared`） |
| P2-02 | `AuthGate` + `Deferred` 重派（`pendingResumptions`/`completedResumptions` 防重入） | M | 同上单测 |
| P2-03 | `PermissionIntentInterceptor`（`PermissionRequired(action)` → `PermissionEngine` → Granted=Proceed / 需 UI=Deferred） | M | 26 处 `ensure{}` 中替换 1 处即见效 |
| P2-04 | `AdIntentInterceptor`（`ShowAd(placement)` → 带超时的调度器 → Proceed；与 P0-01 共用同一实现） | M | 广告回调丢失时导航仍继续 |
| P2-05 | `TrackIntentInterceptor`（Intent 级打点，替代 Route 层 7 处） | S | 同一 Intent 只上报一次 |
| P2-06 | 模板落地：`templates/feature/{presentation,ui}/*` 按新契约改写 | S | 按模板新建空 feature 能编译 |
| P2-07 | 架构守卫 `MviContractTest`（先 `@Ignore`，P3 起逐 feature 启用） | M | 对"故意违规"能报错 |

**退出标准**：基类 + 3 个拦截器有单测；模板可用；新旧写法可并存。

**D3 落地注记**：`AdIntentInterceptor` 全量启用（不灰度），但保留远端 `global_ad_switch` 作为紧急刹车；上线后 2 周内对比「操作完成率」与「插屏曝光率」。

---

### P3 · 逐 feature 迁移（25~35 人日 · 主体）

每个 feature 走同一套 10 步，**每步一个 PR**：

| 步 | 内容 | 谁来主导 |
|---|---|---|
| 1 | **先补回归快照测试**（现有 `_uiState.update` 行为 → "输入 → 关键字段"快照） | AI 生成，人确认语义 |
| 2 | 建 `domain/model` + `domain/service`（把 VM 里的编排搬出去） | 人设计边界，AI 搬运 |
| 3 | `domain/error/XxxErrorCode` + `AppError` 替换 `@StringRes`/`error.message` | AI |
| 4 | `Contract`（`XxxUiState` + sealed `Phase` + **唯一** `sealed XxxIntent` + `sealed XxxEffect`） | 人设计，AI 落地 |
| 5 | `Reducer`（`internal object`，纯函数，无 I/O/格式化/时间） | 人设计，AI 实现 |
| 6 | `ViewModel`（继承基类，`policiesFor` 声明权限/广告/打点） | 人 |
| 7 | `Route`（`onIntent = viewModel::dispatch`）+ `Screen`（只吃 state + onIntent） | AI 为主 |
| 8 | 删该 feature 的 `featureFlow`/`permissionCoordinator`/`analyticsReporter` 参数（含 `register*Routes` 同步） | 人 |
| 9 | 启用该 feature 的 `MviContractTest` 范围 | AI |
| 10 | 独立 PR 删旧类型与死代码 | AI |

**三个特殊 feature 的额外步骤**

| feature | 额外步骤 |
|---|---|
| `files`（9 个雷同页） | **先合并成一个参数化 `FileListFeature`**（`ManagedFileType` + `FileManagerFilters`），再走 10 步 → 9 份工作量变 1.3 份。`duplicates`/`similarphotos`（分组语义）、`photoprivacy`（权限语义）保留独立 |
| `antivirus`（25.7 KB 最大 VM） | 拆 `VirusScanSession`（6 个 Job + `scanGeneration` → `sealed ScanSessionState`）；`currentIcon: Drawable?` → `iconKey: String?`；双 StateFlow → 两个页面或嵌套子状态 |
| `notificationcleaner`（26.3 KB + 广告状态混入业务） | 5 个 generation token → 一个 `sealed ScanSession`；`completionAdInFlight`/`postCompletionPage` 移出业务状态机 |

---

### P4 · DI 收口（3~5 人日）

| 工单 | 内容 | 规模 |
|---|---|---|
| P4-01 | 拆 `DataModule.kt`(99) / `PresentationModule.kt`(85) → 每 feature 一个 `XxxFeatureModule`；app 只 `modules(all)` | M |
| P4-02 | `viewModelOf(::X)` / `singleOf(::X)` 替代 25 处位置参数 `get()` | M |
| P4-03 | 删 80 处 `Dispatchers.*` 字面量，全部走注入 `AppDispatchers` | M |
| P4-04 | `AnalyticsTracker` object → `class`（构造注入 sink/preferences/clock），删 `initialize()` | M |
| P4-05 | `AdvertiseSdkAdapter`/`AdvertiseConfigFactory`/`AdPlacementRegistry` object → `class` + `single` | M |
| P4-06 | 7 个 `object *DataSource` → `class`（构造注入 resolver/dispatcher/clock） | L |
| P4-07 | `AdRuntime` 进程级单例（Koin `single`）；`OpenAdHostActivity` 注入而非 `new` | M |
| P4-08 | 删 `AppPrefsUtils` / `AppLockManager` / `MyApp.instance` | S |
| P4-09 | 加 `checkModules()` 单测 | S |

---

### P5 · 物理分层（8~12 人日）

| 工单 | 内容 | 规模 |
|---|---|---|
| P5-01 | `:core:designsystem` 吸收 `common/ui/**`（41 文件）+ 统一 token（15 个 token 文件 → 1 套 + 每主题一份） | L |
| P5-02 | `:core:permission` 吸收 `common/permission/**`（11 文件）；`POST_NOTIFICATIONS` 并入 `PermissionType`，删 `NotificationPermissionPolicy` 重复部分 | L |
| P5-03 | `:core:preferences`：3 个 SharedPreferences 键空间 → DataStore + 每域 port + 迁移 | L |
| P5-04 | `:core:security`：`KeystoreCipher` + `PinHasher`（P0-08 的实现归位） | M |
| P5-05 | `:core:monetization` 吸收 `common/ads`+`common/analytics`+`common/commercial`（唯一允许 import 三方 SDK 的模块） | L |
| P5-06 | `:core:device` 吸收 `toolbox/shared/{device,network}`（解 12 处跨 feature） | M |
| P5-07 | `:shared:filestore` 吸收 `files/shared/**`（25 文件，解 `toolbox→files` 3 处） | L |
| P5-08 | `:shared:clean` 吸收 `junkclean/scanner/**` + `model/**` + `CleanRepository`/`CleanSessionStore` | L |
| P5-09 | `:shared:session` 吸收 `app.startup.AppLaunchRequest`/`NotificationLaunchSource` | S |
| P5-10 | `SettingsRateDialog`/`openGooglePlayRatePage` → `core:designsystem`（解 `home→settings` 5 处） | S |

---

### P6 · 拆 Gradle 模块（8~12 人日）

按耦合度从低到高，每拆一个 module 跑一次全量门禁：

```
settings → startup → applock → antivirus → notificationcleaner
→ toolbox → home → onboarding → files → junkclean
```

> **注意**：`:junkcc` 保持在 `settings.gradle.kts` 中且被 `:app` 依赖（AC-1），拆其他模块时不动它。

---

### P7 · 路由类型安全 + 收尾（8~12 人日）

| 工单 | 内容 | 规模 | 验收 |
|---|---|---|---|
| P7-01 | `:app` 启用 `kotlin-serialization` 插件 + `kotlinx-serialization-json`（版本目录 `libs.versions.toml:64,85` 已有，当前未被使用） | S | 编译通过 |
| P7-02 | `AppDestination` → `@Serializable sealed interface`，39 目的地逐个迁；`composable<T>` + `toRoute<T>()` | L | 全量替换，编译期保障 |
| P7-03 | 删 `withArgs`/`withDetailInitialIndex`/`detailPattern`/`arguments?.getString(...)` | S | 4 处归零 |
| P7-04 | `AppNavigator` 收敛为 6 方法（删 `openRoute`/`resetToRoute`/`openNotificationTarget`）；`resetTo` 加 `require` + 保根 `popUpTo`；`back()` 改 `previousBackStackEntry == null` 判定 | M | D-①-1/①-2 消失 |
| P7-05 | 三个 Home 目的地 → 单一 `Home(initialTab)`；`navigateTopLevel` 语义 | M | 只有一个 `HomeViewModel` 实例 |
| P7-06 | `NavHostControllerAppNavigator` 清空 `runCatching`（显式分支 + `onFailure` 上报） | M | `grep -c runCatching app/navigation` = 0 |
| P7-07 | `NavigationTicket + Deferred<NavigationReceipt>` 回执机制 | L | 删文件后刷新上一页走回执而非 `resetTo` |
| P7-08 | `ExternalTargetCoordinator`（FIFO + 持久化 + `awaitReady` + ack），合并 4 路通知入口；排除 `OnboardingScan` | L | 深色模式切换不跳页；Onboarding 期间通知不打断 |
| P7-09 | 返回键统一：NavHost 级一个 `BackHandler` + 每页 `BackIntent` | M | 5 个缺 `BackHandler` 的目的地不再绕过 `back()` |
| P7-10 | 测试补齐：`TestNavHostController` 行为测试 + `AppDestination.all` 注册完整性 + 通知入口三场景 | L | 覆盖 D-①-3/①-4/①-5 |

---

## 4. Feature 迁移顺序与理由

| # | feature | 规模 | 位置理由 |
|---|---|---|---|
| 1 | `splash` | 38 行 | 已有纯 reducer（`SplashStateMachine`）+ 18 个既有测试 → **零风险验证基类** |
| 2 | `notificationpermission` | 105 行 | 已有纯 reducer + 355 行测试 → 验证"拦截器接管权限流" |
| 3 | `junkclean` | 359 行 | 已有 Reducer 但是三套类型 + `PendingIntent` 进 Contract → 验证"合并类型 + Android-free" |
| 4 | **`files`（9 页合并）** | 最大收益 | 一次消掉 9 处 `displayState` 合成 + 4 个用户可见 bug + 9 份复制 |
| 5 | `toolbox`（battery/appusage/networkusage/networkscan/networkspeed/deviceinfo/whatsapp） | 中 | 5 个"多布尔"状态 → sealed Phase；验证状态建模 |
| 6 | `settings` | 小 | 依赖少，快速清尾 |
| 7 | `onboarding` | 小 | 依赖 `toolbox`（P5-06 后干净） |
| 8 | `home` | 中 | 聚合页，依赖 4 个 feature（P5 之后才干净） |
| 9 | `applock` | 中 | 平台耦合重（Service/Overlay/PIN 迁移） |
| 10 | `antivirus` | 最大 VM | 风险最高，此时基类/拦截器/门禁均成熟 |
| 11 | `notificationcleaner` | 26 KB | 广告状态与业务状态机耦合最深 |

---

## 5. 周排期与 AI 分工（单人）

### 5.1 排期（单人 + AI，共 15 周）

| 周 | 内容 | 交付 |
|---|---|---|
| W1 | PR-0 + P0（12 工单） | **M0：可发版**，10/11 类 P0 清零 |
| W2 | P1 前半（P1-01~04：core 骨架 + navigation 搬家） | P1-03 完成即解除 94 处反向依赖 |
| W3 | P1 后半（P1-05~10：designsystem 骨架 + 门禁 + CI） | **M1：CI + 真实门禁上线** |
| W4 | P2 | **M2：新契约可用** |
| W5 | P3-1 `splash` + P3-2 `notificationpermission` | 基类与拦截器经真实 feature 验证 |
| W6 | P3-3 `junkclean` | 三套类型合并完成 |
| W7~8 | P3-4 `files`（先合并 9 页，再迁移） | **M3：最大收益点**；4 个 bug 清零 |
| W9 | P3-5 `toolbox`（7 个页面） | — |
| W10 | P3-6/7 `settings`+`onboarding` + P3-8 `home` | — |
| W11 | P3-9/10/11 `applock`+`antivirus`+`notificationcleaner` | **M4：三处散点归零** |
| W12 | P4 DI 收口 | — |
| W13 | P5 前半（designsystem/permission/preferences/security） | — |
| W14 | P5 后半（monetization/device/filestore/clean/session） | **M5：core/shared 成型** |
| W15 | P6 拆模块（分批） | — |
| W16~17 | P7 路由类型安全 + 收尾 | **M6：目标架构** |

> 单人不并行的现实约束：W7~8 与 W13~14 是两个人日消耗最大的阶段，若中途插需求会直接顺延。

### 5.2 AI 分工表

| AI 高杠杆（直接交给 AI，人只做验收） | 人必须亲自做 |
|---|---|
| P1-03 的 111 处 import 机械替换与 `assembleDebug` 修编译错 | §1.3 剩余决策（D5）、广告收入影响判断 |
| P3 每 feature 的步骤 1/3/4/5/7/10（快照测试、ErrorCode、Contract+Reducer 落地、Route/Screen 拆分、删死代码） | P3 步骤 2/6/8（domain 边界划分、`policiesFor` 决策、Route 签名收敛时机） |
| P4-03 删 80 处 `Dispatchers.*`、P4-08 删三个 object | P4-07 `AdRuntime` 单例化的生命周期设计 |
| P5 各模块搬运 + import 重写 | P5-03 SharedPreferences → DataStore 的迁移策略（有数据） |
| P7-02 39 个目的地转 `@Serializable` | P7-07/P7-08 回执机制与外部入口协调（并发语义） |
| 所有 grep 类验收断言、门禁脚本、文档同步 | 真机验证：广告、权限、AppLock 浮层、后台 15 分钟行为 |

### 5.3 单人执行的三个纪律

1. **每个 PR ≤ 400 行改动**（AI 生成 ≠ 可以一次 3000 行；无法回滚的改动不许合并）
2. **每天结束前 `assembleDebug` + 单测必须绿**（无 CI 时这是唯一体检）
3. **P3 期间不动 `common/`**（只允许加 adapter），把搬迁集中到 P5，避免与 feature 迁移互相打断

---

## 6. 验收度量

| 指标 | 现状 | M2 | M4 | M6 | 校验 |
|---|---|---|---|---|---|
| 继承统一 MVI 基类的 VM | 0 / 29 | ≥ 3 | 29 / 29 | 29 / 29 | grep |
| 有纯 Reducer 的页面 | 3 | 3 | 100% | 100% | grep |
| VM 直接测试覆盖 | 6 / 29 | 8 | ≥ 20 | ≥ 25 | 统计 |
| `MutableSharedFlow`（易丢事件） | 6 | 4 | 0 | 0 | grep |
| 双 StateFlow 的 VM | 9 | 9 | 0 | 0 | grep |
| Route 签名带 `featureFlow` | 12 | 8 | 0 | 0 | grep |
| `permissionCoordinator.ensure` 散点 | 26 | 22 | 0 | 0 | grep |
| Route 层打点散点 | 7 | 5 | 0 | 0 | grep |
| feature/common → `app.*` 反向依赖 | 94 | 0 | 0 | 0 | grep |
| 跨 feature import | 20 | 20 | 0 | 0 | grep |
| `feature/*/domain/` 目录 | 0 | 3 | 10 | 10 | 目录统计 |
| `runCatching` 总数 | 177 | 160 | ≤ 60 | ≤ 20 | grep |
| 裸 `android.util.Log` | 18 | 6 | 0 | 0 | grep |
| > 400 行的 kt 文件 | 20 | 20 | ≤ 10 | ≤ 5 | 行数统计 |
| release manifest `<activity>` | 950 | **950（AC-1 接受）** | 950 | 950 | merged manifest |
| release AAB | 51.3 MB | **≈ 51 MB（AC-1 接受）** | 同 | 同 | 构建 |
| 可执行门禁脚本 | 0 / 5 | 5 / 5 | 5 / 5 + 2 | 7 / 7 | exit code |
| CI | 无 | 有 | 有 | 有 | `.github` |
| 单元测试文件 | 33 | 40 | ≥ 80 | ≥ 120 | 统计 |

> AC-1 相关指标在 M6 仍为原值——这是决策 D1 的显式代价，不是缺陷遗漏。

---

## 7. 风险登记册（v2）

| # | 风险 | 概率 | 影响 | 缓解 |
|---|---|---|---|---|
| R1 | 广告收入短期波动（P2 把广告移到拦截器） | 高 | 中 | D3 已接受；保留远端 `global_ad_switch` 紧急刹车；上线后 2 周对比「操作完成率」与「插屏曝光率」 |
| R2 | P0-08 PIN 迁移失败 → 老用户 AppLock 被锁死 | 低 | **致命** | 保留 1 个版本双读路径（先试哈希，失败回退明文并立即升级）；迁移失败降级为"未设 PIN"而非崩溃 |
| R3 | P1-03 111 文件机械替换引入编译错误 | 中 | 中 | 单 PR 只改 `package` 与 import；不夹带逻辑；AI 替换后必须 `assembleDebug` 全绿 |
| R4 | P7-02 与 P3/P5 搬迁冲突 | 中 | 中 | P7 之前禁止改 `AppDestination` 形状 |
| R5 | 零回归网导致迁移引入静默行为变化（AC-3） | **高** | 高 | 严格执行 R3：每 feature 步 1 快照断言不可跳过；M3 后做一次全功能回归 |
| R6 | 单人执行被需求打断 → 阶段顺延 | **高** | 中 | §5.1 排期已按顺序设计，任意阶段可停；M0/M3 前不接无关需求 |
| R7 | 无 CI 期间门禁只是纸面 | 中 | 高 | P1-09 是硬门槛，M1 前必须上线 |
| R8 | ~~删除 junkcc 触发资源缺失~~ | — | — | **已消除**（D1 保留） |
| R9 | P3 与 P5 同时改 `common/` | 中 | 中 | §5.3 纪律 3：P3 期间不动 `common/` |
| **R10** | **密钥不轮换（AC-2）导致泄露** | 中 | 高 | 触发条件出现时必须立即轮换：① 仓库转公开/加人 ② 服务端出现异常调用 ③ 上架前安全审计。**缓解**：确认 4 个密钥的服务端权限最小化、开启用量告警 |
| **R11** | **junkcc 保留（AC-1）导致审核问询/体积超标** | 中 | 中 | 触发条件：Play 审核问询组件清单、或体积阈值被触发时，再单独立项评估（删除只需 4 处改动，半天） |

---

## 8. 不建议做的事（反向清单）

| 不做 | 原因 |
|---|---|
| **先拆 Gradle 模块** | 94 处反向依赖未解时拆模块 = 制造循环依赖；必须 P1-03 之后 |
| **一次性重写某个 feature** | 零回归网下无法验证等价；必须按 §3 的 10 步渐进 |
| **先做类型安全路由** | 111 文件 × 与 P3/P5 冲突，机械量最大风险最高 |
| **保留 `FeatureFlow` 并"优化它"** | 它是"业务结果 = 广告回调"这个错误抽象的载体，只能删 |
| **继续往 `common` 加东西** | `common` 已是杂物间（81 文件，`ui` 占 41）；新代码一律进 `core:*`/`feature:*` |
 **先补覆盖率再改架构** | 现有结构下很多逻辑不可测（VM 内联编排、object 单例、Drawable 进 state）；测试随迁移补 |
| **动 `:junkcc`（AC-1）** | 决策 D1 保留；与重构正交，任何阶段都不要碰 |
| **"顺手"重构无关文件** | 破坏 R1/R4；每 PR 单一目的 |

---

## 9. 第一批 PR 清单（决策已定，可立即开工）

| PR | 内容 | 人日 | 依赖 |
|---|---|---|---|
| **#0** | PR-0：废止母版同步规则 + 清理 `variant-source.json`/`product.schema.json`/`testtt.md` | 0.5 | D2 ✅ |
| #1 | `AdRuntime` 插屏超时（P0-01） | 1 | — |
| #2 | `enqueue` 去重合并回调 + `dispose` 清理（P0-02/03） | 0.5 | #1 |
| #3 | `PermissionCoordinator` 会话超时 + `ensure` 不丢回调（P0-04） | 1 | — |
| #4 | `FileManagerMessages` 读资源 + `cancelDelete` 清 `blockedPhase`（P0-06） | 0.5 | — |
| #5 | manifest 安全收敛（P0-07） | 1 | — |
| #6 | PIN 哈希 + 迁移 + `allowBackup=false`（P0-08） | 1.5 | — |
| #7 | `ad_policy.json` 频控 + 删 `PermissionRejected` 广告（P0-10） | 0.5 | D3 ✅ |
| #8 | 广告位换真 + Crashlytics + 去 key 后缀日志（P0-11/12） | 1 | — |
| #9 | `AppSessionCoordinator.reset()` + 路由变化清位（P0-05） | 0.5 | — |
| #10 | `WAKE_LOCK` 续期 + `lockScreenShowing` 回执复位（P0-09） | 1 | — |
| #11 | 修 5 个 check 脚本至真实通过（P1-07） | 1.5 | — |
| #12 | 最小 CI（P1-09） | 0.5 | #11 |

**合计约 10.5 人日 ≈ 2 周**（含真机验证与回滚余量），完成即 **M0 可发版**。

> 顺序建议：#0 → #1 → #2 → #3 → #4 → #5 → #6 → #7 → #8 → #9 → #10 → #11 → #12。
> 其中 #1~#10 互不依赖，可按风险高低穿插；#11/#12 必须在 P1 前完成。

---

## 10. 需要你确认的最后 2 件事

1. **D5（是否真需要多变体/白牌）**——若不需，P5/P6 可顺手清掉 `product-config.gradle` 的 31 个 `ADV_*` BuildConfig 与 `product.schema.json`（已在 PR-0 列）；若需要，则 §3 需新增一个 flavor 化设计工单。
2. **周排期是否接受 15~17 周**（单人 + AI）。若希望压缩，唯一办法是把 P6（拆 Gradle 模块）与 P7（类型安全路由）的优先级下调——这两段共 16~24 人日，且不影响 P0~M4 的收益。
