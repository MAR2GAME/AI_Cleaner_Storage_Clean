> 历史文档，仅用于追溯；当前结构以 README.md、docs/mvi-architecture.md 和 AGENTS.md 为准。

> 当前生产结构已迁移到多模块 MVI，目录、导航与验证状态以 [MVI 架构规范](../mvi-architecture.md) 和 [迁移记录](multi-module-migration.md) 为准。此文保留为权限、广告职责划分的历史参考；无人处理策略仍返回 Block。

# 重构整体代码（Sample v5 · 历史设计）

> 本文第 3、4 节的“权限拦截器直接持有请求、广告拦截器直接调用 Driver”已被当前实现采用，其余仍是历史示例。生命周期、通知提示和业务行为以 [MVI 规范与垃圾清理试点](mvi-architecture.md) 为准。当前 `resolvePolicies` 对无人处理的策略返回 `Block`；本文后续与其冲突的示例不再适用。

> 纯设计示例，未改动任何现有源码
> v5 相对 v4：`Gate` → 四值 `InterceptResult`（新增 `Busy`）；拦截器即权限协调者（不拆 Coordinator/Session/Gateway）；`resolvePolicies` 语义固定；未处理 policy 放行 + 单测兜底（不 fail-closed）

---

## 文件清单（20 个文件）

```
core/presentation/   IntentPolicy.kt  InterceptResult.kt  IntentInterceptor.kt  MviViewModel.kt
core/file/           FileScanner.kt  FileDeleter.kt
feature/shared/permission/  PermissionNeed.kt
app/permission/      PermissionInterceptor.kt  PermissionHost.kt
core/monetization/   AdPlacement.kt
app/commercial/      AdDriver.kt  AdvertiseAdDriver.kt  AdInterceptor.kt
app/analytics/       AnalyticsEvent.kt  AnalyticsTracker.kt
feature/junkclean/domain/  JunkScanner.kt  JunkCleaner.kt  JunkFileRules.kt
feature/junkclean/data/    AndroidJunkScanner.kt  AndroidJunkCleaner.kt
feature/junkclean/presentation/  Contract.kt  Reducer.kt  ViewModel.kt
feature/junkclean/ui/            Route.kt  Screen.kt
app/                 AppModules.kt  MainActivity.kt  AppRoot.kt
```

---

## 1. `core:presentation`

```kotlin
// ══ IntentPolicy.kt
sealed interface IntentPolicy { val priority: Int }

// ══ InterceptResult.kt
sealed interface InterceptResult {
    /** 当前拦截器不处理这个策略 */
    data object NotHandled : InterceptResult
    /** 检查通过，继续业务 */
    data object Continue : InterceptResult
    /** 明确阻止业务（如用户拒绝权限） */
    data object Block : InterceptResult
    /** 已有同类流程进行中，本次不执行，也不报错 */
    data object Busy : InterceptResult
}

// ══ IntentInterceptor.kt
interface IntentInterceptor {
    suspend fun intercept(policy: IntentPolicy): InterceptResult
}

// ══ MviViewModel.kt
abstract class MviViewModel<S : Any, I : Any, E : Any>(
    initialState: S,
    private val interceptors: List<IntentInterceptor> = emptyList(),
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val effects = Channel<E>(Channel.BUFFERED)
    val effect: Flow<E> = effects.receiveAsFlow()

    private val jobs = mutableMapOf<Any, Job>()

    protected val currentState: S get() = _state.value

    /** 用户意图入口：新意图取消上一条尚未走完的链 */
    fun dispatch(intent: I) = runLatest(DISPATCH_KEY) { chain(intent) }

    /** 异步结果直达：不过拦截链 */
    protected fun dispatchInternal(intent: I) = onIntent(intent)

    private suspend fun chain(intent: I) {
        if (!canDispatch(intent)) return
        val policies = policiesFor(intent)
        if (policies.isNotEmpty()) {
            val result = resolvePolicies(policies)
            if (result != InterceptResult.Continue) return onIntentBlocked(intent, result)
        }
        if (canDispatch(intent)) onIntent(intent)          // 挂起期间状态可能已变
    }

    /**
     * 按 priority 逐个策略执行。
     * 第一个返回非 NotHandled 的拦截器决定该策略的结果。
     * 未注册拦截器的策略直接放行 —— 配置错误由 PolicyCoverageTest 捕获，
     * 不在运行期 fail-closed（否则新增 policy 忘了注册会让整条业务静默失效）。
     */
    protected suspend fun resolvePolicies(policies: List<IntentPolicy>): InterceptResult {
        for (policy in policies.sortedBy { it.priority }) {
            for (interceptor in interceptors) {
                when (val result = interceptor.intercept(policy)) {
                    InterceptResult.NotHandled -> continue
                    InterceptResult.Continue -> break
                    else -> return result
                }
            }
        }
        return InterceptResult.Continue
    }

    /** 同一 key 只保留最新一次 */
    protected fun runLatest(key: Any, block: suspend CoroutineScope.() -> Unit) {
        jobs[key]?.cancel()
        jobs[key] = viewModelScope.launch(block = block)
    }

    protected abstract fun onIntent(intent: I)
    protected open fun policiesFor(intent: I): List<IntentPolicy> = emptyList()
    protected open fun canDispatch(intent: I) = true

    /** result 只可能是 Block 或 Busy */
    protected open fun onIntentBlocked(intent: I, result: InterceptResult) = Unit

    protected fun updateState(reduce: S.() -> S) = _state.update(reduce)
    protected fun emitEffect(e: E) { effects.trySend(e) }

    override fun onCleared() {
        jobs.values.forEach(Job::cancel)
        effects.close()
        super.onCleared()
    }

    private companion object { val DISPATCH_KEY = Any() }
}
```

---

## 2. `core:file`（机制）

```kotlin
data class FileEntry(val path: String, val name: String, val sizeBytes: Long, val modifiedSeconds: Long)

sealed interface ScanProgress {
    data class Running(val percent: Float, val foundBytes: Long, val category: String?) : ScanProgress
    data class Done(val entries: List<FileEntry>) : ScanProgress
}

data class DeleteResult(
    val deleted: List<String>,
    val failed: List<String>,
    val pendingAuthorization: List<String>,
    val freedBytes: Long,
)

interface FileScanner { fun scan(predicate: (FileEntry) -> Boolean): Flow<ScanProgress> }
interface FileDeleter { suspend fun delete(paths: Set<String>): DeleteResult }
```

---

## 3. 权限（3 个文件）

```kotlin
// ══ PermissionNeed.kt —— 5 种权限，各 3 个小函数
enum class PermissionNeed(val key: String) : IntentPolicy {

    StorageFiles("storage") {
        override fun isGranted(c: Context) =
            if (SDK_INT >= 30) runCatching { Environment.isExternalStorageManager() }.getOrDefault(false)
            else runtimePermissions(c).all(c::hasGranted)
        override fun runtimePermissions(c: Context) =
            if (SDK_INT >= 30) emptyList()
            else listOfNotNull(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE.takeIf { SDK_INT <= 29 })
        override fun settingsIntents(c: Context) = listOf(
            Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:${c.packageName}")),
            appDetails(c),
        )
    },

    Location("location") {
        override fun isGranted(c: Context) = c.hasGranted(ACCESS_FINE_LOCATION)
        override fun runtimePermissions(c: Context) = listOf(ACCESS_FINE_LOCATION)
        override fun settingsIntents(c: Context) = listOf(appDetails(c))
    },

    UsageAccess("usage") {
        override fun isGranted(c: Context) = runCatching {
            (c.getSystemService(APP_OPS_SERVICE) as AppOpsManager)
                .checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), c.packageName)
        }.getOrDefault(MODE_ERRORED) == MODE_ALLOWED
        override fun runtimePermissions(c: Context) = emptyList()
        override fun settingsIntents(c: Context) = listOf(Intent(ACTION_USAGE_ACCESS_SETTINGS), appDetails(c))
    },

    NotificationListener("listener") {
        override fun isGranted(c: Context) =
            Settings.Secure.getString(c.contentResolver, "enabled_notification_listeners")
                ?.split(':')?.any { it.contains(c.packageName, ignoreCase = true) } == true
        override fun runtimePermissions(c: Context) = emptyList()
        override fun settingsIntents(c: Context) =
            listOf(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS), appDetails(c))
    },

    Overlay("overlay") {
        override fun isGranted(c: Context) = SDK_INT < 23 || Settings.canDrawOverlays(c)
        override fun runtimePermissions(c: Context) = emptyList()
        override fun settingsIntents(c: Context) = listOf(
            Intent(ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${c.packageName}")),
            appDetails(c),
        )
    };

    override val priority: Int get() = 100

    abstract fun isGranted(context: Context): Boolean

    /** 非空 → 弹系统框；空 → 只能跳设置页 */
    abstract fun runtimePermissions(context: Context): List<String>
    abstract fun settingsIntents(context: Context): List<Intent>
}

enum class PermissionResult { Granted, Denied, Busy }

private fun Context.hasGranted(p: String) =
    ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED

private fun appDetails(c: Context) =
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${c.packageName}"))
```

```kotlin
// ══ PermissionInterceptor.kt —— 既是拦截器，也是权限协调者（30 行）
class PermissionInterceptor(private val context: Context) : IntentInterceptor {

    private var pending: CompletableDeferred<PermissionResult>? = null

    private val _request = MutableStateFlow<Request?>(null)
    val request: StateFlow<Request?> = _request.asStateFlow()

    override suspend fun intercept(policy: IntentPolicy): InterceptResult {
        if (policy !is PermissionNeed) return InterceptResult.NotHandled
        return when (await(policy)) {
            PermissionResult.Granted -> InterceptResult.Continue
            PermissionResult.Denied -> InterceptResult.Block
            PermissionResult.Busy -> InterceptResult.Busy
        }
    }

    private suspend fun await(need: PermissionNeed): PermissionResult {
        if (need.isGranted(context)) return PermissionResult.Granted
        if (_request.value != null) return PermissionResult.Busy      // 已有流程进行中

        val deferred = CompletableDeferred<PermissionResult>()
        pending = deferred
        _request.value = Request(need, Stage.AwaitingUser)
        return deferred.await()
    }

    // ── Host 的调用面 ──

    /** 用户在解释对话框点了确认 */
    fun confirm() {
        _request.value = _request.value
            ?.takeIf { it.stage == Stage.AwaitingUser }
            ?.copy(stage = Stage.LaunchRequested)
    }

    /** Host 启动完系统 UI */
    fun markLaunched() {
        _request.value = _request.value?.copy(stage = Stage.Launched)
    }

    /** 系统权限结果 / 设置页返回 / 启动失败 —— 一律按事实复检 */
    fun complete() {
        val need = _request.value?.need
        finish(if (need != null && need.isGranted(context)) PermissionResult.Granted else PermissionResult.Denied)
    }

    fun dismiss() = finish(PermissionResult.Denied)

    /** 宿主销毁兜底，避免链永久挂起 */
    fun cancelAll() {
        if (_request.value != null) finish(PermissionResult.Denied)
    }

    private fun finish(result: PermissionResult) {
        pending?.complete(result)
        pending = null
        _request.value = null
    }

    data class Request(val need: PermissionNeed, val stage: Stage)
    enum class Stage { AwaitingUser, LaunchRequested, Launched }
}
```

```kotlin
// ══ PermissionHost.kt —— 只做 UI 与 Android 副作用
@Composable
fun PermissionHost(interceptor: PermissionInterceptor) {
    val context = LocalContext.current
    val request by interceptor.request.collectAsStateWithLifecycle()

    val runtimeLauncher = rememberLauncherForActivityResult(RequestMultiplePermissions()) {
        interceptor.complete()
    }
    val settingsLauncher = rememberLauncherForActivityResult(StartActivityForResult()) {
        interceptor.complete()
    }

    LaunchedEffect(request) {
        val r = request ?: return@LaunchedEffect
        if (r.stage != PermissionInterceptor.Stage.LaunchRequested) return@LaunchedEffect

        val perms = r.need.runtimePermissions(context)
        if (perms.isNotEmpty()) {
            runtimeLauncher.launch(perms.toTypedArray())
        } else {
            val launched = r.need.settingsIntents(context).firstNotNullOfOrNull { intent ->
                runCatching { settingsLauncher.launch(intent); true }.getOrNull()
            } ?: false
            if (!launched) {
                interceptor.complete()                 // 全失败 → 按未授予收尾
                return@LaunchedEffect
            }
        }
        interceptor.markLaunched()
    }

    if (request?.stage == PermissionInterceptor.Stage.AwaitingUser) {
        PermissionExplanationDialog(
            need = request!!.need,
            onConfirm = { interceptor.confirm() },
            onDismiss = { interceptor.dismiss() },
        )
    }
}
```

---

## 4. 广告（3 个文件）

```kotlin
// ══ AdPlacement.kt —— 34 个位，与 config/ad_policy.json 一一对应
enum class AdPlacement(val areaKey: String) : IntentPolicy {
    Open("openPageAdv"), Foreground("foreGroundAdv"),
    GuideScanFinish("newGuideScanFinishAdv"), GuideSkip("newGuideSkipAdv"),
    EnterJunkClean("mainJunkCleanAdv"), EnterFileManage("mainFileManageAdv"),
    EnterNotificationClean("mainNotificationCleanAdv"), EnterWhatsappClean("mainWhatsappCleanAdv"),
    EnterVirusAnti("mainVirusAntiAdv"), EnterAppLock("mainAppLockAdv"),
    EnterDeviceInfo("mainDeviceInfoAdv"), EnterBatteryInfo("mainBatteryInfoAdv"),
    EnterAppUsage("mainAppUsageAdv"), EnterNetworkUsage("mainNetworkUsageAdv"),
    EnterNetworkScan("mainNetworkScanAdv"), EnterNetworkSpeed("mainNetworkSpeedAdv"),
    HomeBottom("openMainBottomAdv"),
    JunkCleanFinish("mainJunkCleanFinishAdv"), FileManageFinish("mainFileManageFinishAdv"),
    NotificationCleanFinish("mainNotificationCleanFinishAdv"),
    WhatsappCleanFinish("mainWhatsappCleanFinishAdv"), NetworkSpeedFinish("mainNetworkSpeedFinishAdv"),
    ReturnFromJunkClean("returnFromJunkCleanAdv"), ReturnFromVirusAnti("returnFromVirusAntiAdv"),
    ReturnFromAppLock("returnFromAppLockAdv"), ReturnFromFileManage("returnFromFileManageAdv"),
    ReturnFromDeviceInfo("returnFromDeviceInfoAdv"), ReturnFromBatteryInfo("returnFromBatteryInfoAdv"),
    ReturnFromAppUsage("returnFromAppUsageAdv"),
    ReturnFromNotificationClean("returnFromNotificationCleanAdv"),
    ReturnFromWhatsappClean("returnFromWhatsappCleanAdv"),
    ReturnFromNetworkUsage("returnFromNetworkUsageAdv"),
    ReturnFromNetworkScan("returnFromNetworkScanAdv"), ReturnFromNetworkSpeed("returnFromNetworkSpeedAdv");

    override val priority: Int get() = 900
}

enum class PreloadStage { Startup, EnterFeature, PlayFinish }
```

```kotlin
// ══ AdDriver.kt —— 唯一的 SDK 边界
interface AdDriver {
    fun isAvailable(): Boolean
    fun isOpenAdEnabled(): Boolean
    suspend fun consent(activity: Activity): Boolean
    fun showOpen(activity: Activity, areaKey: String, onClosed: () -> Unit): Boolean
    fun showInterstitial(areaKey: String, onClosed: () -> Unit): Boolean
    fun preload(stage: PreloadStage)

    /** 播放插屏并等它关闭；超时由调用方负责 */
    suspend fun play(areaKey: String) {
        val closed = CompletableDeferred<Unit>()
        showInterstitial(areaKey) { closed.complete(Unit) }
        closed.await()
    }
}

/** 冷启动：consent → 开屏 → 预热。顶层扩展，不需要类 */
suspend fun AdDriver.showColdStart(activity: Activity) {
    consent(activity)
    if (isOpenAdEnabled() && isAvailable()) {
        val closed = CompletableDeferred<Unit>()
        if (showOpen(activity, AdPlacement.Open.areaKey) { closed.complete(Unit) }) {
            withTimeoutOrNull(8_000) { closed.await() }
        }
    }
    preload(PreloadStage.Startup)
}
```

```kotlin
// ══ AdvertiseAdDriver.kt
class AdvertiseAdDriver(
    private val context: Application,
    private val activityProvider: () -> Activity?,
) : AdDriver {

    private fun activity() = activityProvider()?.takeUnless { it.isFinishing || it.isDestroyed }

    override fun isAvailable() = activity() != null
    override fun isOpenAdEnabled() = AdvertiseSdk.isAppOpenAdEnabled

    override suspend fun consent(activity: Activity): Boolean =
        withTimeoutOrNull(6_500) {
            suspendCancellableCoroutine { c ->
                val cached = AdvertiseSdk.initConsent(activity) { if (c.isActive) c.resume(true) }
                if (cached && c.isActive) c.resume(true)
            }
        } ?: false

    override fun showOpen(activity: Activity, areaKey: String, onClosed: () -> Unit): Boolean =
        runCatching {
            AdvertiseSdk.showOpenAd(
                activity = activity,
                areaKey = areaKey,
                onCloseListener = AdvertiseSdk.OpenAdCloseListener(onClosed),
                onLoadedListener = AdvertiseSdk.OpenAdLoadedListener { },
                onPaidListener = AdvertiseSdk.OpenAdPaidListener { },
            )
            true
        }.getOrDefault(false)

    override fun showInterstitial(areaKey: String, onClosed: () -> Unit): Boolean {
        val activity = activity() ?: return false
        return runCatching { AdvertiseSdk.showInterstitialAd(activity, areaKey, onClosed); true }.getOrDefault(false)
    }

    override fun preload(stage: PreloadStage) {
        val key = when (stage) {
            PreloadStage.Startup -> AdvertiseSdk.LOAD_TIME_OPEN_APP
            PreloadStage.EnterFeature -> AdvertiseSdk.LOAD_TIME_ENTER_FEATURE
            PreloadStage.PlayFinish -> AdvertiseSdk.LOAD_TIME_PLAY_FINISH
        }
        runCatching {
            if (AdvertiseSdk.canPreloadOpen(key)) AdvertiseSdk.preloadOpen(context, key)
            if (AdvertiseSdk.canPreloadInterstitial(key)) AdvertiseSdk.preloadInterstitial(context, key)
        }
    }
}
```

```kotlin
// ══ AdInterceptor.kt（12 行）
class AdInterceptor(
    private val driver: AdDriver,
    private val logger: EventLogger,
) : IntentInterceptor {

    override suspend fun intercept(policy: IntentPolicy): InterceptResult {
        if (policy !is AdPlacement) return InterceptResult.NotHandled
        if (!driver.isAvailable()) {
            logger.record(LogEvent("ad_skipped", mapOf("placement" to policy.areaKey, "reason" to "no_activity")))
            return InterceptResult.Continue                     // 不展示 → 放行
        }
        withTimeoutOrNull(8_000) { driver.play(policy.areaKey) }   // 超时也继续
        return InterceptResult.Continue                          // ★ 广告永不阻断业务
    }
}
```

---

## 5. 打点

```kotlin
// ══ AnalyticsEvent.kt（现网 17 个 + 新增 3 个）
enum class AnalyticsEvent(val key: String) {
    SplashDisplay("splash_display"),
    EnterGuideScanning("enter_guide_page_scanning"),
    EnterGuideScanResult("enter_guide_page_scan_result"),
    ClickContinueGuide("click_continue_guidepage"),
    ClickSkipGuide("click_skip_guidepage"),
    AgreeFileManagePermission("agree_filemanage_permission"),
    DisagreeFileManagePermission("disagree_filemanage_permission"),
    AgreePushPermission("agree_push_permission"),
    DisagreePushPermission("disagree_push_permission"),
    EnterHomepage("enter_homepage"),
    ClickCoreFeatureBtn("click_corefeatures_btn"),
    EnterCoreFeaturePage("enter_corefeatures_page"),
    RatePopup("rate_popup"),
    FileManagerPopup("filemanager_popup"),
    NotificationPopup("notification_popup"),
    CheckPrivacy("check_privacy"),
    CheckTerms("check_terms"),
    CleanStarted("clean_started"),
    CleanFinished("clean_finished"),
    PermissionResult("permission_result"),
}

object Param {
    const val DURATION_TIME = "duration_time"
    const val REFERRER_NAME = "referrer_name"
    const val DETAILS = "details"
    const val IF_OK = "if_ok"
    const val NOTICE_FLAG = "noticeflag"
    const val TRAFFIC_SOURCE = "traffic_source"
    const val PERMISSION = "permission"
    const val FREED_BYTES = "freed_bytes"
    const val FAILED_COUNT = "failed_count"
}

// ══ AnalyticsTracker.kt（object，无接口）
object AnalyticsTracker {
    private var sink: ((String, Map<String, Any>) -> Unit)? = null

    fun install(sink: (String, Map<String, Any>) -> Unit) { this.sink = sink }

    fun report(event: AnalyticsEvent, vararg params: Pair<String, Any?>) {
        runCatching { sink?.invoke(event.key, params.toMap().clean()) }
    }

    private fun Map<String, Any?>.clean(): Map<String, Any> =
        mapNotNull { (k, v) ->
            if (k.isBlank()) null
            else when (v) {
                null -> null
                is String -> v.takeIf { it.isNotBlank() }?.let { k to it }
                is Number, is Boolean -> k to v
                else -> v.toString().takeIf { it.isNotBlank() }?.let { k to it }
            }
        }.toMap()
}
```

---

## 6. 领域层

```kotlin
// ══ JunkScanner.kt / JunkCleaner.kt
interface JunkScanner {
    fun scan(): Flow<ScanProgress>
    val running: StateFlow<Boolean>
}

interface JunkCleaner {
    suspend fun clean(ids: Set<String>): CleanOutcome
    val running: StateFlow<Boolean>
}

sealed interface CleanOutcome {
    data class Done(val freedBytes: Long, val cleaned: Int, val failed: Int) : CleanOutcome
    data class NeedsAuthorization(val uris: List<String>) : CleanOutcome
}

// ══ JunkFileRules.kt —— "什么算垃圾"，纯函数 + 白名单表
object JunkFileRules {
    fun isJunk(e: FileEntry): Boolean {
        val parent = e.path.substringBeforeLast('/').substringAfterLast('/').lowercase()
        return parent in CACHE_DIR_NAMES || e.name.endsWith(".cache")
    }

    /** 白名单精确匹配：禁止 substring 判定（"Download".contains("ad") 会误删下载目录） */
    private val CACHE_DIR_NAMES = setOf("cache", "code_cache", "temp", "tmp", "adcache", "ads_cache")
}

// ══ AndroidJunkScanner.kt / AndroidJunkCleaner.kt
class AndroidJunkScanner(private val scanner: FileScanner) : JunkScanner {
    private val _running = MutableStateFlow(false)
    override val running: StateFlow<Boolean> = _running.asStateFlow()
    override fun scan(): Flow<ScanProgress> =
        scanner.scan(JunkFileRules::isJunk).onStart { _running.value = true }.onCompletion { _running.value = false }
}

class AndroidJunkCleaner(private val deleter: FileDeleter) : JunkCleaner {
    private val _running = MutableStateFlow(false)
    override val running: StateFlow<Boolean> = _running.asStateFlow()

    override suspend fun clean(ids: Set<String>): CleanOutcome {
        _running.value = true
        try {
            val r = deleter.delete(ids)
            return if (r.pendingAuthorization.isEmpty())
                CleanOutcome.Done(r.freedBytes, r.deleted.size, r.failed.size)
            else CleanOutcome.NeedsAuthorization(r.pendingAuthorization)
        } finally {
            _running.value = false
        }
    }
}
```

---

## 7. 页面五件套（junkclean）

```kotlin
// ══ Contract.kt
data class JunkCleanUiState(
    val phase: JunkCleanPhase = JunkCleanPhase.Idle,
    val selected: Set<String> = emptySet(),
    val allKeys: Set<String> = emptySet(),
) {
    val canClean: Boolean get() = selected.isNotEmpty()
}

sealed interface JunkCleanPhase {
    data object Idle : JunkCleanPhase
    data class Scanning(val progress: Float, val foundBytes: Long) : JunkCleanPhase
    data class Preview(val groups: List<JunkGroupUi>, val totalBytes: Long) : JunkCleanPhase
    data object Cleaning : JunkCleanPhase
    data class Complete(val freedBytes: Long, val cleaned: Int, val failed: Int) : JunkCleanPhase
    data class MissingPermission(val need: PermissionNeed) : JunkCleanPhase
    data class Failed(val error: AppError) : JunkCleanPhase
}

sealed interface JunkCleanIntent {
    data object Entered : JunkCleanIntent
    data object ScanRequested : JunkCleanIntent
    data class CleanClicked(val ids: Set<String>) : JunkCleanIntent      // 自足
    data object ResultRevealRequested : JunkCleanIntent                  // 完成广告挂这里
    data object BackClicked : JunkCleanIntent
    data class ToggleItem(val id: String) : JunkCleanIntent
    data object ToggleAll : JunkCleanIntent
    data class AuthorizationAnswered(val granted: Boolean) : JunkCleanIntent
}

internal data class JunkScanProgressed(val progress: Float, val bytes: Long) : JunkCleanIntent
internal data class JunkScanFinished(val entries: List<FileEntry>) : JunkCleanIntent
internal data class JunkScanFailed(val error: AppError) : JunkCleanIntent
internal data class JunkCleanFinished(val outcome: CleanOutcome.Done) : JunkCleanIntent
internal data class JunkCleanFailed(val error: AppError) : JunkCleanIntent
internal data class JunkScanCancelled : JunkCleanIntent

sealed interface JunkCleanEffect {
    data object NavigateBack : JunkCleanEffect
    data class AskDeleteAuthorization(val uris: List<String>) : JunkCleanEffect
}
```

```kotlin
// ══ Reducer.kt（纯函数：无 I/O / 无格式化 / 无时间）
internal object JunkCleanReducer {
    fun reduce(state: JunkCleanUiState, intent: JunkCleanIntent): JunkCleanUiState = when (intent) {
        JunkCleanIntent.Entered -> state
        JunkCleanIntent.ScanRequested -> state.copy(phase = JunkCleanPhase.Scanning(0f, 0L))

        is JunkScanProgressed ->
            if (state.phase is JunkCleanPhase.Scanning)
                state.copy(phase = JunkCleanPhase.Scanning(intent.progress, intent.bytes))
            else state

        is JunkScanFinished -> state.copy(
            phase = JunkCleanPhase.Preview(intent.entries.toUiGroups(), intent.entries.sumOf { it.sizeBytes }),
            allKeys = intent.entries.mapTo(linkedSetOf()) { it.path },
            selected = intent.entries.mapTo(linkedSetOf()) { it.path },
        )

        is JunkScanFailed -> state.copy(phase = JunkCleanPhase.Failed(intent.error))
        is JunkCleanIntent.CleanClicked -> state.copy(phase = JunkCleanPhase.Cleaning)
        is JunkCleanFinished -> state.copy(
            phase = JunkCleanPhase.Complete(intent.outcome.freedBytes, intent.outcome.cleaned, intent.outcome.failed),
            selected = emptySet(),
        )
        is JunkCleanFailed -> state.copy(phase = JunkCleanPhase.Failed(intent.error))
        is JunkCleanIntent.ToggleItem -> state.copy(selected = state.selected.toggle(intent.id))
        JunkCleanIntent.ToggleAll ->
            state.copy(selected = if (state.selected.size == state.allKeys.size) emptySet() else state.allKeys)
        else -> state
    }

    /** 权限被拒 */
    fun reduceBlocked(state: JunkCleanUiState, need: PermissionNeed) =
        state.copy(phase = JunkCleanPhase.MissingPermission(need))

    /** 广告期间取消 / 离开页面：回到 Idle，而不是快照回滚 */
    fun reduceAborted(state: JunkCleanUiState) =
        if (state.phase is JunkCleanPhase.Cleaning) state else JunkCleanUiState(selected = state.selected)
}
```

```kotlin
// ══ ViewModel.kt
internal class JunkCleanViewModel(
    private val scanner: JunkScanner,
    private val cleaner: JunkCleaner,
    interceptors: List<IntentInterceptor>,
) : MviViewModel<JunkCleanUiState, JunkCleanIntent, JunkCleanEffect>(
    initialState = JunkCleanUiState(),
    interceptors = interceptors,
) {
    override fun policiesFor(intent: JunkCleanIntent): List<IntentPolicy> = when (intent) {
        JunkCleanIntent.Entered -> listOf(AdPlacement.EnterJunkClean)

        JunkCleanIntent.ScanRequested -> listOf(PermissionNeed.StorageFiles)

        is JunkCleanIntent.CleanClicked -> listOf(PermissionNeed.StorageFiles)

        JunkCleanIntent.ResultRevealRequested -> listOf(AdPlacement.JunkCleanFinish)

        JunkCleanIntent.BackClicked ->
            if (currentState.phase is JunkCleanPhase.Scanning || currentState.phase is JunkCleanPhase.Cleaning)
                emptyList()                                             // 执行中不声明广告位
            else listOf(AdPlacement.ReturnFromJunkClean)

        else -> emptyList()
    }

    override fun canDispatch(intent: JunkCleanIntent): Boolean = when (intent) {
        JunkCleanIntent.ScanRequested ->
            currentState.phase is JunkCleanPhase.Idle || currentState.phase is JunkCleanPhase.Failed
        is JunkCleanIntent.CleanClicked -> currentState.phase is JunkCleanPhase.Preview
        JunkCleanIntent.ResultRevealRequested -> currentState.phase is JunkCleanPhase.Cleaning
        else -> true
    }

    override fun onIntentBlocked(intent: JunkCleanIntent, result: InterceptResult) {
        if (result != InterceptResult.Block) return                        // Busy：静默，保持原状态
        val need = policiesFor(intent).filterIsInstance<PermissionNeed>().firstOrNull() ?: return
        updateState { JunkCleanReducer.reduceBlocked(this, need) }
    }

    override fun onIntent(intent: JunkCleanIntent) {
        when (intent) {
            JunkCleanIntent.Entered ->
                AnalyticsTracker.report(AnalyticsEvent.EnterCoreFeaturePage, Param.DETAILS to "junkClean")

            JunkCleanIntent.ScanRequested -> runLatest(KEY_SCAN) {
                scanner.scan().collect { progress ->
                    when (progress) {
                        is ScanProgress.Running ->
                            dispatchInternal(JunkScanProgressed(progress.percent, progress.foundBytes))
                        is ScanProgress.Done -> dispatchInternal(JunkScanFinished(progress.entries))
                    }
                }
            }

            is JunkCleanIntent.CleanClicked -> {
                AnalyticsTracker.report(AnalyticsEvent.CleanStarted, Param.DETAILS to "junkClean")
                runLatest(KEY_CLEAN) {
                    try {
                        when (val outcome = cleaner.clean(intent.ids)) {
                            is CleanOutcome.Done -> {
                                dispatchInternal(JunkCleanFinished(outcome))
                                AnalyticsTracker.report(
                                    AnalyticsEvent.CleanFinished,
                                    Param.DETAILS to "junkClean",
                                    Param.FREED_BYTES to outcome.freedBytes,
                                    Param.FAILED_COUNT to outcome.failed,
                                )
                                delay(COMPLETE_ANIMATION_MS)
                                dispatch(JunkCleanIntent.ResultRevealRequested)   // 走链 → 完成广告
                            }
                            is CleanOutcome.NeedsAuthorization ->
                                emitEffect(JunkCleanEffect.AskDeleteAuthorization(outcome.uris))
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        dispatchInternal(JunkCleanFailed(AppError.Clean(e)))
                    }
                }
            }

            JunkCleanIntent.BackClicked -> emitEffect(JunkCleanEffect.NavigateBack)
            else -> updateState { JunkCleanReducer.reduce(this, intent) }
        }
    }

    private companion object {
        const val KEY_SCAN = "scan"
        const val KEY_CLEAN = "clean"
        const val COMPLETE_ANIMATION_MS = 1_600L
    }
}
```

```kotlin
// ══ Route.kt
@Composable
fun JunkCleanRoute(
    navigator: AppNavigator,
    viewModel: JunkCleanViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(StartIntentSenderForResult()) { result ->
        viewModel.dispatch(JunkCleanIntent.AuthorizationAnswered(result.resultCode == Activity.RESULT_OK))
    }

    LifecycleResumeEffect(viewModel) {
        viewModel.dispatch(JunkCleanIntent.Entered)          // 幂等
        viewModel.dispatch(JunkCleanIntent.ScanRequested)    // 幂等（canDispatch 守卫）
        onPauseOrDispose { }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                JunkCleanEffect.NavigateBack -> navigator.back()
                is JunkCleanEffect.AskDeleteAuthorization -> launcher.launch(
                    IntentSenderRequest.Builder(
                        MediaStore.createDeleteRequest(context.contentResolver, effect.uris.map(Uri::parse)).intentSender,
                    ).build(),
                )
            }
        }
    }

    BackHandler { viewModel.dispatch(JunkCleanIntent.BackClicked) }

    JunkCleanScreen(
        state = state,
        onIntent = { intent ->
            if (intent is JunkCleanIntent.CleanClicked) {
                viewModel.dispatch(JunkCleanIntent.CleanClicked(state.selected))   // 注入当时状态
            } else {
                viewModel.dispatch(intent)
            }
        },
    )
}

// ══ Screen.kt（只吃 state + onIntent）
@Composable
fun JunkCleanScreen(
    state: JunkCleanUiState,
    onIntent: (JunkCleanIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val phase = state.phase) {
        JunkCleanPhase.Idle -> IdlePart(modifier)
        is JunkCleanPhase.Scanning -> ScanPart(phase, modifier)
        is JunkCleanPhase.Preview -> PreviewPart(phase, state.selected, onIntent, modifier)
        JunkCleanPhase.Cleaning -> CleaningPart(modifier)
        is JunkCleanPhase.Complete -> ResultPart(phase, onIntent, modifier)
        is JunkCleanPhase.MissingPermission -> MissingPermissionPart(phase.need, onIntent, modifier)
        is JunkCleanPhase.Failed -> ErrorPart(phase.error, modifier)
    }
}
```

---

## 8. 装配

```kotlin
// ══ AppModules.kt
val interceptorsModule = module {
    single { PermissionInterceptor(androidContext()) }
    single { AdInterceptor(driver = get(), logger = get()) }
    single<IntentInterceptor> { get<PermissionInterceptor>() }
    single<IntentInterceptor> { get<AdInterceptor>() }
}

val adsModule = module {
    single<AdDriver> { AdvertiseAdDriver(androidApplication(), activityProvider = { currentActivity() }) }
}

val junkCleanModule = module {
    single<FileScanner> { AndroidFileScanner(androidContext()) }
    single<FileDeleter> { AndroidFileDeleter(androidContext()) }
    single<JunkScanner> { AndroidJunkScanner(get()) }
    single<JunkCleaner> { AndroidJunkCleaner(get()) }
    viewModel { JunkCleanViewModel(get(), get(), getAll()) }     // getAll() = List<IntentInterceptor>
}
```

```kotlin
// ══ MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { StorageCleanTheme { AppRoot() } }            // 立即出首帧
        if (savedInstanceState != null) return                   // 重建不重放开屏
        lifecycleScope.launch {
            val sdk: SdkInitialization = getKoin().get()
            if (!sdk.awaitReady(6_500)) return@launch
            getKoin().get<AdDriver>().showColdStart(this@MainActivity)   // 内部 8s 硬超时
        }
    }
}

// ══ AppRoot.kt
@Composable
fun AppRoot() {
    val nav = rememberNavController()
    val permission: PermissionInterceptor = koinInject()

    StorageCleanTheme {
        AppNavGraph(nav)
        PermissionHost(permission)
        DisposableEffect(Unit) { onDispose { permission.cancelAll() } }   // 安全网
    }
}
```

---

## 9. 配置错误的兜底：一条单测

```kotlin
class PolicyCoverageTest {
    private val interceptors = listOf(FakePermissionInterceptor(), FakeAdInterceptor())

    @Test fun `每个 policy 都有拦截器处理`() {
        val allPolicies: List<IntentPolicy> = PermissionNeed.entries + AdPlacement.entries
        runBlocking {
            for (policy in allPolicies) {
                val handled = interceptors.any { it.intercept(policy) != InterceptResult.NotHandled }
                assertTrue(handled, "policy ${policy::class.simpleName} 没有拦截器")
            }
        }
    }
}
```

---

## 10. 待决

| # | 事项 | 建议 |
|---|---|---|
1 | 存储权限是否改 MediaStore + SAF（放弃 `MANAGE_EXTERNAL_STORAGE`） | 先保留（改它会让垃圾扫描少扫 `Android/data`） |
2 | `Foreground`（`foreGroundAdv`）与 `HomeBottom`（`openMainBottomAdv`）现未使用 | 保留枚举但不声明策略 |
3 | SDK 后台广告报表是否已覆盖曝光/点击 | 若已覆盖，App 侧不报广告埋点（本版按此实现） |
