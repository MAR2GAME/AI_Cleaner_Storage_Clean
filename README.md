# Storage Clean

Android 单 Activity + Compose + MVI + Koin + 类型化导航的存储清理应用。

## 模块

- `:app`：应用、页面、领域与数据实现。
- `:junkcc`：独立资源模块，不参与业务代码。

## 包结构

```text
com.aicleanerstorage.spaceclean/
├─ app/       Application、MainActivity、root/、di/、background/、view/<page>/
├─ base/      designsystem/、mvi/、navigation/、permission/、util/
├─ data/      repository/ 端口实现、local/ 平台适配
├─ domain/    model/<concept>/、port/、rule/、error/
└─ app/view/<page>/{interaction,ui}
```

依赖方向：

```text
app/view ──> base + domain
app/root ──> app/view + base + domain
app/di   ──> data + domain + base
data     ──> domain + base
domain   ──> Kotlin/JDK（coroutines 例外）
```

硬规则、命名、代码风格与例外登记以 [AGENTS.md](AGENTS.md) 为准；页面数据流与权限链路见 [docs/mvi-architecture.md](docs/mvi-architecture.md)。

## 关键实现

- `domain/rule/`：文件分类、重复文件、相似照片、WhatsApp、垃圾扫描等纯策略。
- `domain/error/`：`JunkError`、`VirusScanError`。
- `domain/port/`：仓库、扫描器、权限控制等业务契约。
- `data/local/`：MediaStore、文件系统、SharedPreferences、SDK 适配。
- `data/repository/`：`Default<概念>Repository`，只做组合和映射。
- `base/permission/`：`AppPermission`、`PermissionGate`、`PermissionRequestEffect`。
- `app/view/common/files/`：文件管理共享页面、Action、Effect 与组件。

## 常用命令

```powershell
.\scripts\check-rules.ps1
.\gradlew.bat :app:compileDebugKotlin
.\gradlew.bat :app:testDebugUnitTest
.\scripts\ci.ps1
```

首次克隆执行一次 `.\scripts\install-hooks.ps1` 启用 `pre-commit`。
