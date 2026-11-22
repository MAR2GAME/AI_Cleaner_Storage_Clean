package com.aicleanerstorage.spaceclean.app.background

import com.aicleanerstorage.spaceclean.domain.port.BatterySamplingControl
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository

/**
 * 应用级电池采样同步。
 *
 * 负责“设置开关”和“后台运行时”之间的编排；它依赖应用生命周期与
 * [AppRuntimeService]，不是 domain usecase，因此放在 app/background。
 * domain 只保留 [AppSettingsRepository] 契约。
 *
 * @param settingsRepository 读取/写入采样开关。
 * @param syncRuntime 启动或停止后台运行时；由 DI 注入 `AppRuntimeService.reconcile`。
 */
internal class BatterySamplingSync(
    private val settingsRepository: AppSettingsRepository,
    private val syncRuntime: () -> Unit,
) : BatterySamplingControl {
    override fun activate() {
        settingsRepository.enableBackgroundBatterySampling()
        reconcile()
    }

    override fun reconcile() = syncRuntime()
}
