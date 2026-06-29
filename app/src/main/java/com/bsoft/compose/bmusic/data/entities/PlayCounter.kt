package com.bsoft.compose.bmusic.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_counter")
data class PlayCounter(
    @PrimaryKey @ColumnInfo(name = "song") val song: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "played") val played: Int,
)
