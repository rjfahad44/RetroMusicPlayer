package com.ft.ltd.extensions

import androidx.core.view.WindowInsetsCompat
import com.ft.ltd.util.PreferenceUtil
import com.ft.ltd.util.RetroUtil

fun WindowInsetsCompat?.getBottomInsets(): Int {
    return if (PreferenceUtil.isFullScreenMode) {
        return 0
    } else {
        this?.getInsets(WindowInsetsCompat.Type.systemBars())?.bottom ?: RetroUtil.navigationBarHeight
    }
}
