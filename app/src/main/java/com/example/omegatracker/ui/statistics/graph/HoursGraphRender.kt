package com.example.omegatracker.ui.statistics.graph

import android.graphics.Canvas
import android.graphics.Path
import com.example.omegatracker.ui.statistics.graph.SettingsGraph.AXIS_LINE_OFFSET
import com.example.omegatracker.ui.statistics.graph.SettingsGraph.PADDING_BOTTOM
import com.example.omegatracker.ui.statistics.graph.SettingsGraph.PADDING_TOP
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

class HoursGraphRenderer(
    private val paints: Paints,
    private val hours: List<String>,
    private val timeData: List<Triple<Long, Long, Long>>,
    private val maxHeightY: Float
) {
    private val path = Path()
    private var maxHoursX = 0f
    private var maxHoursY = Float.MAX_VALUE
    private val circlePaint = paints.createCirclePaint()
    private val innerCirclePaint = paints.createInnerCircle()

    private fun calculateMaxDurationsByHours(): Map<Double, Long> {
        val durationsByHour = mutableMapOf<Double, Long>()
        timeData.forEach { (_, startTime, endTime) ->
            val startMinute = TimeUnit.MILLISECONDS.toMinutes(startTime) % (60 * 24)
            val endMinute = TimeUnit.MILLISECONDS.toMinutes(endTime) % (60 * 24)
            hours.forEach { hour ->
                val hourInt = hour.toInt()
                val hourStart = hourInt * 60
                val hourEnd = hourStart + 60
                if (startMinute < hourEnd && endMinute > hourStart) {
                    val overlapStart = max(startMinute.toInt(), hourStart)
                    val overlapEnd = min(endMinute.toInt(), hourEnd)
                    val overlapDuration = overlapEnd - overlapStart
                    val hourFraction = hourInt + (overlapStart % 60) / 100.0
                    durationsByHour[hourFraction] = (durationsByHour[hourFraction] ?: 0L) + overlapDuration.toLong()
                }
            }
        }
        return durationsByHour
    }

    private fun calculateCurrentY(duration: Long, yStep: Float, height: Float): Float {
        var currentY = (height - PADDING_BOTTOM) - duration * yStep
        if (currentY < maxHeightY) currentY = maxHeightY
        return currentY
    }

    fun render(canvas: Canvas, graphWidth: Float, height: Float) {
        if (timeData.isEmpty()) return

        val xStep = SettingsGraph.calculateXStep(graphWidth, hours.size)
        val availableHeight = SettingsGraph.calculateAvailableHeight(height)
        val durationsByHour = calculateMaxDurationsByHours()
        val maxDuration = durationsByHour.values.maxOrNull() ?: 1L
        val yStep = SettingsGraph.calculateYStep(availableHeight, maxDuration)

        path.reset()
        drawHoursLinePoints(durationsByHour, xStep, height, yStep)
        drawLastHoursPoint(graphWidth, durationsByHour, yStep, height)
        canvas.drawPath(path, paints.createMainLinePaint(SettingsGraph.START_X_LINE, graphWidth))
        drawCircleAtMaxPoint(canvas, maxHoursX, maxHoursY, height, xStep)
    }

    private fun drawHoursLinePoints(
        durationsByHour: Map<Double, Long>,
        xStep: Float,
        height: Float,
        yStep: Float
    ) {
        var previousX = 0f
        var previousY = 0f
        var isFirstPoint = true
        // Проходим по всем полным часам в диапазоне
        (hours.first().toInt() until hours.last().toInt() + 1).forEach { hour ->
            val duration = durationsByHour[hour.toDouble()] ?: 0L
            val currentX = SettingsGraph.calculateHoursCurrentX(hour, hours.first().toInt(), xStep)
            val currentY = if (duration == 0L) {
                height - PADDING_BOTTOM
            } else {
                calculateCurrentY(duration, yStep, height)
            }
            if (currentY < maxHoursY) {
                maxHoursX = currentX
                maxHoursY = currentY
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

    private fun drawLastHoursPoint(
        graphWidth: Float,
        durationsByHour: Map<Double, Long>,
        yStep: Float,
        height: Float
    ) {
        val lastHour = hours.last().toInt()
        val lastDuration = durationsByHour[lastHour.toDouble()] ?: 0L
        val lastX = graphWidth - SettingsGraph.PADDING_RIGHT
        val lastY = (height - SettingsGraph.PADDING_BOTTOM) - (lastDuration * yStep)
        path.lineTo(lastX, lastY)
    }

    private fun drawSmoothCurve(previousX: Float, previousY: Float, currentX: Float, currentY: Float) {
        val controlX1 = previousX + (currentX - previousX) / 3
        val controlX2 = currentX - (currentX - previousX) / 3
        path.cubicTo(controlX1, previousY, controlX2, currentY, currentX, currentY)
    }

    private fun drawCircleAtMaxPoint(canvas: Canvas, x: Float, y: Float, height: Float, xStep: Float) {
        val gradientPaint = paints.createGradientBackgroundCircle(y - PADDING_TOP - AXIS_LINE_OFFSET)
        canvas.drawRect(
            x - xStep / 2,
            y - PADDING_TOP - AXIS_LINE_OFFSET,
            x + xStep / 2,
            height - PADDING_BOTTOM,
            gradientPaint
        )
        val radius = 20f
        val whiteRadius = radius * 0.6f
        canvas.drawCircle(x, y, radius, circlePaint)
        canvas.drawCircle(x, y, whiteRadius, innerCirclePaint)
    }
}