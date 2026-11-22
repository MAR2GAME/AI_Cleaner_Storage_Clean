> 历史文档，仅用于追溯；当前结构以 README.md、docs/mvi-architecture.md 和 AGENTS.md 为准。

# 三个 kit 的结构复核（2026-11-20 第二轮）

> 状态：历史文档。三个 kit 已于 2026-11-21 合并回 `:app`，现在是
> `com.aicleanerstorage.{cleanerkit,devicekit,networkkit}` 包；本文保留为当时的决策记录。

- 第一轮评审：22:15（`docs/kit-structure-review.md` 初版）
- 第二轮复核：22:50–23:05，对象是 22:32–22:41 之间改过的 `cleaner-kit/`、`device-kit/`、`network-kit/`
- 本轮变更范围：三个模块的 README、模型改名（去掉 `*Model` 后缀）、`MediaDeletionAuthorization`、`network-kit` 模型纯化、`hasUsageAccess` 迁回 app、ktlint + version catalog、补测试
- 验证：`:cleaner-kit:testDebugUnitTest`、`:device-kit:testDebugUnitTest`、`:network-kit:testDebugUnitTest`、`:app:compileDebugKotlin` 全部通过；但 `ktlintCheck` **失败**（见 §7）

## 结论

**切法仍然合理，kit 形态比上一轮接近，但还没有成立；上一轮的结构问题修掉了 4 条，新出现 2 条。**

- 修掉的：network-kit 的展示层/宿主包名/权限越界、cleaner-kit 模型里的 `IntentSender`、三个模块的 ktlint/版本目录/README。
- 仍缺的：命名与包名、发布与 API 契约、device-kit 的展示串、构建约定插件。
- 新出现的：**ktlint 全红**（主源码 414/21/24 条违规），以及**两套媒体授权路径并存**导致 app 的手动授权 UI 成为死代码。

| 维度 | 第一轮 | 第二轮 |
| --- | --- | --- |
| 模块切分 | 合理 | 合理，未变 |
| 依赖方向 | 单向、无环 | 单向、无环，未变 |
| network-kit 边界 | 混入权限/资源/文案 | **已纯化**：无 `res/`、枚举/数值模型、无宿主包名 |
| cleaner-kit 模型纯度 | 公开模型带 `IntentSender` | **已修**：模型只带 `authorizationId`，`IntentSender` 收进授权请求对象 |
| 权限归属 | `hasUsageAccess` 在 network-kit | **已修**：迁回 app `base/permission` |
| 构建一致性 | 硬编码依赖、只 app 有 ktlint | **部分修**：改用 catalog、三个 kit 都加 ktlint；但 ktlint 不通过，且仍无 build-logic |
| 文档 | 只有 cleaner-kit README | **已修**：三个模块各一份 README，均改为“Gradle 项目依赖接入” |
| 命名/包名 | `*-kit` + 连写包名 | **未变**，仍非 App 模块主流 |
| 发布/契约 | 无 | **未变**：无 `maven-publish`、无 `explicitApi()`、无 consumer rules |
| device-kit 展示层 | 未单独评估 | **仍在**：`health/statusText/screenSize/...` 都是展示串 |

一句话：**如果只服务本 App，现在应该改名 `:core:cleaner` / `:core:device` / `:core:network`；如果要继续叫 kit，先让 ktlint 绿、补发布与契约、把 device 的展示串拿出去。**

---

## 1. 当前快照

```text
AICleanerStorageClean
├─ app                 365 kt / 32k 行       （未变）
├─ cleaner-kit          43 主源码 / 8 测试
│  ├─ clean/            CleanRepository + model + internal(scanner)
│  ├─ files/            FileRepository + model + internal
│  ├─ MediaDeletionAuthorization.kt
│  └─ internal/storage/ 扫描、删除快照、PendingAuthorizations、MediaDeletion
├─ device-kit           15 主源码 / 2 测试
├─ network-kit          15 主源码 / 1 测试
│  ├─ model/            纯模型（枚举/数值，无 android.*）
│  ├─ permission/       internal hasUsageAccess
│  └─ internal/         网络信息、用量、测速、Wi-Fi 扫描
└─ junkcc               广告 SDK（不在范围）
```

| 指标 | cleaner-kit | device-kit | network-kit |
| --- | ---: | ---: | ---: |
| 主源码 Kotlin 文件 / 行数 | 43 / 1,807 | 15 / 640 | 15 / 547 |
| 测试文件 / 行数 | 8 / 311 | 2 / 104 | 1 / 16 |
| 公开顶层类型 | 19 | 9 | 13 |
| 测试类 / 用例 | 8 / 21 | 2 / 10 | 1 / 1 |
| `res/` | 无 | 无 | **已删除** |
| README | 有（重写） | **新增** | **新增** |
| ktlint 主源码违规 | **414** | **21** | **24** |
| ktlint 测试源码违规 | **35** | **2** | 0 |
| `maven-publish` / `explicitApi` / consumer rules | 无 | 无 | 无 |

---

## 2. 这一轮修好的（已验证）

1. **network-kit 模型纯化，宿主包名消失**：`NetworkInfo.transport` 改为 `NetworkTransport` 枚举、`ssid/ip` 可空（[NetworkInfo.kt](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/model/NetworkInfo.kt#L3-L9)）；测速结果改为 `Double?`（[NetworkSpeedResult.kt](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/model/NetworkSpeedResult.kt#L3-L8)）；阶段改为 `SpeedTestPhase`（[NetworkSpeedProgress.kt](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/model/NetworkSpeedProgress.kt#L3-L10)）；未知流量改为 `TrafficSource.SystemAndUnknown`（[NetworkUsageApp.kt](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/model/NetworkUsageApp.kt#L3-L6)）。`res/values*` 与 `com.aicleanerstorage.spaceclean.*` 常量都不在了，全库 grep 无宿主包名。
2. **权限工具归位**：`hasUsageAccess` 现在在 app 的 [base/permission/UsageAccess.kt](../app/src/main/java/com/aicleanerstorage/spaceclean/base/permission/UsageAccess.kt#L8)，applock/appusage 的 import 也改回 app 包；network-kit 内只剩 `internal` 副本供 `hasNetworkUsageAccess()` 使用（[UsageAccess.kt](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/permission/UsageAccess.kt#L8)）。
3. **`IntentSender` 离开公开模型**：`JunkDeleteOutcome`/`FileDeleteOutcome` 只保留 `authorizationId`（[JunkDeleteOutcome.kt](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/model/JunkDeleteOutcome.kt#L5-L14)、[FileDeleteResult.kt](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/files/model/FileDeleteResult.kt#L10-L14)）；新增 [MediaDeletionAuthorization.kt](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/MediaDeletionAuthorization.kt#L17-L80) 与 [PendingAuthorizations.kt](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/internal/storage/PendingAuthorizations.kt#L6-L40)，README 第 23–44 行给了手动接入示例。模型包现在没有任何 `android.*` import。
4. **构建配置部分收敛**：三个 kit 都套用了 version catalog（`libs.kotlinx.coroutines.core`、`libs.junit`）和 ktlint 插件（[cleaner](../cleaner-kit/build.gradle.kts#L1-L33)、[device](../device-kit/build.gradle.kts#L1-L33)、[network](../network-kit/build.gradle.kts#L1-L33)）。
5. **文档补齐**：device-kit 与 network-kit 各有 README，三个 README 都改成 `include(":xxx-kit")` + `implementation(project(...))`，不再教宿主复制源码（[cleaner README](../cleaner-kit/README.md#L3-L13)）。
6. **app 已跟上新 API**：app 里已没有 `*Model` 旧名 import，`DataModule`、`NetworkUsageContract`、`NetworkSpeedContract` 都按新模型改写，`:app:compileDebugKotlin` 通过，`app` 单测 86 条全绿。

---

## 3. 仍然存在的问题

### P1

| # | 问题 | 证据 | 影响 |
| --- | --- | --- | --- |
| 3.1 | **ktlint 全红** | `:cleaner-kit:ktlintMainSourceSetCheck` 实跑 exit 1；报告 [cleaner main](../cleaner-kit/build/reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.txt) 414 条、test 35 条；device 21/2；network 24/0（[报告目录](../network-kit/build/reports/ktlint/)） | 加了质量门却没跑 `ktlintFormat`。只要 CI 执行 `check` 或 `ktlintCheck` 就会红；同一仓库的 app 主源码是通过的，说明不是工具配置问题，是三个 kit 没按项目格式写 |
| 3.2 | **两套媒体授权路径并存，app 的手动路径成为死代码** | app 在 [DataModule.kt:44](../app/src/main/java/com/aicleanerstorage/spaceclean/app/di/DataModule.kt#L44) 配了 `authorizeMediaDeletion`（自动路径）；`CleanRepositoryImpl` 在 `authorize != null` 时走 `authorizePendingMedia`，不会产出 `authorizationId`（[CleanRepositoryImpl.kt:154-188](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/CleanRepositoryImpl.kt#L154-L188)）。但 app 仍保留手动 UI：[JunkCleanRoute.kt:77-95](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/ui/JunkCleanRoute.kt#L77-L95) 的 `prepare/complete`、[JunkCleanViewModel.kt:160-224](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/interaction/JunkCleanViewModel.kt#L160-L224) 的 `pendingAuthorizationOutcomes`/`handleAuthorizationResult`、[JunkCleanEffectHandler.kt:19-22](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/ui/JunkCleanEffectHandler.kt#L19-L22) 的分支都不会触发 | 代码能编译、能运行，但 `AwaitingAuthorization` 状态、`JunkDeleteAuthorizationRequested`、`DeleteAuthorizationResultReceived` 这条链路永远走不到；新增的 `MediaDeletionAuthorization` 公开 API 也没有被本 App 验证。两个入口做同一件事，后续改一个很容易漏另一个 |
| 3.3 | **device-kit 仍把展示串放进模型** | `BatteryInfo.health: String`、`availableTime: String`（[BatteryInfo.kt:5,10](../device-kit/src/main/kotlin/com/aicleanerstorage/devicekit/model/BatteryInfo.kt#L5-L10)）；`BatteryStatusInfo.statusText: String`（[BatteryStatusInfo.kt:4](../device-kit/src/main/kotlin/com/aicleanerstorage/devicekit/model/BatteryStatusInfo.kt#L4)）；`screenSize/screenDensity: String`（[DeviceHardwareInfo.kt:6-7](../device-kit/src/main/kotlin/com/aicleanerstorage/devicekit/model/DeviceHardwareInfo.kt#L6-L7)）；生产处是 `"${w}x${h}"`、`"$dpi DPI"`、`"%.1f GHz"`、`"Good"/"Overheat"`、`"Charging"`（[DeviceHardwareReader.kt:34-35,188-193](../device-kit/src/main/kotlin/com/aicleanerstorage/devicekit/internal/DeviceHardwareReader.kt#L34-L35)） | app 只能在 UI 里用 `when ("Good")` 反查字符串资源（[BatteryCard.kt:39-57](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/deviceinfo/ui/part/BatteryCard.kt#L39-L57)）。kit 改一个词、加一个健康状态，app 会静默落到 "Unknown"；换语言、换单位也无从下手。这和 network-kit 刚修好的问题是同一类，两个 kit 的标准不一致 |
| 3.4 | **仍然只是“名为 kit 的内部模块”** | `settings.gradle.kts` 仍是 `app`、`junkcc`、`cleaner-kit`、`device-kit`、`network-kit`（[settings.gradle.kts:28-32](../settings.gradle.kts#L28-L32)）；README 明确写“以 Android Gradle 项目依赖接入”；三个模块都没有 `maven-publish`、`explicitApi()`、`consumer-rules.pro` | 源码依赖 + `*-kit` 命名，介于“内部 core”和“对外 SDK”之间。内部用叫 kit 会误导复用方以为有版本/契约；对外用又没有发布坐标和 API 稳定性保障 |
| 3.5 | **上一轮的行为 P1 没动** | APK 仍按“公共目录 30 天 / Download 7 天”判垃圾（[ApkScanner.kt:26,37-42](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/scanner/ApkScanner.kt#L26-L42)）；Temp 仍把 `.log/.bak/.cache` 当临时文件扫 Documents/DCIM（[TempFileScanner.kt:12-27](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/scanner/TempFileScanner.kt#L12-L27)）；`MemoryCleaner` 仍把 300ms 前后系统 `availMem` 差值当释放量（[MemoryCleaner.kt:12-24](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/MemoryCleaner.kt#L12-L24)） | 误选用户归档 APK/日志、结果页伪指标；这些是清理器的核心行为，和结构同等重要 |

### P2

| # | 问题 | 证据 | 说明 |
| --- | --- | --- | --- |
| 3.6 | 测速探针不校验 HTTP 状态、不 `disconnect()`，默认端点仍是 Google/Cloudflare | [NetworkSpeedProbe.kt:12-62](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/internal/NetworkSpeedProbe.kt#L12-L62)、[NetworkSpeedEndpoints.kt:6-8](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/NetworkSpeedEndpoints.kt#L6-L8) | 中国大陆默认不可达；失败原因不可区分。上一轮 P2.15 未收敛 |
| 3.7 | `scanWifi` 每次扫 254 个 IP + 反查 DNS + 读 `/proc/net/arp` | [NetworkToolboxDataSource.kt:252-294](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/internal/NetworkToolboxDataSource.kt#L252-L294) | 单次扫描重；`canonicalHostName` 可能触发反向解析；无对应测试 |
| 3.8 | 测试覆盖仍薄 | network 只有 1 条 `measuredNetworkSpeed` 单测（[NetworkMeasurementTest.kt](../network-kit/src/test/kotlin/com/aicleanerstorage/networkkit/model/NetworkMeasurementTest.kt#L8-L17)）；`MediaDeletionAuthorization.prepare/complete/discard` 没有测试，只测了底层 `PendingAuthorizations`；APK/Temp 规则仍无表驱动测试 | 新公开 API 和最容易误删的规则都没有回归网 |
| 3.9 | 三个 `build.gradle.kts` 仍是三份复制 | 3 × 33 行，只有 `api`/`implementation` 一行不同 | 应上 `build-logic` convention plugin（Android 官方与 Now in Android 的做法） |
| 3.10 | `NetworkUsageInfo` 字段冗余、构造用位置参数 | `apps` 与 `wifiApps/cellularApps/fallbackApps` 并存（[NetworkUsageInfo.kt:8-13](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/model/NetworkUsageInfo.kt#L8-L13)）；`NetworkScanResult(...)` 位置传参（[NetworkToolboxDataSource.kt:129-136](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/internal/NetworkToolboxDataSource.kt#L129-L136)） | 加字段/换顺序时容易静默错位 |
| 3.11 | `JunkFile.lastModified` 默认当前时间，`scanSafely` 仍吞异常，扫描器固定 `delay(150)` | [JunkFile.kt:9](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/model/JunkFile.kt#L9)、[CleanRepositoryImpl.kt:224-231](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/CleanRepositoryImpl.kt#L224-L231)、[CleanRepositoryImpl.kt:97](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/CleanRepositoryImpl.kt#L97) | 数据层仍承担 UI 节奏，扫描失败仍不可区分 |
| 3.12 | app 的 `JunkCleanRoute` 仍注入 `CleanerKit` 只为拿 `mediaDeletion` | [JunkCleanRoute.kt:35](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/ui/JunkCleanRoute.kt#L35) | 与 3.2 同源；选定一条授权路径后应删除 |

---

## 4. 包结构：现在 vs 主流

**现在（模块内是对的，模块外仍不是主流）：**

```text
com.aicleanerstorage.cleanerkit
├─ CleanerKit / CleanerConfig / MediaDeletionAuthorization
├─ clean/  (CleanRepository, model/, internal/)
├─ files/  (FileRepository, model/, internal/)
└─ internal/storage/

com.aicleanerstorage.devicekit
├─ DeviceKit / DeviceInfoRepository
├─ model/        ← 仍含展示串（Good/Charging/1080x2400/420 DPI）
└─ internal/

com.aicleanerstorage.networkkit
├─ NetworkKit / NetworkRepository / NetworkSpeedEndpoints
├─ model/        ← 已纯化
├─ permission/   ← internal
└─ internal/
```

**主流（App 内部）：**

```text
:app
:core:model
:core:data
:core:domain
:core:network       ← 只放网络读写
:core:database
:core:designsystem
:core:ui
:feature:home
:feature:junkclean
:feature:files
...
build-logic/        ← convention plugins
```

```text
com.example.core.network
├─ NetworkRepository
├─ model/
└─ internal/
```

对照结论：

- 层内“根包接口 + model + internal”仍然是对的，三个 kit 都符合。
- 模块外，`*-kit` + `cleanerkit` 连写包名不是 App 模块主流；`network-kit` 已经修正了自己的越界，`device-kit` 还差展示层这一步。
- “kit”在主流里对应的是**对外发布的库**；内部模块叫 `:core:*`，不需要发布坐标，但需要遵守分层与依赖规则。现在三个模块卡在两者之间。

---

## 5. 主流参考

1. **Now in Android**：`:app` + `:core:{model,data,domain,network,database,designsystem,ui,common}` + `:feature:*`，app → feature → core，feature 之间不互相依赖；构建逻辑集中在 `build-logic` 的 convention plugins（[Now in Android](https://github.com/android/nowinandroid)、[Modularization learning journey](https://github.com/android/nowinandroid/blob/main/docs/ModularizationLearningJourney.md)）。
2. **Android 官方模块化指南**：按 feature 或 layer 拆，模块“高内聚、低耦合”，`:core:network` 只负责网络请求与响应（[App modularization](https://developer.android.com/topic/modularization)）。
3. **AndroidX/库命名**：Gradle 路径用连字符，低层库用 `-core`、测试库用 `-testing`、纯 Kotlin 用 `-common`，包名镜像 group + artifact（[AndroidX API guidelines: modules](https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/docs/api_guidelines/modules.md)）。

---

## 6. 建议（按优先级）

### 第一步：二选一，收敛授权路径（P1）

- **保留自动回调**（当前 app 的做法）：删掉 `JunkCleanRoute` 的手动 `prepare/complete`、`JunkCleanViewModel` 的 `pendingAuthorizationOutcomes/handleAuthorizationResult`、`AwaitingAuthorization` 状态和 `JunkDeleteAuthorizationRequested`/`DeleteAuthorizationResultReceived`，以及 `JunkCleanRoute` 对 `CleanerKit.mediaDeletion` 的注入；`MediaDeletionAuthorization` 要么删掉，要么明确标注“仅外部宿主手动接入时使用”并补测试。
- **或者保留手动路径**：从 `DataModule` 去掉 `authorizeMediaDeletion`，让 `JunkCleanRoute` 成为唯一入口，再给这条路径补 app 层测试。
- 两条都留 = 两套语义、两套测试、两处死代码。**不要继续这样。**

### 第二步：修结构（P1/P2）

1. 命名与包名：`cleaner-kit` → `:core:cleaner`，包 `com.aicleanerstorage.core.cleaner`；device/network 同理。若确定对外发布，则保留 `*-kit` 并补发布。
2. device-kit 去展示层：`BatteryHealth`/`BatteryStatus`/`NetworkTransport` 式枚举 + 数值 + `Double?`/`Int?`，`screenSize` 拆 `widthPx/heightPx`，`screenDensity` 拆 `densityDpi`，`availableTime` 拆 `Int?` 分钟；文案与单位全部回 app。
3. `ktlintFormat` 后把 `ktlintCheck` 跑绿；三个 kit 的 main/test 都要绿，再谈“质量门”。
4. 建 `build-logic`：一个 `android-library` convention plugin 统一 compileSdk/minSdk/jvmToolchain/ktlint/test 依赖；三个 `build.gradle.kts` 缩到几行。
5. 若坚持“kit”：加 `maven-publish`、`explicitApi()`（或 api/impl）、`consumer-rules.pro`、每模块权限/线程/失败语义 README、binary-compatibility-validator；否则改名 `:core:*`。
6. 行为 P1（APK/Temp/Memory）按第一轮评审 §5 的顺序修，先收紧规则再加测试。

### 不建议

- 不要再拆模块：三个已经够用，`app` 保持单模块。
- 不要为了“kit”而复制发布配置；源码依赖就承认是 `:core`。

---

## 7. 验证记录

```powershell
$env:JAVA_HOME='D:\dev\jdk17'

# 编译 + 单测：通过
.\gradlew.bat :cleaner-kit:testDebugUnitTest :device-kit:testDebugUnitTest `
  :network-kit:testDebugUnitTest :app:compileDebugKotlin --console=plain
# BUILD SUCCESSFUL in 44s

# app 单测：通过
.\gradlew.bat :app:testDebugUnitTest --continue --console=plain
# 86 tests, 0 failures

# ktlint：失败
.\gradlew.bat :cleaner-kit:ktlintCheck :device-kit:ktlintCheck :network-kit:ktlintCheck --console=plain
# BUILD FAILED
# cleaner-kit main 414 / test 35；device-kit main 21 / test 2；network-kit main 24 / test 0
```

| 模块 | 测试类 | 用例 | 结果 |
| --- | ---: | ---: | --- |
| cleaner-kit | 8 | 21 | 全绿 |
| device-kit | 2 | 10 | 全绿 |
| network-kit | 1 | 1 | 全绿 |
| app | 27 | 86 | 全绿 |

未执行：真机、ADB、AndroidTest、Release 构建。结构结论来自代码阅读与 JVM 构建；行为结论（APK/Temp/Memory、测速）来自代码阅读，不代表设备已验证。

## 8. 一句话回答

- **合理吗**：切法合理；形态仍不合格，但比上一轮明显接近。
- **包结构是这样吗**：模块内对（接口 + model + internal），模块外不对（`*-kit`、连写包名、缺发布/契约）；network-kit 已纯化，device-kit 还带展示串。
- **主流怎么样**：App 内部用 `:core:*` + `:feature:*` + `build-logic`；只有对外发布的库才叫 kit，并带发布坐标、`explicitApi`、consumer rules 和独立 README。
