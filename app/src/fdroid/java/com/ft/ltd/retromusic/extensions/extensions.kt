@file:Suppress("UNUSED_PARAMETER", "unused")

package com.ft.ltd.retromusic.extensions

import android.content.Context
import android.view.Menu
import androidx.fragment.app.FragmentActivity

fun Context.setUpMediaRouteButton(menu: Menu) {}

fun FragmentActivity.installLanguageAndRecreate(code: String, onInstallComplete: () -> Unit) {
    onInstallComplete()
}

fun Context.goToProVersion() {}

fun Context.installSplitCompat() {}