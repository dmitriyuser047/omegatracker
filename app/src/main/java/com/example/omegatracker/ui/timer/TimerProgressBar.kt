package com.example.omegatracker.ui.timer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View

class TimerProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#E9E9FF")
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val progressPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val startColor = Color.parseColor("#C177FF")
    private val centerColor = Color.parseColor("#7012CE")
    private val endColor = Color.parseColor("#6600BA")

    private var thickness = 45f
    private var progress = 0f
    private var maxProgress = 100f

    private lateinit var sweepGradient: SweepGradient

    init {
        backgroundPaint.strokeWidth = thickness
        progressPaint.strokeWidth = thickness
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val centerX = w / 2f
        val centerY = h / 2f
        sweepGradient = SweepGradient(
            centerX, centerY,
            intArrayOf(startColor, centerColor, endColor, startColor),
            floatArrayOf(0.1f, 0.5f, 0.80f, 1f)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Math.min(centerX, centerY) - thickness / 2

        // Background
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        canvas.save()

        // Animation
        canvas.rotate(-90f, centerX, centerY)

        // Progress Line with Gradient
        val progressAngle = 360 * (progress / maxProgress)
        progressPaint.shader = sweepGradient
        canvas.drawArc(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius,
            0f, progressAngle, false, progressPaint
        )

        canvas.restore()
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

}