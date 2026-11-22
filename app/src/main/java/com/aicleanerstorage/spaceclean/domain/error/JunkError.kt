package com.aicleanerstorage.spaceclean.domain.error

/** 垃圾扫描/清理流程的失败词汇。 */
sealed interface JunkError {
    /** 扫描器整体失败（权限、IO 或会话被清空）；页面回到可重试状态。 */
    data object ScanFailed : JunkError

    /** 删除在开始前失败，已选文件保留，调用方可提示后重试。 */
    data object CleanFailed : JunkError

    /** 授权回调之后的清理失败，按部分失败汇总，不伪装成全部失败。 */
    data object CleanFailedAfterAuthorization : JunkError

    /** 扫描没有产出任何条目（权限受限或目录为空），页面展示空结果。 */
    data object NoData : JunkError
}
