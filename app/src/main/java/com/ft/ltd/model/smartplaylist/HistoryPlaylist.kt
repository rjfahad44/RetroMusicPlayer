package com.ft.ltd.model.smartplaylist

import com.ft.ltd.App
import com.ft.ltd.retromusic.R
import com.ft.ltd.model.Song
import kotlinx.parcelize.Parcelize
import org.koin.core.component.KoinComponent

@Parcelize
class HistoryPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.history),
    iconRes = R.drawable.ic_history
), KoinComponent {

    override fun songs(): List<Song> {
        return topPlayedRepository.recentlyPlayedTracks()
    }
}