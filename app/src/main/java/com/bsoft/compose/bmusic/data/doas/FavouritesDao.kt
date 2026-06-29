package com.bsoft.compose.bmusic.data.doas

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bsoft.compose.bmusic.data.entities.Favourite

@Dao
interface FavouritesDao {
    @Query("SELECT * FROM favourites ORDER BY title ASC")
    fun getAll(): List<Favourite>

    @Query("SELECT * FROM favourites WHERE song LIKE :song LIMIT 1")
    fun get(song: Long): Favourite

    @Insert
    fun insertAll(vararg favourite: Favourite)

    @Delete
    fun delete(favourite: Favourite)
}