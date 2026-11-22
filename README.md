# Storage Clean

Android 鍗?Activity + Compose + MVI + Koin + 绫诲瀷鍖栧鑸殑瀛樺偍娓呯悊搴旂敤銆?
## 妯″潡

- `:app`锛氬簲鐢ㄣ€侀〉闈€侀鍩熶笌鏁版嵁瀹炵幇銆?- `:junkcc`锛氱嫭绔嬭祫婧愭ā鍧楋紝涓嶅弬涓庝笟鍔′唬鐮併€?
## 鍖呯粨鏋?
```text
com.aicleanerstorage.spaceclean/
鈹溾攢 app/       Application銆丮ainActivity銆乺oot/銆乨i/銆乥ackground/銆乿iew/<page>/
鈹溾攢 base/      designsystem/銆乵vi/銆乶avigation/銆乸ermission/銆乽til/
鈹溾攢 data/      repository/ 绔彛瀹炵幇銆乴ocal/ 骞冲彴閫傞厤
鈹溾攢 domain/    model/<concept>/銆乸ort/銆乺ule/銆乪rror/
鈹斺攢 app/view/<page>/{interaction,ui}
```

渚濊禆鏂瑰悜锛?
```text
app/view 鈹€鈹€> base + domain
app/root 鈹€鈹€> app/view + base + domain
app/di   鈹€鈹€> data + domain + base
data     鈹€鈹€> domain + base
domain   鈹€鈹€> Kotlin/JDK锛坈oroutines 渚嬪锛?```

纭鍒欍€佸懡鍚嶃€佷唬鐮侀鏍间笌渚嬪鐧昏浠?[AGENTS.md](AGENTS.md) 涓哄噯锛涢〉闈㈡暟鎹祦涓庢潈闄愰摼璺 [README.md](README.md)銆?
## 鍏抽敭瀹炵幇

- `domain/rule/`锛氭枃浠跺垎绫汇€侀噸澶嶆枃浠躲€佺浉浼肩収鐗囥€乄hatsApp銆佸瀮鍦炬壂鎻忕瓑绾瓥鐣ャ€?- `domain/error/`锛歚JunkError`銆乣VirusScanError`銆?- `domain/port/`锛氫粨搴撱€佹壂鎻忓櫒銆佹潈闄愭帶鍒剁瓑涓氬姟濂戠害銆?- `data/local/`锛歁ediaStore銆佹枃浠剁郴缁熴€丼haredPreferences銆丼DK 閫傞厤銆?- `data/repository/`锛歚Default<姒傚康>Repository`锛屽彧鍋氱粍鍚堝拰鏄犲皠銆?- `base/permission/`锛歚AppPermission`銆乣PermissionGate`銆乣PermissionRequestEffect`銆?- `app/view/common/files/`锛氭枃浠剁鐞嗗叡浜〉闈€丄ction銆丒ffect 涓庣粍浠躲€?
## 甯哥敤鍛戒护

```powershell
.\scripts\check-rules.ps1
.\gradlew.bat :app:compileDebugKotlin
.\gradlew.bat :app:testDebugUnitTest
.\scripts\ci.ps1
```

棣栨鍏嬮殕鎵ц涓€娆?`.\scripts\install-hooks.ps1` 鍚敤 `pre-commit`銆?

