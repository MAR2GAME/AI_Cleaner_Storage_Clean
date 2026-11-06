package com.aicleanerstorage.spaceclean.app.intent

import com.aicleanerstorage.spaceclean.core.presentation.intent.UiRequestBroker
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import org.koin.dsl.module

val appIntentModule = module {
    single { UiRequestBroker() }
    single<UiRequestPort> { get<UiRequestBroker>() }
}
