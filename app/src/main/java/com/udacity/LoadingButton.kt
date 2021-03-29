package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var colorPrimaryBackground = context.getColor(R.color.colorPrimary)
    private var colorPrimaryText = context.getColor(R.color.white)
    private var colorsAccentyellow = context.getColor(R.color.colorAccent)
    private var colorInprogressDarkBlue = context.getColor(R.color.colorPrimaryDark)
    private lateinit var buttonText: String
    private var valuesAnimator = ValueAnimator()
    private var InProgress: Float = 0f
    private var endOfProgress: Float = 0f
    private val pathAngle = Path()
    private val radiusAngle = 50f
    private val rectangleText = Rect()
    private val rectangleLoad = Rect()
    private val rectangleCurve = RectF()


    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        // Set the button text depending on the state of the button
        buttonText = context.getString(buttonState.customTextButton)

        when(new) {

            ButtonState.Loading -> {
                if (old != ButtonState.Loading) {
                    SettingColorBacgroundOfButton(colorPrimaryBackground)
                    /* paintCircle.color = context.getColor(R.color.colorAccent)
                valueAnimator = ValueAnimator.ofFloat(0.0f,
                         measuredWidth.toFloat())
                         .setDuration(2000)
                         .apply {
                             addUpdateListener { valueAnimator ->
                                 value = valueAnimator.animatedValue as Float
                                 sweepAngle = value / 8
                                 width = valueAnimator.animatedValue as Float
                                 repeatMode = ValueAnimator.RESTART
                                 repeatCount = ValueAnimator.INFINITE
                                 invalidate()
                             }
                         }
                 valueAnimator.start()*/
                    settingAnimation()
                }
            }

            ButtonState.Completed -> {
                // Reset to default state
          /*      width = 0f
                sweepAngle = 0f
                valueAnimator.cancel()
                value = 0.0f
                invalidate()*/
                settingProgressOfButton(1f)
                SettingColorTextOfButton(colorPrimaryText)

            }
        }
        invalidate()
        requestLayout()
    }





    init {
        buttonState=ButtonState.Clicked
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.LoadingButton,
                defStyleAttr,
                0
        ).apply {

            try {
                colorPrimaryBackground =getColor(R.styleable.LoadingButton_backgroundColorButton,colorPrimaryBackground)
                colorPrimaryText =getColor(R.styleable.LoadingButton_textColor,colorPrimaryText)
                colorInprogressDarkBlue = getColor(R.styleable.LoadingButton_colorOfInProgressBackground, colorInprogressDarkBlue)
                colorsAccentyellow = getColor(R.styleable.LoadingButton_colorOfCircleProgress, colorsAccentyellow)
            }finally {
                recycle()
            }

        }

    }



    // Paint object -coloring and styling Button
    private var paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      // color = context.getColor(R.color.colorPrimary)
        color=colorPrimaryBackground
       // isAntiAlias = true
    }

    private fun SettingColorBacgroundOfButton(colorPrimaryBackground: Int) {
        this.paintButton.color =colorPrimaryBackground

    }


    private var paintLoadingButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.colorPrimaryDark)
        isAntiAlias = true
    }
    // Paint object -coloring and styling Text on the Button
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style =Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 40.0f
        //   color = context.getColor(R.color.white)
     color=colorPrimaryText


        //isAntiAlias = true

        typeface = Typeface.create("", Typeface.BOLD)

    }
    private fun SettingColorTextOfButton(colorPrimaryText: Int) {

        this.paintText.color =colorPrimaryText
    }

    private val colorOfBackgroundInProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorInprogressDarkBlue
    }

    private val colorCurveInprogress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorsAccentyellow
        style = Paint.Style.FILL
    }


    // Paint object -coloring and styling Circle
    private var paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.colorAccent)
        isAntiAlias = true
    }




 /*   private var valueAnimator = ValueAnimator()*/

  /*  var value = 0.0f
    var width = 0.0f
    var sweepAngle = 0.0f*/



    fun settingProgressOfButton(floatProgress: Float) {
        if (InProgress < floatProgress){

            endOfProgress = floatProgress
            settingAnimation()
        }
    }



    fun addingButtonProgress(floatProgress: Float) {
        // if (InProgress < 0.64f){
        settingProgressOfButton(InProgress + floatProgress)
        // }
    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            save()
            clipPath(pathAngle)
            drawColor(paintButton.color)
            paintText.getTextBounds(buttonText, 0, buttonText.length, rectangleText)
            val xText = width / 2f - rectangleText.width() / 2f
            val yText = height / 2f + rectangleText.height() / 2f - rectangleText.bottom
            var offsetText = 0

            rectangleLoad.set(0, 0, (width * InProgress).roundToInt(), height)
            drawRect(rectangleLoad, colorOfBackgroundInProgress)

            if (buttonState == ButtonState.Loading) {
                val startOfCurveX = width / 2f + rectangleText.width() / 2f
                val startOfCurveY = height / 2f - 19
                rectangleCurve.set(startOfCurveX, startOfCurveY, startOfCurveX + 38, startOfCurveY + 38)
                drawArc(
                    rectangleCurve, InProgress, InProgress * 360f, true, paintCircle
                )
                offsetText = 36
            }

            drawText(buttonText, xText - offsetText, yText, paintText)
            restore()
        }
  /*      if (canvas != null) {
            canvas.drawCircle(widthSize/2F, heightSize/2F,radiusAngle,paintCircle)
        }*/
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(width: Int, height: Int, widthOld: Int, heightOld: Int) {
        super.onSizeChanged(width, height, widthOld, heightOld)
        pathAngle.reset()
        pathAngle.addRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            radiusAngle,
            radiusAngle,
            Path.Direction.CW
        )
        pathAngle.close()
    }


    private fun settingAnimation() {
        valuesAnimator.cancel()
        valuesAnimator = ValueAnimator.ofFloat(InProgress, endOfProgress).apply {
            interpolator = AccelerateDecelerateInterpolator()
            // 1.5 second for 100% progress, 750ms for 50% progress, etc.
            val animDuration = abs(1500 * ((endOfProgress - InProgress) / 100)).toLong()
            // set minimum increment of progress duration to 400ms
            duration = if (animDuration >= 400){
                animDuration
            }else{
                400
            }
            addUpdateListener { animation ->
                InProgress = animation.animatedValue as Float
                postInvalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (InProgress == 1f){


                        resetingProgress()
                    }else{
                        valuesAnimator.cancel()
                    }
                }
            })
            start()
        }
    }


    private fun resetingProgress() {
        endOfProgress = 0f


        settingAnimation()
    }

}