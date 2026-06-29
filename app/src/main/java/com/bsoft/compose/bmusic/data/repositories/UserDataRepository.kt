package com.bsoft.compose.bmusic.data.repositories

import android.content.Context
import androidx.room.Room
import com.bsoft.compose.bmusic.data.UserDataDatabase
import com.bsoft.compose.bmusic.data.entities.Favourite
import com.bsoft.compose.bmusic.data.entities.PlayCounter
import kotlin.getValue

class UserDataRepository(private val context: Context) {
    val userDataDB: UserDataDatabase by lazy {
        return@lazy Room.databaseBuilder(context = context, UserDataDatabase::class.java, "user-database")
            //.enableMultiInstanceInvalidation()
            .build()
    }

    val favourites by lazy { return@lazy fetchFavourites() }
    val playCounter by lazy { return@lazy fetchPlayCounter() }

    private fun fetchFavourites(): List<Favourite>{
        val userDao = userDataDB.favouriteDao()

        return userDao.getAll()
    }

    private fun fetchPlayCounter(): List<PlayCounter>{
        val userDao = userDataDB.playerCounterDao()

        return userDao.getAll()
    }


}