# 权限申请方案：系统运行时授权优先，解释弹窗兜底

- 仓库：`ai_-cleaner_-storage_-clean`
- 当前分支：`codex/mar2sdk-integration`
- 基线提交：`ab02e35`
- 目标版本：1.0.7
- 涉及范围：`base/permission`、`app/view/common/permission`、病毒页、AppLock / 通知清理 / 文件管理的权限入口
- 非目标：广告 SDK、通知权限埋点口径、Trustlook Key、`QUERY_ALL_PACKAGES` 上架合规结论

## 0. 结论

采用“**系统运行时授权框优先，解释弹窗兜底**”策略：

1. 有运行时权限，且系统授权框可以拉起 → **直接拉系统框**，不展示应用内解释。
2. 没有运行时权限（只能去设置）→ **先解释，再跳设置**。
3. 有运行时权限但系统框拉不起 / 已被永久拒绝 → **解释，再跳设置**。
4. 系统框已经展示、用户只是拒绝但还能再次申请 → 直接返回 `Denied`，不弹解释；页面自行决定重试或提示。
5. 解释弹窗只承担“去设置”的说明，不再使用“允许”这类会让人以为还能弹系统框的按钮。

当前 `PermissionPromptController` 已经接近这个行为；本方案的重点是把行为显式化、补齐“系统框不可用”分支、统一文案与视觉，并清掉病毒页的“假权限”。

## 1. 行为定义

### 1.1 三条入口

| 入口 | 判定 | 行为 |
|---|---|---|
| 运行时入口 | `permission.runtimePermissions(context).isNotEmpty()` | 直接 `RequestMultiplePermissions`，不弹应用内卡 |
| 设置入口 | `runtimePermissions` 为空，但 `settingsIntents` 非空 | 先弹解释卡，确认后跳设置 |
| 不可用 | 既没有运行时权限，也没有可用的设置页 | 弹解释卡；确认后返回 `RequiresSettings`，由页面提示 |

“系统框拉不起”不单独做预判，统一用**请求结果 + 复检**判断：

```text
request(runtime)
    ├─ granted              → Granted
    ├─ denied + 还能申请     → Denied（不弹解释）
    ├─ denied + 不能再申请   → 解释 → 设置（SystemUnavailable）
    └─ launcher 拉不起       → 同上，按 PermanentlyDenied 走（SystemUnavailable）
```

判定“还能不能再申请”使用 `Activity.shouldShowRequestPermissionRationale`。注意：

- 这是**请求之后**的判断，首次请求前该值天然为 false，不能拿来当“永久拒绝”；
- 部分 OEM 在首次拒绝后就返回 false，宁可多给一次“去设置”兜底，也不要让流程悬空；
- 需要更精确时可增加“是否请求过”的持久化标记（见 9.2），首版不强制。

### 1.2 决策矩阵

| 权限 | API 29 | API 30–32 | API 33+ | 系统框不可用时 |
|---|---|---|---|---|
| `StorageFiles` | `READ/WRITE_EXTERNAL_STORAGE` 运行时框 | 无运行时框 | 无运行时框 | 应用详情 / 所有文件访问设置 |
| `MediaLocation` | 运行时框 | 运行时框 | 运行时框 | 应用详情 |
| `Location` | 运行时框 | 运行时框 | 运行时框 | 应用详情 |
| `UsageAccess` | 无运行时框 | 无运行时框 | 无运行时框 | 使用情况访问设置 |
| `NotificationListener` | 无运行时框 | 无运行时框 | 无运行时框 | 通知使用权设置 |
| `Overlay` | 无运行时框 | 无运行时框 | 无运行时框 | 悬浮窗设置 |
| `PostNotifications` | 通知总开关 | 通知总开关 | `POST_NOTIFICATIONS` 运行时框 | 应用通知设置 |

`PostNotifications` 目前由 `NotificationPermissionPrompt` 独立处理，已经是“系统框优先、引导兜底”，本方案不重写它，只要求文案和埋点口径与通用权限一致。

### 1.3 结果语义

| 结果 | 何时产生 | 页面处理 |
|---|---|---|
| `Granted` | 权限已授予（运行时或设置返回后复检通过） | 继续业务 |
| `Denied` | 系统框已展示但未授予、且还能再次申请；或用户取消解释卡 | 中止本次操作，可提示；下次点击再走系统框 |
| `RequiresSettings` | 跳设置后返回仍未授予；或设置页/系统框完全拉不起 | 中止本次操作，提示去设置；**不要自动重弹解释卡**，避免循环 |
| `PermanentlyDenied` | 仅作为内部中间态，不再向外返回 | 页面无需再处理；保留枚举只为兼容现有 `when` |

## 2. 状态机

### 2.1 状态

```kotlin
internal data class PermissionExplanation(
    val permission: AppPermission,
    val reason: Reason,
) {
    /** 解释卡出现的原因，决定文案和埋点。 */
    enum class Reason {
        /** 该权限没有运行时授权框，只能去设置页。 */
        SettingsOnly,

        /** 系统授权框已不可用（永久拒绝或拉不起）。 */
        SystemUnavailable,
    }
}
```

`PermissionPromptController` 持有：

| 字段 | 含义 |
|---|---|
| `explanation: PermissionExplanation?` | 正在展示解释卡；null 表示没有 |
| `requesting: AppPermission?` | 已交给系统界面，等待结果 |
| `requestingSettingsOnly: Boolean` | 本次只跳设置，避免再次请求运行时 |
| `callback` / `activePermission` | 内部回调与埋点上下文 |

### 2.2 迁移表

| 当前状态 | 事件 | 下一状态 | 对外动作 |
|---|---|---|---|
| Idle | `request(p)` 已授予 | Idle | `onResult(Granted)` |
| Idle | `request(p)` 有运行时权限 | Requesting | 拉起系统框 |
| Idle | `request(p)` 无运行时权限 | Explaining(SettingsOnly) | 展示解释卡 |
| Requesting | 系统返回 Granted | Idle | `onResult(Granted)` |
| Requesting | 系统返回 Denied 且还能申请 | Idle | `onResult(Denied)` |
| Requesting | 系统返回 Denied 且不能申请 | Explaining(SystemUnavailable) | 展示解释卡 |
| Requesting | launcher 不可用 | Explaining(SystemUnavailable) | 展示解释卡 |
| Explaining | `confirmExplanation` | Requesting(settingsOnly) | 跳设置 |
| Explaining | `dismissExplanation` | Idle | `onResult(Denied)` |
| Requesting(settingsOnly) | 设置返回且已授予 | Idle | `onResult(Granted)` |
| Requesting(settingsOnly) | 设置返回仍未授予 | Idle | `onResult(RequiresSettings)` |

### 2.3 关键规则

1. **同一时刻只处理一个请求**：`explanation != null || requesting != null` 时忽略新请求。
2. **不在设置返回后自动重弹解释卡**：`RequiresSettings` 直接结束；页面想重试必须由用户再次触发。
3. **解释卡永远是“去设置”语义**：`confirmExplanation` 一律 `requestingSettingsOnly = true`。
4. **系统框优先**：`request()` 对有运行时权限的权限直接置 `requesting`，不经过 `explanation`。
5. **永久拒绝只在内部出现**：`completeRequest(PermanentlyDenied)` 转为 `explanation(SystemUnavailable)`；页面不会收到 `PermanentlyDenied`。
6. **埋点只记录用户可见事实**：解释卡展示/选择、系统框结果；不要为“内部状态切换”打点。

## 3. 代码改造

### 3.1 文件清单

| 文件 | 改动 | 目的 |
|---|---|---|
| `app/view/common/permission/PermissionExplanation.kt` | 新增 | 解释卡原因模型 |
| `app/view/common/permission/PermissionPromptController.kt` | 改 | `explaining: AppPermission?` → `explanation: PermissionExplanation?`；确认后强制设置页 |
| `app/view/common/permission/PermissionPrompt.kt` | 改 | 按 `explanation.reason` 选择文案；自定义弹窗回调改传 `PermissionExplanation` |
| `app/view/common/permission/PermissionPromptCopy.kt` | 改 | 通用文案的确认按钮改为“去设置”；`SystemUnavailable` 追加提示要点 |
| `base/designsystem/component/popups/PermissionDialogModels.kt` | 改 | 增加 `actionLayout`、`showSparkle`（视觉方案用） |
| `base/designsystem/component/popups/PermissionDialog.kt` | 改 | 支持左右/上下两种按钮布局，支持标题是否带 ✨ |
| `app/view/common/permission/ui/AppLockOverlayPermissionDialog.kt` | 改 | 对齐 feature：无 ✨、按钮上下堆叠 |
| `app/view/common/permission/ui/AppLockUsagePermissionDialog.kt` | 改 | 对齐 feature：无 ✨、说明居中 |
| `app/view/notificationcleaner/ui/NotificationCleanerRoute.kt` | 改 | 自定义弹窗回调改为 `PermissionExplanation` |
| `app/view/applock/ui/AppLockRoute.kt` | 改 | 同上 |
| `app/view/permissions/ui/ManagePermissionsRoute.kt` | 改（建议） | 运行时权限也走 `rememberPermissionPrompt`，不要直接 `openSettings` |
| 病毒页相关文件 | 改 | 删除假“已安装应用列表权限”门禁，见第 4 节 |
| `app/src/main/res/values*/strings.xml` | 改 | 新增“去设置”“系统不再弹窗”文案，10 个语言包对齐 |
| 对应测试 | 改/新增 | 见第 7 节 |

### 3.2 控制器草图

```kotlin
@Stable
internal class PermissionPromptController(
    private val gate: PermissionGate,
    private val requiresSettings: (AppPermission) -> Boolean = { false },
    private val onEvent: (PermissionPromptEvent) -> Unit = {},
) {
    var explanation by mutableStateOf<PermissionExplanation?>(null)
        private set

    var requesting by mutableStateOf<AppPermission?>(null)
        private set

    var requestingSettingsOnly by mutableStateOf(false)
        private set

    fun request(
        permission: AppPermission,
        onResult: (PermissionResult) -> Unit,
    ) {
        if (explanation != null || requesting != null) return
        if (gate.isGranted(permission)) {
            onResult(PermissionResult.Granted)
            return
        }
        activePermission = permission
        callback = onResult
        if (requiresSettings(permission)) {
            explanation = PermissionExplanation(permission, PermissionExplanation.Reason.SettingsOnly)
        } else {
            requestingSettingsOnly = false
            requesting = permission
        }
    }

    fun confirmExplanation() {
        val current = explanation ?: return
        trackDialogResult(current, accepted = true)
        explanation = null
        requestingSettingsOnly = true
        requesting = current.permission
    }

    fun dismissExplanation() {
        val current = explanation ?: return
        trackDialogResult(current, accepted = false)
        finish(PermissionResult.Denied)
    }

    fun completeRequest(result: PermissionResult) {
        val permission = requesting
        val settingsOnly = requestingSettingsOnly
        requesting = null
        if (result == PermissionResult.PermanentlyDenied && !settingsOnly && permission != null) {
            explanation = PermissionExplanation(permission, PermissionExplanation.Reason.SystemUnavailable)
            return
        }
        finish(result)
    }
}
```

`finish` 里清理 `explanation / requesting / requestingSettingsOnly / activePermission`，并把最终结果回调给页面。

### 3.3 文案

新增资源键（所有 `values-*` 同步）：

| key | en | zh |
|---|---|---|
| `permission_open_settings` | Go to Settings | 去设置 |
| `permission_hint_system_denied` | The system won't show the permission dialog again. Please enable it in Settings. | 系统不会再弹出授权弹窗，请到设置中开启。 |

规则：

- `SettingsOnly`：解释卡沿用权限描述，确认按钮用 `permission_open_settings`。
- `SystemUnavailable`：在解释卡上追加一条 `permission_hint_system_denied` 要点，确认按钮同上。
- 通用 `dialogCopy()`、`junkCleanCopy()`、`virusDeepScanCopy()`、`whatsAppCopy()`、`networkUsageCopy()` 的 `confirmText` 从 `allow` 改为 `permission_open_settings`。
- AppLock 自定义卡保留自己的“管理权限 / 提交”，因为它们本身就是设置页语义。
- `NotificationPermissionGuideDialog` 的“OK / Cancel”不动。

### 3.4 页面接线

所有页面保持“只消费结果”：

| 页面 | 权限 | `Granted` | `Denied` / `RequiresSettings` |
|---|---|---|---|
| 垃圾清理 | `StorageFiles` | 开始扫描 | `PermissionResultReceived(false)` |
| WhatsApp 清理 | `StorageFiles` | 开始扫描 | `PermissionResultReceived(false)` |
| 文件管理 | `StorageFiles` | 继续操作 | 返回权限拒绝态 |
| 隐私图 | `StorageFiles` → `MediaLocation` | 依次补齐 | `PermissionRejected` |
| 网络扫描 | `Location` | 开始扫描 | 停留，提示权限不足 |
| 应用用量 / 网络用量 | `UsageAccess` | 加载数据 | 停留，提示权限不足 |
| AppLock | `UsageAccess` / `Overlay` | 刷新状态 | 停留，刷新状态 |
| 通知清理 | `NotificationListener` | 开启拦截 | 停留 |
| 病毒深扫 | `StorageFiles` | 进入扫描 | 重置 pending，可重试 |
| 管理权限页 | 各权限 | 刷新状态 | 刷新状态 |

页面不要在 `Denied` 时自己再弹一次解释卡；解释卡只由控制器在 `SystemUnavailable` / `SettingsOnly` 时展示。

## 4. 病毒功能专项

### 4.1 现状问题

1. 数据使用提示（“杀毒如何使用你的数据”）在进入页面时自动弹出；用户点“同意并继续”时通常没有 `pendingScanMode`，所以只是关掉，体感像“点了没反应”。
2. “已安装应用列表权限”不是 Android 运行时权限；`hasInstalledAppsAccess()` 只看能否看到任意其他包，而 Manifest 只有 MAIN/LAUNCHER `<queries>`，Android 11+ 上几乎恒为 true，弹窗几乎不会出现；即使出现，“管理权限”打开的是应用详情页，也没有对应开关。
3. 深扫在 API 29 上走系统存储授权框（符合新策略）；API 30+ 走“所有文件访问”设置页（符合新策略）。这两条都不需要改，只需要把前面的数据提示和假权限处理干净。

### 4.2 目标流程

```text
进入病毒页
    └─ 点击「深度扫描 / 快速扫描」
        ├─ 未接受数据提示 → 数据提示弹窗
        │     ├─ 同意并继续 → 记录标记 → 继续本次扫描
        │     └─ 暂不 → 退出病毒页
        └─ 已接受数据提示 → 继续本次扫描
              ├─ 快速扫描 → 直接进入扫描页
              └─ 深度扫描 → 权限流程
                    ├─ API 29：系统存储授权框（优先）
                    └─ API 30+：解释卡 → 所有文件访问设置
```

### 4.3 删除清单

| 文件 / 成员 | 处理 |
|---|---|
| `app/view/antivirus/ui/part/VirusInstalledAppsAccessDialog.kt` | 删除 |
| `AntiVirusScreen` 中 `showInstalledAppsPermissionDialog` 分支 | 删除 |
| `VirusHomeUiState.showInstalledAppsPermissionDialog` / `waitingForSettingsReturn` | 删除 |
| `VirusScanAction.InstalledAppsSettingsRequested` / `InstalledAppsDialogDismissed` / `SettingsLaunchFailed` | 删除 |
| `VirusScanEvent.OpenInstalledAppsSettings` | 删除 |
| `VirusScanViewModel.checkInstalledAppsAccess` 中的访问检查、`hasInstalledAppsAccessFailedBefore`、`setInstalledAppsAccessFailed` | 删除 |
| `VirusEffectHandler` 中 `OpenInstalledAppsSettings` 分支及相关 import | 删除 |
| `VirusScanReducer` 中上述分支 | 删除 |
| `DefaultVirusSecurityRepository.hasInstalledAppsAccess` / 两个偏好键 | 删除 |
| `VirusSecurityRepository.hasInstalledAppsAccess` / `hasInstalledAppsAccessFailedBefore` / `setInstalledAppsAccessFailed` | 删除 |
| `VirusScanViewModelTest.FakeSecurityRepository` 对应实现 | 删除 |

### 4.4 新增 / 保留

- 数据提示只在 `requestHomeScan` 时展示；`Entered` 不再自动展示。
- `ScanNoticeAccepted`：`setScanNoticeAccepted()` 后，若 `pendingScanMode != null` 继续扫描流程；否则只关闭。
- `ScanNoticeDeclined`：保持退出病毒页。
- 深扫权限统一走 `rememberPermissionPrompt(copy = virusDeepScanCopy())`，由第 2 节状态机决定系统框还是解释卡。
- 如果产品确认必须扫描全部已安装应用，再单独立项评估 `QUERY_ALL_PACKAGES`（安装期权限，受 Play 政策限制），不要用“应用详情页”假装能授权。

## 5. 弹窗视觉方案

### 5.1 目标规范

权限卡统一到 `PermissionDialog`，以 feature 分支为基线，并修正小屏溢出：

| 项 | 目标值 |
|---|---|
| 卡片 | 圆角 12dp；`fillMaxWidth + padding(horizontal = 16dp) + widthIn(max = 360dp)`；`offset(y = -10dp)` |
| 渐变 | `#DFFFDC` → 白（26.381%）→ 白 |
| 标题 | 24sp / 28sp，Medium，letterSpacing 0；通用权限带 `✨`，AppLock 覆盖层不带 |
| 说明 | 18sp / 21sp，letterSpacing 0.54sp；通用左对齐，AppLock 使用情况居中 |
| 要点 | 24dp 图标 + 8dp 间距 + 18sp/24sp 文案；每条间距 12dp |
| 按钮（左右） | 两个等宽按钮，间距 5dp，高 44dp，圆角 26dp；内容区最大 316dp 居中 |
| 按钮（上下） | 全宽，高 46dp，间距 12dp；渐变主按钮 + 1.56dp 描边次按钮 |
| 颜色 | 主色 `#5DD595`，渐变起点 `#D0FD91`，正文 `#282927`，次按钮 `#999999` |

左右按钮用 `weight(1f)` 而不是固定 153dp，保留 feature 的视觉效果，同时避免 360dp 屏溢出；宽屏下用 `widthIn(max = 316dp)` 居中。

### 5.2 与 feature 的差异修正

| 差异 | 当前 | 目标 |
|---|---|---|
| 卡片上移 | 无 | `offset(y = -10dp)` |
| 标题字距 | 0.72sp | 0 |
| 卡片宽度 | max 343dp | max 360dp（小屏仍为屏宽 - 32dp） |
| 按钮宽度 | `weight(1f)` 拉伸满卡 | 等宽但内容区最大 316dp 居中 |
| AppLock 覆盖层 | 带 ✨、左右按钮 | 不带 ✨、上下按钮 |
| AppLock 使用情况 | 左对齐、带 ✨ | 居中、不带 ✨ |

### 5.3 组件拆分

- `PermissionDialog`：所有“权限解释 → 去设置”的唯一入口。
- `VirusScanNoticeDialog`：保留，作为“数据使用告知”，不并入权限卡；视觉沿用绿色渐变 + 英雄图 + 上下按钮，与 feature 一致。
- `VirusInstalledAppsAccessDialog`：删除。
- `AppLockUsagePermissionDialog` / `AppLockOverlayPermissionDialog`：继续包 `PermissionDialog`，通过 `showSparkle` / `actionLayout` 参数表达差异，不再各自复制一套颜色和尺寸。
- 新增 `PermissionDialogPreviews.kt`，覆盖左右/上下、有无英雄图、长文案三种状态。

## 6. 埋点

当前 `PermissionPromptEvent` 只对 `StorageFiles` 记录。建议扩展为通用事实：

| 事件 | 字段 | 触发点 |
|---|---|---|
| `DialogShown` | `permission`, `reason` | 解释卡展示 |
| `DialogResult` | `permission`, `reason`, `accepted` | 解释卡确认/取消 |
| `SystemResult` | `permission`, `accepted` | 系统框返回 |
| `SettingsResult` | `permission`, `accepted` | 设置返回复检 |

`Mar2Analytics` 增加通用方法；旧 `trackFileManagerPopup` / `trackFilePermissionResult` 可保留，内部转调新方法，避免一次性改太多调用方。若本期不做埋点，至少保证 `StorageFiles` 现有口径不变。

## 7. 测试计划

### 7.1 控制器（纯 JVM）

文件：`app/src/test/.../PermissionPromptControllerTest.kt`

| 用例 | 断言 |
|---|---|
| 已授予 | 直接 `Granted`，无解释卡 |
| 运行时权限首次请求 | `requesting` 有值，`explanation == null` |
| 运行时权限永久拒绝 | `explanation.reason == SystemUnavailable` |
| 系统框不可用 | 同上 |
| 设置类权限 | `explanation.reason == SettingsOnly`，确认后 `requestingSettingsOnly == true` |
| 取消解释卡 | `Denied`，不跳设置 |
| 设置返回仍未授予 | `RequiresSettings`，不自动重弹 |
| 请求进行中再次请求 | 第二次被忽略 |
| 存储权限埋点 | 解释/结果事件带正确 `permission` / `reason` |

### 7.2 病毒页（纯 JVM）

文件：`app/src/test/.../VirusScanViewModelTest.kt`

| 用例 | 断言 |
|---|---|
| 进入页面不自动弹数据提示 | `showScanNotice == false` |
| 点击深扫且未接受提示 | `showScanNotice == true`，无权限事件 |
| 同意提示后继续 | `RequestDeepScanPermission` 只发一次 |
| 深扫权限拒绝 | `pendingScanMode` 清空，可重试 |
| 已安装应用相关 Action | 不再存在（编译期保证） |

### 7.3 UI / 回归

- 手动验证：API 29 模拟器、API 33+ 模拟器各一台。
- `PermissionDialog` 在 360dp / 393dp 宽度下的按钮和卡片不溢出。
- 病毒页全流程：首次进入、同意、拒绝、再次进入、系统永久拒绝、设置返回未授权。
- 跑 `.\scripts\check-rules.ps1`；合入前 `.\scripts\ci.ps1`。

## 8. 分阶段实施

| 阶段 | 内容 | 可独立合入 |
|---|---|---|
| PR1 | `PermissionExplanation` + 控制器状态机 + 文案“去设置” + 单测（行为与现状基本一致） | 是 |
| PR2 | `PermissionDialog` 视觉参数化 + AppLock 对齐 + 预览 | 是 |
| PR3 | 病毒页删除假权限、数据提示改为扫描时展示 + 单测 | 是 |
| PR4 | 埋点扩展、管理权限页接入系统优先、文档更新 | 是 |

建议按 PR1 → PR3 → PR2 → PR4 合入：先保证行为正确，再做视觉，最后补口径。

## 9. 风险与回滚

### 9.1 风险

| 风险 | 说明 | 应对 |
|---|---|---|
| OEM `shouldShowRequestPermissionRationale` 不准 | 首次拒绝后就返回 false | 解释卡兜底；必要时加“已请求过”标记 |
| 设置返回误判 | 返回时 `ON_RESUME` 可能多次触发 | `awaitingSettings` 只消费一次；返回后只复检，不重弹 |
| 多权限部分授予 | 例如只给了 READ 没给 WRITE | `isGranted` 必须全部满足；结果按 `Denied` 处理，下次只请求缺失项 |
| 删除 `hasInstalledAppsAccess` 影响扫描完整性 | 包可见性不足时 Trustlook 只能看到部分应用 | 单独立项评估 `QUERY_ALL_PACKAGES`；本期不假装能授权 |
| 语言包 key 不一致 | 新增 2 个 key | 所有 `values-*` 同步，跑规则脚本 |
| 视觉回归 | 卡片宽度/按钮布局变化 | Compose 预览 + 360dp/393dp 手动验证 |

### 9.2 可选增强

如果线上发现 OEM 误判严重，增加 `PermissionRequestHistory`（DataStore/SharedPreferences）：

- 记录每个权限“是否请求过”；
- `runtimePermissions` 非空且 `!granted && !shouldExplain && askedBefore` → `SystemUnavailable`；
- 未请求过则无论 rationale 如何都走系统框。

首版先用现有 `shouldShowRequestPermissionRationale`，保持改动面小。

## 10. 验收标准

1. API 29：深度扫描第一次请求 → **直接系统存储授权框**，前面没有应用内权限卡。
2. API 30+：深度扫描 → 解释卡（“去设置”）→ 系统“所有文件访问”页；不出现运行时系统框。
3. 永久拒绝后再次触发：系统框不再出现，直接解释卡；确认后进设置；返回未授权时页面有明确结束状态，不循环。
4. 病毒页不再出现“查看已安装应用列表权限”弹窗，也不再打开应用详情页假装授权。
5. 所有权限解释卡使用同一 `PermissionDialog`；AppLock 与病毒数据提示的差异由参数表达，不再各写一套颜色/尺寸。
6. `PermissionPromptControllerTest`、`VirusScanViewModelTest`、`check-rules.ps1`、`ci.ps1` 全部通过。

## 11. 待确认

1. 数据使用提示：改为“点击扫描时展示”（推荐），还是保留进入页面自动展示？
2. “已安装应用列表”：直接删除假门禁（推荐），还是本期就评估 `QUERY_ALL_PACKAGES`？
3. 解释卡确认按钮：统一“去设置”（推荐），还是保留“允许”？
4. 视觉：按 feature 基线修正（推荐），还是保留当前设计系统卡、只统一病毒弹窗？
5. 管理权限页：运行时权限是否也走系统优先（推荐），还是保持“直接跳设置”的管理页语义？
