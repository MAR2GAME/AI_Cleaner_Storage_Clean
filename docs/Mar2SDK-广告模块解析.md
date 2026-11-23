# Mar2SDK 广告模块解析（2.0.15）

> 读的是 `D:\pros\my\Mar2SDK` 工作树（tag `v2.0.15` 之后的状态）与 Maven `core/impl:2.0.15` 的公开 API。
> 目标：把「广告怎么被决定、怎么加载、怎么比价、怎么展示、怎么算钱、怎么打点」讲清楚，能独立排障。
> 本文只解析，不给接入建议；接入方案见 `docs/Mar2SDK-2.0.15-接入方案-v2.md`。

---

## 0. 速览

| 维度 | 事实 |
|---|---|
| 模块 | `core`（策略/竞价/各平台适配/日志）、`impl`（`AdActivity`、`AppOpenAdManager`、页面级 `BaseScreen`）、`debug`（调试页）、`app`（示例） |
| 平台 | AdMob、MAX、TopOn、TradPlus、Unity LevelPlay（`AdPlatform`） |
| 格式 | `OPEN`、`INTER`、`VIDEO` + 三种组合（`OPEN_INTER`/`INTER_VIDEO`/`VIDEO_INTER`）+ `BANNER`、`NATIVE` |
| 展示模式 | `MODE_555` 单平台单格式、`MODE_666` 单平台组合比价、`MODE_777` 跨平台比价、`MODE_888` 比价后探价 |
| 策略 | 11 项检查（全局开关 / 全局频控 3 项 / 点位 6 项 / 概率） |
| 价格口径 | 统一 USD eCPM 微单位（`Long`），AdMob 支持反射取价与探针取价 |
| 埋点 | `ad_trigger` → `ad_policy` → `ad_occur` → `ad_start_loading/ad_load_*/ad_impression/ad_click/ad_close/ad_show_fail/ad_show_timeout/ad_revenue/ad_explore` |
| 配置 | `res/raw` → SharedPreferences → RemoteConfig/服务端，三级覆盖 |

一句话：**它是一个「以点位（areaKey）为粒度、以本地曝光日志为频控依据、以 eCPM 比价为选优手段、用独立 Activity 承载全屏广告」的聚合广告中间层。**

---

## 1. 模块边界

```text
宿主 APP
  ├─ 直接依赖 core ──────────────► Core / AdShower / AdLoader / AdConfig / RiskUtil
  └─ 依赖 impl ──► AdActivity / AppOpenAdManager / BaseScreen / Mar2Application
                        │
                        ▼
core 内部
  ad/
   ├─ AdShower.kt          平台分派（showOpen/showInter/showVideo/组合格式）
   ├─ AdLoader.kt          预加载入口（fillAd）
   ├─ AdIniter.kt          平台初始化（默认平台 + 按需 ensureInitialized）
   ├─ AdConfig.kt          广告策略配置（点位表、频控、模式、autoRate）
   ├─ policy/              AdPolicy（频控/概率）、AutoRateShower（格式配比）、ScreenAdContext
   ├─ auction/             FullscreenAuction（777/888 的候选收集、排序、回退）
   └─ impl/<platform>/     每平台 4 件套：Initer / Loader（池）/ Shower（展示）/ Config
```

关键事实：

- **策略与竞价全在 `core`**，`impl` 只提供“承载页面”和“前台机会”。
- **每个平台都有独立的广告池**（`AdmobLoader.openPool` 等），池的容量/有效期来自 `admob_config.json` 的 `poolSize` / `timeout`。
- **`core` 的 `AdConfig` 只描述“该不该展示、展示什么格式”**；具体广告位 ID 在 `admob_config`（以及 `max/topon/tradplus/unity_config`）里。

---

## 2. 核心类型字典

| 类型 | 位置 | 作用 |
|---|---|---|
| `ScreenAdContext` | `ad/policy/AdContext.kt` | 一次广告请求的上下文：`requestId`、`areaKey`、`adFormat`、`adPlatform`、`trigger`、`fromRoute`/`toRoute`、`adUnitId`（可被 SDK 回写）、`preloadScene` |
| `ScreenAdTrigger` | 同上 | `ENTER` / `RETURN` / `LEAVE` / `UNKNOW`，只入日志、不参与策略 |
| `ShowCallback` | `ad/callback/ShowCallback.kt` | 宿主回调：`canShowAd()`、`onShowCommitted()`、`onShowAttemptFailed()`、`showFailed(reason)`、`showSuccess()`、`onClicked()`、`onAdClosed()`、`onPaid()`、`onReward()` |
| `AdShowStatus` | `ad/status/` | `SHOW_SUCCESS` / `SHOW_FAIL` / `TIMEOUT` / `OTHER_AD_IS_SHOWING` / `LOAD_FAIL` |
| `ShowFailResult` | 同上 | 9 种失败原因（加载失败/超时、别的广告在播、Activity 结束、展示异常、配置变更…） |
| `AdConfig` | `ad/AdConfig.kt` | 全局开关、模式、频控、`adUnits: Map<String, AdUnitConfig>`、`autoRate` |
| `AdUnitConfig` | 同上 | `rate`（展示概率）、`max1H`、`max24H`、`interval`（秒）、`format`、`fromRoutes`/`toRoutes` |
| `FullscreenCandidate` | `ad/auction/FullscreenAdapter.kt` | 777/888 的候选快照：平台、格式、广告位、SDK 对象身份、加载时间、eCPM、`isValid()`、`claim()`、`show()` |

`ShowCallback` 里两个容易被忽略的钩子：

- `onShowCommitted()`：**即将调用平台 show** 时触发，host 可在这里把“已占用”落盘；
- `onShowAttemptFailed()`：已提交但**曝光前**失败，用于让宿主恢复状态后再换候选。

---

## 3. 一次全屏广告的完整链路

### 3.1 宿主调用 `AdActivity.showAd`（同步守卫）

```kotlin
// impl/AdActivity.kt:51
fun showAd(activity: Activity, adContext: ScreenAdContext): Boolean {
    Core.logAdTrigger(adContext)                  // ① 先记 ad_trigger
    if (!AdPolicy.canShowAd(adContext)) return false   // ② 11 项策略
    return tryShowAd(activity, adContext)         // ③ 启动广告页
}
```

`tryShowAd` 的三道闸：`activity.isFinishing/isDestroyed`、`launchPending` CAS（防并发 startActivity）、已有 `AdActivity` 实例（`currentActivity` 弱引用）。任一不过就 `false`。

**返回值语义**：`true` 只代表 `startActivity` 没抛异常，**不代表广告加载成功，也不代表曝光**。这是最多人误解的一点。

### 3.2 AdActivity 内部状态机

`PageAdRequestState` 把“页面资格”和“SDK 接管屏幕”分开：

| 方法 | 时机 | 语义 |
|---|---|---|
| `onResume()` | `onPostResume` | 页面可用 |
| `start()` | `onPostResume` | 首次且未 started 才发请求（重建不重发） |
| `canShow()` | 每次回调前 | `resumed && started && !completed && !committed` |
| `commit()` | `onShowCommitted` | 标记“已交给 SDK”，此后离页不取消 |
| `onPause()` | `onPause` | **未提交**且已 started → 返回 true（取消加载并 finish）；**已提交** → 返回 false（广告继续） |
| `resetCommitForRetry()` | `onShowAttemptFailed` | 允许换格式重试 |
| `takeCompletion()` | 预留 | 回调早于恢复时，等恢复后再导航 |

重建保护：`onSaveInstanceState` 存 `ad_request_started`，重建后 `onPostResume` 直接 `finish()`，**不重发请求**。

### 3.3 `Core.showAd` 的三级分派

```kotlin
// core/Core.kt:126
suspend fun showAd(activity, callback): AdShowStatus {
    if (autoRate.enable && format !in (BANNER, NATIVE) && selectAutoRateFormat(...) != null)
        return showAutoRateAd(callback, targets, impressionCounts) { showConfiguredAd(activity, it) }
    return when (AdConfig.showMod) {
        MODE_555 -> showConfiguredAd(...)
        MODE_666 -> { 把 OPEN→OPEN_INTER、INTER→INTER_VIDEO、VIDEO→VIDEO_INTER; showConfiguredAd(...) }
        MODE_777 -> AdShower.show777(...)
        MODE_888 -> AdShower.show888(...)
    }
}
```

`showConfiguredAd` 再按 `adFormat` 分派到 `AdShower.showOpen/showInter/showVideo/showOpenInter/showInterVideo/showVideoInter`。

> `BANNER` / `NATIVE` 若从这里进来会被直接 `showFailed(FAILED_TO_SHOW_CONTENT)`——它们必须走 `getBanner` / `getNative`。

### 3.4 平台 Shower：池 → 取最高价 → 展示 → 回补

以 AdMob 为例（其余平台同构）：

```text
showOpen(activity, callback)
 ├─ AdmobLoader.checkPool(OPEN)              // 池空则触发加载/补池
 ├─ openAd = bestPricedAd(openPool.keys)     // 池内按比较价选最高
 │    └─ 池空：loadOpenAd() 等待，超时/失败 → fail(LOAD_FAILED / LOAD_TIMEOUT)
 ├─ 校验 canShowAd()、AppStatus.isShowingAd
 ├─ callback.onShowCommitted()
 ├─ AppOpenAd.show(activity)                 // 真正的 SDK 展示
 ├─ 曝光 → showSuccess()（去重）
 ├─ 关闭 → onAdClosed() + fillPoolInBackground(scene_ad_consumed)
 └─ 失败 → onShowAttemptFailed / showFailed + fillPoolInBackground(scene_ad_show_failed)
```

关键点：

- **池内选优用 eCPM**，同价时按池的插入顺序（`bestPricedAd` 遍历 `keys`）；
- 池有**有效期**（`timeout`：开屏默认 12_600_000ms≈3.5h，插屏/激励 3_000_000ms=50min），过期实例不再参与；
- 展示成功/失败都会**后台补池**，补池用 `AtomicBoolean` 防重入（`fillPoolStarted`）；
- 组合格式（`OPEN_INTER` 等）是“主格式优先、失败换副格式”，不是真比价——真比价在 `MODE_666/777`。

### 3.5 “曝光”与“关闭”是两个信号

- `showSuccess()` **只在 SDK 曝光回调**触发（AdMob 走 `onAdImpression`），并去重；
- `onAdClosed()` 可能**先于**曝光到达（用户秒关），此时不会补记曝光；
- 收入**只**由 `OnPaidEventListener` 记 `ad_revenue` 并触发 `onPaid()`。AdMob 存在“有曝光但没有任何 paid 回调”的情况（SDK 注释明确写了），所以不要把收入字段塞进 `ad_impression`，否则会把 0 当成真实收入；
- 宿主拿到的 `AdShowStatus` 只反映“这次展示流程的结果”，奖励必须看 `onReward()`。

---

## 4. 策略与频控（`AdPolicy`）

`AdPolicy.canShowAd` 顺序执行 11 项，每项都记一条 `ad_policy`（`isOk` + `policyName`）：

| # | policyName | 数据来源 | 说明 |
|---|---|---|---|
| 1 | `global_switch` | `AdConfig.isOpen` | 总开关 |
| 2 | `global_interval` | 本地 `ad_impression` 最大时间 | 全局两次展示最短间隔（秒） |
| 3 | `global_max_1h` | 滚动 60 分钟曝光数 | `AdConfig.max1H` |
| 4 | `global_max_24h` | 滚动 24 小时曝光数 | `AdConfig.max24H` |
| 5 | `placement_config` | `adUnits[areaKey]` | 点位不存在直接拒 |
| 6 | `placement_max_1h` | 该点位 1h 曝光数 | `AdUnitConfig.max1H` |
| 7 | `placement_max_24h` | 该点位 24h 曝光数 | `AdUnitConfig.max24H` |
| 8 | `placement_interval` | 该点位上次曝光 | `interval` 秒 |
| 9 | `from_route` | `fromRoutes` / `"*"` | 路由必须完整匹配 |
| 10 | `to_route` | `toRoutes` / `"*"` | 同上 |
| 11 | `probability` | `Math.random() >= rate` | `rate=1.0` 恒过；`rate<=0` 恒拒 |

通过后**会回写 `adContext.adFormat = config.format`**——即以配置里的格式为准，调用方传的格式只是“请求意图”。

实现观察（排障时很有用）：

1. **频控读的是本地数据库**：`DBUtil.queryLogs` 每次分页 500 条，直到读完，再筛 `ad_impression`。展示前会**同步**（`runBlocking`）读一次，本地日志越多越慢。
2. **滚动窗口**按 `eventTimeMillis` 计算，依赖设备时钟；改系统时间可绕过。
3. `interval` 只在“上次有曝光”时生效，首次展示不受限。
4. `rate` 用 `Math.random()`，无记录、不可复现；只能从 `ad_policy` 的 `isOk=false` + `policyName=probability` 判断“被概率拒了”。

---

## 5. 四种 ShowMod 的真实差异

| 模式 | 实际行为 | 适用 |
|---|---|---|
| `MODE_555` | 按 `callback` 里的平台 + 配置里的格式单点展示 | 只用一家、点位格式固定 |
| `MODE_666` | 先把单格式升级为组合格式（OPEN→OPEN_INTER 等），再走该平台的组合展示（主格式优先、失败换副） | 单聚合内“开屏+插屏”互补 |
| `MODE_777` | `FullscreenAuction`：跨平台 × 跨格式收集候选 → 统一排序 → claim 最优 → 展示 → 失败回退 | 多聚合比价 |
| `MODE_888` | 777 + 用初选胜出的价格设底价，向 `explorePlatforms` 发一次独立请求；合格则优先展示探价广告 | 压价/探底 |

### 5.1 `MODE_777` 的实现细节（`FullscreenAuction`）

```text
1. platforms = [defaultPlatform] + activePlatforms（去重、固定顺序）
2. formats   = 请求格式展开 + OPEN/INTER/VIDEO 补齐去重
3. acquire：全局布尔锁 AppStatus.isShowingAd（非线程安全，靠 Main 单线程）
4. collect()：每个 (平台, 格式) 先看缓存；缺的发/复用加载，
              共用 showMaxTime 预算，10ms 轮询 + Channel 唤醒
5. 冻结候选：只接受 readyAt 在预算内、仍 valid 的实例
6. 排序：candidateOrder =
      已知正价优先 → 价格降序 → 格式顺序 → 平台顺序 → 入池顺序
7. 循环：取最优 → claim()（原子领取）→ onShowCommitted
        → 平台 show → 等 attempt.result
        → 失败且允许重试 → onShowAttemptFailed → 换下一个
        → 曝光/关闭/不可重试 → 结束
8. finally：只有“未提交或 SDK 已结束”才释放全局锁
```

几个容易踩的语义：

- **`claim()` 失败就跳过**（别的并发请求抢走了），不是整体失败；
- `noRetry` 在以下情况置位：`onDisplayStarted`、曝光、点击、奖励、关闭、`ACTIVITY_IS_FINISHING`、`OTHER_AD_IS_SHOWING`。也就是说**SDK 一旦开始展示就不能换广告**；
- `showFailed` 在**已曝光**时返回 `null`（成功），避免“曝光后失败”被当成没播；
- 超时后才就绪的广告**不参与本次**（用 `readyAtElapsedMs` 卡预算），但会入池给下次用。

### 5.2 `MODE_888` 的探价

```text
floor = max(P, P * priceMultiplier + priceOffset)     // P = 初选 eCPM（USD，decimal）
预算   = AdConfig.exploreTime（独立，<=0 跳过）
合格   = 同平台同格式同广告位、readyAt 在探价预算内、
         价格有效且 >= 实际下发底价、不与原候选同一实例
```

跳过原因会写进 `ad_explore` 的 `explore_outcome`：`disabled` / `invalid_price_or_floor` / `unsupported` / `empty_ad_unit` / `config_changed` / `initialization_failed` / `timeout` / `load_exception` / `unqualified` / `qualified`。

> 重要：**探价平台不要求在 `activePlatforms` 里**，但必须能初始化且配置了对应格式的广告位；当前只有 Unity 实现了 `supportsExploration`，而且**只支持 `INTER` / `VIDEO`**（`OPEN` 不支持）；AdMob/MAX/TopOn/TradPlus 的 `explore()` 默认返回 null，会落到 `unsupported` 回退。

### 5.3 代码注释里的“完善版方案”

`AdShower.kt` 顶部有 200+ 行注释，描述 777/888 的目标语义（预算、排序、回退、埋点隔离）。**这些规则大部分已经在 `FullscreenAuction` 落地**（收集预算、稳定排序、claim、失败回退、曝光去重），但注释里仍有“建议”成分（例如“未知价排序”最终实现为“未知价排在已知价之后”，不是另建一套）。看代码为准。

---

## 6. `autoRate`：按格式配比

```kotlin
// core/ad/policy/AutoRateSelection.kt
candidates = targets 里 enable 的 OPEN/INTER/VIDEO（目标值合法）
first      = 配置顺序里的第一个
actual     = 该格式历史曝光数 / 候选总曝光数
target     = 目标值 / 目标值之和
选中       = 第一个 actual 明显低于 target 的格式（容差 1e-12）
没有可用目标 → null（回落到原 format 逻辑）
```

- 计数来自本地 `ad_impression`（跨点位统计）；
- `targets` 顺序决定平手时的优先级，所以 `AdConfig` 落到本地 Preference 时**额外存了 `targetOrder`**（JSONObject 不保证键序）；
- 开启后 `Core.showAd` 会忽略原请求格式，按选出的格式重试；每次失败会把该格式加入 `excluded` 再选，**每种格式最多试一次**；
- Banner/Native 不参与。

---

## 7. 价格与比价（以 AdMob 为例）

### 7.1 统一口径

所有平台的价格最终归一到 **USD eCPM 微单位 `Long`**（1 USD = 1_000_000）。缺价、非 USD、非正数、溢出、异常一律**视为未知**，不是 0。

价格在 `onAdLoaded` 时**一次性快照并冻结**（存进两个 `WeakHashMap`，不延长广告实例生命周期），展示时不再读 SDK，也不再读当前配置：

```kotlin
val price = AdmobReflectProbe.read(openAd)              // 反射价
openAd.reflectPrice = price
openAd.adapterProbeResult = AdmobAdapterProxyReader.read(openAd.responseInfo, probeConfig)   // 探针价 + 冻结的 probe 配置
```

所以：**改了 `probeConfig.mod` 不会重新给已入池的广告定价**，要等下一批加载。

### 7.2 四种取价模式

```kotlin
// AdmobComparisonPrice.resolveComparisonPriceEcpmMicros
REFLECT   → 反射价（要求 currencyCode == "USD"，valueMicros ∈ 1..Long.MAX/1000）
ADAPTER_H → 探针上界（要求 currency == "USD" 且 h > 0）
ADAPTER_M → l + (h - l) / 2（向下取整；h、l 缺一为 null）
ADAPTER_L → 探针下界
```

两个前提条件容易踩：

1. **`comparisonPriceEcpmMicros()` 先取“加载时的探针结果”，没有结果就直接返回 null**（`AdmobPriceExtensions.kt:70`）——不是“有反射价就够”。没走 `AdmobLoader` 加载的广告（没有快照）价格恒为未知。
2. **`REFLECT` 的反射链硬绑定 `play-services-ads 25.3.0`**（`AdmobReflectProbe.SDK_VERSION`）。宿主一旦把 Google Mobile Ads 升到别的版本、或混淆导致类名变化，反射返回 null → AdMob 全部广告“无价” → 比价时永远垫底。这是接入方最该防的一条。

探针价的来源是 `AdmobAdapterProxyReader`：按 `responseInfo.adapterResponses` 的顺序校验瀑布层级，用自定义 event（`AdmobProxyAdapter`，固定返回 `10001` no-fill）证明“某一层真的执行过”，从而推出 H；胜出层之后的层给出 L。任何一层顺序/价格/延迟不符，就会落到 `ORDER_UNVERIFIED`、`CONFIG_MISMATCH` 等 13 种状态之一，价格视为未知。

### 7.3 池内选优

```kotlin
// AdmobShower.kt:70
private fun <T : Any> bestPricedAd(ads: Collection<T>): T? = ads.maxByOrNull {
    comparisonPriceEcpmMicros(it) ?: Long.MIN_VALUE     // 未知价 = 最低
}
```

- 未知价按 `Long.MIN_VALUE` 参与比较，也就是**排在所有已知价之后**；
- 同价时 `maxByOrNull` 取**先遍历到的**（`mutableMapOf` 的插入顺序即入池顺序）；
- 组合格式的两选一用的是 `higherPricedAd`：**缺价或同价时选“主格式”**（`OPEN_INTER`→OPEN、`INTER_VIDEO`→INTER、`VIDEO_INTER`→VIDEO）；
- 跨平台比较在 777 里由 `candidateOrder` 完成（已知正价优先 → 价格降序 → 格式 → 平台 → 入池序）。

### 7.4 收入

`onPaid` 走各平台收益回调，转成 `ad_revenue`（金额 + 币种 + 精度），AdMob 还会做 `SingularAdData` 归因。**展示前价格 ≠ 真实收入**，报表里要分开看。AdMob 存在“有曝光、无 paid 回调”的情况（SDK 注释明确写了），所以收入字段只能挂在 `ad_revenue` 上。

---

## 8. 广告池与预加载

| 平台 | 池 | 容量 | 有效期（默认） |
|---|---|---|---|
| AdMob | openPool / interPool / videoPool | `poolSize`（默认 1） | 开屏 3.5h、插屏/激励 50min |
| MAX | 同类池 | 配置 | 配置 |
| TopOn / TradPlus / Unity | 同类池 | 配置 | 配置 |

- 预加载入口：`AdLoader.fillAd(preloadScene)`，填 `defaultPlatform` + `activePlatforms`；
- **`explorePlatforms` 不在其中**，888 探价时临时 `ensureInitialized`，初始化耗时算在探价预算里；
- 首次进入前台、从后台回前台、广告消耗、展示失败、放弃展示、库存失效都会触发补池，场景名写进 `preload_scene`；
- `AppForegroundPreloader` 保证同一时刻只有一个预加载 Job，但“前台次数”照常累加；
- **池的有效期用墙上时钟**（`mutableMapOf<Ad, Long>`），777 的“就绪时间”另用单调时钟 `WeakHashMap` 记录——改系统时间会影响池过期判断，但不影响 777 的预算判定；
- 两条实现细节：`fill_pool` 事件的 `scene` 被硬编码成 `scene_open_app`（忽略入参）；`admob_config.probeConfig.timeout` 只做序列化往返，**代码里从未读取**。`checkPool` 的清池条件是“广告位 ID 变了”或“超过 TTL”，配置热更新会立刻清掉旧广告位的缓存并在同一预算内重载。

---

## 9. UMP 与广告的关系

- `Core.beginConsentFlow(scene)` → `ConsentFlow`；`initConsent` / `showSplashConsent` / `endConsentFlow` 串成一条漏斗，9 个 `ump_*` 事件自动打点；
- **广告展示不检查 `Core.canRequestAds()`**：`AdActivity.showAd` 里只有策略检查，没有同意检查；
- 要“未同意不出广告”，只能宿主在调用前自己判断，或改 SDK。

---

## 10. 打点体系

| 事件 | 触发点 | 关键字段 |
|---|---|---|
| `ad_trigger` | `AdActivity.showAd` 第一行 | request_id、areakey、format、platform、trigger、from/to_route、traffic_source |
| `ad_policy` | 每一项策略检查 | 同上 + `isOk`、`policyName` |
| `ad_occur` | 777 每次提交展示尝试 | + `attempt`、`duration_time` |
| `ad_start_loading` / `ad_load_success` / `ad_load_fail` | 平台加载 | + `ad_preload`、`preload_scene`、失败原因/错误码 |
| `ad_impression` | 真实曝光 | 用于频控与 autoRate 统计 |
| `ad_click` / `ad_close` | 点击/关闭 | |
| `ad_show_fail` / `ad_show_timeout` | 展示失败/超时 | + `failure_reason`、`show_fail_reason` |
| `ad_revenue` | 收益回调 | 金额、币种、精度 |
| `ad_explore` | 888 探价 | 基准价、底价、探价结果、耗时 |
| `fill_pool` | 补池 | 场景、池状态 |

渠道白名单：`log_config` 的 `fbEvents` / `thEvents` / `localEvents`（默认 `["*"]`）与 `netEvents`（默认只有 4 项）。**频控只认本地 `ad_impression`**，所以 `localEvents` 不能关。

---

## 11. 配置体系

```text
res/raw/ad_config.json ──┐
                         ├─ AdConfig.init()
SharedPreferences ───────┘   （loadConfigFromPreference 覆盖）

RemoteConfig/服务端 ──► RiskUtil.applyJson("ad_config", …) ──► AdConfig.applyConfig(JSONObject)
                                                             ├─ 标量：缺省保留当前值
                                                             ├─ 集合：存在即整体替换
                                                             └─ saveAdConfig() 落盘
```

- 点位键 = `areaKey`，**大小写敏感**；
- `AdConfig.adUnits` 是“整表替换”语义，远程只下发一个点位会把其他点位清空；
- 多平台广告位是独立配置对象（`AdmobConfig`、`MaxConfig`、`ToponConfig`、`TradplusConfig`、`UnityConfig`），各自 `applyConfig` + `onConfigChanged()`；
- `AdmobConfig.applyConfig` 先全部解析再替换，避免“一半新一半旧”——比 `AdConfig` 稳，`AdConfig` 是逐字段赋值。

---

## 12. 宿主接入的四种方式

| 方式 | 入口 | 适用 | 注意 |
|---|---|---|---|
| 全屏（推荐） | `AdActivity.showAd(activity, ctx)` | 开屏/插屏/激励 | 返回值≠曝光；一个进程只有一个广告页 |
| 直接展示 | `Core.showAd(activity, callback)` | 自定义 Activity 内 | 自己处理 `AppStatus`/生命周期 |
| 页面级 | `impl` 的 `contentComposable(route)` + `rememberNavigateWithAd()` | Compose 导航页自动 `{route}_start/back/leave` | **areaKey 由路由拼出来**，`ad_config` 必须配同名点位，否则 `placement_config` 拒 |
| 内嵌 | `getBanner` / `getNative` | 页面内 | 必须自己挂载/释放；不能走 `AdActivity` |

页面级那条最容易被忽略：`BaseScreen` 用 `screenName = destination.route`，自动请求 `${screenName}_start`、`${screenName}_back`、`${screenName}_leave`。想用历史点位（如 `enterHomeFromAppIntroAdv`）就不能套 `BaseScreen`，得自己调 `AdActivity.showAd`。

---

## 13. 设计观察与风险清单

| # | 观察 | 影响 | 建议 |
|---|---|---|---|
| 1 | `AdPolicy` 在主线程 `runBlocking` 读本地日志（分页 500） | 冷启动首次展示可能卡顿；日志多时更明显 | 启动后异步预热；或接受首次延迟 |
| 2 | 频控依赖本地 `ad_impression` | 清数据/换设备后频控归零；`localEvents` 关掉即失效 | 不要关闭本地日志渠道 |
| 3 | `Math.random()` 判概率 | 不可复现、无法回放 | 排障只看 `ad_policy` 的 `probability` |
| 4 | `AdActivity.showAd` 返回 true ≠ 曝光 | 宿主误判“已播”而跳转 | 用 `ON_PAUSE→ON_RESUME` 或 `showSuccess` 判断 |
| 5 | 进程内单广告页（`AtomicReference`） | 并发请求第二个直接 false | 宿主侧自己去重，别靠 SDK 兜 |
| 6 | `MODE_555` 下组合格式是“主→副”而非比价 | 误以为会挑高价 | 要真比价用 666/777 |
| 7 | `AdConfig.applyConfig` 集合整体替换 | 远程只下发部分点位会清空整表 | 远程必须给完整 `ad_units` |
| 8 | `explorePlatforms` 不参与预加载 | 888 首次探价要临时初始化，可能超预算 | 需要探价就预热该平台 |
| 9 | 只有 Unity 支持探价 | 888 配 AdMob/MAX 会 `unsupported` 回退 | 配 `explorePlatforms=UNITY` 并给激励/插屏位 |
| 10 | 777 的 `claim()` 失败静默跳过 | 并发下可能“看起来没播” | 看 `ad_occur` 与 `ad_show_fail` |
| 11 | `BANNER/NATIVE` 走 `Core.showAd` 必失败 | 常见误用 | 用 `getBanner`/`getNative` |
| 12 | `showMinTime` 是“请求到展示的最小等待” | 配太大会显得“点了没反应” | 默认 500ms 即可 |
| 13 | 奖励只认 `onReward` | 关闭/成功都不代表发奖 | 激励场景必须等 `onReward` |
| 14 | `ScreenAdSession` 的 areaKey 由路由拼 | 配置漏点位 → 全被拒 | 要么补齐点位，要么不用 `BaseScreen` |
| 15 | `AdActivity` 重建后直接 finish | 旋转屏幕会丢掉本次广告 | 宿主固定方向或接受 |
| 16 | 池有效期用墙上时钟 | 用户改时间可让过期广告“复活”或提前失效 | 接受；不要靠池过期做风控 |
| 17 | 777 的候选快照冻结后不再补池 | 回退到后面可能“无候选” | 一次请求只保证初始集合内回退 |
| 18 | **`REFLECT` 反射链绑定 `play-services-ads 25.3.0`** | 宿主升级 AdMob 版本 → 所有 AdMob 广告“无价” → 比价永远垫底、`bestPricedAd` 退化成“取池里第一条” | 锁版本；升级前验证 `ad_load_success` 里有没有 `ad_price_micros` |
| 19 | 组合格式预热时 `checkPool(callback.adContext.adFormat)` 传的是**宿主原始格式**，而 `checkPool` 把非 OPEN/VIDEO 全归到 INTER 分支 | `OPEN_INTER` 展示前只清了 INTER 池，OPEN 的过期广告要等后面才清 | 排障时不要假设“池都清过了” |
| 20 | `showOpen` 先取广告、再等 `showMinTime`，等待期间广告可能被 `checkPool` 驱逐（配置变更/TTL） | 可能展示已失效实例或直接失败 | 组合格式在等待后会重新取广告，单格式不会 |
| 21 | 组合格式两条加载分支共写一个 `loadFailure` 变量 | 失败原因可能记成“兄弟格式”的原因 | 看 `ad_occur`/`ad_show_fail` 的 format 字段对不上时优先怀疑这里 |
| 22 | 已提交广告若始终收不到 dismiss/failure（进程被杀、SDK 异常） | `AppStatus.isShowingAd` 常驻 true → 之后所有全屏都返回 `OTHER_AD_IS_SHOWING` | 进程重启自愈；不要在同一进程里指望它恢复 |
| 23 | 老路径允许 `showSuccess` 之后再 `showFailed`（曝光回调不置 `finished`） | 同一次展示可能出现“成功 + 失败”两条记录 | 聚合用 `ad_revenue` 判定收益，不看 `ShowFailResult` |
| 24 | 老路径（555/666）**不等待 `MobileAds.initialize`**，只有 777 会 `ensureInitialized` 轮询 | 冷启动立刻请求开屏时可能撞上初始化未完成，加载变慢 | 启动顺序留出初始化时间 |

---

## 14. 排障手册

**看什么日志**

```powershell
adb logcat -s AdActivity:V AdPolicy:V AdmobShower:V AdmobLoader:V FullscreenAuction:V
```

**一次“没出广告”的判定顺序**

| 现象 | 先看 | 结论 |
|---|---|---|
| 完全没有 `ad_trigger` | 宿主是否调了 `AdActivity.showAd` | 调用没发生 |
| 有 `ad_trigger`，无 `ad_occur` | `ad_policy` 里哪项 `isOk=false` | 策略拒（最常见：`placement_config` 点位不存在、`global_interval`、`probability`） |
| 有 `ad_occur`，无 `ad_impression` | `ad_show_fail` / `ad_show_timeout` 的 `failure_reason` | 加载超时、平台无填充、`canShowAd()=false`（宿主 Activity 已 pause/finish） |
| 有 `ad_impression` 但收益为 0 | `ad_revenue` 是否到达 | 平台未回传收益，或币种/精度异常 |
| 888 没走探价 | `ad_explore.explore_outcome` | `disabled`（exploreTime<=0）/`unsupported`/`unqualified` |

**常见 `ShowFailResult` 对照**

| 值 | 含义 | 排查 |
|---|---|---|
| `LOAD_FAILED` | 加载明确失败 | 平台后台是否有填充、广告位 ID 是否正确 |
| `LOAD_TIMEOUT` | 超过 `showMaxTime` 仍无可用 | 网络、平台初始化、池是否为空 |
| `OTHER_AD_IS_SHOWING` | 全局锁被占 | 上一个广告页没退干净 |
| `OTHER_AD_OCCUR` | 宿主 `canShowAd()` 返回 false | Activity 不是 RESUMED 或宿主主动拒 |
| `ACTIVITY_IS_FINISHING` | 宿主已结束 | 生命周期竞态 |
| `FAILED_TO_SHOW_CONTENT` | SDK 展示失败 | 平台内部错误，看平台日志 |
| `AD_CONFIG_CHANGED` | 请求期间广告位变了 | 配置热更新导致，重试即可 |

---

## 15. 结论

- 这套 SDK 的**核心资产是 `AdPolicy` + 池 + 比价**，`AdActivity` 只是“可靠的展示壳”。
- 理解顺序建议：`ScreenAdContext` → `AdPolicy` → `AdmobLoader`（池）→ `AdmobShower`（取价+展示）→ `FullscreenAuction`（777/888）。
- 最容易出错的四件事：**点位 key 不一致**、**用返回值当曝光**、**远程只下发部分点位**、**Banner/Native 走错入口**。
