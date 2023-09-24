package com.ft.ltd.interfaces

import android.view.View
import com.ft.ltd.db.PlaylistWithSongs

interface IPlaylistClickListener {
    fun onPlaylistClick(playlistWithSongs: PlaylistWithSongs, view: View)
}