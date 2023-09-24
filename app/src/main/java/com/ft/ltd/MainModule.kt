package com.ft.ltd

import androidx.room.Room
import com.ft.ltd.auto.AutoMusicProvider
import com.ft.ltd.retromusic.cast.RetroWebServer
import com.ft.ltd.db.MIGRATION_23_24
import com.ft.ltd.db.RetroDatabase
import com.ft.ltd.fragments.LibraryViewModel
import com.ft.ltd.fragments.albums.AlbumDetailsViewModel
import com.ft.ltd.fragments.artists.ArtistDetailsViewModel
import com.ft.ltd.fragments.genres.GenreDetailsViewModel
import com.ft.ltd.fragments.playlists.PlaylistDetailsViewModel
import com.ft.ltd.model.Genre
import com.ft.ltd.network.provideDefaultCache
import com.ft.ltd.network.provideLastFmRest
import com.ft.ltd.network.provideLastFmRetrofit
import com.ft.ltd.network.provideOkHttp
import com.ft.ltd.repository.AlbumRepository
import com.ft.ltd.repository.ArtistRepository
import com.ft.ltd.repository.GenreRepository
import com.ft.ltd.repository.LastAddedRepository
import com.ft.ltd.repository.LocalDataRepository
import com.ft.ltd.repository.PlaylistRepository
import com.ft.ltd.repository.RealAlbumRepository
import com.ft.ltd.repository.RealArtistRepository
import com.ft.ltd.repository.RealGenreRepository
import com.ft.ltd.repository.RealLastAddedRepository
import com.ft.ltd.repository.RealLocalDataRepository
import com.ft.ltd.repository.RealPlaylistRepository
import com.ft.ltd.repository.RealRepository
import com.ft.ltd.repository.RealRoomRepository
import com.ft.ltd.repository.RealSearchRepository
import com.ft.ltd.repository.RealSongRepository
import com.ft.ltd.repository.RealTopPlayedRepository
import com.ft.ltd.repository.Repository
import com.ft.ltd.repository.RoomRepository
import com.ft.ltd.repository.SongRepository
import com.ft.ltd.repository.TopPlayedRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {

    factory {
        provideDefaultCache()
    }
    factory {
        provideOkHttp(get(), get())
    }
    single {
        provideLastFmRetrofit(get())
    }
    single {
        provideLastFmRest(get())
    }
}

private val roomModule = module {

    single {
        Room.databaseBuilder(androidContext(), RetroDatabase::class.java, "playlist.db")
            .addMigrations(MIGRATION_23_24)
            .build()
    }

    factory {
        get<RetroDatabase>().playlistDao()
    }

    factory {
        get<RetroDatabase>().playCountDao()
    }

    factory {
        get<RetroDatabase>().historyDao()
    }

    single {
        RealRoomRepository(get(), get(), get())
    } bind RoomRepository::class
}
private val autoModule = module {
    single {
        AutoMusicProvider(
            androidContext(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}
private val mainModule = module {
    single {
        androidContext().contentResolver
    }
    single {
        RetroWebServer(get())
    }
}
private val dataModule = module {
    single {
        RealRepository(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    } bind Repository::class

    single {
        RealSongRepository(get())
    } bind SongRepository::class

    single {
        RealGenreRepository(get(), get())
    } bind GenreRepository::class

    single {
        RealAlbumRepository(get())
    } bind AlbumRepository::class

    single {
        RealArtistRepository(get(), get())
    } bind ArtistRepository::class

    single {
        RealPlaylistRepository(get())
    } bind PlaylistRepository::class

    single {
        RealTopPlayedRepository(get(), get(), get(), get())
    } bind TopPlayedRepository::class

    single {
        RealLastAddedRepository(
            get(),
            get(),
            get()
        )
    } bind LastAddedRepository::class

    single {
        RealSearchRepository(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single {
        RealLocalDataRepository(get())
    } bind LocalDataRepository::class
}

private val viewModules = module {

    viewModel {
        LibraryViewModel(get())
    }

    viewModel { (albumId: Long) ->
        AlbumDetailsViewModel(
            get(),
            albumId
        )
    }

    viewModel { (artistId: Long?, artistName: String?) ->
        ArtistDetailsViewModel(
            get(),
            artistId,
            artistName
        )
    }

    viewModel { (playlistId: Long) ->
        PlaylistDetailsViewModel(
            get(),
            playlistId
        )
    }

    viewModel { (genre: Genre) ->
        GenreDetailsViewModel(
            get(),
            genre
        )
    }
}

val appModules = listOf(mainModule, dataModule, autoModule, viewModules, networkModule, roomModule)