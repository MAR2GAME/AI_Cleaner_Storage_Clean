package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission

/**
 * 文件管理流程的权限状态，列表页与详情页共享同一份。
 *
 * 一个流程可能需要多组权限：普通文件页只需要 [AppPermission.StorageFiles]，
 * 隐私图页面还需要 [AppPermission.MediaLocation]。因此状态保存的是
 * [required] 集合，而不是单个 boolean；只有集合内全部授予时 [granted] 才为 true。
 *
 * @param required 本流程必须的权限集合。
 * @param initialGranted 进入页面时根据系统状态算出的初始值。
 */
class FileManagerPermissionState(
    val required: Set<AppPermission>,
    initialGranted: Boolean,
) {
    /** 全部 [required] 是否已授予；只有 true 时页面才允许启动扫描。 */
    var granted by mutableStateOf(initialGranted)
        private set

    /** 页面是否正在离开；用于阻止异步回调返回后再导航。 */
    var leavingPage by mutableStateOf(false)
        private set

    private var permissionRequester: (((Boolean) -> Unit) -> Unit)? = null

    /**
     * 重新校验并申请流程所需权限。
     *
     * 用于删除等二次操作前的兜底；未挂接申请器时按拒绝处理。
     *
     * @param onResult 权限结果回调。
     */
    fun requestPermission(onResult: (Boolean) -> Unit) {
        permissionRequester?.invoke(onResult) ?: onResult(false)
    }

    internal fun attachPermissionRequester(requester: ((Boolean) -> Unit) -> Unit) {
        permissionRequester = requester
    }

    /**
     * 重新从系统读取权限状态。
     *
     * 从系统弹窗、设置页或后台回到页面时调用；只更新 [granted]，不改 [required]。
     *
     * @param context 用于查询系统权限的上下文。
     */
    fun refresh(context: Context) {
        granted = required.all { it.isGranted(context) }
    }

    /** 标记页面正在离开；重复调用无副作用。 */
    fun markLeaving() {
        leavingPage = true
    }
}

/**
 * 回到首页。
 *
 * 先标记离开，避免返回过程中事件再次触发导航；`resetToRoot` 会清空文件管理流程。
 *
 * @param router 应用导航器。
 */
fun FileManagerPermissionState.leaveHome(router: AppNavigator) {
    markLeaving()
    router.resetToRoot(AppDestination.Main())
}

/**
 * 返回上一页。
 *
 * 已经标记离开时直接返回，保证一次退出只导航一次。
 *
 * @param router 应用导航器。
 */
fun FileManagerPermissionState.leaveBack(router: AppNavigator) {
    if (leavingPage) return
    markLeaving()
    router.back()
}
