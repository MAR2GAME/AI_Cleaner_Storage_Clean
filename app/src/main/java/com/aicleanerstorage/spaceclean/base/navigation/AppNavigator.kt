package com.aicleanerstorage.spaceclean.base.navigation

/** 页面导航的唯一入口：页面只表达“去哪里”，清栈范围由实现统一决定。 */
interface AppNavigator {
    fun navigate(destination: AppDestination)

    fun replace(destination: AppDestination)

    fun resetToRoot(destination: AppDestination)

    fun back(): Boolean
}
