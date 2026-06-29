package com.bsoft.compose.bmusic.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bsoft.compose.bmusic.data.doas.FavouritesDao
import com.bsoft.compose.bmusic.data.doas.PlayerCounterDao
import com.bsoft.compose.bmusic.data.entities.Favourite
import com.bsoft.compose.bmusic.data.entities.PlayCounter

@Database(entities = [Favourite::class, PlayCounter::class], version = 1, exportSchema = false)
abstract class UserDataDatabase: RoomDatabase() {
    abstract fun favouriteDao(): FavouritesDao
    abstract fun playerCounterDao(): PlayerCounterDao
}