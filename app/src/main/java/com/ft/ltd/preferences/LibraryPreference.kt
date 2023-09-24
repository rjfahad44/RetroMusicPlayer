/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.ft.ltd.preferences

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat.SRC_IN
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ft.ltd.appthemehelper.common.prefs.supportv7.ATEDialogPreference
import com.ft.ltd.retromusic.R
import com.ft.ltd.adapter.CategoryInfoAdapter
import com.ft.ltd.retromusic.databinding.PreferenceDialogLibraryCategoriesBinding
import com.ft.ltd.extensions.colorButtons
import com.ft.ltd.extensions.colorControlNormal
import com.ft.ltd.extensions.materialDialog
import com.ft.ltd.extensions.showToast
import com.ft.ltd.model.CategoryInfo
import com.ft.ltd.util.PreferenceUtil


class LibraryPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ATEDialogPreference(context, attrs, defStyleAttr, defStyleRes) {
    init {
        icon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            context.colorControlNormal(),
            SRC_IN
        )
    }
}

class LibraryPreferenceDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = PreferenceDialogLibraryCategoriesBinding.inflate(layoutInflater)

        val categoryAdapter = CategoryInfoAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
            categoryAdapter.attachToRecyclerView(this)
        }

        return materialDialog(R.string.library_categories)
            .setNeutralButton(
                R.string.reset_action
            ) { _, _ ->
                updateCategories(PreferenceUtil.defaultCategories)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.done) { _, _ -> updateCategories(categoryAdapter.categoryInfos) }
            .setView(binding.root)
            .create()
            .colorButtons()
    }

    private fun updateCategories(categories: List<CategoryInfo>) {
        if (getSelected(categories) == 0) return
        if (getSelected(categories) > 5) {
            com.ft.ltd.extensions.showToast(R.string.message_limit_tabs)
            return
        }
        PreferenceUtil.libraryCategory = categories
    }

    private fun getSelected(categories: List<CategoryInfo>): Int {
        var selected = 0
        for (categoryInfo in categories) {
            if (categoryInfo.visible)
                selected++
        }
        return selected
    }

    companion object {
        fun newInstance(): LibraryPreferenceDialog {
            return LibraryPreferenceDialog()
        }
    }
}