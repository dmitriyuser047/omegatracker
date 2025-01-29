package com.example.omegatracker.ui.statistics

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.SweepGradient
import androidx.core.content.res.ResourcesCompat
import com.example.omegatracker.R

class Paints(private val context: Context) {

    fun createTextPaint(): Paint {
        return Paint().apply {
            color = context.getColor(R.color.gray)
            textSize = 50f
            typeface = ResourcesCompat.getFont(context, R.font.rubik)
        }
    }


    fun createDashedLinePaint(): Paint {
        return Paint().apply {
            color = context.getColor(R.color.light_gray)
            style = Paint.Style.STROKE
            strokeWidth = 4f
            pathEffect = DashPathEffect(floatArrayOf(25f, 20f), 0f)
        }
    }

    fun createMainLinePaint(startXLine: Float, endXLine: Float): Paint {
        return Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 10f

            val linearGradient = LinearGradient(
                startXLine,
                0f,
                endXLine,
                0f,
                intArrayOf(
                    context.getColor(R.color.end),
                    context.getColor(R.color.center),
                    context.getColor(R.color.start)
                ),
                floatArrayOf(0f, 0.7f, 1f),
                Shader.TileMode.CLAMP
            )

            shader = linearGradient
        }
    }

    fun createCirclePaint(): Paint {
        return Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 20f

            val sweepGradient = SweepGradient(
                20f,
                20f,
                intArrayOf(
                    context.getColor(R.color.start),
                    context.getColor(R.color.center),
                    context.getColor(R.color.end),
                ),
                floatArrayOf(
                    0f, 0.5f, 1f
                )
            )

            shader = sweepGradient
        }
    }

    fun createInnerCircle(): Paint {
        return Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
        }
    }

    fun createGradientBackgroundCircle(y: Float): Paint {
        return Paint().apply {
            val sweepGradient = SweepGradient(
                20f,
                y,
                intArrayOf(
                    context.getColor(R.color.start_background_statistics_circle),
                    context.getColor(R.color.center_background_statistics_circle),
                    context.getColor(R.color.end_background_statistics_circle),
                ),
                floatArrayOf(
                    0f, 0.7f, 1f
                )
            )
            shader = sweepGradient
        }
    }

}