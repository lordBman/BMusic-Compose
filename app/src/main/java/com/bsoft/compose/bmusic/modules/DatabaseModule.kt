package com.bsoft.compose.bmusic.modules

import android.content.Context
import androidx.room.Room
import com.bsoft.compose.bmusic.data.UserDataDatabase
import com.bsoft.compose.bmusic.data.doas.FavouritesDao
import com.bsoft.compose.bmusic.data.doas.PlayerCounterDao
import com.bsoft.compose.bmusic.data.doas.PlaylistDao
import com.bsoft.compose.bmusic.data.repositories.PlaylistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun userDataDBProvider(@ApplicationContext context: Context): UserDataDatabase {
        return Room.databaseBuilder(context = context, UserDataDatabase::class.java, "user-database")
            //.enableMultiInstanceInvalidation()
            .build()
    }

    @Singleton
    @Provides
    fun favouritesDaoProvider(userDataDatabase: UserDataDatabase): FavouritesDao = userDataDatabase.favouriteDao()

    @Singleton
    @Provides
    fun playlistProvider(userDataDatabase: UserDataDatabase): PlaylistDao = userDataDatabase.playlistDao()

    @Singleton
    @Provides
    fun playerCounterDaoProvider(userDataDatabase: UserDataDatabase): PlayerCounterDao = userDataDatabase.playerCounterDao()
}