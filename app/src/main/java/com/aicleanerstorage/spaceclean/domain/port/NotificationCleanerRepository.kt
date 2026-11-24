package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.notification.BlockableNotificationAppModel
import com.aicleanerstorage.spaceclean.domain.model.notification.NotificationCleanerSnapshotModel
import com.aicleanerstorage.spaceclean.domain.model.notification.PostedNotificationModel

/**
 * 通知拦截的业务契约：判断是否拦截、记录已拦截通知并维护应用选择。
 *
 * 通知监听服务在系统回调线程调用 `shouldBlock` / `recordBlockedNotification`，
 * 实现必须快速返回，不做阻塞 IO。
 */
interface NotificationCleanerRepository {
    /**
     * 判断指定应用的通知是否应被拦截。
     *
     * @param packageName 发通知的应用包名。
     * @return true 表示总开关开启且该应用被选中。
     */
    fun shouldBlock(packageName: String): Boolean

    /**
     * 记录一条已取消的通知。
     *
     * @param posted 被拦截通知的快照。
     */
    fun recordBlockedNotification(posted: PostedNotificationModel)

    /**
     * 判断是否已授予通知监听权限。
     *
     * 按系统授权列表里的**组件名**判定，不按包名：同名包下已失效的旧 listener
     * 不算授权。
     *
     * @return true 表示系统已启用本应用的 listener。
     */
    fun hasNotificationListenerAccess(): Boolean

    /**
     * 读取通知清理页面快照。
     *
     * @param includeApps true 表示同时加载可选择的已安装应用。
     * @return 当前开关、已拦截列表与应用列表。
     */
    fun notificationSnapshot(includeApps: Boolean = true): NotificationCleanerSnapshotModel

    /**
     * 设置通知拦截总开关。
     *
     * @param enabled true 表示开启拦截。
     */
    fun setNotificationBlockingEnabled(enabled: Boolean)

    /**
     * 清空已拦截通知记录。
     *
     * @return 被清除的记录数量。
     */
    fun clearBlockedNotifications(): Int

    /**
     * 读取当前选中的拦截应用。
     *
     * @return 包名集合；没有选择时为空集合。
     */
    fun selectedNotificationPackages(): Set<String>

    /**
     * 读取可选择的已安装应用。
     *
     * @return 按名称排序的应用列表。
     */
    fun notificationApps(): List<BlockableNotificationAppModel>

    /**
     * 设置单个应用的拦截选择。
     *
     * @param packageName 目标应用包名。
     * @param selected true 表示拦截该应用通知。
     */
    fun setNotificationPackageSelected(
        packageName: String,
        selected: Boolean,
    )
}
