package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.security.VirusScanItemModel
import com.aicleanerstorage.spaceclean.domain.model.security.VirusThreatModel

/**
 * 病毒扫描的授权标记、应用元数据与威胁判定契约。
 *
 * 扫描引擎由 [VirusScanner] 负责；本端口只提供页面需要的风控与展示信息。
 */
interface VirusSecurityRepository {
    /**
     * 判断是否已接受扫描数据使用提示。
     *
     * @return true 表示用户可以开始扫描。
     */
    fun isScanNoticeAccepted(): Boolean

    /** 记录用户已接受扫描数据使用提示。 */
    fun setScanNoticeAccepted()

    /**
     * 判断之前是否因缺少已安装应用访问权限而失败。
     *
     * @return true 表示需要再次引导用户去系统设置授权。
     */
    fun hasInstalledAppsAccessFailedBefore(): Boolean

    /**
     * 记录已安装应用访问权限失败状态。
     *
     * @param failed true 表示上次因权限失败。
     */
    fun setInstalledAppsAccessFailed(failed: Boolean)

    /**
     * 读取应用显示名称。
     *
     * @param packageName 目标包名。
     * @return 应用名称；查询不到时返回包名。
     */
    fun appLabel(packageName: String): String

    /**
     * 将扫描项转换为威胁展示模型。
     *
     * @param item 扫描项。
     * @param isFile true 表示文件扫描项，false 表示应用扫描项。
     * @return 威胁等级与展示信息。
     */
    fun threatFor(
        item: VirusScanItemModel,
        isFile: Boolean,
    ): VirusThreatModel

    /**
     * 判断当前是否拥有已安装应用访问权限。
     *
     * @return true 表示可以枚举已安装应用。
     */
    fun hasInstalledAppsAccess(): Boolean

    /**
     * 判断设备是否处于 ADB 调试风险状态。
     *
     * @return true 表示检测到 ADB 开启或相关风险。
     */
    fun hasAdbRisk(): Boolean
}
