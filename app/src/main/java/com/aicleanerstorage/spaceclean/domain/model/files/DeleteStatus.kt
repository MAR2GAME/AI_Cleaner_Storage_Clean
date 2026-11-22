package com.aicleanerstorage.spaceclean.domain.model.files

/**
 * 单个文件的删除结果。
 *
 * 页面按 [Deleted] 与 [AuthorizationRequired] 区分"已删"和"待授权"，
 * [AlreadyAbsent] 视为完成；[Changed] / [Failed] 要保留在选中列表里让用户重试。
 */
enum class DeleteStatus {
    Deleted,
    AuthorizationRequired,
    AlreadyAbsent,
    Changed,
    Unknown,
    Failed,
}
