package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.intent

sealed interface NetworkScanOperation {
    data object Start : NetworkScanOperation
}
