package com.bsoft.compose.bmusic.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bsoft.compose.bmusic.data.models.Song

@Entity(tableName = "favourites")
data class FavouriteEntity(
    @PrimaryKey @ColumnInfo(name = "song") val song: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "favourite" , defaultValue = "1") val favourite: Boolean = true
){
    companion object{
        fun fromSong(song: Song): FavouriteEntity{
            return FavouriteEntity(song = song.id, title = song.title)
        }
    }
}