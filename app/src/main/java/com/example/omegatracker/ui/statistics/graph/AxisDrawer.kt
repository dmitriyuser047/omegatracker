package com.example.omegatracker.ui.statistics.graph

import android.graphics.Canvas
import com.example.omegatracker.ui.statistics.graph.SettingsGraph.AXIS_LINE_OFFSET
import com.example.omegatracker.ui.statistics.graph.SettingsGraph.MARGIN_ERROR
import com.example.omegatracker.ui.statistics.graph.SettingsGraph.PADDING_BOTTOM
import com.example.omegatracker.ui.statistics.graph.SettingsGraph.PADDING_LEFT
import com.example.omegatracker.ui.statistics.graph.SettingsGraph.PADDING_RIGHT
import com.example.omegatracker.ui.statistics.graph.SettingsGraph.PADDING_TOP
import com.example.omegatracker.ui.statistics.graph.SettingsGraph.START_X_LINE
import com.example.omegatracker.ui.statistics.graph.SettingsGraph.getEffectiveGraphWidth

class AxisDrawer(private val paints: Paints) {

    var maxHeightY: Float = Float.MAX_VALUE
        private set

    private val textPaint = paints.createTextPaint()
    private val dashedLinePaint = paints.createDashedLinePaint()

    fun drawXAxis(canvas: Canvas, labels: List<String>, width: Float, height: Float): Float {
        val countIntervals = 6
        val xStep = getEffectiveGraphWidth(width) / countIntervals
        val graphWidth = labels.size * xStep + PADDING_LEFT + PADDING_RIGHT
        labels.forEachIndexed { index, label ->
            val x = PADDING_LEFT + index * xStep
            canvas.drawText(label, x + START_X_LINE + 5, (height - PADDING_BOTTOM) + AXIS_LINE_OFFSET, textPaint)
        }
        return graphWidth
    }

    fun drawYAxis(canvas: Canvas, labels: List<String>, height: Float, graphWidth: Float, scrollX: Float) {
        val yStep = (height - PADDING_BOTTOM - PADDING_TOP) / labels.size
        labels.forEachIndexed { index, label ->
            val y = (height - PADDING_BOTTOM) - index * yStep
            canvas.drawLine(PADDING_LEFT + START_X_LINE + scrollX, y, graphWidth - PADDING_RIGHT, y, dashedLinePaint)
            canvas.drawText(label, PADDING_LEFT - PADDING_RIGHT + scrollX, y + MARGIN_ERROR, textPaint)
            if (y < maxHeightY) {
                maxHeightY = y
            }
        }
    }
}
