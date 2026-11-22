# 椤圭洰瑙勫垯

鏈枃浠舵槸纭鍒欍€佷紭鍏堢骇涓庝唬鐮侀鏍肩殑鍞竴鍏ュ彛銆俁EADME / `docs/mvi-architecture.md` 鍙繚鐣欎骇鍝佽鏄庛€佹暟鎹祦缁嗚妭鍜岀幇鐘躲€?
## 瑙勫垯鍏ュ彛涓?Profile

- `AGENTS.md` 鏄鍒欏敮涓€鏉ユ簮锛沗profiles/android-compose-mvi.json` 鍙斁椤圭洰宸紓锛歚sourceRoot`銆乣packageRoot`銆侀〉闈€佺洰鐨勫湴銆佽祫婧愬墠缂€銆乣navHost` 鍜?`checks` 寮€鍏炽€?- 浼樺厛绾э細鐢ㄦ埛褰撳墠鏄庣‘鎸囦护 > 鏈枃浠朵笌褰撳墠 profile > Android / Compose 骞冲彴瑙勫垯 > 閫氱敤宸ョ▼瑙勫垯 > 宸ュ叿榛樿琛屼负銆?- 浠ｇ爜涓庤鑼冧笉涓€鑷存椂榛樿浠ｇ爜鏄敊鐨勶紝闄ら潪宸插湪 `docs/rules/exceptions.md` 鐧昏銆?- 妫€鏌ヨ剼鏈笌瑙勮寖涓嶄竴鑷存椂锛屽湪褰撴鍙樻洿閲屽悓鏃舵敼鑴氭湰鎴栬鍒欙紝涓嶅厑璁搁暱鏈熸紓绉汇€?- 瑙勫垯鍐茬獊銆佽鍒欐湭瑕嗙洊銆侀渶瑕佷緥澶栵細鍏堝湪 `docs/rules/exceptions.md` 鐧昏锛屽啀鏀逛唬鐮併€?
## 瀹氫綅

Android 鍗?Activity + Compose + MVI + Koin + 绫诲瀷鍖栧鑸€傚綋鍓嶅彧鏈?`:app` 鍜?`:junkcc` 涓や釜 Gradle 妯″潡锛涘師 `cleanerkit`銆乣devicekit`銆乣networkkit` 宸插叏閮ㄥ苟鍏?`data`锛屼笉鍐嶆湁鐙珛 kit 鍖呫€傛墍鏈夐〉闈?ViewModel 浣跨敤 `MviViewModel`锛宎pp 缁熶竴瑁呴厤 Koin銆佺被鍨嬪寲 NavHost 涓庢潈闄?Gate銆?
## 鍖呯粨鏋?
```text
app/src/main/java/com/aicleanerstorage/
鈹溾攢 spaceclean/
鈹? 鈹溾攢 app/        缁勫悎鏍癸細Application銆丮ainActivity銆乺oot/銆乨i/銆乥ackground/銆乿iew/<page>/
鈹? 鈹溾攢 base/       designsystem/銆乵vi/銆乶avigation/銆乸ermission/銆乽til/
鈹? 鈹溾攢 data/       repository/ 浠撳簱瀹炵幇銆乴ocal/ 骞冲彴閫傞厤涓庡垹闄ゆ巿鏉?鈹? 鈹溾攢 domain/     model/<concept>/銆乸ort/銆乺ule/銆乪rror/
鈹? 鈹斺攢 app/view/<page>/{interaction,ui}
```

**灞備紭鍏堬紙layer-first锛?*锛歚domain` 鎸夎亴璐ｅ垎涓?`model / port / rule / error`锛屽悓绫绘枃浠堕泦涓紱`data` 鍒嗕负 `repository / local`锛屼粨搴撳疄鐜颁笌骞冲彴閫傞厤鍒嗗紑銆備笟鍔℃蹇甸€氳繃 `model/<concept>` 涓?`local/<capability>` 琛ㄨ揪锛屼笉鍐嶇粰姣忎釜 feature 澶嶅埗涓€濂?`model/port/error` 瀛愬寘銆傜湡鍑虹幇璺ㄥ涓?repository 鐨勭紪鎺掓椂鍐嶅缓 `domain/usecase/`銆?
渚濊禆鏂瑰悜锛堝厑璁稿垪琛紝鐢?`scripts/check-rules.ps1` 閫愭潯妫€鏌ワ級锛?
- `domain` 鍙緷璧栬嚜韬?+ Kotlin/JDK锛坈oroutines 渚嬪瑙?`docs/rules/exceptions.md`锛夛紱涓嶅緱 import `base`銆乣data`銆乣app`銆?- `data` 鍏佽 `domain`銆乣base`锛涗笉寰?import `app`銆?- `app/view` 鍏佽 `base`銆乣domain`锛涗笉寰?import `data`銆?- `app/root` 鍏佽 `app/view`銆乣base`銆乣domain`锛涗笉寰?import `data`銆?- `app/di` 鏄敮涓€瑁呴厤 data 瀹炵幇鐨勫寘銆?- `base` 涓嶅緱渚濊禆 `data` 鎴?`app`銆?
鐩綍鍚?= 鍖呭悕锛屽叏灏忓啓锛涚┖鐩綍涓嶅缓銆?
## domain

- 缁撴瀯锛歚domain/model/<concept>/`銆乣domain/port/`銆乣domain/rule/`銆乣domain/error/`锛涙ā鍨嬫寜姒傚康鍒嗙粍锛岀鍙?瑙勫垯/閿欒骞抽摵銆傜湡鍑虹幇璺ㄤ粨搴撶紪鎺掓椂鍐嶅缓 `domain/usecase/`銆?- 椤跺眰涓氬姟鏁版嵁绫荤粺涓€ `<Concept>Model`锛屾枃浠跺悕涓庣被鍨嬪悕涓€鑷达紱鏋氫妇淇濈暀 `Type`銆乣Mode`銆乣Sort` 绛夎涔夊悕绉般€?- 鍐欐搷浣滆繑鍥?`kotlin.Result`锛涜鎿嶄綔鐩存帴杩斿洖锛涘け璐ヨ涔夊啓杩?KDoc銆俙suspend` 鍙〃杈锯€滃彲鑳借€楁椂鈥濓紝涓嶇粦瀹氱嚎绋嬨€?- 绔彛鎸変笟鍔¤兘鍔涘垝鍒嗭紝涓嶆寜浠嬭川/瀹炵幇缁嗚妭鎷嗗垎锛涚鍙ｅ悕鐢ㄤ笟鍔″姩浣滄垨涓氬姟鍚嶈瘝锛屼笉鐢?`MediaStore`銆乣SAF` 杩欑被浠嬭川鍚嶃€?- 閿欒瀹氫箟鍦?`domain/error/`锛涙瘡涓?`data object` / `data class` 鍒嗘敮蹇呴』鏈?KDoc锛屽啓娓呪€滀綍鏃跺彂鐢熴€佽皟鐢ㄦ柟鎬庝箞澶勭悊鈥濄€?- 瀵瑰鎻愪緵鑳藉姏鐨勭被鍨嬪繀椤绘湁绫荤骇 KDoc锛涘叕寮€鏂规硶鍐欎綔鐢ㄣ€乣@param`銆乣@return`銆?- 绂佹 `android.*`銆乣androidx.*`銆乣Context`銆乣Uri`銆乣ContentResolver`銆乣Compose`銆乣Dispatchers`銆佽祫婧?id銆丼QL 瀛楃涓层€佹棩蹇楀疄鐜般€?
- 端口可以返回 `Flow` 或 `suspend`；不得暴露 `StateFlow`，当前状态由 ViewModel 或明确的 app-scoped store 持有。
## data

- 鍙仛瀹炵幇锛歚repository/` 鏀剧鍙ｅ疄鐜帮紝`local/` 鏀?MediaStore/SAF/绯荤粺鏈嶅姟/鏂囦欢绯荤粺绛夊钩鍙伴€傞厤锛涗笉瀹氫箟涓氬姟瑙勫垯銆?- 鍏蜂綋瀹炵幇绫婚粯璁?`internal`锛汚ndroid 缁勪欢锛坄Service` / `Receiver` / `Provider` / `Activity`锛変緥澶栧苟鐧昏銆?- 鍛藉悕鎸夎鑹诧細鑱氬悎浠撳簱 `<姒傚康>Repository`锛涘崟浠嬭川瀹炵幇 `<姒傚康><浠嬭川>Repository`锛涚鍙ｅ悕琚崰鐢ㄦ椂鐢?`Default<姒傚康>Repository` 鎴?`<绔彛鍚?Impl`锛涘钩鍙版満鍒剁敤鏈哄埗鍓嶇紑銆?- 寮傚父鍦ㄥ疄鐜板眰鏀跺彛鎴?`Result`锛沗CancellationException` 蹇呴』缁х画浼犳挱銆?- 鐩存帴杩斿洖 domain 妯″瀷锛涘彧鏈夊嚭鐜?Entity/DTO 鏃舵墠寤?`mapper/`锛屽綋鍓嶄笉寤恒€?- `data` 鐨勫钩鍙板疄鐜板繀椤荤洿鎺ュ疄鐜?domain 绔彛锛涗笉鍐嶆柊澧炩€滆浆鍙戔€濈殑鍖呰绫汇€?
## UI锛圕ompose + MVI锛?
UI 缁嗚妭浠?`docs/mvi-architecture.md` 涓哄噯锛屾憳瑕侊細

- 鐘舵€佸彧鑳界敱 `setState` / Reducer 淇敼锛涘壇浣滅敤鍙嚭鐜板湪 Route / EffectHandler銆?- 浜嬩欢閫氶亾鐢?`base/mvi/CollectEffects` 鍗曟秷璐硅€呮敹闆嗭紝绂佹鎵嬪啓 `LaunchedEffect { viewModel.event.collect { } }`銆?- `ui/part` 鏃犵姸鎬侊細鍙傛暟鏄暟鎹?+ lambda锛屼笉鎺?ViewModel銆佷笉 collect Flow銆佷笉鍋氬鑸€佷笉璇?Koin銆?- `XxxScreen` 鍙傛暟鍙敤 `state`銆乣onAction`銆乣modifier`锛涙彃妲藉拰骞冲彴瀵硅薄鍥炶皟渚嬪銆?- 鐢ㄦ埛鍙鏂囨璧?`strings.xml` / `UiText`锛涢敊璇埌鏂囨鏀跺彛鍦ㄩ〉闈?`interaction/XxxErrorText.kt`銆?- 绯荤粺杩斿洖閿繀椤?dispatch 椤甸潰 Action锛岀敱 Route 娑堣垂 Event 鍐?`back()`銆?
## 鍙嶈繃搴﹂槻寰?
- 淇′换灞傞棿濂戠害锛歞ata 宸茬敤 `Result` 鏀跺彛寮傚父锛孷iewModel 鐩存帴 `fold` / `onFailure`锛屼笉鍐?`try-catch` 杞秷鎭€?- `try-catch` 鍙厑璁稿嚭鐜板湪骞冲彴杈圭晫锛坉ata銆乥ase 鐨勫钩鍙板伐鍏凤級锛沗try-finally` 鍙敤浜庢竻鐞嗚祫婧愶紱`CancellationException` 蹇呴』閲嶆姏銆?- 闈炵┖绫诲瀷鍙傛暟涓嶅啓 `?.` / `?: emptyList()` 鍏滃簳锛涘彧鏈変笟鍔′笂纭疄鍙┖鐨勭姸鎬佹墠浣跨敤 nullable銆?- `require` / `check` 鍙斁鍦ㄥ閮ㄨ緭鍏ユ垨鏋勯€犲叆鍙傝竟鐣岋紱鍐呴儴鍑芥暟涓嶉噸澶嶆牎楠屻€?- `UiState` 鍙繚鐣欐覆鏌撳繀椤荤殑瀛楁锛涜兘鐢?`error: UiText?` 鎺ㄥ鐨?`isError` 涓嶅缓銆?- 涓嶅洜涓衡€滀互鍚庡彲鑳芥浛鎹⑩€濆垱寤烘帴鍙ｃ€丗actory 鎴栫┖瀹炵幇锛沝omain port 鏄笟鍔″绾︼紝鎸変笟鍔￠渶瑕佷繚鐣欍€?
## 浠ｇ爜椋庢牸

- Kotlin 瀹樻柟椋庢牸锛? 绌烘牸缂╄繘銆佸琛岃皟鐢ㄥ熬閫楀彿銆佹棤閫氶厤 import銆?- 涓€涓枃浠朵竴涓《灞傚叕寮€绫诲瀷锛屾枃浠跺悕涓庣被鍨嬪悕涓€鑷达紱渚嬪锛歚XxxContract.kt`銆乣XxxErrorText.kt`銆乣XxxPreviews.kt`銆乣XxxReducer.kt`銆乣XxxModels.kt`銆乣PreviewFixtures.kt`銆乣Theme.kt`銆乣AppThemeTokens.kt`銆乣AppModule.kt`銆乣CollectEffects.kt`銆乣AppDestination.kt`銆乣BatteryChartModels.kt`銆乣VirusAndroidEffects.kt`銆?- 鎺ュ彛涓嶅姞 `I` 鍓嶇紑锛涢〉闈㈠唴绫诲瀷甯﹂〉闈㈠墠缂€锛歚JunkCleanUiState`銆乣NetworkSpeedUiState`銆?- 缁勪欢缁熶竴 `CleanX` / `App` 鍓嶇紑锛涢€氱敤璁捐缁勪欢鏀?`base/designsystem/component`銆?- `XxxAction` 鏄?UI 涓婃姤鐨勪簨瀹烇紙杩囧幓寮忥級锛沗XxxEvent` 鏄?VM 涓嬪彂鐨勫懡浠わ紙绁堜娇寮忥級銆?- 鏈鍙湁涓€濂楋細`document`銆乣file`銆乣junk`銆乣clean`銆乣scan`銆乣delete`銆乣permission`锛涗唬鐮併€佽祫婧愰敭銆佹枃妗堜竴鑷淬€?- 璧勬簮閿細鏂板椤甸潰鐢?`<page>_<thing>`锛涘瓨閲忛敭淇濈暀鐜版湁鍓嶇紑锛屼笉涓€娆℃€ч噸鍛藉悕锛沗values/` 涓?`values-zh/` 绛夎瑷€鍖呴敭闆嗗悎涓€鑷淬€?- `const val` 涓€寰?UPPER_SNAKE_CASE锛涙櫘閫?`val` 鐢?camelCase锛涘竷灏斿瓧娈电敤 `is` / `has` / `can` / `should` 寮€澶达紱闆嗗悎鐢ㄥ鏁般€?- 鍗曞嚱鏁帮紙涓嶅惈 `when` 鍒嗘敮浣擄級涓嶈秴杩?40 琛岋紱鍙傛暟涓嶈秴杩?5 涓紝鏇村鏃剁敤鍙傛暟瀵硅薄锛涘祵濂椾笉瓒呰繃 3 灞傦紝浼樺厛鏃╄繑鍥炪€?- 缂╁啓鎸夊崟璇嶅ぇ灏忓啓锛歚Api`銆乣Dto`銆乣Id`銆乣Url`銆乣Ui`銆乣Json`锛涗笉鍐?`API`銆乣DTO`銆乣ID`銆乣URL`銆?- 杩斿洖 `Flow` 鐨勫嚱鏁扮敤 `observeXxx`锛涙寕璧峰嚱鏁扮洿鎺ュ姩璇嶏紝涓嶅姞 `Async` 鍚庣紑銆?- KDoc 鐢ㄤ腑鏂囷細鍐欌€滀负浠€涔堚€濆拰瀵瑰濂戠害锛屼笉閫愯澶嶈堪瀹炵幇锛涚函杞戒綋锛圲iState / Action / Event / Reducer锛夐潬鍛藉悕琛ㄨ揪銆?
## 娴嬭瘯

- domain 鐢ㄧ函 JVM 娴嬭瘯锛屼笉寮?Robolectric銆?- ViewModel 娴嬭瘯鐢?`runTest`锛涢渶瑕佷富璋冨害鍣ㄦ椂鐢?`Dispatchers.setMain`锛屽閮ㄤ緷璧栫敤 Fake 绔彛瀹炵幇銆?- data 鐨勭鍙ｅ疄鐜版祴璇曠敤鐪熷疄涓存椂鐩綍鎴栧唴瀛樹粙璐紝涓嶄緷璧栬澶囥€?- 鏂囦欢鍚?`<Type>Test.kt`锛涙柟娉曠敤 camelCase 琛屼负鍙ワ紙`nextRequiresSelection`锛夛紝涓嶅啓 `test1` / `should` 鍓嶇紑銆?- 鏇胯韩鐢?`FakeXxx`锛屽す鍏风敤 `XxxFixture`锛屼笉鐢?`Mock` 鍓嶇紑锛岄櫎闈炵湡鐨勫紩浜?mock 妗嗘灦銆?- 淇?bug 鏃惰ˉ涓€鏉¤兘澶嶇幇鐨勭敤渚嬨€?
## 鎻愪氦

- 涓€娆℃彁浜や竴涓富棰橈紱淇℃伅鐢?`feat:` / `fix:` / `docs:` / `refactor:` / `test:` / `chore:` 鍓嶇紑锛屽彲甯?scope 鍜屼腑鏂囨弿杩般€?- 鎻愪氦鍓嶈嚦灏戣窇 `.\scripts\check-rules.ps1`锛涘悎鍏ュ墠璺?`.\scripts\ci.ps1`锛堣鑼?+ 缂栬瘧 + 鍗曟祴锛夈€?
## 涓嶈鎻愬墠寤?
| 涓滆タ | 浠€涔堟椂鍊欏缓 |
|---|---|
| `mapper/` | 瀛樺偍妯″瀷 鈮?棰嗗煙妯″瀷 |
| 鏂?usecase | 鍑虹幇璺ㄥ涓?repository 鎴栬法鍩熺殑缂栨帓锛涘崟绔彛銆佸彧杞彂鐨勪笉寤?|
| `data/remote/` | 绗竴涓湡瀹炵綉缁滆皟鐢?|
| `base/util/` 鍐嶆媶鏂囦欢 | 鍑虹幇鍙嫭绔嬪鐢ㄧ殑鑱岃矗鏃讹紝涓嶆寜鏂囦欢琛屾暟鎷嗗垎 |
| `domain/rule/` 鏂版枃浠?| 鍑虹幇绗竴鏉＄函瑙勫垯鏃?|

鍒ゆ柇娉曪細鐩綍閲岀幇鍦ㄦ斁杩涘幓鐨勬枃浠舵暟鏄?0 鈫?涓嶅缓锛涘悓涓€璋冪敤閾句笂鍙湁鍗曠洰鏍囥€佹棤鐙珛璇箟鐨勮浆鍙戝眰 鈫?涓嶅缓锛涘垽鏂笉浜嗘斁鍝眰 鈫?鐪嬪畠 import 涓?import 骞冲彴 API銆?
## 妫€鏌?
```powershell
.\scripts\check-rules.ps1    # 鎵ц绛栫暐鍏佽鏃?.\scripts\check-rules.cmd    # 鎵ц绛栫暐鍙楅檺鏃?```

褰撳墠闂ㄧ瑕嗙洊锛歞omain 绾噣銆佸垎灞傛柟鍚戙€乨ata 鍙鎬?濂戠害銆乿iew 涓?import data銆乨omain 妯″瀷鍛藉悕銆侀敊璇垎鏀?KDoc銆佸崟鏂囦欢鍏紑绫诲瀷銆佸懡鍚嶃€乁iText銆丆ollectEffects銆丅ackHandler銆乪ffect 浣嶇疆銆乸ackage/鐩綍銆佽瑷€鍖?key銆備换浣曚竴椤逛笉閫氳繃閫€鍑虹爜闈?0銆?
鏂板厠闅嗘墽琛屼竴娆?`.\scripts\install-hooks.ps1` 鍚敤 `pre-commit`锛涢挬瀛愬彲琚?`git commit --no-verify` 缁曡繃锛孋I 蹇呴』璺戝悓涓€鑴氭湰鍏滃簳銆?
