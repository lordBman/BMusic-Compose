package com.bsoft.compose.bmusic.modules

import com.bsoft.compose.bmusic.data.QueueManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QueueModule {
    @Provides
    @Singleton
    fun provideQueueManager()= QueueManager()
}