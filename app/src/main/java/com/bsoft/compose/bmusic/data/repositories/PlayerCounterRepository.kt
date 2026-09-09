package com.bsoft.compose.bmusic.data.repositories

import com.bsoft.compose.bmusic.data.doas.PlayerCounterDao
import com.bsoft.compose.bmusic.data.entities.PlayCounterEntity
import com.bsoft.compose.bmusic.data.entities.PlaylistEntity
import com.bsoft.compose.bmusic.data.models.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerCounterRepository @Inject constructor(private val playerCounterDao: PlayerCounterDao){
    suspend fun incrementCount(song: Song){
        val result = playerCounterDao.get(song.id)
        if(result == null){
            playerCounterDao.insertAll(PlayCounterEntity(song = song.id, title = song.title, played = 1))
        }else{
            playerCounterDao.update(result.copy(played = result.played + 1))
        }
    }

    fun getMostPlayed(): Flow<List<PlayCounterEntity>> {
        return playerCounterDao.getMostPlayed()
    }

    fun getLastPlayed(): Flow<List<PlayCounterEntity>> {
        return playerCounterDao.getLastPlayed()
    }

    suspend fun deletePlaylist(song: Long){
        playerCounterDao.get(song)?.let {
            playerCounterDao.delete(playCounter = it)
        }
    }
}