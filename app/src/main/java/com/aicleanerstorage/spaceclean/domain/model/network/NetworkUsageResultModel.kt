package com.aicleanerstorage.spaceclean.domain.model.network

/** 流量查询结果：实体加本次查询的范围与降级信息。 */
data class NetworkUsageResultModel(
    val usage: NetworkUsageInfoModel,
    val fallbackApps: List<NetworkUsageAppModel> = emptyList(),
    val isToday: Boolean,
    val needsUsageAccess: Boolean,
)
