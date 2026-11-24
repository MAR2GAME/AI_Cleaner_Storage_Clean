# testres：病毒功能的假病毒测试物料

这个目录只放**看起来像病毒、实际完全无害**的测试物料，用来测 Storage Clean 的杀毒流程（扫描动画、结果页、威胁列表、删除/打开设置等）。

> 这里没有任何真实的恶意代码：两个演示应用只声明权限、显示一段说明文字，不读取短信/通讯录、不联网上传、没有后台服务。

## 目录结构

```text
testres/
├─ build.ps1                 # 构建 / 安装 / 推送 / 卸载
├─ demo-apps/                # 两个独立的最小 Android 工程（Java，无第三方依赖）
│  ├─ pua-app/               # “假病毒”：声明一堆高风险权限，但什么也不做
│  └─ safe-app/              # 对照组：不声明高风险权限
└─ fake-virus-files/         # build.ps1 生成的两个 APK（被 .gitignore 忽略）
```

`demo-apps` 是独立 Gradle 工程，不会被根工程 `settings.gradle.kts` 包含，也不影响 `:app` 构建。

## 快速开始

```powershell
# 构建两个 APK 到 testres/fake-virus-files
.\testres\build.ps1

# 执行策略受限时用 .cmd 包装
.\testres\build.cmd

# 构建 + 安装到当前 adb 设备
.\testres\build.ps1 -Install

# 构建 + 安装 + 推一份 APK 到 /sdcard/Download（测深度扫描对存储文件的处理）
.\testres\build.ps1 -Install -PushToDevice

# 指定设备
.\testres\build.ps1 -Install -Serial RZCY128JJLV

# 卸载
.\testres\build.ps1 -Uninstall
```

然后：打开 Storage Clean → 首页 Virus 卡片 → Clean Now → Quick Scan / Deep Scan。

## 两个 APK 是什么

| APK | 包名 | 说明 |
|---|---|---|
| `Android.PUA.Harmless.Test.apk` | `com.aicleanerstorage.testres.pua` | 声明短信、通讯录、通话记录、定位、相机、悬浮窗、安装应用等高风险权限，用来“钓”云端风险评分；代码只有一个说明页 |
| `Android.Safe.Control.apk` | `com.aicleanerstorage.testres.clean` | 不声明任何高风险权限，作为“不应误报”的对照组 |

两者都使用 debug 签名，`adb uninstall` 即可完全清除。

## 实测结论（很重要）

这台设备（Android 12，app 1.0.7）上验证过：

1. **自建 APK 不会被 Trustlook 云端判成病毒。**
   安装 PUA 假应用后做快速扫描和深度扫描，威胁列表里都没有它；扫描只报了两项：
   - `USB debugging enabled`（ADB 风险，本地判断，可稳定复现）
   - 一个系统里已有的 `System Service` 应用（云端判定为 High Risk，与本目录无关）
2. **深度扫描确实会枚举 `/sdcard` 下的 `.apk` / `.xapk`**，但同样要云端判黑才会出现在结果里；把假 APK 推到 Download 也不会变成威胁。
3. **Trustlook API key 绑定主应用包名。** 用同一个 key 在另一个包名的探针应用里调用 `CloudScanClient` 会直接返回 403（`API key verification failed with 4xx response: 403`），所以没法用独立小工具去查“我这个包多少分”。
4. 目前**不需要真病毒也能稳定触发威胁结果页**的路径只有：打开 USB 调试（设置里开启开发者选项），扫描后会出一条 Medium Risk 的 `USB debugging enabled`。

结论：`testres` 里的假应用适合测**扫描过程、应用列表展示、“没发现病毒”的结果页、ADB 风险卡片**，但不能用来测“发现多个病毒”的结果页。

## 关于 EICAR 文件

目录里可能有 `eicar.txt` / `eicar.com`，内容是行业标准的 EICAR 反病毒测试字符串（68 字节，**无害**，所有杀毒软件都拿它做自检）。注意：

- Windows Defender 可能直接把它当病毒隔离，文件会消失，属正常现象。
- 当前杀毒流程走的是 Trustlook 的 Quick/Deep Scan，只会枚举已安装应用和 `/sdcard` 下的 `.apk` / `.xapk`，**不会**扫普通 `.txt`；SDK 虽然有 `startEicarScan`，但 app 没有接入，所以 EICAR 不会出现在威胁列表里。

## 稳定测试“发现威胁”的结果页（仅 debug）

已内置一个只在 debug 构建生效的假扫描器，fixture 在 `testres/fake-virus-fixtures/fake_virus_scan.json`：

```powershell
.\gradlew.bat :app:assembleDebug -PfakeVirusScan=true
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

- Quick Scan 回放 `apps`；Deep Scan 额外回放 `files`（文件威胁）
- 不联网、不读设备，只用来测威胁列表、Solve（跳应用设置/删除文件）和结果页跳转
- **release 构建不包含这段代码，也不会打包 fixture**
- 不加 `-PfakeVirusScan=true` 时，debug 构建仍走真实 Trustlook 云扫描

测完恢复正常扫描：不带参数重新构建并安装即可。

```powershell
.\gradlew.bat :app:assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 清理

```powershell
.\testres\build.ps1 -Uninstall
adb shell rm /sdcard/Download/Android.PUA.Harmless.Test.apk
adb shell rm /sdcard/Download/Fake.Trojan.Test.apk
adb shell rm /sdcard/Download/Android.Safe.Control.apk
```

## 注意

- `fake-virus-files/*.apk` 被 `.gitignore` 的 `*.apk` 规则忽略，不会进版本库；需要时用 `build.ps1` 重新生成。
- 不要把这些 APK 上传到应用商店、网盘或发给普通用户；它们只是测试物料。
- 如果系统自带的安全软件扫描本目录并弹出“发现风险”，属于正常现象（因为它们声明了高风险权限），忽略即可。
