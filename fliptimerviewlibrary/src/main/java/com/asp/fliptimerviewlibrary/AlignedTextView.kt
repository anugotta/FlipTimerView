package com.asp.fliptimerviewlibrary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView

class AlignedTextView : TextView {
    private var alignment = ProperTextAlignment.TOP
    private val textRect = Rect()

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        attrs?.let {
            val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.AlignedTextView, defStyleAttr, 0)

            val alignment = typedArray?.getInt(R.styleable.AlignedTextView_alignment, 0)
            if (alignment != null && alignment != 0) {
                setAlignment(alignment)
            } else {
                Log.e("AlignedTextView", "You did not set an alignment for an AlignedTextView. Default is top alignment.")
            }

            invalidate()
            typedArray?.recycle()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            canvas.getClipBounds(textRect)
            val cHeight = textRect.height()
            paint.getTextBounds(this.text.toString(), 0, this.text.length, textRect)
            val bottom = textRect.bottom;
            textRect.offset(-textRect.left, -textRect.top)
            paint.textAlign = Paint.Align.CENTER
            var drawY = 0f
            if (alignment == ProperTextAlignment.TOP) {
                drawY = (textRect.bottom - bottom).toFloat() - ((textRect.bottom - textRect.top) / 2)
            } else if (alignment == ProperTextAlignment.BOTTOM) {
                drawY = top + cHeight.toFloat() + ((textRect.bottom - textRect.top) / 2)
            }
            val drawX = (canvas.width / 2).toFloat()
            paint.color = this.currentTextColor
            canvas.drawText(this.text.toString(), drawX, drawY, paint)
        }
    }

    private fun setAlignment(alignment: Int) {
        if (alignment == 1) {
            this.alignment = ProperTextAlignment.TOP
        } else if (alignment == 2) {
            this.alignment = ProperTextAlignment.BOTTOM
        }
    }

    private enum class ProperTextAlignment {
        TOP,
        BOTTOM
    }
}
