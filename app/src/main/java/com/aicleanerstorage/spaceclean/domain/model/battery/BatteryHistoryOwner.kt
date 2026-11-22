package com.aicleanerstorage.spaceclean.domain.model.battery

/** 电池采样的持有者；用于多页面/服务之间的引用计数。 */
enum class BatteryHistoryOwner {
    /** 后台采样服务。 */
    Service,

    /** 电池信息页面。 */
    BatteryPage,
}
