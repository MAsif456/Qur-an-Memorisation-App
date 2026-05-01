package com.example.mob_dev_portfolio.ui.recitation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.mob_dev_portfolio.R

/**
 * Bar-style amplitude visualiser. Holds a small ring buffer of 0..100
 * samples; the recorder pushes samples roughly every 60ms.
 */
class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val barCount = DEFAULT_BAR_COUNT
    private val amplitudes = IntArray(barCount)
    private var writeIndex = 0

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.brand_primary)
        style = Paint.Style.FILL
    }

    /** Append a 0..100 sample and request redraw. */
    fun setAmplitude(value: Int) {
        amplitudes[writeIndex] = value.coerceIn(0, MAX_AMPLITUDE)
        writeIndex = (writeIndex + 1) % barCount
        invalidate()
    }

    /** Clear the buffer and redraw a flat baseline. */
    fun reset() {
        amplitudes.fill(0)
        writeIndex = 0
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val totalWidth = width.toFloat()
        val totalHeight = height.toFloat()
        if (totalWidth <= 0f || totalHeight <= 0f) return
        val barWidth = totalWidth / barCount
        val gap = barWidth * BAR_GAP_FRACTION
        val drawWidth = barWidth - gap
        val midY = totalHeight / 2f
        for (i in 0 until barCount) {
            val sourceIndex = (writeIndex + i) % barCount
            val amp = amplitudes[sourceIndex]
            val barHalf = (amp / MAX_AMPLITUDE.toFloat()) * (totalHeight / 2f)
            val x = i * barWidth + gap / 2f
            canvas.drawRoundRect(
                x,
                midY - barHalf,
                x + drawWidth,
                midY + barHalf,
                drawWidth / 2f,
                drawWidth / 2f,
                barPaint,
            )
        }
    }

    private companion object {
        const val DEFAULT_BAR_COUNT = 48
        const val MAX_AMPLITUDE = 100
        const val BAR_GAP_FRACTION = 0.3f
    }
}
