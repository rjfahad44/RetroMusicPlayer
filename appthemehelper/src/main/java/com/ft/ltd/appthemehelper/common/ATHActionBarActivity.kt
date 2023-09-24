package com.ft.ltd.appthemehelper.common

import androidx.appcompat.widget.Toolbar

import com.ft.ltd.appthemehelper.util.ToolbarContentTintHelper

class ATHActionBarActivity : ATHToolbarActivity() {

    override fun getATHToolbar(): Toolbar? {
        return ToolbarContentTintHelper.getSupportActionBarView(supportActionBar)
    }
}
