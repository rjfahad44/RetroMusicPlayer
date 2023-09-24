package com.ft.ltd.interfaces

import android.view.View
import com.ft.ltd.model.Genre

interface IGenreClickListener {
    fun onClickGenre(genre: Genre, view: View)
}