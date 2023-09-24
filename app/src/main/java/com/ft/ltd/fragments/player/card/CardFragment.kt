/*
 * Copyright (c) 2020 Hemanth Savarla.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package com.ft.ltd.fragments.player.card

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.ft.ltd.appthemehelper.util.ToolbarContentTintHelper
import com.ft.ltd.retromusic.R
import com.ft.ltd.retromusic.databinding.FragmentCardPlayerBinding
import com.ft.ltd.extensions.drawAboveSystemBars
import com.ft.ltd.extensions.whichFragment
import com.ft.ltd.fragments.base.AbsPlayerFragment
import com.ft.ltd.fragments.player.PlayerAlbumCoverFragment
import com.ft.ltd.helper.MusicPlayerRemote
import com.ft.ltd.model.Song
import com.ft.ltd.util.color.MediaNotificationProcessor

class CardFragment : AbsPlayerFragment(R.layout.fragment_card_player) {
    override fun playerToolbar(): Toolbar {
        return binding.playerToolbar
    }

    private var lastColor: Int = 0
    override val paletteColor: Int
        get() = lastColor

    private lateinit var playbackControlsFragment: CardPlaybackControlsFragment
    private var _binding: FragmentCardPlayerBinding? = null
    private val binding get() = _binding!!


    override fun onShow() {
        playbackControlsFragment.show()
    }

    override fun onHide() {
        playbackControlsFragment.hide()
    }

    override fun toolbarIconColor(): Int {
        return Color.WHITE
    }

    override fun onColorChanged(color: MediaNotificationProcessor) {
        playbackControlsFragment.setColor(color)
        lastColor = color.primaryTextColor
        libraryViewModel.updateColor(color.primaryTextColor)
        ToolbarContentTintHelper.colorizeToolbar(binding.playerToolbar, Color.WHITE, activity)
    }

    override fun toggleFavorite(song: Song) {
        super.toggleFavorite(song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
    }

    override fun onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.currentSong)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCardPlayerBinding.bind(view)
        setUpSubFragments()
        setUpPlayerToolbar()
        (binding.playbackControlsFragment.parent as View).drawAboveSystemBars()
    }

    private fun setUpSubFragments() {
        playbackControlsFragment = whichFragment(R.id.playbackControlsFragment)
        val playerAlbumCoverFragment: PlayerAlbumCoverFragment =
            whichFragment(R.id.playerAlbumCoverFragment)
        playerAlbumCoverFragment.setCallbacks(this)
        playerAlbumCoverFragment.removeSlideEffect()
    }

    private fun setUpPlayerToolbar() {
        binding.playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            setOnMenuItemClickListener(this@CardFragment)

            ToolbarContentTintHelper.colorizeToolbar(this, Color.WHITE, activity)
        }
    }

    override fun onServiceConnected() {
        updateIsFavorite()
    }

    override fun onPlayingMetaChanged() {
        updateIsFavorite()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
