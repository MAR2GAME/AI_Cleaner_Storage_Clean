package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.applock.AppLockAppModel
import kotlinx.coroutines.flow.StateFlow

/**
 * 应用锁的业务契约：PIN、锁定包、监控开关与可锁定应用查询。
 *
 * 该端口按应用锁能力聚合，不拆分；页面、监控服务和包安装回调共同依赖它。
 * 权限是否授予由 UI 的 `AppPermission` 判断，不在本端口重复查询。
 */
interface AppLockRepository {
    /** 当前已锁定且监控开启的应用数量。 */
    val lockedAppCountFlow: StateFlow<Int>

    /**
     * 判断是否已经设置过 PIN。
     *
     * @return true 表示已设置，页面应进入 PIN 验证流程。
     */
    fun isPinSet(): Boolean

    /**
     * 保存 PIN。
     *
     * @param pin 4 位数字 PIN；实现负责校验长度与字符范围。
     */
    fun savePin(pin: String)

    /**
     * 校验 PIN。
     *
     * @param pin 用户输入的 4 位 PIN。
     * @return true 表示匹配；false 表示不匹配或格式非法。
     */
    fun verifyPin(pin: String): Boolean

    /**
     * 读取已锁定包名。
     *
     * @return 当前锁定的包名集合；未设置时为空集合。
     */
    fun lockedPackages(): Set<String>

    /**
     * 判断单个包是否被锁定。
     *
     * @param packageName 目标应用包名。
     * @return true 表示该应用受应用锁保护。
     */
    fun isPackageLocked(packageName: String): Boolean

    /**
     * 设置单个包的锁定状态。
     *
     * @param packageName 目标应用包名；空值或本应用包名应被忽略。
     * @param locked true 表示锁定，false 表示解除锁定。
     */
    fun setPackageLocked(
        packageName: String,
        locked: Boolean,
    )

    /**
     * 覆盖式保存锁定包集合。
     *
     * @param packageNames 目标包名集合；实现会去重并过滤本应用。
     */
    fun setLockedPackages(packageNames: Set<String>)

    /**
     * 查询设备上可锁定的启动器应用。
     *
     * @return 按名称排序的应用列表；未设置 PIN 时返回默认锁定状态。
     */
    suspend fun lockableApps(): List<AppLockAppModel>

    /**
     * 判断应用锁监控是否开启。
     *
     * @return true 表示监控服务应检测前台应用。
     */
    fun isMonitoringEnabled(): Boolean

    /**
     * 设置应用锁监控开关。
     *
     * @param enabled true 表示开启监控；权限缺失由调用方先处理。
     */
    fun setMonitoringEnabled(enabled: Boolean)

    /**
     * 判断自动锁定新安装应用是否开启。
     *
     * @return true 表示新安装应用默认加入锁定列表。
     */
    fun isAutoLockEnabled(): Boolean

    /**
     * 设置自动锁定新安装应用开关。
     *
     * @param enabled true 表示新安装应用默认锁定。
     */
    fun setAutoLockEnabled(enabled: Boolean)

    /**
     * 判断 PIN 输入是否启用振动反馈。
     *
     * @return true 表示按键时振动。
     */
    fun isVibrationEnabled(): Boolean

    /**
     * 设置 PIN 输入振动反馈。
     *
     * @param enabled true 表示按键时振动。
     */
    fun setVibrationEnabled(enabled: Boolean)

    /**
     * 处理新安装应用事件。
     *
     * @param packageName 新安装应用包名；自动锁定关闭时应忽略。
     */
    fun handlePackageAdded(packageName: String)
}
