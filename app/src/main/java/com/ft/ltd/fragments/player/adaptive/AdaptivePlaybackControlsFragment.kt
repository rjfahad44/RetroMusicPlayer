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
package com.ft.ltd.fragments.player.adaptive

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.ft.ltd.appthemehelper.util.ATHUtil
import com.ft.ltd.appthemehelper.util.ColorUtil
import com.ft.ltd.appthemehelper.util.MaterialValueHelper
import com.ft.ltd.appthemehelper.util.TintHelper
import com.ft.ltd.extensions.accentColor
import com.ft.ltd.extensions.getSongInfo
import com.ft.ltd.extensions.ripAlpha
import com.ft.ltd.retromusic.R
import com.ft.ltd.retromusic.databinding.FragmentAdaptivePlayerPlaybackControlsBinding
import com.ft.ltd.retromusic.extensions.*
import com.ft.ltd.fragments.base.AbsPlayerControlsFragment
import com.ft.ltd.helper.MusicPlayerRemote
import com.ft.ltd.util.PreferenceUtil
import com.ft.ltd.util.color.MediaNotificationProcessor
import com.google.android.material.slider.Slider

class AdaptivePlaybackControlsFragment :
    AbsPlayerControlsFragment(R.layout.fragment_adaptive_player_playback_controls) {

    private var _binding: FragmentAdaptivePlayerPlaybackControlsBinding? = null
    private val binding get() = _binding!!

    override val progressSlider: Slider
        get() = binding.progressSlider

    override val shuffleButton: ImageButton
        get() = binding.shuffleButton

    override val repeatButton: ImageButton
        get() = binding.repeatButton

    override val nextButton: ImageButton
        get() = binding.nextButton

    override val previousButton: ImageButton
        get() = binding.previousButton

    override val songTotalTime: TextView
        get() = binding.songTotalTime

    override val songCurrentProgress: TextView
        get() = binding.songCurrentProgress

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdaptivePlayerPlaybackControlsBinding.bind(view)

        setUpPlayPauseFab()
    }

    private fun updateSong() {
        if (PreferenceUtil.isSongInfo) {
            binding.songInfo.text = getSongInfo(MusicPlayerRemote.currentSong)
            binding.songInfo.show()
        } else {
            binding.songInfo.hide()
        }
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
    }

    override fun onServiceConnected() {
        updatePlayPauseDrawableState()
        updateRepeatState()
        updateShuffleState()
        updateSong()
    }

    override fun onPlayStateChanged() {
        updatePlayPauseDrawableState()
    }

    override fun onRepeatModeChanged() {
        updateRepeatState()
    }

    override fun onShuffleModeChanged() {
        updateShuffleState()
    }

    override fun setColor(color: MediaNotificationProcessor) {
        if (ColorUtil.isColorLight(
                ATHUtil.resolveColor(
                    requireContext(),
                    android.R.attr.windowBackground
                )
            )
        ) {
            lastPlaybackControlsColor = MaterialValueHelper.getSecondaryTextColor(activity, true)
            lastDisabledPlaybackControlsColor =
                MaterialValueHelper.getSecondaryDisabledTextColor(activity, true)
        } else {
            lastPlaybackControlsColor = MaterialValueHelper.getPrimaryTextColor(activity, false)
            lastDisabledPlaybackControlsColor =
                MaterialValueHelper.getPrimaryDisabledTextColor(activity, false)
        }

        updateRepeatState()
        updateShuffleState()
        updatePrevNextColor()
        updatePlayPauseColor()

        val colorFinal = if (PreferenceUtil.isAdaptiveColor) {
            color.primaryTextColor
        } else {
            accentColor()
        }.ripAlpha()

        TintHelper.setTintAuto(
            binding.playPauseButton,
            MaterialValueHelper.getPrimaryTextColor(context, ColorUtil.isColorLight(colorFinal)),
            false
        )
        TintHelper.setTintAuto(binding.playPauseButton, colorFinal, true)
        binding.progressSlider.applyColor(colorFinal)
        volumeFragment?.setTintable(colorFinal)
    }

    private fun updatePlayPauseColor() {
        // playPauseButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN);
    }

    private fun setUpPlayPauseFab() {
        binding.playPauseButton.setOnClickListener {
            if (MusicPlayerRemote.isPlaying) {
                MusicPlayerRemote.pauseSong()
            } else {
                MusicPlayerRemote.resumePlaying()
            }
            it.showBounceAnimation()
        }
    }

    private fun updatePlayPauseDrawableState() {
        if (MusicPlayerRemote.isPlaying) {
            binding.playPauseButton.setImageResource(R.drawable.ic_pause)
        } else {
            binding.playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_32dp)
        }
    }

    override fun show() {}

    override fun hide() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
