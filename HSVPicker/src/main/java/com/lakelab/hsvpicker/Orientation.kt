package com.lakelab.hsvpicker

import androidx.annotation.IntDef
import com.lakelab.hsvpicker.Orientation.Companion.HORIZONTAL
import com.lakelab.hsvpicker.Orientation.Companion.VERTICAL

@IntDef(HORIZONTAL, VERTICAL)
annotation class Orientation {
    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }
}