package com.aicleanerstorage.spaceclean.app.view.antivirus.interaction

import com.aicleanerstorage.spaceclean.data.security.model.VirusScanMode

internal val VirusScanMode.minDurationMillis get() = if (this == VirusScanMode.Quick) 7_000L else 15_000L
internal val VirusScanMode.stepCount get() = if (this == VirusScanMode.Quick) 3 else 4
internal val VirusScanMode.displayUpdateIntervalMillis get() = if (this == VirusScanMode.Quick) 80L else 40L

internal fun VirusScanMode.circleStartThreshold(circleIndex: Int): Float {
    val circleDiameter = 60f
    val connectorWidth = 30f
    val totalWidth = stepCount * circleDiameter + (stepCount - 1) * connectorWidth
    val circleLeft = circleIndex * (circleDiameter + connectorWidth)
    return (circleLeft / totalWidth).coerceIn(0f, 1f)
}

internal fun VirusScanMode.circleFillThreshold(circleIndex: Int): Float {
    val circleDiameter = 60f
    val connectorWidth = 30f
    val totalWidth = stepCount * circleDiameter + (stepCount - 1) * connectorWidth
    val circleRight = circleIndex * (circleDiameter + connectorWidth) + circleDiameter
    return (circleRight / totalWidth).coerceIn(0f, 1f)
}
