# 权限层全貌

> 状态：**已落地**（2026-11-20）。
> 验证：`:app:compileDebugKotlin`、`:app:testDebugUnitTest`（105 项 0 失败）、`:app:assembleDebug` 全部通过。

## 0. 结论

权限只有两层：**机制**（`base/permission`，4 个文件 342 行）和**提示**（`app/permission`，1 个文件 113 行）。
状态机、拦截器、权限 Host、解释弹窗、`hasRequestedBefore` 标记全部删除。

## 1. 文件清单

| 文件 | 行数 | 职责 |
|---|---:|---|
| `base/permission/AppPermission.kt` | 121 | 6 类权限的判定、运行时权限清单、设置页候选 Intent |
| `base/permission/PermissionGate.kt` | 150 | 已授权放行 / 拉起系统界面 / 返回后复检 / 只跳设置页 |
| `base/permission/PermissionChecker.kt` | 14 | 只读检查，给 ViewModel 用 |
| `base/permission/MediaStoreDeleteRequest.kt` | 57 | 媒体删除授权（`suspend`，结果可信） |
| `app/permission/NotificationPermissionPrompt.kt` | 113 | 通知权限：先系统框，弹不出来再解释并引导设置 |
| `app/platform/permission/PermissionPreferences.kt` | 20 | 只存提示频率时间戳 |
| `base/designsystem/.../NotificationPermissionDialog.kt` | 173 | 通用弹窗视觉 |

## 2. 调用关系

```text
ViewModel（只读）                    Route / EffectHandler（副作用）
      │                                    │
      │ PermissionChecker.isGranted()      │ rememberPermissionGate()
      │                                    │   ├─ allow(p) { 授权后做什么 }
      ▼                                    │   ├─ request(p) { 结果回调 }
domain.port ────────────────────────────────┤   ├─ openSettings(p)
                                            │   └─ shouldShowRationale(p)
                                            ▼
                              base/permission/AppPermission
                                            │
                                            ▼
                              Activity Result API

通知权限：
NotificationPermissionPrompt（挂载于 AppRootView）
      → gate.request(PostNotifications)
      → 失败且无 rationale ⇒ 弹 NotificationPermissionDialog ⇒ gate.openSettings
```

三条原则：

- **副作用归 Route**；ViewModel 只读 `PermissionChecker`。
- **授权结论只来自 `AppPermission.isGranted`**；launcher 回传值不作数（设置页恒 `CANCELED`）。
- **不猜测系统状态**；「弹不弹得出来」由申请结果反推，不落盘。

## 3. `PermissionGate`

```kotlin
@Stable
class PermissionGate internal constructor(
    private val hasPermission: (AppPermission) -> Boolean,
    private val rationale: (AppPermission) -> Boolean,
) {
    fun allow(permission: AppPermission, onGranted: () -> Unit = {})      // 已授权直接执行
    fun request(permission: AppPermission, onResult: (Boolean) -> Unit = {}) // 无条件拉起
    fun openSettings(permission: AppPermission)                            // 只走设置页
    fun shouldShowRationale(permission: AppPermission): Boolean            // 只对运行时权限有意义
}
```

实现要点：

- 单飞：`pendingPermission != null` 时忽略后续请求；`finish()` 只做一次 `isGranted` 复检。
- `launchSystemUi`：有缺失的运行时权限 → 弹系统框；否则依次尝试设置页；全都拉不起来 → 立即回调，避免请求悬空。
- `openSettings` 跳过运行时申请，专供「系统已经不会再弹」的场景。
- 构造函数 `internal`，测试可在 `app/src/test` 直接构造；不持有 `Context`，纯 JVM 可测。

## 4. 通知权限提示

```kotlin
@Composable
fun NotificationPermissionPrompt(
    onGranted: () -> Unit = {},
    onActiveChanged: (Boolean) -> Unit = {},
) {
    // enabled = areNotificationsEnabled()，与「系统是否真的在发通知」一致
    // LaunchedEffect(Unit)：
    //   已授权 → 不做
    //   < 33  → 没有系统框，只能弹说明窗
    //   33+   → gate.request(PostNotifications)
    //           授予                     → onGranted
    //           拒绝 && rationale        → 什么都不做
    //           拒绝 && !rationale       → 系统不会再弹 → 说明窗（每天一次）→ openSettings
    // ON_RESUME → 复检，授予则 onGranted
}
```

**为什么不需要 `hasRequestedBefore`**

平台只在 `shouldShowRequestPermissionRationale` 一个位上表达状态，而它 `false` 同时覆盖「从没问过」和「永久拒绝」。旧实现为此落盘一个标记来区分。新实现改成**先申请、用结果反推**：

- 申请之前不需要区分，两个状态都直接拉系统框；
- 申请之后仍然读到 `false`，只可能是永久拒绝（刚刚问过）。

因此不需要持久化任何「是否申请过」的标记，`PermissionPreferences` 只剩提示频率。

## 5. 行为表

| 场景 | 第一个弹窗 | 之后 |
|---|---|---|
| 首启、未授权（33+） | 系统框 | 拒绝一次 → 静默；再拒 → 永久 |
| 已永久拒绝 | 无（静默失败） | 同一次流程内弹说明窗 → 确认 → 设置页 |
| 当天已引导过 | 无 | 不再弹说明窗 |
| 从设置页返回 | — | `ON_RESUME` 复检，开启则回调 `onGranted` |
| API < 33 且通知关闭 | 说明窗 | 确认 → 设置页 |

## 6. 已删除

| 内容 | 行数 | 原因 |
|---|---:|---|
| `NotificationPermissionPolicy.kt` | 323 | 5 个 phase / 3 个 surface / 会话延后 / 每日频率，被「申请结果反推」取代 |
| `NotificationPermissionViewModel.kt` | 104 | 逻辑并入 Prompt |
| `NotificationPermissionEffects.kt` | 78 | 命令翻译成 launcher 的中间层不再需要 |
| `NotificationPermissionStateMachineTest.kt` | 383 | 随状态机删除 |
| `NotificationPermissionEffectsTest.kt` | 88 | 随 effects 删除 |
| `PermissionRequest.kt`（早期改动） | 63 | 被 `PermissionGate` 取代 |
| 4 个零调用点弹窗（早期改动） | 533 | `AppLockPermissionPopups` / `NotificationEnableGuideDialog` / `PermissionPopups` / `PermissionCopy` |

## 7. 测试

| 文件 | 用例 | 覆盖 |
|---|---:|---|
| `PermissionGateTest.kt` | 8 | 已授权短路、先拉起后复检、结论来自复检、单飞、拉不起来立即回调、未装配立即回调、rationale 委托、openSettings 转发 |
| `NotificationPermissionPromptTest.kt` | 4 | 首次允许、当天拦截、隔日允许、时钟回拨允许 |

全量：105 项，0 失败。

## 8. 待办

1. **真机验证**：通知权限首启系统框、拒绝两次后弹说明窗、设置页返回复检；以及第 10 节的 9 个业务权限往返。
2. **判定口径统一**：`NotificationPermissionPrompt` 用 `NotificationManagerCompat.areNotificationsEnabled()`，而 `AppConfig.hasPostNotificationsPermission()` 仍用 `checkSelfPermission`（前台服务在用）。两者对「用户在设置里关闭通知」的判定不同，建议后续统一为前者。
3. **官方立场**：Google 不建议在用户拒绝后主动引导去设置页（`Don't link to system settings in an effort to convince users to change their decision`）。当前只在系统确实不会再弹时兜底一次，若产品再加主动引导需重新评估。
4. **门禁脚本过期**：`check-mvi-boundaries.ps1` / `check-permission-ad-boundaries.ps1` 仍按 `core/*`、`feature/*` 找模块，跑必然 `Missing module` 失败；后者还要求存在已删除的 `PermissionInterceptor`。架构约束目前无人看守。

## 9. 业务权限调用点（9 处）

| 调用点 | 用法 |
|---|---|
| `JunkCleanRoute` | `allow(StorageFiles) { dispatch(intent) }` |
| `WhatsAppCleanerRoute` | `allow(StorageFiles)` ×2（首屏自动扫描、清理选中） |
| `NetworkScanRoute` | `allow(Location) { dispatch(StartScan) }` |
| `NotificationCleanerRoute` | `allow(NotificationListener) { dispatch(action) }` |
| `AppUsageRoute` / `NetworkUsageRoute` | `request(UsageAccess) { dispatch(Granted/Rejected) }` |
| `AppLockRoute` | `allow(Overlay)`（结果由 `ON_RESUME` 复检） |
| `VirusEffectHandler` | `request(StorageFiles) { dispatch(DeepScanPermissionResult(it)) }` |
| `ManagePermissionsRoute` | `request(permission) { refreshStatuses() }`（已授权行也要能进设置页） |
