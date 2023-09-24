package com.ft.ltd.model.smartplaylist

import com.ft.ltd.App
import com.ft.ltd.retromusic.R
import com.ft.ltd.model.Song
import kotlinx.parcelize.Parcelize

@Parcelize
class ShuffleAllPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.action_shuffle_all),
    iconRes = R.drawable.ic_shuffle
) {
    override fun songs(): List<Song> {
        return songRepository.songs()
    }
}