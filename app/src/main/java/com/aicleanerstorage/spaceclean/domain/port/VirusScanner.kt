package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.error.VirusScanError
import com.aicleanerstorage.spaceclean.domain.model.security.VirusScanItemModel
import com.aicleanerstorage.spaceclean.domain.model.security.VirusScanMode

/**
 * 病毒扫描引擎契约。
 *
 * 扫描是异步的，进度与结果通过 [Listener] 回调；同一实例同时只支持一个扫描，
 * 新扫描开始前必须先取消旧扫描或等待旧扫描结束。
 */
interface VirusScanner {
    /**
     * 启动扫描。
     *
     * @param mode 快速或深度扫描。
     * @param listener 扫描生命周期回调；实现保证最终只回调一次结束/取消/中断。
     */
    fun startScan(
        mode: VirusScanMode,
        listener: Listener,
    )

    /** 取消当前扫描；没有扫描时调用应无副作用。 */
    fun cancelScan()

    /** 扫描生命周期回调。 */
    interface Listener {
        /** 扫描已开始。 */
        fun onScanStarted()

        /**
         * 扫描到一项。
         *
         * @param item 当前检查项；引擎只做进度通知时可传 null。
         */
        fun onScanProgress(item: VirusScanItemModel?)

        /**
         * 扫描失败。
         *
         * @param error 失败原因；调用方应停止进度并展示错误。
         */
        fun onScanError(error: VirusScanError)

        /** 扫描被用户取消。 */
        fun onScanCanceled()

        /** 扫描被系统或引擎中断。 */
        fun onScanInterrupt()

        /** 扫描正常完成。 */
        fun onScanFinished()
    }
}
