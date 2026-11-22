package com.aicleanerstorage.spaceclean.base.permission

import android.content.IntentSender

/**
 * 系统删除授权请求契约。
 *
 * data 层只依赖这个接口；具体实现由 app 层持有 ActivityResultRegistry，
 * 在 Activity 创建时 attach、销毁时 detach。
 */
interface MediaDeletionRequester {
    /**
     * 弹出系统删除确认。
     *
     * @param sender MediaStore 提供的删除请求。
     * @return 用户确认返回 true；无宿主、取消或异常返回 false。
     */
    suspend fun request(sender: IntentSender): Boolean
}
