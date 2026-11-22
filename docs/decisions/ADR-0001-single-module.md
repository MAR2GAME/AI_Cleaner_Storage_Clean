# ADR-0001：继续使用单 `:app` 模块

- 状态：已接受
- 日期：2026-11-23
- 相关：`AGENTS.md`、`docs/architecture.md`

## 背景

仓库当前约 3.8 万行业务代码、25 个页面、36 个目的地，采用 layer-first（`domain / data / base / app`）包分层，只有 `:app` 与 `:junkcc` 两个 Gradle 模块。

历史上曾尝试 `core/*` + `feature/*` 多模块拆分，但已回退；`scripts/` 中仍留有面向旧模块树的失效脚本，本次整改已删除。

## 决策

继续使用单 `:app` 模块，不立即拆分 `feature/*` 或 `core/*`。

理由：

1. 当前规模仍在单模块可维护范围内，拆模块的构建/维护成本高于收益。
2. 页面已经按 `app/view/<page>` 隔离，共享文件管理放在 `app/view/common/files`，包边界足以支撑当前协作。
3. 过早拆模块会重复历史上的半成品结构，增加 Koin/Compose/资源装配复杂度。

## 后果

- 构建简单，IDE 索引与重构成本低。
- `app/view` 体积较大，`FileManagerNavGraph` 仍偏长，需要继续按导航图拆分。
- 包边界必须由 `check-rules.ps1` 与 `DomainPurityTest`/`MotherTemplateBoundaryTest` 守住，不能依赖 Gradle 模块边界。

## 重新评估的触发条件

满足任一条件时重新评估：

- `app/src/main` 超过 45k 行，或页面超过 30 个。
- 增量编译长期超过 2 分钟。
- `AppNavHost`/`FileManagerNavGraph` 或 `app/view/common/files` 成为长期合并冲突热点。

若触发，优先拆 `:core:designsystem`、`:core:mvi`、`:core:navigation`、`:core:permission`，暂不拆 `feature/*`；同时重写模块边界检查，替代已删除的旧脚本。

## 备注

`:junkcc` 是独立资源模块，不属于本决策的业务模块范围。
