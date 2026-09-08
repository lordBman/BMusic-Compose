package com.bsoft.compose.bmusic.data.doas

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bsoft.compose.bmusic.data.entities.PlaylistEntity
import com.bsoft.compose.bmusic.data.entities.PlaylistSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert
    suspend fun createPlaylists(vararg playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlists")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :id LIMIT 1")
    suspend fun getPlaylist(id: Long): PlaylistEntity

    @Query("DELETE FROM playlists WHERE id = :playlist")
    suspend fun deletePlaylist(playlist: Long)

    @Update
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity)

    @Query("UPDATE playlists SET count = :songsCount, duration = :songDuration WHERE id = :playlist")
    suspend fun updateSongDetails(playlist: Long, songsCount: Int, songDuration: Long)

    @Insert
    suspend fun insertPlaylistSong(vararg playlistSongEntity: PlaylistSongEntity)

    @Query("Select * from playlist_songs WHERE playlist = :playlist")
    suspend fun getPlaylistSongs(playlist: Long): List<PlaylistSongEntity>

    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlist = :playlist")
    suspend fun getPlaylistSongCount(playlist: Long): Int

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_songs WHERE id = :id AND playlist = :playlist)")
    fun doesSongExistInPlaylist(id: Long, playlist: Long): Boolean

    @Query("SELECT SUM(duration) FROM playlist_songs WHERE playlist = :playlist")
    suspend fun getTotalSongsDurationForPlaylist(playlist: Long): Long

    @Delete
    suspend fun deleteSongFromPlaylist(songEntity: PlaylistSongEntity)

    @Query("DELETE FROM playlist_songs WHERE playlist = :playlist")
    suspend fun deleteAllSongFromPlaylist(playlist: Long)
}