package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.applock.AppLockAppModel
import kotlinx.coroutines.flow.Flow

/**
 * 搴旂敤閿佺殑涓氬姟濂戠害锛歅IN銆侀攣瀹氬寘銆佺洃鎺у紑鍏充笌鍙攣瀹氬簲鐢ㄦ煡璇€? *
 * 璇ョ鍙ｆ寜搴旂敤閿佽兘鍔涜仛鍚堬紝涓嶆媶鍒嗭紱椤甸潰銆佺洃鎺ф湇鍔″拰鍖呭畨瑁呭洖璋冨叡鍚屼緷璧栧畠銆? * 鏉冮檺鏄惁鎺堜簣鐢?UI 鐨?`AppPermission` 鍒ゆ柇锛屼笉鍦ㄦ湰绔彛閲嶅鏌ヨ銆? */
interface AppLockRepository {
    /**
     * 观察当前已锁定且监控开启的应用数量。
     *
     * @return 应用数量变化流；订阅时立即收到当前值。
     */
    fun observeLockedAppCount(): Flow<Int>

    /**
     * 读取当前已锁定应用数量，供无法等待 Flow 的初始化场景使用。
     *
     * @return 当前数量。
     */
    fun currentLockedAppCount(): Int

    /**
     * 鍒ゆ柇鏄惁宸茬粡璁剧疆杩?PIN銆?     *
     * @return true 琛ㄧず宸茶缃紝椤甸潰搴旇繘鍏?PIN 楠岃瘉娴佺▼銆?     */
    fun isPinSet(): Boolean

    /**
     * 淇濆瓨 PIN銆?     *
     * @param pin 4 浣嶆暟瀛?PIN锛涘疄鐜拌礋璐ｆ牎楠岄暱搴︿笌瀛楃鑼冨洿銆?     */
    fun savePin(pin: String)

    /**
     * 鏍￠獙 PIN銆?     *
     * @param pin 鐢ㄦ埛杈撳叆鐨?4 浣?PIN銆?     * @return true 琛ㄧず鍖归厤锛沠alse 琛ㄧず涓嶅尮閰嶆垨鏍煎紡闈炴硶銆?     */
    fun verifyPin(pin: String): Boolean

    /**
     * 璇诲彇宸查攣瀹氬寘鍚嶃€?     *
     * @return 褰撳墠閿佸畾鐨勫寘鍚嶉泦鍚堬紱鏈缃椂涓虹┖闆嗗悎銆?     */
    fun lockedPackages(): Set<String>

    /**
     * 鍒ゆ柇鍗曚釜鍖呮槸鍚﹁閿佸畾銆?     *
     * @param packageName 鐩爣搴旂敤鍖呭悕銆?     * @return true 琛ㄧず璇ュ簲鐢ㄥ彈搴旂敤閿佷繚鎶ゃ€?     */
    fun isPackageLocked(packageName: String): Boolean

    /**
     * 璁剧疆鍗曚釜鍖呯殑閿佸畾鐘舵€併€?     *
     * @param packageName 鐩爣搴旂敤鍖呭悕锛涚┖鍊兼垨鏈簲鐢ㄥ寘鍚嶅簲琚拷鐣ャ€?     * @param locked true 琛ㄧず閿佸畾锛宖alse 琛ㄧず瑙ｉ櫎閿佸畾銆?     */
    fun setPackageLocked(
        packageName: String,
        locked: Boolean,
    )

    /**
     * 瑕嗙洊寮忎繚瀛橀攣瀹氬寘闆嗗悎銆?     *
     * @param packageNames 鐩爣鍖呭悕闆嗗悎锛涘疄鐜颁細鍘婚噸骞惰繃婊ゆ湰搴旂敤銆?     */
    fun setLockedPackages(packageNames: Set<String>)

    /**
     * 鏌ヨ璁惧涓婂彲閿佸畾鐨勫惎鍔ㄥ櫒搴旂敤銆?     *
     * @return 鎸夊悕绉版帓搴忕殑搴旂敤鍒楄〃锛涙湭璁剧疆 PIN 鏃惰繑鍥為粯璁ら攣瀹氱姸鎬併€?     */
    suspend fun lockableApps(): List<AppLockAppModel>

    /**
     * 鍒ゆ柇搴旂敤閿佺洃鎺ф槸鍚﹀紑鍚€?     *
     * @return true 琛ㄧず鐩戞帶鏈嶅姟搴旀娴嬪墠鍙板簲鐢ㄣ€?     */
    fun isMonitoringEnabled(): Boolean

    /**
     * 璁剧疆搴旂敤閿佺洃鎺у紑鍏炽€?     *
     * @param enabled true 琛ㄧず寮€鍚洃鎺э紱鏉冮檺缂哄け鐢辫皟鐢ㄦ柟鍏堝鐞嗐€?     */
    fun setMonitoringEnabled(enabled: Boolean)

    /**
     * 鍒ゆ柇鑷姩閿佸畾鏂板畨瑁呭簲鐢ㄦ槸鍚﹀紑鍚€?     *
     * @return true 琛ㄧず鏂板畨瑁呭簲鐢ㄩ粯璁ゅ姞鍏ラ攣瀹氬垪琛ㄣ€?     */
    fun isAutoLockEnabled(): Boolean

    /**
     * 璁剧疆鑷姩閿佸畾鏂板畨瑁呭簲鐢ㄥ紑鍏炽€?     *
     * @param enabled true 琛ㄧず鏂板畨瑁呭簲鐢ㄩ粯璁ら攣瀹氥€?     */
    fun setAutoLockEnabled(enabled: Boolean)

    /**
     * 鍒ゆ柇 PIN 杈撳叆鏄惁鍚敤鎸姩鍙嶉銆?     *
     * @return true 琛ㄧず鎸夐敭鏃舵尟鍔ㄣ€?     */
    fun isVibrationEnabled(): Boolean

    /**
     * 璁剧疆 PIN 杈撳叆鎸姩鍙嶉銆?     *
     * @param enabled true 琛ㄧず鎸夐敭鏃舵尟鍔ㄣ€?     */
    fun setVibrationEnabled(enabled: Boolean)

    /**
     * 澶勭悊鏂板畨瑁呭簲鐢ㄤ簨浠躲€?     *
     * @param packageName 鏂板畨瑁呭簲鐢ㄥ寘鍚嶏紱鑷姩閿佸畾鍏抽棴鏃跺簲蹇界暐銆?     */
    fun handlePackageAdded(packageName: String)
}
