package com.bsoft.compose.bmusic.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class Favourite(
    @PrimaryKey @ColumnInfo(name = "song") val song: Long,
    @ColumnInfo(name = "title") val title: String
)