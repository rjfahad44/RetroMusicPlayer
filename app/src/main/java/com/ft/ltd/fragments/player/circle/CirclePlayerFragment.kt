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
package com.ft.ltd.fragments.player.circle

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.Toolbar
import androidx.core.content.getSystemService
import com.ft.ltd.appthemehelper.util.ColorUtil
import com.ft.ltd.appthemehelper.util.MaterialValueHelper
import com.ft.ltd.appthemehelper.util.TintHelper
import com.ft.ltd.appthemehelper.util.ToolbarContentTintHelper
import com.ft.ltd.retromusic.R
import com.ft.ltd.retromusic.databinding.FragmentCirclePlayerBinding
import com.ft.ltd.retromusic.extensions.*
import com.ft.ltd.fragments.MusicSeekSkipTouchListener
import com.ft.ltd.fragments.base.AbsPlayerFragment
import com.ft.ltd.fragments.base.goToAlbum
import com.ft.ltd.fragments.base.goToArtist
import com.ft.ltd.glide.RetroGlideExtension
import com.ft.ltd.glide.RetroGlideExtension.simpleSongCoverOptions
import com.ft.ltd.glide.crossfadeListener
import com.ft.ltd.helper.MusicPlayerRemote
import com.ft.ltd.helper.MusicProgressViewUpdateHelper
import com.ft.ltd.helper.MusicProgressViewUpdateHelper.Callback
import com.ft.ltd.helper.PlayPauseButtonOnClickHandler
import com.ft.ltd.util.MusicUtil
import com.ft.ltd.util.PreferenceUtil
import com.ft.ltd.util.color.MediaNotificationProcessor
import com.ft.ltd.volume.AudioVolumeObserver
import com.ft.ltd.volume.OnAudioVolumeChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.ft.ltd.extensions.accentColor
import com.ft.ltd.extensions.colorControlNormal
import com.ft.ltd.extensions.getSongInfo
import com.ft.ltd.extensions.isColorLight
import com.google.android.material.slider.Slider
import me.tankery.lib.circularseekbar.CircularSeekBar

/**
 * Created by hemanths on 2020-01-06.
 */

class CirclePlayerFragment : AbsPlayerFragment(R.layout.fragment_circle_player), Callback,
    OnAudioVolumeChangedListener,
    CircularSeekBar.OnCircularSeekBarChangeListener {

    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper
    private var audioVolumeObserver: AudioVolumeObserver? = null

    private val audioManager: AudioManager
        get() = requireContext().getSystemService()!!

    private var _binding: FragmentCirclePlayerBinding? = null
    private val binding get() = _binding!!

    private var rotateAnimator: ObjectAnimator? = null
    private var lastRequest: RequestBuilder<Drawable>? = null

    var isSeeking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCirclePlayerBinding.bind(view)

        setupViews()
        binding.title.isSelected = true
        binding.title.setOnClickListener {
            goToAlbum(requireActivity())
        }
        binding.text.setOnClickListener {
            goToArtist(requireActivity())
        }
        binding.songInfo.drawAboveSystemBars()
    }

    private fun setUpPlayerToolbar() {
        binding.playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            setOnMenuItemClickListener(this@CirclePlayerFragment)
            ToolbarContentTintHelper.colorizeToolbar(
                this,
                colorControlNormal(),
                requireActivity()
            )
        }
    }

    private fun setupViews() {
        setUpProgressSlider()
        binding.volumeSeekBar.circleProgressColor = accentColor()
        binding.volumeSeekBar.circleColor = ColorUtil.withAlpha(accentColor(), 0.25f)
        setUpPlayPauseFab()
        setUpPrevNext()
        setUpPlayerToolbar()
        binding.albumCoverOverlay.background = ColorDrawable(
            MaterialValueHelper.getPrimaryTextColor(
                requireContext(),
                accentColor().isColorLight
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpPrevNext() {
        updatePrevNextColor()
        binding.nextButton.setOnTouchListener(MusicSeekSkipTouchListener(requireActivity(), true))
        binding.previousButton.setOnTouchListener(
            MusicSeekSkipTouchListener(
                requireActivity(),
                false
            )
        )
    }

    private fun updatePrevNextColor() {
        val accentColor = accentColor()
        binding.nextButton.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN)
        binding.previousButton.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN)
    }

    private fun setUpPlayPauseFab() {
        TintHelper.setTintAuto(
            binding.playPauseButton,
            accentColor(),
            false
        )
        binding.playPauseButton.setOnClickListener(PlayPauseButtonOnClickHandler())
    }

    private fun setupRotateAnimation() {
        rotateAnimator = ObjectAnimator.ofFloat(binding.albumCover, View.ROTATION, 360F).apply {
            interpolator = LinearInterpolator()
            repeatCount = Animation.INFINITE
            duration = 10000
            if (MusicPlayerRemote.isPlaying) {
                start()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
        if (audioVolumeObserver == null) {
            audioVolumeObserver = AudioVolumeObserver(requireActivity())
        }
        audioVolumeObserver?.register(AudioManager.STREAM_MUSIC, this)

        val audioManager = audioManager
        binding.volumeSeekBar.max =
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        binding.volumeSeekBar.progress =
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        binding.volumeSeekBar.setOnSeekBarChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        lastRequest = null
        progressViewUpdateHelper.stop()
    }

    override fun playerToolbar(): Toolbar {
        return binding.playerToolbar
    }

    override fun onShow() {
    }

    override fun onHide() {
    }

    override fun toolbarIconColor(): Int =
        colorControlNormal()

    override val paletteColor: Int
        get() = Color.BLACK

    override fun onColorChanged(color: MediaNotificationProcessor) {
    }

    override fun onFavoriteToggled() {
    }

    override fun onPlayStateChanged() {
        updatePlayPauseDrawableState()
        if (MusicPlayerRemote.isPlaying) {
            if (rotateAnimator?.isStarted == true) rotateAnimator?.resume() else rotateAnimator?.start()
        } else {
            rotateAnimator?.pause()
        }
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateSong()
        updatePlayPauseDrawableState()
        setupRotateAnimation()
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        binding.title.text = song.title
        binding.text.text = song.artistName

        if (PreferenceUtil.isSongInfo) {
            binding.songInfo.text = getSongInfo(song)
            binding.songInfo.show()
        } else {
            binding.songInfo.hide()
        }
        Glide.with(this)
            .load(RetroGlideExtension.getSongModel(MusicPlayerRemote.currentSong))
            .simpleSongCoverOptions(MusicPlayerRemote.currentSong)
            .thumbnail(lastRequest)
            .error(Glide.with(this).load(R.drawable.default_audio_art).fitCenter())
            .fitCenter().also {
                lastRequest = it.clone()
                it.crossfadeListener()
                    .into(binding.albumCover)
            }
    }

    private fun updatePlayPauseDrawableState() {
        when {
            MusicPlayerRemote.isPlaying -> binding.playPauseButton.setImageResource(R.drawable.ic_pause)
            else -> binding.playPauseButton.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    override fun onAudioVolumeChanged(currentVolume: Int, maxVolume: Int) {
        _binding?.volumeSeekBar?.max = maxVolume.toFloat()
        _binding?.volumeSeekBar?.progress = currentVolume.toFloat()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (audioVolumeObserver != null) {
            audioVolumeObserver!!.unregister()
        }
        _binding = null
    }


    override fun onProgressChanged(
        circularSeekBar: CircularSeekBar?,
        progress: Float,
        fromUser: Boolean,
    ) {
        val audioManager = audioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress.toInt(), 0)
    }

    override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
    }

    private fun setUpProgressSlider() {
        binding.progressSlider.applyColor(accentColor())
        val progressSlider = binding.progressSlider
        progressSlider.addOnChangeListener(Slider.OnChangeListener { _, value, fromUser ->
            if (fromUser) {
                onUpdateProgressViews(
                    value.toInt(),
                    MusicPlayerRemote.songDurationMillis
                )
            }
        })
        progressSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                isSeeking = true
                progressViewUpdateHelper.stop()
            }

            override fun onStopTrackingTouch(slider: Slider) {
                isSeeking = false
                MusicPlayerRemote.seekTo(slider.value.toInt())
                progressViewUpdateHelper.start()
            }
        })
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        val progressSlider = binding.progressSlider
        progressSlider.valueTo = total.toFloat()

        progressSlider.valueTo = total.toFloat()

        progressSlider.value =
            progress.toFloat().coerceIn(progressSlider.valueFrom, progressSlider.valueTo)

        binding.songTotalTime.text = MusicUtil.getReadableDurationString(total.toLong())
        binding.songCurrentProgress.text = MusicUtil.getReadableDurationString(progress.toLong())
    }
}
