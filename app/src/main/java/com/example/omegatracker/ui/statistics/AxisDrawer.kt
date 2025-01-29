package com.example.omegatracker.ui.statistics

import android.graphics.Canvas

class AxisDrawer(private val paints: Paints) {

    private val textPaint = paints.createTextPaint()
    private val dashedLinePaint = paints.createDashedLinePaint()
    private val circlePaint = paints.createCirclePaint()
    private val innerCirclePaint = paints.createInnerCircle()

    private val paddingLeft = 150f
    private val paddingRight = 100f
    private val paddingTop = 100f
    private val paddingBottom = 200f
    private val marginOfError = 10

    private val startXLine = 90f
    private val axisLineOffset = 110f
    private val countPoints = 6
    private val path = android.graphics.Path()

    fun drawX(
        canvas: Canvas,
        labels: List<String>,
        width: Float,
        height: Float
    ): Float {
        val xStep = ((width - paddingRight) - paddingLeft) / countPoints
        val graphWidth = labels.size * xStep + paddingLeft + paddingRight
        labels.forEachIndexed { index, label ->
            val x = paddingLeft + index * xStep
            canvas.drawText(label, x + startXLine + 5, (height - paddingBottom) + axisLineOffset, textPaint)
        }
        return graphWidth
    }

    fun drawY(canvas: Canvas, labels: List<String>, height: Float, graphWidth: Float, scrollX: Float) {
        val yStep = ((height - paddingBottom) - paddingTop) / labels.size
        println("drawY ystep = $yStep")
        labels.forEachIndexed { index, label ->
            val y = (height - paddingBottom) - index * yStep
            println("drawY = $y")
            canvas.drawLine(
                paddingLeft + startXLine + scrollX,
                y,
                graphWidth - paddingRight,
                y,
                dashedLinePaint
            )
            canvas.drawText(
                label,
                paddingLeft - paddingRight + scrollX,
                y + marginOfError,
                textPaint
            )
        }
    }

    //HoursMainLine

    // WeekMainLine
    fun drawWeekMainLine(
        canvas: Canvas,
        timeAndDays: List<Triple<Int, Long, Long>>,
        graphWidth: Float,
        width: Float,
        height: Float
    ) {
        val mainLinePaint = paints.createMainLinePaint(startXLine, width)
        if (timeAndDays.isEmpty()) return

        val xStep = calculateXStep(width)

        val availableHeight = calculateAvailableHeight(height)

        val maxDurationsByDay = calculateMaxDurationsByDay(timeAndDays)

        val maxDuration = maxDurationsByDay.values.maxOrNull() ?: 1L
        val yStep = calculateYStep(availableHeight, maxDuration)

        path.reset()
        var (previousX, previousY) = 0f to 0f
        var (maxX, maxY) = 0f to Float.MAX_VALUE
        var isFirstPoint = true

        (0 until countPoints).forEach { dayOfWeek ->
            val duration = maxDurationsByDay[dayOfWeek] ?: 0L
            val currentX = calculateCurrentX(dayOfWeek, xStep)
            val currentY = calculateCurrentY(duration, yStep, height)

            if (currentY < maxY) {
                maxX = currentX
                maxY = currentY
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
        drawLastPoint(graphWidth, maxDurationsByDay, yStep, height)

        canvas.drawPath(path, mainLinePaint)
        drawCircleAtMaxPoint(canvas, maxX, maxY, height, xStep)
    }

    //step X
    private fun calculateXStep(width: Float): Float {
        return ((width - paddingRight) - paddingLeft) / countPoints
    }

    //height for mainLine
    private fun calculateAvailableHeight(height: Float): Float {
        return (height - paddingBottom) - paddingTop
    }

    //calculate max duration in week
    private fun calculateMaxDurationsByDay(timeAndDays: List<Triple<Int, Long, Long>>): Map<Int, Long> {
        return timeAndDays
            .groupBy { it.first }
            .mapValues { (_, values) -> values.maxOf { it.third - it.second } }
            .mapValues { (_, maxDuration) -> roundDurationToStep(maxDuration) }
    }

    //step Y
    private fun calculateYStep(availableHeight: Float, maxDuration: Long): Float {
        return (availableHeight - paddingTop + marginOfError.toFloat()) / maxDuration.toFloat()
    }

    //current X point
    private fun calculateCurrentX(dayOfWeek: Int, xStep: Float): Float {
        return (paddingLeft + dayOfWeek * (xStep + 5)) + startXLine + 10
    }

    //current Y point
    private fun calculateCurrentY(duration: Long, yStep: Float, height: Float): Float {
        return (height - paddingBottom) - duration * yStep
    }

    //line bends
    private fun drawSmoothCurve(previousX: Float, previousY: Float, currentX: Float, currentY: Float) {
        val controlX1 = previousX + (currentX - previousX) / 3
        val controlX2 = currentX - (currentX - previousX) / 3

        path.cubicTo(controlX1, previousY, controlX2, currentY, currentX, currentY)
    }
    //last points
    private fun drawLastPoint(graphWidth: Float, maxDurationsByDay: Map<Int, Long>, yStep: Float, height: Float) {
        val lastX = graphWidth - paddingRight
        val lastY = (height - paddingBottom) - (maxDurationsByDay[countPoints] ?: 0L) * yStep
        path.lineTo(lastX, lastY)
    }

    //rounding
    private fun roundDurationToStep(durationMillis: Long): Long {
        val step = 30 * 60 * 1000  // 30 минут

        return ((durationMillis + step / 2) / step) * step
    }

    //circle in max point in axis y
    private fun drawCircleAtMaxPoint(canvas: Canvas, x: Float, y: Float, height: Float, xStep: Float) {
        val gradientPaint = paints.createGradientBackgroundCircle(y - paddingTop - axisLineOffset)
        canvas.drawRect(x-(xStep/2), y - paddingTop - axisLineOffset, x + (xStep/2), height - paddingBottom, gradientPaint)

        val radius = 20f
        val whiteRadius = radius * 0.6f

        canvas.drawCircle(x, y, radius, circlePaint)

        canvas.drawCircle(x, y, whiteRadius, innerCirclePaint)
    }


}