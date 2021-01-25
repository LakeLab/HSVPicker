package com.lakelab.hsvpicker

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader.TileMode
import android.util.AttributeSet
import androidx.annotation.FloatRange

class HueSlider @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Slider(context, attrs, defStyle) {

    private val hueColors = intArrayOf(
        Color.HSVToColor(floatArrayOf(0f, 1f, 1f)),
        Color.HSVToColor(floatArrayOf(60f, 1f, 1f)),
        Color.HSVToColor(floatArrayOf(120f, 1f, 1f)),
        Color.HSVToColor(floatArrayOf(180f, 1f, 1f)),
        Color.HSVToColor(floatArrayOf(240f, 1f, 1f)),
        Color.HSVToColor(floatArrayOf(300f, 1f, 1f)),
        Color.HSVToColor(floatArrayOf(360f, 1f, 1f))
    )

    var hue = DEFAULT_HSV_HUE
        private set
    val color: Int
        get() = Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
    private var listener: OnHueChangedListener? = null

    interface OnHueChangedListener {
        fun onHueChanged(@FloatRange(from = 0.0, to = 360.0) hue: Float)
        fun onHueConfirmed(@FloatRange(from = 0.0, to = 360.0) hue: Float)
    }

    fun setOnHueChangedListener(listener: OnHueChangedListener?) {
        this.listener = listener
    }

    @JvmOverloads
    fun setHue(hue: Float?, callback: Boolean = false) {
        hue ?: return
        this.hue = hue
        if (callback) {
            listener?.onHueChanged(this.hue)
        }
        setValue(hue / 360f, callback)
    }

    override fun onBeforeDrawThumb(thumbPaint: Paint) {
        super.onBeforeDrawThumb(thumbPaint)
        if (thumbColor == null)
            thumbPaint.color = Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
    }

    override fun onValueChanged(value: Float) {
        super.onValueChanged(value)
        hue = value * 360f
        listener?.onHueChanged(value * 360f)
    }

    override fun onValueConfirmed(value: Float) {
        super.onValueConfirmed(value)
        listener?.onHueConfirmed(value * 360f)
    }

    override fun onPanelSizeChanged(panelPaint: Paint, panel: RectF) {
        super.onPanelSizeChanged(panelPaint, panel)
        panelPaint.shader = LinearGradient(
            panel.left, 0f, panel.right, 0f, hueColors, null, TileMode.REPEAT
        )
    }
}