package com.example.omegatracker.ui.statistics.graph

import android.graphics.Canvas
import android.graphics.Path
import com.example.omegatracker.ui.statistics.graph.SettingsGraph.PADDING_BOTTOM
import com.example.omegatracker.ui.statistics.graph.SettingsGraph.PADDING_RIGHT

class WeekGraphRenderer(
    private val paints: Paints,
    private val dayData: List<Triple<Int, Long, Long>>,
    private val maxHeightY: Float
) {
    companion object {
        private const val DAYS_COUNT = 6
        private const val STEP_DURATION_MS = 30 * 60 * 1000L
    }

    private val path = Path()
    private var maxDaysX = 0f
    private var maxDaysY = Float.MAX_VALUE
    private val circlePaint = paints.createCirclePaint()
    private val innerCirclePaint = paints.createInnerCircle()

    private fun calculateCurrentY(duration: Long, yStep: Float, height: Float): Float {
        var currentY = (height - PADDING_BOTTOM) - duration * yStep
        if (currentY < maxHeightY) currentY = maxHeightY
        return currentY
    }

    private fun calculateMaxDurationsByDay(): Map<Int, Long> {
        return dayData
            .groupBy { it.first }
            .mapValues { (_, values) -> values.maxOf { it.third - it.second } }
            .mapValues { (_, maxDuration) -> roundDurationToStep(maxDuration) }
    }

    private fun roundDurationToStep(durationMillis: Long): Long {
        return ((durationMillis + STEP_DURATION_MS / 2) / STEP_DURATION_MS) * STEP_DURATION_MS
    }

    fun render(canvas: Canvas, graphWidth: Float, width: Float, height: Float) {
        if (dayData.isEmpty()) return

        val countDays = DAYS_COUNT
        val xStep = SettingsGraph.calculateXStep(width, countDays)
        val availableHeight = SettingsGraph.calculateAvailableHeight(height)
        val maxDurationsByDay = calculateMaxDurationsByDay()
        val maxDuration = maxDurationsByDay.values.maxOrNull() ?: 1L
        val yStep = SettingsGraph.calculateYStep(availableHeight, maxDuration)

        path.reset()
        drawDaysLinePoints(xStep, yStep, height, countDays, maxDurationsByDay)
        drawLastDayPoint(graphWidth, maxDurationsByDay, yStep, height, countDays)
        canvas.drawPath(path, paints.createMainLinePaint(SettingsGraph.START_X_LINE, width))
        drawCircleAtMaxPoint(canvas, maxDaysX, maxDaysY, height, xStep)
    }

    private fun drawDaysLinePoints(
        xStep: Float,
        yStep: Float,
        height: Float,
        countDays: Int,
        maxDurationsByDay: Map<Int, Long>
    ) {
        var previousX = 0f
        var previousY = 0f
        var isFirstPoint = true
        (0 until countDays).forEach { dayOfWeek ->
            val duration = maxDurationsByDay[dayOfWeek] ?: 0L
            val currentX = SettingsGraph.PADDING_LEFT + SettingsGraph.START_X_LINE + dayOfWeek * (xStep + 5) + 10
            val currentY = calculateCurrentY(duration, yStep, height)
            if (currentY < maxDaysY) {
                maxDaysX = currentX
                maxDaysY = currentY
            }
            if (isFirstPoint) {
                path.moveTo(currentX, currentY)
                isFirstPoint = false
            } else {
                drawSmoothCurve(previousX, previousY, currentX, currentY)
            }
            previousX = currentX
            previousY = currentY
        }
    }

    private fun drawLastDayPoint(
        graphWidth: Float,
        maxDurationsByDay: Map<Int, Long>,
        yStep: Float,
        height: Float,
        countPoints: Int
    ) {
        val lastX = graphWidth - PADDING_RIGHT
        val lastY = (height - PADDING_BOTTOM) - (maxDurationsByDay[countPoints] ?: 0L) * yStep
        path.lineTo(lastX, lastY)
    }

    private fun drawCircleAtMaxPoint(canvas: Canvas, x: Float, y: Float, height: Float, xStep: Float) {
        val gradientPaint = paints.createGradientBackgroundCircle(y, height - PADDING_BOTTOM)
        canvas.drawRect(
            x - xStep / 2,
            y,
            x + xStep / 2,
            height - PADDING_BOTTOM,
            gradientPaint
        )
        canvas.drawLine(
            x, y ,
            x,  height - PADDING_BOTTOM,
            paints.createDashedCircleBackgroundLinePaint()
        )
        val radius = 20f
        val whiteRadius = radius * 0.6f
        canvas.drawCircle(x, y, radius, circlePaint)
        canvas.drawCircle(x, y, whiteRadius, innerCirclePaint)
    }

    private fun drawSmoothCurve(previousX: Float, previousY: Float, currentX: Float, currentY: Float) {
        val controlX1 = previousX + (currentX - previousX) / 3
        val controlX2 = currentX - (currentX - previousX) / 3
        path.cubicTo(controlX1, previousY, controlX2, currentY, currentX, currentY)
    }
}