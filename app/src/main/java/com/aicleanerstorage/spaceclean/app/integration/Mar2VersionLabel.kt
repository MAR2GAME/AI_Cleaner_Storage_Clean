package com.aicleanerstorage.spaceclean.app.integration

import com.mar2sdk.core.Core
import com.mar2sdk.core.common.RiskUtil
import com.mar2sdk.core.common.status.UserType

fun mar2VersionLabel(appVersionName: String): String {
    val snapshot = RiskUtil.getPolicySourceSnapshot()
    val type =
        snapshot.serverAdPolicyUserType.takeUnless { it == UserType.UNKNOW }
            ?: snapshot.localAdPolicyUserType
    val source =
        when {
            snapshot.serverAdPolicyUserType != UserType.UNKNOW -> "S"
            type != UserType.UNKNOW -> "L"
            else -> "U"
        }
    val policy =
        when (type) {
            UserType.RISK -> "R"
            UserType.COMMON -> "C"
            UserType.NATURE -> "N"
            UserType.HIGH_VALUE -> "H"
            UserType.UNKNOW -> "U"
        }
    return "${appVersionName}_${Core.SDK_VERSION}_${source}_$policy"
}
