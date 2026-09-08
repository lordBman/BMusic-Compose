package com.bsoft.compose.bmusic.data.doas

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bsoft.compose.bmusic.data.entities.FavouriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {
    @Query("SELECT * FROM favourites ORDER BY title ASC")
    fun getAll(): Flow<List<FavouriteEntity>>

    @Query("SELECT * FROM favourites WHERE song LIKE :song LIMIT 1")
    fun get(song: Long): FavouriteEntity?

    @Insert
    suspend fun insertAll(vararg favourite: FavouriteEntity)

    @Query(value = "DELETE FROM favourites WHERE song = :song")
    suspend fun delete(song: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE song = :song)")
    fun doesSongExist(song: Long): Boolean

    @Query("UPDATE favourites SET favourite = :favourite WHERE song = :song")
    suspend fun updateFavorites(song: Long, favourite: Boolean)
}