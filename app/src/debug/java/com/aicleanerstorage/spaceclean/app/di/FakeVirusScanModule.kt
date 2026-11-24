package com.aicleanerstorage.spaceclean.app.di

import com.aicleanerstorage.spaceclean.data.local.security.FakeVirusScanner
import com.aicleanerstorage.spaceclean.domain.port.VirusScanner
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/** 仅 debug 构建：用假扫描器覆盖真实实现，fixture 来自 `testres/fake-virus-fixtures`。 */
internal val fakeVirusScanModule: Module =
    module {
        single<VirusScanner>(override = true) { FakeVirusScanner(androidContext()) }
    }
