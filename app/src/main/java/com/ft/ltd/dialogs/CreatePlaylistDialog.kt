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
package com.ft.ltd.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.ft.ltd.EXTRA_SONG
import com.ft.ltd.retromusic.R
import com.ft.ltd.retromusic.databinding.DialogPlaylistBinding
import com.ft.ltd.extensions.colorButtons
import com.ft.ltd.extensions.extra
import com.ft.ltd.extensions.materialDialog
import com.ft.ltd.fragments.LibraryViewModel
import com.ft.ltd.model.Song
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class CreatePlaylistDialog : DialogFragment() {
    private var _binding: DialogPlaylistBinding? = null
    private val binding get() = _binding!!
    private val libraryViewModel by activityViewModel<LibraryViewModel>()

    companion object {
        fun create(song: Song): CreatePlaylistDialog {
            val list = mutableListOf<Song>()
            list.add(song)
            return create(list)
        }

        fun create(songs: List<Song>): CreatePlaylistDialog {
            return CreatePlaylistDialog().apply {
                arguments = bundleOf(EXTRA_SONG to songs)
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogPlaylistBinding.inflate(layoutInflater)

        val songs: List<Song> = extra<List<Song>>(EXTRA_SONG).value ?: emptyList()
        val playlistView: TextInputEditText = binding.actionNewPlaylist
        val playlistContainer: TextInputLayout = binding.actionNewPlaylistContainer
        return materialDialog(R.string.new_playlist_title)
            .setView(binding.root)
            .setPositiveButton(
                R.string.create_action
            ) { _, _ ->
                val playlistName = playlistView.text.toString()
                if (!TextUtils.isEmpty(playlistName)) {
                    libraryViewModel.addToPlaylist(requireContext(), playlistName, songs)
                } else {
                    playlistContainer.error = "Playlist name can't be empty"
                }
            }
            .setNegativeButton(R.string.action_cancel, null)
            .create()
            .colorButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
