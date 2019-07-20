package com.asp.fliptimerviewlibrary

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_countdown_clock_digit.view.*
import kotlinx.android.synthetic.main.view_simple_clock.view.*
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit


class CountDownClock : LinearLayout {
    private var countDownTimer: CountDownTimer? = null
    private var countdownListener: CountdownCallBack? = null
    private var countdownTickInterval = 1000

    private var almostFinishedCallbackTimeInSeconds: Int = 5

    private var resetSymbol: String = "8"

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        View.inflate(context, R.layout.view_simple_clock, this)

        attrs?.let {
            val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.CountDownClock, defStyleAttr, 0)
            val resetSymbol = typedArray?.getString(R.styleable.CountDownClock_resetSymbol)
            if (resetSymbol != null) {
                setResetSymbol(resetSymbol)
            }

            val digitTopDrawable = typedArray?.getDrawable(R.styleable.CountDownClock_digitTopDrawable)
            setDigitTopDrawable(digitTopDrawable)
            val digitBottomDrawable = typedArray?.getDrawable(R.styleable.CountDownClock_digitBottomDrawable)
            setDigitBottomDrawable(digitBottomDrawable)
            val digitDividerColor = typedArray?.getColor(R.styleable.CountDownClock_digitDividerColor, 0)
            setDigitDividerColor(digitDividerColor ?: 0)
            val digitSplitterColor = typedArray?.getColor(R.styleable.CountDownClock_digitSplitterColor, 0)
            setDigitSplitterColor(digitSplitterColor ?: 0)

            val digitTextColor = typedArray?.getColor(R.styleable.CountDownClock_digitTextColor, 0)
            setDigitTextColor(digitTextColor ?: 0)

            val digitTextSize = typedArray?.getDimension(R.styleable.CountDownClock_digitTextSize, 0f)
            setDigitTextSize(digitTextSize ?: 0f)
            setSplitterDigitTextSize(digitTextSize ?: 0f)

            val digitPadding = typedArray?.getDimension(R.styleable.CountDownClock_digitPadding, 0f)
            setDigitPadding(digitPadding?.toInt() ?: 0)

            val splitterPadding = typedArray?.getDimension(R.styleable.CountDownClock_splitterPadding, 0f)
            setSplitterPadding(splitterPadding?.toInt() ?: 0)

            val halfDigitHeight = typedArray?.getDimensionPixelSize(R.styleable.CountDownClock_halfDigitHeight, 0)
            val digitWidth = typedArray?.getDimensionPixelSize(R.styleable.CountDownClock_digitWidth, 0)
            setHalfDigitHeightAndDigitWidth(halfDigitHeight ?: 0, digitWidth ?: 0)

            val animationDuration = typedArray?.getInt(R.styleable.CountDownClock_animationDuration, 0)
            setAnimationDuration(animationDuration ?: 600)

            val almostFinishedCallbackTimeInSeconds = typedArray?.getInt(R.styleable.CountDownClock_almostFinishedCallbackTimeInSeconds, 5)
            setAlmostFinishedCallbackTimeInSeconds(almostFinishedCallbackTimeInSeconds ?: 5)

            val countdownTickInterval = typedArray?.getInt(R.styleable.CountDownClock_countdownTickInterval, 1000)
            this.countdownTickInterval = countdownTickInterval ?: 1000

            val greatestVisibleDigit = typedArray?.getInteger(R.styleable.CountDownClock_greatestVisibleDigit, 0)
            setGreatestVisibleDigit(greatestVisibleDigit ?: 0)

            invalidate()
            typedArray?.recycle()
        }
    }

    ////////////////
    // Public methods
    ////////////////

    private var milliLeft: Long = 0

    fun startCountDown(timeToNextEvent: Long) {
        countDownTimer?.cancel()
        var hasCalledAlmostFinished = false
        countDownTimer = object : CountDownTimer(timeToNextEvent, countdownTickInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                milliLeft = millisUntilFinished
                if (millisUntilFinished / 1000 <= almostFinishedCallbackTimeInSeconds && !hasCalledAlmostFinished) {
                    hasCalledAlmostFinished = true
                    countdownListener?.countdownAboutToFinish()
                }
                setCountDownTime(millisUntilFinished)
            }

            override fun onFinish() {
                hasCalledAlmostFinished = false
                countdownListener?.countdownFinished()
            }
        }
        countDownTimer?.start()
    }

    fun resetCountdownTimer() {
        countDownTimer?.cancel()
        firstDigitDays.setNewText(resetSymbol)
        secondDigitDays.setNewText(resetSymbol)
        firstDigitHours.setNewText(resetSymbol)
        secondDigitHours.setNewText(resetSymbol)
        firstDigitMinute.setNewText(resetSymbol)
        secondDigitMinute.setNewText(resetSymbol)
        firstDigitSecond.setNewText(resetSymbol)
        secondDigitSecond.setNewText(resetSymbol)
    }

    ////////////////
    // Private methods
    ////////////////

    private fun setCountDownTime(timeToStart: Long) {

        val days = TimeUnit.MILLISECONDS.toDays(timeToStart)
        val hours = TimeUnit.MILLISECONDS.toHours(timeToStart - TimeUnit.DAYS.toMillis(days))
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeToStart - (TimeUnit.DAYS.toMillis(days) + TimeUnit.HOURS.toMillis(hours)))
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeToStart - (TimeUnit.DAYS.toMillis(days) + TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes)))

        val daysString = days.toString()
        val hoursString = hours.toString()
        val minutesString = minutes.toString()
        val secondsString = seconds.toString()


        when {
            daysString.length == 2 -> {
                firstDigitDays.animateTextChange((daysString[0].toString()))
                secondDigitDays.animateTextChange((daysString[1].toString()))
            }
            daysString.length == 1 -> {
                firstDigitDays.animateTextChange(("0"))
                secondDigitDays.animateTextChange((daysString[0].toString()))
            }
            else -> {
                firstDigitDays.animateTextChange(("3"))
                secondDigitDays.animateTextChange(("0"))
            }
        }

        when {
            hoursString.length == 2 -> {
                firstDigitHours.animateTextChange((hoursString[0].toString()))
                secondDigitHours.animateTextChange((hoursString[1].toString()))
            }
            hoursString.length == 1 -> {
                firstDigitHours.animateTextChange(("0"))
                secondDigitHours.animateTextChange((hoursString[0].toString()))
            }
            else -> {
                firstDigitHours.animateTextChange(("1"))
                secondDigitHours.animateTextChange(("1"))
            }
        }

        when {
            minutesString.length == 2 -> {
                firstDigitMinute.animateTextChange((minutesString[0].toString()))
                secondDigitMinute.animateTextChange((minutesString[1].toString()))
            }
            minutesString.length == 1 -> {
                firstDigitMinute.animateTextChange(("0"))
                secondDigitMinute.animateTextChange((minutesString[0].toString()))
            }
            else -> {
                firstDigitMinute.animateTextChange(("5"))
                secondDigitMinute.animateTextChange(("9"))
            }
        }
        when {
            secondsString.length == 2 -> {
                firstDigitSecond.animateTextChange((secondsString[0].toString()))
                secondDigitSecond.animateTextChange((secondsString[1].toString()))
            }
            secondsString.length == 1 -> {
                firstDigitSecond.animateTextChange(("0"))
                secondDigitSecond.animateTextChange((secondsString[0].toString()))
            }
            else -> {
                firstDigitSecond.animateTextChange((secondsString[secondsString.length - 2].toString()))
                secondDigitSecond.animateTextChange((secondsString[secondsString.length - 1].toString()))
            }
        }
    }

    private fun setResetSymbol(resetSymbol: String?) {
        resetSymbol?.let {
            if (it.isNotEmpty()) {
                this.resetSymbol = resetSymbol
            } else {
                this.resetSymbol = ""
            }
        } ?: kotlin.run {
            this.resetSymbol = ""
        }
    }

    private fun setDigitTopDrawable(digitTopDrawable: Drawable?) {
        if (digitTopDrawable != null) {
            firstDigitDays.frontUpper.background = digitTopDrawable
            firstDigitDays.backUpper.background = digitTopDrawable
            secondDigitDays.frontUpper.background = digitTopDrawable
            secondDigitDays.backUpper.background = digitTopDrawable
            firstDigitHours.frontUpper.background = digitTopDrawable
            firstDigitHours.backUpper.background = digitTopDrawable
            secondDigitHours.frontUpper.background = digitTopDrawable
            secondDigitHours.backUpper.background = digitTopDrawable
            firstDigitMinute.frontUpper.background = digitTopDrawable
            firstDigitMinute.backUpper.background = digitTopDrawable
            secondDigitMinute.frontUpper.background = digitTopDrawable
            secondDigitMinute.backUpper.background = digitTopDrawable
            firstDigitSecond.frontUpper.background = digitTopDrawable
            firstDigitSecond.backUpper.background = digitTopDrawable
            secondDigitSecond.frontUpper.background = digitTopDrawable
            secondDigitSecond.backUpper.background = digitTopDrawable
        } else {
            setTransparentBackgroundColor()
        }
    }

    private fun setDigitBottomDrawable(digitBottomDrawable: Drawable?) {
        if (digitBottomDrawable != null) {
            firstDigitDays.frontLower.background = digitBottomDrawable
            firstDigitDays.backLower.background = digitBottomDrawable
            secondDigitDays.frontLower.background = digitBottomDrawable
            secondDigitDays.backLower.background = digitBottomDrawable
            firstDigitHours.frontLower.background = digitBottomDrawable
            firstDigitHours.backLower.background = digitBottomDrawable
            secondDigitHours.frontLower.background = digitBottomDrawable
            secondDigitHours.backLower.background = digitBottomDrawable
            firstDigitMinute.frontLower.background = digitBottomDrawable
            firstDigitMinute.backLower.background = digitBottomDrawable
            secondDigitMinute.frontLower.background = digitBottomDrawable
            secondDigitMinute.backLower.background = digitBottomDrawable
            firstDigitSecond.frontLower.background = digitBottomDrawable
            firstDigitSecond.backLower.background = digitBottomDrawable
            secondDigitSecond.frontLower.background = digitBottomDrawable
            secondDigitSecond.backLower.background = digitBottomDrawable
        } else {
            setTransparentBackgroundColor()
        }
    }

    private fun setDigitDividerColor(digitDividerColor: Int) {
        var dividerColor = digitDividerColor
        if (dividerColor == 0) {
            dividerColor = ContextCompat.getColor(context, R.color.transparent)
        }

        firstDigitDays.digitDivider.setBackgroundColor(dividerColor)
        secondDigitDays.digitDivider.setBackgroundColor(dividerColor)
        firstDigitHours.digitDivider.setBackgroundColor(dividerColor)
        secondDigitHours.digitDivider.setBackgroundColor(dividerColor)
        firstDigitMinute.digitDivider.setBackgroundColor(dividerColor)
        secondDigitMinute.digitDivider.setBackgroundColor(dividerColor)
        firstDigitSecond.digitDivider.setBackgroundColor(dividerColor)
        secondDigitSecond.digitDivider.setBackgroundColor(dividerColor)
    }

    private fun setDigitSplitterColor(digitsSplitterColor: Int) {
        if (digitsSplitterColor != 0) {
            //  digitsSplitter.setTextColor(digitsSplitterColor)
        } else {
            // digitsSplitter.setTextColor(ContextCompat.getColor(context, R.color.transparent))
        }
    }

    private fun setSplitterDigitTextSize(digitsTextSize: Float) {
        //digitsSplitter.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
    }

    private fun setDigitPadding(digitPadding: Int) {

        firstDigitDays.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        secondDigitDays.setPadding(digitPadding, digitPadding, digitPadding , digitPadding)
        firstDigitHours.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        secondDigitHours.setPadding(digitPadding, digitPadding, digitPadding , digitPadding)

        firstDigitMinute.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        secondDigitMinute.setPadding(digitPadding, digitPadding, digitPadding , digitPadding)
        firstDigitSecond.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        secondDigitSecond.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
    }

    private fun setSplitterPadding(splitterPadding: Int) {
        //digitsSplitter.setPadding(splitterPadding, 0, splitterPadding, 0)
    }

    private fun setDigitTextColor(digitsTextColor: Int) {
        var textColor = digitsTextColor
        if (textColor == 0) {
            textColor = ContextCompat.getColor(context, R.color.transparent)
        }

        firstDigitDays.frontUpperText.setTextColor(textColor)
        firstDigitDays.backUpperText.setTextColor(textColor)
        firstDigitHours.frontUpperText.setTextColor(textColor)
        firstDigitHours.backUpperText.setTextColor(textColor)
        secondDigitDays.frontUpperText.setTextColor(textColor)
        secondDigitDays.backUpperText.setTextColor(textColor)
        secondDigitHours.frontUpperText.setTextColor(textColor)
        secondDigitHours.backUpperText.setTextColor(textColor)

        firstDigitMinute.frontUpperText.setTextColor(textColor)
        firstDigitMinute.backUpperText.setTextColor(textColor)
        secondDigitMinute.frontUpperText.setTextColor(textColor)
        secondDigitMinute.backUpperText.setTextColor(textColor)
        firstDigitSecond.frontUpperText.setTextColor(textColor)
        firstDigitSecond.backUpperText.setTextColor(textColor)
        secondDigitSecond.frontUpperText.setTextColor(textColor)
        secondDigitSecond.backUpperText.setTextColor(textColor)


        firstDigitDays.frontLowerText.setTextColor(textColor)
        firstDigitDays.backLowerText.setTextColor(textColor)

        firstDigitHours.frontLowerText.setTextColor(textColor)
        firstDigitHours.backLowerText.setTextColor(textColor)

        secondDigitDays.frontLowerText.setTextColor(textColor)
        secondDigitDays.backLowerText.setTextColor(textColor)

        secondDigitHours.frontLowerText.setTextColor(textColor)
        secondDigitHours.backLowerText.setTextColor(textColor)

        firstDigitMinute.frontLowerText.setTextColor(textColor)
        firstDigitMinute.backLowerText.setTextColor(textColor)
        secondDigitMinute.frontLowerText.setTextColor(textColor)
        secondDigitMinute.backLowerText.setTextColor(textColor)
        firstDigitSecond.frontLowerText.setTextColor(textColor)
        firstDigitSecond.backLowerText.setTextColor(textColor)
        secondDigitSecond.frontLowerText.setTextColor(textColor)
        secondDigitSecond.backLowerText.setTextColor(textColor)
    }

    private fun setDigitTextSize(digitsTextSize: Float) {

        firstDigitDays.frontUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitDays.backUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitDays.frontUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitDays.backUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitHours.frontUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitHours.backUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitHours.frontUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitHours.backUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitMinute.frontUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitMinute.backUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitMinute.frontUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitMinute.backUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitSecond.frontUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitSecond.backUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitSecond.frontUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitSecond.backUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitDays.frontLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitDays.backLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitDays.frontLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitDays.backLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitHours.frontLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitHours.backLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitHours.frontLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitHours.backLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitMinute.frontLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitMinute.backLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitMinute.frontLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitMinute.backLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitSecond.frontLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        firstDigitSecond.backLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitSecond.frontLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        secondDigitSecond.backLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
    }

    private fun setHalfDigitHeightAndDigitWidth(halfDigitHeight: Int, digitWidth: Int) {
        setHeightAndWidthToView(firstDigitDays.frontUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitDays.backUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitDays.frontUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitDays.backUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitHours.frontUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitHours.backUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitHours.frontUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitHours.backUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitMinute.frontUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitMinute.backUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitMinute.frontUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitMinute.backUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitSecond.frontUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitSecond.backUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitSecond.frontUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitSecond.backUpper, halfDigitHeight, digitWidth)

        // Lower
        setHeightAndWidthToView(firstDigitDays.frontLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitDays.backLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitDays.frontLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitDays.backLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitHours.frontLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitHours.backLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitHours.frontLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitHours.backLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitMinute.frontLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitMinute.backLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitMinute.frontLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitMinute.backLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitSecond.frontLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(firstDigitSecond.backLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitSecond.frontLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(secondDigitSecond.backLower, halfDigitHeight, digitWidth)

        // Dividers
        firstDigitDays.digitDivider.layoutParams.width = digitWidth
        secondDigitDays.digitDivider.layoutParams.width = digitWidth
        firstDigitHours.digitDivider.layoutParams.width = digitWidth
        secondDigitHours.digitDivider.layoutParams.width = digitWidth
        firstDigitMinute.digitDivider.layoutParams.width = digitWidth
        secondDigitMinute.digitDivider.layoutParams.width = digitWidth
        firstDigitSecond.digitDivider.layoutParams.width = digitWidth
        secondDigitSecond.digitDivider.layoutParams.width = digitWidth
    }

    private fun setHeightAndWidthToView(view: View, halfDigitHeight: Int, digitWidth: Int) {
        val firstDigitMinuteFrontUpperLayoutParams = view.layoutParams
        firstDigitMinuteFrontUpperLayoutParams.height = halfDigitHeight
        firstDigitMinuteFrontUpperLayoutParams.width = digitWidth
        firstDigitDays.frontUpper.layoutParams = firstDigitMinuteFrontUpperLayoutParams
    }

    private fun setAnimationDuration(animationDuration: Int) {
        firstDigitDays.setAnimationDuration(animationDuration.toLong())
        secondDigitDays.setAnimationDuration(animationDuration.toLong())
        firstDigitHours.setAnimationDuration(animationDuration.toLong())
        secondDigitHours.setAnimationDuration(animationDuration.toLong())
        firstDigitMinute.setAnimationDuration(animationDuration.toLong())
        secondDigitMinute.setAnimationDuration(animationDuration.toLong())
        firstDigitSecond.setAnimationDuration(animationDuration.toLong())
        secondDigitSecond.setAnimationDuration(animationDuration.toLong())
    }

    private fun setAlmostFinishedCallbackTimeInSeconds(almostFinishedCallbackTimeInSeconds: Int) {
        this.almostFinishedCallbackTimeInSeconds = almostFinishedCallbackTimeInSeconds
    }

    private fun setTransparentBackgroundColor() {
        val transparent = ContextCompat.getColor(context, R.color.transparent)
        firstDigitDays.frontLower.setBackgroundColor(transparent)
        firstDigitDays.backLower.setBackgroundColor(transparent)
        secondDigitDays.frontLower.setBackgroundColor(transparent)
        secondDigitDays.backLower.setBackgroundColor(transparent)
        firstDigitHours.frontLower.setBackgroundColor(transparent)
        firstDigitHours.backLower.setBackgroundColor(transparent)
        secondDigitHours.frontLower.setBackgroundColor(transparent)
        secondDigitHours.backLower.setBackgroundColor(transparent)
        firstDigitMinute.frontLower.setBackgroundColor(transparent)
        firstDigitMinute.backLower.setBackgroundColor(transparent)
        secondDigitMinute.frontLower.setBackgroundColor(transparent)
        secondDigitMinute.backLower.setBackgroundColor(transparent)
        firstDigitSecond.frontLower.setBackgroundColor(transparent)
        firstDigitSecond.backLower.setBackgroundColor(transparent)
        secondDigitSecond.frontLower.setBackgroundColor(transparent)
        secondDigitSecond.backLower.setBackgroundColor(transparent)
    }

    ////////////////
    // Listeners
    ////////////////

     fun setCountdownListener(countdownListener: CountdownCallBack) {
        this.countdownListener = countdownListener
    }

    interface CountdownCallBack {
        fun countdownAboutToFinish()
        fun countdownFinished()
    }


    fun pauseCountDownTimer() {
        countDownTimer?.cancel()
    }

     fun resumeCountDownTimer() {
        startCountDown(milliLeft)
    }


    fun setCustomTypeface(typeface : Typeface){
        firstDigitDays.setTypeFace(typeface)
        firstDigitDays.setTypeFace(typeface)
        secondDigitDays.setTypeFace(typeface)
        secondDigitDays.setTypeFace(typeface)
        firstDigitHours.setTypeFace(typeface)
        firstDigitHours.setTypeFace(typeface)
        secondDigitHours.setTypeFace(typeface)
        secondDigitHours.setTypeFace(typeface)
        firstDigitMinute.setTypeFace(typeface)
        firstDigitMinute.setTypeFace(typeface)
        secondDigitMinute.setTypeFace(typeface)
        secondDigitMinute.setTypeFace(typeface)
        firstDigitSecond.setTypeFace(typeface)
        firstDigitSecond.setTypeFace(typeface)
        secondDigitSecond.setTypeFace(typeface)
        secondDigitSecond.setTypeFace(typeface)

    }

    fun setGreatestVisibleDigit(greatestVisibleDigit: Int) {
        when (greatestVisibleDigit) {
            0 -> {
                // do nothing, all digits should be visible
            }
            1 -> {
                // days must be invisible
                layoutDays.visibility = View.GONE
            }
            2 -> {
                // days and hours must be invisible
                layoutDays.visibility = View.GONE
                layoutHours.visibility = View.GONE
            }
            3 -> {
                // days, hours and minutes must be invisible
                layoutDays.visibility = View.GONE
                layoutHours.visibility = View.GONE
                layoutMinutes.visibility = View.GONE
            }
            else -> throw IllegalArgumentException("greatestVisibleDigit should be one of {0,1,2,3} but is: $greatestVisibleDigit")
        }
    }

}
