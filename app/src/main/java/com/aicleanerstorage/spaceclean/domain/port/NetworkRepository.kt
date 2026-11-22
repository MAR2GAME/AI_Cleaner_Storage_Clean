package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.network.NetworkInfoModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkScanResultModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkSpeedModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkUsageResultModel

/**
 * 缃戠粶鐘舵€併€佸眬鍩熺綉鎵弿銆佹祴閫熶笌娴侀噺缁熻鐨勪笟鍔″绾︺€? *
 * 鍦?IO 绾跨▼鍥炶皟锛孶I 鍙妸瀹冭浆鎴?Action锛屼笉鐩存帴鏀圭姸鎬併€? */
interface NetworkRepository {
    /**
     * 璇诲彇褰撳墠缃戠粶杩炴帴淇℃伅銆?     *
     * @return 缃戠粶绫诲瀷銆佸悕绉颁笌杩炴帴鐘舵€併€?     */
    fun readNetworkInfo(): NetworkInfoModel

    /**
     * 鍒ゆ柇褰撳墠鏄惁鏈夊彲鐢ㄧ綉缁溿€?     *
     * @return true 琛ㄧず瀛樺湪宸茶繛鎺ョ綉缁溿€?     */
    fun isNetworkAvailable(): Boolean

    /**
     * 鍒ゆ柇褰撳墠鏄惁杩炴帴 Wi-Fi銆?     *
     * @return true 琛ㄧず Wi-Fi 宸茶繛鎺ャ€?     */
    fun isWifiConnected(): Boolean

    /**
     * 鍒ゆ柇鏄惁鎷ユ湁浣跨敤鎯呭喌璁块棶鏉冮檺銆?     *
     * @return true 琛ㄧず鍙互璇诲彇娴侀噺缁熻銆?     */
    fun hasNetworkUsageAccess(): Boolean

    /**
     * 璇诲彇娴侀噺缁熻銆?     *
     * @return 娴侀噺姹囨€讳笌搴旂敤鏄庣粏锛涙棤鏉冮檺鏃剁敱瀹炵幇杩斿洖闄嶇骇鏁版嵁銆?     */
    suspend fun readNetworkUsage(): NetworkUsageResultModel

    /**
     * 鎵ц涓€娆＄綉缁滄祴閫熴€?     *
     * @param onProgress 娴嬮€熼樁娈典笌瀹炴椂閫熷害鍥炶皟锛涘彲鑳藉湪 IO 绾跨▼璋冪敤銆?     * @return 鏈€缁堟祴閫熺粨鏋溿€?     */
    suspend fun runSpeedTest(onProgress: (NetworkSpeedModel) -> Unit): NetworkSpeedModel

    /**
     * 鎵弿褰撳墠灞€鍩熺綉璁惧銆?     *
     * @return 鎵弿缁撴灉锛涙棤缃戠粶鎴栨湭鎺堟潈鏃惰繑鍥炵┖缁撴灉銆?     */
    suspend fun scanWifi(): NetworkScanResultModel
}
