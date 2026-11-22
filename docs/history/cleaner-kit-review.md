> 历史文档，仅用于追溯；当前结构以 README.md、docs/mvi-architecture.md 和 AGENTS.md 为准。

# cleaner-kit 评审：问题清单（2026-11-20 复核）

- 复核时间：2026-11-20 21:50
- 评审对象：当前工作树中的 `cleaner-kit/`（仍未 `git add`，`?? cleaner-kit/`）
- 当前规模：`cleaner-kit` 105 个 Kotlin 文件 / 4,246 行；`app` 365 个 Kotlin 文件 / 32,257 行
- 验证结果：
  - `:cleaner-kit:testDebugUnitTest`：11 个测试类、33 条测试、0 失败
  - `:app:compileDebugKotlin`：BUILD SUCCESSFUL
  - 复核开始时工作树**编译失败**，本次修了 5 处编译错误，详见 §6

## 结论

上一版评审指出的最严重问题——`Download` 被当作广告垃圾目录、整目录递归删除——**已经拆除**。扫描器只剩 `cache/temp/apk/duplicate` 四类，缓存只扫本应用 cache，删除只处理常规文件，不再递归删目录；删除授权、权限异常、PIN 存储、通知内容留存、扫描选择默认值也都做了实质修正。

但当前模块仍有 P1 级问题：APK/日志规则仍可能误选用户文件；内存清理仍把系统可用内存差值当“释放量”；相似照片仍不是相似；包可见性、电池采样、错误吞噬等仍未收敛。整体判断：**行为从“会误删目录”降到“可能误选文件”；结构仍偏“数据层合集”，不是可复用 kit。**

| 维度 | 判断 |
| --- | --- |
| 模块边界、依赖方向 | 合理 |
| 行为正确性 | 基本可用，仍有 P1 误选/伪指标 |
| API 契约 | 明显改善（权限异常、删除状态、配置回调），仍缺统一错误类型 |
| 可复用性 | 仍弱（9 个领域、硬编码 prefs/默认包、无 api/impl） |

---

## 1. 已修复/已验证（上一版 P0）

| # | 修复内容 | 证据 |
| --- | --- | --- |
| 1.1 | `AdJunkScanner`、`ResidualScanner` 已删除，不再按 `"ad"`/`"log"` 目录名整目录判垃圾 | [CleanRepositoryImpl.kt:49](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/CleanRepositoryImpl.kt#L49-L55) |
| 1.2 | `CacheScanner` 只扫 `context.cacheDir` 与 `externalCacheDirs`，`isJunkFile` 恒真 | [CacheScanner.kt:7](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/scanner/CacheScanner.kt#L7-L11) |
| 1.3 | 文件系统扫描只产出常规文件，跳过符号链接，不再产出“目录型 junk” | [FileSystemJunkScanner.kt:14](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/scanner/FileSystemJunkScanner.kt#L14-L30) |
| 1.4 | 删除前用 `inspectFile` 校验 size/mtime/常规文件，`Os.remove` 删除，无递归删目录 | [JunkFileDeleteHelper.kt:23](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/JunkFileDeleteHelper.kt#L23-L55)、[DeletionSnapshot.kt:12](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/internal/storage/DeletionSnapshot.kt#L12-L35) |
| 1.5 | `freedBytes` 只在 `DeleteStatus.Deleted` 时计入；`AlreadyAbsent` 只从会话移除，不计释放 | [JunkFileDeleteHelper.kt:32](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/JunkFileDeleteHelper.kt#L32-L35)、[CleanRepositoryImpl.kt:181](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/CleanRepositoryImpl.kt#L181) |
| 1.6 | 删除授权改为宿主回调：Android 11+ 分批 `createDeleteRequest`（200/批），Android 10 用 `recoveryIntent` 单项重试；处理 `RecoverableSecurityException` | [MediaDeletion.kt:36](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/internal/storage/MediaDeletion.kt#L36-L66)、[CleanerConfig.kt:16](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/CleanerConfig.kt#L16) |
| 1.7 | `files` 域补齐权限异常与逐项删除结果：`FileAccessRequiredException`、`DeleteStatus`、`FileDeleteOutcome`、`recoveryIntent` 与逐项 outcomes | [FileAccessRequiredException.kt:3](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/files/FileAccessRequiredException.kt#L3)、[FileRepositoryImpl.kt:120](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/files/internal/FileRepositoryImpl.kt#L120-L172) |
| 1.8 | MediaStore 查询失败不再吞成空列表，改为抛异常；无权限时由 `requireMediaReadAccess`/`requireSharedStorageAccess` 抛 `FileAccessRequiredException` | [MediaStoreFileQuery.kt:30](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/files/internal/MediaStoreFileQuery.kt#L30-L67)、[FileRepositoryImpl.kt:174](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/files/internal/FileRepositoryImpl.kt#L174-L189) |
| 1.9 | 应用锁 PIN 改为 PBKDF2-HmacSHA256（210,000 次、随机盐），并支持明文 PIN 迁移 | [PinCredential.kt:9](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/applock/internal/PinCredential.kt#L9-L35)、[AppLockRepositoryImpl.kt:28](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/applock/internal/AppLockRepositoryImpl.kt#L28-L48) |
| 1.10 | 备份规则排除 `phone_cleaner_sp` 与 `notification_blocker`；通知内容默认不留存 | [backup_rules.xml:2](../app/src/main/res/xml/backup_rules.xml#L2-L3)、[NotificationRepositoryImpl.kt:17](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/notification/internal/NotificationRepositoryImpl.kt#L17-L23) |
| 1.11 | 默认只勾选 CACHE，日志/备份/APK/重复文件需用户主动选择 | [JunkCleanReducer.kt:257](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/interaction/JunkCleanReducer.kt#L257-L260) |
| 1.12 | 扫描/删除加 `Mutex`、`generation`、`scanId`；授权结果一次性消费 | [CleanRepositoryImpl.kt:61](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/CleanRepositoryImpl.kt#L61-L74)、[InMemoryCleanSessionStore.kt:25](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/InMemoryCleanSessionStore.kt#L25-L40) |
| 1.13 | `VirusScanMode` 去掉 UI 几何与 7s/15s 最短展示时间；动画参数移回 app | [VirusScanMode.kt:3](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/security/model/VirusScanMode.kt#L3)、[VirusScanAnimation.kt:5](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/antivirus/interaction/VirusScanAnimation.kt#L5-L23) |
| 1.14 | 新增规则/会话测试：常规文件快照、授权一次性消费、变更/缺失检测 | [FileSelectionSafetyTest.kt:16](../cleaner-kit/src/test/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/scanner/FileSelectionSafetyTest.kt#L16-L34)、[CleanSessionResultTest.kt:17](../cleaner-kit/src/test/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/CleanSessionResultTest.kt#L17-L31) |

---

## 2. 仍然存在的问题

### P1：行为与数据风险

| # | 问题 | 证据 | 影响 |
| --- | --- | --- | --- |
| 2.1 | `ApkScanner` 把公共目录中超过 30 天的任意 APK 判为垃圾；Download 中超过 7 天即判 | [ApkScanner.kt:26](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/scanner/ApkScanner.kt#L26)、[ApkScanner.kt:37](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/scanner/ApkScanner.kt#L37-L48) | 用户归档的安装包可能被清理；默认只选 CACHE 降低了概率，但用户“全选”仍会中招 |
| 2.2 | `TempFileScanner` 把 `.log/.bak/.cache` 当临时文件，扫描 Document/Pictures/DCIM/Movies/Music | [TempFileScanner.kt:12](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/scanner/TempFileScanner.kt#L12-L27) | 用户日志/备份文件可能被误选 |
| 2.3 | `MemoryCleaner` 不再杀进程（`killedCount=0`），但仍把 300ms 前后系统 `availMem` 差值当“释放量”；结果页显示“可用内存增加” | [MemoryCleaner.kt:12](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/MemoryCleaner.kt#L12-L25)、[JunkCleanResultContent.kt:106](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/ui/part/JunkCleanResultContent.kt#L106-L113) | 伪指标；数字与本次清理无因果关系 |
| 2.4 | “相似照片”仍按相册 + 天 + 512KB 尺寸桶分组，无感知哈希 | [SimilarPhotoGrouping.kt:7](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/files/internal/SimilarPhotoGrouping.kt#L7-L19) | 同日同相册、尺寸相近的不同照片会被推荐删除 |
| 2.5 | 应用锁/通知应用列表依赖枚举全部启动器应用，但 manifest 只声明 WhatsApp `<queries>`，无 `QUERY_ALL_PACKAGES` | [LockableAppQuery.kt:32](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/applock/internal/LockableAppQuery.kt#L32-L40)、[AndroidManifest.xml:33](../app/src/main/AndroidManifest.xml#L33-L36) | Android 11+ 列表可能静默不全；是否可用取决于广告 AAR 是否合并了该权限，属隐式依赖 |
| 2.6 | 电池历史默认 2 秒采样；24h 窗口 43,201 条；每次 append 两次排序，每 2 秒写盘，每 10 分钟全量重写；`CleanerConfig` 可调但 App 未覆盖 | [BatteryHistoryRule.kt:4](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/battery/BatteryHistoryRule.kt#L4-L6)、[BatteryHistoryRepositoryImpl.kt:42](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/battery/internal/BatteryHistoryRepositoryImpl.kt#L42-L56) | 一个“省电”工具自身制造 IO 抖动与耗电 |
| 2.7 | 扫描器异常仍被 `catch (Throwable) → emptyList()` 吞掉，没有错误类型 | [CleanRepositoryImpl.kt:236](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/CleanRepositoryImpl.kt#L236-L243) | 权限/IO/空目录仍可能不可区分；部分成功无法表达 |
| 2.8 | `ScanResult.sharedStorageAccessLimited` 已计算，但 App 没有使用/展示 | [ScanResult.kt:8](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/model/ScanResult.kt#L8)、[CleanRepositoryImpl.kt:116](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/CleanRepositoryImpl.kt#L116-L119) | 受限扫描结果不会提示用户，仍像“扫到 0 B” |
| 2.9 | 每个扫描器固定 `delay(150)`，4 个扫描器最少 600ms | [CleanRepositoryImpl.kt:93](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/CleanRepositoryImpl.kt#L93) | UI 节奏写进数据层，扫描被无谓拉长 |
| 2.10 | MediaStore 仍一次性全表查询、依赖废弃 `DATA` 列、不过滤 `IS_PENDING/IS_TRASHED`、不分页 | [MediaStoreFileQuery.kt:30](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/files/internal/MediaStoreFileQuery.kt#L30-L67) | 大量媒体时内存/耗时高；已回收条目可能被当作可删项 |
| 2.11 | `findMediaUri` 用 `DATA = absolutePath` 精确匹配，每个失败文件最多 5 次查询 | [JunkFileDeleteHelper.kt:103](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/JunkFileDeleteHelper.kt#L103-L144) | `/sdcard` 与 `/storage/emulated/0` 差异、大小写差异会导致授权 URI 查不到 |
| 2.12 | 公开 model 直接携带 `android.content.IntentSender` | [JunkDeleteOutcome.kt:3](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/model/JunkDeleteOutcome.kt#L3-L18)、[FileDeleteResult.kt:10](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/files/model/FileDeleteResult.kt#L10-L14) | `cleaner-kit` 的 API 不再是纯 Kotlin/Android-free；纯 JVM 测试与跨平台复用受限 |
| 2.13 | 未设置 PIN 时，应用锁页面仍把 7 个硬编码包显示为“已锁” | [AppLockRepositoryImpl.kt:78](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/applock/internal/AppLockRepositoryImpl.kt#L78-L88)、[AppLockRepositoryImpl.kt:142](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/applock/internal/AppLockRepositoryImpl.kt#L142-L151) | UI 显示“已锁”，实际 `lockedPackages()` 为空、监控也不启用 |
| 2.14 | 通知应用列表为空时回退为三个固定包名的“假应用” | [NotificationDataSource.kt:96](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/notification/internal/NotificationDataSource.kt#L96-L110) | 用户可能看到未安装的应用 |
| 2.15 | 测速仍不校验 HTTP 状态码、不 `disconnect()`；端点可通过 `CleanerConfig` 替换，但默认仍为 Google/Cloudflare | [NetworkSpeedProbe.kt:10](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/network/internal/NetworkSpeedProbe.kt#L10-L62) | 中国大陆默认不可达；失败原因不可区分 |

### P2：架构与可复用性

| # | 问题 | 证据 | 影响 |
| --- | --- | --- | --- |
| 3.1 | `ioDispatcher` 只注入 `DeviceInfoRepository` 与电池采样，其余 28 处硬编码 `Dispatchers.IO` | [CleanerKit.kt:35](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/CleanerKit.kt#L35-L46) | 宿主无法统一调度，测试无法替换 |
| 3.2 | 仍是 9 个领域、61 个公开顶层声明；`phone_cleaner_sp`、`quick_clean_settings`、默认锁定包、宿主包名仍在库内 | [AppLockRepositoryImpl.kt:132](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/applock/internal/AppLockRepositoryImpl.kt#L132-L138) | 换宿主即冲突/失效，“kit”名不副实 |
| 3.3 | 扫描会话与待授权结果只在内存；`CleanerKit` 无 `close()` | [InMemoryCleanSessionStore.kt:17](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/InMemoryCleanSessionStore.kt#L17-L25) | 进程死亡丢结果；采样协程忘 stop 会持续写盘 |
| 3.4 | 库带 `res/`、无 api/impl 分离、无 `consumer-rules.pro`；README 仍要求“复制模块到宿主工程” | [build.gradle.kts:25](../cleaner-kit/build.gradle.kts#L25-L29)、[README.md:3](../cleaner-kit/README.md#L3) | 资源命名冲突、ABI/混淆问题留给复用方 |
| 3.5 | 测试 33 条，仍无 APK/Temp/Cache 规则测试，无 `FileRepository` 删除/授权流程、`MediaDeletion`、PIN、通知、网络测试 | `src/test` | 2.1–2.5 这类问题没有回归网 |
| 3.6 | `JunkFile.lastModified` 默认 `System.currentTimeMillis()`；`id` 用 `path.hashCode()` | [JunkFile.kt:9](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/model/JunkFile.kt#L9-L12) | 默认值无意义；hash 碰撞/负值风险 |
| 3.7 | `CleanerConfig` 已参数化测速端点、通知留存、电池间隔、授权回调，但未覆盖 prefs 名、默认锁定包、扫描规则/阈值 | [CleanerConfig.kt:12](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/CleanerConfig.kt#L12-L20) | “可配置”只做了一半 |

---

## 3. 模块是否太大

**按体量不大，按职责太宽。**

| 指标 | cleaner-kit | app（对照） |
| --- | --- | --- |
| Kotlin 文件 | 105 | 365 |
| Kotlin 行数 | 4,246 | 32,257 |
| 运行时依赖 | coroutines、core-ktx | Compose/Koin/广告 SDK 等 |
| 最大单文件 | 281 行（`NetworkToolboxDataSource.kt`） | — |
| 公开顶层类型 | 61 | — |
| 领域数 | 9 | — |

按领域拆分：

| 包 | 行数 | 占比 |
| --- | ---: | ---: |
| `clean`（含 scanner/model） | 750 | 18% |
| `files`（含 model） | 613 | 14% |
| `device`（含 model） | 615 | 14% |
| `network`（含 model） | 522 | 12% |
| `internal/storage`（共享） | 435 | 10% |
| `notification` | 322 | 8% |
| `battery` | 283 | 7% |
| `applock` | 262 | 6% |
| `security` | 189 | 4% |
| `appusage` | 157 | 4% |
| 门面/配置/权限 | 98 | 2% |

判断：

- 4,246 行约占 app Kotlin 的 13%，没有超大文件，也没有单个领域超过 1,000 行；从编译、AAR 体积看**不大**。
- 但 9 个领域、61 个公开类型、10 个 lazy 仓库，是“9 个变更理由共用 1 个模块”。这是**内聚性**问题，不是规模问题。
- 不建议按 9 个领域拆 9 个 Gradle 模块。这个仓库刚从多 feature 模块合并回 `app`（`b9b8098`），物理拆分成本已经交过学费；4k 行拆 9 份，模块税大于收益。
- 务实切法：只服务本 App 就保持 1 个模块，改名 `:core:data` 或 `:cleaner-core`；需要限制依赖面最多拆 2 个（`core` / `extras`）；真要对外再拆 `api`/`impl`。

---

## 4. 哪些功能应放回 app

约一半应放回。`clean` + `files` + `internal/storage` 是清理引擎，留下；其余是 App 功能/平台适配。

| 领域 | 行数 | 建议 | 关键理由 |
| --- | ---: | --- | --- |
| `clean` | 750 | 留下 | 清理器核心；P1 规则也在这里修 |
| `files` | 613 | 留下 | 媒体/文件管理核心 |
| `internal/storage` | 435 | 留下 | clean/files 共享 |
| `device` | 615 | 移出（先放 app；复用再独立 `core:device`） | 通用设备信息，不服务清理；sysfs/反射是平台适配 |
| `network` | 522 | 移出 | 工具箱功能；端点、位置权限、隐私语义 |
| `notification` | 322 | 移出 | 宿主必须注册 `NotificationListenerService`；内容留存是 App 策略 |
| `battery` | 283 | 移出 | 2 秒采样绑定 App 前台服务生命周期 |
| `applock` | 262 | 移出 | 悬浮窗/UsageStats/FGS、宿主 prefs、默认锁定包 |
| `security` | 189 | 移出 | Trustlook 是 App 集成；扫描时长是 UI |
| `appusage` | 157 | 移出 | UsageStats + 单一功能 |
| 门面/配置/权限 | 98 | 随剩余核心保留或拆分 | 配置需按领域切 |

移动后：

- 规模从 **4,246 行 / 105 文件** 降到约 **1,857 行 / 43 文件**（clean + files + storage + 门面）。
- 权限面收敛为存储/媒体；悬浮窗、通知监听、UsageStats、位置、包可见性、FGS 回到 app。
- 公开类型和 Koin 绑定同步减少；库内不再出现 App prefs 名/默认包/宿主包名。

不建议：

- 不要把 `clean`/`files` 也放回 app；它们是模块存在的理由。
- 不要为了“放回”再拆 9 个模块；放 `app/data/...`、`app/domain/...` 包即可。
- 不要建 `utils`/`common` 新杂物间。

迁移顺序：

1. 先修 §2 的 P1（规则、内存指标、错误处理），改的正好是留下的 clean/files；
2. 一次性机械搬家（applock/notification/network/appusage/security/battery/device），只改包名和 import；
3. 再决定改名 `:core:data` 或拆 `api`/`impl`。

---

## 5. 修复优先级

### P0（已完成/需真机确认）

1. ~~`Download` 整目录删除链~~ 已拆除。
2. 真机验证：Android 10/11+/13+ 的扫描、批量删除授权、`RecoverableSecurityException`、Android 10 `recoveryIntent`、目录/空文件/被并发修改文件的逐项结果。

### P1（建议下一批）

3. 收紧 `ApkScanner`：只在 Download/明确安装包目录，且让用户确认；不要按 30 天全盘判垃圾。
4. 收紧 `TempFileScanner`：`.log/.bak` 仅在明确的 temp/cache 目录；不要在 Documents/DCIM 里删。
5. 删除或明确标注 `MemoryCleaner`：不再展示“可用内存增加”作为清理成果。
6. `SimilarPhotoGrouping`：上 pHash/dHash，或改名“同相册相近尺寸照片”，不要叫“相似”。
7. 包可见性：声明 `QUERY_ALL_PACKAGES` 或 `LauncherApps`/`<queries>`；不可见时让 API 返回明确错误。
8. 电池采样：默认降频（15 分钟级）或批量写；App 侧显式设置 `batterySampleIntervalMillis`。
9. `scanSafely`：至少保留 `CancellationException`、把其他异常映射为 `ScanError`，不要静默空列表。
10. 结果页展示 `sharedStorageAccessLimited`，提示“未授予完整访问，结果可能不全”。

### P2（结构/契约）

11. 统一 `DeleteStrategy`/`DeleteStatus` 的错误语义；`FileRepository` 与 `CleanRepository` 对齐。
12. 把 `IntentSender` 从公开 model 移到 host 回调参数；api 保持纯 Kotlin（若要做 SDK）。
13. 注入 dispatcher/clock/config；去掉剩余硬编码 `Dispatchers.IO`。
14. 会话持久化或明确“进程死亡后需重扫”；`CleanerKit` 增加 `close()`。
15. 模块改名/分层；去掉 `res/` 或明确资源前缀；加 `consumer-rules.pro`。
16. 补测试：扫描规则表驱动、`FileRepository` 删除/授权、`MediaDeletion`、PIN 迁移、通知留存、网络失败。
17. README 写清权限前置、线程模型、删除授权时序、失败语义、API 29/30+ 差异。

---

## 6. 验证记录与本次编译修复

执行：

```powershell
$env:JAVA_HOME='D:\dev\jdk17'
.\gradlew.bat :cleaner-kit:testDebugUnitTest --console=plain
.\gradlew.bat :app:compileDebugKotlin --console=plain
```

结果：

- `:cleaner-kit:testDebugUnitTest`：11 个测试类、33 条测试、0 失败
- `:app:compileDebugKotlin`：BUILD SUCCESSFUL

复核开始时工作树编译失败，本次修复了 5 处：

1. [DeletionSnapshot.kt:29](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/internal/storage/DeletionSnapshot.kt#L29)：`android.system.Os.unlink` 不存在 → `android.system.Os.remove`
2. [MediaStoreFileQuery.kt:40](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/files/internal/MediaStoreFileQuery.kt#L40)：`emptyList()`/`buildList` 泛型无法推断 → 显式 `emptyList<ManagedFileItem>()` / `buildList<ManagedFileItem>`
3. [MediaStoreFileQuery.kt:91](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/files/internal/MediaStoreFileQuery.kt#L91)：同上
4. [NetworkSpeedContract.kt:26](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/networkspeed/interaction/NetworkSpeedContract.kt#L26)：`when` 分支返回 `String?` → `?:` 链
5. [NetworkSpeedContract.kt:34](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/networkspeed/interaction/NetworkSpeedContract.kt#L34)：同上

未执行：真机安装、ADB、AndroidTest、真实文件删除/系统授权交互。上述 P1/P2 结论均来自代码阅读与 JVM 测试，不代表设备行为已验证。

---

## 7. 做得好的地方（不要改坏）

- 扫描器已收敛为“cache/temp/apk/duplicate”，不再按目录名整目录删除；`FileSystemJunkScanner` 只产出常规文件、跳过符号链接。
- 删除前 `inspectFile` 校验 size/mtime/常规文件；`Os.remove` 不递归；`AlreadyAbsent` 与 `Deleted` 分开，释放量只算真实删除。
- 重复文件删除前重新计算哈希并校验保留副本，保留副本被改/删/同批选中都会拒绝。
- 删除授权两阶段设计完整：直删 → 失败给 `authorizationUri`/`recoveryIntent` → 宿主 `createDeleteRequest`/API 29 recovery → 复核结果；授权结果一次性消费。
- 媒体查询失败不再吞成空列表；权限缺失抛 `FileAccessRequiredException`，不再伪装成“没有文件”。
- PIN 已 PBKDF2 哈希并迁移；备份规则排除了 PIN 与通知 prefs；通知内容默认不留存。
- 默认只勾选 CACHE，日志/备份/APK/重复文件需用户主动选择。
- 33 条 JVM 测试全绿，覆盖重复删除安全、哈希 TOCTOU、授权一次性消费、常规文件快照等关键规则。

## 8. 参考基线

- 平台能力边界：[Android 11 存储更新](https://developer.android.com/about/versions/11/privacy/storage)、[MediaStore](https://developer.android.com/reference/android/provider/MediaStore)、[StorageStatsManager](https://developer.android.com/reference/android/app/usage/StorageStatsManager)
- Play 权限政策：[所有文件访问声明](https://support.google.com/googleplay/android-developer/answer/10467955)、[敏感权限与 API](https://support.google.com/googleplay/android-developer/answer/16558241)
- 第三方参考：[SD Maid SE](https://github.com/d4rken-org/sdmaid-se)、[SD Maid FAQ：没有 API 可直接清其他 App 缓存](https://sdmse.darken.eu/faq)
