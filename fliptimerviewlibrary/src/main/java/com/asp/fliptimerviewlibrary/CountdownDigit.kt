package com.asp.fliptimerviewlibrary

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_countdown_clock_digit.view.*

class CountDownDigit : FrameLayout {
    private var animationDuration = 600L

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.view_countdown_clock_digit, this)

        frontUpperText.measure(0, 0)
        frontLowerText.measure(0, 0)
        backUpperText.measure(0, 0)
        backLowerText.measure(0, 0)
    }

    fun setNewText(newText: String) {
        frontUpper.clearAnimation()
        frontLower.clearAnimation()

        frontUpperText.text = newText
        frontLowerText.text = newText
        backUpperText.text = newText
        backLowerText.text = newText
    }

    fun animateTextChange(newText: String) {
        if (backUpperText.text == newText) {
            return
        }

        frontUpper.clearAnimation()
        frontLower.clearAnimation()

        backUpperText.text = newText
        frontUpper.pivotY = frontUpper.bottom.toFloat()
        frontLower.pivotY = frontUpper.top.toFloat()
        frontUpper.pivotX = (frontUpper.right - ((frontUpper.right - frontUpper.left) / 2)).toFloat()
        frontLower.pivotX = (frontUpper.right - ((frontUpper.right - frontUpper.left) / 2)).toFloat()

        frontUpper.animate()
            .setDuration(getHalfOfAnimationDuration())
            .rotationX(-90f)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                frontUpperText.text = backUpperText.text
                frontUpper.rotationX = 0f
                frontLower.rotationX = 90f
                frontLowerText.text = backUpperText.text
                frontLower.animate()
                    .setDuration(getHalfOfAnimationDuration())
                    .rotationX(0f)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction {
                        backLowerText.text = frontLowerText.text
                    }.start()
            }.start()
    }

    fun setAnimationDuration(duration: Long) {
        this.animationDuration = duration
    }

    private fun getHalfOfAnimationDuration(): Long {
        return animationDuration / 2
    }


    fun setTypeFace(typeFace: Typeface) {

        frontUpperText.typeface = typeFace
        frontLowerText.typeface = typeFace
        backUpperText.typeface = typeFace
        backLowerText.typeface = typeFace

    }
}
