package com.bsoft.compose.bmusic.modules

import android.content.Context
import com.bsoft.compose.bmusic.data.EqualizerManager
import com.bsoft.compose.bmusic.data.QueueManager
import com.bsoft.compose.bmusic.data.preferences.EqualizerPreferences
import com.bsoft.compose.bmusic.data.repositories.SongRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSongRepository(@ApplicationContext context: Context): SongRepository {
        return SongRepository(context)
    }

    @Provides
    @Singleton
    fun provideQueueManager()= QueueManager()

    @Provides
    @Singleton
    fun provideEqualizerManager()= EqualizerManager()

    @Provides
    @Singleton
    fun provideEqualizerPreferences(@ApplicationContext context: Context): EqualizerPreferences{
        return EqualizerPreferences(context)
    }
}