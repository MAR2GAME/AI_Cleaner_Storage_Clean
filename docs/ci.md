# CI

## 跑什么

`.github/workflows/ci.yml`（push / pull_request）依次执行：

| 步骤 | 命令 | 作用 |
|---|---|---|
| MVI 规则 | `pwsh -NoProfile -File scripts/check-rules.ps1` | domain 纯净、分层方向、UiText、CollectEffects、BackHandler、包名、语言包 key |
| 单测 | `./gradlew :app:testDebugUnitTest` | JVM 单测（当前 25 suites / 78 tests） |
| 构建 | `./gradlew :app:assembleDebug` | 验证资源和 Kotlin 能编译出 debug APK |

失败时上传 `app/build/reports/tests/testDebugUnitTest` 和 `app/build/test-results`。

## 本地对齐

Windows：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts\ci.ps1
```

它按顺序跑门禁、单测、`assembleDebug`，任一失败即中断。

## 说明

- runner 用 `ubuntu-latest` + JDK 17 + `android-actions/setup-android`；`compileSdk 36.1` 由 AGP 自动下载，licenses 由 setup-android 接受。
- 自托管 runner 若已装 SDK，可去掉 `Set up Android SDK` 一步，保留 `ANDROID_HOME` 即可。
- debug 构建不需要签名和 `local.properties`；`local.properties` 被 `.gitignore` 排除，CI 通过 `ANDROID_HOME` 定位 SDK。
- 仓库主 remote 是 GitHub（`origin`），另有 `gitee` remote。Gitee Go/Jenkins 只需按上表三步调用同一批命令，无需改脚本。
- `ktlintMainSourceSetCheck` 暂不接入：仓库当前仍有历史违规，等清理后再加第三步。
