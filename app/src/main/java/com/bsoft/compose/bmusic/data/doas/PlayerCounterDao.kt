package com.bsoft.compose.bmusic.data.doas

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bsoft.compose.bmusic.data.entities.PlayCounterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerCounterDao {
    @Query("SELECT * FROM player_counter ORDER BY played DESC")
    fun getMostPlayed(): Flow<List<PlayCounterEntity>>

    @Query("SELECT * FROM player_counter ORDER BY last_played DESC LIMIT 20")
    fun getLastPlayed(): Flow<List<PlayCounterEntity>>

    @Query("SELECT * FROM player_counter ORDER BY title ASC")
    fun getAll(): Flow<List<PlayCounterEntity>>

    @Query("SELECT * FROM player_counter WHERE song LIKE :song LIMIT 1")
    suspend fun get(song: Long): PlayCounterEntity?

    @Insert
    suspend fun insertAll(vararg playCounter: PlayCounterEntity)

    @Delete
    suspend fun delete(playCounter: PlayCounterEntity)

    @Update
    suspend fun update(playCounter: PlayCounterEntity)
}