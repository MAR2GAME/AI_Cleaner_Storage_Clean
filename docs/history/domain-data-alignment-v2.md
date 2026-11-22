> 历史文档，仅用于追溯；当前结构以 README.md、docs/mvi-architecture.md 和 AGENTS.md 为准。

# domain / data 对齐 PDFScanRead 方案 v2

> 状态：已完成（2026-11-21 拟定，2026-11-22 落地）。
> 执行状态（2026-11-22）：**kit 已完全溶解**。`cleanerkit`、`devicekit`、`networkkit` 全部并入 `data`，四个 mapper 已删除，`view → kit` 为 0。
> 架构已从 business-first 改为 **layer-first**：`domain/{model/<concept>,port,rule,usecase,error}`、`data/{repository,local/<capability>}`；`MediaDeletionAuthorization` 在 `data/local/storage`。
> 当前：domain 59 文件、data 61 文件、门禁 21 条、测试 152。
> 基线：`domain` 34 文件、`data` 20 文件；`view → data` 0；`view → kit` 49 文件 / 76 import；
> `check-rules.ps1` 14 条全绿。参考实现 `D:\pros\pdfscan-read`：AGENTS.md 240 行、
> 23 条门禁、domain 46 / data 13、`view → SDK` 0。
>
> 目标：`view → kit` 0；domain 覆盖 25/25 个页面；data 除 Android 组件外全 `internal`；
> 写操作统一 `Result`；domain/data 类级 KDoc 覆盖 ≥ 90%；门禁补齐到 19 条（第二批再补 UI 侧至 23 条）；`AGENTS.md` 成为规则唯一入口。
>
> 与 v1 的关系：`docs/domain-data-alignment-plan.md` 已完成「把模型/接口从 data 搬进 domain」，
> 本方案只做 v1 没有覆盖的「边界收口 + 规范 + 门禁」，不改 UI 结构、导航、广告、权限链路。

## 0. 五个决策（先定，避免返工）

| 编号 | 决策 | 理由 |
|---|---|---|
| D1 | **目录保持业务优先**（`domain/<feature>/{model,port,rule,usecase,error}`），不改成 ref 的 `domain/{model,port,rule,usecase,error}` | 两个项目的概念数不同：ref 只有 document 一个核心概念，层优先更好；本项目有 10 个互不相干的业务，层优先会得到 8 个平铺的 `model/` 子目录。等价结构，不改形状（layer-first 变体见附录 A） |
| D2 | **domain 允许 `kotlinx.coroutines.flow`**，登记到 `docs/rules/exceptions.md` | 现有 2 个端口已用 `StateFlow`，强行去掉会把响应式状态上移成 ViewModel 手工轮询。ref 的「domain 只依赖 Kotlin/JDK」按字面执行成本高、收益低（严格方案见附录 B） |
| D3 | **写操作返回 `kotlin.Result`，读操作直接返回值** | 对齐 ref 的失败语义：基础设施异常在 data 收口，页面只处理 `Result` 和业务错误；读操作失败直接抛，由 VM 的边界兜底（与 ref `DocumentRepository` 一致）。本轮覆盖新增的 4 个端口 + `SettingsRepository`；`AppLock` / `AppUsage` / `NotificationCleaner` / `Security` 的存量写方法分批改，不进本轮验收 |
| D4 | **data 只允许 Android 组件 public**（`Service` / `Receiver` / `Provider` / `Activity`），其余类型 `internal` | 对齐 ref「具体实现默认 internal」；当前唯一例外是 `StorageCleanNotificationListener` |
| D5 | **本轮不做**：UI 重构、导航改造、广告/权限链路、kit 模块内部重构 | 与 v1 相同的非目标；kit 只被 data 适配，不再被页面直接使用 |

## 1. 目标结构

### 1.1 目录树（目标态）

```text
app/src/main/java/com/aicleanerstorage/spaceclean/
├─ domain/                                  纯 Kotlin，只依赖自身 + Kotlin/JDK（例外：coroutines 见 D2）
│  ├─ applock/{model,port,rule}            不变
│  ├─ appusage/{model,port}                不变
│  ├─ battery/{model,port,usecase}          不变
│  ├─ files/
│  │  ├─ model/
│  │  │  ├─ DeleteStatus.kt                 [新] 删除状态（从 kit 复制语义）
│  │  │  ├─ FileDeleteSummaryModel.kt       已有
│  │  │  ├─ FileKind.kt                     已有
│  │  │  └─ ManagedFileModel.kt             已有
│  │  └─ port/
│  │     └─ ManagedFileRepository.kt        [新] 文件列表 + 删除 + 去定位
│  ├─ junkclean/
│  │  ├─ error/JunkError.kt                 改：补分支 KDoc
│  │  ├─ model/
│  │  │  ├─ CleanupSummaryModel.kt          已有
│  │  │  ├─ JunkCategory.kt                 [新] 从 kit 复制枚举
│  │  │  ├─ JunkDeleteOutcomeModel.kt       [新]
│  │  │  ├─ JunkFileModel.kt                [新]
│  │  │  ├─ JunkMemoryCleanModel.kt         [新]
│  │  │  ├─ JunkScanProgressModel.kt        [新]
│  │  │  └─ JunkScanResultModel.kt          [新]
│  │  └─ port/
│  │     └─ JunkCleanRepository.kt          [新]
│  ├─ deviceinfo/                           [新] 设备信息的领域归属
│  │  ├─ model/
│  │  │  ├─ BatteryInfoModel.kt             [新]
│  │  │  ├─ CapacityInfoModel.kt            [新] 内存/存储共用一个容量模型
│  │  │  ├─ DeviceCpuInfoModel.kt           [新]
│  │  │  ├─ DeviceHardwareInfoModel.kt      [新]
│  │  │  └─ DeviceSensorInfoModel.kt        [新]
│  │  └─ port/
│  │     └─ DeviceInfoRepository.kt         [新]
│  ├─ network/                              [新] 网络信息的领域归属
│  │  ├─ model/
│  │  │  ├─ NetworkDeviceModel.kt           [新]
│  │  │  ├─ NetworkInfoModel.kt             [新]
│  │  │  ├─ NetworkScanResultModel.kt       [新]
│  │  │  ├─ NetworkSpeedModel.kt            [新]
│  │  │  ├─ NetworkTransport.kt             [新] 枚举
│  │  │  ├─ NetworkUsageAppModel.kt         [新]
│  │  │  ├─ NetworkUsageInfoModel.kt        [新]
│  │  │  ├─ SpeedTestPhase.kt               [新] 枚举
│  │  │  └─ TrafficSource.kt                [新] sealed interface
│  │  └─ port/
│  │     └─ NetworkRepository.kt            [新]
│  ├─ notificationcleaner/{model,port}      不变
│  ├─ security/{error,model,port}           不变
│  ├─ settings/port/SettingsRepository.kt   改：写方法返回 Result
│  ├─ similarphotos/port                    不变
│  ├─ whatsapp/
│  │  ├─ model/WhatsAppFileBucket.kt        [改] 改名 WhatsAppFileBucketModel.kt
│  │  └─ port                               不变
│  ├─ error/                                [删] 空目录
│  └─ rule/                                 [删] 空目录
│
├─ data/                                    平台与存储机制，实现 domain 端口
│  ├─ applock/internal/                     不变
│  ├─ appusage/internal/                    不变
│  ├─ battery/internal/
│  │  ├─ BatteryHistoryFileStore.kt         已有
│  │  ├─ BatteryHistoryRepositoryImpl.kt    已有（实现 BatteryHistoryRepository）
│  │  └─ BatteryHistorySampler.kt           [移] 从 data/battery/ 移入（实现 BatteryHistoryRecorder）
│  ├─ files/internal/
│  │  ├─ ManagedFileMapper.kt               [移] 从 data/files/ 移入
│  │  └─ ManagedFileRepositoryImpl.kt       [移] 从 cleanerkit/files/internal/FileRepositoryImpl 搬入，
│  ├─ junkclean/internal/
│  │  ├─ JunkCleanMapper.kt                 [新]
│  │  └─ JunkCleanRepositoryImpl.kt         [新] 实现 JunkCleanRepository
│  ├─ deviceinfo/internal/
│  │  ├─ DeviceInfoMapper.kt                [新]
│  │  └─ DeviceInfoRepositoryImpl.kt        [新] 实现 domain DeviceInfoRepository
│  ├─ network/internal/
│  │  ├─ NetworkMapper.kt                   [新]
│  │  └─ NetworkRepositoryImpl.kt           [新] 实现 domain NetworkRepository
│  ├─ notificationcleaner/
│  │  ├─ internal/{NotificationCleanerDataSource,NotificationCleanerRepositoryImpl}.kt
│  │  └─ StorageCleanNotificationListener.kt [移] 从 data/repository/notificationcleaner/ 移入
│  ├─ repository/SettingsRepositoryImpl.kt  [移] → data/settings/internal/SettingsRepositoryImpl.kt
│  ├─ security/internal/                    不变
│  ├─ similarphotos/internal/
│  │  ├─ SimilarPhotoGrouping.kt            [移] 从 data/similarphotos/ 移入
│  │  └─ SimilarPhotoRepositoryImpl.kt      已有
│  ├─ whatsapp/internal/
│  │  ├─ WhatsAppFileClassifier.kt          [移] 从 data/whatsapp/ 移入
│  │  ├─ WhatsAppFiles.kt                   [移] 从 data/whatsapp/ 移入
│  │  └─ WhatsAppCleanerRepositoryImpl.kt   已有
│  ├─ internal/Catching.kt                  [新] 把异常收口成 Result 的公共工具
│  ├─ local/                                [删] 空目录
│  └─ remote/                               [删] 空目录
│
└─ app/
   ├─ di/DataModule.kt                      改：新增 4 个端口绑定 + applicationScope
   ├─ di/PresentationModule.kt              改：loadFiles 的接收者换成 domain 端口
   └─ view/                                 只依赖 base + domain + domain model
```

### 1.2 文件级改动清单

#### A. 新增 domain（4 个端口 + 23 个模型/枚举）

| 文件 | 内容 | 对应 kit 类型 |
|---|---|---|
| `domain/files/port/ManagedFileRepository.kt` | 图片/视频/音频/截图/隐私图/文档/大文件/重复分组 + 删除 + 去定位 | `cleanerkit.files.FileRepository` |
| `domain/files/model/DeleteStatus.kt` | 删除状态枚举 | `cleanerkit.files.model.DeleteStatus` |
| `domain/junkclean/port/JunkCleanRepository.kt` | 扫描进度/结果、内存清理、批量删除 | `cleanerkit.clean.CleanRepository` |
| `domain/junkclean/model/JunkCategory.kt` | 垃圾分类枚举 | `cleanerkit.clean.model.JunkCategory` |
| `domain/junkclean/model/JunkFileModel.kt` | 单条垃圾文件 | `cleanerkit.clean.model.JunkFile` |
| `domain/junkclean/model/JunkScanProgressModel.kt` | 扫描进度 | `cleanerkit.clean.model.ScanProgress` |
| `domain/junkclean/model/JunkScanResultModel.kt` | 扫描结果 + 分类汇总 | `cleanerkit.clean.model.ScanResult` |
| `domain/junkclean/model/JunkDeleteOutcomeModel.kt` | 逐文件删除结果 | `cleanerkit.clean.model.JunkDeleteOutcome` |
| `domain/junkclean/model/JunkMemoryCleanModel.kt` | 内存清理结果 | `cleanerkit.clean.model.MemoryCleanResult` |
| `domain/deviceinfo/port/DeviceInfoRepository.kt` | 电量/内存/存储/CPU/传感器 | `devicekit.DeviceInfoRepository` |
| `domain/deviceinfo/model/BatteryInfoModel.kt` | 电量信息 | `devicekit.model.BatteryInfo` |
| `domain/deviceinfo/model/CapacityInfoModel.kt` | 内存/存储容量 | `devicekit.model.CapacityInfo` |
| `domain/deviceinfo/model/DeviceCpuInfoModel.kt` | CPU 信息 | `devicekit.model.DeviceCpuInfo` |
| `domain/deviceinfo/model/DeviceHardwareInfoModel.kt` | 硬件聚合 | `devicekit.model.DeviceHardwareInfo` |
| `domain/deviceinfo/model/DeviceSensorInfoModel.kt` | 传感器布尔组 | `devicekit.model.DeviceSensorInfo` |
| `domain/network/port/NetworkRepository.kt` | 网络状态/扫描/测速/流量 | `networkkit.NetworkRepository` |
| `domain/network/model/NetworkInfoModel.kt` | 当前网络 | `networkkit.model.NetworkInfo` |
| `domain/network/model/NetworkTransport.kt` | 传输类型枚举 | `networkkit.model.NetworkTransport` |
| `domain/network/model/NetworkDeviceModel.kt` | 局域网设备 | `networkkit.model.NetworkDeviceInfo` |
| `domain/network/model/NetworkScanResultModel.kt` | 扫描结果 | `networkkit.model.NetworkScanResult` |
| `domain/network/model/NetworkSpeedModel.kt` | 测速结果 | `networkkit.model.NetworkSpeed` |
| `domain/network/model/SpeedTestPhase.kt` | 测速阶段枚举 | `networkkit.model.SpeedTestPhase` |
| `domain/network/model/NetworkUsageAppModel.kt` | 单 App 流量 | `networkkit.model.NetworkUsageApp` |
| `domain/network/model/NetworkUsageInfoModel.kt` | 流量汇总 | `networkkit.model.NetworkUsageInfo` |
| `domain/network/model/TrafficSource.kt` | App/系统流量来源 | `networkkit.model.TrafficSource` |

#### B. 新增/移动 data（4 个适配器 + 7 个文件归位）

| 动作 | 源 | 目标 | 说明 |
|---|---|---|---|
| 新 | — | `data/internal/Catching.kt` | `catching { }`，取消必须重抛 |
| 移 | `cleanerkit/files/internal/FileRepositoryImpl.kt` | `data/files/internal/ManagedFileRepositoryImpl.kt` | 直接实现 `ManagedFileRepository`；删除 kit `FileRepository` 接口和转发类 |
| 新 | — | `data/junkclean/internal/JunkCleanMapper.kt` | kit ↔ domain 模型转换 |
| 新 | — | `data/junkclean/internal/JunkCleanRepositoryImpl.kt` | 实现 `JunkCleanRepository` |
| 新 | — | `data/deviceinfo/internal/DeviceInfoMapper.kt` | kit ↔ domain 模型转换 |
| 新 | — | `data/deviceinfo/internal/DeviceInfoRepositoryImpl.kt` | 实现 domain `DeviceInfoRepository` |
| 新 | — | `data/network/internal/NetworkMapper.kt` | kit ↔ domain 模型转换 |
| 新 | — | `data/network/internal/NetworkRepositoryImpl.kt` | 实现 domain `NetworkRepository` |
| 移 | `data/files/ManagedFileMapper.kt` | `data/files/internal/ManagedFileMapper.kt` | 与 `internal` 约定一致 |
| 移 | `data/battery/BatteryHistorySampler.kt` | `data/battery/internal/BatteryHistorySampler.kt` | 实现类归 `internal` |
| 移 | `data/similarphotos/SimilarPhotoGrouping.kt` | `data/similarphotos/internal/SimilarPhotoGrouping.kt` | 同上 |
| 移 | `data/whatsapp/WhatsAppFileClassifier.kt` | `data/whatsapp/internal/WhatsAppFileClassifier.kt` | 同上 |
| 移 | `data/whatsapp/WhatsAppFiles.kt` | `data/whatsapp/internal/WhatsAppFiles.kt` | 同上 |
| 移 | `data/repository/notificationcleaner/StorageCleanNotificationListener.kt` | `data/notificationcleaner/StorageCleanNotificationListener.kt` | 平台组件，不是 repository |
| 移 | `data/repository/SettingsRepositoryImpl.kt` | `data/settings/internal/SettingsRepositoryImpl.kt` | 业务优先，和 `domain/settings` 对齐 |
| 删 | `data/local/`、`data/remote/` | — | 空目录；`remote` 等第一个网络调用再建 |
| 删 | `domain/error/`、`domain/rule/` | — | 空目录；错误已在 `*/error`，规则已在 `*/rule` |
| 改 | `domain/whatsapp/model/WhatsAppFileBucket.kt` | `.../WhatsAppFileBucketModel.kt` | 补 `Model` 后缀，遵守本项目第 33 行规则 |
| 改 | `domain/settings/port/SettingsRepository.kt` | 写方法返回 `Result<Unit>` | 对齐 D3；调用方 `onFailure` 提示 |

#### C. 改动的 view 文件（按类型簇）

| 类型簇 | 涉及文件数 | 改动 |
|---|---|---|
| `ManagedFileItem` / `FileUri` / `DeleteStatus` | 10 | `ManagedFileItem → ManagedFileModel`、`FileUri → String`；`FileOperations` / 各 Manager VM 的删除改 `Result` |
| `JunkFile` / `JunkCategory` / `ScanResult` / `ScanProgress` / `JunkDeleteOutcome` / `MemoryCleanResult` | 约 20 | 类型改名 + `filePath→path`、`fileName→name`、`fileSize→sizeBytes`、`lastModified→modifiedMillis`、`junkFiles→files`、`junkFile→file`；删除 `formattedSize`，改用 `FormatUtil.formatSize` |
| `DeviceHardwareInfo` / `BatteryInfo` / `MemoryInfo` / `StorageInfo` / `DeviceCpuInfo` / `DeviceSensorInfo` | 11 | 类型加 `Model` 后缀；集中在 `app/view/common/toolbox/interaction/DeviceInfoModels.kt` |
| `NetworkInfo` / `NetworkScanResult` / `NetworkDeviceInfo` / `NetworkSpeed` / `NetworkUsageInfo` / `NetworkUsageApp` / 枚举 | 13 | 类型加 `Model` 后缀；枚举改包名不改名 |
| `CleanRepository` / `FileRepository` / `DeviceInfoRepository` / `NetworkRepository` 的注入 | 14 个 VM | 换成 domain 端口；`PresentationModule` 的 `loadFiles` 接收者跟着换 |
| 测试 | 10 左右 | import 与类型同步；新增 4 个适配器测试 |

> 改动方式：先加 domain/data/DI，再逐簇改 view，最后开 `viewKitIsolation` 门禁；全程由编译错误驱动，不手改字符串。

## 2. 核心代码

### 2.1 domain/files

```kotlin
// domain/files/model/DeleteStatus.kt
package com.aicleanerstorage.spaceclean.domain.files.model

/**
 * 单个文件的删除结果。
 *
 * 页面按 [Deleted] 与 [AuthorizationRequired] 区分"已删"和"待授权"，
 * [AlreadyAbsent] 视为完成；[Changed] / [Failed] 要保留在选中列表里让用户重试。
 */
enum class DeleteStatus {
    Deleted,
    AuthorizationRequired,
    AlreadyAbsent,
    Changed,
    Unknown,
    Failed,
}
```

```kotlin
// domain/files/port/ManagedFileRepository.kt
package com.aicleanerstorage.spaceclean.domain.files.port

import com.aicleanerstorage.spaceclean.domain.files.model.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.files.model.ManagedFileModel

/**
 * 文件列表与删除的业务契约，按页面用途查询，不暴露 MediaStore / SAF。
 *
 * 查询方法正常返回；删除是写操作，failure 表示整批没有执行，
 * 成功值里的计数描述部分失败。调用方不捕获平台异常。
 */
interface ManagedFileRepository {
    suspend fun loadImages(): List<ManagedFileModel>

    suspend fun loadVideos(): List<ManagedFileModel>

    suspend fun loadAudios(): List<ManagedFileModel>

    suspend fun loadScreenshots(): List<ManagedFileModel>

    suspend fun loadPrivacyImages(): List<ManagedFileModel>

    suspend fun loadDocuments(): List<ManagedFileModel>

    suspend fun loadLargeFiles(minBytes: Long = DEFAULT_LARGE_FILE_BYTES): List<ManagedFileModel>

    suspend fun loadDuplicateGroups(): List<List<ManagedFileModel>>

    suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel>

    suspend fun removeLocationData(files: List<ManagedFileModel>): Result<Int>

    companion object {
        const val DEFAULT_LARGE_FILE_BYTES = 10L * 1024 * 1024
    }
}
```

`ManagedFileModel` 沿用现有字段（`id / uri: String / path / name / sizeBytes / modifiedSeconds / mimeType / bucketName / kind`），不再新增 `FileUri` 值类，UI 直接用 `String`。

### 2.2 domain/junkclean

```kotlin
// domain/junkclean/model/JunkCategory.kt
package com.aicleanerstorage.spaceclean.domain.junkclean.model

/** 垃圾清理的分类；页面按此分组，kit 扫描器只负责产出对应枚举。 */
enum class JunkCategory {
    CACHE,
    TEMP_FILE,
    RESIDUAL,
    APK,
    DUPLICATE,
    LARGE_FILE,
}
```

```kotlin
// domain/junkclean/model/JunkFileModel.kt
package com.aicleanerstorage.spaceclean.domain.junkclean.model

/**
 * 一条可清理的垃圾文件。
 *
 * [path] 是扫描时的绝对路径，删除前必须重新校验文件状态；
 * [duplicateHash] 只用于重复文件分组，不参与删除判断。
 */
data class JunkFileModel(
    val id: String,
    val path: String,
    val name: String,
    val sizeBytes: Long,
    val category: JunkCategory,
    val modifiedMillis: Long = System.currentTimeMillis(),
    val duplicateHash: String? = null,
    val duplicateOriginalPath: String? = null,
    val scanId: String = "",
)
```

```kotlin
// domain/junkclean/model/JunkScanProgressModel.kt
package com.aicleanerstorage.spaceclean.domain.junkclean.model

/** 扫描进度；只用于展示，不参与业务判断。 */
data class JunkScanProgressModel(
    val percent: Float = 0f,
    val currentCategory: JunkCategory? = null,
    val foundCount: Int = 0,
    val foundSize: Long = 0,
) {
    companion object {
        val IDLE = JunkScanProgressModel()
    }
}
```

```kotlin
// domain/junkclean/model/JunkScanResultModel.kt
package com.aicleanerstorage.spaceclean.domain.junkclean.model

/** 一次完整扫描的结果；`sharedStorageAccessLimited` 表示共享存储权限受限。 */
data class JunkScanResultModel(
    val files: List<JunkFileModel>,
    val sharedStorageAccessLimited: Boolean = false,
) {
    val totalCount: Int get() = files.size

    val totalSize: Long get() = files.sumOf { it.sizeBytes }

    val categoryBreakdown: Map<JunkCategory, List<JunkFileModel>> get() = files.groupBy { it.category }

    companion object {
        val EMPTY = JunkScanResultModel(emptyList())
    }
}
```

```kotlin
// domain/junkclean/model/JunkDeleteOutcomeModel.kt
package com.aicleanerstorage.spaceclean.domain.junkclean.model

import com.aicleanerstorage.spaceclean.domain.files.model.DeleteStatus

/** 单条垃圾文件的删除结果；[authorizationId] 非空时等待系统授权。 */
data class JunkDeleteOutcomeModel(
    val file: JunkFileModel,
    val status: DeleteStatus,
    val authorizationId: String? = null,
) {
    val deleted: Boolean get() = status == DeleteStatus.Deleted

    val freedBytes: Long get() = if (deleted) file.sizeBytes else 0L
}
```

```kotlin
// domain/junkclean/model/JunkMemoryCleanModel.kt
package com.aicleanerstorage.spaceclean.domain.junkclean.model

/** 内存清理结果；只有 [freedBytes] 参与完成页展示。 */
data class JunkMemoryCleanModel(
    val freedBytes: Long,
)
```

```kotlin
// domain/junkclean/port/JunkCleanRepository.kt
package com.aicleanerstorage.spaceclean.domain.junkclean.port

import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkDeleteOutcomeModel
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkMemoryCleanModel
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkScanProgressModel
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkScanResultModel
import kotlinx.coroutines.flow.StateFlow

/**
 * 垃圾扫描与清理的业务契约。
 *
 * [scanProgress] 在扫描期间持续更新；[scanResult] 保存最近一次完整扫描，
 * 页面重新进入时先读它再决定是否重扫。[scan] / [deleteFiles] 的 failure
 * 表示整批操作没有开始或基础设施异常，逐文件结果在成功值里。
 */
interface JunkCleanRepository {
    val scanProgress: StateFlow<JunkScanProgressModel>

    val scanResult: StateFlow<JunkScanResultModel?>

    fun clearSession()

    fun removeCleanedFiles(files: List<JunkFileModel>)

    suspend fun scan(): Result<JunkScanResultModel>

    suspend fun cleanMemory(): Result<JunkMemoryCleanModel>

    suspend fun deleteFiles(files: List<JunkFileModel>): Result<List<JunkDeleteOutcomeModel>>

    suspend fun deleteFiles(
        files: List<JunkFileModel>,
        shouldStop: () -> Boolean,
    ): Result<List<JunkDeleteOutcomeModel>>
}
```

### 2.3 domain/deviceinfo

```kotlin
// domain/deviceinfo/model/BatteryInfoModel.kt
package com.aicleanerstorage.spaceclean.domain.deviceinfo.model

/** 设备电量信息；字段保留 kit 语义，展示文案由 UI 解析。 */
data class BatteryInfoModel(
    val levelPercent: Int,
    val health: String,
    val temperature: Float,
    val voltage: Int,
    val technology: String,
    val capacity: Int,
    val availableTime: String = "Unknown",
    val statusText: String = "Unknown",
    val isCharging: Boolean = false,
)
```

```kotlin
// domain/deviceinfo/model/CapacityInfoModel.kt
package com.aicleanerstorage.spaceclean.domain.deviceinfo.model

/** 内存或存储的容量信息。 */
data class CapacityInfoModel(
    val totalBytes: Long,
    val availableBytes: Long,
) {
    val usedBytes: Long get() = if (totalBytes > 0L) totalBytes - availableBytes else 0L

    val usagePercent: Int get() = if (totalBytes > 0L) (usedBytes.toFloat() / totalBytes * 100).toInt() else 0
}
```

```kotlin
// domain/deviceinfo/model/DeviceHardwareInfoModel.kt
package com.aicleanerstorage.spaceclean.domain.deviceinfo.model

/** 设备硬件聚合；CPU 与传感器各自建模，避免页面拆字段。 */
data class DeviceHardwareInfoModel(
    val model: String,
    val androidVersion: String,
    val screenSize: String,
    val screenDensity: String,
    val multiTouchSupported: Boolean,
    val sensors: DeviceSensorInfoModel,
    val cpu: DeviceCpuInfoModel,
)
```

`DeviceCpuInfoModel(hardware, model, cores, maxFrequency)`、`DeviceSensorInfoModel(accelerometer, magneticField, orientation, gyroscope, light, proximity, ambientTemperature)` 与 kit 字段一一对应。

```kotlin
// domain/deviceinfo/port/DeviceInfoRepository.kt
package com.aicleanerstorage.spaceclean.domain.deviceinfo.port

import com.aicleanerstorage.spaceclean.domain.deviceinfo.model.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.deviceinfo.model.CapacityInfoModel
import com.aicleanerstorage.spaceclean.domain.deviceinfo.model.DeviceHardwareInfoModel

/**
 * 设备信息读取契约；只读，不缓存，调用方按页面生命周期自行刷新。
 *
 * [readCpuUsagePercent] 的 [previousPercent] 用于计算区间占用率，
 * 第一次读取传 null。
 */
interface DeviceInfoRepository {
    suspend fun readCpuUsagePercent(previousPercent: Int?): Int?

    fun batteryInfo(): BatteryInfoModel

    fun memoryInfo(): CapacityInfoModel

    fun internalStorageInfo(): CapacityInfoModel

    fun hardwareInfo(): DeviceHardwareInfoModel

    fun cpuTemperatureC(): Float?

    fun batteryCurrentNowMa(): Float?

    fun batteryCurrentAverageMa(): Float?
}
```

### 2.4 domain/network

```kotlin
// domain/network/model/NetworkTransport.kt
package com.aicleanerstorage.spaceclean.domain.network.model

/** 当前网络的传输类型。 */
enum class NetworkTransport { Wifi, Cellular, Ethernet, Unknown }
```

```kotlin
// domain/network/model/NetworkInfoModel.kt
package com.aicleanerstorage.spaceclean.domain.network.model

/** 当前网络；ssid / ip 在无权限或飞行模式时为 null。 */
data class NetworkInfoModel(
    val transport: NetworkTransport = NetworkTransport.Unknown,
    val ssid: String? = null,
    val ip: String? = null,
)
```

```kotlin
// domain/network/model/NetworkSpeedModel.kt
package com.aicleanerstorage.spaceclean.domain.network.model

/** 测速阶段。 */
enum class SpeedTestPhase { Idle, Latency, Download, Upload, Done }

/** 一次测速的结果；[measured] 为 false 时字段可能全为 null。 */
data class NetworkSpeedModel(
    val downloadMbps: Double? = null,
    val uploadMbps: Double? = null,
    val latencyMs: Long? = null,
    val phase: SpeedTestPhase = SpeedTestPhase.Idle,
    val measured: Boolean = false,
)
```

其余模型：

```kotlin
data class NetworkDeviceModel(val ip: String, val hostName: String?, val macAddress: String?)

data class NetworkScanResultModel(
    val ssid: String?,
    val gatewayIp: String?,
    val dnsIp: String?,
    val deviceIp: String?,
    val devices: List<NetworkDeviceModel>,
    val hasWifi: Boolean,
)

sealed interface TrafficSource {
    data class App(val packageName: String, val appName: String) : TrafficSource
    data object SystemAndUnknown : TrafficSource
}

data class NetworkUsageAppModel(
    val source: TrafficSource,
    val rxBytes: Long,
    val txBytes: Long,
) {
    val totalBytes: Long get() = rxBytes + txBytes
}

data class NetworkUsageInfoModel(
    val wifiRxBytes: Long,
    val wifiTxBytes: Long,
    val cellularRxBytes: Long,
    val cellularTxBytes: Long,
    val wifiApps: List<NetworkUsageAppModel> = emptyList(),
    val cellularApps: List<NetworkUsageAppModel> = emptyList(),
    val fallbackApps: List<NetworkUsageAppModel> = emptyList(),
    val isToday: Boolean,
    val needsUsageAccess: Boolean,
) {
    val wifiTotalBytes: Long get() = wifiRxBytes + wifiTxBytes
    val cellularTotalBytes: Long get() = cellularRxBytes + cellularTxBytes
}
```

```kotlin
// domain/network/port/NetworkRepository.kt
package com.aicleanerstorage.spaceclean.domain.network.port

import com.aicleanerstorage.spaceclean.domain.network.model.NetworkInfoModel
import com.aicleanerstorage.spaceclean.domain.network.model.NetworkScanResultModel
import com.aicleanerstorage.spaceclean.domain.network.model.NetworkSpeedModel
import com.aicleanerstorage.spaceclean.domain.network.model.NetworkUsageInfoModel
import kotlinx.coroutines.flow.StateFlow

/**
 * 网络状态、局域网扫描、测速与流量统计的业务契约。
 *
 * [lastScan] 由扫描页和局域网设备页共享；[runSpeedTest] 的 [onProgress]
 * 在 IO 线程回调，UI 只把它转成 Action，不直接改状态。
 */
interface NetworkRepository {
    fun readNetworkInfo(): NetworkInfoModel

    fun isNetworkAvailable(): Boolean

    fun isWifiConnected(): Boolean

    fun hasNetworkUsageAccess(): Boolean

    suspend fun readNetworkUsage(): NetworkUsageInfoModel

    suspend fun runSpeedTest(onProgress: (NetworkSpeedModel) -> Unit): NetworkSpeedModel

    val lastScan: StateFlow<NetworkScanResultModel?>

    suspend fun scanWifi(): NetworkScanResultModel

    fun clearLastScan()
}
```

### 2.5 data 适配器

> 线程说明：下面的适配器按「迁移期」写法展示，IO 调度仍由 ViewModel 的
> `withContext(ioDispatcher)` 承担（与现状一致，避免同一次改动里既搬类型又改调度）。
> P6 收口时给适配器构造参数加 `ioDispatcher`、每个 suspend 方法包 `withContext(ioDispatcher)`，
> 再删 VM 的 dispatcher 参数；这才是 ref 的约定（线程切换是实现层的事）。

公共工具：

```kotlin
// data/internal/Catching.kt
package com.aicleanerstorage.spaceclean.data.internal

import kotlinx.coroutines.CancellationException

/**
 * 把平台异常收口成 Result；取消必须继续传播，不能当成失败。
 */
internal suspend fun <T> catching(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        Result.failure(error)
    }
```

文件仓库（**实现下沉**，不再保留 kit 接口和转发类）：

```kotlin
// data/files/internal/ManagedFileRepositoryImpl.kt
// 由 cleanerkit.files.internal.FileRepositoryImpl 整体搬入；kit 的 FileRepository 接口已删除。
internal class ManagedFileRepositoryImpl(
    context: Context,
    private val authorize: (suspend (IntentSender) -> Boolean)?,
    private val authorization: MediaDeletionAuthorization,
) : ManagedFileRepository {
    override suspend fun loadImages(): List<ManagedFileModel> = queryImages().map { it.toModel() }

    override suspend fun loadVideos(): List<ManagedFileModel> = queryVideos().map { it.toModel() }

    override suspend fun loadAudios(): List<ManagedFileModel> = queryAudios().map { it.toModel() }

    // loadScreenshots / loadPrivacyImages / loadDocuments / loadLargeFiles 同理

    override suspend fun loadDuplicateGroups(): List<List<ManagedFileModel>> =
        withContext(Dispatchers.IO) {
            val files = discoverFiles(context).filter { it.type != ManagedFileType.Image }
            DuplicateFileSelection.buildDuplicateFileGroups(files) { item ->
                FileContentHasher.hash(context, item, coroutineContext)
            }
        }.map { group -> group.map { it.toModel() } }

    override suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel> =
        catching { deleteSelection(files.map { it.toItem() }).toSummary() }

    override suspend fun removeLocationData(files: List<ManagedFileModel>): Result<Int> =
        catching { FileLocationMetadata.removeLocationData(context, files.map { it.toItem() }) }

    /** 供 data 内 whatsapp / similarphotos 使用，不属于 domain 端口。 */
    internal suspend fun loadFilesInDirectories(
        relativeDirectories: List<String>,
        matchesMedia: (ManagedFileItem) -> Boolean,
    ): List<ManagedFileItem> = /* 原 FileRepositoryImpl 的目录查询 */

    internal suspend fun deleteOrThrow(files: List<ManagedFileModel>): FileDeleteSummaryModel =
        delete(files).getOrThrow()
}
```

```kotlin
// data/files/internal/ManagedFileMapper.kt
internal fun ManagedFileItem.toModel(): ManagedFileModel = /* 现有实现 + duplicatePeers 展开一层 */

internal fun ManagedFileModel.toItem(): ManagedFileItem = /* 现有实现 */

internal fun FileDeleteResult.toSummary(): FileDeleteSummaryModel =
    FileDeleteSummaryModel(
        deletedCount = deletedCount,
        freedBytes = freedBytes,
        skippedCount = skippedCount,
    )
```

垃圾清理：

```kotlin
// data/junkclean/internal/JunkCleanRepositoryImpl.kt
package com.aicleanerstorage.spaceclean.data.junkclean.internal

import com.aicleanerstorage.cleanerkit.clean.CleanRepository
import com.aicleanerstorage.spaceclean.data.internal.catching
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkDeleteOutcomeModel
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkMemoryCleanModel
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkScanProgressModel
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkScanResultModel
import com.aicleanerstorage.spaceclean.domain.junkclean.port.JunkCleanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** 用 cleaner-kit 的扫描器与删除器实现垃圾清理。 */
internal class JunkCleanRepositoryImpl(
    private val clean: CleanRepository,
    scope: CoroutineScope,
) : JunkCleanRepository {
    override val scanProgress: StateFlow<JunkScanProgressModel> =
        clean.scanProgress
            .map { it.toModel() }
            .stateIn(scope, SharingStarted.Eagerly, clean.scanProgress.value.toModel())

    override val scanResult: StateFlow<JunkScanResultModel?> =
        clean.scanResult
            .map { it?.toModel() }
            .stateIn(scope, SharingStarted.Eagerly, clean.scanResult.value?.toModel())

    override fun clearSession() = clean.clearSession()

    override fun removeCleanedFiles(files: List<JunkFileModel>) =
        clean.removeCleanedFiles(files.map { it.toItem() })

    override suspend fun scan(): Result<JunkScanResultModel> =
        catching { clean.performFullScan().toModel() }

    override suspend fun cleanMemory(): Result<JunkMemoryCleanModel> =
        catching { JunkMemoryCleanModel(clean.cleanMemory().freedBytes) }

    override suspend fun deleteFiles(files: List<JunkFileModel>): Result<List<JunkDeleteOutcomeModel>> =
        catching {
            clean.deleteFileItems(files.map { it.toItem() }).map { it.toModel() }
        }

    override suspend fun deleteFiles(
        files: List<JunkFileModel>,
        shouldStop: () -> Boolean,
    ): Result<List<JunkDeleteOutcomeModel>> =
        catching {
            clean.deleteFileItems(files.map { it.toItem() }, shouldStop).map { it.toModel() }
        }
}
```

```kotlin
// data/junkclean/internal/JunkCleanMapper.kt
package com.aicleanerstorage.spaceclean.data.junkclean.internal

import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory as KitJunkCategory
import com.aicleanerstorage.cleanerkit.clean.model.JunkDeleteOutcome as KitJunkDeleteOutcome
import com.aicleanerstorage.cleanerkit.clean.model.JunkFile as KitJunkFile
import com.aicleanerstorage.cleanerkit.clean.model.ScanProgress as KitScanProgress
import com.aicleanerstorage.cleanerkit.clean.model.ScanResult as KitScanResult
import com.aicleanerstorage.cleanerkit.files.model.DeleteStatus as KitDeleteStatus
import com.aicleanerstorage.spaceclean.domain.files.model.DeleteStatus
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkCategory
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkDeleteOutcomeModel
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkScanProgressModel
import com.aicleanerstorage.spaceclean.domain.junkclean.model.JunkScanResultModel

internal fun KitJunkFile.toModel(): JunkFileModel =
    JunkFileModel(
        id = id,
        path = filePath,
        name = fileName,
        sizeBytes = fileSize,
        category = category.toModel(),
        modifiedMillis = lastModified,
        duplicateHash = duplicateHash,
        duplicateOriginalPath = duplicateOriginalPath,
        scanId = scanId,
    )

internal fun JunkFileModel.toItem(): KitJunkFile =
    KitJunkFile(
        id = id,
        filePath = path,
        fileName = name,
        fileSize = sizeBytes,
        category = category.toKit(),
        lastModified = modifiedMillis,
        duplicateHash = duplicateHash,
        duplicateOriginalPath = duplicateOriginalPath,
        scanId = scanId,
    )

internal fun KitJunkCategory.toModel(): JunkCategory = when (this) {
    KitJunkCategory.CACHE -> JunkCategory.CACHE
    KitJunkCategory.TEMP_FILE -> JunkCategory.TEMP_FILE
    KitJunkCategory.RESIDUAL -> JunkCategory.RESIDUAL
    KitJunkCategory.APK -> JunkCategory.APK
    KitJunkCategory.DUPLICATE -> JunkCategory.DUPLICATE
    KitJunkCategory.LARGE_FILE -> JunkCategory.LARGE_FILE
}

internal fun JunkCategory.toKit(): KitJunkCategory = when (this) {
    JunkCategory.CACHE -> KitJunkCategory.CACHE
    JunkCategory.TEMP_FILE -> KitJunkCategory.TEMP_FILE
    JunkCategory.RESIDUAL -> KitJunkCategory.RESIDUAL
    JunkCategory.APK -> KitJunkCategory.APK
    JunkCategory.DUPLICATE -> KitJunkCategory.DUPLICATE
    JunkCategory.LARGE_FILE -> KitJunkCategory.LARGE_FILE
}

internal fun KitScanProgress.toModel(): JunkScanProgressModel =
    JunkScanProgressModel(
        percent = percent,
        currentCategory = currentCategory?.toModel(),
        foundCount = foundCount,
        foundSize = foundSize,
    )

internal fun KitScanResult.toModel(): JunkScanResultModel =
    JunkScanResultModel(
        files = junkFiles.map { it.toModel() },
        sharedStorageAccessLimited = sharedStorageAccessLimited,
    )

internal fun KitJunkDeleteOutcome.toModel(): JunkDeleteOutcomeModel =
    JunkDeleteOutcomeModel(
        file = junkFile.toModel(),
        status = status.toModel(),
        authorizationId = authorizationId,
    )

internal fun KitDeleteStatus.toModel(): DeleteStatus = when (this) {
    KitDeleteStatus.Deleted -> DeleteStatus.Deleted
    KitDeleteStatus.AuthorizationRequired -> DeleteStatus.AuthorizationRequired
    KitDeleteStatus.AlreadyAbsent -> DeleteStatus.AlreadyAbsent
    KitDeleteStatus.Changed -> DeleteStatus.Changed
    KitDeleteStatus.Unknown -> DeleteStatus.Unknown
    KitDeleteStatus.Failed -> DeleteStatus.Failed
}
```

设备信息：

```kotlin
// data/deviceinfo/internal/DeviceInfoRepositoryImpl.kt
package com.aicleanerstorage.spaceclean.data.deviceinfo.internal

import com.aicleanerstorage.devicekit.DeviceInfoRepository as DeviceKitInfoRepository
import com.aicleanerstorage.spaceclean.domain.deviceinfo.model.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.deviceinfo.model.CapacityInfoModel
import com.aicleanerstorage.spaceclean.domain.deviceinfo.model.DeviceHardwareInfoModel
import com.aicleanerstorage.spaceclean.domain.deviceinfo.port.DeviceInfoRepository

/** 用 device-kit 的实现读取设备信息；本类只做类型转换与空值兜底。 */
internal class DeviceInfoRepositoryImpl(
    private val device: DeviceKitInfoRepository,
) : DeviceInfoRepository {
    override suspend fun readCpuUsagePercent(previousPercent: Int?): Int? =
        device.readCpuUsagePercent(previousPercent)

    override fun batteryInfo(): BatteryInfoModel = device.batteryInfo().toModel()

    override fun memoryInfo(): CapacityInfoModel = device.memoryInfo().toModel()

    override fun internalStorageInfo(): CapacityInfoModel = device.internalStorageInfo().toModel()

    override fun hardwareInfo(): DeviceHardwareInfoModel = device.hardwareInfo().toModel()

    override fun cpuTemperatureC(): Float? = device.cpuTemperatureC()

    override fun batteryCurrentNowMa(): Float? = device.batteryCurrentNowMa()

    override fun batteryCurrentAverageMa(): Float? = device.batteryCurrentAverageMa()
}
```

`DeviceInfoMapper.kt` 里每个 `toModel()` 都是一行字段映射（kit 与 domain 保持同名字段，避免 UI 二次改名）。

网络：

```kotlin
// data/network/internal/NetworkRepositoryImpl.kt
package com.aicleanerstorage.spaceclean.data.network.internal

import com.aicleanerstorage.networkkit.NetworkRepository as NetworkKitRepository
import com.aicleanerstorage.spaceclean.domain.network.model.NetworkInfoModel
import com.aicleanerstorage.spaceclean.domain.network.model.NetworkScanResultModel
import com.aicleanerstorage.spaceclean.domain.network.model.NetworkSpeedModel
import com.aicleanerstorage.spaceclean.domain.network.model.NetworkUsageInfoModel
import com.aicleanerstorage.spaceclean.domain.network.port.NetworkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** 用 network-kit 实现网络状态、扫描、测速与流量统计。 */
internal class NetworkRepositoryImpl(
    private val network: NetworkKitRepository,
    scope: CoroutineScope,
) : NetworkRepository {
    override val lastScan: StateFlow<NetworkScanResultModel?> =
        network.lastScan
            .map { it?.toModel() }
            .stateIn(scope, SharingStarted.Eagerly, network.lastScan.value?.toModel())

    override fun readNetworkInfo(): NetworkInfoModel = network.readNetworkInfo().toModel()

    override fun isNetworkAvailable(): Boolean = network.isNetworkAvailable()

    override fun isWifiConnected(): Boolean = network.isWifiConnected()

    override fun hasNetworkUsageAccess(): Boolean = network.hasNetworkUsageAccess()

    override suspend fun readNetworkUsage(): NetworkUsageInfoModel = network.readNetworkUsage().toModel()

    override suspend fun runSpeedTest(onProgress: (NetworkSpeedModel) -> Unit): NetworkSpeedModel =
        network.runSpeedTest { onProgress(it.toModel()) }.toModel()

    override suspend fun scanWifi(): NetworkScanResultModel = network.scanWifi().toModel()

    override fun clearLastScan() = network.clearLastScan()
}
```

### 2.6 DI 装配

```kotlin
// app/di/DataModule.kt（目标态）
package com.aicleanerstorage.spaceclean.app.di

import android.content.ComponentName
import com.aicleanerstorage.cleanerkit.CleanerConfig
import com.aicleanerstorage.cleanerkit.CleanerKit
import com.aicleanerstorage.cleanerkit.clean.CleanRepository as CleanKitRepository
import com.aicleanerstorage.cleanerkit.clean.CleanRepository as CleanKitRepository
import com.aicleanerstorage.devicekit.DeviceInfoRepository as DeviceKitInfoRepository
import com.aicleanerstorage.devicekit.DeviceKit
import com.aicleanerstorage.networkkit.NetworkKit
import com.aicleanerstorage.networkkit.NetworkRepository as NetworkKitRepository
import com.aicleanerstorage.spaceclean.app.background.AppRuntimeService
import com.aicleanerstorage.spaceclean.base.permission.MediaDeletionHost
import com.aicleanerstorage.spaceclean.base.storage.PreferencesUtil
import com.aicleanerstorage.spaceclean.base.toolkit.antivirus.TrustlookVirusScanner
import com.aicleanerstorage.spaceclean.data.applock.internal.AppLockRepositoryImpl
import com.aicleanerstorage.spaceclean.data.appusage.internal.AppUsageRepositoryImpl
import com.aicleanerstorage.spaceclean.data.battery.internal.BatteryHistoryRepositoryImpl
import com.aicleanerstorage.spaceclean.data.battery.internal.BatteryHistorySampler
import com.aicleanerstorage.spaceclean.data.deviceinfo.internal.DeviceInfoRepositoryImpl
import com.aicleanerstorage.spaceclean.data.files.internal.ManagedFileRepositoryImpl
import com.aicleanerstorage.spaceclean.data.junkclean.internal.JunkCleanRepositoryImpl
import com.aicleanerstorage.spaceclean.data.network.internal.NetworkRepositoryImpl
import com.aicleanerstorage.spaceclean.data.notificationcleaner.StorageCleanNotificationListener
import com.aicleanerstorage.spaceclean.data.notificationcleaner.internal.NotificationCleanerRepositoryImpl
import com.aicleanerstorage.spaceclean.data.security.internal.VirusSecurityRepositoryImpl
import com.aicleanerstorage.spaceclean.data.settings.internal.SettingsRepositoryImpl
import com.aicleanerstorage.spaceclean.data.similarphotos.internal.SimilarPhotoRepositoryImpl
import com.aicleanerstorage.spaceclean.data.whatsapp.internal.WhatsAppCleanerRepositoryImpl
import com.aicleanerstorage.spaceclean.domain.applock.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.appusage.port.AppUsageRepository
import com.aicleanerstorage.spaceclean.domain.battery.port.BatteryHistoryRecorder
import com.aicleanerstorage.spaceclean.domain.battery.port.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.domain.battery.usecase.SyncBackgroundBatterySamplingUseCase
import com.aicleanerstorage.spaceclean.domain.deviceinfo.port.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.domain.files.port.ManagedFileRepository
import com.aicleanerstorage.spaceclean.domain.junkclean.port.JunkCleanRepository
import com.aicleanerstorage.spaceclean.domain.network.port.NetworkRepository
import com.aicleanerstorage.spaceclean.domain.notificationcleaner.port.NotificationCleanerRepository
import com.aicleanerstorage.spaceclean.domain.security.port.VirusScanner
import com.aicleanerstorage.spaceclean.domain.security.port.VirusSecurityRepository
import com.aicleanerstorage.spaceclean.domain.settings.port.SettingsRepository
import com.aicleanerstorage.spaceclean.domain.similarphotos.port.SimilarPhotoRepository
import com.aicleanerstorage.spaceclean.domain.whatsapp.port.WhatsAppCleanerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val APPLICATION_SCOPE = "applicationScope"

val dataModule: Module =
    module {
        single(named(APPLICATION_SCOPE)) { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

        single {
            CleanerKit.create(
                context = androidContext(),
                config = CleanerConfig(authorizeMediaDeletion = MediaDeletionHost::authorize),
            )
        }
        single { PreferencesUtil(androidContext()) }

        // kit 只作为 data 实现的输入，不再被页面直接注入。
        single<CleanKitRepository> { get<CleanerKit>().clean }
        single<DeviceKitInfoRepository> { DeviceKit.create(androidContext()) }
        single<NetworkKitRepository> { NetworkKit.create(androidContext()) }

        single {
            val kit = get<CleanerKit>()
            ManagedFileRepositoryImpl(androidContext(), kit.authorizeMediaDeletion, kit.mediaDeletion)
        }
        single<ManagedFileRepository> { get<ManagedFileRepositoryImpl>() }
        single<JunkCleanRepository> { JunkCleanRepositoryImpl(get(), get(named(APPLICATION_SCOPE))) }
        single<DeviceInfoRepository> { DeviceInfoRepositoryImpl(get()) }
        single<NetworkRepository> { NetworkRepositoryImpl(get(), get(named(APPLICATION_SCOPE))) }

        single<SettingsRepository> { SettingsRepositoryImpl(get()) }
        single<AppLockRepository> { AppLockRepositoryImpl(androidContext()) }
        single<AppUsageRepository> { AppUsageRepositoryImpl(androidContext()) }
        single<BatteryHistoryRepository> { BatteryHistoryRepositoryImpl(androidContext()) }
        single<BatteryHistoryRecorder> { BatteryHistorySampler(get(), get(), Dispatchers.IO) }
        single<VirusScanner> {
            TrustlookVirusScanner(androidContext(), com.aicleanerstorage.spaceclean.BuildConfig.TRUSTLOOK_API_KEY)
        }
        single<VirusSecurityRepository> { VirusSecurityRepositoryImpl(androidContext()) }
        single<WhatsAppCleanerRepository> { WhatsAppCleanerRepositoryImpl(get()) }
        single<SimilarPhotoRepository> { SimilarPhotoRepositoryImpl(get()) }
        single<NotificationCleanerRepository> {
            val context = androidContext()
            NotificationCleanerRepositoryImpl(
                context = context,
                listener = ComponentName(context, StorageCleanNotificationListener::class.java),
                retainContent = false,
            )
        }
        single {
            val context = androidContext()
            val settingsRepository = get<SettingsRepository>()
            val appLockRepository = get<AppLockRepository>()
            SyncBackgroundBatterySamplingUseCase(
                settingsRepository = settingsRepository,
                syncRuntime = { AppRuntimeService.reconcile(context, appLockRepository, settingsRepository) },
            )
        }
    }
```

`PresentationModule` 只改工厂接收者：

```kotlin
viewModel(named<AppDestination.VideosManager>()) {
    MediaManagerViewModel(
        repository = get(),
        loadFiles = ManagedFileRepository::loadVideos,
        defaultTabs = listOf("All", "DCIM", "Download"),
        detailDestination = { AppDestination.VideosDetail(it) },
        ioDispatcher = Dispatchers.IO,
    )
}
// LargeFiles / Documents 同理，把 FileRepository::loadXxx 换成 ManagedFileRepository::loadXxx
```

### 2.7 ViewModel / UI 改动示例

`JunkCleanViewModel`：构造参数和类型替换；`performFullScan` / `deleteFileItems` / `cleanMemory`
改成 `Result` 的 `getOrElse` 分支，其余状态机不变：

```kotlin
// 构造参数：CleanRepository -> JunkCleanRepository
class JunkCleanViewModel(
    private val cleanRepository: JunkCleanRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val scanPreviewMinMillis: Long = SCAN_PREVIEW_MIN_MILLIS,
    private val cleaningAnimationMinMillis: Long = CLEANING_ANIMATION_MIN_MILLIS,
) : MviViewModel<JunkCleanUiState, JunkCleanAction, JunkCleanEvent>(initialState = JunkCleanUiState()) {
    // 其余字段与 onAction 不变
}

// runScan()：保留 viewModelScope.launch 结构，只替换扫描调用与失败分支
private fun runScan() {
    scanJob =
        viewModelScope.launch {
            cleanRepository.clearSession()
            val scanStartedAt = System.currentTimeMillis()
            dispatch(JunkScanStarted)
            observeProgress()
            val result =
                cleanRepository.scan().getOrElse {
                    progressJob?.cancel()
                    dispatch(JunkFailed(JunkError.ScanFailed, duringScan = true))
                    return@launch
                }
            progressJob?.cancel()
            dispatch(JunkScanCompleted(result))
            delayRemainingScanAnimation(scanStartedAt)
            loadPreview(result)
        }
}

// runCleaning(files)：以下片段位于 viewModelScope.launch 内，只替换删除/内存清理两处
val outcomes =
    cleanRepository.deleteFiles(files) { stopCleaning }.getOrElse {
        dispatch(JunkFailed(JunkError.CleanFailed))
        return@launch
    }
val deleted = outcomes.filter { it.deleted }
val pending = outcomes.filter { !it.deleted && it.authorizationId != null }
val failed = outcomes.filter { !it.deleted && it.authorizationId == null }
val memoryResult = if (stopCleaning) null else cleanRepository.cleanMemory().getOrNull()
```

> `deleteFiles` 的逐文件状态仍在成功值里；`authorizationId` 非空的分支、待授权事件、停止清理逻辑全部不变。
> 迁移期保留 `withContext(ioDispatcher)`；P6 把 IO 调度下沉到适配器后再删（见 2.5 的说明）。

`FileListManagerViewModel` / `MediaManagerViewModel`：

```kotlin
class FileListManagerViewModel(
    private val repository: ManagedFileRepository,
    private val loadFiles: suspend ManagedFileRepository.() -> List<ManagedFileModel>,
    private val iconKind: FileListIconKind,
    private val detailDestination: (Int) -> AppDestination,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    // ...
) : MviViewModel<FileListManagerUiState, FileManagerAction, FileManagerEvent>(FileListManagerUiState(iconKind = iconKind))
```

`IndexedFileItem` 与 UI 展示模型：

```kotlin
data class IndexedFileItem(
    val id: Int,
    val file: ManagedFileModel,
)

data class FileImageDisplayItem(
    val id: Int,
    val name: String,
    val meta: String,
    val sizeLabel: String,
    val paletteIndex: Int,
    val uri: String?,
    val bucketName: String?,
    val path: String?,
    val bestPhoto: Boolean = false,
)

fun managedFileModelToImageDisplayItem(
    id: Int,
    file: ManagedFileModel,
    bestPhoto: Boolean = false,
): FileImageDisplayItem =
    FileImageDisplayItem(
        id = id,
        name = file.name,
        meta = "${formatManagedFileDate(file.modifiedSeconds)} ${FormatUtil.formatSize(file.sizeBytes)}",
        sizeLabel = FormatUtil.formatSize(file.sizeBytes),
        paletteIndex = id - 1,
        uri = file.uri,
        bucketName = file.bucketName,
        path = file.path,
        bestPhoto = bestPhoto,
    )
```

`DeviceInfoUiState` / `NetworkScanUiState` 只换类型：

```kotlin
data class DeviceInfoUiState(
    val mode: DeviceInfoMode = DeviceInfoMode.Device,
    val battery: BatteryInfoModel = EMPTY_BATTERY_INFO,
    val memory: CapacityInfoModel = EMPTY_MEMORY_INFO,
    val storage: CapacityInfoModel = EMPTY_STORAGE_INFO,
    val hardware: DeviceHardwareInfoModel = EMPTY_HARDWARE_INFO,
    // ...
)

data class NetworkScanUiState(
    val networkInfo: NetworkInfoModel = NetworkInfoModel(),
    val scan: NetworkScanResultModel? = null,
    // ...
)
```

`CleanItem` 去掉 `JunkFile.formatFileSize` 依赖：

```kotlin
data class CleanItem(
    val junkFile: JunkFileModel,
    val isChecked: Boolean = true,
) {
    val category: JunkCategory get() = junkFile.category
    val fileSize: Long get() = junkFile.sizeBytes
    val formattedSize: String get() = FormatUtil.formatSize(junkFile.sizeBytes)
    val fileName: String get() = junkFile.name
}
```

### 2.8 门禁增量（`scripts/check-rules.ps1`）

在现有 14 条之后追加；`viewKitIsolation` 在 P5 已改为零容忍，baseline 文件已删除。
新增 5 条：`viewKitIsolation`、`domainModelNaming`、`errorComments`、`publicTypes`、`naming`；
收紧 3 条：`layerDirection`（base 方向）、`dataContracts`（只禁 public interface）、`dataVisibility`（全 data 目录）。

```powershell
# 2f. pages never depend on first-party kit packages.
if (Test-CheckEnabled 'viewKitIsolation') {
    $pattern = '^import com\.aicleanerstorage\.(cleanerkit|devicekit|networkkit)\.'
    $hits = @()
    foreach ($dir in @((Join-Path $src "$pkgPath/app/view"), (Join-Path $src "$pkgPath/app/root"))) {
        if (-not (Test-Path -LiteralPath $dir)) { continue }
        $hits += Get-KotlinFiles $dir | Select-String -Pattern $pattern
    }
    if ($hits.Count -eq 0) { Add-Pass 'viewKitIsolation' } else {
        $hits | Select-Object -First 20 | ForEach-Object {
            Add-Failure 'viewKitIsolation' ("{0}:{1} view must depend on domain ports, not kit" -f (RelativePath $_.Path), $_.LineNumber)
        }
    }
}
```

```powershell
# 11. domain model naming: data class in domain/<feature>/model must be <Concept>Model.
if (Test-CheckEnabled 'domainModelNaming' -and (Test-Path -LiteralPath $domainDir)) {
    $bad = @()
    Get-ChildItem -Directory $domainDir -ErrorAction SilentlyContinue |
        ForEach-Object { Join-Path $_.FullName 'model' } |
        Where-Object { Test-Path -LiteralPath $_ } |
        ForEach-Object {
            foreach ($file in Get-KotlinFiles $_) {
                $text = Get-Content -LiteralPath $file.FullName -Raw
                foreach ($type in [regex]::Matches($text, '(?m)^(?:(?:public|internal) )?data class (\w+)')) {
                    $name = $type.Groups[1].Value
                    if ($name -cnotmatch 'Model$' -or $name -cne $file.BaseName) {
                        $bad += ("{0}: model data class must be <Concept>Model and match the file" -f (RelativePath $file.FullName))
                    }
                }
            }
        }
    if ($bad.Count -eq 0) { Add-Pass 'domainModelNaming' } else {
        $bad | Select-Object -First 20 | ForEach-Object { Add-Failure 'domainModelNaming' $_ }
    }
}
```

```powershell
# 12. every error branch in domain/<feature>/error carries a KDoc explaining when it happens.
if (Test-CheckEnabled 'errorComments' -and (Test-Path -LiteralPath $domainDir)) {
    $missing = @()
    Get-ChildItem -Directory $domainDir -ErrorAction SilentlyContinue |
        ForEach-Object { Join-Path $_.FullName 'error' } |
        Where-Object { Test-Path -LiteralPath $_ } |
        ForEach-Object {
            foreach ($file in Get-KotlinFiles $_) {
                $lines = Get-Content -LiteralPath $file.FullName
                for ($i = 0; $i -lt $lines.Count; $i++) {
                    if ($lines[$i] -notmatch '^\s*data (object|class)\s+\w+') { continue }
                    $previous = $i - 1
                    while ($previous -ge 0 -and $lines[$previous].Trim() -eq '') { $previous-- }
                    if ($previous -lt 0) { continue }
                    if ($lines[$previous].Trim() -notmatch '^(\/\*\*|\*|\/\/)') {
                        $missing += ("{0}:{1}" -f (RelativePath $file.FullName), ($i + 1))
                    }
                }
            }
        }
    if ($missing.Count -eq 0) { Add-Pass 'errorComments' } else {
        $missing | Select-Object -First 20 | ForEach-Object { Add-Failure 'errorComments' $_ }
    }
}
```

```powershell
# 13. one top-level public type per file (exceptions are shared rule, not profile values).
if (Test-CheckEnabled 'publicTypes') {
    $exceptions = '(Contract|ErrorText|Previews|Reducer|Models)\.kt$'
    $exact = @('PreviewFixtures.kt', 'Theme.kt', 'AppThemeTokens.kt', 'AppModule.kt',
        'CollectEffects.kt', 'AppDestination.kt', 'BatteryChartModels.kt')
    $bad = Get-KotlinFiles $src | Where-Object {
        $_.Name -notmatch $exceptions -and $_.Name -notin $exact
    } | ForEach-Object {
        $text = Get-Content -LiteralPath $_.FullName -Raw
        $types = [regex]::Matches($text, '(?m)^(?:public )?(?:(?:data|sealed|enum|value|abstract|open) )*(?:class|interface|object) (\w+)')
        if ($types.Count -gt 1 -or ($types.Count -eq 1 -and $types[0].Groups[1].Value -cne $_.BaseName)) {
            "{0}: public type/file mismatch or multiple public types" -f (RelativePath $_.FullName)
        }
    }
    if ($bad.Count -eq 0) { Add-Pass 'publicTypes' } else {
        $bad | Select-Object -First 20 | ForEach-Object { Add-Failure 'publicTypes' $_ }
    }
}
```

```powershell
# 14. naming: Impl suffix only on <Port>Impl; const val in UPPER_SNAKE_CASE.
if (Test-CheckEnabled 'naming') {
    $bad = Get-KotlinFiles $src | Select-String -CaseSensitive -Pattern `
        '^(internal |)(abstract |open |sealed |)(class|interface|object) (?!\w*RepositoryImpl\b)\w+Impl\b|^\s*(private )?const val (?!([A-Z][A-Z0-9_]*)\b)'
    if ($bad) {
        $bad | Select-Object -First 20 | ForEach-Object {
            Add-Failure 'naming' ("{0}:{1}: {2}" -f (RelativePath $_.Path), $_.LineNumber, $_.Line.Trim())
        }
    } else {
        Add-Pass 'naming'
    }
}
```

```powershell
# 15. base must not depend on data/app/domain rules; only base/toolkit may use domain.port.
if (Test-CheckEnabled 'layerDirection' -and (Test-Path -LiteralPath $baseDir)) {
    $bad = @()
    foreach ($file in Get-KotlinFiles $baseDir) {
        $isToolkit = $file.FullName -match '[\\/]base[\\/]toolkit[\\/]'
        $patterns = @(
            '^import com\.aicleanerstorage\.spaceclean\.(data|app)\.',
            '^import com\.aicleanerstorage\.spaceclean\.domain\.(rule|usecase)\.'
        )
        if (-not $isToolkit) { $patterns += '^import com\.aicleanerstorage\.spaceclean\.domain\.port\.' }
        foreach ($pattern in $patterns) {
            foreach ($hit in (Select-String -LiteralPath $file.FullName -Pattern $pattern)) {
                $bad += ("{0}:{1}: {2}" -f (RelativePath $file.FullName), $hit.LineNumber, $hit.Line.Trim())
            }
        }
    }
    if ($bad.Count -eq 0) { Add-Pass 'layerDirection' } else {
        $bad | Select-Object -First 20 | ForEach-Object { Add-Failure 'layerDirection' $_ }
    }
}
```

同时把 `dataContracts` 放宽为「禁止 public interface，允许 internal」：

```powershell
$bad = Get-KotlinFiles $dataDir | Select-String -Pattern '^(?:public )?interface '
```

`dataVisibility` 扩到全 data（Android 组件放行）：

```powershell
$componentNames = @('StorageCleanNotificationListener.kt')
$bad = Get-KotlinFiles $dataDir |
    Where-Object { $_.Name -notin $componentNames } |
    Select-String -Pattern '^(?:public )?(?:abstract |open )?class \w+'
```

profile 打开新检查：

```json
{
  "checks": {
    "domainPurity": true,
    "layerDirection": true,
    "dataImplementationIsolation": true,
    "dataVisibility": true,
    "dataContracts": true,
    "domainKitIsolation": true,
    "viewKitIsolation": true,
    "domainModelNaming": true,
    "errorComments": true,
    "publicTypes": true,
    "naming": true,
    "viewDoesNotImportRoot": true,
    "uiPartStateless": true,
    "errorText": true,
    "collectEffects": true,
    "backHandler": true,
    "effectLocation": true,
    "packageMatchesDirectory": true,
    "localeParity": true
  }
}
```

### 2.9 AGENTS.md 结构（新建，从 ref 裁剪）

```text
# 项目规则

## 规则入口与 Profile
- AGENTS.md 是硬规则与优先级的唯一来源；profiles/android-compose-mvi.json 只放项目差异。
- 优先级：用户当前指令 > 本文件 > Android/Compose 平台规则 > 通用工程规则。
- 代码与规范不一致时默认代码是错的，除非登记在 docs/rules/exceptions.md。
- 检查脚本与规范不一致时，同一次变更里同时改脚本或规则。

## 定位
Android 单 Activity + Compose + MVI + Koin + 类型化导航；cleanerkit / devicekit / networkkit 是 :app 内的能力包（不是独立模块）。

## 包结构
（贴 docs/mvi-architecture.md 第 6–27 行的表 + 依赖允许列表）
- app/view 允许 base、domain；不得 import data、kit。
- base 不得依赖 data/app/domain.rule|usecase；base/toolkit 可以实现 domain.port。
- data 允许 domain、base、kit；不得 import app。

## domain
- 业务优先：domain/<feature>/{model,port,rule,usecase,error}。
- 顶层业务数据类 <Concept>Model，文件名与类型名一致；枚举保留语义名。
- 写操作返回 Result；读操作直接返回值；错误分支写"何时发生/调用方怎么处理"。
- 只依赖自身 + Kotlin/JDK（coroutines 例外见 exceptions.md）。
- 禁止 android/androidx/Context/Uri/Compose/Dispatchers/资源 id/SQL 字符串。

## data
- 只做实现：适配器、仓库、介质读取；不定义业务规则。
- 类默认 internal；Android 组件（Service/Receiver/Provider/Activity）例外并登记。
- 命名按角色：聚合 <概念>Repository / 单介质 <概念><介质>Repository / 适配器 <能力><介质> / Default<概念>Repository 或 <端口名>Impl。
- 异常在实现层收口成 Result；CancellationException 继续传播。
- 直接返回 domain 模型；出现 Entity/DTO 再建 mapper/。

## UI / 反过度防御 / 代码风格 / 测试 / 提交 / 不要提前建
（从 ref AGENTS.md 第 118–225 行裁剪；UI 细节继续以 docs/mvi-architecture.md 为准）

## 检查
.\scripts\check-rules.ps1
.\gradlew.bat :app:compileDebugKotlin
.\gradlew.bat :app:testDebugUnitTest --tests "<相关测试>"
```

## 3. 分阶段执行

| 阶段 | 内容 | 产出 | 验证 | 估算 |
|---|---|---|---|---|
| P0 | 基础设施 | `data/internal/Catching.kt`、DI 的 `applicationScope`、`viewKitIsolation` baseline、profile 新开关、`docs/rules/exceptions.md`（coroutines 例外） | `check-rules` 仍绿；baseline 行数 = 76 | 0.5 人日 |
| P1 | files 域 | `ManagedFileRepository`；kit `FileRepositoryImpl` 下沉为 `data/files/internal/ManagedFileRepositoryImpl`（删除 kit 接口和转发类）；10 个 view 文件换 `ManagedFileModel`；删除改 `Result`；适配器测试 | 文件页编译通过；`view → FileRepository` 归零 | 1–1.5 |
| P2 | junkclean 域（已完成） | 7 个模型 + 1 个端口；kit `CleanRepositoryImpl` 下沉为 `data/junkclean/internal/JunkCleanRepositoryImpl`（删除 kit 接口）；`CleanSessionStore` 改为 domain 模型；约 20 个 view 文件换类型；`JunkError` 补 KDoc；`JunkCleanRepositoryTest` | 垃圾清理页 + 单测通过 | 1.5–2 |
| P3 | deviceinfo 域（已完成） | 5 个模型 + 端口；`devicekit.internal.DeviceInfoRepositoryImpl` 下沉为 `data/deviceinfo/internal/DeviceInfoRepositoryImpl`（删除 kit 接口和 `DeviceKit` 根对象）；`DeviceInfoModels.kt` 等 11 个文件换类型 | 首页/设备/电池/引导页通过 | 1 |
| P4 | network 域（已完成） | 9 个模型 + 端口；`networkkit.internal.NetworkRepositoryImpl` 下沉为 `data/network/internal/NetworkRepositoryImpl`（删除 kit 接口和 `NetworkKit` 根对象）；`NetworkScan/Speed/Usage` 13 个文件换类型 | 三个网络页通过 | 1 |
| P5 | 收口（已完成） | 删 baseline、`viewKitIsolation` 零容忍；删除手动媒体授权死代码；补存量 KDoc；`SettingsRepository` 写方法改 `Result`；删空目录；`StorageCleanNotificationListener` / `SettingsRepositoryImpl` / `BatteryHistorySampler` 等归位；改旧测试包名；`BatteryChartModel.kt` 改名 `BatteryChartModels.kt` | `check-rules` 17 条全绿；`view → kit` 0；`data` 实现类 KDoc 基本覆盖 | 1.5 |
| P6 | 规则与流程（已完成，IO 下沉除外） | `AGENTS.md`、`docs/rules/00-precedence.md`、`docs/rules/exceptions.md`、`.githooks/pre-commit`、`scripts/install-hooks.ps1`、profile 补 `pages`/`destinations`/`navHost`；`domainModelNaming` / `errorComments` / `publicTypes` / `naming` 已入门禁 | 新克隆执行 `install-hooks.ps1` 后提交被拦截；`check-rules` 21 条全绿 | 0.5–1 |

合计约 **7–8 人日**，每个阶段可独立合并。

### 每阶段 Definition of Done

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts\check-rules.ps1
.\gradlew.bat :app:compileDebugKotlin
.\gradlew.bat :app:testDebugUnitTest --tests "<本阶段相关测试>"
```

- [ ] diff 中除类型、包名、`Result` 分支外无行为改动
- [ ] `view → kit` 的存量只减不增
- [ ] 新增/移动的类有类级 KDoc；错误分支有"何时发生"
- [ ] 测试 import 与新类型同步；新增适配器测试
- [ ] DI 绑定全部指向 domain 端口，实现类仍 `internal`

## 4. 验收指标

| 指标 | 通过条件 |
|---|---|
| `app/view|app/root → kit` | 0（零容忍，baseline 已删除） |
| `app/view|app/root → data` | 0（保持） |
| domain 页面覆盖 | 25/25 个页面的数据入口都是 domain port |
| data public 类型 | 仅 Android 组件；其余 `internal` |
| 写操作 Result | `ManagedFileRepository`、`JunkCleanRepository`、`SettingsRepository` 全部覆盖 |
| domain KDoc | model/port ≥ 90% |
| data KDoc | 实现类 100% |
| 门禁 | 21 条全绿 |
| `AGENTS.md` | 存在，且 `docs/rules/exceptions.md` 有 coroutines 登记 |
| 构建/测试 | `:app:assembleDebug` + `:app:testDebugUnitTest` 通过 |

## 5. 风险与回滚

| 风险 | 应对 |
|---|---|
| Koin 类型名冲突（domain `DeviceInfoRepository` / `NetworkRepository` 与 kit 同名） | DI 里给 kit 类型起别名 `DeviceKitInfoRepository` / `NetworkKitRepository`，只在 `DataModule` 内部使用 |
| `stateIn` 需要长生命周期 scope | DI 提供 `named(APPLICATION_SCOPE)` 的 `CoroutineScope(SupervisorJob() + Dispatchers.Default)`；不放进 ViewModel |
| UI 类型大范围改名导致漏改 | 每阶段只改一簇，靠编译器暴露；不改字符串/资源 |
| 删除语义从"抛异常"改成 `Result` 后页面漏判 | `FileOperations.changeFiles` 的 `isSuccessful` 显式检查 `isSuccess`；补删除失败单测 |
| `publicTypes` 误报（`*Models.kt` 等） | 把例外写进脚本常量，并在 AGENTS.md 列明；`BatteryChartModel.kt` 改名 `BatteryChartModels.kt` |
| 迁移与需求并行 | 每阶段独立 PR；P0 的 baseline 保证不新增越层 |

回滚：每阶段是纯类型/包搬迁 + 适配器新增，`git revert` 单个 PR 即可；P0 的门禁可单独回滚（删 baseline 与 profile 开关）。

## 附录 A：如果必须 layer-first（不推荐）

```text
domain/
├─ model/
│  ├─ applock/AppLockAppModel.kt
│  ├─ appusage/AppUsageInfoModel.kt
│  ├─ battery/{BatteryHistoryOwner,BatteryHistorySampleModel}.kt
│  ├─ deviceinfo/{BatteryInfoModel,CapacityInfoModel,...}.kt
│  ├─ files/{FileDeleteSummaryModel,FileKind,ManagedFileModel}.kt
│  ├─ junkclean/{CleanupSummaryModel,JunkCategory,JunkFileModel,...}.kt
│  ├─ network/{NetworkInfoModel,NetworkSpeedModel,...}.kt
│  ├─ notificationcleaner/...kt
│  ├─ security/...kt
│  └─ whatsapp/...kt
├─ error/{DocumentError...}        ← 本项目对应 JunkError / ScanEngineConfigurationException
├─ port/{AppLockRepository,ManagedFileRepository,JunkCleanRepository,...}.kt
├─ rule/{AppLockPinRule,AppLockMonitoringRule}.kt
└─ usecase/SyncBackgroundBatterySamplingUseCase.kt
```

改动量：34 个 domain 文件全部移动 + 全仓 import 改写，约 1.5 人日纯搬迁，不产生功能收益；`AGENTS.md` 与门禁也要跟着改成层优先。除非要「目录逐字对齐 ref」，否则不建议。

## 附录 B：domain 严格无 coroutines（D2 的替代方案）

如果坚持 ref 的"domain 只依赖 Kotlin/JDK"，把响应式状态改成回调：

```kotlin
interface JunkCleanRepository {
    suspend fun scan(onProgress: (JunkScanProgressModel) -> Unit): Result<JunkScanResultModel>
    suspend fun lastResult(): JunkScanResultModel?
    // ...
}

interface NetworkRepository {
    suspend fun lastScan(): NetworkScanResultModel?
    suspend fun scanWifi(): NetworkScanResultModel
    // ...
}
```

代价：设备页不再实时收到扫描页的结果，需要页面每次进入时主动 `lastScan()`；`JunkCleanViewModel` 的 `observeProgress()` 改成把 `onProgress` 直接 `dispatch`。约多 0.5 人日，且要改 2 个页面的刷新时机。建议按 D2 登记例外，不采用本方案。

## 附录 C：类型/字段改名对照表

| 现在（kit） | 目标（domain） | 字段变化 |
|---|---|---|
| `JunkFile` | `JunkFileModel` | `filePath→path`、`fileName→name`、`fileSize→sizeBytes`、`lastModified→modifiedMillis`；删除 `formattedSize`（UI 用 `FormatUtil.formatSize`） |
| `JunkCategory` | `JunkCategory`（同包名换域） | 无 |
| `ScanProgress` | `JunkScanProgressModel` | 无 |
| `ScanResult` | `JunkScanResultModel` | `junkFiles→files` |
| `JunkDeleteOutcome` | `JunkDeleteOutcomeModel` | `junkFile→file` |
| `MemoryCleanResult` | `JunkMemoryCleanModel` | 无 |
| `DeleteStatus` | `DeleteStatus`（domain/files） | 无 |
| `ManagedFileItem` | `ManagedFileModel` | `uri: FileUri→String`、`type→kind` |
| `FileUri` | —（UI 用 `String`） | 无 |
| `BatteryInfo` | `BatteryInfoModel` | 无 |
| `CapacityInfo` / `MemoryInfo` / `StorageInfo` | `CapacityInfoModel` | 无 |
| `DeviceCpuInfo` | `DeviceCpuInfoModel` | 无 |
| `DeviceSensorInfo` | `DeviceSensorInfoModel` | 无 |
| `DeviceHardwareInfo` | `DeviceHardwareInfoModel` | 无 |
| `NetworkInfo` | `NetworkInfoModel` | 无 |
| `NetworkTransport` | `NetworkTransport`（domain/network） | 无 |
| `NetworkDeviceInfo` | `NetworkDeviceModel` | 无 |
| `NetworkScanResult` | `NetworkScanResultModel` | 无 |
| `NetworkSpeed` | `NetworkSpeedModel` | 无 |
| `SpeedTestPhase` | `SpeedTestPhase`（domain/network） | 无 |
| `NetworkUsageApp` | `NetworkUsageAppModel` | 无 |
| `NetworkUsageInfo` | `NetworkUsageInfoModel` | 无 |
| `TrafficSource` | `TrafficSource`（domain/network） | 无 |

> 字段改名只在 junkclean 簇发生；device/network 保持同名字段，避免 UI 二次改动。
