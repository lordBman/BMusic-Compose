package com.bsoft.compose.bmusic.data.repositories

import com.bsoft.compose.bmusic.data.doas.FavouritesDao
import com.bsoft.compose.bmusic.data.entities.FavouriteEntity
import com.bsoft.compose.bmusic.data.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouriteRepository @Inject constructor(private val favouritesDao: FavouritesDao) {
    suspend fun toggleFavourite(song: Song) = withContext(Dispatchers.IO) {
        if(isFavorite(song.id)){
            unFavourite(song = song.id)
        }else{
            favouritesDao.insertAll(FavouriteEntity.fromSong(song = song))
        }
    }

    fun getAllFavorites(): Flow<List<FavouriteEntity>> {
        return favouritesDao.getAll()
    }

    fun isFavorite(song: Long): Boolean {
        return favouritesDao.doesSongExist(song = song)
    }

    suspend fun unFavourite(song: Long) = withContext(Dispatchers.IO) {
        favouritesDao.delete(song = song)
    }

    suspend fun updateFavourite(song: Long, isFavorite: Boolean) = withContext(Dispatchers.IO){
        favouritesDao.updateFavorites(song = song, isFavorite)
    }
}