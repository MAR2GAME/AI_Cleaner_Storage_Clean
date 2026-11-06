package com.aicleanerstorage.spaceclean.core.navigation

interface AppNavigator {
    fun navigate(destination: AppDestination)
    fun replaceCurrent(destination: AppDestination)
    fun navigateTopLevel(tab: MainTab)
    fun resetTo(destination: AppDestination)
    fun backToMain()
    fun back(): Boolean
}
