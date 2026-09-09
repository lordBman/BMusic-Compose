package com.bsoft.compose.bmusic.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bsoft.compose.bmusic.data.doas.FavouritesDao
import com.bsoft.compose.bmusic.data.doas.PlayerCounterDao
import com.bsoft.compose.bmusic.data.doas.PlaylistDao
import com.bsoft.compose.bmusic.data.entities.FavouriteEntity
import com.bsoft.compose.bmusic.data.entities.PlayCounterEntity
import com.bsoft.compose.bmusic.data.entities.PlaylistEntity
import com.bsoft.compose.bmusic.data.entities.PlaylistSongEntity

@Database(entities = [FavouriteEntity::class, PlayCounterEntity::class, PlaylistEntity::class, PlaylistSongEntity::class], version = 1, exportSchema = false)
abstract class UserDataDatabase: RoomDatabase() {
    abstract fun favouriteDao(): FavouritesDao
    abstract fun playerCounterDao(): PlayerCounterDao
    abstract fun playlistDao(): PlaylistDao
}