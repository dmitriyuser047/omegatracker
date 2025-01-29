package com.example.omegatracker.ui.statistics

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.omegatracker.R


class StatisticsGraph @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paints = Paints(context)

    private val axisDrawer = AxisDrawer(paints)

    private val backgroundDrawable =
        ContextCompat.getDrawable(context, R.drawable.background_statistics_graph)

    private var yLabels = emptyList<String>()
    private var xLabels = emptyList<String>()
    var timePoints = emptyList<Triple<Int, Long, Long>>()

    private var graphWidth = 0f
    private var lastTouchX = 0f
    private var lastScrollX = 0f

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

        graphWidth = axisDrawer.drawX(canvas, xLabels, width.toFloat(), height.toFloat())
        axisDrawer.drawY(canvas, yLabels, height.toFloat(), graphWidth, scrollX.toFloat())
        axisDrawer.drawWeekMainLine(canvas, timePoints, graphWidth,width.toFloat(), height.toFloat())
    }

}