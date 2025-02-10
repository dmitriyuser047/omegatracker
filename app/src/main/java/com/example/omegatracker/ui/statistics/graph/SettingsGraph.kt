package com.example.omegatracker.ui.statistics.graph

object SettingsGraph {
    const val PADDING_LEFT = 150f
    const val PADDING_RIGHT = 100f
    const val PADDING_TOP = 100f
    const val PADDING_BOTTOM = 200f
    const val START_X_LINE = 90f
    const val AXIS_LINE_OFFSET = 110f
    const val MARGIN_ERROR = 10

    fun getEffectiveGraphWidth(width: Float): Float =
        width - PADDING_LEFT - PADDING_RIGHT

    fun calculateXStep(width: Float, countPoints: Int): Float =
        getEffectiveGraphWidth(width) / countPoints

    fun calculateAvailableHeight(height: Float): Float =
        height - PADDING_TOP - PADDING_BOTTOM

    fun calculateYStep(availableHeight: Float, maxDuration: Long): Float =
        availableHeight / maxDuration.toFloat()

    fun calculateHoursCurrentX(hour: Int, startHour: Int, xStep: Float): Float {
        val offsetX = (hour - startHour) * xStep
        return PADDING_LEFT + START_X_LINE + offsetX
    }
}