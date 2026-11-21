# domain / data 分层对齐方案

> 状态：**已执行完成**（2026-11-21，阶段 0–7）。
> 参考实现：`D:\pros\my\PDFScanRead`（domain 46 个文件 / data 13 个 / `app/view → data` 0 处）。
> 目标：把本项目从「data 同时承担模型 + 接口 + 规则 + 实现」对齐到「domain 定义业务规则与出站契约，data 只做实现」。
>
> 结果：`domain` 4 → **34** 个文件，`data` 39 → **20** 个，`app/view|root → data` 56 → **0**，`data` 内 public interface 6 → **0**。
> 业务优先目录（`domain/<feature>/{model,port,rule,usecase,error}`）与 `data/<feature>`、`app/view/<feature>` 对齐。
> `dataImplementationIsolation`、`dataVisibility`、`domainKitIsolation` 已进入 `scripts/check-rules.ps1` 常驻门禁，baseline 已删除。

## 1. 目标与非目标

目标结构：

```text
domain/  纯 Kotlin：业务模型、错误、规则、出站端口、跨仓库 usecase
data/    平台与存储机制：*Impl、*DataSource、介质适配（实现 internal）
app/     组合根与页面：view 只依赖 base + domain；DI 是唯一装配 data 的地方
```

**非目标**（本轮不做）：

- 不拆 Gradle 模块，不新建 `feature:*` / `core:*`。
- 不把 `cleaner-kit` / `device-kit` / `network-kit` 的模型搬进 app。
- 不改 UI 结构、导航、广告、权限链路。
- 不为"看起来完整"新增无调用者的 usecase、shared 包或 DTO。

## 2. 现状盘点

| 指标 | 当前 | 目标 |
|---|---|---|
| `domain/` 文件 | 4 | ≥ 26 |
| `data/` 文件 | 39 | ≤ 18 |
| `app/view` → `data` import | **56**（33 个文件） | 0 |
| `app/root` → `data` import | 0 | 0 |
| `data/` 里的 public interface | 6 | 0 |
| `data/` 里的 model | 12 | 0 |
| `data/` 里的业务规则 | 2 | 0 |

56 处违规按 feature 分布：

| feature | import 数 | 涉及文件 | 主要符号 |
|---|---|---|---|
| security | 19 | 13 | `VirusScanMode`(11)、`VirusThreatModel`(4)、`VirusScanner`、`VirusSecurityRepository` |
| applock | 9 | 6 | `AppLockAppModel`、`AppLockRepository`、`AppLockPinRule` |
| battery | 8 | 3 | `BatteryHistoryRepository`、`BatteryHistorySampler`、2 个 model |
| notificationcleaner | 7 | 4 | 4 个 model、`NotificationCleanerRepository` |
| whatsapp | 8 | 3 | `WhatsAppCleanerCategory/Group`、`WhatsAppFileBucket`、`loadWhatsAppFileGroups` |
| appusage | 4 | 3 | `AppUsageRepository`、`AppUsageInfoModel` |
| similarphotos | 1 | 1 | `groupSimilarPhotos` |
| 合计 | 56 | 33（文件可跨 feature 复用） | — |

已核实的前提：

- 12 个 model（applock / appusage / battery / notificationcleaner / security）**全部零 import**，是纯 Kotlin，可以直接迁入 domain。
- `data/**/internal/*` 的实现类已经是 `internal`，`dataVisibility` 现状即满足，加门禁是为了防止回退。
- 测试侧另有 19 处涉及 data 类型（含 2 个测试包声明），随批次一起改（见 §6）。

## 3. 目标目录

```text
domain/
├─ applock/
│  ├─ model/AppLockAppModel.kt
│  ├─ port/AppLockRepository.kt
│  └─ rule/{AppLockPinRule,AppLockMonitoringRule}.kt
├─ appusage/
│  ├─ model/AppUsageInfoModel.kt
│  └─ port/AppUsageRepository.kt
├─ battery/
│  ├─ model/{BatteryHistoryOwner,BatteryHistorySampleModel}.kt
│  ├─ port/{BatteryHistoryRepository,BatteryHistoryRecorder}.kt
│  └─ usecase/SyncBackgroundBatterySamplingUseCase.kt
├─ notificationcleaner/
│  ├─ model/{BlockableNotificationAppModel,BlockedNotificationModel,
│  │          NotificationCleanerSnapshotModel,PostedNotificationModel}.kt
│  └─ port/NotificationCleanerRepository.kt
├─ security/
│  ├─ error/ScanEngineConfigurationException.kt
│  ├─ model/{VirusScanErrorKind,VirusScanItemModel,VirusScanMode,VirusThreatModel}.kt
│  └─ port/{VirusScanner,VirusSecurityRepository}.kt
├─ junkclean/
│  ├─ error/JunkError.kt
│  └─ model/CleanupSummaryModel.kt
└─ settings/
   └─ port/SettingsRepository.kt
```

**结构约定：业务优先。** `domain/<feature>` 与 `data/<feature>`、`app/view/<feature>` 同名，一个业务能力在四层里都叫同一个名字（applock 在 `domain/applock`、`data/applock`、`app/view/applock`）。不在 domain 顶层按 `model/port/rule` 铺平——那样不同业务的规则会混在同一目录。跨 feature 的契约放在其归属 feature 下（`SettingsRepository` 归 `settings`），不新建 `shared` 包。

`data/` 只保留：`*RepositoryImpl`、`*DataSource`、`*FileStore`、`BatteryHistorySampler`（实现 `BatteryHistoryRecorder`）、`SettingsRepositoryImpl`、`WhatsApp`/`SimilarPhoto` 的介质适配。

依赖方向（写进规范，由门禁逐条检查）：

- `domain` 只依赖自身 + Kotlin/JDK；不得 import `base`、`data`、`app`、三个 kit。
- `data` 允许 `domain`、`base`、kit；不得 import `app`。
- `app/view` 允许 `base`、`domain`；不得 import `data`。
- `app/di` 是唯一装配 `data` 实现与 `base/toolkit` 端口实现的包。

## 4. 门禁（先装，给迁移上锁）

在 `scripts/check-rules.ps1` 增加 4 个检查，并在 `profiles/android-compose-mvi.json` 打开：

| 检查名 | 内容 |
|---|---|
| `dataImplementationIsolation` | `app/view`、`app/root` 不得 `import ....data.`；迁移期用 baseline 过滤 |
| `dataContracts` | `data/` 下不得出现非 `internal` 的 `interface`；需要的数据层抽象必须 `internal` |
| `dataVisibility` | `data/repository` 下的实现类必须是 `internal` |
| `domainKitIsolation` | `domain/` 不得 import `cleanerkit` / `devicekit` / `networkkit` |

`domainPurity`、`layerDirection` 保持，迁移完成后它们才有实际覆盖面（4 → 26 个文件）。

### 4.1 baseline 机制（迁移期使用，已完成使命）

迁移完成后 `profiles/data-import-baseline.txt` 与 `-UpdateBaseline` 参数已删除，`dataImplementationIsolation` 现在是普通零容忍检查。下面保留迁移期的做法作为记录：

直接打开 `dataImplementationIsolation` 会让 CI 立刻变红（56 处存量）。迁移期采用只减不增的 baseline：

- 新增 `profiles/data-import-baseline.txt`，每行一条 `相对路径|完整 import 符号`。
- 脚本新增 `-UpdateBaseline` 参数：迁移一批后重新生成 baseline；普通运行只校验"不新增"。
- 每批结束检查 baseline 行数必须下降；收尾删除该文件并移除 `-UpdateBaseline` 分支。
- baseline 里已不存在的条目视为 stale，提示删除（防止台账腐烂）。

对应代码骨架（与现有 `Run-FilePatternCheck` 风格一致）：

```powershell
if (Test-CheckEnabled 'dataImplementationIsolation') {
    $baselinePath = Join-Path $root 'profiles/data-import-baseline.txt'
    $baseline = @(Get-Content -LiteralPath $baselinePath -ErrorAction SilentlyContinue |
        Where-Object { $_ -and -not $_.TrimStart().StartsWith('#') } |
        ForEach-Object { $_.Trim() })
    $hits = @(Get-KotlinFiles $viewDir) + @(Get-KotlinFiles (Join-Path $src "$pkgPath/app/root")) |
        Select-String -Pattern ('^import ' + [regex]::Escape($data.packageRoot) + '\.data\.')
    $new = @()
    foreach ($hit in $hits) {
        $key = '{0}|{1}' -f (RelativePath $hit.Path), ($hit.Line.Trim() -replace '^import ', '')
        if ($baseline -notcontains $key) { $new += $key }
    }
    if ($script:UpdateBaseline) { Set-Content -LiteralPath $baselinePath -Value ($hits | ForEach-Object { ... }) }
    if ($new.Count -eq 0) { Add-Pass 'dataImplementationIsolation' }
    else { $new | Select-Object -First 20 | ForEach-Object { Add-Failure 'dataImplementationIsolation' $_ } }
}
```

## 5. 迁移批次

每批都是独立 PR：只移动类型 + 改 import + 改 DI 绑定，不顺手改行为；`git mv` 保留历史。

| 批次 | 范围 | 迁入 domain | 影响 import | 预估 |
|---|---|---|---|---|
| B0 | 门禁 | 无 | 0 | 0.5 人日 |
| B1 | applock | `AppLockAppModel`、`AppLockRepository`、`AppLockPinRule`、`AppLockMonitoringRule` | 9 / 6 文件 | 0.5 |
| B2 | battery | `BatteryHistoryOwner`、`BatteryHistorySampleModel`、`BatteryHistoryRepository`、新增 `BatteryHistoryRecorder`、`SyncBackgroundBatterySamplingUseCase` 归入 `domain/battery/usecase`；`BatteryHistorySampler` 改实现端口并 `internal` | 8 / 3 | 0.5 |
| B3 | appusage | `AppUsageInfoModel`、`AppUsageRepository` | 4 / 3 | 0.25 |
| B4 | notificationcleaner | 4 个 model、`NotificationCleanerRepository` | 7 / 4 | 0.5 |
| B5 | security | 4 个 model、`VirusScanner`、`VirusSecurityRepository`、`ScanEngineConfigurationException` 归入 `domain/security/error` | 19 / 13 | 0.75 |
| B6 | whatsapp + similarphotos | 3 个 model 拆成单类型文件、新增 `WhatsAppCleanerRepository`、`SimilarPhotoRepository`；`loadWhatsAppFileGroups`/`groupSimilarPhotos` 改为 data 内部适配 | 9 / 4 | 1.0 |
| B7 | 收尾 | `SettingsRepository` 归入 `domain/settings/port`、`JunkError`/`CleanupSummaryModel` 归入 `domain/junkclean/{error,model}`；删 baseline、全局门禁、文档与模板同步 | 0 | 0.5 |

合计约 4.5 人日，7 个 PR。

### 5.1 两个需要单独评审的改动

1. **`AppLockMonitoringRule.shouldMonitorApps(repository)`** 现在把仓库当参数，属于"规则依赖数据接口"。迁入 domain 后改成纯参数：

   ```kotlin
   fun shouldMonitor(
       pinSet: Boolean,
       monitoringEnabled: Boolean,
       lockedCount: Int,
       hasOverlay: Boolean,
       hasUsageAccess: Boolean,
   ): Boolean
   ```

   这是**行为等价重构**，单独一个 PR，配 `AppLockMonitoringRuleTest`（现在测试在 `data/applock` 包，随迁）。

2. **`BatteryHistorySampler`** 是有状态协程调度器，留在 data；新增 `BatteryHistoryRecorder` 端口暴露 `start / stop / sampleOnce`，`AppRuntimeService` 可以继续直接用实现类（不在 view 门禁范围），`BatteryInfoViewModel` / `DeviceInfoViewModel` / `DeviceInfoModels` 改依赖端口。

## 6. 每批 Definition of Done

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts\check-rules.ps1
.\gradlew.bat :app:compileDebugKotlin
.\gradlew.bat :app:testDebugUnitTest --tests "<本批相关测试>"
```

- [ ] `check-rules` 全绿，baseline 行数比上一批少
- [ ] 编译通过，`data` 不再被 `app/view` 引用
- [ ] 测试 import 同步更新（共 19 处，主要在 `VirusScanViewModelTest`、`NotificationCleanerViewModelTest`、`AppLockViewModelTest`、`HomeViewModelTest`、`AppUsageViewModelEffectTest`、`BatteryHistoryRepositoryTest`）
- [ ] `DataModule.kt` 的 `single<...>` 绑定指向新包，实现类仍 `internal`
- [ ] diff 中除包名、import、可见性外无逻辑改动（规则重构 PR 除外，且附测试）

## 7. 验收指标

| 指标 | 通过条件 |
|---|---|
| `app/view|app/root` → `data` | 0 处，`profiles/data-import-baseline.txt` 删除 |
| `data` 下 public interface | 0 个 |
| `data/repository` 实现 | 全部 `internal` |
| `domain` 文件数 | ≥ 26 |
| `data` 文件数 | ≤ 18 |
| 构建 | `:app:assembleDebug` 通过 |
| 测试 | `:app:testDebugUnitTest` 通过 |
| 行为 | 除 §5.1 两处等价重构外，无行为变化 |

## 8. 风险与回滚

| 风险 | 应对 |
|---|---|
| 移动后 Koin 绑定类型漂移 | 每批编译即暴露；`DataModule` 单独检查 |
| `internal` 实现被公开签名暴露 | 编译器报错；端口在 domain 公开、实现在 data internal |
| baseline 与真实违规不同步 | 每批 `-UpdateBaseline` 并人工确认只减不增 |
| 测试包名与目录已不一致（如 `domain/toolbox/...` 旧路径） | 单独一个整理 PR，不混入迁移 |
| 规则重构夹带行为变化 | 拆 PR + 单测覆盖 |
| 迁移中途插需求 | 每批可独立合并；B0 的 baseline 保证不新增违规 |

回滚：每批是纯类型搬迁，`git revert` 单个 PR 即可；B0 的门禁可单独回滚（删除 baseline 与 checks 开关）。

## 9. 文档同步（B7）

- `docs/mvi-architecture.md` 第 4 行仍写"4 个 core + 10 个 feature 模块"，与实际单 `:app` 不符；改为当前包结构。
- 补"包职责与依赖方向"的允许列表（对齐 PDFScanRead `AGENTS.md` 第 36–43 行的写法）。
- `templates/feature/README.md` 与 `templates/feature/domain/FeatureRepository.kt.template` 已指向 `domain/port/<feature>/`，迁移后自动变成正确示范，只需补一句"实现放 data 且 internal"。
- 可选：新建 `docs/rules/exceptions.md`，登记"kit 模型经 data 适配"的例外（如果 B6 采用端口方案就不需要）。

## 10. 不做什么

- 不为了填满 domain 搬 kit 模型或造 DTO。
- 不新增 `feature/*`、`core/*` 模块。
- 不同时改 ViewModel 行为、Reducer、UI。
- 不把 `JunkError` / `CleanupSummaryModel` 移出 domain：它们在 domain 与 PDFScanRead 的 `Feedback*` 同构（单 feature 的业务词汇也归 domain），B7 只把它们归到 `domain/junkclean/` 子包。

## 11. 待确认

1. **B6 是否纳入**：纳入则 `view → data` 归零，多约 1 人日；不纳入则 whatsapp/similarphotos 保留 9 处 import，baseline 变成长期例外台账。
2. **规则签名重构**（§5.1）是否随 B1 做，还是单独排期。
3. **门禁强度**：`dataContracts` 是否允许 `internal interface`（推荐允许），否则 `data/local` 内部抽象会很难写。
