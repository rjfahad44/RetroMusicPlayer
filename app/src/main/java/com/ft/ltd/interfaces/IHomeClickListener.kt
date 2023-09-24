package com.ft.ltd.interfaces

import com.ft.ltd.model.Album
import com.ft.ltd.model.Artist
import com.ft.ltd.model.Genre

interface IHomeClickListener {
    fun onAlbumClick(album: Album)

    fun onArtistClick(artist: Artist)

    fun onGenreClick(genre: Genre)
}