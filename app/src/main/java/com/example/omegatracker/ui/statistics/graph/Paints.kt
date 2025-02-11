package com.example.omegatracker.ui.statistics.graph

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import com.example.omegatracker.R
import com.omega_r.libs.extensions.metrics.toDp

class Paints(private val context: Context) {

    fun createTextPaint(): Paint {
        return Paint().apply {
            color = context.getColor(R.color.gray)
            textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16f, context.resources.displayMetrics
            )
            typeface = ResourcesCompat.getFont(context, R.font.rubik)
        }
    }


    fun createDashedLinePaint(): Paint {
        return Paint().apply {
            color = context.getColor(R.color.light_gray)
            style = Paint.Style.STROKE
            strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,2f, context.resources.displayMetrics
            )
            pathEffect = DashPathEffect(floatArrayOf(25f, 20f), 0f)
        }
    }

    fun createDashedCircleBackgroundLinePaint(): Paint {
        return Paint().apply {
            color = context.getColor(R.color.white)
            style = Paint.Style.STROKE
            strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,1f, context.resources.displayMetrics
            )
            pathEffect = DashPathEffect(floatArrayOf(15f, 25f), 0f)
        }
    }

    fun createMainLinePaint(startXLine: Float, endXLine: Float): Paint {
        return Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2f, context.resources.displayMetrics
            )

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
            strokeWidth = 20f.toDp()

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

    fun createGradientBackgroundCircle(y: Float, bottom: Float): Paint {
        return Paint().apply {
            isAntiAlias = true
            shader = LinearGradient(
                0f, bottom,
                0f, y,
                intArrayOf(
                    ColorUtils.setAlphaComponent(context.getColor(R.color.backround_circular), 255),
                    ColorUtils.setAlphaComponent(context.getColor(R.color.backround_circular), 63)
                ),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
        }
    }

}