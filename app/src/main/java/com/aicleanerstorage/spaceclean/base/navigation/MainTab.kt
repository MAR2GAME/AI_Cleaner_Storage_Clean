package com.aicleanerstorage.spaceclean.base.navigation

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
 * 主界面 Tab，作为类型化路由 [AppDestination.Main] 的参数参与序列化。
 *
 * Navigation 构建路由 NavType 时会用 `Class.forName(serialName)` 反射查找枚举类，
 * 因此必须用 [Keep] 保留类名，否则 release 混淆后启动即崩溃。
 */
@Keep
@Serializable
enum class MainTab { Home, Files, Toolbox }
