package com.jay.galanto.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CircularProgress : View {
    private var progress = 0
    private var maxProgress = 100

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.GRAY
        strokeWidth = 20f // Adjust the stroke width as needed
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.BLUE // Change color to whatever you prefer
        strokeWidth = 20f // Should match the stroke width of the circlePaint
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 40f // Adjust text size as needed
    }

    private val oval = RectF()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (Math.min(width, height) - circlePaint.strokeWidth) / 2f

        oval.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)


        canvas.drawArc(oval, 0f, 360f, false, circlePaint)


        val sweepAngle = 360f * progress / maxProgress
        canvas.drawArc(oval, -90f, sweepAngle, false, progressPaint)

        val text = "$progress °"
        val textWidth = textPaint.measureText(text)
        canvas.drawText(text, centerX - textWidth / 2, centerY + textPaint.textSize / 2, textPaint)
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun setMaxProgress(maxProgress: Int) {
        this.maxProgress = maxProgress
    }
}
