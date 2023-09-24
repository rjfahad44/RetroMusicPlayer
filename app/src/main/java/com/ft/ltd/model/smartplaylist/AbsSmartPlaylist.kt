package com.ft.ltd.model.smartplaylist

import androidx.annotation.DrawableRes
import com.ft.ltd.retromusic.R
import com.ft.ltd.model.AbsCustomPlaylist

abstract class AbsSmartPlaylist(
    name: String,
    @DrawableRes val iconRes: Int = R.drawable.ic_queue_music
) : AbsCustomPlaylist(
    id = PlaylistIdGenerator(name, iconRes),
    name = name
)