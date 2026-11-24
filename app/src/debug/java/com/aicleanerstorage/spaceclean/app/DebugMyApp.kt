package com.aicleanerstorage.spaceclean.app

import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.app.di.dataModule
import com.aicleanerstorage.spaceclean.app.di.fakeVirusScanModule
import com.aicleanerstorage.spaceclean.app.di.presentationModule
import org.koin.core.module.Module

/**
 * debug 构建的 Application：只有显式打开 `-PfakeVirusScan=true` 时才追加假扫描器模块。
 *
 * 默认行为和 [MyApp] 完全一致；release 构建不包含本类，也不会打包 testres 的 fixture。
 */
class DebugMyApp : MyApp() {
    override fun appModules(): List<Module> =
        if (BuildConfig.FAKE_VIRUS_SCAN) {
            listOf(dataModule, presentationModule, fakeVirusScanModule)
        } else {
            super.appModules()
        }
}
