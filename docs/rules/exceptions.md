# 瑙勫垯渚嬪鐧昏

鏈枃浠剁櫥璁颁笌 `AGENTS.md` 瀛楅潰瑙勫垯涓嶄竴鑷淬€佷絾宸茶瘎瀹℃帴鍙楃殑鍋氭硶銆傛柊澧炰緥澶栧繀椤诲啓娓呰寖鍥淬€佸師鍥犲拰閫€鍑烘潯浠讹紱娌℃湁鐧昏鐨勪笉涓€鑷撮粯璁ゆ寜鈥滀唬鐮佹槸閿欑殑鈥濆鐞嗐€?
## E1：domain 允许使用 `kotlinx.coroutines.flow`

- 范围：`domain/**/port/*.kt` 的 `Flow` 类型。
- 原因：扫描、网络扫描、电池历史需要跨页面共享的可观察数据；把 Flow 上移到 ViewModel 轮询会增加页面生命周期复杂度，且 `data` 实现天然需要协程。
- 约束：只允许 flow 类型与 `suspend`；禁止 `StateFlow`、`Dispatchers`、`CoroutineScope`、`androidx.*`。
- 退出条件：domain 端口改为一次性读取 + 页面自行刷新，或项目引入独立的 `base` 状态存储。
## E2锛歚VirusAndroidEffects.kt` 鑱氬悎澶氫釜椤跺眰鍏紑绫诲瀷

- 鑼冨洿锛歚app/view/antivirus/ui/effect/VirusAndroidEffects.kt`銆?- 鍘熷洜锛歟ffect 鏂囦欢鍚屾椂鎵胯浇 Composable 瀹夸富涓?`PackageRemovedReceiver`锛屾媶鍒嗗悗 receiver 浠嶅湪 `ui/effect` 鐩綍锛屼細涓?`effectLocation` 鍛藉悕瑙勫垯鍐茬獊銆?- 绾︽潫锛氳鏂囦欢鍙斁 Antivirus 椤电殑骞冲彴鍓綔鐢紝涓嶆斁涓氬姟妯″瀷鎴栬法椤电粍浠躲€?- 閫€鍑烘潯浠讹細Antivirus effect 鎷嗘垚鐙珛鏂囦欢鍜岀嫭绔嬪钩鍙扮粍浠剁洰褰曟椂鍒犻櫎鏈緥澶栥€?
