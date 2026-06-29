package com.bsoft.compose.bmusic.data.doas

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bsoft.compose.bmusic.data.entities.PlayCounter

@Dao
interface PlayerCounterDao {
    @Query("SELECT * FROM player_counter ORDER BY title ASC")
    fun getAll(): List<PlayCounter>

    @Query("SELECT * FROM player_counter WHERE song LIKE :song LIMIT 1")
    fun get(song: Long): PlayCounter

    @Insert
    fun insertAll(vararg playCounter: PlayCounter)

    @Delete
    fun delete(playCounter: PlayCounter)

    @Update
    fun update(playCounter: PlayCounter)
}