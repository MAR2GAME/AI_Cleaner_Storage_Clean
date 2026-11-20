# 数据类与类型排查（2026-11-21）

- 范围：`cleaner-kit`、`device-kit`、`network-kit` 的公开模型，以及 app 里与这三个 kit 直接相邻的状态/结果模型
- 方法：逐个类型查构造点、读取点、公开 API 返回路径；没有改代码
- 规模：三个 kit 共 24 个 public data class、41 个 public 类型；app 主源码有 206 个 data class、35 个 enum、41 个 sealed（app 全量不在此次逐个排查范围）
- 结论：**有 14 处明确可以删/并/降级为计算属性，8 处中等代价，6 处不建议动。问题不是“类型多”，而是“同一事实存了两三份”。**

## 一句话结论

最该先删的是**死类型和派生字段**：

- 死类型：`CleanResult`（kit 里没有任何 API 返回它）、`JunkCleanResultUiState`（8 个字段只用到 4 个）
- 纯派生字段：`ScanResult` 的 3 个聚合、`FileDeleteResult` 的 3 个聚合、`JunkDeleteOutcome` 的 2 个派生字段、`MemoryInfo` 的 3 个字段、`StorageInfo.usedBytes`
- 同构类型：`BatteryStatusInfo` → `BatteryInfo`、`NetworkSpeedProgress` + `NetworkSpeedResult` → 一个 `NetworkSpeed`、`MemoryInfo` + `StorageInfo` → 一个 `CapacityInfo`
- 纯重复字段：`NetworkUsageInfo.apps`、`NetworkUsageApp.uid`、`JunkFile.scanId`、`JunkCategory.displayName/description`

---

## 1. 明确可删/可并（低风险，优先做）

| # | 类型/字段 | 现状 | 建议 | 证据 |
| --- | --- | --- | --- | --- |
| 1.1 | `CleanResult` | kit 的 `CleanRepository` 没有任何方法返回它；kit 里只有声明和一个未使用的 import；app 只在 `finishCleaning` 里临时构造一下 | **删除**。app 直接用 `CleanupSummaryModel` / 局部变量 | [CleanResult.kt](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/model/CleanResult.kt#L6-L18)、[CleanRepository.kt](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/CleanRepository.kt#L16-L34)、[JunkCleanViewModel.kt:371-385](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/interaction/JunkCleanViewModel.kt#L371-L385) |
| 1.2 | `MemoryCleanResult.killedCount` | 永远传 `0`；app 只把它塞进 summary，UI 从不显示“清理进程数” | 删字段；`cleanMemory()` 可只返回 `Long freedBytes`，或保留单字段类型 | [MemoryCleaner.kt:12-24](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/MemoryCleaner.kt#L12-L24)、[JunkCleanViewModel.kt:383-384](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/interaction/JunkCleanViewModel.kt#L383-L384) |
| 1.3 | `JunkDeleteOutcome.deleted` + `freedBytes` | 两者都能从 `status` 推出：`Deleted` 时 `freedBytes = junkFile.fileSize`，其余为 0；`status` 的默认值又反过来由这两个字段算 | 只保留 `junkFile`、`status`、`authorizationId`；`deleted`/`freedBytes` 改计算属性（调用方代码不变） | [JunkDeleteOutcome.kt:5-14](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/model/JunkDeleteOutcome.kt#L5-L14) |
| 1.4 | `ScanResult.totalSize` / `totalCount` / `categoryBreakdown` | 全部由 `junkFiles` 派生：`sumOf`、`size`、`groupBy`。构造时又算一遍 | 三个字段改计算属性，`ScanResult` 只留 `junkFiles` + `sharedStorageAccessLimited` | [ScanResult.kt:3-20](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/model/ScanResult.kt#L3-L20)、[CleanRepositoryImpl.kt:114-123](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/CleanRepositoryImpl.kt#L114-L123) |
| 1.5 | `FileDeleteResult.freedBytes` / `deletedCount` / `skippedCount` | 构造时由 `outcomes` 聚合而来；`FileRepositoryImpl` 就一行 `deleted.sumOf { ... }, deleted.size, final.size - deleted.size` | 三个字段改计算属性；`FileDeleteResult` 只留 `outcomes` | [FileDeleteResult.kt:3-14](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/files/model/FileDeleteResult.kt#L3-L14)、[FileRepositoryImpl.kt:199](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/files/internal/FileRepositoryImpl.kt#L199) |
| 1.6 | `JunkCategory.displayName` / `description` | 两个英文展示串，全工程 0 次读取；app 用 `R.string.category_*` 自己映射 | 删两个属性，枚举变纯值 | [JunkCategory.kt:3-13](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/model/JunkCategory.kt#L3-L13)、[JunkScanLoading.kt:100-109](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/ui/part/JunkScanLoading.kt#L100-L109) |
| 1.7 | `JunkFile.scanId` | 扫描时 `copy(scanId = scanId)` 写入，之后全工程 0 次读取 | 删字段 | [JunkFile.kt:12](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/model/JunkFile.kt#L12)、[CleanRepositoryImpl.kt:100](../cleaner-kit/src/main/kotlin/com/aicleanerstorage/cleanerkit/clean/internal/CleanRepositoryImpl.kt#L100) |
| 1.8 | `MemoryInfo.usedBytes` / `usagePercent` / `isTotalValid` | `usedBytes = total - available`、`usagePercent = used/total` 都是派生；`isTotalValid` 全工程 0 次读取（minSdk 29，`totalMem` 一直可用） | 只留 `totalBytes` + `availableBytes`，其余改计算属性 | [MemoryInfo.kt:3-9](../device-kit/src/main/kotlin/com/aicleanerstorage/devicekit/model/MemoryInfo.kt#L3-L9)、[DeviceInfoDataSource.kt:56-77](../device-kit/src/main/kotlin/com/aicleanerstorage/devicekit/internal/DeviceInfoDataSource.kt#L56-L77) |
| 1.9 | `StorageInfo.usedBytes` | `usedBytes = total - available`；`usagePercent` 已经是计算属性了 | 删 `usedBytes` 字段，改计算属性 | [StorageInfo.kt:3-11](../device-kit/src/main/kotlin/com/aicleanerstorage/devicekit/model/StorageInfo.kt#L3-L11) |
| 1.10 | `BatteryStatusInfo` | 只有 `statusText` + `isCharging`，却单独占一个类型、一个 repository 方法、一次读取；app 还要分别拿 `battery` 和 `batteryStatus` | **并入 `BatteryInfo`**：加 `status: BatteryStatus` 枚举 + `isCharging`；`batteryStatusInfo()` 删除 | [BatteryStatusInfo.kt:3-6](../device-kit/src/main/kotlin/com/aicleanerstorage/devicekit/model/BatteryStatusInfo.kt#L3-L6)、[DeviceInfoRepository.kt:12-14](../device-kit/src/main/kotlin/com/aicleanerstorage/devicekit/DeviceInfoRepository.kt#L12-L14) |
| 1.11 | `NetworkSpeedProgress` + `NetworkSpeedResult` | 两个类型字段几乎一样（download/upload/latency），一个多 `phase`，一个多 `measured`；`measured` 只有测试读，UI 不读 | 合成一个 `NetworkSpeed(downloadMbps, uploadMbps, latencyMs, phase)`；`measured` 要么删，要么在 UI 真正展示“估算” | [NetworkSpeedProgress.kt:3-10](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/model/NetworkSpeedProgress.kt#L3-L10)、[NetworkSpeedResult.kt:3-11](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/model/NetworkSpeedResult.kt#L3-L11) |
| 1.12 | `NetworkUsageInfo.apps` | 构造时 `apps = wifiApps`；app 只读 `wifiApps` / `cellularApps` / `fallbackApps`，从不读 `apps` | 删 `apps`，`wifiApps` 默认 `emptyList()` | [NetworkUsageInfo.kt:3-17](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/model/NetworkUsageInfo.kt#L3-L17)、[NetworkUsageContract.kt:78-86](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/networkusage/interaction/NetworkUsageContract.kt#L78-L86) |
| 1.13 | `NetworkUsageApp.uid` | kit 构造后没人读；app 只用 `source`/`rxBytes`/`txBytes`/`totalBytes` | 删字段；uid 在构造前用于解析包名，不需要带出来 | [NetworkUsageApp.kt:8-15](../network-kit/src/main/kotlin/com/aicleanerstorage/networkkit/model/NetworkUsageApp.kt#L8-L15) |
| 1.14 | app `JunkCleanResultUiState` | 8 个字段，UI 实际只用 `formattedFreedSpace`、`hasVisibleResult`、`failedCount`、`memoryFreedBytes`；其余是 `CleanupSummaryModel` 的拷贝 | **删除该类型**，`JunkCleanResultContent` 直接接 `CleanupSummaryModel?`，格式化放 UI | [JunkCleanContract.kt:26-35](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/interaction/JunkCleanContract.kt#L26-L35)、[JunkCleanReducer.kt:275-285](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/interaction/JunkCleanReducer.kt#L275-L285)、[JunkCleanResultContent.kt:36-37,90-106](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/ui/part/JunkCleanResultContent.kt#L36-L106) |
| 1.15 | app `SelectionSummary` | 三个字段都由 `groups` + `checkedEmptyCategories` 派生；构造一次后必须跟着选择变化同步，属于典型的派生状态 | 删类型，`JunkCleanUiState` 暴露 `checkedCount`/`checkedSize`/`checkedEmptyCategoryCount` 计算属性 | [JunkCleanContract.kt:37-41](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/interaction/JunkCleanContract.kt#L37-L41)、[JunkCleanReducer.kt:245-250](../app/src/main/java/com/aicleanerstorage/spaceclean/app/view/junkclean/interaction/JunkCleanReducer.kt#L245-L250) |

### 这一组做完的收益

- 三个 kit 的 public data class：24 → 约 20（删 `CleanResult`、`BatteryStatusInfo`，并掉 `NetworkSpeedProgress/Result`）；再把 `MemoryInfo`/`StorageInfo` 并成 `CapacityInfo` 后约 19
- 纯冗余字段：约 18 个
- app 清理结果从 4 层类型降到 2 层；`JunkCleanResultUiState` 和 `SelectionSummary` 两个类型消失

---

## 2. 值得做，但要改调用方（中等代价）

| # | 类型 | 现状 | 建议 | 代价/风险 |
| --- | --- | --- | --- | --- |
| 2.1 | `MemoryInfo` + `StorageInfo` | 两个类型都是 `totalBytes/availableBytes/usedBytes/usagePercent`，只差一个 `isTotalValid` | 合成一个 `CapacityInfo(totalBytes, availableBytes)`；`MemoryInfo`/`StorageInfo` 用 typealias 保留名字，或直接改名 | 语意上 RAM/磁盘不同；API 名和 app 构造点要改，但读点（`usedBytes`/`usagePercent`）不用改 |
| 2.2 | `NetworkUsageApp.TrafficSource` | 为一个“包名有没有”的分支开了 sealed interface + `App` data class + data object，共 3 个类型；app 还要 `as? TrafficSource.App` | 扁平化：`packageName: String?`、`appName: String?`，`isSystem` 由 `packageName == null` 计算；3 个类型 → 0 个新增类型 | 失去 sealed 的穷尽性；如果以后要加“VPN/热点”等来源，再考虑 sealed |
| 2.3 | `JunkFile.id` | 扫描时 `id = filePath`，和 `filePath` 完全重复；app 的选中逻辑用 `id` | 改计算属性 `val id get() = filePath`，或直接让 app 用 `filePath` 做 key | 构造函数位置参数会变，测试和扫描器要跟着改；收益是删一个重复字段 |
| 2.4 | `NetworkScanResult` 的 `ssid` / `deviceIp` | 与 `NetworkInfo.ssid` / `ip` 重复；app 里 `scan.ssid ?: networkInfo.ssid` 就是在补这个重复 | `NetworkScanResult` 内嵌 `network: NetworkInfo`，只留 `gatewayIp`/`dnsIp`/`devices`/`hasWifi` | 读点要改；`NetworkInfo` 多一个 `transport` 字段，也算合理 |
| 2.5 | app `NetworkUsageDisplayItem` | 6 个字段里 5 个是 `NetworkUsageApp` 的拷贝，只多一个 `isAggregate`；`buildNetworkUsageDisplayItems` 还会改 `displayBytes` | 改成 `data class NetworkUsageDisplayItem(val app: NetworkUsageApp, val displayBytes: Long)`，`isAggregate`/名称/字节都从 `app` 读 | UI 读点要改；`displayBytes` 归一化逻辑保留 |
| 2.6 | `ManagedFileItem.duplicatePeers` | 每个重复文件里嵌一份完整 `ManagedFileItem` 列表（递归结构）；kit 只用它做 `fileIdentity` 过滤，app 只用 `isNotEmpty()` | 改成 `peerIds: List<Long>` 或 `peerPaths: List<String>`；完整分组由 `loadDuplicateFiles(): List<List<...>>` 提供 | 删除授权前的安全校验要跟着改；收益是内存和嵌套类型都下降 |
| 2.7 | app `BatteryCurrentSample` + `BatteryTemperatureSample` | 两个 2 字段 data class，形状完全一样：`timestampMillis + Float` | 合成一个 `BatterySample(timestampMillis, value)`；图表各自的轴/单位不变 | 语义会变弱；如果希望保留“电流/温度”名字，就保持现状 |
| 2.8 | app `IndexedFileItem` | 只给 `ManagedFileItem` 加了一个 `id: Int` | 直接用 `ManagedFileItem.id`，或 `List<ManagedFileItem>.mapIndexed` 时就地用 index | 18 处使用；收益一般，优先级低 |

---

## 3. 不建议合并（保留独立性）

| 类型 | 原因 |
| --- | --- |
| `JunkFile` vs `ManagedFileItem` | 前者是“垃圾扫描结果”（category、duplicate 信息），后者是“媒体库条目”（uri、mime、bucket、type）；合并会得到一个什么都有的大 union，反而更难维护 |
| `CleanRepository` / `FileRepository` / `NetworkRepository` / `DeviceInfoRepository` | 这是 kit 对外的 API 边界，单实现不代表应该删接口；删了以后 app 直接依赖 impl，测试也没法替身 |
| `ScanProgress` vs `ScanResult` | 一个是流式进度（percent/currentCategory/foundCount），一个是终态快照；字段重合但生命周期不同 |
| `FileUri` | 单字段 value class，零运行时开销，能防止 `String` 路径/URI 混用；为了“少一个类型”删掉不划算 |
| `MediaDeletionRequest` / `MediaDeletionAuthorization` / `PendingAuthorizations` | 一个是对外请求句柄，一个是宿主手动授权状态机，一个是内部 ID 表；职责不同，合并会把授权时序塞回 model |
| `DeviceCpuInfo` / `DeviceSensorInfo` | 4 个 CPU 字段、7 个传感器布尔值，都是内聚的小快照；拆成散字段会让 `DeviceHardwareInfo` 变胖，收益不大 |
| `DeleteStatus` | 6 个值分别对应删除、待授权、已不存在、已变更、未知、失败；合并 `Unknown`/`Failed` 会丢错误语义 |
| `NetworkDeviceInfo` / `NetworkInfo` | 一个是扫描到的设备，一个是本机网络状态；没有重复到需要合并的程度 |

---

## 4. 专项：清理结果现在有 4 层

```text
cleaner-kit: CleanResult                 （kit 里没有任何 API 返回，只被 app 临时构造）
     ↓
app domain: CleanupSummaryModel          （5 个字段，其中 memoryProcessesKilled 永远 0）
     ↓
app ui:     JunkCleanResultUiState       （8 个字段，UI 只用 4 个）
     ↓
app ui:     JunkCleanUiState.stoppedSummary / cleanResult   （同一份结果存两个字段）
```

建议只保留两层：

```text
app domain: CleanupSummaryModel          （freedSpace, cleanedCount, failedCount, memoryFreedBytes）
     ↓
app ui:     JunkCleanUiState.summary     （一个字段；stopped/complete 由 phase 区分）
```

- `CleanResult` 删除
- `JunkCleanResultUiState` 删除
- `memoryProcessesKilled` 删除（它恒为 0，`hasVisibleResult` 也跟着简化）
- `stoppedSummary` + `cleanResult` 合并成 `summary`
- `formattedFreedSpace` 这类格式化字符串移到 UI，不放进 state

---

## 5. 专项：设备模型

现在 7 个公开类型：

```text
BatteryInfo(7 字段) + BatteryStatusInfo(2 字段)
DeviceCpuInfo(4) + DeviceSensorInfo(7 个 Boolean)
DeviceHardwareInfo(7 字段，含上面两个)
MemoryInfo(5) + StorageInfo(3)
```

可收敛为 5 个：

```text
BatteryInfo(level, health, temperature, voltage, technology, capacity, availableTime, status, isCharging)
CapacityInfo(totalBytes, availableBytes)        // memory + storage 共用
DeviceCpuInfo + DeviceSensorInfo + DeviceHardwareInfo   // 保留
```

`BatteryStatusInfo` 删除；`batteryStatusInfo()` 删除；`EMPTY_BATTERY_STATUS` 删除。
`MemoryInfo`/`StorageInfo` 的 `usedBytes`、`usagePercent` 全部改计算属性；app 的 `EMPTY_MEMORY_INFO`/`EMPTY_STORAGE_INFO` 从 5/3 个参数降到 2 个。

顺带：`BatteryInfo.health: String`、`BatteryInfo.availableTime: String`、`BatteryStatusInfo.statusText: String`、`DeviceHardwareInfo.screenSize/screenDensity: String` 都是展示串。它们不是“该不该合并”的问题，而是**该不该进模型**的问题；这次先记着，不建议和合并一起做。

---

## 6. 专项：网络模型

现在 8 个 data class：

```text
NetworkInfo(3) / NetworkTransport
NetworkScanResult(6) / NetworkDeviceInfo(3)
NetworkSpeedProgress(4) / NetworkSpeedResult(4) / SpeedTestPhase
NetworkUsageInfo(10) / NetworkUsageApp(5) / TrafficSource(3 个类型)
```

收敛后：

```text
NetworkInfo + NetworkTransport
NetworkScanResult(network: NetworkInfo, gatewayIp, dnsIp, devices, hasWifi)
NetworkDeviceInfo
NetworkSpeed(downloadMbps, uploadMbps, latencyMs, phase)      // 合并 Progress/Result
NetworkUsageInfo(wifi/cellular/fallback + isToday/needsUsageAccess)  // 删 apps
NetworkUsageApp(packageName: String?, appName: String?, rxBytes, txBytes) // 删 uid，扁平化 TrafficSource
```

`NetworkSpeedResult.measured` 目前只有单测读，UI 不读；要么在 UI 显示“估算值”，要么删掉。

---

## 7. 专项：app 里的 UI 投影类型（第二优先，不要和上面一起动）

这些不是“错误”，但确实是类型膨胀源：

- `FileManagerDisplayModels.kt` 里有 10 个类型（7 个 data class + 2 个 enum + 1 个 sealed）；`FileImageDisplayItem`、`FileListDisplayItem`、`FileDetailDisplayItem` 共享 `id/name/meta/sizeLabel/bucketName/path` 6 个字段，可以合成一个 `FileDisplayItem` + 可空 `preview/iconKind`。代价是 UI 布局分支要改，建议单独一轮。
- `IndexedFileItem` 只加了一个 `id: Int`，价值低。
- `BatteryCurrentSample` / `BatteryTemperatureSample` 形状相同，见 2.7。
- `JunkCleanUiState.formattedFoundSize` 是派生字符串，可以移到 UI。
- `NetworkScanUiState.hasWifi` 与 `scan?.hasWifi` 重复，可用后者计算。

---

## 8. 建议顺序

1. **删死类型/死字段**（1.1、1.2、1.6、1.7、1.13、1.14、1.15）：不改行为，只删代码。
2. **派生字段改计算属性**（1.3、1.4、1.5、1.8、1.9）：API 读法不变，构造点减少。
3. **并同构类型**（1.10、1.11、2.1、2.2）：需要改调用方，但类型数明显下降。
4. **清理结果三层并两层**（§4）：和授权路径收敛一起做，避免又留一个死分支。
5. **UI 投影类型**（§7）：最后做，风险最高、收益最小。

## 9. 不要做的事

- 不要为了减少类型把 `JunkFile` 和 `ManagedFileItem` 合并。
- 不要删 repository 接口去直接依赖 impl。
- 不要把 `ScanProgress` 塞进 `ScanResult`。
- 不要用 `Map<String, Any>` / `Pair` 代替有名字的模型；那是另一种“抽象税”。
- 不要在没有测试的删除逻辑上先动 `JunkDeleteOutcome` 的语义（先补规则测试，再做 1.3/1.5）。

---

## 10. 排查依据

- 类型/字段读取点：对三个 kit + app 做全量 grep，记录构造点与读取点
- 公开 API 返回路径：`CleanRepository`、`FileRepository`、`DeviceInfoRepository`、`NetworkRepository` 的返回类型
- 现有构建/测试：上一轮 `BUILD SUCCESSFUL`，cleaner 21 / device 10 / network 1 / app 86 条测试通过；本文只做审计，没有改代码
