package com.bsoft.compose.bmusic.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bsoft.compose.bmusic.data.models.Song

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "title") val name: String,
    @ColumnInfo(name = "duration") val duration: Long? = 0L,
    @ColumnInfo(name = "count") val count: Int = 0,
    @ColumnInfo(name = "checked") val checked: Boolean = false
){
    companion object{
        fun fromSong(playlist: Long, song: Song): PlaylistSongEntity{
            return PlaylistSongEntity(
                id = song.id, displayName = song.displayName, title = song.title, album = song.album,
                artist = song.artist, duration =  song.duration, playlist = playlist
            )
        }
    }
}