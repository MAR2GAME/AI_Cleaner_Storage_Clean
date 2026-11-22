package com.aicleanerstorage.spaceclean.domain.port

/**
 * 电池采样开关的应用级控制。
 *
 * 页面只关心“打开采样并同步运行时”或“按当前设置重新同步”，不关心
 * SharedPreferences 与前台服务；具体实现放在 app/background。
 */
interface BatterySamplingControl {
    /** 打开采样并立即同步运行时。 */
    fun activate()

    /** 按当前设置同步运行时，不修改设置。 */
    fun reconcile()
}
