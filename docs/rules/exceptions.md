# 规则例外登记

本文件登记与 `AGENTS.md` 字面规则不一致、但已评审接受的做法。新增例外必须写清范围、原因和退出条件；没有登记的不一致默认按“代码是错的”处理。

## E1：domain 允许使用 `kotlinx.coroutines.flow`

- 范围：`domain/**/port/*.kt` 的 `Flow` 类型。
- 原因：扫描、网络扫描、电池历史需要跨页面共享的响应式数据；把 Flow 上移成 ViewModel 轮询会增加页面生命周期复杂度，且 `data` 实现天然需要协程。
- 约束：只允许 `Flow` 类型与 `suspend`；禁止 `StateFlow`、`Dispatchers`、`CoroutineScope`、`androidx.*`。
- 退出条件：domain 端口改为一次性读取 + 页面自行刷新，或项目引入独立的 `base` 状态存储。

## E2：`VirusAndroidEffects.kt` 聚合多个顶层公开类型

- 范围：`app/view/antivirus/ui/effect/VirusAndroidEffects.kt`。
- 原因：effect 文件同时承载 Composable 宿主与 `PackageRemovedReceiver`，拆分后 receiver 仍在 `ui/effect` 目录，会与 `effectLocation` 命名规则冲突。
- 约束：该文件只放 Antivirus 页的平台副作用，不放业务模型或跨页组件。
- 退出条件：Antivirus effect 拆成独立文件和独立平台组件目录时删除本例外。

## E3：ktlint 关闭 `standard:filename`

- 范围：`app/src/**/*.kt`。
- 原因：项目规则允许多个顶层类型聚合在 `XxxContract.kt`、`XxxModels.kt`、`XxxReducer.kt` 等文件；ktlint 的 filename 规则不识别这些例外，会对合法文件报错。
- 约束：文件名与顶层类型的一致性仍由 `check-rules.ps1` 的 `publicTypes` 检查；新增聚合文件必须在 `AGENTS.md` 的例外清单登记。
- 退出条件：ktlint 支持按文件/正则豁免，或聚合文件拆分为单类型文件。
