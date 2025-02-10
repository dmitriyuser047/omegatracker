package com.example.omegatracker.ui.statistics

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.omegatracker.R
import com.example.omegatracker.ui.statistics.graph.AxisDrawer
import com.example.omegatracker.ui.statistics.graph.HoursGraphRenderer
import com.example.omegatracker.ui.statistics.graph.Paints
import com.example.omegatracker.ui.statistics.graph.WeekGraphRenderer

class StatisticsGraph @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paints = Paints(context)

    private val axisDrawer = AxisDrawer(paints)

    // Рендереры для графиков по часам и неделям – они будут создаваться/обновляться при каждом вызове onDraw
    private var hoursRenderer: HoursGraphRenderer? = null
    private var weekRenderer: WeekGraphRenderer? = null

    private val backgroundDrawable =
        ContextCompat.getDrawable(context, R.drawable.background_statistics_graph)

    private var yLabels = emptyList<String>()
    private var xLabels = emptyList<String>()
    var dayPoints = emptyList<Triple<Int, Long, Long>>()
    var hoursPoints = emptyList<Triple<Long, Long, Long>>()

    private var graphWidth = 0f
    private var lastTouchX = 0f
    private var lastScrollX = 0f
    var isWeek = false

    fun setYLabels(labels: List<String>) {
        yLabels = labels.ifEmpty {
            context.resources.getStringArray(R.array.default_hours_labels).toList()
        }
        invalidate()
    }

    fun setXLabels(labels: List<String>) {
        xLabels = labels
        lastScrollX = 0f
        scrollTo(0, 0)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (graphWidth > width) {
                    val deltaX = event.x - lastTouchX
                    lastScrollX = (lastScrollX - deltaX).coerceIn(0f, graphWidth - width)
                    scrollTo(lastScrollX.toInt(), 0)
                    lastTouchX = event.x
                    invalidate()
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                return true
            }
            else -> return super.onTouchEvent(event)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        backgroundDrawable?.setBounds(
            (scrollX % graphWidth).toInt(),
            0,
            ((scrollX % graphWidth) + width).toInt(),
            height
        )
        backgroundDrawable?.draw(canvas)

        graphWidth = axisDrawer.drawXAxis(canvas, xLabels, width.toFloat(), height.toFloat())
        axisDrawer.drawYAxis(canvas, yLabels, height.toFloat(), graphWidth, scrollX.toFloat())

        if (isWeek) {
            weekRenderer = WeekGraphRenderer(paints, dayPoints, axisDrawer.maxHeightY)
            weekRenderer?.render(canvas, graphWidth, width.toFloat(), height.toFloat())
        } else {
            hoursRenderer = HoursGraphRenderer(paints, xLabels, hoursPoints, axisDrawer.maxHeightY)
            hoursRenderer?.render(canvas, graphWidth, height.toFloat())
        }
    }
}