package com.lakelab.hsvpicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange

open class Slider @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val density = getContext().resources.displayMetrics.density
    private val fillMargin = 0.3f * density
    private val circleCornerRadius = DEFAULT_CORNER_RADIUS_DP * density

    private val panelWithPadding = RectF()
    private val panel = RectF()

    @FloatRange(from = 0.0, to = 1.0)
    var value = DEFAULT_SLIDER_VALUE
        private set
    private var listener: OnSliderChangedListener? = null

    interface OnSliderChangedListener {
        fun onValueChanged(@FloatRange(from = 0.0, to = 1.0) value: Float)
        fun onValueConfirmed(@FloatRange(from = 0.0, to = 1.0) value: Float)
    }

    private val obtainStyledAttributes = attrs?.let { attributeSet ->
        context?.theme?.obtainStyledAttributes(attributeSet, R.styleable.Slider, 0, 0)
    }

    @ColorInt
    var thumbColor: Int? =
        obtainStyledAttributes?.takeIf { it.hasValue(R.styleable.Slider_thumbColor) }
            ?.getColor(R.styleable.Slider_thumbColor, Color.TRANSPARENT)
        set(value) {
            field = value
            thumbPaint.color = value ?: Color.TRANSPARENT
            invalidate()
        }

    @ColorInt
    var thumbStrokeColor: Int? =
        obtainStyledAttributes?.takeIf { it.hasValue(R.styleable.Slider_thumbStrokeColor) }
            ?.getColor(R.styleable.Slider_thumbStrokeColor, Color.TRANSPARENT)
        set(value) {
            field = value
            thumbStrokePaint.color = value ?: Color.TRANSPARENT
            invalidate()
        }


    var thumbRadius = obtainStyledAttributes
        ?.takeIf { it.hasValue(R.styleable.Slider_thumbRadius) }
        ?.getDimension(R.styleable.Slider_thumbRadius, DEFAULT_THUMB_RADIUS_DP * density)
        ?: DEFAULT_THUMB_RADIUS_DP * density
        set(value) {
            field = value
            invalidate()
        }

    var thumbStrokeWidth = obtainStyledAttributes
        ?.takeIf { it.hasValue(R.styleable.Slider_thumbStrokeWidth) }
        ?.getDimension(R.styleable.Slider_thumbStrokeWidth, DEFAULT_THUMB_STROKE_WIDTH_DP * density)
        ?: DEFAULT_THUMB_STROKE_WIDTH_DP * density
        set(value) {
            field = value
            invalidate()
        }

    var sliderWidth =
        obtainStyledAttributes?.takeIf { it.hasValue(R.styleable.Slider_sliderWidth) }
            ?.getDimension(R.styleable.Slider_sliderWidth, DEFAULT_SLIDER_WIDTH_DP * density)
            ?: DEFAULT_SLIDER_WIDTH_DP * density
        set(value) {
            field = value
            requestLayout()
        }

    private var yThumbPosition = 0f

    private val panelPaint = Paint()
    private val thumbPaint = Paint().apply {
        style = Paint.Style.FILL
        thumbColor?.let { thumbColor -> color = thumbColor }
        isAntiAlias = true
    }

    private val thumbStrokePaint = Paint().apply {
        isAntiAlias = true
        color = thumbStrokeColor ?: Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = thumbStrokeWidth
    }

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        obtainStyledAttributes?.recycle()
    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(panel, circleCornerRadius, circleCornerRadius, panelPaint)
        val xThumbPosition = value * panel.width() + panel.left
        if (thumbStrokeWidth > 0) {
            canvas.drawCircle(
                xThumbPosition, yThumbPosition, thumbRadius + thumbStrokeWidth / 2, thumbStrokePaint
            )
        }
        onBeforeDrawThumb(thumbPaint)
        if (thumbRadius > 0) {
            canvas.drawCircle(xThumbPosition, yThumbPosition, thumbRadius + fillMargin, thumbPaint)
        }
    }


    private fun getValue(x: Float): Float {
        val xInBound = when {
            x < panel.left -> 0f
            x > panel.right -> panel.width()
            else -> x - panel.left
        }
        return xInBound / panel.width()
    }

    private var startTouchPoint = Point()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var update = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startTouchPoint.set(event.x.toInt(), event.y.toInt())
                update = processForThumbMove(event)
            }
            MotionEvent.ACTION_MOVE -> update = processForThumbMove(event)
            MotionEvent.ACTION_UP -> {
                update = processForThumbMove(event)
                listener?.onValueConfirmed(value)
                onValueConfirmed(value)
                performClick()
            }
        }
        if (update) {
            listener?.onValueChanged(value)
            onValueChanged(value)
            invalidate()
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun processForThumbMove(event: MotionEvent): Boolean {
        var update = false
        val startX = startTouchPoint.x
        if (panelWithPadding.contains(startX.toFloat(), startTouchPoint.y.toFloat())) {
            value = getValue(event.x)
            update = true
        }
        return update
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            sliderWidth.coerceAtLeast((thumbRadius + thumbStrokeWidth) * 2)
                .plus(paddingBottom)
                .plus(paddingTop).toInt()
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        panelWithPadding.set(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            (w - paddingRight).toFloat(),
            (h - paddingBottom).toFloat()
        )
        val thumbIsGreaterThanSliderWidth = thumbRadius + thumbStrokeWidth > sliderWidth / 2
        val diffThumbAndSlider = thumbRadius + thumbStrokeWidth - sliderWidth/2

        val left = thumbRadius + thumbStrokeWidth + paddingLeft
        val top =
            paddingTop + (if (thumbIsGreaterThanSliderWidth) diffThumbAndSlider else 0f)
        val right = w - thumbRadius - paddingRight - thumbStrokeWidth
        val bottom =
            h - paddingBottom - (if (thumbIsGreaterThanSliderWidth) diffThumbAndSlider else 0f)
        val width = right - left
        val height = bottom - top

        panel.set(left, top, left + width, top + height)
        yThumbPosition = h.div(2).toFloat()
        onPanelSizeChanged(panelPaint, panel)
    }

    open fun onValueChanged(@FloatRange(from = 0.0, to = 1.0) value: Float) {}
    open fun onValueConfirmed(@FloatRange(from = 0.0, to = 1.0) value: Float) {}
    open fun onPanelSizeChanged(panelPaint: Paint, panel: RectF) {}
    open fun onBeforeDrawThumb(thumbPaint: Paint) {}

    open fun setOnSliderChangedListener(listener: OnSliderChangedListener) {
        this.listener = listener
    }

    @JvmOverloads
    fun setValue(@FloatRange(from = 0.0, to = 1.0) value: Float?, callback: Boolean = false) {
        value ?: return
        this.value = value
        if (callback) {
            listener?.onValueChanged(this.value)
        }
        invalidate()
    }
}