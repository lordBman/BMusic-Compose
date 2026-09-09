package com.bsoft.compose.bmusic.data.repositories

import com.bsoft.compose.bmusic.data.doas.PlaylistDao
import com.bsoft.compose.bmusic.data.entities.PlaylistEntity
import com.bsoft.compose.bmusic.data.entities.PlaylistSongEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(private val playlistDao: PlaylistDao) {
    suspend fun createPlayList(playlistEntity: PlaylistEntity){
        playlistDao.createPlaylists(playlistEntity)
    }

    fun getPlayLists(): Flow<List<PlaylistEntity>> {
        return playlistDao.getPlaylists()
    }

    suspend fun deletePlayList(playlist: Long){
        playlistDao.deletePlaylist(playlist = playlist)
        playlistDao.deleteAllSongFromPlaylist(playlist = playlist)
    }

    suspend fun insertSongsIntoPlayList(playlistSongEntities: List<PlaylistSongEntity>) {
        playlistSongEntities.forEach {
            if(playlistDao.doesSongExistInPlaylist(id = it.id, playlist = it.playlist).not()){
                playlistDao.insertPlaylistSong(it)
            }
        }

        if(playlistSongEntities.isNotEmpty()){
            val playlist = playlistSongEntities.first().playlist
            val songsDuration = playlistDao.getTotalSongsDurationForPlaylist(playlist)
            val songsCount = playlistDao.getPlaylistSongCount(playlist)
            playlistDao.updateSongDetails(playlist, songsCount, songsDuration)
        }
    }

    suspend fun getPlayList(playlist: Long): PlaylistEntity{
        return playlistDao.getPlaylist(playlist)
    }

    suspend fun getSongsFromPlayList(playlist: Long): List<PlaylistSongEntity> {
        return playlistDao.getPlaylistSongs(playlist = playlist)
    }

    suspend fun deleteFromPlaylist(playlistSongEntity: PlaylistSongEntity){
        val playlist = playlistSongEntity.playlist

        playlistDao.deleteSongFromPlaylist(playlistSongEntity)
        val songsDuration = playlistDao.getTotalSongsDurationForPlaylist(playlist)
        val songsCount = playlistDao.getPlaylistSongCount(playlist)
        playlistDao.updateSongDetails(playlist, songsCount, songsDuration)
    }
}