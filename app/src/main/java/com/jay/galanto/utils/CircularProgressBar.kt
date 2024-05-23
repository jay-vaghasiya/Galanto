package com.jay.galanto.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.jay.galanto.R

class CircularProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var maxTimeMillisValue = 30000L
    private var currentTimeMillis = maxTimeMillisValue
    private var progressColor = Color.BLUE
    private var textColor = Color.BLACK

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 24f
        color = progressColor
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = 100f
        textAlign = Paint.Align.CENTER
    }
    private val rectF = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {
            val centerX = width / 2f
            val centerY = height / 2f
            val radius = (width - paint.strokeWidth) / 2f

            rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
            drawArc(rectF, -90f, 360f * currentTimeMillis / maxTimeMillisValue, false, paint)

            val text = "${currentTimeMillis / 1000}"
            drawText(
                text,
                centerX,
                centerY - (textPaint.descent() + textPaint.ascent()) / 2,
                textPaint
            )
        }
    }





    fun updateProgress(progressMillis: Long) {
        currentTimeMillis = if (progressMillis > maxTimeMillisValue) {
            maxTimeMillisValue
        } else {
            progressMillis
        }
        invalidate()
    }
    fun updateMaxTimeMillis(maxTimeMillis: Long) {
        this.maxTimeMillisValue = maxTimeMillis
        invalidate()
    }


}
