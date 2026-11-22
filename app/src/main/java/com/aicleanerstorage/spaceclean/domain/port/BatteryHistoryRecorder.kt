package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.battery.BatteryHistoryOwner
import com.aicleanerstorage.spaceclean.domain.model.battery.BatteryHistorySampleModel

/**
 * 电池历史采样的启动、停止与单次采样契约。
 *
 * 采样器按 owner 计数：页面或服务各自 start/stop，最后一个 owner 停止后才真正停表。
 */
interface BatteryHistoryRecorder {
    /**
     * 为指定 owner 开启采样。
     *
     * @param owner 页面或服务标识，用于引用计数。
     */
    fun start(owner: BatteryHistoryOwner)

    /**
     * 为指定 owner 停止采样。
     *
     * @param owner 页面或服务标识；未开启时调用应无副作用。
     */
    fun stop(owner: BatteryHistoryOwner)

    /**
     * 立即采样一次。
     *
     * @param force true 表示忽略采样间隔限制。
     * @return 本次采样；无法读取电池信息时返回 null。
     */
    fun sampleOnce(force: Boolean = false): BatteryHistorySampleModel?
}
