package com.github.lakelab.hsvpicker

import androidx.annotation.IntDef
import com.github.lakelab.hsvpicker.Orientation.Companion.HORIZONTAL
import com.github.lakelab.hsvpicker.Orientation.Companion.VERTICAL

@IntDef(HORIZONTAL, VERTICAL)
annotation class Orientation {
    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }
}